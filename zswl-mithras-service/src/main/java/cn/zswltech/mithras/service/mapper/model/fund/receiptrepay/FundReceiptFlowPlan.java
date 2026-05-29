package cn.zswltech.mithras.service.mapper.model.fund.receiptrepay;

import cn.zswltech.mithras.service.enums.capital.FinanceCashFlowItemEnum;
import cn.zswltech.mithras.service.enums.fund.receiptrepay.CashFlowState;
import cn.zswltech.mithras.common.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2024/8/19
 * @description 资金端计划现金流
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("fund_receipt_flow_plan")
public class FundReceiptFlowPlan extends BaseModelWithLogicDelete {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    @TableField(value = "id")
    private Long id;

    /**
     * 收付款id
     */
    @TableField(value = "receipt_repay_id")
    private Long receiptRepayId;

    /**
     * 现金流期项
     */
    @TableField(value = "cash_flow_phase")
    private Integer cashFlowPhase;

    /**
     * 现金流编号
     */
    @TableField(value = "cash_flow_code")
    private String cashFlowCode;

    /**
     * 现金流类型 {@link FinanceCashFlowItemEnum#name()}
     */
    @TableField(value = "cash_flow_item")
    private String cashFlowItem;

    /**
     * 现金流日期
     */
    @TableField(value = "cash_flow_date")
    private LocalDate cashFlowDate;

    /**
     * 总金额
     */
    @TableField(value = "total_amount")
    private Long totalAmount;

    /**
     * 本金金额
     */
    @TableField(value = "principal_amount")
    private Long principalAmount;

    /**
     * 利息金额
     */
    @TableField(value = "interest_amount")
    private Long interestAmount;

    /**
     * 核销状态 {@link CashFlowState#name()}
     */
    @TableField(value = "write_off_state")
    private String writeOffState;

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;
}
