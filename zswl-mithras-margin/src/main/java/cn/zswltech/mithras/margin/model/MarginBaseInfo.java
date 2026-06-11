package cn.zswltech.mithras.margin.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 保证金明细表
 * @TableName margin_base_info
 */
@TableName(value ="margin_base_info")
@Data
public class MarginBaseInfo extends BaseModel implements Serializable {
    /**
     * 保证金id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 编号
     */
    private String marginCode;

    /**
     * 合同id
     */
    private Long contractId;

    /**
     * 合同编号
     */
    private String contractCode;

    /**
     * 客户id
     */
    private Long clientId;

    /**
     * 收款日期
     */
    private LocalDate collectionDate;

    /**
     * 保证金金额
     */
    private Long collectionAmount;

    /**
     * 已退金额
     */
    private Long backAmount;

    /**
     * 已抵扣金额
     */
    private Long deductAmount;

    /**
     * 计划收款金额
     */
    private Long planMarginAmount;

    /**
     * 计划收款日期
     */
    private LocalDate planMarginDate;

    /**
     * 合同是否结清
     */
    private Integer contractIsSettle;

    /**
     * 累加应收金额
     */
    private Long totalReceivableAmount;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}