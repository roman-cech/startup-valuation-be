package cz.utb.fai.model.dto;

import cz.utb.fai.model.type.Evidence;

import java.util.List;

public class StartupValuationReq {
    private List<Evidence> evidences;

    public StartupValuationReq() {}

    public List<Evidence> getEvidences() {
        return evidences;
    }

    public void setEvidences(List<Evidence> evidences) {
        this.evidences = evidences;
    }
}