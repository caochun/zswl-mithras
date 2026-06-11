package cn.zswltech.mithras.fund.mapper.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 直融资金还款记录
 * @author yangxiong
 * @TableName fund_direct_financing_repay_record
 */
@Data
@TableName(value ="fund_direct_financing_repay_record")
@EqualsAndHashCode(callSuper = true)
public class FundDirectFinancingRepayRecord extends BaseModelWithLogicDelete implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 资金主表ID
     */
    @TableField(value = "financing_id")
    private String financingId;

    /**
     * 现金流项目
     */
    @TableField(value = "cash_flow_item")
    private String cashFlowItem;

    /**
     * 期项
     */
    @TableField(value = "phase")
    private Integer phase;

    /**
     * 收款金额，存毫厘
     */
    @TableField(value = "pay_amount")
    private Long payAmount;

    /**
     * 收款日期
     */
    @TableField(value = "pay_date")
    private LocalDateTime payDate;

    /**
     * 收款对应流水ID
     */
    @TableField(value = "finance_flow_id")
    private Long financeFlowId;

    /**
     * 收款对应银行流水号
     */
    @TableField(value = "bank_detail_no")
    private String bankDetailNo;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}