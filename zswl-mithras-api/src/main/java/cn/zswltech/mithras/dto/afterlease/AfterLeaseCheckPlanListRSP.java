package cn.zswltech.mithras.dto.afterlease;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2022/11/9
 * @description
 */
@Data
@ApiModel("租后管理-检查计划列表-返回体")
public class AfterLeaseCheckPlanListRSP {
    @ApiModelProperty("主键id")
    private Long id;

    @ApiModelProperty("计划名称")
    private String planName;

    @ApiModelProperty("计划类型")
    private String planType;

    @ApiModelProperty("年份")
    private Integer year;

    @ApiModelProperty("季度")
    private Integer quarter;

    @ApiModelProperty("月份")
    private Integer month;

    @ApiModelProperty("检查完毕项目数")
    private Integer finishCount;

    @ApiModelProperty("计划包含项目数")
    private Integer totalCount;

    @ApiModelProperty("检查开始时间")
    private String checkStartDate;

    @ApiModelProperty("检查结束时间")
    private String checkEndDate;

    @ApiModelProperty("计划状态")
    private String planStatus;

    @ApiModelProperty("审批状态")
    private String approvalStatus;

    @ApiModelProperty("创建时间")
    private String createTime;

    @ApiModelProperty("变更时间")
    private String updateTime;

    @ApiModelProperty("管理形式")
    private String checkWay;

    @ApiModelProperty("上次管理形式")
    private String lastCheckWay;

    @ApiModelProperty("截止时间")
    private LocalDate deadLine;


    @ApiModelProperty("检查计划中的客户记录id")
    private Long checkPlanClientId;

    private Boolean isAssetManager;

    @ApiModelProperty("风险敞口（万元）")
    private String riskExposure;

    @ApiModelProperty("剩余本金（万元）")
    private String remainingPrincipal;

    @ApiModelProperty("项目主办ID")
    private Long projSponsorUserId;

    @ApiModelProperty("项目主办名字")
    private String projSponsorUserName;

    @ApiModelProperty("业务部门ID")
    private Long bizDeptId;

    @ApiModelProperty("业务部门名称")
    private String bizDeptName;

    @ApiModelProperty("协查风控经理")
    private Long riskControlManagerId;

    @ApiModelProperty("协查风控经理名字")
    private String riskControlManagerName;

    @ApiModelProperty("当前审批人")
    private String curAssigneeNames;

}
