package io.synlabs.synvision.controller.parking;

import io.synlabs.synvision.config.FileStorageProperties;
import io.synlabs.synvision.controller.anpr.AnprController;
import io.synlabs.synvision.ex.FileStorageException;
import io.synlabs.synvision.service.parking.ParkingGuidanceService;
import io.synlabs.synvision.views.UploadFileResponse;
import io.synlabs.synvision.views.parking.HourlyStatsResponse;
import io.synlabs.synvision.views.parking.ParkingDashboardResponse;
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
import java.util.List;

@RestController
@RequestMapping("/api/apms/guidance")
public class ParkingGuidanceController {

    private static final Logger logger = LoggerFactory.getLogger(AnprController.class);

    @Autowired
    private FileStorageProperties fileStorageProperties;

    @Autowired
    private ParkingGuidanceService guidanceService;

    @GetMapping("/stats")
    public ParkingDashboardResponse stats() {
        return guidanceService.stats("lucknow");
    }

    @GetMapping("/hourly")
    public List<HourlyStatsResponse> hourly() {
        return guidanceService.hourly("lucknow");
    }

    //TODO attach this with the lot and do slot calculation
    //TODO upload to S3
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
}
