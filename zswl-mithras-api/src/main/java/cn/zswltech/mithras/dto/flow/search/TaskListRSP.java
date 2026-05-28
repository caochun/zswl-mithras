package cn.zswltech.mithras.dto.flow.search;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 任务
 *
 * @author wangchuanhao
 * @date 2022/6/22 11:09 PM
 */
@ApiModel("任务列表数据")
@Data
public class TaskListRSP {

    /**
     * 任务id
     */
    @ApiModelProperty("任务id")
    private String taskId;

    /**
     * 流程实例id
     */
    @ApiModelProperty("流程实例id")
    private String processInstanceId;

    /**
     * 流程模型名称
     */
    @ApiModelProperty("流程模型名称")
    private String modelName;

    /**
     * 模型key
     */
    @ApiModelProperty("模型key")
    private String modelKey;

    /**
     * 任务节点 定义id
     */
    @ApiModelProperty("任务节点id")
    private String taskActivityId;

    /**
     * 任务节点名称
     */
    @ApiModelProperty("任务节点名称")
    private String taskName;

    /**
     * 任务状态
     */
    @ApiModelProperty("任务状态,枚举taskStatus")
    private String taskStatus;

    /**
     * 流程状态
     */
    @ApiModelProperty("流程状态,枚举processStatus")
    private String processStatus;

    /**
     * 审批人id
     */
    @ApiModelProperty("审批人id")
    private Long assignee;

    /**
     * 审批人名称
     */
    @ApiModelProperty("审批人名称")
    private String assineeName;

    /**
     * 流程发起人id
     */
    @ApiModelProperty("流程发起人id")
    private Long startUserId;

    /**
     * 流程发起人名称
     */
    @ApiModelProperty("流程发起人名称")
    private String startUserName;

    /**
     * 任务开始时间
     */
    @ApiModelProperty("任务开始时间")
    private LocalDateTime taskCreateTime;

    /**
     * 任务结束时间
     */
    @ApiModelProperty("任务结束时间（待办列表没有该字段）")
    private LocalDateTime taskEndTime;

    /**
     * 业务标识
     */
    @ApiModelProperty("业务标识")
    private String businessKey;

    @ApiModelProperty("流程开始时间")
    private LocalDateTime processStartTime;

    @ApiModelProperty("流程名称")
    private String processName;

    @ApiModelProperty("发起人部门id")
    private Long startUserDeptId;

    @ApiModelProperty("发起人部门名称")
    private String startUserDeptName;

    @ApiModelProperty("二级模块枚举（法人自然人、租赁转租赁保理债权转让）")
    private String subModule;

    @ApiModelProperty("主模块枚举（客户管理、立项管理）")
    private String mainModule;

    @ApiModelProperty("流程定义id")
    private String processDefineId;

    @ApiModelProperty("流程对应的客户id")
    private Long clientId;

    @ApiModelProperty("流程对应的客户名称")
    private String clientName;

    @ApiModelProperty("项目名称")
    private String projName;

    @ApiModelProperty("项目编号")
    private String projCode;

    @ApiModelProperty("合同编号")
    private String contractCode;

}
