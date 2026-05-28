package cn.zswltech.mithras.dto.capital;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author dingqi
 * @date 2024/6/12
 * @description
 */
@Data
public class BankFlowProcessingCenterFinancePaymentCashFlowREQ {
    @NotEmpty(message = "收付款id列表不能为空")
    @ApiModelProperty("收付款id列表")
    private List<Long> receiptRepayBaseIdList;

    @NotBlank(message = "应付日期开始不能为空")
    @ApiModelProperty(value = "应付日期开始")
    private String actualLoanDateFrom;

    @NotBlank(message = "应付日期结束不能为空")
    @ApiModelProperty(value = "应付日期结束")
    private String actualLoanDateTo;

    @ApiModelProperty(value = "流水记录id")
    private List<Long> financeFlowIdList;
}
