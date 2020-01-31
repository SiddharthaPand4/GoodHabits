package io.synlabs.synvision.views.apc;

import io.synlabs.synvision.views.common.PageRequest;

public class ApcFilterRequest extends PageRequest {
    public String pcId;
    public String fromDate;
    public String fromTime;
    public String toDate;
    public String toTime;
    //public int page;
    //public int pageSize;
    public String getPcId(){
        return pcId;
    }
    public void setPcId(String pcId){
        this.pcId=pcId;
    }
    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }


    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setFromTime(String fromTime) {
        this.fromTime = fromTime;
    }

    public String getFromTime() {
        return fromTime;
    }


    public void setToTime(String toTime) {
        this.toTime = toTime;
    }

    public String getToTime() {
        return toTime;
    }


}
