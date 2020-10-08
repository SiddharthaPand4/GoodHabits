package io.synlabs.synvision.controller.avc;

import io.synlabs.synvision.service.avc.AvcReportService;
import io.synlabs.synvision.util.LongObfuscator;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/report/avc")
public class AvcReportController {

    private static final Logger logger = LoggerFactory.getLogger(AvcReportController.class);

    @Autowired
    private AvcReportService avcReportService;

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

    @GetMapping("/survey")
    public void avcEventReport(@RequestParam Long surveyId, HttpServletResponse response) throws IOException {
        String fileName = avcReportService.downloadAvcReportBySurvey(LongObfuscator.INSTANCE.unobfuscate(surveyId));
        handleHttpFileResponse(response, fileName);
    }

}
