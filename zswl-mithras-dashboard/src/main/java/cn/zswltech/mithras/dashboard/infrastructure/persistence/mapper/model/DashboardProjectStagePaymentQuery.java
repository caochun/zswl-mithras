package cn.zswltech.mithras.dashboard.infrastructure.persistence.mapper.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2024/6/17
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DashboardProjectStagePaymentQuery extends CommonAuthQuery {
    private Long clientId;
    private String contractCode;
    private String paymentStatus;
    private String paymentProcessStatus;
    private LocalDate applyPayDateFrom;
    private LocalDate applyPayDateTo;
    private Long bizDeptId;
    private Long projSponsorUserId;
    // 1-付款阶段，2-投放阶段
    private Integer viewType;
    /**
     * "可见范围：全部项目，我的项目 - all ,own"
     */
    private String permissionType;
}
