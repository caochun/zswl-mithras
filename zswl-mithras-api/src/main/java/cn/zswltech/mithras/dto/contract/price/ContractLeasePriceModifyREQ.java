package cn.zswltech.mithras.dto.contract.price;
import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @description 合同-租赁报价方案表
 * @author vico
 * @date 2022-08-12
 */
@Data
@ApiModel("合同-租赁报价方案表编辑-请求体")
public class ContractLeasePriceModifyREQ {

    /**
    * 租赁报价方案id
    */
    @ApiModelProperty(value = "租赁报价方案id")
    @NotNull(message = "id不能为空")
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
    @NotBlank(message = "'还款频率'不能为空")
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
    @NotBlank(message = "利息计算方式不能为空")
    @ApiModelProperty("利息计算方式")
    private String interestWay;

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

    @ApiModelProperty(value = "租加点")
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
    @ApiModelProperty(value = "内部收益率。百分之多少")
    private Integer irrPercent;

    /**
    * 额度是否可循环
    */
    @ApiModelProperty(value = "额度是否可循环")
    private Integer creditAmountLoop;

}
