package cn.zswltech.mithras.dto.contract.receipt;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/7/4 15:54
 */
@Data
@ApiModel("更新实际irr-请求体")
public class ContractReceiptUpdateIrrREQ {

    @ApiModelProperty("借据id")
    @NotNull
    private Long receiptId;

    @ApiModelProperty("实际irr")
    private Integer actualIrr;
}
