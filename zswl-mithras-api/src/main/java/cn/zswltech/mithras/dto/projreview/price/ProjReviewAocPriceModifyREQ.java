package cn.zswltech.mithras.dto.projreview.price;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @author zhaozhengkang
 * @description 债权转让报价方案表
 * @date 2022-08-02
 */
@Data
@ApiModel("债权转让报价方案表编辑-请求体")
public class ProjReviewAocPriceModifyREQ {

    /**
     * 立项基本信息表id
     */
    @ApiModelProperty(value = "立项基本信息表id")
    private Long id;

    /**
     * 所属项目id
     */
    @ApiModelProperty(value = "所属项目id")
    private Long projectId;

    /**
     * 申报授信金额
     */
    @ApiModelProperty(value = "申报授信金额")
    @NotNull(message = "字段'申报授信金额'为空")
    private Long applyCreditAmount;

    /**
     * 转让额度有效期（月
     */
    @ApiModelProperty(value = "转让额度有效期（月")
    @NotNull(message = "字段'转让额度有效期'为空")
    private Integer aocCreditTerm;

//    /**
//     * 还款方式。间接还款：indirect、直接还款：direct
//     */
//    @ApiModelProperty(value = "还款方式。间接还款：indirect、直接还款：direct")
//    @NotNull(message = "字段'还款方式'为空")
//    private String repayType;

    /**
     * 额度是否可循环
     */
    @ApiModelProperty(value = "额度是否可循环")
    @NotNull(message = "字段'额度是否可循环'为空")
    private Integer creditAmountLoop;

    /**
     * 保证金
     */
    @ApiModelProperty(value = "保证金")
    @NotNull(message = "字段'保证金'为空")
    private Long earnestMoney;

//    /**
//     * 确权方
//     */
//    @ApiModelProperty(value = "确权方")
//    private String confirmatoryParty;

    /**
     * 手续费
     */
    @ApiModelProperty(value = "手续费")
    @NotNull(message = "字段'手续费'为空")
    private Long consultingFee;

//    /**
//     * 转让融资比例
//     */
//    @ApiModelProperty(value = "转让融资比例")
//    @NotNull(message = "字段'转让融资比例'为空")
//    private Integer aocFinancingProportion;

//    /**
//     * 拟转应收账款概述
//     */
//    @ApiModelProperty(value = "拟转应收账款概述")
//    @NotNull(message = "字段'拟转应收账款概述'为空")
//    private String summary;

    /**
     * 转让费率类型。固定利率：fixed、浮动利率：float
     */
    @ApiModelProperty(value = "转让费率类型。固定利率：fixed、浮动利率：float")
//    @NotNull(message = "字段'费率类型'为空")
    private String rateType;

    /**
     * 转让费率值。百分之多少
     */
    @ApiModelProperty(value = "转让费率值。百分之多少")
//    @NotNull(message = "字段'转让费率'为空")
    private Integer aocRatePercent;

    /**
     * 内部收益率。百分之多少
     * @deprecated irr保存有单独接口 cn.zswltech.mithras.api.projreview.ProjReviewPriceApi#saveIrrPercent
     */
    @Deprecated
    @ApiModelProperty(value = "内部收益率。百分之多少")
//    @NotNull(message = "字段'内部收益率'为空")
    private Integer irrPercent;

    /**
     * 计划起租日
     */
    @ApiModelProperty(value = "计划起租日")
    @NotNull(message = "字段'计划起租日'为空")
    private LocalDate plannedStartingDate;

    /**
     * 还款计算方式。等额租金：equivalent_rental、等额本金：equivalent_capital、平息法：pacification、不规则还款：irregular_repaynn其他：other
     */
    @ApiModelProperty(value = "还款计算方式")
    @NotNull(message = "字段'还款计算方式'为空")
    private String rentalCalcType;

    /**
     * 利息方式
     */
    @ApiModelProperty("利息计算方式")
    @NotNull(message = "利息计算方式不能为空")
    private String interestWay;

    @ApiModelProperty(value = "还款频率")
    @NotBlank(message = "'还款频率'不能为空")
    private String repayRate;

}
