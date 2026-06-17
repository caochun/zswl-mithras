package cn.zswltech.mithras.api.report;

import java.time.LocalDateTime;

public class ReportAssetClassifyClientSnapshot {

    private final Long clientId;
    private final String classifyResult;
    private final LocalDateTime identificationDate;

    public ReportAssetClassifyClientSnapshot(Long clientId, String classifyResult, LocalDateTime identificationDate) {
        this.clientId = clientId;
        this.classifyResult = classifyResult;
        this.identificationDate = identificationDate;
    }

    public Long getClientId() {
        return clientId;
    }

    public String getClassifyResult() {
        return classifyResult;
    }

    public LocalDateTime getIdentificationDate() {
        return identificationDate;
    }
}
