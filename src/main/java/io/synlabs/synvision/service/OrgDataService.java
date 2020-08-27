package io.synlabs.synvision.service;

import io.synlabs.synvision.entity.core.Org;
import io.synlabs.synvision.entity.vids.HighwayIncident;
import io.synlabs.synvision.ex.NotFoundException;
import io.synlabs.synvision.jpa.OrgRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
public class OrgDataService extends BaseService {

    @Autowired
    private OrgRepository orgRepository;

    @Value("${file.upload-dir}")
    private String uploadDirPath;

    public Resource logoFile(Long id) {
        String tag = "orgLogo";
        Path fileStorageLocation = Paths.get(uploadDirPath)
                .toAbsolutePath().normalize();

        String filename = null;

        try {
            Optional<Org> org = orgRepository.findById(id);
            if (org.isPresent()) {
                filename = org.get().getLogoFileName();
                Path filePath = Paths.get(fileStorageLocation.toString(), tag, filename).toAbsolutePath().normalize();
                Resource resource = new UrlResource(filePath.toUri());
                if (resource.exists()) {
                    return resource;
                } else {
                    throw new NotFoundException("File not found " + filename);
                }
            } else {
                throw new NotFoundException("File not found " + filename);
            }

        } catch (MalformedURLException ex) {
            throw new NotFoundException("File not found " + filename, ex);
        }
    }

}
