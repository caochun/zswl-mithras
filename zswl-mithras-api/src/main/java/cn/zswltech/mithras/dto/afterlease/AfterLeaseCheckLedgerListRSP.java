package cn.zswltech.mithras.dto.afterlease;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: heng
 * @Date: 2025/12/3 17:17
 */
@Data
@ApiModel("租后管理-检查台账列表-返回体")
public class AfterLeaseCheckLedgerListRSP {
    @ApiModelProperty("主键id")
    private Long id;

    @ApiModelProperty("计划Id")
    private Long checkPlanClientId;

    @ApiModelProperty("计划名称")
    private String planName;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("计划类型")
    private String planType;

    @ApiModelProperty("项目主办ID")
    private Long belongSponsorId;

    @ApiModelProperty("项目主办名字")
    private String belongSponsorUserName;

    @ApiModelProperty("业务部门ID")
    private Long belongDeptId;

    @ApiModelProperty("业务部门名称")
    private String belongDeptName;

    @ApiModelProperty("协查风控经理")
    private Long riskManagerId;

    @ApiModelProperty("协查风控经理名字")
    private String riskManagerName;

    @ApiModelProperty("检查形式")
    private String checkWay;

    @ApiModelProperty("检查报告模板")
    private String reportType;

    @ApiModelProperty("租后截止日期")
    private String deadLine;

    @ApiModelProperty("现场检查日期")
    private String checkTime;

    @ApiModelProperty("报告提交日期")
    private String commitTime;

    @ApiModelProperty("当前状态")
    private String checkStatus;

    @ApiModelProperty("是否逾期")
    private String overdue;

    @ApiModelProperty("逾期天数")
    private String overdueDays;

}
