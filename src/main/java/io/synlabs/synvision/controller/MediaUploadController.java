package io.synlabs.synvision.controller;

import io.synlabs.synvision.config.FileStorageProperties;
import io.synlabs.synvision.controller.vids.VidsController;
import io.synlabs.synvision.ex.FileStorageException;
import io.synlabs.synvision.views.UploadFileResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public abstract class MediaUploadController {

    private static final Logger logger = LoggerFactory.getLogger(VidsController.class);

    protected static UploadFileResponse UploadFile(MultipartFile file, String tag, FileStorageProperties fileStorageProperties) {
        if (file == null) throw new FileStorageException("Missing file in multipart");
        logger.info("File uploaded, now importing..{} with tag {}", file.getOriginalFilename(), tag);
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String strDate=formatter.format(new Date());
        try {
            Path fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir(), tag).toAbsolutePath().normalize();
            fileStorageLocation=fileStorageLocation.resolve(strDate);
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
