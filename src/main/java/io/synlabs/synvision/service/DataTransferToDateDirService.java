package io.synlabs.synvision.service;

import com.querydsl.jpa.impl.JPAQuery;
import io.synlabs.synvision.config.FileStorageProperties;
import io.synlabs.synvision.entity.anpr.AnprEvent;
import io.synlabs.synvision.entity.anpr.QAnprEvent;
import io.synlabs.synvision.entity.atcc.AtccEvent;
import io.synlabs.synvision.entity.vids.HighwayIncident;
import io.synlabs.synvision.entity.vids.HighwayTrafficState;
import io.synlabs.synvision.ex.FileStorageException;
import io.synlabs.synvision.jpa.AnprEventRepository;
import io.synlabs.synvision.jpa.AtccEventRepository;
import io.synlabs.synvision.jpa.HighwayIncidentRepository;
import io.synlabs.synvision.jpa.HighwayTrafficStateRepository;
import io.synlabs.synvision.views.UploadFileResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.List;

@Service
public class DataTransferToDateDirService {
    @Autowired
    private AnprEventRepository anprEventRepository;
    @Autowired
    private AtccEventRepository atccEventRepository;
    @Autowired
    private HighwayIncidentRepository incidentRepository;
    @Autowired
    private HighwayTrafficStateRepository stateRepository;

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private FileStorageProperties fileStorageProperties;

    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

