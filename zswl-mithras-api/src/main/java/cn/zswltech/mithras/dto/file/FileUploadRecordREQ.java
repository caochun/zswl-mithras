package cn.zswltech.mithras.dto.file;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author jackerhe
 * @date 2022/8/15
 */
@Data
@ApiModel("上传文件-预上传地址获取-请求体")
public class FileUploadRecordREQ {

    @NotEmpty(message = "文件名称不能为空")
    @ApiModelProperty("文件名称")
    private List<String> fileNames;

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

}
