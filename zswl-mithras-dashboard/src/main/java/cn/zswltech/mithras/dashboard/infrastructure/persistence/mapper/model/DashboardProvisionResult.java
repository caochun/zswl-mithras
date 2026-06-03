package cn.zswltech.mithras.dashboard.infrastructure.persistence.mapper.model;

import lombok.Data;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2024/6/25
 * @description
 */
@Data
public class DashboardProvisionResult {
    private Long clientId;
    private String clientName;
    private Long contractId;
    private String contractCode;
    private Long contractAmount;
    private Long remainingPrincipal;
    private Long earnestBalance;
    private Long exposure;
    private Long provisionBalance;
    private String projClassify;
    private String bizTypeCode;
    private String leaseTypeCode;
    private Long bizDeptId;
    private String bizDeptName;
    private LocalDate deadline;
    private LocalDate provisionDate;
    private String riskLevel;
    private Integer withdrawalRatio;
}
