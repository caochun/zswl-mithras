package cn.zswltech.mithras.dto.contract.leaseitem;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2023/9/21
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("合同管理-租赁物信息-列表-请求参数")
public class ContractLeaseItemListREQ extends PageReq {
    @NotNull(message = "合同id不能为空")
    @ApiModelProperty("合同id")
    private Long contractId;
}
