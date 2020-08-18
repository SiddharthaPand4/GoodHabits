package io.synlabs.synvision.views;

import io.synlabs.synvision.views.common.Request;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AlertSettingsRequest implements Request {

    private String alertType;

    private Boolean status;

}
