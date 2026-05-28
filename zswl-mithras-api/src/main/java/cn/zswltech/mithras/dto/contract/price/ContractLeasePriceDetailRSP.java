package cn.zswltech.mithras.dto.contract.price;

import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description 合同-租赁报价方案表
 * @author vico
 * @date 2022-08-12
 */
@Data
@ApiModel("合同-租赁报价详情返回体")
public class ContractLeasePriceDetailRSP extends ListBaseRSP {

    /**
    * 租赁报价方案id
    */
    @ApiModelProperty(value = "租赁报价方案id")
    private Long id;

    /**
    * 所属合同id
    */
    @ApiModelProperty(value = "所属合同id")
    private Long contractId;

    /**
    * 申报授信金额-合同金额
    */
    @ApiModelProperty(value = "申报授信金额-合同金额")
    private Long applyCreditAmount;

    /**
    * 租赁期限月数
    */
    @ApiModelProperty(value = "租赁期限月数")
    private Integer leaseMonthCount;

    /**
    * 还款频率。按月，按季，按年，不规则
    */
    @ApiModelProperty(value = "还款频率。按月，按季，按年，不规则")
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
    * 租金计算方式
    */
    @ApiModelProperty(value = "租金计算方式")
    private String rentalCalcType;

    /**
     * 利息计算方式
     */
    @ApiModelProperty("利息计算方式")
    private String interestWay;

    /**
    * 保证金
    */
    @ApiModelProperty(value = "保证金")
    private Long earnestMoney;

    @ApiModelProperty(value = "保证金率")
    private Integer earnestMoneyRate;

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

    @ApiModelProperty(value = "服务费率/咨询费率/手续费率")
    private Integer consultingFeeRate;

    /**
     * 手续费(元)
     */
    @ApiModelProperty(value = "手续费(元)")
    private Long commission;

    /**
     * 首期利息(元)
     */
    @ApiModelProperty(value = "首期利息(元)")
    private Long firstInstallmentInterest;

    @ApiModelProperty(value = "结构化利息列表")
    private List<StructuredInterest> structuredInterestList;

    private String structuredInterest;

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
    * lpr品种 一年期，五年期
    */
    @ApiModelProperty(value = "lpr品种 一年期，五年期")
    private String lprType;

    /**
    * lpr
    */
    @ApiModelProperty(value = "lpr")
    private Integer lprPercent;

    @ApiModelProperty(value = "加点")
    private Integer lprAddPercent;

    @ApiModelProperty(value = "租前lpr")
    private Integer beforeLprPercent;

    @ApiModelProperty(value = "租前家电")
    private Integer beforeLprAddPercent;

    /**
     * 租赁利率类型。固定利率：fixed、浮动利率：float
     */
    @ApiModelProperty(value = "租赁利率类型。固定利率：fixed、浮动利率：float")
    private String beforeRateType;

    /**
     * lpr品种 一年期，五年期
     */
    @ApiModelProperty(value = "lpr品种 一年期，五年期")
    private String beforeLprType;

    /**
    * 罚息日利率
    */
    @ApiModelProperty(value = "罚息日利率")
    private Integer defaultInterestRate;

    /**
    * 内部收益率。百分之多少
    */
    @ApiModelProperty(value = "合同irr")
    private Integer irrPercent;

    @ApiModelProperty(value = "定价irr")
    private Integer pricingIrrPercent;

    /**
    * 额度是否可循环
    */
    @ApiModelProperty(value = "额度是否可循环")
    private Integer creditAmountLoop;

    @ApiModelProperty(value = "项目申报授信金额")
    private Long projCreditAmount;

    @ApiModelProperty(value = "项目保证金")
    private Long projEarnestMoney;

    @ApiModelProperty(value = "项目首期租金")
    private Long projDownPayment;

    @ApiModelProperty(value = "项目服务费/咨询费")
    private Long projConsultingFee;

    @ApiModelProperty(value = "项目租赁利率值")
    private Integer leaseRatePercent;

    @ApiModelProperty(value = "项目irr")
    private Integer projIrrPercent;


}
