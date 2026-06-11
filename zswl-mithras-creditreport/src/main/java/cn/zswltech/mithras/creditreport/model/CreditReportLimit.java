package cn.zswltech.mithras.creditreport.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @description 征信报告-信用额度表
 * @author vico
 * @date 2025-11-14
 */
@Data
public class CreditReportLimit extends BaseModelWithLogicDelete implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 查询编号
    */
    @TableField("credit_code")
    private Long creditCode;

    /**
    * 征信报告基本表id
    */
    @TableField("credit_report_id")
    private Long creditReportId;

    /**
     * 征信报告客户表id
     */
    @TableField("credit_report_client_id")
    private Long creditReportClientId;


    /**
    * 非循环-总额
    */
    @TableField("total_amount")
    private BigDecimal totalAmount;

    /**
    * 非循环-已用额度
    */
    @TableField("used_amount")
    private BigDecimal usedAmount;

    /**
    * 非循环-剩余可用额度
    */
    @TableField("remaining_available_amount")
    private BigDecimal remainingAvailableAmount;

    /**
    * 循环-总额
    */
    @TableField("cycle_total_amount")
    private BigDecimal cycleTotalAmount;

    /**
    * 循环-已用额度
    */
    @TableField("cycle_used_amount")
    private BigDecimal cycleUsedAmount;

    /**
    * 循环-剩余可用额度
    */
    @TableField("cycle_remaining_available_amount")
    private BigDecimal cycleRemainingAvailableAmount;

}
