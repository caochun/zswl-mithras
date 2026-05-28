package cn.zswltech.mithras.dto.afterlease;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author dingqi
 * @date 2022/11/10
 * @description
 */
@Data
@ApiModel("租后管理-保存检查计划需检查客户-请求体")
public class AfterLeaseCheckClientSaveREQ {
    @NotNull(message = "检查计划id不能为空")
    @ApiModelProperty("检查计划id")
    private Long planId;

    @ApiModelProperty("id")
    private Long id;

//    @NotNull(message = "项目id不能为空")
//    @ApiModelProperty("项目id")
//    private Long projectId;

    @NotNull(message = "项目主办不能为空")
    @ApiModelProperty("项目主办id")
    private Long sponsorUserId;

    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty("检查形式 参考枚举 AfterLeaseCheckWayEnum")
    private String checkWay;

    @NotNull(message = "是否检查不能为空")
    @ApiModelProperty("本次是否需要检查，true-需要，false-不需要")
    private Boolean check;

    @ApiModelProperty("协查风控经理id")
    private Long riskManagerId;

    @ApiModelProperty("协查风控经理名称")
    private String riskManagerName;

    @ApiModelProperty("模板类型")
    private String reportType;
}
