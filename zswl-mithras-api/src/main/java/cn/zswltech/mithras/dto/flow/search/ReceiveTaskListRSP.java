package cn.zswltech.mithras.dto.flow.search;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 我收到的 待审批 已审批 任务列表
 *
 * @author wangchuanhao
 * @date 2022/10/10 11:26 AM
 */
@Data
public class ReceiveTaskListRSP extends TaskListRSP {

    /**
     * 当前流程任务id 以,分割
     */
    @ApiModelProperty("流程当前进行中任务id")
    private String curTaskIds;

    /**
     * 当前任务节点id 以,分割 去重
     */
    @ApiModelProperty("流程当前所处节点id")
    private String curTaskActivityIds;

    /**
     * 当前任务节点id 以,分割 去重
     */
    @ApiModelProperty("流程当前所处节点名称")
    private String curTaskNames;

    /**
     * 当前审批人id 以,分割 去重
     */
    @ApiModelProperty("流程当前所处节点审批人id")
    private String curAssigneeIds;

    /**
     * 当前审批人id 以,分割 去重
     */
    @ApiModelProperty("流程当前所处节点审批人名称")
    private String curAssigneeNames;

    /**
     * 是否超时，1超时，0未超时
     */
    @ApiModelProperty("是否超时，1超时，0未超时")
    private Integer overtimeFlag;

    @ApiModelProperty("所属部门id")
    private Long belongDeptId;

    @ApiModelProperty("所属部门名称")
    private String belongDeptName;
}
