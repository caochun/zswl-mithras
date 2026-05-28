package cn.zswltech.mithras.dto.projpricing.price;

import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author zhaozhengkang
 * @description 债权转让报价方案表
 * @date 2022-08-02
 */
@Data
@ApiModel("债权转让报价方案表列表-返回体")
public class ProjPricingAocPriceRSP extends ListBaseRSP {

    /**
     * 所属项目id
     */
    @ApiModelProperty(value = "所属项目id")
    private Long projectId;

    /**
     * 申报授信金额
     */
    @ApiModelProperty(value = "申报授信金额")
    private Long applyCreditAmount;

    @ApiModelProperty(value = "项目批复金额")
    private Long approvedAmount;

    /**
     * 转让额度有效期（月
     */
    @ApiModelProperty(value = "转让额度有效期（月")
    private Integer aocCreditTerm;

//    /**
//     * 还款方式。间接还款：indirect、直接还款：direct
//     */
//    @ApiModelProperty(value = "还款方式。间接还款：indirect、直接还款：direct")
//    private String repayType;

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

//    /**
//     * 确权方
//     */
//    @ApiModelProperty(value = "确权方")
//    private String confirmatoryParty;

    /**
     * 手续费
     */
    @ApiModelProperty(value = "手续费")
    private Long consultingFee;

//    /**
//     * 转让融资比例
//     */
//    @ApiModelProperty(value = "转让融资比例")
//    private Integer aocFinancingProportion;

//    /**
//     * 拟转应收账款概述
//     */
//    @ApiModelProperty(value = "拟转应收账款概述")
//    private String summary;

    /**
     * 转让费率类型。固定利率：fixed、浮动利率：float
     */
    @ApiModelProperty(value = "转让费率类型。固定利率：fixed、浮动利率：float")
    private String rateType;

    /**
     * 转让费率值。百分之多少
     */
    @ApiModelProperty(value = "转让费率值。百分之多少")
    private Integer aocRatePercent;

    /**
     * 内部收益率。百分之多少
     */
    @ApiModelProperty(value = "内部收益率。百分之多少")
    private Integer irrPercent;

    /**
     * 计划起租日
     */
    @ApiModelProperty(value = "计划起租日")
    private LocalDate plannedStartingDate;

    /**
     * 还款计算方式
     */
    @ApiModelProperty(value = "还款计算方式")
    private String rentalCalcType;

    /**
     * 利息计算方式
     */
    @ApiModelProperty("利息计算方式")
    private String interestWay;

    @ApiModelProperty(value = "还款频率")
    private String repayRate;

}
