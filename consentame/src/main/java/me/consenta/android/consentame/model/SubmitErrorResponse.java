package me.consenta.android.consentame.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "error",
        "code"
})
public class SubmitErrorResponse implements Serializable
{

    @JsonProperty("error")
    private String error;
    @JsonProperty("code")
    private List<Integer> code = null;
    private final static long serialVersionUID = -2064843755389305167L;

    /**
     * No args constructor for use in serialization
     *
     */
    public SubmitErrorResponse() {
    }

    @JsonProperty("error")
    public String getErrorMessage() {
        return error;
    }

    @JsonProperty("error")
    public void setErrorMessage(String error) {
        this.error = error;
    }

    @JsonProperty("code")
    public List<Integer> getCodes() {
        return code;
    }

    @JsonProperty("code")
    public void setCodes(List<Integer> code) {
        this.code = code;
    }

}