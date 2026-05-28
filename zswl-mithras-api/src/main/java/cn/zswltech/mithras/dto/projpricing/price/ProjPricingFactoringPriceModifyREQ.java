package cn.zswltech.mithras.dto.projpricing.price;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author zhaozhengkang
 * @description 保理报价方案表
 * @date 2022-08-02
 */
@Data
@ApiModel("保理报价方案表编辑-请求体")
public class ProjPricingFactoringPriceModifyREQ {

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
    @NotNull(message = "字段'申报授信金额'为null")
    private Long applyCreditAmount;

    @ApiModelProperty(value = "项目批复金额")
    private Long approvedAmount;

    /**
     * 保理额度有效期（月
     */
    @ApiModelProperty(value = "保理额度有效期（月")
    @NotNull(message = "字段'保理额度有效期'为null")
    private Integer factoringCreditTerm;

//    /**
//     * 还款方式。间接还款：indirect、直接还款：direct
//     */
//    @ApiModelProperty(value = "还款方式。间接还款：indirect、直接还款：direct")
//    @NotNull(message = "字段'还款方式'为null")
//    private String repayType;

    /**
     * 额度是否可循环
     */
    @ApiModelProperty(value = "额度是否可循环")
    @NotNull(message = "字段'额度是否可循环'为null")
    private Integer creditAmountLoop;

    /**
     * 保证金
     */
    @ApiModelProperty(value = "保证金")
    @NotNull(message = "字段'保证金'为null")
    private Long earnestMoney;

//    /**
//     * 确权方
//     */
//    @ApiModelProperty(value = "确权方")
//    private String confirmatoryParty;

    /**
     * 保理融资比例
     */
    @ApiModelProperty(value = "保理融资比例")
    @NotNull(message = "字段'保理融资比例'为null")
    private Integer factoringFinancingProportion;

    /**
     * 手续费
     */
    @ApiModelProperty(value = "手续费")
    private Long consultingFee;

//    /**
//     * 拟转应收账款概述
//     */
//    @ApiModelProperty(value = "拟转应收账款概述")
//    @NotNull(message = "字段'拟转应收账款概述'为null")
//    private String summary;

    /**
     * 保理费率类型。固定利率：fixed、浮动利率：float
     */
    @ApiModelProperty(value = "保理费率类型。固定利率：fixed、浮动利率：float")
    @NotNull(message = "字段'费率类型'为null")
    private String rateType;

    /**
     * 保理费率值。百分之多少
     */
    @ApiModelProperty(value = "保理费率值。百分之多少")
    @NotNull(message = "字段'保理费率值'为null")
    private Integer factoringRatePercent;

    /**
     * 内部收益率。百分之多少
     * @deprecated irr保存有单独接口 cn.zswltech.mithras.api.projreview.ProjReviewPriceApi#saveIrrPercent
     */
    @Deprecated
    @ApiModelProperty(value = "内部收益率。百分之多少")
//    @NotNull(message = "字段'内部收益率'为null")
    private Integer irrPercent;

//    /**
//     * 是否有存续租赁授信
//     */
//    @ApiModelProperty(value = "是否有存续租赁授信")
//    private Boolean existingLeaseCredit;

//    /**
//     * 是否新增租赁授信
//     */
//    @ApiModelProperty(value = "是否新增租赁授信")
//    private Boolean newLeaseCredit;

//    /**
//     * 计划起租日
//     */
//    @ApiModelProperty(value = "计划起租日")
//    @NotNull(message = "字段'计划起租日'为null")
//    private LocalDate plannedStartingDate;

    /**
     * 还款计算方式
     */
    @ApiModelProperty(value = "还款计算方式")
    @NotNull(message = "还款计算方式不能为空")
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
