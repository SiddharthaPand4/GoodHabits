package io.synlabs.synvision.controller;

import io.synlabs.synvision.jpa.ParkingEventRepository;
import io.synlabs.synvision.service.AnprService;
import io.synlabs.synvision.service.report.AtccReportService;
import io.synlabs.synvision.service.parking.ApmsService;
import io.synlabs.synvision.service.report.VidsReportService;
import io.synlabs.synvision.views.anpr.AnprReportRequest;
import io.synlabs.synvision.views.atcc.AtccReportRequest;
import io.synlabs.synvision.views.parking.ParkingReportRequest;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

import static io.synlabs.synvision.auth.SynvisionAuth.Privileges.ANPR_READ;
import static io.synlabs.synvision.auth.SynvisionAuth.Privileges.PARKING_READ;

/**
 * Created by itrs on 02/03/2020.
 */
@RestController
@RequestMapping("/api/report")
public class ReportController {

    @Autowired
    private ApmsService apmsService;

    @Autowired
    private AnprService anprService;

    @Autowired
    private AtccReportService atccReportService;

    @Autowired
    private VidsReportService vidsReportService;

    @Autowired
    private ParkingEventRepository parkingEventRepository;

    private static Logger logger = LoggerFactory.getLogger(ReportController.class);

    @PostMapping("/parkingevents")
    @Secured(PARKING_READ)
    public void parkingEventReport(@RequestBody ParkingReportRequest request, HttpServletResponse response) throws IOException {
        String fileName = null;
        if (request.getXAxis().equals("All Entry-Exit")) {
            fileName = apmsService.downloadParkingEvents(request);

        } else {
            fileName = apmsService.downloadParkingEventsOnDailyBasis(request);
        }
        handleHttpFileResponse(response, fileName);
    }

    @PostMapping("/anprevents")
    @Secured(ANPR_READ)
    public void anprEventReport(@RequestBody AnprReportRequest request, HttpServletResponse response) throws IOException {
        String fileName = null;

        if (request.getXAxis().equals("All Entry-Exit")) {
            fileName = anprService.downloadAnprEvents(request);
        } else {
            fileName = anprService.downloadAnprEventsOnDailyBasis(request);
        }
        handleHttpFileResponse(response, fileName);
    }

    @PostMapping("/atcc/events/all")
    public void atccEventAllReport(@RequestBody AtccReportRequest request, HttpServletResponse response) throws IOException {
        String fileName = atccReportService.downloadAtccEvents(request);
        handleHttpFileResponse(response, fileName);
    }

    @PostMapping("/atcc/events/summary/day-wise")
    public void atccEventSummaryReport(@RequestBody AtccReportRequest request, HttpServletResponse response) throws IOException {
        String fileName = atccReportService.downloadEventsDaywiseSummary(request);
        handleHttpFileResponse(response, fileName);
    }

    @PostMapping("/vids/incidents/all")
    public void vidsInciendentAllReport(@RequestBody AtccReportRequest request, HttpServletResponse response) throws IOException {
        String fileName = vidsReportService.generateHighwayIncidentsReport(request);
        handleHttpFileResponse(response, fileName);
    }

    @PostMapping("/vids/incidents/summary/day-wise")
    public void vidsInciendentSummaryReport(@RequestBody AtccReportRequest request, HttpServletResponse response) throws IOException {
        String fileName = vidsReportService.downloadHighwayIncidentsOnDailyBasis(request);
        handleHttpFileResponse(response, fileName);
    }


    private void handleHttpFileResponse(HttpServletResponse response, String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
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
