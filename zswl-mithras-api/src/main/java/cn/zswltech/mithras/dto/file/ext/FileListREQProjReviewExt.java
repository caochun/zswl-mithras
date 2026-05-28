package cn.zswltech.mithras.dto.file.ext;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 项目评审模块 文件列表 入参扩展
 *
 * @author wangchuanhao
 * @date 2023/2/6 1:44 PM
 */
@Data
public class FileListREQProjReviewExt {

    @ApiModelProperty("流程id")
    private String processInstanceId;

}
