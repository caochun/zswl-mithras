package cn.zswltech.mithras.dto.creditreport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @description 征信报告-信用额度表
 * @author vico
 * @date 2025-11-14
 */
@Data
@ApiModel("征信报告-信用额度表列表-返回体")
public class CreditReportLimitListRSP {

    /**
    * id
    */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
    * 查询编号
    */
    @ApiModelProperty(value = "查询编号")
    private Long creditCode;

    /**
    * 征信报告基本表id
    */
    @ApiModelProperty(value = "征信报告基本表id")
    private Long creditReportId;

    /**
    * 非循环-总额
    */
    @ApiModelProperty(value = "非循环-总额")
    private BigDecimal totalAmount;

    /**
    * 非循环-已用额度
    */
    @ApiModelProperty(value = "非循环-已用额度")
    private BigDecimal usedAmount;

    /**
    * 非循环-剩余可用额度
    */
    @ApiModelProperty(value = "非循环-剩余可用额度")
    private BigDecimal remainingAvailableAmount;

    /**
    * 循环-已用额度
    */
    @ApiModelProperty(value = "循环-已用额度")
    private BigDecimal cycleTotalAmount;

    /**
    * 循环-已用额度
    */
    @ApiModelProperty(value = "循环-已用额度")
    private BigDecimal cycleUsedAmount;

    /**
    * 循环-已用额度
    */
    @ApiModelProperty(value = "循环-已用额度")
    private BigDecimal cycleRemainingAvailableAmount;

    /**
    * 逻辑删除，0-未删除，1-已删除
    */
    @ApiModelProperty(value = "逻辑删除，0-未删除，1-已删除")
    private Integer deleted;

}
