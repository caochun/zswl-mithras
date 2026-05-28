package cn.zswltech.mithras.dto.projestablish.priceaoc;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaozhengkang
 * @description 债权转让报价方案表
 * @date 2022-07-20
 */
@Data
@ApiModel("债权转让报价方案表新增-请求体")
public class ProjEstablishAocPriceAddREQ {

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
     * 转让额度有效期（月
     */
    @ApiModelProperty(value = "转让额度有效期（月")
    private Integer aocCreditTerm;

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

    @ApiModelProperty("转让融资比例")
    private Integer aocFinancingProportion;
    /**
     * 拟转应收账款概述
     */
    @ApiModelProperty(value = "拟转应收账款概述")
    private String summary;

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
     * 租金计算方式。等额租金：equivalent_rental、等额本金：equivalent_capital、平息法：pacification、不规则还款：irregular_repaynn其他：other
     */
    @ApiModelProperty(value = "租金计算方式")
    private String rentalCalcType;

    @ApiModelProperty(value = "还款频率")
    private String repayRate;

}
