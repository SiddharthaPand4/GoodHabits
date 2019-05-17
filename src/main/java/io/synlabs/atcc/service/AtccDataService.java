package io.synlabs.atcc.service;

import io.synlabs.atcc.config.FileStorageProperties;
import io.synlabs.atcc.entity.AtccRawData;
import io.synlabs.atcc.entity.AtccSummaryData;
import io.synlabs.atcc.entity.AtccVideoData;
import io.synlabs.atcc.entity.ImportStatus;
import io.synlabs.atcc.enums.TimeSpan;
import io.synlabs.atcc.ex.FileStorageException;
import io.synlabs.atcc.jpa.AtccRawDataRepository;
import io.synlabs.atcc.jpa.AtccSummaryDataRepository;
import io.synlabs.atcc.jpa.AtccVideoDataRepository;
import io.synlabs.atcc.jpa.ImportStatusRepository;
import org.joda.time.DateTime;
import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;
import org.simpleflatmapper.csv.CsvMapper;
import org.simpleflatmapper.csv.CsvMapperFactory;
import org.simpleflatmapper.csv.CsvParser;
import org.simpleflatmapper.map.property.ConverterProperty;
import org.simpleflatmapper.map.property.DateFormatProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.synlabs.atcc.views.AtccRawDataResponse;
import io.synlabs.atcc.views.AtccSummaryDataResponse;
import io.synlabs.atcc.views.ResponseWrapper;
import io.synlabs.atcc.views.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Long.parseLong;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
public class AtccDataService extends BaseService {

    private static final Logger logger = LoggerFactory.getLogger(AtccDataService.class);

    private final AtccRawDataRepository rawDataRepository;

    private final AtccSummaryDataRepository summaryDataRepository;

    private final Path fileStorageLocation;

    private final ImportStatusRepository statusRepository;

    private AtccVideoDataRepository videoDataRepository;



    @Qualifier("dataSource")
    @Autowired
    private DataSource dataSource;

    public AtccDataService(AtccRawDataRepository rawDataRepository,
                           AtccSummaryDataRepository summaryDataRepository,
                           ImportStatusRepository statusRepository,
                           FileStorageProperties fileStorageProperties,
                           AtccVideoDataRepository videoDataRepository) {

        this.rawDataRepository = rawDataRepository;
        this.summaryDataRepository = summaryDataRepository;
        this.videoDataRepository = videoDataRepository;

        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
        this.statusRepository = statusRepository;
    }

    public ResponseWrapper<AtccRawDataResponse> listRawData(SearchRequest searchRequest) {
        Page<AtccRawData> page = rawDataRepository.findAll(PageRequest.of(searchRequest.getPage(), searchRequest.getPageSize(), Sort.by(isDescending(searchRequest.getSorted()) ? DESC : Sort.Direction.ASC, getDefaultSortId(searchRequest.getSorted(), "id"))));
        List<AtccRawDataResponse> collect = page.get().map(AtccRawDataResponse::new).collect(Collectors.toList());
        ResponseWrapper<AtccRawDataResponse> wrapper = new ResponseWrapper<>();
        wrapper.setData(collect);
        wrapper.setCurrPage(searchRequest.getPage());
        wrapper.setTotalElements(page.getTotalElements());
        return wrapper;
    }

