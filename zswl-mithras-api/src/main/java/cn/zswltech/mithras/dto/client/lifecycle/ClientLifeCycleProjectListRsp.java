package cn.zswltech.mithras.dto.client.lifecycle;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/6/25 14:46
 */
@ApiModel("客户全周期项目列表返回")
@Data
public class ClientLifeCycleProjectListRsp {

    @ApiModelProperty("立项id")
    private Long projectId;

    @ApiModelProperty("评审id")
    private Long projReviewId;

    @ApiModelProperty("项目名称")
    private String projectName;

    @ApiModelProperty("申报授信金额")
    private Long applyCreditAmount;

    @ApiModelProperty("合同金额")
    private Long contractAmount;

    @ApiModelProperty("业务类型, 取枚举projectBizType")
    private String bizType;

    @ApiModelProperty("项目阶段，取枚举projStageEnum")
    private String projStage;

    @ApiModelProperty
    private String dataType;

    @ApiModelProperty("项目编号")
    private String projectCode;

    @ApiModelProperty("客户id")
    private Long clientId;
}
