package cn.zswltech.mithras.service.mapper.model.fund.receiptrepay;

import cn.zswltech.mithras.common.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author zhaozhengkang
 * @description 还款计划
 * @date 2023-02-20
 */
@Data
public class FundReceiptRepayPlan extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 收付款id
     */
    @TableField("receipt_repay_id")
    private Long receiptRepayId;

    /**
     * batch_id
     */
    @TableField("batch_id")
    private Long batchId;

    @TableField("repay_year")
    private Integer repayYear;

    @TableField("repay_month")
    private Integer repayMonth;

//    /**
//     * 融资机构名称
//     */
//    @TableField("financing_org_name")
//    private String financingOrgName;

    /**
     * 付款编号
     */
    @TableField("receipt_repay_code")
    private String receiptRepayCode;

    /**
     * 资金收付款版本
     */
    @TableField("receipt_repay_version")
    private String receiptRepayVersion;

    /**
     * 融资金额（元）
     */
    @TableField("financing_amount")
    private Long financingAmount;

//    /**
//     * 融资编号
//     */
//    @TableField("financing_code")
//    private String financingCode;

    /**
     * 累计已还本金（元）
     */
    @TableField("paid_principal")
    private Long paidPrincipal;

    /**
     * 累计已还利息（元）
     */
    @TableField("paid_interest")
    private Long paidInterest;

    /**
     * 配套项目
     */
    @TableField("supporting_project_json")
    private String supportingProjectJson;

    /**
     * 质押合同编号
     */
    @TableField("pledge_contrac_code_json")
    private String pledgeContracCodeJson;

    /**
     * 借款日期
     */
    @TableField("borrowing_date")
    private LocalDate borrowingDate;

    /**
     * 到期日期
     */
    @TableField("expiration_date")
    private LocalDate expirationDate;

    /**
     * 本月计划还款合计（元）
     */
    @TableField("planed_repay_amount")
    private Long planedRepayAmount;

    /**
     * 本月计划还款本金（元）
     */
    @TableField("planed_repay_principal")
    private Long planedRepayPrincipal;

    /**
     * 本月计划还款利息（元）
     */
    @TableField("planed_repay_interest")
    private Long planedRepayInterest;

    /**
     * 计划还本日
     */
    @TableField("planed_repay_principle_date")
    private LocalDate planedRepayPrincipleDate;

    /**
     * 计划还息日
     */
    @TableField("planed_repay_interest_date")
    private LocalDate planedRepayInterestDate;

}
