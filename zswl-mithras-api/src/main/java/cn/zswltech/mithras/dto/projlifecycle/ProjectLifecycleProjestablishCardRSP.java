package cn.zswltech.mithras.dto.projlifecycle;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @create: 2022-10-24
 **/

@Data
public class ProjectLifecycleProjestablishCardRSP {
    @ApiModelProperty("项目id")
    private Long projectId;

    @ApiModelProperty("审批状态")
    private String processStatus;

    @ApiModelProperty("审批类型")
    private String approvalType;

    @ApiModelProperty("创建时间")
    private LocalDate createTime;

    @ApiModelProperty("审批通过时间")
    private LocalDate approveTime;

    @ApiModelProperty("流程类型")
    private String processType;

    @ApiModelProperty("当前节点")
    private String currentNode;

    @ApiModelProperty("申请授信金额")
    private Long applyCreditAmount;

}
