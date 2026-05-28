package cn.zswltech.mithras.dto.file.template;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author yibin
 */
@Data
public class FileTemplateHistoryListRSP {
    @ApiModelProperty("历史记录id")
    private Long id;

    @ApiModelProperty("文件id")
    private Long fileId;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("创建人id")
    private Long createBy;

    @ApiModelProperty("创建人")
    private String createByName;
}
