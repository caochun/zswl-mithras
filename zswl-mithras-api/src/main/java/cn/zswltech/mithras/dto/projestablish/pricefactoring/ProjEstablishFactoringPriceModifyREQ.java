package cn.zswltech.mithras.dto.projestablish.pricefactoring;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author zhaozhengkang
 * @description 保理报价方案表
 * @date 2022-07-20
 */
@Data
@ApiModel("保理报价方案表编辑-请求体")
public class ProjEstablishFactoringPriceModifyREQ {

    /**
     * 立项基本信息表id
     */
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 所属立项id
     */
    @ApiModelProperty(value = "所属立项id")
    private Long projEstablishId;

    /**
     * 申报授信金额
     */
    @ApiModelProperty(value = "申报授信金额")
    @NotNull(message = "字段'申报授信金额'为null")
    @Min(value = 1, message = "申报授信金额需大于0")
    private Long applyCreditAmount;

    /**
     * 保理额度有效期（月
     */
    @ApiModelProperty(value = "保理额度有效期（月")
    private Integer factoringCreditTerm;

    /**
     * 额度是否可循环
     */
    @ApiModelProperty(value = "额度是否可循环")
    private Integer creditAmountLoop;

    /**
     * 保证金
     */
    @ApiModelProperty(value = "保证金")
    private Long earnestMoney;

    /**
     * 手续费
     */
    @ApiModelProperty(value = "手续费")
    private Long consultingFee;

    @ApiModelProperty(value = "保理融资比例")
    private Integer factoringFinancingProportion;

    /**
     * 保理费率类型。固定利率：fixed、浮动利率：float
     */
    @ApiModelProperty(value = "保理费率类型。固定利率：fixed、浮动利率：float")
    private String rateType;

    /**
     * 保理费率值。百分之多少
     */
    @ApiModelProperty(value = "保理费率值。百分之多少")
    private Integer factoringRatePercent;

    /**
     * 内部收益率。百分之多少
     */
    @ApiModelProperty(value = "内部收益率。百分之多少")
    private Integer irrPercent;

    /**
     * 还款计算方式
     */
    @ApiModelProperty(value = "还款计算方式")
    private String rentalCalcType;

    @ApiModelProperty(value = "利息计算方式")
    private String interestWay;

    @ApiModelProperty(value = "还款频率")
    private String repayRate;

}
