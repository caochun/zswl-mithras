package cn.zswltech.mithras.dto.trackEvent;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TrackEventMainREQ extends TrackEventListREQ {

    @ApiModelProperty(value = "记录ID集合")
    private List<Long> ids;
//
//    @ApiModelProperty(value = "任务名称")
//    private String taskName;
//
//    @ApiModelProperty(value = "任务类型 枚举-TrackTaskTypeEnum")
//    private String taskType;
//
//    @ApiModelProperty(value = "合同编号")
//    private String contractCode;
//
//    @ApiModelProperty(value = "客户id")
//    private Long clientId;
//
//    @ApiModelProperty(value = "项目名称")
//    private String projName;
//
//    @ApiModelProperty(value = "任务状态")
//    private Boolean taskStatus;
//
//    @ApiModelProperty(value = "提出人")
//    private Long createBy;
//
//    @ApiModelProperty(value = "处理人")
//    private Long processorId;

}
