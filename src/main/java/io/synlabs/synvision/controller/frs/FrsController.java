package io.synlabs.synvision.controller.frs;

import io.synlabs.synvision.service.frs.FrsEventService;
import io.synlabs.synvision.service.frs.RegisteredPersonService;
import io.synlabs.synvision.views.frs.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/frs/")
public class FrsController {

    @Autowired
    private RegisteredPersonService registeredPersonService;

    @Autowired
    private FrsEventService eventService;

    @PostMapping("register")
    public FRSRegisterResponse register(@RequestBody FRSRegisterRequest request) throws IOException {
        return new FRSRegisterResponse(registeredPersonService.register(request));
    }

    @PostMapping("lookup")
    public FRSLookupResponse lookup(@RequestBody FRSLookupRequest request){
        return new FRSLookupResponse(registeredPersonService.lookup(request));
    }

    @PostMapping("users")
    public FrsUserPageResponse getRegisteredUsers(@RequestBody FrsFilterRequest request) {
        return registeredPersonService.getRegistersUsers(request);
    }

    @PostMapping("events")
    public FrsEventPageResponse getEvents(@RequestBody FrsFilterRequest request) {
        return eventService.getEvents(request);
    }
}
