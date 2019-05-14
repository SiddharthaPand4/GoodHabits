package io.synlabs.atcc.view;

import io.synlabs.atcc.entity.AtccRawData;
import lombok.Getter;
import lombok.Setter;

import java.text.SimpleDateFormat;

@Getter
@Setter
public class AtccRawDataResponse {
    private String type;
    private String ts;
    private int lane;

    public AtccRawDataResponse(AtccRawData atccRawData) {
        this.type = atccRawData.getType();
        this.ts = this.ts = new SimpleDateFormat("DD/MM/YYYY HH:MM:SS").format(atccRawData.getTs());
        this.lane = atccRawData.getLane();
    }
}
