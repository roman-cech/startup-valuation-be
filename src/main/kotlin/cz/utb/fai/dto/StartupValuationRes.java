package cz.utb.fai.dto;

import java.util.List;

public class StartupValuationRes {
    private final Double rate;
    private final List<String> explanations;

    public StartupValuationRes(Double rate, List<String> explanations) {
        this.rate = rate;
        this.explanations = explanations;
    }

    public Double getRate() {
        return rate;
    }

    public List<String> getExplanations() {
        return explanations;
    }
}