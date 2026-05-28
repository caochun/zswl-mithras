package cn.zswltech.mithras.dto.file;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author jackerhe
 * @date 2022/8/15
 */
@Data
@ApiModel("下载文件-请求体")
public class FileDownLoadTemplateREQ {

    @NotBlank(message = "模版名称不能为空")
    @ApiModelProperty("模版名称 FileTemplateEnum")
    private String templateName;

    @ApiModelProperty("模块类型 BusinessModuleEnum")
    private String moduleType;
}
