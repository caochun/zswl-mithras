package cn.zswltech.mithras.service.mapper.model.nettingRefund;

import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.plugin.IncludeNull;
import cn.zswltech.mithras.service.util.LongUtil;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 轧差退款表
 * netting_refund
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "netting_refund")
@Data
public class NettingRefund extends BaseModel implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 收款明细id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 银行流水记录的ID
     */
    @TableField(value = "finance_flow_id")
    private Long financeFlowId;


    /**
     * 银行流水编号,由于历史原因,不指向finance_flow_record的该字段,而是billno
     */
    @TableField(value = "bank_detail_no")
    private String bankDetailNo;


    /**
     * 轧差退款金额
     */
    @TableField("netting_amount")
    private Long nettingAmount;

}