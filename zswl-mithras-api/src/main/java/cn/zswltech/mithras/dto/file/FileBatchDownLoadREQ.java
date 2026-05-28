package cn.zswltech.mithras.dto.file;

import cn.zswltech.mithras.dto.MaterialsListIdType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author jackerhe
 * @date 2022/8/15
 */
@Data
@ApiModel("下载文件-请求体")
public class FileBatchDownLoadREQ extends MaterialsListIdType {
    @NotNull(message = "文件id不能为空")
    @ApiModelProperty("文件")
    private List<Long> fileId;

    @NotNull(message = "模块主id不能为空")
    @ApiModelProperty("模块主id")
    private Long mainId;

    @NotBlank(message = "模块不能为空")
    @ApiModelProperty("模块类型 BusinessModuleEnum")
    private String moduleType;

    //@NotNull(message = "文件类型不能为空")
//    @ApiModelProperty("文件类型 MaterialsEnum")
//    private String materialsType;

    @ApiModelProperty("是否平铺压缩")
    private Boolean colZipFlag;

    @ApiModelProperty("权限校验令牌")
    private String token;
}
