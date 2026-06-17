package cn.zswltech.mithras.creditreport.service;

import lombok.Data;

import java.util.List;

@Data
public class CreditReportProjectSnapshot {

    private Long projId;

    private String projIdDataType;

    private String projCode;

    private String projName;

    private List<Long> clientIds;
}
