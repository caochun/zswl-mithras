package cn.zswltech.mithras.service.service.bo;

import lombok.Data;

/**
 * @author dingqi
 * @date 2022/8/2
 * @description
 */
@Data
public class ProjReviewCashFlowPlanBO {
    private Long id;

    /**
     * 日期
     */
    private String date;

    /**
     * 期项
     */
    private Integer phase;

    /**
     * 现金流金额，单位：毫厘
     */
    private String cashFlowAmount;

    /**
     * 租金，单位：毫厘
     */
    private String rent;

    /**
     * 本金，单位：毫厘
     */
    private String principal;

    /**
     * 利息，单位：毫厘
     */
    private String interest;

    /**
     * 剩余本金，单位：毫厘
     */
    private String remainingPrincipal;
}
