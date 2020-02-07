package io.synlabs.synvision.views.parking;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateSlotRequest {
    private String lot;
    private String slot;
    private boolean status;
    private boolean misaligned;
}
