package cn.zswltech.mithras.dto.file;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
@ApiModel("上传文件-返回体")
public class FileUploadRSP {
    @NotNull(message = "上传文件id")
    @ApiModelProperty("文件")
    private Long fileId;

    @NotNull(message = "模块主id不能为空")
    @ApiModelProperty("模块主id")
    private Long mainId;

    @NotBlank(message = "模块不能为空")
    @ApiModelProperty("模块类型 BusinessModuleEnum")
    private String moduleType;

    @NotNull(message = "文件类型不能为空")
    @ApiModelProperty("文件类型 MaterialsEnum")
    private String materialsType;

    @ApiModelProperty("文件子类型")
    private String materialsSubType;

    @ApiModelProperty("上传路径")
    private String fileUrl;
}
