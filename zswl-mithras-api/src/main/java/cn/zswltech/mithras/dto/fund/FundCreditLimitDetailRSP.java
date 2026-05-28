package cn.zswltech.mithras.dto.fund;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@ApiModel("授信使用详情返回体")
@Data
public class FundCreditLimitDetailRSP {

    @ApiModelProperty("详情")
    private List<LimitDetail> limitDetailList;

    @ApiModelProperty("总计")
    private LimitSum limitDetailSum;

    @ApiModelProperty("合计")
    private LimitDetail limitDetailListSum;


    @Data
    public static class LimitDetail{
        @ApiModelProperty("融资id")
        private Long financingId;

        @ApiModelProperty("融资编号")
        private String financingCode;

        @ApiModelProperty("融资机构id")
        private Long organizationId;

        @ApiModelProperty("融资机构名称")
        private String organizationName;

        @ApiModelProperty("融资金额")
        private Long financingAmount;
        @ApiModelProperty("担保融资金额")
        private Long guaranteeFinancingAmount;
        @ApiModelProperty("信用融资金额")
        private Long creditFinancingAmount;

        @ApiModelProperty(value = "占用授信额度（元）")
        private Long usedTotalCreditAmount;
        @ApiModelProperty(value = "占用信用额度（元）")
        private Long usedCreditAmount;
        @ApiModelProperty(value = "占用担保额度（元）")
        private Long usedGuaranteeAmount;

        @ApiModelProperty("剩余本金（元）")
        private Long remainingAmount;
        @ApiModelProperty(value = "剩余担保本金（元）")
        private Long remainingCreditAmount;
        @ApiModelProperty(value = "剩余信用本金（元）")
        private Long remainingGuaranteeAmount;

        @ApiModelProperty("合同利率")
        private Long contractRate;

        @ApiModelProperty("借款日期")
        private LocalDate borrowDate;

        @ApiModelProperty("到期日期")
        private LocalDate expireDate;

        @ApiModelProperty("融资状态")
        private String financingStatus;

        @ApiModelProperty("创建人id")
        private Long createBy;

        @ApiModelProperty("创建人名称")
        private String createByName;

        @ApiModelProperty("创建时间")
        private LocalDateTime createTime;

        @ApiModelProperty("修改时间")
        private LocalDateTime updateTime;

    }


    @Data
    public static class LimitSum {

        @ApiModelProperty(value = "占用总额度（元）")
        private Long usedTotalCreditAmountSum;
        @ApiModelProperty(value = "占用信用额度（元）")
        private Long usedCreditAmountSum;
        @ApiModelProperty(value = "占用担保额度（元）")
        private Long usedGuaranteeAmountSum;

        @ApiModelProperty(value = "剩余总授信额度（元）")
        private Long remainingTotalCreditAmountSum;
        @ApiModelProperty(value = "剩余信用额度（元）")
        private Long remainingCreditAmountSum;
        @ApiModelProperty(value = "剩余担保额度（元）")
        private Long remainingGuaranteeAmountSum;

    }


}
