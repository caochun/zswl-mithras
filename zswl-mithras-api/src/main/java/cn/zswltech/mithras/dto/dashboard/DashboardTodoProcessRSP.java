package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author dingqi
 * @date 2024/6/12
 * @description
 */
@Data
public class DashboardTodoProcessRSP {
    @ApiModelProperty("任务id")
    private String taskId;
    @ApiModelProperty("待发起id")
    private String prepareId;
    @ApiModelProperty("流程实例ID")
    private String processInstanceId;
    @ApiModelProperty("流程类型")
    private String processModelType;
    @ApiModelProperty("模型key")
    private String modelKey;
    @ApiModelProperty("业务标识")
    private String businessKey;
    @ApiModelProperty("表单名称")
    private String processName;
    @ApiModelProperty("发起人ID")
    private Long startUserId;
    @ApiModelProperty("发起人姓名")
    private String startUserName;
    @ApiModelProperty("发起人部门ID")
    private Long startUserDeptId;
    @ApiModelProperty("发起人部门名称")
    private String startUserDeptName;
    @ApiModelProperty("流程开始时间")
    private LocalDateTime processStartTime;
    @ApiModelProperty("流程结束时间")
    private LocalDateTime processEndTime;
    @ApiModelProperty("客户名称")
    private String clientName;
    @ApiModelProperty("流程对应的客户id")
    private Long clientId;
    @ApiModelProperty("项目名称")
    private String projName;
    @ApiModelProperty("项目编号")
    private String projCode;
    @ApiModelProperty("合同编号")
    private String contractCode;
    @ApiModelProperty("当前审批节点名称，多个用英文逗号分隔")
    private String curTaskNames;
    @ApiModelProperty("当前审批人ID，多个用英文逗号分隔")
    private String curAssigneeIds;
    @ApiModelProperty("审批人id")
    private Long assignee;
    @ApiModelProperty("审批人名称")
    private String assineeName;
    @ApiModelProperty("当前审批人名称，多个用英文逗号分隔")
    private String curAssigneeNames;
    @ApiModelProperty("类型")
    private String type;
    @ApiModelProperty("流程状态")
    private String processStatus;
    @ApiModelProperty("任务开始时间")
    private LocalDateTime taskCreateTime;
    @ApiModelProperty("任务结束时间（待办列表没有该字段）")
    private LocalDateTime taskEndTime;
    @ApiModelProperty("待办任务生成时间")
    private LocalDateTime applyTime;
}
