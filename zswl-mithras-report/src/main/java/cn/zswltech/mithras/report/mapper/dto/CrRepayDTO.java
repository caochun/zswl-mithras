package cn.zswltech.mithras.report.mapper.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.function.LongBinaryOperator;

/**
 * 还款表dto
 *
 * @author wangchuanhao
 * @date 2023/1/13 11:32 AM
 */
@Data
public class CrRepayDTO {

    /**
     * 还款计划id
     */
    private Long repayPlanId;

    /**
     * 客户名称
     */
    private String clientName;

    /**
     * 实际还款id
     */
    private Long actualRepayId;

    /**
     * 业务标识
     */
    private String businessKey;

    /**
     * 借据编号
     */
    private String paymentApplyCode;

    /**
     * 合同ID
     */
    private Long contractId;

    /**
     * 期项
     */
    private Integer phase;

    /**
     * 应收日期
     */
    private LocalDate cashFlowDate;

    /**
     * 宽限期(天)
     */
    private String gracePeriod;

    /**
     * 应收租金
     */
    private Long rent;

    /**
     * 应收本金
     */
    private Long principal;

    /**
     * 收款日期
     */
    private LocalDate payDate;

    /**
     * 实收金额
     */
    private Long collectionAmount;

    /**
     * 实收本金
     */
    private Long collectionPrincipal;

    /**
     * 审批状态
     */
    private String approvalStatus;

    /**
     * 付款申请id
     */
    private Long paymentId;

}
