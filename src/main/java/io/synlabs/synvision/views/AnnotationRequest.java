package io.synlabs.synvision.views;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class AnnotationRequest {
   // private MultipartFile file;
    private List<LineSegment> lines;
   // private String dataURL;

}

