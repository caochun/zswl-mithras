package cn.zswltech.mithras.dto.fund.directfinancing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author jackerhe
 * @date 2022/8/15
 */
@Data
@ApiModel("下载文件-请求体")
public class FileDownloadREQ {
    @NotNull(message = "上传文件不能为空")
    @ApiModelProperty("文件")
    private Long fileId;
}
