package cn.zswltech.mithras.dto.capital;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2024/6/12
 * @description
 */
@Data
public class BankFlowProcessingCenterFinanceCashFlowREQ {
    @NotNull(message = "收付款id不能为空")
    @ApiModelProperty("收付款id")
    private Long receiptRepayBaseId;

    @NotBlank(message = "现金流类型不能为空")
    @ApiModelProperty("现金流类型")
    private String cashFlowItem;
}
