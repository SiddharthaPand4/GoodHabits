package io.synlabs.synvision.service;

import io.synlabs.synvision.config.FileStorageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneId;
@Component
public class DataDeleteScheduler {

    @Value("${synvision.auth.data_del_after_days}")
    private int data_del_after_days;

    @Value("${synvision.auth.del_records_fromDB}")
    private boolean del_records_fromDB;

    @Autowired
    private AnprService anprService;
    @Autowired
    private AtccDataService atccDataService;
    @Autowired
    private VidsService vidsService;
    @Autowired
    private FileStorageProperties fileStorageProperties;

    private static final Logger logger = LoggerFactory.getLogger(DataDeleteScheduler.class);

    public DataDeleteScheduler(FileStorageProperties fileStorageProperties){
        this.fileStorageProperties=fileStorageProperties;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void DeleteFilesAfterNDays()
    {

        Path fileStorageLocation= Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();
        final File directory = new File(fileStorageLocation.toString());
        if(directory.exists())
        {
            File[] listFiles = directory.listFiles();
            for(File listFile:listFiles)
            {
                if(listFile.isDirectory())
                {
                    File[] subFiles=listFile.listFiles();
                    for(File subFile :subFiles)
                    {
                        LocalDate lastModified=null;
                        try {
                            lastModified= Files.getLastModifiedTime(Paths.get(subFile.getPath())).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        } catch (java.io.IOException e) {
                            e.printStackTrace();
                        }
                        if(lastModified.plusDays(data_del_after_days).isBefore(LocalDate.now()))
                        { subFile.delete();
                        logger.info("Data deleted on " + LocalDate.now());
                        }
                    }
                }
            }
            if(del_records_fromDB)
            {
                anprService.deleteData(data_del_after_days);
                atccDataService.deleteData(data_del_after_days);
                vidsService.deleteData(data_del_after_days);
            }
        }
    }
}
