package cn.zswltech.mithras.dto.contract.leaseitem;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author dingqi
 * @date 2023/9/27
 * @description
 */
@Data
public class ContractChooseLeaseItemREQ {
    @NotNull(message = "合同id不能为空")
    @ApiModelProperty("合同id")
    private Long contractId;

    @NotNull(message = "租赁物审核管理id不能为空")
    @ApiModelProperty("租赁物审核管理id")
    private Long leaseItemInfoId;

    @ApiModelProperty("单条租赁物数据id")
    private List<Long> itemIds;
}
