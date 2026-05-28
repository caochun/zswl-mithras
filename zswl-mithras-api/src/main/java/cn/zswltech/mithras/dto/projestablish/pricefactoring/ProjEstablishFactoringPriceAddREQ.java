package cn.zswltech.mithras.dto.projestablish.pricefactoring;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaozhengkang
 * @description 保理报价方案表
 * @date 2022-07-20
 */
@Data
@ApiModel("保理报价方案表新增-请求体")
public class ProjEstablishFactoringPriceAddREQ {

    /**
     * 所属立项id
     */
    @ApiModelProperty(value = "所属立项id")
    private Long projEstablishId;

    /**
     * 申报授信金额
     */
    @ApiModelProperty(value = "申报授信金额")
    private Long applyCreditAmount;

    /**
     * 保理额度有效期（月
     */
    @ApiModelProperty(value = "保理额度有效期（月")
    private Integer factoringCreditTerm;

    /**
     * 还款方式。间接还款：indirect、直接还款：direct
     */
    @ApiModelProperty(value = "还款方式。间接还款：indirect、直接还款：direct")
    private String repayType;

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
     * 确权方
     */
    @ApiModelProperty(value = "确权方")
    private String confirmatoryParty;

    /**
     * 服务费/咨询费
     */
    @ApiModelProperty(value = "服务费/咨询费")
    private Long consultingFee;

    @ApiModelProperty(value = "保理融资比例")
    private Integer factoringFinancingProportion;
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
     * 是否存续租赁授信
     */
    @ApiModelProperty("是否存续租赁授信")
    private Boolean existingLeaseCredit;

    /**
     * 是否新增租赁授信
     */
    @ApiModelProperty("是否新增租赁授信")
    private Boolean newLeaseCredit;

    /**
     * 租金计算方式。等额租金：equivalent_rental、等额本金：equivalent_capital、平息法：pacification、不规则还款：irregular_repaynn其他：other
     */
    @ApiModelProperty(value = "租金计算方式")
    private String rentalCalcType;

    @ApiModelProperty(value = "还款频率")
    private String repayRate;

}
