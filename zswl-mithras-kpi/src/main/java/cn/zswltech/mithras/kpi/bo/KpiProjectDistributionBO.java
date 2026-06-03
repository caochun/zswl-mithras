package cn.zswltech.mithras.kpi.bo;

import lombok.Data;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2023/6/16
 * @description
 */
@Data
public class KpiProjectDistributionBO {
    private Long id;
    private String contractCode;
    private Long contractId;
    private Long projectDistributionId;
    private Integer distributionStatus;
    private String approvalStatus;
    private String projName;
    private LocalDate contractStartDate;
    private Long contractBelongDeptId;
    private Long sponsorUserId;
    private String projClassify;
    private String projSource;
    private String projReviewSource;
    private Integer effectYear;
    private Integer effectMonth;
}
