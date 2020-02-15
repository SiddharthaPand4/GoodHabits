package io.synlabs.synvision.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import io.synlabs.synvision.config.FileStorageProperties;
import io.synlabs.synvision.entity.core.Feed;
import io.synlabs.synvision.entity.vids.HighwayIncident;
import io.synlabs.synvision.entity.vids.HighwayTrafficState;
import io.synlabs.synvision.entity.vids.QHighwayIncident;
import io.synlabs.synvision.ex.NotFoundException;
import io.synlabs.synvision.jpa.FeedRepository;
import io.synlabs.synvision.jpa.HighwayIncidentRepository;
import io.synlabs.synvision.jpa.HighwayTrafficStateRepository;
import io.synlabs.synvision.views.common.PageResponse;
import io.synlabs.synvision.views.vids.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
public class VidsService {

    private static final Logger logger = LoggerFactory.getLogger(VidsService.class);

    @Autowired
    private HighwayIncidentRepository incidentRepository;

    @Autowired
    private FeedRepository feedRepository;

    @Autowired
    private HighwayTrafficStateRepository stateRepository;

    @Autowired
    private FileStorageProperties fileStorageProperties;

    public PageResponse<VidsResponse> listIncidents(VidsFilterRequest request) {
        BooleanExpression query = getQuery(request);
        int count = (int) incidentRepository.count(query);
        int pageCount = (int) Math.ceil(count * 1.0 / request.getPageSize());
        Pageable paging = PageRequest.of(request.getPage() - 1, request.getPageSize(), Sort.by(DESC, "incidentDate"));

        Page<HighwayIncident> page = incidentRepository.findAll(query, paging);
        //List<AnprResponse> list = page.get().map(AnprResponse::new).collect(Collectors.toList());

        List<VidsResponse> list = new ArrayList<>(page.getSize());
        page.get().forEach(item -> {
            list.add(new VidsResponse(item));
        });

        return new VidsPageResponse(request.getPageSize(), pageCount, request.getPage(), list);
    }

    public BooleanExpression getQuery(VidsFilterRequest request) {

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            String fromDate = request.getFromDate();
            String toDate = request.getToDate();
            QHighwayIncident root = QHighwayIncident.highwayIncident;
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
        Feed feed = feedRepository.findOneByName(request.getSource());
        incident.setFeed(feed);
        incidentRepository.save(incident);
    }


    public void updateFlow(TrafficFlowUpdateRequest request) {
        HighwayTrafficState state = request.toEntity();
        Feed feed = feedRepository.findOneByName(request.getSource());
        state.setFeed(feed);
        stateRepository.save(state);
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

                Path filePath = Paths.get(fileStorageLocation.toString(), tag, filename).toAbsolutePath().normalize();
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

                Path filePath = Paths.get(fileStorageLocation.toString(), tag, filename).toAbsolutePath().normalize();
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

                Path filePath = Paths.get(fileStorageLocation.toString(), tag, filename).toAbsolutePath().normalize();
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


}
