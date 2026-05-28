package cn.zswltech.mithras.dto.managereport;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author dingqi
 * @date 2024/12/10
 * @description
 */
@Data
public class YeWuYunXingFenXiDetailRSP {
    @ApiModelProperty("流程实例ID")
    private String processInstanceId;
    @ApiModelProperty("流程模型类型")
    private String processModelType;
    @ApiModelProperty("项目阶段")
    private String projectStage;
    @ApiModelProperty("租赁类型")
    private String leaseTypes;
    @ApiModelProperty("租赁类型-展示")
    private String leaseTypesDisplay;
    @ApiModelProperty("客户风控行业分类")
    private String clientRiskControlIndustryClassify;
    @ApiModelProperty("业务类型")
    private String businessCategory;
    @ApiModelProperty("项目编号")
    private String projCode;
    @ApiModelProperty("项目名称")
    private String projName;
    @ApiModelProperty("合同编号")
    private String contractCode;
    @ApiModelProperty("项目金额（毫厘）")
    private Long projAmount;
    @ApiModelProperty("业务部门ID")
    private Long bizDeptId;
    @ApiModelProperty("业务部门名称")
    private String bizDeptName;
    @ApiModelProperty("项目主办ID")
    private Long projSponsorUserId;
    @ApiModelProperty("项目主办名称")
    private String projSponsorUserName;
    @ApiModelProperty("流程状态")
    private Integer processStatus;
    @ApiModelProperty("流程状态-展示")
    private String processStatusDisplay;
    @ApiModelProperty("流程开始时间")
    private String processStartTimeStr;
    @ApiModelProperty("流程结束时间")
    private String processEndTimeStr;
    @ApiModelProperty("流程结束时间所属月份")
    private String processEndTimeMonth;
    @ApiModelProperty("流程结束时间所属年份")
    private String processEndTimeYear;
    @ApiModelProperty("投放日期")
    private String minPayDateStr;
    @ApiModelProperty("投放日期所属月份")
    private String minPayDateMonth;
    @ApiModelProperty("投放金额（毫厘）")
    private Long actualPayAmount;
}
