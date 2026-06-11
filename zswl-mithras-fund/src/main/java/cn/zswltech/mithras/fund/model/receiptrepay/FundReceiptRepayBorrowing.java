package cn.zswltech.mithras.fund.model.receiptrepay;

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
 * @description 借款流入
 * @date 2023-02-20
 * @deprecated 本表会继续保留使用，优化后数据会同步至FundReceiptFlowPlan，后续尽可能使用新表
 */
@Deprecated
@Data
public class FundReceiptRepayBorrowing extends BaseModel implements Serializable, IEntity {

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
     * 现金流编号
     */
    @TableField("cash_flow_code")
    private String cashFlowCode;

    /**
     * 期项
     */
    @TableField("term")
    private Integer term;

    /**
     * 本金
     */
    @TableField("principal")
    private Long principal;

    /**
     * 核销状态
     */
    @TableField("write_off_state")
    private String writeOffState;

    /**
     * 实际贷款日期
     */
    @TableField("actual_loan_date")
    private LocalDate actualLoanDate;

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
