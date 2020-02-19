package io.synlabs.synvision.controller.peopleCounting;

import io.synlabs.synvision.config.FileStorageProperties;
import io.synlabs.synvision.ex.AuthException;
import io.synlabs.synvision.service.ApcFileService;
import io.synlabs.synvision.views.UploadFileResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/public/apc")
public class ApcFileController {

    private static final Logger logger = LoggerFactory.getLogger(ApcFileController.class);

    @Autowired
    private final ApcFileService apcFileService;
    private String uploadKey;

    public ApcFileController(ApcFileService apcFileService, FileStorageProperties fileStorageProperties) {
        this.apcFileService = apcFileService;
        this.uploadKey = fileStorageProperties.getUploadKey();
    }

    @PostMapping("/import/csv")
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("key") String authKey, @RequestParam("tag") String tag) {

        logger.info("File uploaded, now importing..{} with tag {}", file.getOriginalFilename(), tag);
        if (uploadKey != null && !uploadKey.equals(authKey)) {
            logger.error("Keys not matching! supplied - {}", authKey);
            throw new AuthException("Not allowed");
        }

        String fileName = apcFileService.importFile(file, tag);

        return new UploadFileResponse(fileName, file.getContentType(), file.getSize(), tag);
    }
}
