package io.synlabs.synvision.controller.peopleCounting;

import io.synlabs.synvision.config.FileStorageProperties;
import io.synlabs.synvision.service.ApcFileService;
import io.synlabs.synvision.views.apc.ApcFilterRequest;
import io.synlabs.synvision.views.apc.ApcRequest;
import io.synlabs.synvision.views.apc.ApcResponse;
import io.synlabs.synvision.views.common.PageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/apc")
public class PeopleCountingController {

    private static final Logger logger = LoggerFactory.getLogger(PeopleCountingController.class);

    @Autowired
    private FileStorageProperties fileStorageProperties;
    @Autowired
    private ApcFileService apcFileService;

  @PostMapping("/pplData")
    public PageResponse<ApcResponse> listPeople(@RequestBody ApcFilterRequest request) {
        return apcFileService.listPeople(request);
    }


    @DeleteMapping("/{id}")
    public void archiveEvent(@PathVariable Long id) {
        apcFileService.archiveEvent(new ApcRequest(id));
    }



}
