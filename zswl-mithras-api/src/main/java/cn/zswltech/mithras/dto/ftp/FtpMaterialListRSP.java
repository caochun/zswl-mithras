package cn.zswltech.mithras.dto.ftp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/1/10 10:02
 */
@ApiModel
@Data
public class FtpMaterialListRSP {
    @ApiModelProperty("id")
    private Long id;
    @ApiModelProperty("文件名")
    private String fileName;
}
