package cz.utb.fai.dto.type;

public class Conclusion {
    private String description;
    private Double rate;

    public Conclusion(String description, Double rate) {
        this.description = description;
        this.rate = rate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }
}
