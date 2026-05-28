package cn.zswltech.mithras.service.mapper.model.projlifecycle;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author yibin
 */
@Data
public class ProjLifecycleListDO {
    private Long establishId;
    private Long reviewId;
    private Long clientId;
    private String projName;
    private Long applyCreditAmount;
    private Long establishApplyCreditAmount;
    private String bizType;
    private Long projSponsorUserId;
    private Long bizDeptId;
    private String assignees;
    private String flowType;
    private LocalDateTime startTime;
    private LocalDateTime createTime;
    private Integer settled;
    private String establishStatus;
    private String establishProcessStatus;
    private String reviewStatus;
    private String reviewProcessStatus;

    //
    private Long contractAmountApplied;
    private Long contractAmountEffected;
    private Long paymentAmountApplied;
    private Long paymentAmountEffected;
    private Long paymentAmountWrittenOff;
    private Long remainingPrincipal;
    //
    private String projLifecycleStatus;
}
