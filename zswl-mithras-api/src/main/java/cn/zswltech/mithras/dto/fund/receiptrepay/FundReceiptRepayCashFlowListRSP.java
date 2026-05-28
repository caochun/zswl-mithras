package cn.zswltech.mithras.dto.fund.receiptrepay;

import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author zhaozhengkang
 * @description 本金利息一览表
 * @date 2023-02-20
 */
@Data
@ApiModel("本金利息一览表列表-返回体")
public class FundReceiptRepayCashFlowListRSP extends ListBaseRSP {

    @ApiModelProperty(value = "主键")
    private Long id;
    @ApiModelProperty(value = "现金流编号")
    private String cashFlowCode;
    @ApiModelProperty("还款日期")
    private LocalDate repayDate;
    @ApiModelProperty("还款期项")
    private Integer phase;
    @ApiModelProperty("本金")
    private Long principleAmount;
    @ApiModelProperty("利息")
    private Long interestAmount;
//    @ApiModelProperty(value = "本月支付本金")
//    private Long planRepayPrincipal;
//    @ApiModelProperty(value = "本月支付利息")
//    private Long planRepayInterest;
    @ApiModelProperty(value = "核销状态")
    private String writeOffState;
    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "是否标红")
    private Boolean isRed;
    @ApiModelProperty("实付本金")
    private Long actualPrincipalAmount;
    @ApiModelProperty("实付利息")
    private Long actualInterestAmount;
}
