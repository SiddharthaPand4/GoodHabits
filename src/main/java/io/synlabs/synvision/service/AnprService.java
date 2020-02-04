package io.synlabs.synvision.service;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import io.synlabs.synvision.entity.anpr.AnprEvent;
import io.synlabs.synvision.entity.anpr.HotListVehicle;
import io.synlabs.synvision.entity.anpr.QAnprEvent;
import io.synlabs.synvision.entity.anpr.QHotListVehicle;
import io.synlabs.synvision.entity.parking.ParkingEvent;
import io.synlabs.synvision.enums.VehicleType;
import io.synlabs.synvision.jpa.AnprEventRepository;
import io.synlabs.synvision.jpa.HotListVehicleRepository;
import io.synlabs.synvision.jpa.ParkingEventRepository;
import io.synlabs.synvision.views.anpr.*;
import io.synlabs.synvision.views.common.PageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.springframework.data.domain.Sort.Direction.DESC;

/**
 * Created by itrs on 10/21/2019.
 */
@Service
public class AnprService extends BaseService {

    @Autowired
    private AnprEventRepository anprEventRepository;

    @Autowired
    private HotListVehicleRepository hotListVehicleRepository;

    @Autowired
    private ParkingEventRepository parkingEventRepository;

    @Autowired
    private EntityManager entityManager;


    private static final Logger logger = LoggerFactory.getLogger(AnprService.class);

    public PageResponse<AnprResponse> list(AnprFilterRequest request) {
        BooleanExpression query = getQuery(request);
        int count = (int) anprEventRepository.count(query);
        int pageCount = (int) Math.ceil(count * 1.0 / request.getPageSize());
        Pageable paging = PageRequest.of(request.getPage() - 1, request.getPageSize(), Sort.by(DESC, "eventDate"));

        Page<AnprEvent> page = anprEventRepository.findAll(query, paging);
        //List<AnprResponse> list = page.get().map(AnprResponse::new).collect(Collectors.toList());

        List<AnprResponse> list = new ArrayList<>(page.getSize());
        page.get().forEach(item -> {
            list.add(new AnprResponse(item));
        });

        return (PageResponse<AnprResponse>) new AnprPageResponse(request.getPageSize(), pageCount, request.getPage(), list);
    }

