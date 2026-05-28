package cn.zswltech.mithras.dto.projreview.price;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.contract.ContractBaseInfoRSP;
import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @author zhaozhengkang
 * @description 保理报价方案
 * @date 2022-08-02
 */
@Data
@ApiModel("保理报价方案表列表-返回体")
public class ProjReviewFactoringPriceRSP extends ListBaseRSP {

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

    /**
     * 项目批复金额
     */
    @ApiModelProperty("项目批复金额")
    private Long approvedAmount;

    /**
     * 保理额度有效期（月
     */
    @ApiModelProperty(value = "保理额度有效期（月")
    private Integer factoringCreditTerm;

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
     * 保理融资比例
     */
    @ApiModelProperty(value = "保理融资比例")
    private Integer factoringFinancingProportion;

    /**
     * 手续费
     */
    @ApiModelProperty(value = "手续费")
    private Long consultingFee;

    /**
     * 拟转应收账款概述
     */
    @ApiModelProperty(value = "拟转应收账款概述")
    private String summary;

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
     * 计划起租日
     */
    @ApiModelProperty(value = "计划起租日")
    private LocalDate plannedStartingDate;

    @ApiModelProperty(value = "存续合同列表")
    private List<ContractBaseInfoRSP> contracts;

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
