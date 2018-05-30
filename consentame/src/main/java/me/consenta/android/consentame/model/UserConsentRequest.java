package me.consenta.android.consentame.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import me.consenta.android.consentame.utils.Constants;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "tec",
        "accepted"
})
public class UserConsentRequest implements Serializable
{

    @JsonProperty("tec")
    private boolean tec;
    @JsonProperty("accepted")
    private List<Integer> accepted = null;

    @JsonIgnore
    private String consentId;

    private final static long serialVersionUID = -2886769225057096028L;

    /**
     * No args constructor for use in serialization
     *
     */
    public UserConsentRequest() {
    }

    /**
     * Creates a new {@link UserConsentRequest} for the specified list of {@link UserChoice}
     * @param choiceList the user's choices
     */
    public UserConsentRequest(String consentId, List<UserChoice> choiceList) {
        this.tec = false;

        this.consentId = consentId;

        this.accepted = new LinkedList<>();
        for (UserChoice choice : choiceList) {
            if (choice.getId() == TermsAndConditions.ID) {
                // this is a TermsAndConditions; save status
                tec = choice.getSwitch().isChecked();
            } else {
                // this is a Purpose; append ID to list (only if selected)
                choice.ifSelectedAppendTo(accepted);
            }
        }
    }

    /**
     * Build a {@link Request okhttp3.Request} from this object
     * @return a {@link Request okhttp3.Request} to be sent with a {@link okhttp3.OkHttpClient}
     */
    public Request parse() throws JsonProcessingException {
        MediaType json = MediaType.parse("application/json; charset=utf-8");
        ObjectMapper mapper = new ObjectMapper();
        RequestBody body = RequestBody.create(json, mapper.writeValueAsString(this));


        Request r = new Request.Builder()
                .url(Constants.HOST + "/" + consentId + "/")
                .post(body)
                .build();

        return r;
    }

    @JsonProperty("tec")
    public boolean isTec() {
        return tec;
    }

    @JsonProperty("tec")
    public void setTec(boolean tec) {
        this.tec = tec;
    }

    @JsonProperty("accepted")
    public List<Integer> getAccepted() {
        return accepted;
    }

    @JsonProperty("accepted")
    public void setAccepted(List<Integer> accepted) {
        this.accepted = accepted;
    }

}