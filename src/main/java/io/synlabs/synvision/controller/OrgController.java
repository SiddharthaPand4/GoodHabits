package io.synlabs.synvision.controller;

import io.synlabs.synvision.service.OrgService;
import io.synlabs.synvision.views.OrgRequest;
import io.synlabs.synvision.views.OrgResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static io.synlabs.synvision.auth.SynvisionAuth.Privileges.ORG_SETTING_READ;
import static io.synlabs.synvision.auth.SynvisionAuth.Privileges.ORG_SETTING_WRITE;

@RestController
@RequestMapping("/api/org")
public class OrgController {

    @Autowired
    private OrgService orgService;

    @GetMapping("/details")
    @Secured(ORG_SETTING_READ)
    public OrgResponse orgDetails() {
        return orgService.orgDetails();
    }

    @PutMapping("/details")
    @Secured(ORG_SETTING_WRITE)
    public void saveOrgDetails(@RequestParam String name,
                               @RequestParam(required = false) Long id,
                               @RequestParam String legalName,
                               @RequestParam("logoFile") MultipartFile logoFile) {
        OrgRequest orgRequest = new OrgRequest(id, name, legalName, logoFile.getOriginalFilename());
        orgService.saveOrgDetails(orgRequest, logoFile);
    }

}
