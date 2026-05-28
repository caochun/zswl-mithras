package cn.zswltech.mithras.dto.capital;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author dingqi
 * @date 2025/3/29
 * @description
 */
@Data
public class FinanceRepaySplitREQ {
//    @ApiModelProperty("银行流水号")
//    private String bankFlowNo;

    @Valid
    @NotEmpty(message = "目标现金流信息不能为空")
    @ApiModelProperty("目标现金流信息")
    private List<Data> targetCashFlow;

    @lombok.Data
    public static class Data {
        @NotNull(message = "资金收付款id不能为空")
        @ApiModelProperty("资金收付款id")
        private Long receiptRepayBaseId;

        @NotBlank(message = "现金流编号不能为空")
        @ApiModelProperty("现金流编号")
        private String cashFlowCode;

        @NotBlank(message = "现金流类型不能为空")
        @ApiModelProperty("现金流类型")
        private String cashFlowItem;
    }
}
