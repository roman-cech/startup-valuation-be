package cz.utb.fai.model.type;

public class Evidence {
    private String description;
    private String value;

    public Evidence() {}
    // Constructor with description and value arguments
    public Evidence(String description, String value) {
        this.description = description;
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}