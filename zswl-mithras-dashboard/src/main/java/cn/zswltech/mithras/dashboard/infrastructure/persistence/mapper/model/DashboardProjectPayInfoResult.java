package cn.zswltech.mithras.dashboard.infrastructure.persistence.mapper.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2024/6/24
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DashboardProjectPayInfoResult extends DashboardProjectBasicResult {
    private Long contractId;
    private String contractCode;
    private LocalDate actualPayDate;
    private Long actualPayAmount;
    private Integer irr;
    private String rateType;
    private Integer duration;
    private Integer lpr;
    private Long contractAmount;
    private Long consultingFee;
    private Long commission;
    private Long earnestMoney;
    private String regionalProjectClassify;
    private Long bizDeptId;
    private Long projSponsorUserId;
    private String projCosponsorUserIds;
    private String bizType;
    // 20241024 新增借据编号，数据维度发生变化
    private Long receiptId;
    private String receiptCode;
    private Long paymentId;
    private String paymentCode;
    private Long paymentConsultingFee;
    private Long paymentCommission;
    private Long paymentEarnestMoney;
}
