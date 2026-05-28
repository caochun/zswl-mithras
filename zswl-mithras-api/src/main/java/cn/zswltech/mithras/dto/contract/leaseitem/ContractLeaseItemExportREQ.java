package cn.zswltech.mithras.dto.contract.leaseitem;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author dingqi
 * @date 2023/9/28
 * @description
 */
@Data
public class ContractLeaseItemExportREQ {
    @NotNull(message = "合同id不能为空")
    @ApiModelProperty("合同id")
    private Long contractId;

    @ApiModelProperty("版本号")
    private String version;

    @ApiModelProperty("租赁物单条数据id")
    private List<Long> itemIds;
}
