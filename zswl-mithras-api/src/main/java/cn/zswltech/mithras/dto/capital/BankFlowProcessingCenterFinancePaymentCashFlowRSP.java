package cn.zswltech.mithras.dto.capital;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author dingqi
 * @date 2024/6/12
 * @description
 */
@Data
public class BankFlowProcessingCenterFinancePaymentCashFlowRSP implements Serializable {

    private static final long serialVersionUID = 8154665058295891793L;
    @ApiModelProperty(value = "idKey")
    private String idKey;

    @ApiModelProperty(value = "机构名称")
    private String orgName;

    @ApiModelProperty(value = "借据ID")
    private Long receiptRepayBaseId;

    @ApiModelProperty(value = "借据编号")
    private String receiptRepayBaseCode;

    @ApiModelProperty(value = "融资id")
    private Long financingId;

    @ApiModelProperty(value = "融资类型")
    private String financingType;

    @ApiModelProperty(value = "融资编号")
    private String financingCode;

    @ApiModelProperty(value = "业务类型")
    private String businessType;

    @ApiModelProperty(value = "期项")
    private Integer phase;

    @ApiModelProperty(value = "起息日")
    private String beginInterestDate;

    @ApiModelProperty(value = "融资金额(元)")
    private Long financingAmount;

    @ApiModelProperty("现金流编号")
    private String cashFlowCode;

    @ApiModelProperty("现金流类型")
    private String cashFlowItem;

    @ApiModelProperty("应付金额")
    private Long shouldPayAmount;

    @ApiModelProperty("未付款金额")
    private Long noPayAmount;

    @ApiModelProperty("应付日期")
    private String shouldPayTime;

    @ApiModelProperty(value = "本次核销金额")
    private Long thisWriteOffAmount;

    private long actualDetailAmount;

    private Long originalWriteOffAmount;
}
