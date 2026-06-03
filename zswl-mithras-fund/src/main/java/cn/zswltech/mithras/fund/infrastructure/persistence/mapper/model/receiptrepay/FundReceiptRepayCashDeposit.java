package cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.receiptrepay;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.mapper.tag.IEntity;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhaozhengkang
 * @description 保证金明细
 * @date 2023-02-20
 * @deprecated 本表会继续保留使用，优化后数据会同步至FundReceiptFlowPlan，后续尽可能使用新表
 */
@Deprecated
@Data
public class FundReceiptRepayCashDeposit extends BaseModel implements Serializable, IEntity {

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
    @TableField("deposit_cash_flow_type")
    private String depositCashFlowType;

    /**
     * 金额
     */
    @TableField("amount")
    private Long amount;

    /**
     * 核销状态
     */
    @TableField("write_off_state")
    private String writeOffState;

    /**
     * 本月支付金额
     */
    @TableField("paid_amount")
    private Long paidAmount;

    /**
     * 本月收入金额
     */
    @TableField("receipt_amount")
    private Long receiptAmount;

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
