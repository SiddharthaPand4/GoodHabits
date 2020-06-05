package io.synlabs.synvision.views;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AnnotationRequest {

    private List<LineSegment> lines;

}

