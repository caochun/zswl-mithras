package cn.zswltech.mithras.dashboard.infrastructure.persistence.mapper.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2024/6/25
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DashboardProjectPayNoSettleResult extends DashboardProjectBasicResult {
    private Long projReviewId;
    private String projName;
    private Long clientId;
    private String clientName;
    private Long contractId;
    private String contractCode;
    private LocalDate minPayDate;
    private Long totalPayAmount;
    private Long planRentAmount;
    private Long planFirstRentAmount;
    private Long actualFirstRentAmount;
    private Long planPrincipalAmount;
    private Long planInterestAmount;
    private Long actualRentAmount;
    private Long actualPrincipalAmount;
    private Long actualInterestAmount;
    private String regionalProjectClassify;
    private LocalDate actualLeaseDate;
    private Long earnestMoney;
    private Integer duration;
    private Integer estimateIrr;
    private Integer isRelated;
    private LocalDate deadline;
}
