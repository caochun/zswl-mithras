package cn.zswltech.mithras.dto.file.template;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author yibin
 */

@Data
public class FileTemplateTypeAddREQ {
    @ApiModelProperty("文件模板类型名称")
    @NotBlank
    private String name;
}
