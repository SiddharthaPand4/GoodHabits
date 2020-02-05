package io.synlabs.synvision.views.parking;

import io.synlabs.synvision.entity.parking.ParkingSlot;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ParkingSlotResponse {

    private String name;

    private boolean misaligned;

    private boolean free;

    private String slotGroup;

    private String vehicleType;

    private int x;
    private int y;

    private int p1x;
    private int p1y;

    private int p2x;
    private int p2y;

    private int p3x;
    private int p3y;

    private int p4x;
    private int p4y;

    public ParkingSlotResponse(ParkingSlot slot) {
        this.name = slot.getName();
        this.free = slot.isFree();
        this.slotGroup = slot.getSlotGroup();
        this.vehicleType = slot.getVehicleType().name();
        this.misaligned = slot.isMisaligned();
        this.x = slot.getX();
        this.y = slot.getY();
        this.p1x = slot.getP1x();
        this.p1y = slot.getP1y();
        this.p2x = slot.getP2x();
        this.p2y = slot.getP2y();
        this.p3x = slot.getP3x();
        this.p3y = slot.getP3y();
        this.p4x = slot.getP4x();
        this.p4y = slot.getP4y();
    }
}
