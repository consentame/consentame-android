
package me.consenta.android.consentame.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "version",
    "url",
    "title",
    "mandatory"
})
public class TermsAndConditions implements Serializable
{

    @JsonProperty("version")
    private int version;
    @JsonProperty("url")
    private String url;
    @JsonProperty("title")
    private String title;
    @JsonProperty("mandatory")
    private boolean mandatory;
    @JsonProperty("restrictive")
    private String restrictiveText;

    @JsonIgnore
    private boolean checked;

    public static final int ID = -1;
    public static final int RESTRICTIVE_ID = -2;

    /**
     * No args constructor for use in serialization
     * 
     */
    public TermsAndConditions() {
    }

    @JsonProperty("version")
    public int getVersion() {
        return version;
    }

    @JsonProperty("version")
    public void setVersion(int version) {
        this.version = version;
    }

    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("mandatory")
    public boolean isMandatory() {
        return mandatory;
    }

    @JsonProperty("mandatory")
    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    @JsonProperty("restrictive")
    public String getRestrictiveText() {
        return restrictiveText;
    }

    @JsonProperty("restrictive")
    public void setRestrictiveText(String restrictive) {
        this.restrictiveText = restrictive;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isChecked() {
        return checked;
    }

    @Override
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "TermsAndConditions (" + (checked ? "checked" : "unchecked") + ")";
        }
    }
}