    public BooleanExpression getQuery(AnprFilterRequest request) {

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            String fromDate = request.getFromDate();
            String toDate = request.getToDate();
            QAnprEvent root = QAnprEvent.anprEvent;
            BooleanExpression query = root.archived.isFalse();

            if (request.getLpr() != null) {
                query = query.and(root.anprText.likeIgnoreCase("%" + request.getLpr() + "%"));
            }

            if (request.getFromDate() != null) {
                String fromTime = request.getFromTime() == null ? "00:00:00" : request.getFromTime();
                String starting = fromDate + " " + fromTime;
                Date startingDate = dateFormat.parse(starting);
                query = query.and(root.eventDate.after(startingDate));
            }

            if (request.getToDate() != null) {
                String toTime = request.getToTime() == null ? "00:00:00" : request.getToTime();
                String ending = toDate + " " + toTime;
                Date endingDate = dateFormat.parse(ending);
                query = query.and(root.eventDate.before(endingDate));
            }
            return query;
        } catch (Exception e) {
            logger.error("Error in parsing date", e);
        }
        return null;
    }

    public void archiveAnprEvent(AnprRequest request) {
        AnprEvent anprEvent = anprEventRepository.getOne(request.getId());
        anprEvent.setArchived(true);
        anprEventRepository.saveAndFlush(anprEvent);
    }
    public void archiveAnprEvents(AnprRequest request) {

        List<AnprEvent> events = anprEventRepository.findAllByAnprTextAndArchived(request.anprText, false);
        int pageSize = 5;
        int currentPage = 0;

        QAnprEvent event = QAnprEvent.anprEvent;
        JPAQuery<AnprEvent> query = createAnprQuery(event);
        query.where(event.anprText.eq(request.anprText))
                .where(event.archived.isFalse());

        //pagination
        int count = (int) query.fetchCount();
        int pageCount = (int) Math.ceil(count * 1.0 / pageSize);

        for (currentPage = 0; currentPage < pageCount; currentPage++) {
            int offset = (currentPage) * pageSize;
            query.offset(offset);
            query.limit(pageSize);
            events = query.fetch();

        events.forEach(e->{
            e.setArchived(true);
        });
        anprEventRepository.saveAll(events);
    }

    }
    public void addAnprEvent(CreateAnprRequest request) {
        AnprEvent anprEvent = request.toEntity();
        anprEvent.setHotlisted(checkHotListed(anprEvent));
        anprEventRepository.save(anprEvent);

        //new parking event record
        ParkingEvent parkingEvent= parkingEventRepository.findByEventIdAndCheckInIsNull(anprEvent.getId().toString());
        if(parkingEvent==null){
            parkingEvent= new ParkingEvent();
        }
        parkingEvent.setCheckIn(anprEvent.getEventDate());
        parkingEvent.setEventId(anprEvent.getId().toString());
        parkingEvent.setVehicleNo(anprEvent.getAnprText());
        parkingEvent.setOrg(anprEvent.getOrg());

        if(anprEvent.getVehicleClass().equals("car")){
            parkingEvent.setType(VehicleType.Car);
        }
        else{
            parkingEvent.setType(VehicleType.Bike);
        }

        parkingEventRepository.save(parkingEvent);
    }

    public AnprResponse updateAnprEvent(AnprRequest request) {
        AnprEvent anprEvent = anprEventRepository.getOne(request.getId());
        anprEvent.setAnprText(request.getAnprText());
        anprEvent = anprEventRepository.saveAndFlush(anprEvent);
        return new AnprResponse(anprEvent);
    }

    private boolean checkHotListed(AnprEvent anprEvent) {
        HotListVehicle hottie = hotListVehicleRepository.findOneByLpr(anprEvent.getAnprText());
        return hottie != null;
    }

    public PageResponse<AnprResponse> listIncidents(AnprFilterRequest request) {
        BooleanExpression query = getIncidentQuery(request);
        int count = (int) anprEventRepository.count(query);
        int pageCount = (int) Math.ceil(count * 1.0 / request.getPageSize());
        Pageable paging = PageRequest.of(request.getPage() - 1, request.getPageSize(), Sort.by(DESC, "eventDate"));

        Page<AnprEvent> page = anprEventRepository.findAll(query, paging);
        //List<AnprResponse> list = page.get().map(AnprResponse::new).collect(Collectors.toList());

        List<AnprResponse> list = new ArrayList<>(page.getSize());
        page.get().forEach(item -> {
            list.add(new AnprResponse(item));
        });
        return (PageResponse<AnprResponse>) new AnprPageResponse(request.getPageSize(), pageCount, request.getPage(), list);
    }

    public PageResponse<AnprResponse> listHotListedIncidents(AnprFilterRequest request) {

        QAnprEvent event = QAnprEvent.anprEvent;
        QHotListVehicle hotListVehicle = QHotListVehicle.hotListVehicle;

        JPAQuery<AnprEvent> query = createAnprQuery(event);
        query = addFiltersInAnprQuery(request, event, query);

        // for hotListed vehicles
        query = query.innerJoin(hotListVehicle).on(event.anprText.eq(hotListVehicle.lpr));
        query = query.where(hotListVehicle.archived.isFalse());


        //pagination
        int count = (int) query.fetchCount();
        int pageCount = (int) Math.ceil(count * 1.0 / request.getPageSize());

        query.orderBy(event.eventDate.desc());

        int offset = (request.getPage() - 1) * request.getPageSize();
        query.offset(offset);
        if (request.getPageSize() > 0) {
            query.limit(request.getPageSize());
        }

        List<AnprEvent> data = query.fetch();

        List<AnprResponse> list = new ArrayList<>(request.getPageSize());
        data.forEach(item -> {
            list.add(new AnprResponse(item));
        });
        return (PageResponse<AnprResponse>) new AnprPageResponse(request.getPageSize(), pageCount, request.getPage(), list);
    }

    private JPAQuery<AnprEvent> addFiltersInAnprQuery(AnprFilterRequest request, QAnprEvent event, JPAQuery<AnprEvent> query) {
        query = query.where(event.archived.isFalse());

        if (!StringUtils.isEmpty(request.getLpr())) {
            query = query.where(event.anprText.likeIgnoreCase("%" + request.getLpr() + "%"));
        }

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            String fromDate = request.getFromDate();
            String toDate = request.getToDate();

            if (request.getFromDate() != null) {
                String fromTime = request.getFromTime() == null ? "00:00:00" : request.getFromTime();
                String starting = fromDate + " " + fromTime;
                Date startingDate = dateFormat.parse(starting);
                query = query.where(event.eventDate.after(startingDate));
            }

            if (request.getToTime() != null) {
                String toTime = request.getToTime() == null ? "00:00:00" : request.getToTime();
                String ending = toDate + " " + toTime;
                Date endingDate = dateFormat.parse(ending);
                query = query.where(event.eventDate.after(endingDate));
            }
        } catch (Exception e) {
            logger.error("Error in parsing date", e);
        }
        return query;
    }

    private JPAQuery<AnprEvent> createAnprQuery(QAnprEvent event) {
        JPAQuery<AnprEvent> query = new JPAQuery<>(entityManager);

        query = query.select(event).from(event);
        return query;
    }

    private BooleanExpression getIncidentQuery(AnprFilterRequest request) {
        BooleanExpression query = getQuery(request);
        QAnprEvent root = QAnprEvent.anprEvent;
        query = query.and(root.direction.eq("rev").or(root.helmetMissing.isTrue()));
        return query;
    }

    public PageResponse<IncidentRepeatCount> listRepeatedIncidents(AnprFilterRequest request) {

        QAnprEvent event = QAnprEvent.anprEvent;
        JPAQuery<Tuple> query = new JPAQuery<>(entityManager);

        query = query.select(event.anprText, event.anprText.count()).from(event);

        // for repeated incidents
        query = query.where(event.direction.eq("rev"));

        if (!StringUtils.isEmpty(request.getLpr())) {
            query = query.where(event.anprText.eq(request.getLpr()));
        }

        query = query.groupBy(event.anprText)
                .having(event.anprText.count().gt(1))
                .orderBy(event.anprText.count().desc());

        //pagination start
        int count = (int) anprEventRepository.countReverseDirectionRepeatedIncidents();
        int pageCount = (int) Math.ceil(count * 1.0 / request.getPageSize());
        int offset = (request.getPage() - 1) * request.getPageSize();
        query.offset(offset);
        if (request.getPageSize() > 0) {
            query.limit(request.getPageSize());
        }
        //pagination ends

        List<Tuple> data = query.fetch();
        List<IncidentRepeatCount> list = new ArrayList<>(request.getPageSize());
        data.forEach(item -> {
            String anprText = item.get(event.anprText);
            Long repeatedTimes = item.get(1, Long.class);

            IncidentRepeatCount res = new IncidentRepeatCount(anprText, repeatedTimes);
            list.add(res);
        });
        return (PageResponse<IncidentRepeatCount>) new IncidentRepeatPageResponse(request.getPageSize(), pageCount, request.getPage(), list);
    }

    public PageResponse<IncidentRepeatCount> listRepeatedHelmetMissingIncidents(AnprFilterRequest request) {

        QAnprEvent event = QAnprEvent.anprEvent;
        JPAQuery<Tuple> query = new JPAQuery<>(entityManager);

        query = query.select(event.anprText, event.anprText.count()).from(event);

        // for repeated incidents
        query = query.where(event.helmetMissing.isTrue());

        if (!StringUtils.isEmpty(request.getLpr())) {
            query = query.where(event.anprText.eq(request.getLpr()));
        }

        query = query.groupBy(event.anprText)
                .having(event.anprText.count().gt(1))
                .orderBy(event.anprText.count().desc());
        //pagination start
        int count = (int) anprEventRepository.countHelmetMissingRepeatedIncidents();
        int pageCount = (int) Math.ceil(count * 1.0 / request.getPageSize());
        int offset = (request.getPage() - 1) * request.getPageSize();
        query.offset(offset);
        if (request.getPageSize() > 0) {
            query.limit(request.getPageSize());
        }
        //pagination ends


        List<Tuple> data = query.fetch();
        List<IncidentRepeatCount> list = new ArrayList<>(request.getPageSize());
        data.forEach(item -> {
            String anprText = item.get(event.anprText);
            Long repeatedTimes = item.get(1, Long.class);

            IncidentRepeatCount res = new IncidentRepeatCount(anprText, repeatedTimes);
            list.add(res);
        });
        return (PageResponse<IncidentRepeatCount>) new IncidentRepeatPageResponse(request.getPageSize(), pageCount, request.getPage(), list);
    }

    public PageResponse<AnprResponse> getIncidentsTimeline(AnprFilterRequest request) {

        QAnprEvent event = QAnprEvent.anprEvent;
        JPAQuery<AnprEvent> query = new JPAQuery<>(entityManager);

        query = query.select(event).from(event);
        //query = query.where(event.helmetMissing.isTrue());

        query = query.where(event.anprText.eq(request.getLpr()));
        if (StringUtils.isEmpty(request.getIncidentType())) {
            request.setIncidentType("all");
        }
        switch (request.getIncidentType()) {
            case "Reverse":
                query = query.where(event.direction.eq("rev"));
                break;
            case "Helmet-Missing":
                query = query.where(event.helmetMissing.isTrue());
                break;
            default:
                query = query.where((event.helmetMissing.isTrue()).or(event.direction.eq("rev")));
        }

        //pagination start
        int count = (int) query.fetchCount();
        int pageCount = (int) Math.ceil(count * 1.0 / request.getPageSize());
        int offset = (request.getPage() - 1) * request.getPageSize();
        query.offset(offset);
        if (request.getPageSize() > 0) {
            query.limit(request.getPageSize());
        }
        //pagination ends

        query.orderBy(event.eventDate.desc());

        List<AnprEvent> data = query.fetch();
        List<AnprResponse> list = new ArrayList<>(request.getPageSize());
        data.forEach(item -> {
            AnprResponse res = new AnprResponse(item);
            list.add(res);
        });
        return (PageResponse<AnprResponse>) new AnprPageResponse(request.getPageSize(), pageCount, request.getPage(), list);
    }

    public PageResponse<AnprResponse> getEventsListByLpr(AnprFilterRequest request) {

        QAnprEvent event = QAnprEvent.anprEvent;
        JPAQuery<AnprEvent> query = new JPAQuery<>(entityManager);

        query = query.select(event).from(event);


        query = query.where(event.anprText.eq(request.getLpr()));
        if (StringUtils.isEmpty(request.getIncidentType())) {
            request.setIncidentType("all");
        }


        //pagination start
        int count = (int) query.fetchCount();
        int pageCount = (int) Math.ceil(count * 1.0 / request.getPageSize());
        int offset = (request.getPage() - 1) * request.getPageSize();
        query.offset(offset);
        if (request.getPageSize() > 0) {
            query.limit(request.getPageSize());
        }
        //pagination ends

        query.orderBy(event.eventDate.desc());

        List<AnprEvent> data = query.fetch();
        List<AnprResponse> list = new ArrayList<>(request.getPageSize());
        data.forEach(item -> {
            AnprResponse res = new AnprResponse(item);
            list.add(res);
        });
        return (PageResponse<AnprResponse>) new AnprPageResponse(request.getPageSize(), pageCount, request.getPage(), list);
    }

    public PageResponse<AnprResponse> getEventsCountListByLpr(AnprFilterRequest request) {
        QAnprEvent event = QAnprEvent.anprEvent;
        JPAQuery<Tuple> query = new JPAQuery<>(entityManager);

        query = query.select(event.anprText, event.anprText.count()).from(event);

        query = query.where(event.archived.isFalse());

        if (!StringUtils.isEmpty(request.getLpr())) {
            query = query.where(event.anprText.eq(request.getLpr()));
        }

        query = query.groupBy(event.anprText)

                .orderBy(event.anprText.count().desc());

        //pagination start
        int count = (int) anprEventRepository.findTotalEventsCountListOfEachLpr();
        int pageCount = (int) Math.ceil(count * 1.0 / request.getPageSize());
        int offset = (request.getPage() - 1) * request.getPageSize();
        query.offset(offset);
        if (request.getPageSize() > 0) {
            query.limit(request.getPageSize());
        }
        //pagination ends

        List<Tuple> data = query.fetch();
        List<IncidentRepeatCount> list = new ArrayList<>(request.getPageSize());
        data.forEach(item -> {
            String anprText = item.get(event.anprText);
            Long repeatedTimes = item.get(1, Long.class);

            IncidentRepeatCount res = new IncidentRepeatCount(anprText, repeatedTimes);
            list.add(res);
        });
        return (PageResponse<AnprResponse>) new IncidentRepeatPageResponse(request.getPageSize(), pageCount, request.getPage(), list);

    }
}
