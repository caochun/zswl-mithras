package cn.zswltech.mithras.creditreport.service;

import lombok.Data;

@Data
public class CreditReportClientSnapshot {

    private Long clientId;

    private String clientName;

    private String cscCode;

    private String zhongZhengCode;
}
