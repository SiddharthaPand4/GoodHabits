package io.synlabs.synvision.controller.frs;

import io.synlabs.synvision.service.FaceRecService;
import io.synlabs.synvision.views.frs.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/frs/")
public class FaceRecController {

    @Autowired
    private FaceRecService faceRecService;

    @PostMapping("register")
    public FRSRegisterResponse register(@RequestBody FRSRegisterRequest request){
        return new FRSRegisterResponse(faceRecService.register(request));
    }

    @PostMapping("lookup")
    public FRSLookupResponse lookup(@RequestBody FRSLookupRequest request){
        return new FRSLookupResponse(faceRecService.lookup(request));
    }

    @PostMapping("users")
    public FrsUserPageResponse getRegisteredUsers(@RequestBody FrsFilterRequest request) {
        return faceRecService.getRegistersUsers(request);
    }
}
