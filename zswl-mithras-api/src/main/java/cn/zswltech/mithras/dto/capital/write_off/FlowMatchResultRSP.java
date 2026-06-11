package cn.zswltech.mithras.dto.capital.write_off;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author bigbear
 * @date 2024/9/19 19:10
 * @description
 */
@Data
@ApiModel(value = "选择流水后的排序结果")
public class FlowMatchResultRSP {

    @ApiModelProperty(value = "tab主键ID")
    private Long id;

    @ApiModelProperty(value = "tab名称")
    private String tabName;

    @ApiModelProperty(value = "批次号")
    private String batchNumber;

    @ApiModelProperty(value = "账户类型")
    private String accountType;

    @ApiModelProperty(value = "是否完美匹配")
    private Boolean perfectMatch;

    @ApiModelProperty(value = "银行流水金额合计")
    private Long bankFlowAmountSum;

    @ApiModelProperty(value = "业务流水金额合计")
    private Long businessFlowAmountSum;

    @ApiModelProperty(value = "本次是否核销")
    private Boolean isWriteOff;

    @ApiModelProperty(value = "流水结果列表")
    private List<BankFlowCenterListBO> bankFlowList;

    @ApiModelProperty(value = "业务流水列表")
    private List<FinanceFlowMatchResultRSP> businessFlowList;
}
