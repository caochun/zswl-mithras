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
@ApiModel("流程列表数据")
public class ProcessListRSP {

    @ApiModelProperty("流程实例id")
    private String processInstanceId;

    /**
     * 业务标识
     */
    @ApiModelProperty("业务主键")
    private String businessKey;

    /**
     * 流程模型名称
     */
    @ApiModelProperty("流程模型名称")
    private String modelName;

    /**
     * 流程状态
     */
    @ApiModelProperty("流程状态,枚举processStatus")
    private String processStatus;

    /**
     * 模型key
     */
    @ApiModelProperty("模型key")
    private String modelKey;

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
     * 流程开始时间
     */
    @ApiModelProperty("流程开始时间")
    private LocalDateTime startTime;

    /**
     * 流程结束时间
     */
    @ApiModelProperty("流程结束时间（未结束则没有该字段）")
    private LocalDateTime endTime;

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


    @ApiModelProperty("流程名称")
    private String processName;

    @ApiModelProperty("发起人部门id")
    private Long startUserDeptId;

    @ApiModelProperty("发起人部门名称")
    private String startUserDeptName;

    @ApiModelProperty("最后审批人id")
    private Long finalAssigneeId;

    @ApiModelProperty("最后审批人名称")
    private String finalAssigneeName;

    @ApiModelProperty("二级模块枚举（法人自然人、租赁转租赁保理债权转让）")
    private String subModule;

    @ApiModelProperty("主模块枚举（客户管理、立项管理）")
    private String mainModule;

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
