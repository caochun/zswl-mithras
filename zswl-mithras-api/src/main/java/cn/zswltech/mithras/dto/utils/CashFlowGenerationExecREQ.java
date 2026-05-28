package cn.zswltech.mithras.dto.utils;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * @author yibin
 */
@Data
public class CashFlowGenerationExecREQ {
    /**
     * 授信金额
     */
    @NotNull
    @ApiModelProperty("授信金额")
    private Long creditAmount;

    @NotNull
    @ApiModelProperty("租赁期限（月）")
    private Integer leaseMonthCount;
    /**
     * 保证金
     */
    @NotNull
    @ApiModelProperty("保证金金额")
    private Long earnestMoney;
    /**
     * 首期租金
     */
    //@NotNull
    @ApiModelProperty("首期租金")
    private Long downPayment;
    /**
     * 服务费/咨询费/手续费
     */
    @NotNull
    @ApiModelProperty("服务费/咨询费/手续费")
    private Long consultingFee;
    /**
     * 名义价款
     */
    //@NotNull
    @ApiModelProperty("名义货价")
    private Long nominalPrice;
    /**
     * 起租日期
     */
    @NotNull
    @ApiModelProperty("起租日期")
    private LocalDate startDate;
    /**
     * 利率
     */
    @NotNull
    @ApiModelProperty("利率")
    private Integer interestRate;
    /**
     * 还款频率枚举
     */
    @NotBlank
    @ApiModelProperty("还款频率枚举")
    private String repayRate;
    /**
     * 还款期数
     */
    @NotNull
    @ApiModelProperty("还款期数")
    private Integer repayTimes;

    /**
     * 租金计算方式
     */
    @NotBlank
    @ApiModelProperty("租金计算方式")
    private String rentalCalcType;

    /**
     * 利息计算方式
     */
    @NotBlank(message = "利息计算方式不能为空")
    @ApiModelProperty("利息计算方式")
    private String interestWay;

    /**
     * 支付方式。先付：advanced、后付：afterward
     */
    //@NotBlank
    @ApiModelProperty("支付方式")
    private String payType;

    @ApiModelProperty("首期利息")
    private Long firstInstallmentInterest;

    @ApiModelProperty("手续费")
    private Long commission;
}
