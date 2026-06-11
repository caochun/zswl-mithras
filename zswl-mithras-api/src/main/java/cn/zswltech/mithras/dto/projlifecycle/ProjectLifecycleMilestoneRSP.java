package cn.zswltech.mithras.dto.projlifecycle;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @create: 2022-10-24
 **/

@Data
public class ProjectLifecycleMilestoneRSP {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("事件时间")
    private LocalDateTime eventTime;

    @ApiModelProperty("事件类型")
    private String eventType;

    @ApiModelProperty("事件")
    private String event;

    @ApiModelProperty("操作人")
    private String operator;

    @ApiModelProperty("事件描述")
    private String eventdesc;
}
