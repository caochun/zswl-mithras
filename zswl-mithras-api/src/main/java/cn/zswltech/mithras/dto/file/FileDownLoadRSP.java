package cn.zswltech.mithras.dto.file;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 文件下载
 *
 * @author wangchuanhao
 * @date 2023/2/6 10:32 AM
 */
@Data
public class FileDownLoadRSP extends FileListRSP {

    @ApiModelProperty("文件地址")
    private String fileUrl;

}
