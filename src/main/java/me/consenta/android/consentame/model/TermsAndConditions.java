
package me.consenta.android.consentame.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.apache.commons.lang.builder.ToStringBuilder;

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

    public static final int ID = -1;

    private final static long serialVersionUID = -5558217988778676762L;

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

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("version", version).append("url", url).append("title", title).append("mandatory", mandatory).toString();
    }

}
