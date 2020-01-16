package io.synlabs.synvision.service;

import io.synlabs.synvision.config.FileStorageProperties;
import io.synlabs.synvision.entity.ImportStatus;
import io.synlabs.synvision.entity.apc.ApcEvent;
import io.synlabs.synvision.ex.FileStorageException;
import io.synlabs.synvision.jpa.ApcEventRepository;
import io.synlabs.synvision.jpa.ImportStatusRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.util.*;

@Service
public class ApcFileService {

    private static final Logger logger = LoggerFactory.getLogger(ApcFileService.class);

    private Path fileStorageLocation;

    @Autowired
    private FileStorageProperties fileStorageProperties;

    @Autowired
    private ImportStatusRepository statusRepository;

    @Autowired
    private ApcEventRepository apcEventRepository;


    @PostConstruct
    public void init(){
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public String importFile(MultipartFile file, String tag) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        long cnt = statusRepository.countByFilenameAndFeedAndStatus(fileName, tag, "OK");

        if (cnt > 0) {
            logger.warn("File {}  - {} is already imported, not importing again", tag, fileName);
            return fileName;
        }

        ImportStatus status = new ImportStatus();
        status.setFilename(fileName);
        status.setImportDate(new Date());
        status.setFeed(tag);
        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            List<ApcEvent> datalist = importData(targetLocation, tag);

            apcEventRepository.saveAll(datalist);

            addStatusSpan(datalist, status);
            status.setStatus("OK");

            return fileName;
        } catch (IOException ex) {
            status.setStatus("FAILED");
            status.setError(ex.getMessage());
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        } finally {
            statusRepository.save(status);
        }

    }

    private List<ApcEvent> importData(Path fileName, String tag) {
        try {
            List<ApcEvent> items = new LinkedList<>();
            getCSVRecords(fileName, items, tag);
            return items;
        } catch (Exception e) {
            logger.error("Error occurred while loading object list from file " + fileName, e);
            return Collections.emptyList();
        }
    }

    private void addStatusSpan(List<ApcEvent> datalist, ImportStatus status) {
        if (datalist == null || datalist.isEmpty()) return;
        ApcEvent first = datalist.get(0);
        ApcEvent last = datalist.get(datalist.size() - 1);
        status.setFrom(first.getEventDate());
        status.setTo(last.getEventDate());
        status.setDataDate(first.getEventDate());
    }


    private void getCSVRecords(Path fileName, List<ApcEvent> items, String tag) throws IOException, ParseException {
        Reader reader = Files.newBufferedReader(fileName);
        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                .withFirstRecordAsHeader()
                .withSkipHeaderRecord()
                .withTrim());

        for (CSVRecord csvRecord : csvParser) {
            // Accessing Values by Column Index
            String eventId = csvRecord.get(0);
            String timestamp = csvRecord.get(1);
            String direction = csvRecord.get(2);

            long ts = (long) Double.parseDouble(timestamp);

            ApcEvent apcEvent = new ApcEvent();
            apcEvent.setEventId(eventId);
            apcEvent.setEventDate(new Date(ts * 1000));
            apcEvent.setDirection(direction);
            apcEvent.setSource(tag);
            apcEvent.setArchived(false);

            items.add(apcEvent);
        }
    }
}
