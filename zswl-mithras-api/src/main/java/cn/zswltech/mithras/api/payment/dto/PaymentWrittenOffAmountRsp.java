package cn.zswltech.mithras.api.payment.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dingqi
 * @date 2023/5/16
 * @description 付款相关金额详情
 */
@Data
@ApiModel("付款信息-相关核销金额详情-返回体")
public class PaymentWrittenOffAmountRsp {
    @ApiModelProperty("付款id")
    private Long paymentId;

    @ApiModelProperty("付款编号")
    private String paymentCode;

    @ApiModelProperty("已核销付款金额")
    private Long paymentAmount = 0L;

    @ApiModelProperty("已核销首期租金")
    private Long downPaymentAmount = 0L;

    @ApiModelProperty("已核销客户保证金")
    private Long earnestMoneyAmount = 0L;

    @ApiModelProperty("已核销厂商保证金")
    private Long retentionMoneyAmount = 0L;

    @ApiModelProperty("已核销咨询费/服务费/手续费")
    private Long consultingFeeAmount = 0L;

    @ApiModelProperty("已核销名义价款")
    private Long nominalPriceAmount = 0L;

    @ApiModelProperty("实际付款日期")
    private String actualPayDate;

    @ApiModelProperty("资金FTP成本")
    private Integer cashFtp;

    @ApiModelProperty("票据FTP成本")
    private Integer billFtp;

    @ApiModelProperty("业务类型")
    private String bizType;

    @ApiModelProperty("手续费(元)")
    private Long commission = 0L;

    @ApiModelProperty("首期利息(元)")
    private Long firstInstallmentInterest = 0L;

    @ApiModelProperty("FTP审核表格")
    private FtpAssessDTO ftpAssessDto;

    @Data
    public static class FtpAssessDTO {

        @ApiModelProperty(value = "id")
        private Long id;

        @ApiModelProperty(value = "付款id")
        private Long paymentId;

        @ApiModelProperty(value = "基础价格")
        private Long basePrice;

        @ApiModelProperty(value = "山区调整")
        private Long mountainAdjustment;

        @ApiModelProperty(value = "评级调整")
        private Long gradeAdjustment;

        @ApiModelProperty(value = "评级调整")
        private Long guidePrice;

        @ApiModelProperty(value = "是否质押")
        private Long pledgePrice;

        @ApiModelProperty(value = "手工调整")
        private Long handAdjustment;

        @ApiModelProperty(value = "考核价格")
        private Long assessmentPrice;

        @ApiModelProperty(value = "票据价格")
        private Long ticketPrice;

        @ApiModelProperty(value = "备注")
        private String remark;

        @ApiModelProperty(value = "杭甬特殊调整")
        private Long hangyongSpecialAdjustment;

        @ApiModelProperty(value = "是否特殊事项")
        private Integer isSpecialMatter;
    }
}
