package cn.zswltech.mithras.dto.contract.leaseitem;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author dingqi
 * @date 2023/9/27
 * @description
 */
@Data
public class ContractReferenceLeaseItemREQ {
    @ApiModelProperty("合同id")
    private Long contractId;

    @ApiModelProperty("引用的")
    private List<Long> itemIds;
}
