package cz.utb.fai.dto;

import cz.utb.fai.dto.type.Evidence;

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