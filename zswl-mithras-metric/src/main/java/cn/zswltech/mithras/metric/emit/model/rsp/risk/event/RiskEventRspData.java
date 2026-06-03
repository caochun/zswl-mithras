package cn.zswltech.mithras.metric.emit.model.rsp.risk.event;

import lombok.Data;



@Data
public class RiskEventRspData {
    private String eventName;
    private String eventCode;
    private String error;
}
