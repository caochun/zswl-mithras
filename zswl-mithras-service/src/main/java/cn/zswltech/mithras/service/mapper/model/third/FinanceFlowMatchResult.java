package cn.zswltech.mithras.service.mapper.model.third;

import cn.zswltech.mithras.common.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 自动核销预核销记录表
 *
 * @author bigbear
 * @TableName finance_flow_match_result
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName(value = "finance_flow_match_result")
public class FinanceFlowMatchResult extends BaseModelWithLogicDelete implements Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联tab主表ID
     */
    @TableField(value = "main_id")
    private Long mainId;

    /**
     * 业务模块 WriteOffBusinessModelEnum#name
     */
    @TableField(value = "business_model")
    private String businessModel;

    /**
     * 银行流水ID
     */
    @TableField(value = "finance_flow_id_list")
    private String financeFlowIdList;

    /**
     * 银行流水编号
     */
    @TableField(value = "bank_flow_no_list")
    private String bankFlowNoList;

    /**
     * 源表数据ID，对应双端的应付应收表ID
     */
    @TableField(value = "source_id")
    private Long sourceId;

    /**
     * 客户ID
     */
    @TableField(value = "client_id")
    private Long clientId;

    /**
     * 源数据名称，资金端-机构名称；项目端-合同编号
     */
    @TableField(value = "source_business_code")
    private String sourceBusinessCode;

    /**
     * 现金流项目，对应各自领域的枚举
     */
    @TableField(value = "cash_flow_item")
    private String cashFlowItem;

    /**
     * 现金流编号，对应各自领域的编号
     */
    @TableField(value = "cash_flow_code")
    private String cashFlowCode;

    /**
     * 应付/应收时间
     */
    @TableField(value = "should_write_off_time")
    private LocalDate shouldWriteOffTime;

    /**
     * 应付/应收金额，毫厘
     */
    @TableField(value = "should_write_off_amount")
    private Long shouldWriteOffAmount;

    /**
     * 未付/未收金额，毫厘
     */
    @TableField(value = "no_write_off_amount")
    private Long noWriteOffAmount;

    /**
     * 本次核销金额，毫厘
     */
    @TableField(value = "this_write_off_amount")
    private Long thisWriteOffAmount;

    /**
     * 是否是系统生成
     */
    @TableField(value = "is_system_generate")
    private Integer isSystemGenerate;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}