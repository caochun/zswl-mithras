package cn.zswltech.mithras.creditreport.mapper.model;

import cn.zswltech.mithras.creditreport.enums.CreditReportRepaymentLiabilityEnum;
import cn.zswltech.mithras.service.mapper.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @description 征信报告-相关还款责任信息概要表
 * @author vico
 * @date 2025-11-14
 */
@Data
public class CreditReportRepaymentResponsibility extends BaseModelWithLogicDelete implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 查询编号
    */
    @TableField("credit_code")
    private String creditCode;

    /**
    * 征信报告基本表id
    */
    @TableField("credit_report_id")
    private Long creditReportId;

    /**
     * 征信报告客户表id
     */
    @TableField("credit_report_client_id")
    private Long creditReportClientId;

    /**
    * 责任类型
     * {@link CreditReportRepaymentLiabilityEnum#name()}
    */
    @TableField("responsibility_type")
    private String responsibilityType;

    /**
    * 被追偿业务-还款责任金额
    */
    @TableField("recoverable_repayment_responsibility_amount")
    private BigDecimal recoverableRepaymentResponsibilityAmount;

    /**
    * 被追偿业务-账户数
    */
    @TableField("recoverable_account_number")
    private BigDecimal recoverableAccountNumber;

    /**
    * 被追偿业务-余额
    */
    @TableField("recoverable_balance")
    private BigDecimal recoverableBalance;

    /**
    * 其他借贷交易-还款责任金额
    */
    @TableField("other_repayment_responsibility_amount")
    private BigDecimal otherRepaymentResponsibilityAmount;

    /**
    * 其他借贷交易-账户数
    */
    @TableField("other_account_number")
    private BigDecimal otherAccountNumber;

    /**
    * 其他借贷交易-余额
    */
    @TableField("other_balance")
    private BigDecimal otherBalance;

    /**
    * 其他借贷交易-关注类余额
    */
    @TableField("other_focus_balance")
    private BigDecimal otherFocusBalance;

    /**
    * 其他借贷交易-不良类余额
    */
    @TableField("other_bad_balance")
    private BigDecimal otherBadBalance;


}
