package io.synlabs.synvision.service;

import io.synlabs.synvision.views.frs.FRSLookupRequest;
import io.synlabs.synvision.views.frs.FRSLookupResponse;
import io.synlabs.synvision.views.frs.FRSRegisterRequest;
import io.synlabs.synvision.views.frs.FRSRegisterResponse;
import org.springframework.stereotype.Service;

@Service
public class FaceRecService {

    public FRSRegisterResponse register(FRSRegisterRequest request) {
        FRSRegisterResponse response = new FRSRegisterResponse("001", "sushil", "vipul square");
        return response;
    }

    public FRSLookupResponse lookup(FRSLookupRequest request) {
        FRSLookupResponse response = new FRSLookupResponse("001", "sushil", "vipul square");
        return response;
    }
}
