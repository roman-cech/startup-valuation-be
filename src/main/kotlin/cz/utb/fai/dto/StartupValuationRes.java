package cz.utb.fai.dto;

import java.util.ArrayList;
import java.util.List;

public class StartupValuationRes {
    private Double rate = 0d;
    private List<String> explanation = new ArrayList<>();

    public StartupValuationRes() {}

    public StartupValuationRes(Double rate, List<String> explanation) {
        this.rate = rate;
        this.explanation = explanation;
    }

    // Method to append an explanation to the existing list
    public void appendExplanation(String newExplanation) {
        this.explanation.add(newExplanation);
    }

    // Method to append a rate
    public void appendRate(Double newRate) { this.rate += newRate; }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public List<String> getExplanation() {
        return explanation;
    }

    public void setExplanation(List<String> explanation) {
        this.explanation = explanation;
    }
}