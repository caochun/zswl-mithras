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
public class DashboardProjectStagePaymentResult extends DashboardProjectStageContractResult {
    private String paymentCode;
    private String paymentStatus;
    private String paymentProcessStatus;
    private Long applyPayAmount;
    private LocalDate applyPayDate;
}
