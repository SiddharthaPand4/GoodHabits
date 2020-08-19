package io.synlabs.synvision.service;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.synlabs.synvision.config.FileStorageProperties;
import io.synlabs.synvision.entity.anpr.AnprEvent;
import io.synlabs.synvision.entity.atcc.AtccEvent;
import io.synlabs.synvision.entity.atcc.AtccSummaryData;
import io.synlabs.synvision.entity.atcc.QAtccEvent;
import io.synlabs.synvision.entity.core.Feed;
import io.synlabs.synvision.enums.TimeSpan;
import io.synlabs.synvision.ex.FileStorageException;
import io.synlabs.synvision.ex.NotFoundException;
import io.synlabs.synvision.jpa.*;
import io.synlabs.synvision.views.atcc.*;
import io.synlabs.synvision.views.common.DummyRequest;
import io.synlabs.synvision.views.common.PageResponse;
import io.synlabs.synvision.views.common.ResponseWrapper;
import io.synlabs.synvision.views.common.SearchRequest;
import org.joda.time.DateTime;
import org.simpleflatmapper.csv.CsvWriter;
import org.simpleflatmapper.util.CheckedConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
public class AtccDataService extends BaseService {

    private static final Logger logger = LoggerFactory.getLogger(AtccDataService.class);

    private final AtccEventRepository atccEventRepository;

    private final Path fileStorageLocation;

    private final ImportStatusRepository statusRepository;

    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

    @Autowired
    private FeedRepository feedRepository;

    @Autowired
    private AnprEventRepository anprEventRepository;

    @Autowired
    private HighwayIncidentRepository incidentRepository;

    @Autowired
    private EntityManager entityManager;

    @Qualifier("dataSource")
    @Autowired
    private DataSource dataSource;

