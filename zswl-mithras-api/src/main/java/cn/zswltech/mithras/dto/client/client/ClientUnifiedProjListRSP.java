package cn.zswltech.mithras.dto.client.client;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class ClientUnifiedProjListRSP {

    private Long id;

    @ApiModelProperty("项目名称")
    private String projName;

    @ApiModelProperty("授信金额")
    private Long applyCreditAmount;

    @ApiModelProperty("项目阶段 DashboardCardGroupEnum")
    private String projectStage;

    @ApiModelProperty("所属部门")
    private Long belongDeptId;

    @ApiModelProperty("所属部门名称")
    private String belongDeptName;

    @ApiModelProperty("业务类型")
    private String bizType;

}
