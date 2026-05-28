package cn.zswltech.mithras.dto.afterlease;

import cn.zswltech.mithras.dto.ListBaseRSP;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2022/11/14
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel("租后管理-租后检查计划检查项目（更改为客户）列表-返回体")
public class AfterLeaseCheckClientListRSP extends ListBaseRSP {
    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("客户类型")
    private String clientType;

    @ApiModelProperty("客户编号")
    private String clientCode;

    @ApiModelProperty("业务部门id")
    private Long bizDeptId;

    @ApiModelProperty("业务部门名称")
    private String bizDeptName;

    @ApiModelProperty("主办id")
    private Long sponsorId;

    @ApiModelProperty("主办名称")
    private String sponsorName;


//    @ApiModelProperty("项目立项id")
//    private Long projectEstablishId;
//
//    @ApiModelProperty("项目评审id")
//    private Long projectReviewId;
//
//    @ApiModelProperty("项目数据类型")
//    private String dataType;
//
//    @ApiModelProperty("项目编号")
//    private String projectCode;
//
//    @ApiModelProperty("项目名称")
//    private String projectName;
//
//    @ApiModelProperty("项目类型")
//    private String projectType;
//
//    @ApiModelProperty("业务类型")
//    private String bizType;
//
//    @ApiModelProperty("项目主办id")
//    private Long projectSponsorId;
//
//    @ApiModelProperty("项目主办名称")
//    private String projectSponsorName;

    @ApiModelProperty("是否检查")
    private Boolean check;

    @ApiModelProperty("检查方式")
    private String checkWay;

    @ApiModelProperty("协查风控经理id")
    private Long riskManagerId;

    @ApiModelProperty("协查风控经理名称")
    private String riskManagerName;

    @ApiModelProperty("检查报告类型")
    private String reportType;

    @ApiModelProperty("审批状态")
    private String approvalStatus;

    @ApiModelProperty("检查时间")
    private LocalDate checkTime;
}
