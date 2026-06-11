package cn.zswltech.mithras.fund.mapper.model.financing;

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
@TableName("fund_financing_repay_estimate")
public class FundFinancingRepayEstimate extends BaseModel implements IEntity {
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

    @Override
    public void setMainId(Long id) {
        this.financingId = id;
    }

    @Override
    public Long getMainId() {
        return this.financingId;
    }
}