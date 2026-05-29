package cn.zswltech.mithras.service.fund.direct.entity;

import cn.zswltech.mithras.common.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @description 直接融资-实际还款表
 * @author zhaozhengkang
 * @date 2023-06-17
 */
@Data
public class FundDirectFinancingRepayActual extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 融资id
    */
    @TableField("financing_id")
    private Long financingId;

    /**
    * 现金流编号
    */
    @TableField("cash_flow_code")
    private String cashFlowCode;

    /**
     * 还款日期
     */
    @TableField("repay_date")
    private LocalDate repayDate;

    /**
    * 还款期项
    */
    @TableField("phase")
    private Integer phase;

    /**
    * 本金
    */
    @TableField("principle_amount")
    private Long principleAmount;

    /**
    * 利息
    */
    @TableField("interest_amount")
    private Long interestAmount;

    /**
    * 应还总额
    */
    @TableField("repay_amount")
    private Long repayAmount;

    /**
    * 剩余未还本金
    */
    @TableField("remaining_principle_amount")
    private Long remainingPrincipleAmount;

    /**
     * 预付差额
     */
    @TableField("pre_pay_difference")
    private Long prePayDifference;

    /**
     * 核销状态
     */
    @TableField("write_off_status")
    private String writeOffStatus;

    /**
     * 确认状态是否已确认
     */
    @TableField(value = "is_confirmed")
    private Integer isConfirmed;

    /**
     * 还款状态是否已确认
     */
    @TableField(value = "is_paid")
    private Integer isPaid;

}
