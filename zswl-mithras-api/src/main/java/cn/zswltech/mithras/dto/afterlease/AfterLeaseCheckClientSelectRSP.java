package cn.zswltech.mithras.dto.afterlease;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dingqi
 * @date 2022/11/18
 * @description
 */
@Data
@ApiModel("租后检查计划-非季度计划选择项目（更改为客户）-返回体")
public class AfterLeaseCheckClientSelectRSP {
//    @ApiModelProperty("项目id")
//    private Long projectId;
//
//    @ApiModelProperty("项目名称")
//    private String projectName;
//
//    @ApiModelProperty("项目编号")
//    private String projectCode;
//
//    @ApiModelProperty("业务类型")
//    private String bizType;
//
//    @ApiModelProperty("主办id")
//    private Long projectSponsorId;
//
//    @ApiModelProperty("主办名称")
//    private String projectSponsorName;

    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("客户类型")
    private String clientType;

    @ApiModelProperty("客户编号")
    private String clientCode;

    @ApiModelProperty("主办id")
    private Long sponsorId;

    @ApiModelProperty("主办名称")
    private String sponsorName;

    @ApiModelProperty("业务部门id")
    private Long bizDeptId;

    @ApiModelProperty("业务部门名称")
    private String bizDeptName;

    @ApiModelProperty("是否已被选择")
    private Boolean isSelected;
}
