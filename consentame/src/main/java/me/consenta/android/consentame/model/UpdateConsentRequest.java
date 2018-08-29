package me.consenta.android.consentame.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import me.consenta.android.consentame.utils.Constants;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class UpdateConsentRequest extends UserConsentRequest {

    /**
     * No args constructor for use in serialization
     *
     */
    public UpdateConsentRequest() {
        super();
    }

    public UpdateConsentRequest(String userConsentId, String accessToken, List<UserChoice> choiceList) {
        super(userConsentId, choiceList);
        apiUrl = Constants.HOST + "/api/userconsent/" + userConsentId + "/" + accessToken;
    }

    @Override
    public Request parse() throws JsonProcessingException {
        MediaType json = MediaType.parse("application/json; charset=utf-8");
        ObjectMapper mapper = new ObjectMapper();
        RequestBody body = RequestBody.create(json, mapper.writeValueAsString(this));

        return new Request.Builder()
                .url(apiUrl)
                .post(body)
                .build();
    }
}
