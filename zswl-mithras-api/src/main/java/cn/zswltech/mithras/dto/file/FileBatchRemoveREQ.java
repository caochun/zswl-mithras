package cn.zswltech.mithras.dto.file;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * @date 2022/8/15
 */
@Data
@ApiModel("删除文件-请求体")
public class FileBatchRemoveREQ {
    @NotNull(message = "删除文件id列表不能为空")
    @ApiModelProperty("文件id列表")
    private List<Long> fileIds;

    @NotNull(message = "模块主id不能为空")
    @ApiModelProperty("模块主id")
    private Long mainId;

    @NotBlank(message = "模块不能为空")
    @ApiModelProperty("模块类型 BusinessModuleEnum")
    private String moduleType;

    @ApiModelProperty("各模块扩展参数")
    private Map<String, Object> ext;

    @ApiModelProperty("用户id")
    private Long userId;

}
