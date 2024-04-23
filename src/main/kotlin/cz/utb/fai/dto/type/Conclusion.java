package cz.utb.fai.dto.type;

import java.util.ArrayList;
import java.util.List;

public class Conclusion {
    private Double rate = 0.0;
    private final List<String> explanations = new ArrayList<>();

    public Conclusion() {}

    public List<String> getExplanations() {
        return explanations;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    // Method to append an explanation to the existing list
    public void appendExplanation(String newExplanation) {
        this.explanations.add(newExplanation);
    }

    // Method to append a rate
    public void appendRate(Double newRate) { this.rate += newRate; }
}
