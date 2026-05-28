package cn.zswltech.mithras.dto.file;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * @ClassName FileListREQ
 * @Author jackerhe
 * @Date 2022/11/19 11:22 上午
 * @Version 1.0
 **/
@Data
public class FileListREQ extends PageReq {
    @NotNull(message = "模块主id不能为空")
    @ApiModelProperty("模块主id")
    private Long mainId;

    @NotBlank(message = "模块不能为空")
    @ApiModelProperty("模块类型 BusinessModuleEnum")
    private String moduleType;

//    @NotNull(message = "文件类型不能为空")
    @ApiModelProperty("文件类型 MaterialsEnum")
    private List<String> materialsTypes;

    private String materialsType;

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("各模块扩展参数")
    private Map<String, Object> ext;

    @ApiModelProperty("流程Id")
    private String processInstanceId;

    @ApiModelProperty("是否需要返回预览地址")
    private Boolean needPreviewUrl = Boolean.FALSE;

}
