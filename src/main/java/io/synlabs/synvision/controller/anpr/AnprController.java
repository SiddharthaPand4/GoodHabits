package io.synlabs.synvision.controller.anpr;

import io.synlabs.synvision.config.FileStorageProperties;
import io.synlabs.synvision.ex.FileStorageException;
import io.synlabs.synvision.service.AnprService;
import io.synlabs.synvision.service.parking.AnprReportService;
import io.synlabs.synvision.views.UploadFileResponse;
import io.synlabs.synvision.views.anpr.*;
import io.synlabs.synvision.views.common.PageResponse;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Created by itrs on 10/21/2019.
 */
@RestController
@RequestMapping("/api/anpr")
public class AnprController {

    private static final Logger logger = LoggerFactory.getLogger(AnprController.class);

    @Autowired
    private AnprReportService anprReportService;
    @Autowired
    private FileStorageProperties fileStorageProperties;

    @Autowired
    private AnprService anprService;

    @PostMapping("/events")
    public PageResponse<AnprResponse> list(@RequestBody AnprFilterRequest request) {
        return anprService.list(request);
    }
    //shashank
    @PostMapping("/anprevent")
    public void anprEventReport(@RequestBody AnprReportRequest request, HttpServletResponse response) throws IOException {
        File file=null;
        String fileName=null;
        fileName = anprReportService.downloadAnprEvents(request);

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
    //shashank

    @PostMapping("/events/list/lpr/count")
    public PageResponse<AnprResponse> getEventsCountListByLpr(@RequestBody AnprFilterRequest request) {
        return anprService.getEventsCountListByLpr(request);
    }
    @PostMapping("/events/list/bylpr")
    public PageResponse<AnprResponse> getEventsListByLpr(@RequestBody AnprFilterRequest request) {
        return anprService.getEventsListByLpr(request);
    }

    @PostMapping("/incidents")
    public PageResponse<AnprResponse> listIncidents(@RequestBody AnprFilterRequest request) {
        return anprService.listIncidents(request);
    }

    @PostMapping("/incidents/hotListed")
    public PageResponse<AnprResponse> listHotListedIncidents(@RequestBody AnprFilterRequest request) {
        return anprService.listHotListedIncidents(request);
    }

    @DeleteMapping("/{id}")
    public void archiveAnpr(@PathVariable Long id) {
        anprService.archiveAnprEvent(new AnprRequest(id));
    }

    @PostMapping("/vehicle")
    public void addVehicle(@RequestBody CreateAnprRequest request) {
        anprService.addAnprEvent(request);
    }

    @PostMapping("/image")
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("tag") String tag) {

        if (file == null) throw new FileStorageException("Missing file in multipart");
        logger.info("File uploaded, now importing..{} with tag {}", file.getOriginalFilename(), tag);
        try {
            Path fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir(), tag).toAbsolutePath().normalize();

            if (!Files.exists(fileStorageLocation)) {
                File dir = new File(fileStorageLocation.toString());
                dir.mkdirs();
            }

            String fileName = StringUtils.cleanPath(file.getOriginalFilename());

            Path targetLocation = fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return new UploadFileResponse(fileName, file.getContentType(), file.getSize(), tag);
        } catch (IOException e) {
            throw new FileStorageException("Error copying file to storage");
        }
    }

    @PutMapping("/event")
    public AnprResponse updateEvent(@RequestBody AnprRequest request) {
        return anprService.updateAnprEvent(request);
    }

    @PutMapping("/events/archive/{lpr}")
    public void archiveAnpr(@PathVariable String lpr) {
        anprService.archiveAnprEvents(new AnprRequest(lpr));
    }
}
