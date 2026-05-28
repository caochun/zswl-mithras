package cn.zswltech.mithras.dto.archives;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @create: 2023-02-23
 **/
@Data
public class ArchiveTemplateListRSP {
    @ApiModelProperty("模版id")
    private Long templateId;

    @ApiModelProperty("模版名称")
    private String templateName;

    @ApiModelProperty("业务类型")
    private List<String> bizType;

    @ApiModelProperty("模版状态")
    private String status;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
}
