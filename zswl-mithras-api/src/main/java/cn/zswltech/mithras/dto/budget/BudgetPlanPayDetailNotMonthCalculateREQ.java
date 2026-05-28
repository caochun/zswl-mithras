package cn.zswltech.mithras.dto.budget;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

/**
 * @author dingqi
 * @date 2025/4/16
 * @description
 */
@Data
public class BudgetPlanPayDetailNotMonthCalculateREQ {
    @NotNull(message = "<投放计划id>不能为空")
    @ApiModelProperty("投放计划id")
    private Long budgetPlanPayId;

    @ApiModelProperty("投放计划明细id")
    private Long budgetPlanPayDetailId;

    @NotBlank(message = "<客户名称>不能为空")
    @ApiModelProperty("客户名称")
    private String clientName;

    @NotBlank(message = "<FTP行业分类>不能为空")
    @ApiModelProperty("FTP行业分类")
    private String ftpIndustryCategory;

    @NotBlank(message = "<风控行业分类>不能为空")
    @ApiModelProperty("风控行业分类")
    private String riskControlIndustryClassify;

    @NotBlank(message = "<业务类型>不能为空")
    @ApiModelProperty("业务类型（租赁类型）")
    private String leaseType;

    @ApiModelProperty("项目主办id")
    private Long sponsorUserId;

    @ApiModelProperty("业务负责人id")
    private Long bizDeptLeaderId;

    @ApiModelProperty("业务部门id")
    private Long belongDeptId;

    @NotNull(message = "<项目金额>不能为空")
    @ApiModelProperty("项目金额")
    private Long projectAmount;

    @ApiModelProperty("首期租金率")
    private Long firstRentRate;

    @NotNull(message = "<租赁期限>不能为空")
    @ApiModelProperty("租赁期限")
    private Integer termMonth;

    @ApiModelProperty("保证金率")
    private Integer depositRate;

    @ApiModelProperty("还款频率")
    private String repayFrequency;

    @ApiModelProperty("咨询费率")
    private Integer consultingFeeRate;

    @ApiModelProperty("还款期数")
    private Integer repayTimesTotal;

    @ApiModelProperty("手续费率")
    private Integer commissionRate;

    @ApiModelProperty("支付方式")
    private String payType;

    @ApiModelProperty("名义价款")
    private Long nominalPrice;

    @ApiModelProperty("利息计算方式")
    private String interestCalculateWay;

    @NotBlank(message = "<合同利率类型>不能为空")
    @ApiModelProperty("合同利率类型")
    private String contractInterestRateType;

    @NotNull(message = "<合同利率>不能为空")
    @ApiModelProperty("合同利率")
    private Integer contractInterestRate;

    @NotNull(message = "<投放日>不能为空")
    @ApiModelProperty("投放日")
    private LocalDate payDate;

    @ApiModelProperty("IRR")
    private Integer irr;

    @ApiModelProperty("现金流计划")
    private List<BudgetPlanPayDetailNotMonthCashFlowREQ> cashFlowList;

    @ApiModelProperty(value = "国家")
    private String country;

    @ApiModelProperty(value = "省份")
    private String province;

    @ApiModelProperty(value = "城市")
    private String city;

    @ApiModelProperty(value = "区、县")
    private String district;
}
