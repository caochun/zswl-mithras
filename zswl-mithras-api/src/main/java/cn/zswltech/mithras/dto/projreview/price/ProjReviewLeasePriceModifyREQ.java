package cn.zswltech.mithras.dto.projreview.price;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @author zhaozhengkang
 * @description 租赁报价方案表
 * @date 2022-08-02
 */
@Data
@ApiModel("租赁报价方案表编辑-请求体")
public class ProjReviewLeasePriceModifyREQ {

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
     * 租赁期限月数
     */
    @ApiModelProperty(value = "租赁期限月数")
    @NotNull(message = "字段'租赁期限'为空")
    @Min(value = 1,message = "字段'租赁期限'需大于0")
    private Integer leaseMonthCount;

    @ApiModelProperty(value = "还款频率")
    @NotBlank(message = "'还款频率'不能为空")
    private String repayRate;

    /**
     * 还款期数
     */
    @ApiModelProperty(value = "还款期数")
    @NotNull(message = "字段'还款期数'为空")
    @Min(value = 1,message = "字段'还款期数'需大于0")
    private Integer repayTimesTotal;

    /**
     * 支付方式。先付：advanced、后付：afterward
     */
    @ApiModelProperty(value = "支付方式。先付：advanced、后付：afterward")
    @NotNull(message = "字段'支付方式'为空")
    private String payType;

    /**
     * 租金计算方式。等额租金：equivalent_rental、等额本金：equivalent_capital、平息法：pacification、不规则还款：irregular_repaynn其他：other
     */
    @ApiModelProperty(value = "租金计算方式")
    @NotNull(message = "字段'租金计算方式'为空")
    private String rentalCalcType;

    /**
     * 利息方式
     */
    @ApiModelProperty("利息计算方式")
    @NotNull(message = "利息计算方式不能为空")
    private String interestWay;

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

    /**
     * 首付款
     */
    @ApiModelProperty(value = "首付款")
    @NotNull(message = "字段'首付款'为空")
    private Long downPayment;

    /**
     * 服务费/咨询费
     */
    @ApiModelProperty(value = "服务费/咨询费")
    @NotNull(message = "字段'服务费/咨询费'为空")
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

    /**
     * 名义货价
     */
    @ApiModelProperty(value = "名义货价")
    @NotNull(message = "字段'名义价款'为空")
    private Long nominalPrice;

    /**
     * 租赁利率类型。固定利率：fixed、浮动利率：float
     */
    @ApiModelProperty(value = "租赁利率类型。固定利率：fixed、浮动利率：float")
    @NotNull(message = "字段'租赁利率类型'为空")
    private String rateType;

    /**
     * 租赁利率值。百分之多少
     */
    @ApiModelProperty(value = "租赁利率值。百分之多少")
    @NotNull(message = "字段'租赁利率值'为空")
    private Integer leaseRatePercent;

    /**
     * 内部收益率。百分之多少
     * @deprecated irr保存有单独接口 cn.zswltech.mithras.api.projreview.ProjReviewPriceApi#saveIrrPercent
     */
    @Deprecated
    @ApiModelProperty(value = "内部收益率。百分之多少")
//    @NotNull(message = "字段'IRR'为空")
    private Integer irrPercent;

    /**
     * 计划起租日
     */
    @ApiModelProperty(value = "计划起租日")
    @NotNull(message = "字段'计划起租日'为空")
    private LocalDate plannedStartingDate;

}
