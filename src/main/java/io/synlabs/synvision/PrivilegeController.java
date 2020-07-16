package io.synlabs.synvision;


import io.synlabs.synvision.service.PrivilegeService;
import io.synlabs.synvision.views.core.PrivilegeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/privilege/")
public class PrivilegeController {

    @Autowired
    private PrivilegeService privilegeService;

    @GetMapping("get/list")
    public List<PrivilegeResponse> getPrivilegeTypes() {
        return privilegeService.getPrivilegeTypes().stream().map(PrivilegeResponse::new).collect(Collectors.toList());
    }
}
