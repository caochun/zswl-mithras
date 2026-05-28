package cn.zswltech.mithras.dto.capital;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author zhouning
 * @date 2024/7/16/13:31
 * @description
 */
@Data
public class BankFlowConfirmIncomeREQ {

    @ApiModelProperty(value = "流水ID")
    private Long financeFlowId;

    @ApiModelProperty(value = "现金流项目")
    private String cashFlowItem;

    @ApiModelProperty(value = "客户id")
    private Long clientId;

    @ApiModelProperty(value = "合同id")
    private Long contractId;

    @ApiModelProperty(value = "合同借据id")
    private Long receiptId;


    @ApiModelProperty(value = "核销金额")
    private String writeOffAmount;
}
