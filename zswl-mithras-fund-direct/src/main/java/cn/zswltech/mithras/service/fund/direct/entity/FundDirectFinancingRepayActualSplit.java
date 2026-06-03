package cn.zswltech.mithras.service.fund.direct.entity;

import cn.zswltech.mithras.service.mapper.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2025/3/28
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("fund_direct_financing_repay_actual_split")
public class FundDirectFinancingRepayActualSplit extends BaseModelWithLogicDelete {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    @TableField(value = "id")
    private Long id;

    /**
     * 融资id
     */
    @TableField(value = "financing_id")
    private Long financingId;

    /**
     * 产品明细id
     */
    @TableField(value = "product_detail_id")
    private Long productDetailId;

    /**
     * 实际还款计划id
     */
    @TableField(value = "repay_actual_id")
    private Long repayActualId;

    /**
     * 现金流编号（父）
     */
    @TableField(value = "cash_flow_code_parent")
    private String cashFlowCodeParent;

    /**
     * 现金流编号
     */
    @TableField(value = "cash_flow_code")
    private String cashFlowCode;

    /**
     * 序号
     */
    @TableField(value = "sequence")
    private Integer sequence;

    /**
     * 还款日期
     */
    @TableField(value = "repay_date")
    private LocalDate repayDate;

    /**
     * 还款期项
     */
    @TableField(value = "phase")
    private Integer phase;

    /**
     * 应还总额
     */
    @TableField(value = "repay_amount")
    private Long repayAmount;

    /**
     * 应还本金
     */
    @TableField(value = "principal_amount")
    private Long principalAmount;

    /**
     * 应还利息
     */
    @TableField(value = "interest_amount")
    private Long interestAmount;

    /**
     * 剩余本金
     */
    @TableField(value = "remaining_principal_amount")
    private Long remainingPrincipalAmount;

    /**
     * 核销状态
     */
    @TableField(value = "write_off_status")
    private String writeOffStatus;

    @Override
    public void reset() {
        super.reset();
        this.id = null;
    }
}
