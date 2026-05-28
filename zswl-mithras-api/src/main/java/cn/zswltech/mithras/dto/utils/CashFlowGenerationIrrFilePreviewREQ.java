package cn.zswltech.mithras.dto.utils;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author yibin
 */
@Data
public class CashFlowGenerationIrrFilePreviewREQ {
    @ApiModelProperty("文件路径")
    @NotBlank
    private String ossFilename;
}