    public void transferAnprData() {
       List<AnprEvent> anprEvents= anprEventRepository.findAll();
        for(AnprEvent anprEvent :anprEvents)
        {
            Path fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir(),"anpr").toAbsolutePath().normalize();
            Path fileName=fileStorageLocation.resolve(anprEvent.getVehicleImage()+".jpg");
            if(Files.exists(fileName))
            {
                String date=formatter.format(anprEvent.getEventDate());
                fileStorageLocation=fileStorageLocation.resolve(date);
                if (!Files.exists(fileStorageLocation)) {
                    File dir = new File(fileStorageLocation.toString());
                    dir.mkdirs();
                }
                try {
                    Files.copy(fileName,fileStorageLocation.resolve(anprEvent.getVehicleImage()+".jpg"),StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void transferAtccData() {
        List<AtccEvent> atccEvents= atccEventRepository.findAll();
        for(AtccEvent atccEvent :atccEvents)
        {
            Path fileStorageLocationImg = Paths.get(fileStorageProperties.getUploadDir(),"atcc-image").toAbsolutePath().normalize();
            Path fileStorageLocationVid = Paths.get(fileStorageProperties.getUploadDir(),"atcc-video").toAbsolutePath().normalize();
            Path fileNameImg=fileStorageLocationImg.resolve(atccEvent.getEventImage());
            Path fileNameVid=fileStorageLocationVid.resolve(atccEvent.getEventVideo());
            if(Files.exists(fileNameImg))
            {
                String date=formatter.format(atccEvent.getEventDate());
                fileStorageLocationImg=fileStorageLocationImg.resolve(date);
                if (!Files.exists(fileStorageLocationImg)) {
                    File dir = new File(fileStorageLocationImg.toString());
                    dir.mkdirs();
                }
                try {
                    Files.copy(fileNameImg,fileStorageLocationImg.resolve(atccEvent.getEventImage()),StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(Files.exists(fileNameVid))
            {
                String date=formatter.format(atccEvent.getEventDate());
                fileStorageLocationVid=fileStorageLocationVid.resolve(date);
                if (!Files.exists(fileStorageLocationVid)) {
                    File dir = new File(fileStorageLocationVid.toString());
                    dir.mkdirs();
                }
                try {
                    Files.copy(fileNameVid,fileStorageLocationVid.resolve(atccEvent.getEventVideo()),StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void transferVidsData() {
        List<HighwayIncident> highwayIncidents=incidentRepository.findAll();
        for(HighwayIncident highwayIncident :highwayIncidents)
        {
            Path fileStorageLocationImg = Paths.get(fileStorageProperties.getUploadDir(),"vids-image").toAbsolutePath().normalize();
            Path fileStorageLocationVid = Paths.get(fileStorageProperties.getUploadDir(),"vids-video").toAbsolutePath().normalize();
            Path fileNameImg=fileStorageLocationImg.resolve(highwayIncident.getIncidentImage());
            Path fileNameVid=fileStorageLocationVid.resolve(highwayIncident.getIncidentVideo());
            String date=formatter.format(highwayIncident.getIncidentDate());
            if(Files.exists(fileNameImg))
            {

                fileStorageLocationImg=fileStorageLocationImg.resolve(date);
                if (!Files.exists(fileStorageLocationImg)) {
                    File dir = new File(fileStorageLocationImg.toString());
                    dir.mkdirs();
                }
                try {
                    Files.copy(fileNameImg,fileStorageLocationImg.resolve(highwayIncident.getIncidentImage()),StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                   e.printStackTrace();
                }
            }
            if(Files.exists(fileNameVid))
            {
                fileStorageLocationVid=fileStorageLocationVid.resolve(date);
                if (!Files.exists(fileStorageLocationVid)) {
                    File dir = new File(fileStorageLocationVid.toString());
                    dir.mkdirs();
                }
                try {
                    Files.copy(fileNameVid,fileStorageLocationVid.resolve(highwayIncident.getIncidentVideo()),StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                   e.printStackTrace();
                }
            }
        }
    }

    public void transferOffenceData(){
        List<AnprEvent> anprEvents= anprEventRepository.findAll();
        for(AnprEvent anprEvent :anprEvents)
        {
            Path fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir(),"vehicle").toAbsolutePath().normalize();
            Path fileName=fileStorageLocation.resolve(anprEvent.getVehicleImage()+".jpg");
            if(Files.exists(fileName))
            {
                String date=formatter.format(anprEvent.getEventDate());
                fileStorageLocation=fileStorageLocation.resolve(date);
                if (!Files.exists(fileStorageLocation)) {
                    File dir = new File(fileStorageLocation.toString());
                    dir.mkdirs();
                }
                try {
                    Files.copy(fileName,fileStorageLocation.resolve(anprEvent.getVehicleImage()+".jpg"),StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void transferFlowStateData(){
        List<HighwayTrafficState> trafficStates= stateRepository.findAll();
        for(HighwayTrafficState trafficState :trafficStates)
        {
            Path fileStorageLocationImg = Paths.get(fileStorageProperties.getUploadDir(),"flow-image").toAbsolutePath().normalize();
            Path fileStorageLocationVid = Paths.get(fileStorageProperties.getUploadDir(),"flow-video").toAbsolutePath().normalize();
            Path fileNameImg=fileStorageLocationImg.resolve(trafficState.getFlowImage());
            //Path fileNameVid=fileStorageLocationVid.resolve(trafficState.getFlowVideo());
            if(Files.exists(fileNameImg))
            {
                String date=formatter.format(trafficState.getUpdateDate());
                fileStorageLocationImg=fileStorageLocationImg.resolve(date);
                if (!Files.exists(fileStorageLocationImg)) {
                    File dir = new File(fileStorageLocationImg.toString());
                    dir.mkdirs();
                }
                try {
                    Files.copy(fileNameImg,fileStorageLocationImg.resolve(trafficState.getFlowImage()),StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
           // if(Files.exists(fileNameVid))
           // {
           //     String date=formatter.format(trafficState.getUpdateDate());
           //     fileStorageLocationVid=fileStorageLocationVid.resolve(date);
           //     if (!Files.exists(fileStorageLocationVid)) {
           //         File dir = new File(fileStorageLocationVid.toString());
           //         dir.mkdirs();
           //     }
           //     try {
            //       Files.copy(fileNameVid,fileStorageLocationVid.resolve(trafficState.getFlowVideo()),StandardCopyOption.REPLACE_EXISTING);
           //     } catch (IOException e) {
           //         e.printStackTrace();
           //     }
           // }
        }
    }
}
