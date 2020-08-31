package io.synlabs.synvision.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.synlabs.synvision.config.FileStorageProperties;
import io.synlabs.synvision.entity.core.Feed;
import io.synlabs.synvision.entity.vids.HighwayIncident;
import io.synlabs.synvision.entity.vids.HighwayTrafficState;
import io.synlabs.synvision.entity.vids.QHighwayIncident;
import io.synlabs.synvision.entity.vids.VidsAlertSetting;
import io.synlabs.synvision.enums.HighwayIncidentType;
import io.synlabs.synvision.ex.NotFoundException;
import io.synlabs.synvision.jpa.FeedRepository;
import io.synlabs.synvision.jpa.HighwayIncidentRepository;
import io.synlabs.synvision.jpa.HighwayTrafficStateRepository;
import io.synlabs.synvision.jpa.VidsAlertSettingRepository;
import io.synlabs.synvision.views.common.PageResponse;
import io.synlabs.synvision.views.frs.AlertMessage;
import io.synlabs.synvision.views.vids.*;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
public class VidsService {

    private static final Logger logger = LoggerFactory.getLogger(VidsService.class);

    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
    @Autowired
    private HighwayIncidentRepository incidentRepository;

    @Autowired
    private FeedRepository feedRepository;

    @Autowired
    private VidsAlertSettingRepository vidsAlertSettingRepository;

    @Autowired
    private HighwayTrafficStateRepository stateRepository;

    @Autowired
    private FileStorageProperties fileStorageProperties;
    @Autowired
    private EntityManager entityManager;

    @Autowired
    private SimpMessagingTemplate websocket;

    private Set<HighwayIncident> highwayIncidents;

    @PostConstruct
    private void initializeIncidentList() {
        highwayIncidents = new HashSet<>();
    }

    @Scheduled(fixedRate = 1000 * 5)
    protected void generateAlerts() {
        //find alert for which file exists
        HashSet<HighwayIncident> validAlert = highwayIncidents.stream().
                filter(incident -> incidentImageExists(incident) && incidentVideoExists(incident)).collect(Collectors.toCollection(HashSet::new));

        HashSet<HighwayIncidentType> incidentTypes =
                vidsAlertSettingRepository.findAllByEnabledTrue().stream().
                        map(VidsAlertSetting::getIncidentType).collect(Collectors.toCollection(HashSet::new));

        validAlert.stream().filter(incident -> incidentTypes.contains(incident.getIncidentType())).forEach(this::generateAlert);  //generate alert for every valid alert

        highwayIncidents.removeAll(validAlert);  //remove the alerts which were valid and are generated
    }

    public PageResponse<VidsResponse> listIncidents(VidsFilterRequest request) {
        BooleanExpression query = getQuery(request);
        int count = (int) incidentRepository.count(query);

        int pageCount = (int) Math.ceil(count * 1.0 / request.getPageSize());
        Pageable paging = PageRequest.of(request.getPage() - 1, request.getPageSize(), Sort.by(DESC, "incidentDate"));

        Page<HighwayIncident> page = incidentRepository.findAll(query, paging);
        //List<AnprResponse> list = page.get().map(AnprResponse::new).collect(Collectors.toList());

        List<VidsResponse> list = new ArrayList<>(page.getSize());
        page.get().forEach(item -> list.add(new VidsResponse(item)));

        return new VidsPageResponse(request.getPageSize(), pageCount, request.getPage(), list);
    }

