
package me.consenta.android.consentame.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "data_controller",
    "purposes",
    "terms_and_condition",
    "version",
    "title",
    "text",
    "creation",
    "consent_id",
    "policy_url",
    "website_domain"
})
public class Consent implements Serializable
{

    @JsonProperty("data_controller")
    private List<DataController> dataProcessors = null;
    @JsonProperty("purposes")
    private List<Purpose> purposes = null;
    @JsonProperty("terms_and_condition")
    private TermsAndConditions termsAndConditions;
    @JsonProperty("version")
    private int version;
    @JsonProperty("title")
    private String title;
    @JsonProperty("text")
    private String text;
    @JsonProperty("creation")
    private String creation;
    @JsonProperty("consent_id")
    private String consentId;
    @JsonProperty("policy_url")
    private String policyUrl;
    @JsonProperty("website_domain")
    private String websiteDomain;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private final static long serialVersionUID = 324402938814626015L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Consent() {
    }

    @JsonProperty("data_controller")
    public List<DataController> getDataProcessors() {
        return dataProcessors;
    }

    @JsonProperty("data_controller")
    public void setDataProcessors(List<DataController> dataProcessors) {
        this.dataProcessors = dataProcessors;
    }

    @JsonProperty("purposes")
    public List<Purpose> getPurposes() {
        return purposes;
    }

    @JsonProperty("purposes")
    public void setPurposes(List<Purpose> purposes) {
        this.purposes = purposes;
    }

    @JsonProperty("terms_and_condition")
    public TermsAndConditions termsAndConditions() {
        return termsAndConditions;
    }

    @JsonProperty("terms_and_condition")
    public void setTermsAndConditions(TermsAndConditions termsAndConditions) {
        this.termsAndConditions = termsAndConditions;
    }

    @JsonProperty("version")
    public int getVersion() {
        return version;
    }

    @JsonProperty("version")
    public void setVersion(int version) {
        this.version = version;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("text")
    public String getText() {
        return text;
    }

    @JsonProperty("text")
    public void setText(String text) {
        this.text = text;
    }

    @JsonProperty("creation")
    public String getCreation() {
        return creation;
    }

    @JsonProperty("creation")
    public void setCreation(String creation) {
        this.creation = creation;
    }

    @JsonProperty("consent_id")
    public String getConsentId() {
        return consentId;
    }

    @JsonProperty("consent_id")
    public void setConsentId(String consentId) {
        this.consentId = consentId;
    }

    @JsonProperty("policy_url")
    public String getPolicyUrl() {
        return policyUrl;
    }

    @JsonProperty("policy_url")
    public void setPolicyUrl(String policyUrl) {
        this.policyUrl = policyUrl;
    }

    @JsonProperty("website_domain")
    public String getWebsiteDomain() {
        return websiteDomain;
    }

    @JsonProperty("website_domain")
    public void setWebsiteDomain(String websiteDomain) {
        this.websiteDomain = websiteDomain;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("dataProcessors", dataProcessors)
                .append("purposes", purposes)
                .append("termsAndConditions", termsAndConditions)
                .append("version", version)
                .append("title", title)
                .append("text", text)
                .append("creation", creation)
                .append("consentId", consentId)
                .append("policyUrl", policyUrl)
                .append("websiteDomain", websiteDomain)
                .append("additionalProperties", additionalProperties)
                .toString();
    }

}
