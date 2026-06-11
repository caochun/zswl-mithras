package cn.zswltech.mithras.riskcontrol.report.gljy;

import lombok.Data;

@Data
public class RiskControlRelatedClientExternal {
    private String creditCode;
    private String name;
    private Integer partyType;
    private String relationDesc;
}
