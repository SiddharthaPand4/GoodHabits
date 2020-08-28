package io.synlabs.synvision.controller;

import io.synlabs.synvision.service.OrgService;
import io.synlabs.synvision.views.OrgRequest;
import io.synlabs.synvision.views.OrgResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/org")
public class OrgController {

    @Autowired
    private OrgService orgService;

    @GetMapping("/details")
    public OrgResponse orgDetails() {
        return orgService.orgDetails();
    }

    @PutMapping("/details")
    public void saveOrgDetails(@RequestParam String name,
                               @RequestParam(required = false) Long id,
                               @RequestParam String legalName,
                               @RequestParam("logoFile") MultipartFile logoFile) {
        OrgRequest orgRequest = new OrgRequest(id, name, legalName, logoFile.getOriginalFilename());
        orgService.saveOrgDetails(orgRequest, logoFile);
    }

}
