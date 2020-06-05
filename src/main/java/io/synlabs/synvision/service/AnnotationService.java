package io.synlabs.synvision.service;

import io.synlabs.synvision.views.AnnotationRequest;
import io.synlabs.synvision.views.LineSegment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Service
public class AnnotationService {

    private static final Logger logger = LoggerFactory.getLogger(AnnotationService.class);

    public void saveAnnotation( AnnotationRequest request) throws IOException {
        File file = new File("annotation.txt");
        if(file.exists()) {
            file.delete();
        }
        FileWriter fw = new FileWriter("annotation.txt");
        for (LineSegment lineSegment : request.getLines()) {
            String line = String.format("[x1:%s, y1:%s, x2:%s, y2:%s]\n",
                    lineSegment.getX1(), lineSegment.getY1(), lineSegment.getX2(), lineSegment.getY2());
            fw.write(line);
        }
        logger.info("Annotations written to file");
        fw.close();
    }
}
