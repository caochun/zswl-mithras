package cn.zswltech.mithras.dto.process.prepare;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author luyi
 */
@Data
public class ProcessPrepareListRSP {
    private Long id;

    @ApiModelProperty("流程类型")
    private String processType;

    @ApiModelProperty("业务ID")
    private String businessId;

    private String processTypeName;

    @ApiModelProperty("表单名称")
    private String formName;

    @ApiModelProperty("项目名称")
    private String projName;

    @ApiModelProperty("项目编号")
    private String projCode;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("当前节点")
    private String currentNode;

    private String currentAssignee;
    private Integer overtimeFlag;

    @ApiModelProperty("当前审批人")
    private String currentAssigneeNames;

    @ApiModelProperty("申请时间")
    private LocalDateTime applyTime;

    @ApiModelProperty("资产管理岗确认")
    private String isAssetConfirm;
}
