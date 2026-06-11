package cn.zswltech.mithras.dto.capital.write_off;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ReleaseBankFlowREQ {

    @ApiModelProperty(value = "批次号")
    @NotBlank(message = "批次号不能为空")
    private String batchNumber;
}
