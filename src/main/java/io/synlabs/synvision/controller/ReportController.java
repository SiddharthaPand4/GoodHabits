package io.synlabs.synvision.controller;

import ch.qos.logback.core.util.FileUtil;
import io.synlabs.synvision.entity.parking.ParkingEvent;
import io.synlabs.synvision.jpa.ParkingEventRepository;
import io.synlabs.synvision.service.parking.ApmsService;
import io.synlabs.synvision.views.parking.ParkingReportRequest;
import io.synlabs.synvision.views.parking.ParkingReportResponse;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

/**
 * Created by itrs on 02/03/2020.
 */
@RestController
@RequestMapping("/api/report")
public class ReportController {

    @Autowired
    private ApmsService apmsService;

    @Autowired
    private ParkingEventRepository parkingEventRepository;

    private static Logger logger = LoggerFactory.getLogger(ReportController.class);

    @PostMapping("/parkingevents")
    public void parkingEventReport(@RequestBody ParkingReportRequest request, HttpServletResponse response) throws IOException {
        File file=null;
        String fileName=null;

        if (request.getXAxis().equals("All Entry-Exit")) {
            fileName = apmsService.downloadParkingEvents(request);

        } else {
            fileName = apmsService.downloadParkingEventsOnDailyBasis(request);

        }


        file = new File(fileName);

        if (file != null && file.exists()) {
            String extension = FilenameUtils.getExtension(file.getName());
            fileName = fileName + UUID.randomUUID().toString();
            try {
                FileInputStream is = new FileInputStream(file);
                OutputStream outputStream = response.getOutputStream();
                response.setContentType("application/vnd.ms-excel");
                response.setHeader("Content-Disposition", "attachment; filename=" + fileName + "." + extension);
                IOUtils.copy(is, outputStream);
                outputStream.flush();
                outputStream.close();
                is.close();

                if (!file.delete()) {
                    logger.warn("Coudn't delete the file : " + file.getAbsolutePath());
                }
            } catch (FileNotFoundException e) {
                logger.error("UNABLE TO FIND FILE : " + file.getAbsolutePath(), e);
            } catch (IOException e) {
                logger.error("ERROR IN FILE : " + file.getAbsolutePath(), e);
            }
        } else {
            logger.warn("Generated file is empty.");
        }

    }
}
