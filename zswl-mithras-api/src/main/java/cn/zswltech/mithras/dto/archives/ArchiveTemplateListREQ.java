package cn.zswltech.mithras.dto.archives;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @create: 2023-02-23
 **/

@Data
public class ArchiveTemplateListREQ extends PageReq {

    @ApiModelProperty("模版名称")
    private String templateName;

    @ApiModelProperty("业务类型")
    private String bizType;

    @ApiModelProperty("模版状态")
    private String status;

    @ApiModelProperty("创建时间从")
    private LocalDateTime createTimeFrom;

    @ApiModelProperty("创建时间到")
    private LocalDateTime createTimeTo;

    @ApiModelProperty("更新时间从")
    private LocalDateTime updateTimeFrom;

    @ApiModelProperty("更新时间到")
    private LocalDateTime updateTimeTo;
}