    public BooleanExpression getQuery(VidsFilterRequest request) {

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            String fromDate = request.getFromDate();
            String toDate = request.getToDate();
            QHighwayIncident root = new QHighwayIncident("highwayIncident");
            BooleanExpression query = root.archived.isFalse();

            //TODO add time also
            if (request.getFromDate() != null) {
                String fromTime = request.getFromTime() == null ? "00:00:00" : request.getFromTime();
                String starting = fromDate + " " + fromTime;
                Date startingDate = dateFormat.parse(starting);
                query = query.and(root.incidentDate.after(startingDate));
            }

            if (request.getToDate() != null) {
                String toTime = request.getToTime() == null ? "00:00:00" : request.getToTime();
                String ending = toDate + " " + toTime;
                Date endingDate = dateFormat.parse(ending);
                query = query.and(root.incidentDate.before(endingDate));
            }
            if (!request.getIncidentType().isEmpty()) {
                query = query.and(root.incidentType.eq(HighwayIncidentType.valueOf(request.getIncidentType())));
            }

            if (request.getFeedId() != null && request.getFeedId() != 0) {
                query = query.and(root.feed.id.eq(request.getFeedId()));
            }
            return query;

        } catch (Exception e) {
            logger.error("Error in parsing date", e);
        }
        return null;
    }


    public void archiveIncident(Long id) {
        HighwayIncident incident = incidentRepository.getOne(id);
        incident.setArchived(true);
        incidentRepository.saveAndFlush(incident);
    }

    public void addIncident(CreateIncidentRequest request) {
        HighwayIncident incident = request.toEntity();
        if (incident.getIncidentType().equals(HighwayIncidentType.NoVideo)) {
            incident.setIncidentImage("novideo.jpg");
            incident.setIncidentVideo("novideo.mp4");
        }
        Feed feed = feedRepository.findOneByName(request.getSource());
        incident.setFeed(feed);
        incident = incidentRepository.saveAndFlush(incident);
        highwayIncidents.add(incident);
    }


    public void updateFlow(TrafficFlowUpdateRequest request) {
        HighwayTrafficState state = request.toEntity();
        Feed feed = feedRepository.findOneByName(request.getSource());

        switch (state.getDensity()) {
            case Queue:
                createIncident(request);
                break;
            default:
        }
        state.setFeed(feed);
        stateRepository.save(state);
    }

    private void createIncident(TrafficFlowUpdateRequest request) {
        HighwayIncident incident = new HighwayIncident();
        incident.setEventId(UUID.randomUUID().toString());
        incident.setIncidentDate(new Date(request.getTimeStamp() * 1000));
        incident.setTimeStamp(request.getTimeStamp());
        incident.setIncidentType(HighwayIncidentType.Queue);
        incident.setIncidentImage(request.getFlowImage());
        incident.setIncidentVideo(request.getFlowVideo());
        Feed feed = feedRepository.findOneByName(request.getSource());
        incident.setFeed(feed);
        incident = incidentRepository.saveAndFlush(incident);
        highwayIncidents.add(incident);
    }

    private void generateAlert(HighwayIncident incident) {
        VidsAlertMessage message = new VidsAlertMessage(incident);
        websocket.convertAndSend("/alert", message);
    }

    public boolean incidentVideoExists(HighwayIncident incident) {

        String tag = "vids-video";
        Path fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        String filename;

        try {
            filename = incident.getIncidentVideo();
            String incidentDate=formatter.format(incident.getIncidentDate());
            if (filename == null) {
                throw new NotFoundException("Missing video file name");
            }
            Path filePath = Paths.get(fileStorageLocation.toString(), tag,incidentDate,filename).toAbsolutePath().normalize();
            Resource resource = new UrlResource(filePath.toUri());
            return resource.exists();
        } catch (MalformedURLException ex) {
            return false;
        }
    }

    public boolean incidentImageExists(HighwayIncident incident) {

        String tag = "vids-image";
        Path fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        String filename;

        try {
            filename = incident.getIncidentImage();
            String incidentDate=formatter.format(incident.getIncidentDate());
            Path filePath = Paths.get(fileStorageLocation.toString(), tag,incidentDate,filename).toAbsolutePath().normalize();
            Resource resource = new UrlResource(filePath.toUri());
            return resource.exists();
        } catch (MalformedURLException ex) {
            return false;
        }
    }

    public Resource downloadIncidentImage(Long id) {

        String tag = "vids-image";
        Path fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        String filename = null;

        try {
            Optional<HighwayIncident> incident = incidentRepository.findById(id);
            if (incident.isPresent()) {
                filename = incident.get().getIncidentImage();
                String incidentDate=formatter.format(incident.get().getIncidentDate());
                Path filePath = Paths.get(fileStorageLocation.toString(), tag,incidentDate,filename).toAbsolutePath().normalize();
                Resource resource = new UrlResource(filePath.toUri());
                if (resource.exists()) {
                    return resource;
                } else {
                    throw new NotFoundException("File not found " + filename);
                }
            } else {
                throw new NotFoundException("File not found " + filename);
            }

        } catch (MalformedURLException ex) {
            throw new NotFoundException("File not found " + filename, ex);
        }
    }

    public Resource downloadIncidentVideo(Long id) {

        String tag = "vids-video";
        Path fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        String filename = null;

        try {
            Optional<HighwayIncident> incident = incidentRepository.findById(id);
            if (incident.isPresent()) {
                filename = incident.get().getIncidentVideo();
                String incidentDate=formatter.format(incident.get().getIncidentDate());
                if (filename == null) {
                    throw new NotFoundException("Missing video file name");
                }
                Path filePath = Paths.get(fileStorageLocation.toString(), tag,incidentDate,filename).toAbsolutePath().normalize();
                Resource resource = new UrlResource(filePath.toUri());
                if (resource.exists()) {
                    return resource;
                } else {
                    throw new NotFoundException("File not found " + filename);
                }
            } else {
                throw new NotFoundException("File not found " + filename);
            }

        } catch (MalformedURLException ex) {
            throw new NotFoundException("File not found " + filename, ex);
        }
    }

    public Resource downloadFlowImage(Long id) {
        String tag = "flow-image";
        Path fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        String filename = null;

        try {
            Optional<HighwayTrafficState> state = stateRepository.findById(id);
            if (state.isPresent()) {
                filename = state.get().getFlowImage();
                String stateDate=formatter.format(state.get().getUpdateDate());
                Path filePath = Paths.get(fileStorageLocation.toString(), tag,stateDate, filename).toAbsolutePath().normalize();
                Resource resource = new UrlResource(filePath.toUri());
                if (resource.exists()) {
                    return resource;
                } else {
                    throw new NotFoundException("File not found " + filename);
                }
            } else {
                throw new NotFoundException("File not found " + filename);
            }

        } catch (MalformedURLException ex) {
            throw new NotFoundException("File not found " + filename, ex);
        }
    }
    @Transactional
    public void deleteData (int days)
    {
        Date date=new DateTime().minusDays(days).toDate() ;
        QHighwayIncident highwayIncident=new QHighwayIncident("highwayIncident");
        JPAQueryFactory query=new JPAQueryFactory(entityManager);
        query.delete(highwayIncident).where(highwayIncident.incidentDate.before(date)).execute();
    }
}
