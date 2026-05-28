package cn.zswltech.mithras.dto.filingmaterials;

import cn.zswltech.mithras.dto.MaterialsListIdType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author jackerhe
 * @date 2022/8/15
 */
@Data
@ApiModel("下载文件-请求体")
public class FilingFileDownloadREQ{
    @NotNull(message = "上传文件不能为空")
    @ApiModelProperty("文件")
    private Long fileId;
}
