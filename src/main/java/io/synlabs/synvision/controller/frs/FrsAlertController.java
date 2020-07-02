package io.synlabs.synvision.controller.frs;

import io.synlabs.synvision.entity.frs.FrsEvent;
import io.synlabs.synvision.service.frs.FrsEventService;
import io.synlabs.synvision.views.frs.AlertMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public/frsalert")
public class FrsAlertController {

    @Autowired
    private SimpMessagingTemplate websocket;

    @Autowired
    private FrsEventService eventService;

    @GetMapping("{eid}")
    public void blacklistAlert(@PathVariable("eid") String eid) {
        FrsEvent event = eventService.getEvent(eid);
        AlertMessage message = new AlertMessage(event);
        websocket.convertAndSend("/alert", message);
    }

}
