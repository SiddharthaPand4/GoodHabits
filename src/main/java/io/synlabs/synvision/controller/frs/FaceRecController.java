package io.synlabs.synvision.controller.frs;

import io.synlabs.synvision.service.FaceRecService;
import io.synlabs.synvision.views.frs.FRSLookupRequest;
import io.synlabs.synvision.views.frs.FRSLookupResponse;
import io.synlabs.synvision.views.frs.FRSRegisterRequest;
import io.synlabs.synvision.views.frs.FRSRegisterResponse;
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
        return faceRecService.register(request);
    }

    @PostMapping("lookup")
    public FRSLookupResponse register(@RequestBody FRSLookupRequest request){
        return new FRSLookupResponse(faceRecService.lookup(request));
    }
}
