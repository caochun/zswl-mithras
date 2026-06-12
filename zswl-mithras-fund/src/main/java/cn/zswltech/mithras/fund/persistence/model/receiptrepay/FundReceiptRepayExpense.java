package cn.zswltech.mithras.fund.persistence.model.receiptrepay;

import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhaozhengkang
 * @description 费用一览表
 * @date 2023-02-20
 * @deprecated 本表会继续保留使用，优化后数据会同步至FundReceiptFlowPlan，后续尽可能使用新表
 */
@Deprecated
@Data
public class FundReceiptRepayExpense extends BaseModel implements Serializable, IEntity {

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

    @TableField("cash_flow_code")
    private String cashFlowCode;

    /**
     * 费用类型
     */
    @TableField("expense_type")
    private String expenseType;
    @TableField("total_amount")
    private Long totalAmount;
    @TableField("total_paid_amount")
    private Long totalPaidAmount;
    @TableField("pay_amount")
    private Long payAmount;

    /**
     * 核销状态
     */
    @TableField("write_off_state")
    private String writeOffState;

    /**
     * 直融关联的费用id
     */
    @TableField("direct_fee_id")
    private Long directFeeId;

    /**
     * 间融关联的费用id
     */
    @TableField("fee_id")
    private Long feeId;

    /**
     * 备注
     */
    @TableField(value = "remark", updateStrategy = FieldStrategy.IGNORED)
    private String remark;

    @Override
    public void setMainId(Long id) {
        this.setReceiptRepayId(id);
    }

    @Override
    public Long getMainId() {
        return this.getReceiptRepayId();
    }

}
