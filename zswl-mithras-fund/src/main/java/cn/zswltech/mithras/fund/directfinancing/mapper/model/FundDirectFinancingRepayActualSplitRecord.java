package cn.zswltech.mithras.fund.directfinancing.mapper.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2025/3/30
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("fund_direct_financing_repay_actual_split_record")
public class FundDirectFinancingRepayActualSplitRecord extends BaseModelWithLogicDelete {
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
     * 现金流类型
     */
    @TableField(value = "cash_flow_item")
    private String cashFlowItem;

    /**
     * 核销金额
     */
    @TableField(value = "write_off_amount")
    private Long writeOffAmount;
}