    public ResponseWrapper<AtccSummaryDataResponse> listSummaryData(SearchRequest searchRequest, String interval) {

        ResponseWrapper<AtccSummaryDataResponse> wrapper = new ResponseWrapper<>();

        long totalRecords = 0;
        AtccSummaryData atccSummaryData = null;
        List<AtccSummaryData> data = new ArrayList<>();
        Connection connection = null;

        switch (interval) {
            case "day":

                try {
                    String query = "SELECT COUNT(1) AS COUNT, type,`date`, 1 AS span, MIN(`date`) AS `from`, MAX(`date`) AS `to` FROM atcc_raw_data GROUP BY type, date ORDER BY `" + getDefaultSortId(searchRequest.getSorted(), "id") + "` " + (isDescending(searchRequest.getSorted()) ? "DESC" : "ASC") + " LIMIT ?, ? ;";

                    connection = dataSource.getConnection();
                    PreparedStatement ps = connection.prepareStatement(query);
                    ps.setInt(1, searchRequest.getPageSize() * searchRequest.getPage());
                    ps.setInt(2, searchRequest.getPageSize());
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        atccSummaryData = new AtccSummaryData();
                        atccSummaryData.setCount(rs.getInt("count"));
                        atccSummaryData.setType(rs.getString("type"));
                        atccSummaryData.setDate(rs.getDate("date"));
                        atccSummaryData.setFrom(rs.getDate("from"));
                        atccSummaryData.setTo(rs.getDate("to"));
                        atccSummaryData.setSpan(TimeSpan.Day);
                        data.add(atccSummaryData);
                    }

                    query = "SELECT COUNT(*) AS count FROM (SELECT TYPE FROM atcc_raw_data GROUP BY TYPE, `date`) AS atcc_summary_data";
                    ps = connection.prepareStatement(query);
                    rs = ps.executeQuery();
                    while (rs.next()) {
                        totalRecords = rs.getLong("count");
                    }
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }


                break;
            case "month":

                try {
                    String query = "SELECT COUNT(1) AS COUNT, type,`date`, 1 AS span, MIN(`date`) AS `from`, MAX(`date`) AS `to` FROM atcc_raw_data GROUP BY type, MONTH(`date`) ORDER BY `" + getDefaultSortId(searchRequest.getSorted(), "id") + "` " + (isDescending(searchRequest.getSorted()) ? "DESC" : "ASC") + " LIMIT ?, ? ;";

                    connection = dataSource.getConnection();
                    PreparedStatement ps = connection.prepareStatement(query);
                    ps.setInt(1, searchRequest.getPageSize() * searchRequest.getPage());
                    ps.setInt(2, searchRequest.getPageSize());
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        atccSummaryData = new AtccSummaryData();
                        atccSummaryData.setCount(rs.getInt("count"));
                        atccSummaryData.setType(rs.getString("type"));
                        atccSummaryData.setDate(rs.getDate("date"));
                        atccSummaryData.setFrom(rs.getDate("from"));
                        atccSummaryData.setTo(rs.getDate("to"));
                        atccSummaryData.setSpan(TimeSpan.Month);
                        data.add(atccSummaryData);
                    }

                    query = "SELECT COUNT(*) AS count FROM (SELECT TYPE FROM atcc_raw_data GROUP BY TYPE, MONTH(`date`)) AS atcc_summary_data";
                    ps = connection.prepareStatement(query);
                    rs = ps.executeQuery();
                    while (rs.next()) {
                        totalRecords = rs.getLong("count");
                    }
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;

            case "hour":
            default:
                Page<AtccSummaryData> page = summaryDataRepository.findAll(PageRequest.of(searchRequest.getPage(), searchRequest.getPageSize(), Sort.by(isDescending(searchRequest.getSorted()) ? DESC : Sort.Direction.ASC, getDefaultSortId(searchRequest.getSorted(), "id"))));
                data = page.get().collect(Collectors.toList());
                totalRecords = page.getTotalElements();
                break;
        }

        List<AtccSummaryDataResponse> collect = data.stream().map(AtccSummaryDataResponse::new).collect(Collectors.toList());
        wrapper.setTotalElements(totalRecords);
        wrapper.setCurrPage(searchRequest.getPage());
        wrapper.setData(collect);

        return wrapper;
    }

    public String importVideo(MultipartFile file) {
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        ImportStatus status = new ImportStatus();
        status.setFilename(fileName);
        status.setImportDate(new Date());

        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            videoDataRepository.save(populateFields(fileName));
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

    private AtccVideoData populateFields(String fileName) {
        long ts = Long.parseLong(fileName.split("_")[0]);
        DateTime videoDate = new DateTime(ts * 1000L);
        AtccVideoData videoData = new AtccVideoData();
        videoData.setDate(videoDate.toDate());
        videoData.setTime(videoDate.toDate());
        videoData.setTimeStamp(ts);
        return videoData;
    }

    private void addStatusSpan(List<AtccRawData> datalist, ImportStatus status) {
        if (datalist == null || datalist.isEmpty()) return;
        AtccRawData first = datalist.get(0);
        AtccRawData last = datalist.get(datalist.size() - 1);
        status.setFrom(first.getTime());
        status.setTo(last.getTime());
        status.setDataDate(first.getDate());
    }

    private List<AtccRawData> importData(Path fileName) {
        try {

            List<AtccRawData> raws = new LinkedList<>();

            CsvParser
                    .mapWith(getCsvFactory())
                    .forEach(fileName.toFile(), raws::add);

            return raws;
        } catch (Exception e) {
            logger.error("Error occurred while loading object list from file " + fileName, e);
            return Collections.emptyList();
        }

    }

    private CsvMapper<AtccRawData> getCsvFactory() {
        return CsvMapperFactory
                .newInstance()
                .addColumnProperty("Time", new DateFormatProperty("HH:mm:ss"))
                .addColumnProperty("Date", new DateFormatProperty("dd/MM/yyyy"))
                .addAlias("Class", "type")
                .addColumnProperty("Class", ConverterProperty.of(new ContextualConverter<String, String>() {
                    @Override
                    public String convert(String s, Context context) throws Exception {
                        switch (s) {
                            case "0":
                                return "2-Wheeler";
                            case "1":
                                return "4-Wheeler";
                            case "2":
                                return "Bus/Truck";
                            case "3":
                                return "OSW";
                            default:
                                return "NA";
                        }
                    }
                }))
                .newMapper(AtccRawData.class);
    }

    public String importFile(MultipartFile file) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        ImportStatus status = new ImportStatus();
        status.setFilename(fileName);
        status.setImportDate(new Date());

        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            List<AtccRawData> datalist = importData(targetLocation);

            rawDataRepository.saveAll(datalist);

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
}
