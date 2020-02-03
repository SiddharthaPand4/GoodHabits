package io.synlabs.synvision.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.synlabs.synvision.entity.frs.RegisteredPerson;
import io.synlabs.synvision.enums.PersonType;
import io.synlabs.synvision.ex.ValidationException;
import io.synlabs.synvision.jpa.RegisteredPersonRepository;
import io.synlabs.synvision.views.frs.FRSLookupRequest;
import io.synlabs.synvision.views.frs.FRSLookupResponse;
import io.synlabs.synvision.views.frs.FRSRegisterRequest;
import io.synlabs.synvision.views.frs.FRSRegisterResponse;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;


@Service
public class FaceRecService {

    private static final Logger logger = LoggerFactory.getLogger(FaceRecService.class);

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    @Autowired
    private RegisteredPersonRepository frsRepository;

    public RegisteredPerson register(FRSRegisterRequest request) {

        OkHttpClient client = new OkHttpClient();
        try {
            Request okrequest = new Request.Builder()
                    .header("Authorization", "your token")
                    .url("http://localhost:5000/register")
                    .post(RequestBody.create(request.toJsonString(), JSON))
                    .build();

            logger.info("Outbound: {}", okrequest);
            Response okresponse = client.newCall(okrequest).execute();
            if (okresponse.isSuccessful()) {
                RegisteredPerson person = new RegisteredPerson();
                person.setPid(request.getId());
                person.setName(request.getName());
                person.setAddress(request.getAddress());
                person.setPersonType(PersonType.Subject);
                frsRepository.save(person);
                return person;
            } else {
                throw new IOException("Unexpected code " + okresponse);
            }

        } catch (IOException e) {
            throw new ValidationException("Error!");
        }

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
                    .url("http://localhost:5000/lookup")
                    .post(RequestBody.create(request.toJsonString(), JSON))
                    .build();

            Response okresponse = client.newCall(okrequest).execute();
            if (okresponse.isSuccessful()) {
                ObjectMapper mapper = new ObjectMapper();
                OkResponse resp = mapper.readValue(Objects.requireNonNull(okresponse.body()).string(), OkResponse.class);
                RegisteredPerson re =  frsRepository.findOneByPidAndActiveTrue(resp.id);
                return re;
            } else {
                throw new IOException("Unexpected code " + okresponse);
            }

        } catch (IOException e) {
            logger.error("Error calling api", e);
            throw new ValidationException("Error!");
        }
    }
}
