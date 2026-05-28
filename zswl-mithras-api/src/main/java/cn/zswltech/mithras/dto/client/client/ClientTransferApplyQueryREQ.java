package cn.zswltech.mithras.dto.client.client;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author dingqi
 * @date 2024/9/26
 * @description
 */
@Data
public class ClientTransferApplyQueryREQ {
    @NotBlank(message = "批次号不能为空")
    @ApiModelProperty("批次号")
    private String batchNo;
}
