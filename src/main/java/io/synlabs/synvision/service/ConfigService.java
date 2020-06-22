package io.synlabs.synvision.service;

import io.synlabs.synvision.views.ConfigRequest;
import io.synlabs.synvision.views.LineSegment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ConfigService {

    private static final Logger logger = LoggerFactory.getLogger(ConfigService.class);

    private Process process;

    public void saveAnnotation(ConfigRequest request) throws IOException {
        String filename = null;
        Path path = Paths.get("d:/");
        filename = path.resolve(UUID.randomUUID().toString() + ".txt").toString();
        File file = new File(String.valueOf(path));
        if (file.exists()) {
            file.delete();
            //file.renameTo(new File(""));
        }
        FileWriter fw = new FileWriter(filename);
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

}
