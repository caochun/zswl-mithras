package cn.zswltech.mithras.dto.file.ext;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 租后调整
 *
 * @author wangchuanhao
 * @date 2023/2/6 2:06 PM
 */
@Data
public class FileListREQAdjustExt {

    @ApiModelProperty("流程id")
    private String processInstanceId;

}
