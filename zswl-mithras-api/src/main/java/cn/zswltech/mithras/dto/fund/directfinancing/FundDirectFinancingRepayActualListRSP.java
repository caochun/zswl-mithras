package cn.zswltech.mithras.dto.fund.directfinancing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author zhaozhengkang
 * @description 直接融资-实际还款表
 * @date 2023-06-17
 */
@Data
@ApiModel("直接融资-实际还款表列表-返回体")
public class FundDirectFinancingRepayActualListRSP {

    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "融资id")
    private Long financingId;

    @ApiModelProperty(value = "现金流编号")
    private String cashFlowCode;

    @ApiModelProperty(value = "还款日期")
    private LocalDate repayDate;

    @ApiModelProperty(value = "还款期项")
    private Integer phase;

    @ApiModelProperty(value = "本金")
    private String principleAmount;

    @ApiModelProperty(value = "利息")
    private String interestAmount;

    @ApiModelProperty(value = "应还总额")
    private String repayAmount;

    @ApiModelProperty(value = "剩余未还本金")
    private String remainingPrincipleAmount;

    @ApiModelProperty("预付差额")
    private Long prePayDifference;

    @ApiModelProperty("核销状态")
    private String writeOffStatus;

    @ApiModelProperty("确认状态")
    private Integer isConfirmed;

    @ApiModelProperty("核销状态")
    private Integer isPaid;
}
