package cn.zswltech.mithras.dto.newftp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @description 融资成本定价
 * @author zhaozhengkang
 * @date 2023-05-21
 */
@Data
@ApiModel("融资成本定价编辑-请求体")
public class NewFtpFinancingCostPricingModifyREQ {

    @ApiModelProperty(value = "ftpId")
    private Long mainId;

    /**
     * month
     */
    @ApiModelProperty(value = "month")
    private LocalDate month;

    /**
     * 一年当期均值
     */
    @ApiModelProperty(value = "一年当期均值")
    private Integer oneCurrentAverage;

    @ApiModelProperty(value = "一年当季均值")
    private Integer oneCurrentQuarterAverage;

    /**
     * 一年当年均值
     */
    @ApiModelProperty(value = "一年当年均值")
    private Integer oneAnnualAverage;

    /**
    * 三年当期均值
    */
    @ApiModelProperty(value = "三年当期均值")
    private Integer threeCurrentAverage;

    @ApiModelProperty(value = "一年当季均值")
    private Integer threeCurrentQuarterAverage;

    /**
    * 三年当年均值
    */
    @ApiModelProperty(value = "三年当年均值")
    private Integer threeAnnualAverage;

    /**
     * 五年当期均值
     */
    @ApiModelProperty(value = "五年当期均值")
    private Integer fiveCurrentAverage;

    @ApiModelProperty(value = "一年当季均值")
    private Integer fiveCurrentQuarterAverage;

    /**
     * 五年当年均值
     */
    @ApiModelProperty(value = "五年当年均值")
    private Integer fiveAnnualAverage;


}
