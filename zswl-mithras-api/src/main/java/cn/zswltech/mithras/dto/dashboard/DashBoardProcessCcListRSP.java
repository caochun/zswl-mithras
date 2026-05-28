package cn.zswltech.mithras.dto.dashboard;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 抄送消息列表
 *
 * @author wangchuanhao
 * @date 2022/8/1 10:19 AM
 */
@Data
public class DashBoardProcessCcListRSP {

    @ApiModelProperty("抄送消息id")
    private Long id;

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

    @ApiModelProperty("流程名称")
    private String processName;

    @ApiModelProperty("发起人部门id")
    private Long startUserDeptId;

    @ApiModelProperty("发起人部门名称")
    private String startUserDeptName;

    @ApiModelProperty("抄送人id")
    private Long senderId;

    @ApiModelProperty("抄送人名称")
    private String senderName;

    @ApiModelProperty("是否已读，1已读，0未读")
    private Integer readFlag;

    @ApiModelProperty("二级子模块（法人自然人、租赁转租赁保理债权转让）")
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
