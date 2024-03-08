package cz.utb.fai.model.dto;

import java.util.Collections;
import java.util.List;

public class StartupValuationRes {
    private Double rate;
    private List<String> explanation;

    public StartupValuationRes() {}

    public StartupValuationRes(Double rate, List<String> explanation) {
        this.rate = rate;
        this.explanation = explanation;
    }

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