    public AtccDataService(AtccEventRepository atccEventRepository,
                           ImportStatusRepository statusRepository,
                           FileStorageProperties fileStorageProperties) {

        this.atccEventRepository = atccEventRepository;

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
        Page<AtccEvent> page = atccEventRepository.findAll(PageRequest.of(searchRequest.getPage(), searchRequest.getPageSize(), Sort.by(DESC, "eventDate")));

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
        AtccSummaryData atccSummaryData;
        List<AtccSummaryData> data = new ArrayList<>();
        Connection connection;

        switch (interval) {
            case "day":

                try {
                    String query = "SELECT COUNT(1) AS COUNT, type,`date`, 1 AS span, MIN(`date`) AS `from`, MAX(`date`) AS `to` FROM atcc_raw_data GROUP BY type, date ORDER BY `date` DESC, `time` DESC LIMIT ?, ? ;";

                    connection = dataSource.getConnection();
                    PreparedStatement ps = connection.prepareStatement(query);
                    ps.setInt(1, searchRequest.getPageSize() * searchRequest.getPage());
                    ps.setInt(2, searchRequest.getPageSize());
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        atccSummaryData = getAtccSummaryData(rs);
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
                    String query = "SELECT COUNT(1) AS COUNT, type,`date`, 1 AS span, MIN(`date`) AS `from`, MAX(`date`) AS `to` FROM atcc_raw_data GROUP BY type, MONTH(`date`) ORDER BY `date` DESC, `time` DESC LIMIT ?, ? ;";

                    connection = dataSource.getConnection();
                    PreparedStatement ps = connection.prepareStatement(query);
                    ps.setInt(1, searchRequest.getPageSize() * searchRequest.getPage());
                    ps.setInt(2, searchRequest.getPageSize());
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        atccSummaryData = getAtccSummaryData(rs);
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
                try {
                    String query = "SELECT COUNT(1) AS COUNT, type,`date`, 1 AS span, SEC_TO_TIME(hour(time)*60*60) AS `from`, SEC_TO_TIME((hour(time) + 1)*60*60-1) AS `to` FROM atcc_raw_data GROUP BY type, `date`, hour(time) ORDER BY `date` DESC, `time` DESC LIMIT ?, ? ;";

                    connection = dataSource.getConnection();
                    PreparedStatement ps = connection.prepareStatement(query);
                    ps.setInt(1, searchRequest.getPageSize() * searchRequest.getPage());
                    ps.setInt(2, searchRequest.getPageSize());
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        atccSummaryData = getAtccSummaryData(rs);
                        atccSummaryData.setSpan(TimeSpan.Hour);
                        data.add(atccSummaryData);
                    }

                    query = "SELECT COUNT(*) AS count FROM (SELECT type FROM atcc_raw_data GROUP BY type, `date`, hour(time)) AS atcc_summary_data";
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
        }

        List<AtccSummaryDataResponse> collect = data.stream().map(AtccSummaryDataResponse::new).collect(Collectors.toList());
        wrapper.setTotalElements(totalRecords);
        wrapper.setCurrPage(searchRequest.getPage());
        wrapper.setData(collect);

        return wrapper;
    }

    private AtccSummaryData getAtccSummaryData(ResultSet rs) throws SQLException {
        AtccSummaryData atccSummaryData = new AtccSummaryData();
        atccSummaryData.setCount(rs.getInt("count"));
        atccSummaryData.setType(rs.getString("type"));
        atccSummaryData.setDate(rs.getDate("date"));
        atccSummaryData.setFrom(rs.getDate("from"));
        atccSummaryData.setTo(rs.getDate("to"));
        return atccSummaryData;
    }

    private int getDirection(String direction) {
        switch (direction) {
            case "fwd":
                return 1;
            case "stop":
                return 0;
            case "rev":
                return -1;
        }
        return 1;
    }

    public Resource makeSummaryData(String interval) throws IOException {

        String filename = "summary-" + interval + "-" + UUID.randomUUID().toString() + ".csv";
        Path filePath = this.fileStorageLocation.resolve(filename).normalize();

        SearchRequest request = new SearchRequest();
        request.setPage(0);
        request.setPageSize(1000);
        ResponseWrapper<AtccSummaryDataResponse> wrapper = listSummaryData(request, interval);
        List<AtccSummaryDataResponse> data = wrapper.getData();

        CsvWriter.CsvWriterDSL<AtccSummaryDataResponse> writerDsl =
                CsvWriter
                        .from(AtccSummaryDataResponse.class)
                        .columns("type", "date", "from", "to", "span", "count");
        try (FileWriter fileWriter = new FileWriter(filePath.toFile())) {
            CsvWriter<AtccSummaryDataResponse> writer = writerDsl.to(fileWriter);
            data.forEach(CheckedConsumer.toConsumer(writer::append));
        }

        Resource resource = new UrlResource(filePath.toUri());

        if (resource.exists()) {
            return resource;
        } else {
            throw new NotFoundException("File not found " + filename);
        }
    }

    public File listRawData() throws IOException {

        String filename = "rawdata-" + UUID.randomUUID().toString() + ".csv";
        Path filePath = this.fileStorageLocation.resolve(filename).normalize();


        //List<AtccRawData> data =  rawDataRepository.findAll(Sort.by(DESC, "timeStamp"));
        int currPage = 0;
        Page<AtccEvent> page = atccEventRepository.findAll(PageRequest.of(currPage, 10000, Sort.by(DESC, "timeStamp")));
        int totalPages = page.getTotalPages();

        logger.info("Page Size - 10000, Current Page - ", currPage);
        logger.info("Total Pages - ", totalPages);
        File file = filePath.toFile();
        try (FileWriter fileWriter = new FileWriter(file)) {

            CsvWriter.CsvWriterDSL<AtccEvent> writerDsl =
                    CsvWriter
                            .from(AtccEvent.class)
                            .columns("date", "time", "timestamp", "lane", "speed", "direction", "type", "feed", "vid");

            CsvWriter<AtccEvent> writer = writerDsl.to(fileWriter);
            page.get().forEach(CheckedConsumer.toConsumer(writer::append));

            for (currPage = 1; currPage < totalPages; currPage++) {
                logger.info("Current Page - {}", currPage);
                page = atccEventRepository.findAll(PageRequest.of(currPage, 10000, Sort.by(DESC, "timeStamp")));
                page.get().forEach(CheckedConsumer.toConsumer(writer::append));
            }

        }

        /*Resource resource = new UrlResource(filePath.toUri());*/

        if (file.exists()) {
            return file;
        } else {
            throw new NotFoundException("File not found " + filename);
        }
    }


    @Transactional(readOnly = true)
    public File streamRawData() throws IOException {

        String filename = "rawdata-" + UUID.randomUUID().toString() + ".csv";
        Path filePath = this.fileStorageLocation.resolve(filename).normalize();

        File file = filePath.toFile();
        try (FileWriter fileWriter = new FileWriter(file)) {

            CsvWriter.CsvWriterDSL<AtccEvent> writerDsl =
                    CsvWriter
                            .from(AtccEvent.class)
                            .columns("date", "time", "timestamp", "lane", "speed", "direction", "type", "feed", "vid");

            CsvWriter<AtccEvent> writer = writerDsl.to(fileWriter);

            try (Stream<AtccEvent> atccRawDataStream = atccEventRepository.getAll()) {
                atccRawDataStream.forEach(CheckedConsumer.toConsumer(writer::append));
            }
        }

        /*Resource resource = new UrlResource(filePath.toUri());*/

        if (file.exists()) {
            return file;
        } else {
            throw new NotFoundException("File not found " + filename);
        }
    }


    public Resource loadFileAsResource(String id) {

        try {
            Optional<AtccEvent> data = atccEventRepository.findById(Long.parseLong(id));
            if (data.isPresent()) {
                String fileName = data.get().getEventVideo();
                String eventDate =formatter.format(data.get().getEventDate());
                Path filePath = this.fileStorageLocation.resolve("atcc-video").resolve(eventDate).resolve(fileName).normalize();
                Resource resource = new UrlResource(filePath.toUri());
                if (resource.exists()) {
                    return resource;
                } else {
                    throw new NotFoundException("File not found " + fileName);
                }
            } else {
                throw new NotFoundException("event not found ");
            }
        } catch (NumberFormatException ex) {
            throw new NotFoundException("number format for " + id, ex);
        } catch (MalformedURLException ex) {
            throw new NotFoundException("error:", ex);
        }
    }

    public Resource getScreenshot(Long id) throws IOException {

        if (id == null) throw new NotFoundException("Not a valid id");

        Optional<AtccEvent> data = atccEventRepository.findById(id);

        if (data.isPresent()) {
            String fileName = data.get().getEventImage();
            String eventDate =formatter.format(data.get().getEventDate());
            Path filePath = this.fileStorageLocation.resolve("atcc-image").resolve(eventDate).resolve(fileName).normalize();

            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new NotFoundException("File not found " + fileName);
            }

        } else {
            throw new NotFoundException("Not a valid id");
        }

    }

