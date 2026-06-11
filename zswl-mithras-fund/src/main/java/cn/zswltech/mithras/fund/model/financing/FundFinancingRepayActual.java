package cn.zswltech.mithras.fund.model.financing;

import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("fund_financing_repay_actual")
public class FundFinancingRepayActual extends BaseModel implements IEntity {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    @TableField("id")
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

    @Override
    public void setMainId(Long id) {
        this.financingId = id;
    }

    @Override
    public Long getMainId() {
        return this.financingId;
    }
}