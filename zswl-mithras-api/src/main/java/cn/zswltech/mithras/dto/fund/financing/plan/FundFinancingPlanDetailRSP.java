package cn.zswltech.mithras.dto.fund.financing.plan;

import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author dingqi
 * @date 2023/2/21
 * @description
 */
@Data
@ApiModel("融资管理-融资方案详情-返回体")
public class FundFinancingPlanDetailRSP extends ListBaseRSP {
    @ApiModelProperty("主键id")
    private Long id;

    @ApiModelProperty("融资金额")
    private Long financingAmount;

    @ApiModelProperty("预计利息金额")
    private Long interestAmount;

    @ApiModelProperty("融资期限（月）")
    private Integer financingMonth;

    @ApiModelProperty("手续费")
    private Long serviceChargeAmount;

    @ApiModelProperty("还款期数")
    private Integer repayTimes;

    @ApiModelProperty("保证金")
    private Long earnestMoneyAmount;

    @ApiModelProperty("还款频率")
    private String repayFrequency;

    @ApiModelProperty("开证许可证费用")
    private Long licenseAmount;

    @ApiModelProperty("还款方式")
    private String repayWay;

    @ApiModelProperty("其他费用")
    private Long otherAmount;

    @ApiModelProperty("担保费信息")
    private List<GuaranteeAmountInfoRSP> guaranteeAmountInfoList;

    @ApiModelProperty("担保信息")
    private List<GuaranteeInfoRSP> guaranteeInfoList;

    /**
     * FTP收益率
     */
    @ApiModelProperty("FTP收益率")
    private Integer ftpYieldRate;

    @ApiModelProperty("借款年利率类型")
    private String interestRateType;

    @ApiModelProperty("LPR品种")
    private String lprType;

    @ApiModelProperty("LPR利率")
    private Integer lprRatePercent;

    @ApiModelProperty("LPR加点")
    private Integer lprAddPercent;

    @ApiModelProperty("综合借款年利率")
    private Integer comprehensiveInterestRate;

    @ApiModelProperty("综合融资成本")
    private Integer comprehensiveFinancingCost;

    @ApiModelProperty("还款日")
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

    @ApiModelProperty("费用合计")
    private Long totalFee;

    @Data
    public static class GuaranteeAmountInfoRSP {
        @ApiModelProperty("融资机构id")
        private Long organizationId;

        @ApiModelProperty("融资机构名称")
        private String organizationName;

        @ApiModelProperty("担保机构id")
        private Long guaranteeAgencyId;

        @ApiModelProperty("担保机构名称")
        private String guaranteeAgencyName;

        @ApiModelProperty("担保费率")
        private Integer guaranteeFeeRate;

        @ApiModelProperty("担保费")
        private Long guaranteeFeeAmount;
    }

    @Data
    public static class GuaranteeInfoRSP {
        @ApiModelProperty("融资机构id")
        private Long organizationId;

        @ApiModelProperty("融资机构名称")
        private String organizationName;

        @ApiModelProperty("担保机构id")
        private Long guaranteeAgencyId;

        @ApiModelProperty("担保机构名称")
        private String guaranteeAgencyName;

        @ApiModelProperty("担保金额")
        private Long guaranteeAmount;
    }
}
