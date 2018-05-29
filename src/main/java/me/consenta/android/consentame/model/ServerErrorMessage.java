package me.consenta.android.consentame.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * To this class are mapped error messages from consenta.me server
 */
@JsonPropertyOrder({
        "error",
        "code"
})
public class ServerErrorMessage extends IllegalArgumentException {

    @JsonProperty("error")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String errorMsg;

    @JsonProperty("code")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String errorCode;

    private ServerErrorMessage() {
    }

    @JsonProperty("error")
    public String getSimpleMessage() {
        return errorMsg;
    }

    @JsonProperty("code")
    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        return "Server said: " + errorCode + " - " + errorMsg;
    }
}
