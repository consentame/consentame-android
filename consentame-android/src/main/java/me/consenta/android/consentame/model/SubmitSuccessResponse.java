package me.consenta.android.consentame.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;

/**
 * This class is used for JSON mapping with fasterxml
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "user_consent_id"
})
public class SubmitSuccessResponse implements Serializable
{

    @JsonProperty("user_consent_id")
    private String userConsentId;
    private final static long serialVersionUID = -4231491057136415192L;

    /**
     * No args constructor for use in serialization
     *
     */
    public SubmitSuccessResponse() {
    }

    @JsonProperty("user_consent_id")
    public String getUserConsentId() {
        return userConsentId;
    }

    @JsonProperty("user_consent_id")
    public void setUserConsentId(String userConsentId) {
        this.userConsentId = userConsentId;
    }

}