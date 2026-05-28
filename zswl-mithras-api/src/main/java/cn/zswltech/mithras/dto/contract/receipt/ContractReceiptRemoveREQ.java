package cn.zswltech.mithras.dto.contract.receipt;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2022/8/20
 * @description
 */
@Data
@ApiModel("删除借据-请求体")
public class ContractReceiptRemoveREQ {
    @NotNull(message = "借据id不能为空")
    @ApiModelProperty("借据id")
    private Long receiptId;
}
