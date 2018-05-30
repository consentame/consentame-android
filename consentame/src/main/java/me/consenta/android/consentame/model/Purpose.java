
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

    @JsonProperty("id")
    private int id;
    @JsonProperty("data_processor")
    private List<DataController> dataController = null;
    @JsonProperty("version")
    private int version;
    @JsonProperty("description")
    private String description;
    @JsonProperty("internal_id")
    private String internalId;
    @JsonProperty("long_description")
    private String longDescription;
    @JsonProperty("dataset")
    private String dataset;
    @JsonProperty("mandatory")
    private boolean mandatory;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
    private final static long serialVersionUID = -1714245501197493152L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Purpose() {
    }

    @JsonProperty("data_processor")
    public List<DataController> getDataControllers() {
        return dataController;
    }

    @JsonProperty("data_processor")
    public void setDataController(List<DataController> dataController) {
        this.dataController = dataController;
    }

    @JsonProperty("id")
    public int getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(int id) {
        this.id = id;
    }

    @JsonProperty("version")
    public int getVersion() {
        return version;
    }

    @JsonProperty("version")
    public void setVersion(int version) {
        this.version = version;
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
