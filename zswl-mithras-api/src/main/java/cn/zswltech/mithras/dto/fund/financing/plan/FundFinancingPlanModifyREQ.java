package cn.zswltech.mithras.dto.fund.financing.plan;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author dingqi
 * @date 2023/2/21
 * @description
 */
@Data
@ApiModel("融资管理-融资方案修改-请求体")
public class FundFinancingPlanModifyREQ {
    @ApiModelProperty("主键id")
    @NotNull(message = "id不能为空")
    private Long id;

    @ApiModelProperty("融资金额")
    @NotNull(message = "融资金额不能为空")
    private Long financingAmount;

    @ApiModelProperty("预计利息金额")
    @NotNull(message = "预计利息金额不能为空")
    private Long interestAmount;

    @ApiModelProperty("融资期限（月）")
    @NotNull(message = "融资期限（月）不能为空")
    private Integer financingMonth;

//    @ApiModelProperty("保理手续费")
//    private Long serviceChargeAmount;

    @ApiModelProperty("还款期数")
    @NotNull(message = "还款期数不能为空")
    private Integer repayTimes;

    @ApiModelProperty("保证金")
    private Long earnestMoneyAmount;

    @ApiModelProperty("还款频率")
    @NotBlank(message = "还款频率不能为空")
    private String repayFrequency;

//    @ApiModelProperty("开证许可证费用")
//    private Long licenseAmount;

    @ApiModelProperty("还款方式")
    @NotBlank(message = "还款方式不能为空")
    private String repayWay;

//    @ApiModelProperty("其他费用")
//    private Long otherAmount;

    @ApiModelProperty("担保费信息")
    @Valid
    private List<GuaranteeAmountInfoREQ> guaranteeAmountInfoList;

    @ApiModelProperty("担保信息")
    @Valid
    private List<GuaranteeInfoREQ> guaranteeInfoList;

    @ApiModelProperty("借款年利率类型")
    @NotBlank(message = "利率类型不能为空")
    private String interestRateType;

    @ApiModelProperty("LPR品种")
    @NotBlank(message = "LPR品种不能为空")
    private String lprType;

    @ApiModelProperty("LPR利率")
    @NotNull(message = "LPR不能为空")
    private Integer lprRatePercent;

    @ApiModelProperty("LPR加点")
    @NotNull(message = "LPR加点不能为空")
    private Integer lprAddPercent;

//    @ApiModelProperty("综合借款年利率")
//    @NotNull(message = "综合借款年利率不能为空")
//    private Integer comprehensiveInterestRate;

    @ApiModelProperty("还款日")
    @Min(value = 1, message = "还款日数值最小为1")
    @Max(value = 31, message = "还款日数值最大为31")
    private Integer repayDay;

    /**
     * LPR调整方式
     */
    @ApiModelProperty("LPR调整方式")
    private String lprArrangeMode;

    /**
     * LPR调整日
     */
    @ApiModelProperty("LPR调整日")
    private String lprAdjustmentDay;


    @Data
    public static class GuaranteeAmountInfoREQ {
        @ApiModelProperty("融资机构id")
        @NotNull(message = "融资机构id不能为空")
        private Long organizationId;

        @ApiModelProperty("融资机构名称")
        @NotNull(message = "融资机构名称不能为空")
        private String organizationName;

        @ApiModelProperty("担保机构id")
        @NotNull(message = "担保机构id不能为空")
        private Long guaranteeAgencyId;

        @ApiModelProperty("担保机构id")
        @NotNull(message = "担保机构名称不能为空")
        private String guaranteeAgencyName;

        @ApiModelProperty("担保费率")
        @NotNull(message = "担保费率不能为空")
        private Integer guaranteeFeeRate;

        @ApiModelProperty("担保费")
        @NotNull(message = "担保费不能为空")
        private Long guaranteeFeeAmount;
    }

    @Data
    public static class GuaranteeInfoREQ {
        @ApiModelProperty("融资机构id")
        @NotNull(message = "融资机构id不能为空")
        private Long organizationId;

        @ApiModelProperty("融资机构名称")
        @NotNull(message = "融资机构名称不能为空")
        private String organizationName;

        @ApiModelProperty("担保机构id")
        @NotNull(message = "担保机构id不能为空")
        private Long guaranteeAgencyId;

        @ApiModelProperty("担保机构名称")
        @NotNull(message = "担保机构名称不能为空")
        private String guaranteeAgencyName;

        @ApiModelProperty("担保金额")
        @NotNull(message = "担保金额不能为空")
        private Long guaranteeAmount;
    }
}
