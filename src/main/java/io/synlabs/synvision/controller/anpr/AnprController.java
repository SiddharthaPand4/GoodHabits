package io.synlabs.synvision.controller.anpr;

import io.synlabs.synvision.config.FileStorageProperties;
import io.synlabs.synvision.ex.FileStorageException;
import io.synlabs.synvision.service.AnprService;
import io.synlabs.synvision.views.UploadFileResponse;
import io.synlabs.synvision.views.anpr.AnprFilterRequest;
import io.synlabs.synvision.views.anpr.AnprRequest;
import io.synlabs.synvision.views.anpr.AnprResponse;
import io.synlabs.synvision.views.anpr.CreateAnprRequest;
import io.synlabs.synvision.views.common.PageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Created by itrs on 10/21/2019.
 */
@RestController
@RequestMapping("/api/anpr")
public class AnprController {

    private static final Logger logger = LoggerFactory.getLogger(AnprController.class);

    @Autowired
    private FileStorageProperties fileStorageProperties;

    @Autowired
    private AnprService anprService;

    @PostMapping("/events")
    public PageResponse<AnprResponse> list(@RequestBody AnprFilterRequest request) {
        return anprService.list(request);
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
        anprService.archiveAnpr(new AnprRequest(id));
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

}
