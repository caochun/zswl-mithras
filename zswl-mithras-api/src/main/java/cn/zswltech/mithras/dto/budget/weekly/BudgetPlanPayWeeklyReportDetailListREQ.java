package cn.zswltech.mithras.dto.budget.weekly;

import cn.zswltech.mithras.dto.PageReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

/**
 * @description 预算管理-投放计划（月度）-项目周报-详情
 * @author vico
 * @date 2025-04-11
 */
@Data
@ApiModel("预算管理-投放计划（月度）-项目周报-详情列表-请求体")
public class BudgetPlanPayWeeklyReportDetailListREQ extends PageReq {


    @ApiModelProperty("项目周报id")
    private Long budgetPlanWeeklyReportId;

    @ApiModelProperty("部门id")
    private Long deptId;

    @ApiModelProperty("FTP行业分类")
    private String ftpIndustryCategory;

    @ApiModelProperty("租赁类型")
    private String leaseType;

    @ApiModelProperty("项目主办id")
    private Long sponsorUserId;

    @ApiModelProperty("投放日-起")
    private LocalDate planPayDateFrom;

    @ApiModelProperty("投放日-止")
    private LocalDate planPayDateTo;

}
