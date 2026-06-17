package cn.zswltech.mithras.riskcontrol.application.port;

import java.util.Date;

public class RiskControlWarnWorkflowInstance {

    private final String businessKey;
    private final Date startTime;
    private final Date endTime;

    public RiskControlWarnWorkflowInstance(String businessKey, Date startTime, Date endTime) {
        this.businessKey = businessKey;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }
}
