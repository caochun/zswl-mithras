package cn.zswltech.mithras.dto.capital;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dingqi
 * @date 2024/6/12
 * @description
 */
@Data
public class BankFlowProcessingCenterFinanceCashFlowRSP {
    @ApiModelProperty("收付款id")
    private Long receiptRepayBaseId;

    @ApiModelProperty("现金流编号")
    private String cashFlowCode;

    @ApiModelProperty("现金流类型")
    private String cashFlowItem;

    @ApiModelProperty("计划付款/收款金额")
    private Long shouldPayAmount;

    @ApiModelProperty("实际付款/收款金额")
    private Long noPayAmount;

    @ApiModelProperty("计划付款/收款日期")
    private String shouldPayTime;
}
