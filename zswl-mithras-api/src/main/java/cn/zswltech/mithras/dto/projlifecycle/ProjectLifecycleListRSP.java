package cn.zswltech.mithras.dto.projlifecycle;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @create: 2022-10-21
 **/

@Data
public class ProjectLifecycleListRSP {
   /* @ApiModelProperty("项目id")
    private Long projectId;*/

    @ApiModelProperty("立项id")
    private Long establishId;

    @ApiModelProperty("评审id")
    private Long reviewId;

  /*  @ApiModelProperty("key")
    private String key;*/

    @ApiModelProperty("项目名称")
    private String projectName;

    @ApiModelProperty("客户Id")
    private Long clientId;

    @ApiModelProperty("客户类型")
    private String clientType;

    @ApiModelProperty("境内or境外")
    private String domesticOrAbroad;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("业务类型")
    private String bizType;

    @ApiModelProperty("项目阶段")
    private String projStage;

    @ApiModelProperty("项目主办")
    private Long projSponsorUserId;

    @ApiModelProperty("项目主办名")
    private String projSponsorUserName;

    @ApiModelProperty("立项时间")
    private LocalDate projestablishTime;

    @ApiModelProperty("业务部门")
    private String bizDeptName;

    @ApiModelProperty("数据类型")
    private String dataType;

    @ApiModelProperty("流程类型")
    private String processType;

    @ApiModelProperty("当前节点")
    private String currentNode;

    @ApiModelProperty("申请时间")
    private LocalDate applyTime;

    @ApiModelProperty("申请授信金额")
    private Long applyCreditAmount;
    @ApiModelProperty("申请授信金额")
    private Long establishApplyCreditAmount;

    @ApiModelProperty("立项状态")
    private String establishStatus;
    @ApiModelProperty("立项流程状态")
    private String establishProcessStatus;
    @ApiModelProperty("评审状态")
    private String reviewStatus;
    @ApiModelProperty("评审流程状态")
    private String reviewProcessStatus;


    //
    @ApiModelProperty("合同金额-已申请")
    private Long contractAmountApplied;
    @ApiModelProperty("合同金额-已生效")
    private Long contractAmountEffected;
    @ApiModelProperty("付款金额-已申请")
    private Long paymentAmountApplied;
    @ApiModelProperty("付款金额-已生效")
    private Long paymentAmountEffected;
    @ApiModelProperty("付款金额-已核销")
    private Long paymentAmountWrittenOff;
    @ApiModelProperty("剩余本金")
    private Long remainingPrincipal;
    @ApiModelProperty("项目阶段状态")
    private String projLifecycleStatus;


}
