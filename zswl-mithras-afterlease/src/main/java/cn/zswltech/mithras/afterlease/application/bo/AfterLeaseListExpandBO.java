package cn.zswltech.mithras.afterlease.application.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yangxiong
 * @date 2024/8/28/16:30
 * @description 租后台账列表扩展字段
 */
@Data
public class AfterLeaseListExpandBO {

    @ApiModelProperty(value = "计划id")
    private Long planId;

    @ApiModelProperty(value = "客户id")
    private Long clientId;

    @ApiModelProperty("项目主办ID")
    private Long projSponsorUserId;

    @ApiModelProperty("业务部门ID")
    private Long bizDeptId;

    @ApiModelProperty("协查风控经理")
    private Long riskControlManagerId;

    @ApiModelProperty("协查风控经理名字")
    private String riskControlManagerName;
}
