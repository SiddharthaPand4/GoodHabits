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
import java.util.ArrayList;
import java.util.List;

@Component
public class DataDeleteScheduler {

    @Value("${synvision.data.ttl.days}")
    private int data_del_after_days;
    @Value("${synvision.data.ttl.include.database.records}")
    private boolean del_records_fromDB;
    @Value("${synvision.data.ttl.include.folders}")
    private ArrayList<String> folders;
    @Autowired
    private AnprService anprService;
    @Autowired
    private AtccDataService atccDataService;
    @Autowired
    private VidsService vidsService;
    @Autowired
    private FileStorageProperties fileStorageProperties;

    private static final Logger logger = LoggerFactory.getLogger(DataDeleteScheduler.class);

    public DataDeleteScheduler(FileStorageProperties fileStorageProperties) {
        this.fileStorageProperties = fileStorageProperties;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void DeleteFilesAfterNDays() {
        Path fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();
        final File directory = new File(fileStorageLocation.toString());
        if (directory.exists()) {
            File[] listFiles = directory.listFiles();
            for (File listFile : listFiles) {
                if (listFile.isDirectory() && folders.contains(listFile.getName())) {
                    File[] subFiles = listFile.listFiles();
                    for (File subFile : subFiles) {
                        LocalDate lastModified = null;
                        try {
                            lastModified = Files.getLastModifiedTime(Paths.get(subFile.getPath())).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        } catch (java.io.IOException e) {
                            e.printStackTrace();
                        }
                        if (lastModified.plusDays(data_del_after_days).isBefore(LocalDate.now())) {
                            subFile.delete();
                            logger.info(subFile + " deleted on " + LocalDate.now());
                        }
                    }
                }
            }
           if (del_records_fromDB) {
               anprService.deleteData(data_del_after_days);
               atccDataService.deleteData(data_del_after_days);
               vidsService.deleteData(data_del_after_days);
           }
        }
    }
}
