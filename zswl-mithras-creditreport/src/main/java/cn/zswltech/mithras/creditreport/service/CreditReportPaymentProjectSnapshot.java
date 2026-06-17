package cn.zswltech.mithras.creditreport.service;

import lombok.Data;

import java.util.List;

@Data
public class CreditReportPaymentProjectSnapshot {

    private Long contractId;

    private String projCode;

    private String projName;

    private List<Long> clientIds;
}
