package cn.zswltech.mithras.dto.capital;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author yangxiong
 * @date 2024/5/20/18:20
 * @description
 */
@Data
@ApiModel(value = "业务流水资金端列表返回体")
public class BusinessFlowFinanceListRSP {

    @ApiModelProperty(value = "idKey")
    private String idKey;

    @ApiModelProperty(value = "收付款id")
    private Long receiptRepayId;

    @ApiModelProperty(value = "核销状态")
    private String writeOffStatus;

    @ApiModelProperty(value = "核销状态展示文案")
    private String writeOffStatusDisplay;

//    @ApiModelProperty(value = "应收/付款类型")
//    private String type;

    @ApiModelProperty(value = "现金流项目")
    private String cashFlowItem;

    @ApiModelProperty(value = "现金流项目展示文案")
    private String cashFlowItemDisplay;

    @ApiModelProperty(value = "金额")
    private Long amount;

    @ApiModelProperty(value = "本金")
    private Long principalAmount;

    @ApiModelProperty(value = "利息")
    private Long interestAmount;

    @ApiModelProperty(value = "期项")
    private Integer phase;

    @ApiModelProperty(value = "已核销金额")
    private Long actualVerifyAmount;

    @ApiModelProperty(value = "已核销本金金额")
    private Long actualVerifyPrincipalAmount;

    @ApiModelProperty(value = "已核销利息金额")
    private Long actualVerifyInterestAmount;

    @ApiModelProperty(value = "日期")
    private String date;

    @ApiModelProperty(value = "最新付款日")
    private LocalDate cashFlowDate;

    @ApiModelProperty(value = "流水ID")
    private String serialNo;

    @ApiModelProperty(value = "融资渠道")
    private String financingRoute;

    @ApiModelProperty(value = "融资金额")
    private Long financingAmount;

    @ApiModelProperty(value = "业务类型")
    private String businessType;

    @ApiModelProperty(value = "融资编号")
    private String financingCode;

    @ApiModelProperty(value = "融资id")
    private Long financingId;
}
