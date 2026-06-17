package cn.zswltech.mithras.riskcontrol.application.port;

import lombok.Data;

@Data
public class RiskControlConcentrationClientFact {

    private Long clientId;

    private Long groupId;

    private Integer related;

    private String riskControlIndustryClassify;

    private String province;

    private Long sponsorId;
}
