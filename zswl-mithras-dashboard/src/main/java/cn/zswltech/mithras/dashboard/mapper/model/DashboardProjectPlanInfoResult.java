package cn.zswltech.mithras.dashboard.mapper.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;


@EqualsAndHashCode(callSuper = true)
@Data
public class DashboardProjectPlanInfoResult extends DashboardProjectBasicResult {
    private Long contractId;
    private String bizType;
    private String projName;
    private String contractCode;

    private Long paymentId;
    /**
     * 投放金额
     */
    private Long actualPayAmountLong;

    /**
     * 投放时间
     */
    private LocalDate actualPayDate;

    private Long bizDeptId;
    private String bizDeptName;
    private Long province;
    private Long city;
    private Long district;
    private String provinceDisplay;
    private String cityDisplay;
    private String districtDisplay;
    private Long clientId;
    private String clientName;
    /**
     * 项目主办
     */
    private Long projSponsorUserId;
    private String projSponsorUserName;

    /**
     * 租赁类型，保理类型，转让租赁类型
     */
    private String leaseType;

    private String projClassify;

    /**
     * 合同金额
     */
    private Long contractAmountLong;

    /**
     * irr
     */
    private Integer irrInt;

    /**
     * 手续费
     */
    private Long commissionLong;

    /**
     * 保证金
     */
    private Long earnestMoneyLong;

    /**
     * 合同期限
     */
    private Long contractLimit;

    //private Integer irr;
}
