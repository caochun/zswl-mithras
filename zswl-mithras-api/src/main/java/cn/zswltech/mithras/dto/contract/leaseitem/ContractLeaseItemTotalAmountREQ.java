package cn.zswltech.mithras.dto.contract.leaseitem;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2023/9/28
 * @description
 */
@Data
public class ContractLeaseItemTotalAmountREQ {
    @NotNull(message = "合同id不能为空")
    @ApiModelProperty("合同id")
    private Long contractId;

//    @NotNull(message = "租赁物总额不能为空")
//    @ApiModelProperty("租赁物总额")
//    private Long leaseItemTotalAmount;
}
