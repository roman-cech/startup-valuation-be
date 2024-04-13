package cz.utb.fai.dto.type;

public class Hypothesis {
    private String description;

    public Hypothesis() {
    }

    public Hypothesis(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