    public Resource downloadVehicleImage(Long id) {
        return downloadByTag("vehicle", id);
    }

    public Resource downloadLprImage(Long id) {
        return downloadByTag("anpr", id);
    }

    public Resource downloadByTag(String tag, Long mid) {
        long id = new DummyRequest().unmask(mid);
        try {
            Optional<AnprEvent> eventop = anprEventRepository.findById(id);
            if (eventop.isPresent()) {
                String filename = eventop.get().getVehicleImage() + ".jpg";
                String eventDate=formatter.format(eventop.get().getEventDate());
                Path filePath = Paths.get(this.fileStorageLocation.toString(),tag,eventDate,filename).toAbsolutePath().normalize();

                Resource resource = new UrlResource(filePath.toUri());
                if (resource.exists()) {
                    return resource;
                } else {
                    throw new NotFoundException("File not found " + filename);
                }
            } else {
                throw new NotFoundException("event not found ");
            }

        } catch (MalformedURLException ex) {
            throw new NotFoundException("unknown error", ex);
        }
    }

    public void addEvent(CreateAtccEventRequest request) {
        AtccEvent atccEvent = request.toEntity();
        Feed feed = feedRepository.findOneByName(request.getSource());
        atccEvent.setFeed(feed);
        atccEventRepository.save(atccEvent);
    }

    private Feed getFeed(String tag) {
        return feedRepository.findOneByName(tag);
    }

    public PageResponse<AtccRawDataResponse> list(AtccEventFilterRequest request) {
        JPAQuery<AtccEvent> query = getQuery(request);

        int count = (int) query.fetchCount();
        int pageCount = (int) Math.ceil(count * 1.0 / request.getPageSize());

        int offset = (request.getPage() - 1) * request.getPageSize();
        query.offset(offset);
        if (request.getPageSize() > 0) {
            query.limit(request.getPageSize());
        }

        List<AtccEvent> data = query.fetch();

        List<AtccRawDataResponse> list = new ArrayList<>(request.getPageSize());
        data.forEach(item -> {
            list.add(new AtccRawDataResponse(item));
        });
        return (PageResponse<AtccRawDataResponse>) new AtccPageResponse(request.getPageSize(), pageCount, request.getPage(), list);
    }

    private JPAQuery<AtccEvent> getQuery(AtccEventFilterRequest request) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fromDate = request.getFromDate();
        String toDate = request.getToDate();
        QAtccEvent atccEvent = new QAtccEvent("atccEvent");
        JPAQuery<AtccEvent> query = new JPAQuery<>(entityManager);
        query = query.select(atccEvent).from(atccEvent).orderBy(atccEvent.eventDate.desc());
        try {
            if (request.getFromDate() != null) {
                String fromTime = request.getFromTime() == null ? "00:00:00" : request.getFromTime();
                String starting = fromDate + " " + fromTime;
                Date startingDate = dateFormat.parse(starting);
                query = query.where(atccEvent.eventDate.after(startingDate));

            }

            if (request.getToDate() != null) {
                String toTime = request.getToTime() == null ? "00:00:00" : request.getToTime();
                String ending = toDate + " " + toTime;
                Date endingDate = dateFormat.parse(ending);
                query = query.where(atccEvent.eventDate.before(endingDate));

            }

            if (request.getFeedId() != null && request.getFeedId() != 0) query.where(atccEvent.feed.id.eq(request.getFeedId()));

        } catch (Exception e) {
            logger.error("Error in parsing date", e);
        }
        return query;
    }
    @Transactional
    public void deleteData (int days)
    {
        Date date=new DateTime().minusDays(days).toDate() ;
        QAtccEvent atccEvent=new QAtccEvent("atccEvent");
        JPAQueryFactory query=new JPAQueryFactory(entityManager);
        query.delete(atccEvent).where(atccEvent.eventDate.before(date)).execute();
    }


}

