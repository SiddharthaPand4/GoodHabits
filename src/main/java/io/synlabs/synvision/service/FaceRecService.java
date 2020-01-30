package io.synlabs.synvision.service;

import io.synlabs.synvision.ex.ValidationException;
import io.synlabs.synvision.views.frs.FRSLookupRequest;
import io.synlabs.synvision.views.frs.FRSLookupResponse;
import io.synlabs.synvision.views.frs.FRSRegisterRequest;
import io.synlabs.synvision.views.frs.FRSRegisterResponse;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;


@Service
public class FaceRecService {

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    public FRSRegisterResponse register(FRSRegisterRequest request) {

        OkHttpClient client = new OkHttpClient();
        try {
            Request okrequest = new Request.Builder()
                    .header("Authorization", "your token")
                    .url("http://localhost:5000/register")
                    .post(RequestBody.create(request.toJsonString(), JSON))
                    .build();

            Response okresponse = client.newCall(okrequest).execute();
            if (!okresponse.isSuccessful()) throw new IOException("Unexpected code " + okresponse);
            return FRSRegisterResponse.fromJson(Objects.requireNonNull(okresponse.body()).string());

        } catch (IOException e) {
            throw new ValidationException("Error!");
        }

    }

    public FRSLookupResponse lookup(FRSLookupRequest request) {
        OkHttpClient client = new OkHttpClient();
        try {
            Request okrequest = new Request.Builder()
                    .header("Authorization", "your token")
                    .url("http://localhost:5000/lookup")
                    .post(RequestBody.create(request.toJsonString(), JSON))
                    .build();

            Response okresponse = client.newCall(okrequest).execute();
            if (!okresponse.isSuccessful()) throw new IOException("Unexpected code " + okresponse);
            return FRSLookupResponse.fromJson(Objects.requireNonNull(okresponse.body()).string());

        } catch (IOException e) {
            throw new ValidationException("Error!");
        }
    }
}
