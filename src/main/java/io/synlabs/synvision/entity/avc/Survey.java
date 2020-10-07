package io.synlabs.synvision.entity.avc;

import io.synlabs.synvision.entity.BaseEntity;
import io.synlabs.synvision.views.avc.SurveyRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Survey extends BaseEntity {

    private String name;

    @Column(unique = true)
    private String folderName;

    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;

    public Survey (SurveyRequest request) {
        this.name = request.getName();
        this.folderName = request.getFolder();
        this.startDate = request.getStartDate();
        this.endDate = request.getEndDate();
    }

}
