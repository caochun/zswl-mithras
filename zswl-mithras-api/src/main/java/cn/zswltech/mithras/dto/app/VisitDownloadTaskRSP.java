package cn.zswltech.mithras.dto.app;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author dingqi
 * @date 2025/9/18
 * @description
 */
@Data
public class VisitDownloadTaskRSP {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("用户名称")
    private String userName;

    @ApiModelProperty("任务状态")
    private String taskStatus;

    @ApiModelProperty("文件url")
    private String fileUrl;

    @ApiModelProperty("文件名称")
    private String fileName;

    @ApiModelProperty("任务结束时间")
    private LocalDateTime finishTime;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}
