
package me.consenta.android.consentame.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "data_processor",
    "version",
    "description",
    "internal_id",
    "long_description",
    "dataset",
    "mandatory"
})
public class Purpose implements Serializable
{

    /* Minimal object, returned by api/userconsent/<ucid> - used for consent review/update */
    @JsonProperty("id")
    private int id;
    @JsonProperty("description")
    private String description;
    @JsonProperty("internal_id")
    private String internalId;

    /* Full object, returned when reading a Consent */
    @JsonInclude(Include.NON_ABSENT)
    @JsonProperty("data_processor")
    private List<DataController> dataController = null;
    @JsonInclude(Include.NON_ABSENT)
    @JsonProperty("version")
    private int version = -1;
    @JsonInclude(Include.NON_ABSENT)
    @JsonProperty("long_description")
    private String longDescription = null;
    @JsonInclude(Include.NON_ABSENT)
    @JsonProperty("dataset")
    private String dataset = null;
    @JsonInclude(Include.NON_ABSENT)
    @JsonProperty("mandatory")
    private boolean mandatory;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<>();

    /**
     * No args constructor for use in serialization
     * 
     */
    Purpose() {
    }

    /**
     * Create a minimal instance of {@link Purpose}
     *
     * @param id the ID of this purpose, which uniquely identifies each Purpose
     * @param description a brief description of the Purpose
     * @param internalId a String that can be used to identify the Purpose internally
     *                   (set when creating a new Consent)
     */
    public Purpose(int id, String description, String internalId) {
        this.id = id;
        this.description = description;
        this.internalId = internalId;
    }

    /* Minimal object */
    @JsonProperty("id")
    public int getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(int id) {
        this.id = id;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("internal_id")
    public String getInternalId() {
        return internalId;
    }

    @JsonProperty("internal_id")
    public void setInternalId(String internalId) {
        this.internalId = internalId;
    }

    public boolean isFullObject() {
        return longDescription != null &&
                dataset != null &&
                version > 0;
    }

    /* Full object */
    @JsonProperty("data_processor")
    public List<DataController> getDataControllers() {
        return dataController;
    }

    @JsonProperty("data_processor")
    public void setDataController(List<DataController> dataController) {
        this.dataController = dataController;
    }

    @JsonProperty("version")
    public int getVersion() {
        return version;
    }

    @JsonProperty("version")
    public void setVersion(int version) {
        this.version = version;
    }

    @JsonProperty("long_description")
    public String getLongDescription() {
        return longDescription;
    }

    @JsonProperty("long_description")
    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    @JsonProperty("dataset")
    public String getDataset() {
        return dataset;
    }

    @JsonProperty("dataset")
    public void setDataset(String dataset) {
        this.dataset = dataset;
    }

    @JsonProperty("mandatory")
    public boolean isMandatory() {
        return mandatory;
    }

    @JsonProperty("mandatory")
    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
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
        return new ToStringBuilder(this).append("dataController", dataController).append("version", version).append("description", description).append("internalId", internalId).append("longDescription", longDescription).append("dataset", dataset).append("mandatory", mandatory).append("additionalProperties", additionalProperties).toString();
    }

}
