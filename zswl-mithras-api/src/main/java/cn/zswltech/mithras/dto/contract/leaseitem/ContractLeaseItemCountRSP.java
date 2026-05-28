package cn.zswltech.mithras.dto.contract.leaseitem;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dingqi
 * @date 2023/9/21
 * @description
 */
@Data
public class ContractLeaseItemCountRSP {
    @ApiModelProperty("账面原值总计")
    private Long originalBookValueTotal;

    @ApiModelProperty("账面净值总计")
    private Long originalBookNetValueTotal;

    @ApiModelProperty("评估原值总计")
    private Long assessedValueTotal;

    @ApiModelProperty("评估净值总计")
    private Long assessedNetValueTotal;
}
