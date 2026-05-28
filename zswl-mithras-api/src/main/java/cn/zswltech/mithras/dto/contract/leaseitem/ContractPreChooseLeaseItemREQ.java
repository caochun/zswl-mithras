package cn.zswltech.mithras.dto.contract.leaseitem;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2023/9/27
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ContractPreChooseLeaseItemREQ extends PageReq {
    @NotNull(message = "合同id不能为空")
    @ApiModelProperty("合同id")
    private Long contractId;

    @ApiModelProperty("租赁物名称")
    private String name;
}
