package io.synlabs.synvision.service;

import io.synlabs.synvision.views.AnnotationRequest;
import io.synlabs.synvision.views.LineSegment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Decoder;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import static com.sun.jmx.snmp.EnumRowStatus.destroy;

@Service
public class AnnotationService {

    private static final Logger logger = LoggerFactory.getLogger(AnnotationService.class);

    private Process process;

    public void saveAnnotation(AnnotationRequest request) throws IOException {
        File file = new File("annotation.txt");
        if (file.exists()) {
            file.delete();
            //file.renameTo(new File(""));
        }
        FileWriter fw = new FileWriter("annotation.txt");
        for (LineSegment lineSegment : request.getLines()) {
            String line = String.format("[x1:%s, y1:%s, x2:%s, y2:%s]\n",
                    lineSegment.getX1(), lineSegment.getY1(), lineSegment.getX2(), lineSegment.getY2());
            fw.write(line);
        }
        logger.info("Annotations written to file");
        fw.close();

        //for converting url to image and saving it.
        //  String sourceData=request.getDataURL();
        //  String[] parts = sourceData.split(",");
        //  String imageString = parts[1];


        //  BufferedImage image = null;
        //  byte[] imageByte;

        //  BASE64Decoder decoder = new BASE64Decoder();
        //  imageByte = decoder.decodeBuffer(imageString);
        //  ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
        //  image = ImageIO.read(bis);
        //  bis.close();

// wr//te the image to a file
        //  File outputfile = new File("image.png");
        //  if(outputfile.exists())
        //  {
        //      outputfile.delete();
        //  }
        //  ImageIO.write(image, "png", outputfile);
    }

    public void startFeed() throws IOException, InterruptedException {
        System.out.println("Process started");
        File dir = new File("E://LiveFeed");

        process = Runtime.getRuntime().exec("streamer https://www.radiantmediaplayer.com/media/bbb-360p.mp4 localhost:9000 ", null, dir);

    }

    public void stopFeed() throws IOException, InterruptedException {

        process.destroy();

    }
}
