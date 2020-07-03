package io.synlabs.synvision.service.frs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.types.dsl.BooleanExpression;
import io.synlabs.synvision.config.FileStorageProperties;
import io.synlabs.synvision.entity.frs.QRegisteredPerson;
import io.synlabs.synvision.entity.frs.RegisteredPerson;
import io.synlabs.synvision.ex.NotFoundException;
import io.synlabs.synvision.ex.ValidationException;
import io.synlabs.synvision.jpa.RegisteredPersonRepository;
import io.synlabs.synvision.views.frs.*;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.springframework.data.domain.Sort.Direction.DESC;


@Service
public class RegisteredPersonService {

    private static final Logger logger = LoggerFactory.getLogger(RegisteredPersonService.class);

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    @Autowired
    private FileStorageProperties fileStorageProperties;
    @Autowired
    private RegisteredPersonRepository frsRepository;

    @Value("${file.upload-dir}")
    private String uploadDirPath;

    @Value("${frs.url}")
    private String frsurl;


    private Path fileStorageLocation;

    @PostConstruct
    public void init() {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();
    }
    public FrsUserPageResponse getRegistersUsers(FrsFilterRequest request) {
        BooleanExpression query = getQuery(request);
        int count = (int) frsRepository.count(query);
        int pageCount = (int) Math.ceil(count * 1.0 / request.getPageSize());
        Pageable paging = PageRequest.of(request.getPage() - 1, request.getPageSize(), Sort.by(DESC, "pid"));

        Page<RegisteredPerson> page = frsRepository.findAll(query, paging);
        List<FrsUserResponse> list = new ArrayList<>(page.getSize());
        page.get().forEach(item -> list.add(new FrsUserResponse(item)));

        return new FrsUserPageResponse(request.getPageSize(), pageCount, request.getPage(), list);
    }

    public BooleanExpression getQuery(FrsFilterRequest request) {

        try {

            String fromDate = request.getFromDate();
            String toDate = request.getToDate();
            QRegisteredPerson root = new QRegisteredPerson("registeredPerson");
            BooleanExpression query = root.active.isTrue();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


            if (request.getFromDate() != null) {
                String fromTime = request.getFromTime() == null ? "00:00:00" : request.getFromTime();
                String starting = fromDate + " " + fromTime;
                Date startingDate = dateFormat.parse(starting);
                query = query.and(root.createdDate.after(startingDate));
            }

            if (request.getToDate() != null) {
                String toTime = request.getToTime() == null ? "00:00:00" : request.getToTime();
                String ending = toDate + " " + toTime;
                Date endingDate = dateFormat.parse(ending);
                query = query.and(root.createdDate.before(endingDate));
            }

            if (request.getName() != null) {
                query = query.and(
                        root.name.likeIgnoreCase("%" + request.getName() + "%").or(root.pid.likeIgnoreCase("%" + request.getName() + "%"))
                );
            }

            return query;
        } catch (Exception e) {
            logger.error("Error in parsing date", e);
        }
        return null;
    }


    public RegisteredPerson register(FRSRegisterRequest request) throws IOException {

        RegisteredPerson re = frsRepository.findOneByPidAndActiveTrue(request.getId());

        if (re != null) {
            throw new ValidationException("Duplicate ID");
        }

        OkHttpClient client = new OkHttpClient();
        try {
            Request okrequest = new Request.Builder()
                    .header("Authorization", "your token")
                    .url(frsurl)
                    .post(RequestBody.create(request.toJsonString(), JSON))
                    .build();

            logger.info("Outbound: {}", okrequest);
            Response okresponse = client.newCall(okrequest).execute();
            if (okresponse.isSuccessful()) {

                ObjectMapper mapper = new ObjectMapper();
                RegisterResponse resp = mapper.readValue(Objects.requireNonNull(okresponse.body()).string(), RegisterResponse.class);

                if (resp.error) {
                    throw new ValidationException(resp.message);
                }

                return frsRepository.findOneByUid(resp.uid);
            } else {
                throw new IOException("Unexpected code " + okresponse);
            }

        } catch (IOException e) {
            throw new ValidationException("Error!");
        }

    }

    static class RegisterResponse {
        public boolean error;
        public String message;
        public String uid;
    }

    static class OkResponse {
        public String id;
        public String dist;
    }

    public RegisteredPerson lookup(FRSLookupRequest request) {

        OkHttpClient client = new OkHttpClient();
        try {
            Request okrequest = new Request.Builder()
                    .header("Authorization", "your token")
                    .url(frsurl)
                    .post(RequestBody.create(request.toJsonString(), JSON))
                    .build();

            Response okresponse = client.newCall(okrequest).execute();
            if (okresponse.isSuccessful()) {
                ObjectMapper mapper = new ObjectMapper();
                OkResponse resp = mapper.readValue(Objects.requireNonNull(okresponse.body()).string(), OkResponse.class);
                return frsRepository.findOneByPidAndActiveTrue(resp.id);
            } else {
                throw new IOException("Unexpected code " + okresponse);
            }

        } catch (IOException e) {
            logger.error("Error calling api", e);
            throw new ValidationException("Error!");
        }
    }

    public Resource downloadFaceImage(String uid) {
        RegisteredPerson person = frsRepository.findOneByUid(uid);
        if (person == null) {
            throw new NotFoundException("Cannot locate person!");
        }

        if (StringUtils.isEmpty(person.getFaceImage())) {
            throw new NotFoundException("Cannot locate person face image!");
        }
        return download(person.getFaceImage());
    }


    public Resource downloadPersonImage(String uid) {
        RegisteredPerson person = frsRepository.findOneByUid(uid);
        if (person == null) {
            throw new NotFoundException("Cannot locate person!");
        }

        if (StringUtils.isEmpty(person.getFullImage())) {
            throw new NotFoundException("Cannot locate person face image!");
        }
        return download(person.getFaceImage());
    }

    private Resource download(String filepath) {

        try {
            Path filePath = Paths.get(this.fileStorageLocation.toString(), filepath).toAbsolutePath().normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new NotFoundException("File not found " + filepath);
            }


        } catch (MalformedURLException ex) {
            throw new NotFoundException("unknown error", ex);
        }
    }

}
