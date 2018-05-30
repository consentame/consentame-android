
package me.consenta.android.consentame.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "version",
    "name",
    "contact",
    "address",
    "email",
    "VAT",
    "on_behalf",
    "privacy_policy",
    "terms_and_conditions"
})
public class DataController implements Serializable
{

    @JsonProperty("version")
    private int version;
    @JsonProperty("name")
    private String name;
    @JsonProperty("contact")
    private String contact;
    @JsonProperty("address")
    private String address;
    @JsonProperty("email")
    private String email;
    @JsonProperty("VAT")
    private String vAT;
    @JsonProperty("on_behalf")
    private boolean onBehalf;
    @JsonProperty("privacy_policy")
    private String privacyPolicy;
    @JsonProperty("terms_and_conditions")
    private String termsAndConditions;

    private final static long serialVersionUID = 6397837644252632483L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public DataController() {
    }

    @JsonProperty("version")
    public int getVersion() {
        return version;
    }

    @JsonProperty("version")
    public void setVersion(int version) {
        this.version = version;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("contact")
    public String getContact() {
        return contact;
    }

    @JsonProperty("contact")
    public void setContact(String contact) {
        this.contact = contact;
    }

    @JsonProperty("address")
    public String getAddress() {
        return address;
    }

    @JsonProperty("address")
    public void setAddress(String address) {
        this.address = address;
    }

    @JsonProperty("email")
    public String getEmail() {
        return email;
    }

    @JsonProperty("email")
    public void setEmail(String email) {
        this.email = email;
    }

    @JsonProperty("VAT")
    public String getVAT() {
        return vAT;
    }

    @JsonProperty("VAT")
    public void setVAT(String vAT) {
        this.vAT = vAT;
    }

    @JsonProperty("on_behalf")
    public boolean isOnBehalf() {
        return onBehalf;
    }

    @JsonProperty("on_behalf")
    public void setOnBehalf(boolean onBehalf) {
        this.onBehalf = onBehalf;
    }

    @JsonProperty("privacy_policy")
    public String getPrivacyPolicy() {
        return privacyPolicy;
    }

    @JsonProperty("privacy_policy")
    public void setPrivacyPolicy(String privacyPolicy) {
        this.privacyPolicy = privacyPolicy;
    }

    @JsonProperty("terms_and_conditions")
    public String getTermsAndConditions() {
        return termsAndConditions;
    }

    @JsonProperty("terms_and_conditions")
    public void setTermsAndConditions(String termsAndConditions) {
        this.termsAndConditions = termsAndConditions;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("version", version).append("name", name).append("contact", contact).append("address", address).append("email", email).append("vAT", vAT).append("onBehalf", onBehalf).append("privacyPolicy", privacyPolicy).append("termsAndConditions", termsAndConditions).toString();
    }
}
