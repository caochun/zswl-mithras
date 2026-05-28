package cn.zswltech.mithras.dto.app;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author dingqi
 * @date 2025/9/9
 * @description
 */
@Data
public class AppContractQueryREQ {
    @NotBlank(message = "项目编号不能为空")
    @ApiModelProperty("项目编号")
    private String projCode;
}
