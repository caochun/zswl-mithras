package cn.zswltech.mithras.dto.projlifecycle;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @create: 2022-10-26
 **/

@Data
public class ProjectLifecycleEventREQ extends PageReq {
    @ApiModelProperty("项目id")
    private Long projectId;

    @ApiModelProperty("立项id")
    private Long establishId;

    @ApiModelProperty("评审id")
    private Long reviewId;

    @ApiModelProperty("事件时间从")
    private LocalDate eventTimeFrom;

    @ApiModelProperty("事件时间到")
    private LocalDate eventTimeTo;

    @ApiModelProperty("事件类型")
    private String eventType;

    @ApiModelProperty("数据类型")
    private String dataType;
}
