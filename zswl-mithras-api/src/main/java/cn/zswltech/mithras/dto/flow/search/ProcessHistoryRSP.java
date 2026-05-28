package cn.zswltech.mithras.dto.flow.search;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 流程列表
 *
 * @author wangchuanhao
 * @date 2022/6/22 10:51 PM
 */
@Data
@ApiModel("流程运行历史操作")
public class ProcessHistoryRSP {

    /**
     * 操作类型
     * @see cn.zswltech.flow.core.enums.CommentTypeEnum
     */
    @ApiModelProperty("操作类型")
    private String type;

    @ApiModelProperty("操作类型中文")
    private String typeName;

    /**
     * 操作描述
     */
    @ApiModelProperty("操作备注")
    private String message;

    /**
     * 操作时间
     */
    @ApiModelProperty("操作时间")
    private LocalDateTime operateTime;

    @ApiModelProperty("任务节点名称")
    private String taskNodeName;

    @ApiModelProperty("任务节点id")
    private String taskActivityId;

    /**
     * 操作人id
     */
    @ApiModelProperty("操作人id")
    private Long operatorId;

    @ApiModelProperty("操作人名称")
    private String operatorName;

}
