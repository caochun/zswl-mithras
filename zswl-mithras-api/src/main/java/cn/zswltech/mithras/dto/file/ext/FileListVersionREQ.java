package cn.zswltech.mithras.dto.file.ext;

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
public class FileListVersionREQ {

    @NotNull(message = "版本id不能为空")
    @ApiModelProperty("版本id")
    private Long versionId;

    @NotBlank(message = "模块不能为空")
    @ApiModelProperty("模块类型 BusinessModuleEnum")
    private String moduleType;

}
