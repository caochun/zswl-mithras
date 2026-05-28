package cn.zswltech.mithras.dto.projestablish.pricelease;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description 租赁报价方案表
 * @author zhaozhengkang
 * @date 2022-07-20
 */
@Data
@ApiModel("租赁报价方案表新增-请求体")
public class ProjEstablishLeasePriceAddREQ {

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
    * 租赁期限月数
    */
    @ApiModelProperty(value = "租赁期限月数")
    private Integer leaseMonthCount;

    @ApiModelProperty(value = "还款频率")
    private String repayRate;

    /**
    * 还款期数
    */
    @ApiModelProperty(value = "还款期数")
    private Integer repayTimesTotal;

    /**
    * 支付方式。先付：advanced、后付：afterward
    */
    @ApiModelProperty(value = "支付方式。先付：advanced、后付：afterward")
    private String payType;

    /**
    * 租金计算方式。等额租金：equivalent_rental、等额本金：equivalent_capital、平息法：pacification、不规则还款：irregular_repaynn其他：other
    */
    @ApiModelProperty(value = "租金计算方式。等额租金：equivalent_rental、等额本金：equivalent_capital、平息法：pacification、不规则还款：irregular_repay 其他：other")
    private String rentalCalcType;

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
    * 首付款
    */
    @ApiModelProperty(value = "首付款")
    private Long downPayment;

    /**
    * 服务费/咨询费
    */
    @ApiModelProperty(value = "服务费/咨询费")
    private Long consultingFee;

    /**
    * 名义货价
    */
    @ApiModelProperty(value = "名义货价")
    private Long nominalPrice;

    /**
    * 租赁利率类型。固定利率：fixed、浮动利率：float
    */
    @ApiModelProperty(value = "租赁利率类型。固定利率：fixed、浮动利率：float")
    private String rateType;

    /**
    * 租赁利率值。百分之多少
    */
    @ApiModelProperty(value = "租赁利率值。百分之多少")
    private Integer leaseRatePercent;

    /**
    * 内部收益率。百分之多少
    */
    @ApiModelProperty(value = "内部收益率。百分之多少")
    private Integer irrPercent;

}
