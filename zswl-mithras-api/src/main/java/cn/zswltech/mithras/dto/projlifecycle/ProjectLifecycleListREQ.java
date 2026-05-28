package cn.zswltech.mithras.dto.projlifecycle;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @create: 2022-10-21
 **/

@Data
public class ProjectLifecycleListREQ extends PageReq {

    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty("项目名称")
    private String projName;

    @ApiModelProperty("业务类型")
    private String bizType;

    @ApiModelProperty("项目阶段")
    private String projStage;

    @ApiModelProperty("业务部门")
    private Long bizDeptId;

    @ApiModelProperty("项目主办")
    private Long projSponsorUserId;

    @ApiModelProperty("立项时间从")
    private LocalDateTime projestablishFrom;

    @ApiModelProperty("立项时间到")
    private LocalDateTime projestablishTo;

    @Deprecated
    @ApiModelProperty("资金已投放")
    private Integer invested;

    @Deprecated
    @ApiModelProperty("合同已生效")
    private Integer effected;

    @ApiModelProperty("立项状态")
    private String establishStatus;
    @ApiModelProperty("立项流程状态")
    private String establishProcessStatus;
    @ApiModelProperty("评审状态")
    private String reviewStatus;
    @ApiModelProperty("评审流程状态")
    private String reviewProcessStatus;
    //
    @ApiModelProperty("项目阶段状态")
    private String projLifecycleStatus;

    //非前段传入字段
    private List<Long> deptIdList;
    private Long sponsorId;

}
