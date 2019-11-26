package io.synlabs.synvision.views.anpr;

import io.synlabs.synvision.views.common.Request;

import java.util.Date;

public class CreateAnprRequest implements Request {

    private Long id;
    private String eventId;
    private Date eventDate;
    private String ocrImage;

    private String anprText;
    private String vehicleImage;
}
