package cn.zswltech.mithras.dto.trackevent;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class TrackEventAddREQ{

    @ApiModelProperty(value = "任务名称")
    @NotNull(message = "任务名称不得为空")
    private String taskName;

    @ApiModelProperty(value = "任务类型 枚举-TrackTaskTypeEnum")
    @NotNull(message = "任务类型不得为空")
    private String taskType;

    @ApiModelProperty(value = "提出人")
    private Long createBy;

    @ApiModelProperty(value = "计划日期")
    private LocalDate planTime;

    @ApiModelProperty(value = "起租后X自然日")
    private Integer startRentAfterDay;

    @ApiModelProperty(value = "处理人id")
    @NotNull(message = "处理人不得为空")
    private Long processorId;

    @ApiModelProperty(value = "处理人岗位")
    private String processorDept;

    @ApiModelProperty(value = "提醒频率")
    @NotNull(message = "提醒频率不得为空")
    private String remindFrequency;

    @ApiModelProperty(value = "任务内容")
    @NotNull(message = "任务内容不得为空")
    private String taskContent;

    @ApiModelProperty(value = "业务id")
    private Long bizId;

    @ApiModelProperty(value = "业务来源")
    private String bizSource;

    @ApiModelProperty(value = "合同编号")
    private String contractCode;

    @ApiModelProperty(value = "客户id")
    private Long clientId;

    @ApiModelProperty(value = "项目名称")
    private String projName;

    @ApiModelProperty(value = "项目编号")
    private String projCode;

    @ApiModelProperty(value = "是否来自台账")
    private Boolean isLedger;

    @ApiModelProperty(value = "项目评审会议纪要ID")
    private Long projReviewMeetMinuteId;

}


