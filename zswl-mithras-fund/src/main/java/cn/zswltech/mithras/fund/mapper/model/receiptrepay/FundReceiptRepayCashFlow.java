package cn.zswltech.mithras.fund.mapper.model.receiptrepay;

import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @author zhaozhengkang
 * @description 本金利息一览表
 * @date 2023-02-20
 * @deprecated 本表会继续保留使用，优化后数据会同步至FundReceiptFlowPlan，后续尽可能使用新表
 */
@Deprecated
@Data
public class FundReceiptRepayCashFlow extends BaseModel implements Serializable, IEntity {

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
     * 融资id
     */
    @TableField("financing_id")
    private Long financingId;

    /**
     * 现金流id
     */
    @TableField("cash_flow_id")
    private Long cashFlowId;
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
     * 核销状态
     */
    @TableField("write_off_state")
    private String writeOffState;

    /**
     * 备注
     */
    @TableField(value = "remark", updateStrategy = FieldStrategy.IGNORED)
    private String remark;

    /**
     * 审批通过时间
     */
    @TableField("approval_pass_date")
    private LocalDate approvalPassDate;

    @Override
    public void setMainId(Long id) {
        this.setReceiptRepayId(id);
    }

    @Override
    public Long getMainId() {
        return this.getReceiptRepayId();
    }

}
