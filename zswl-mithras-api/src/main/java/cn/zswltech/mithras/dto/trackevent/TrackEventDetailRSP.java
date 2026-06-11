package cn.zswltech.mithras.dto.trackevent;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class TrackEventDetailRSP extends TrackEventListRSP{

    @ApiModelProperty(value = "合同")
    TrackEventContractInfoRSP trackEventContractInfo;

    @ApiModelProperty(value = "跟踪任务id")
    private Long id;

    @ApiModelProperty(value = "任务名称")
    private String taskName;

    @ApiModelProperty(value = "任务类型 枚举-TrackTaskTypeEnum")
    private String taskType;

    @ApiModelProperty(value = "提出人")
    private Long createBy;

    @ApiModelProperty(value = "提出人名称")
    private String createByName;

    @ApiModelProperty(value = "计划日期")
    private LocalDate planTime;

    @ApiModelProperty(value = "起租后X自然日")
    private Integer startRentAfterDay;

    @ApiModelProperty(value = "处理人id")
    private Long processorId;

    @ApiModelProperty(value = "处理人名称")
    private String processor;

    @ApiModelProperty(value = "处理人岗位")
    private String processorDept;

    @ApiModelProperty(value = "提醒频率 枚举-TrackFrequencyEnum")
    private String remindFrequency;

    @ApiModelProperty(value = "任务内容")
    private String taskContent;

    @ApiModelProperty(value = "任务状态")
    private Boolean taskStatus;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

}


