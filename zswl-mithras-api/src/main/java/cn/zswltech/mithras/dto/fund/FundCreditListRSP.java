package cn.zswltech.mithras.dto.fund;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.fund.financing.FundFinancingListRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author zhaozhengkang
 * @description fund_credit
 * @date 2022-12-13
 */
@Data
@ApiModel("fund_credit列表-返回体")
public class FundCreditListRSP {

    @ApiModelProperty("列表记录")
    private PageR<FundCreditListRSP.FundCreditList> records;

    @ApiModelProperty("合计")
    private FundCreditListRSP.Sum sum;

    @Data
    public static class FundCreditList{
    @ApiModelProperty(value = "id")
    private Long id;
    @ApiModelProperty(value = "授信编号")
    private String creditCode;
    @ApiModelProperty(value = "授信机构")
    private Long organizationId;
    @ApiModelProperty(value = "额度是否可循环")
    private Integer recyclable;
    @ApiModelProperty(value = "授信产品")
    private String creditType;
    @ApiModelProperty(value = "授信机构名称")
    private String organizationName;
    @ApiModelProperty(value = "授信总额-总额度")
    private Long totalCreditLimit;
    @ApiModelProperty(value = "授信总额-担保额度")
    private Long guaranteeAmount;
    @ApiModelProperty(value = "授信总额-信用额度")
    private Long creditLimit;
    @ApiModelProperty(value = "已使用-总授信额度（元）")
    private Long usedTotalCreditAmount;
    @ApiModelProperty(value = "已使用-信用额度（元）")
    private Long usedCreditAmount;
    @ApiModelProperty(value = "已使用-担保额度（元）")
    private Long usedGuaranteeAmount;
    @ApiModelProperty(value = "剩余-总授信额度（元）")
    private Long remainingLimit;
    @ApiModelProperty(value = "剩余-信用额度（元）")
    private Long remainingCreditAmount;
    @ApiModelProperty(value = "剩余-担保额度（元）")
    private Long remainingGuaranteeAmount;
    @ApiModelProperty(value = "剩余总授信额度（元）")
    private Long remainingTotalLimit;
    @ApiModelProperty(value = "剩余授信时间")
    private Long remainingDays;
    @ApiModelProperty(value = "授信到期日")
    private LocalDate effectiveDateTo;
    @ApiModelProperty(value = "创建人id")
    private Long createBy;
    @ApiModelProperty(value = "创建人name")
    private String createByName;
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;
    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;
    @ApiModelProperty(value = "是否生效")
    private Boolean effective;
    @ApiModelProperty(value = "剩余本金")
    private Long remainingAmount;
}
    @Data
    public static class Sum{
        @ApiModelProperty(value = "授信总额-总额度")
        private Long totalCreditLimit;
        @ApiModelProperty(value = "授信总额-担保额度")
        private Long guaranteeAmount;
        @ApiModelProperty(value = "授信总额-信用额度")
        private Long creditLimit;
        @ApiModelProperty(value = "已使用-总授信额度（元）")
        private Long usedTotalCreditAmount;
        @ApiModelProperty(value = "已使用-信用额度（元）")
        private Long usedCreditAmount;
        @ApiModelProperty(value = "已使用-担保额度（元）")
        private Long usedGuaranteeAmount;
        @ApiModelProperty(value = "剩余-总授信额度（元）")
        private Long remainingLimit;
        @ApiModelProperty(value = "剩余总授信额度（元）")
        private Long remainingTotalLimit;
        @ApiModelProperty(value = "剩余-信用额度（元）")
        private Long remainingCreditAmount;
        @ApiModelProperty(value = "剩余-担保额度（元）")
        private Long remainingGuaranteeAmount;
        @ApiModelProperty(value = "剩余本金")
        private Long remainingAmount;
    }
}
