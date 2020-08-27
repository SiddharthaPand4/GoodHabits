package io.synlabs.synvision.service;

import io.synlabs.synvision.entity.core.Org;
import io.synlabs.synvision.entity.core.SynVisionUser;
import io.synlabs.synvision.jpa.OrgRepository;
import io.synlabs.synvision.views.OrgRequest;
import io.synlabs.synvision.views.OrgResponse;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;

@Service
public class OrgService extends BaseService {

    private static final Logger logger = LoggerFactory.getLogger(OrgService.class);

    @Autowired
    private OrgRepository orgRepository;

    @Value("${file.upload-dir}")
    private String uploadDirPath;

    public OrgResponse orgDetails() {
//        SynVisionUser user = getCurrentUser();
//        Org org = user.getOrg();
//        return new OrgResponse(org);
        return new OrgResponse(orgRepository.findFirstByOrderByIdDesc());
    }

    public void saveOrgDetails(OrgRequest request, MultipartFile logoFileMultipart) {
        try {
            String logoFolder = uploadDirPath + "orgLogo/";
            File logoFile = new File(logoFolder + logoFileMultipart.getOriginalFilename());
            FileUtils.copyInputStreamToFile(logoFileMultipart.getInputStream(), logoFile);

            if (request.getId() != null && request.getId() != 0) {
                Optional<Org> opOrg = orgRepository.findById(request.getId());
                if (opOrg.isPresent()) {
                    Org org = opOrg.get();

                    File prevLogo = new File(logoFolder + org.getLogoFileName());
                    if (prevLogo.exists()) {
                        if (prevLogo.delete()) {
                            logger.info("Previous logo file deleted");
                        } else {
                            logger.error("Couldn't delete previous logo file");
                        }
                    }

                    org.update(request);
                    orgRepository.saveAndFlush(org);
                }
            } else {
                orgRepository.saveAndFlush(new Org(request));
            }
        } catch (IOException e) {
            logger.error("Error while saving org logo file ", e);
        }
    }

}