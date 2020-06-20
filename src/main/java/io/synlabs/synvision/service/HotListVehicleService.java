package io.synlabs.synvision.service;

import com.querydsl.jpa.impl.JPAQuery;
import io.synlabs.synvision.entity.anpr.HotListVehicle;
import io.synlabs.synvision.entity.anpr.QHotListVehicle;
import io.synlabs.synvision.jpa.HotListVehicleRepository;
import io.synlabs.synvision.views.hotlist.HotListVehicleFilterRequest;
import io.synlabs.synvision.views.hotlist.HotListVehiclePageResponse;
import io.synlabs.synvision.views.hotlist.HotListVehicleRequest;
import io.synlabs.synvision.views.hotlist.HotListVehicleResponse;
import io.synlabs.synvision.views.anpr.AnprPageResponse;
import io.synlabs.synvision.views.common.PageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class HotListVehicleService {

    @Autowired
    private HotListVehicleRepository hotListVehicleRepository;

    @Autowired
    private EntityManager entityManager;

    private static final Logger logger = LoggerFactory.getLogger(HotListVehicleService.class);


    public PageResponse<HotListVehicleResponse> listHotListedVehicles(HotListVehicleFilterRequest request) {

        QHotListVehicle hotListVehicle = new QHotListVehicle("hotListVehicle");


        JPAQuery<HotListVehicle> query = createHotListVehicleQuery(hotListVehicle);
        query = addFiltersInHotListVehicleQuery(request, hotListVehicle, query);


        //pagination
        int count = (int) query.fetchCount();
        int pageCount = (int) Math.ceil(count * 1.0 / request.getPageSize());

        int offset = (request.getPage() - 1) * request.getPageSize();
        query.offset(offset);
        if (request.getPageSize() > 0) {
            query.limit(request.getPageSize());
        }

        List<HotListVehicle> data = query.fetch();

        List<HotListVehicleResponse> list = new ArrayList<>(request.getPageSize());
        data.forEach(item -> {
            list.add(new HotListVehicleResponse(item));
        });
        return (PageResponse<HotListVehicleResponse>) new HotListVehiclePageResponse(request.getPageSize(), pageCount, request.getPage(), list);
    }


    private JPAQuery<HotListVehicle> createHotListVehicleQuery(QHotListVehicle hotListVehicle) {
        JPAQuery<HotListVehicle> query = new JPAQuery<>(entityManager);

        query = query.select(hotListVehicle).from(hotListVehicle);
        return query;
    }

    private JPAQuery<HotListVehicle> addFiltersInHotListVehicleQuery(HotListVehicleFilterRequest request, QHotListVehicle hotListVehicle, JPAQuery<HotListVehicle> query) {

        if (!StringUtils.isEmpty(request.getLpr())) {
            query = query.where(hotListVehicle.lpr.likeIgnoreCase("%" + request.getLpr() + "%"));
        }

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            String fromDate = request.getFromDate();
            String toDate = request.getToDate();

            if (request.getFromDate() != null) {
                String fromTime = request.getFromTime() == null ? "00:00:00" : request.getFromTime();
                String starting = fromDate + " " + fromTime;
                Date startingDate = dateFormat.parse(starting);
                query = query.where(hotListVehicle.createdDate.after(startingDate));
            }

            if (request.getToDate() != null) {
                String toTime = request.getToTime() == null ? "00:00:00" : request.getToTime();
                String ending = toDate + " " + toTime;
                Date endingDate = dateFormat.parse(ending);
                query = query.where(hotListVehicle.createdDate.after(endingDate));
            }
        } catch (Exception e) {
            logger.error("Error in parsing date", e);
        }
        return query;
    }

    public HotListVehicleResponse save(HotListVehicleRequest request) {
        HotListVehicle hotListVehicle = hotListVehicleRepository.findOneById(request.getId());
        if(hotListVehicle==null){
            hotListVehicle = new HotListVehicle();
        }
        hotListVehicle.setLpr(request.getLpr());
        hotListVehicle.setArchived(request.isArchived());
        hotListVehicle = hotListVehicleRepository.saveAndFlush(hotListVehicle);
        return new HotListVehicleResponse(hotListVehicle);
    }

}
