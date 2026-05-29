package cn.zswltech.mithras.service.mapper.model;
import cn.zswltech.mithras.common.model.BaseModelWithLogicDelete;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 资金流水核销详情关联记录表
 * @author yangxiong
 * @TableName finance_flow_write_off_detail
 */
@TableName(value ="finance_flow_write_off_detail")
@Data
public class FinanceFlowWriteOffDetail extends BaseModelWithLogicDelete implements Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 记录主表枚举{@link cn.zswltech.mithras.service.enums.capital.FinanceFlowDetailTableEnum}
     */
    @TableField(value = "record_main_table")
    private String recordMainTable;

    /**
     * 目标记录ID
     */
    @TableField(value = "main_id")
    private Long mainId;

    /**
     * 银行流水编号,由于历史原因,不指向finance_flow_record的该字段,而是billno
     */
    @TableField(value = "bank_detail_no")
    private String bankDetailNo;

    /**
     * 银行流水记录的ID
     */
    @TableField(value = "finance_flow_id")
    private Long financeFlowId;


    /**
     * 是否删除，0：未删除，1：已删除，默认0
     */
    @TableField(value = "deleted")
    private Integer deleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}