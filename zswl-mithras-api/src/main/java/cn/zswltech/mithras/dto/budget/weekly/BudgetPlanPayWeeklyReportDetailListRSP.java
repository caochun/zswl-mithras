package cn.zswltech.mithras.dto.budget.weekly;

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
@ApiModel("预算管理-投放计划（月度）-项目周报-详情列表-返回体")
public class BudgetPlanPayWeeklyReportDetailListRSP {
    @ApiModelProperty("投放计划明细id")
    private Long id;

    @ApiModelProperty("最后一次操作人id")
    private Long lastOperateUserId;

    @ApiModelProperty("预算计划id")
    private Long budgetPlanId;

    @ApiModelProperty("投放计划id")
    private Long budgetPlanPayId;

    @ApiModelProperty("项目评审id")
    private Long projReviewId;

    @ApiModelProperty("项目编号")
    private String projCode;

    @ApiModelProperty("客户id")
    private Long clientId;

    @ApiModelProperty("客户名称")
    private String clientName;

    @ApiModelProperty("关联合同数量")
    private Integer contractCount;

    @ApiModelProperty("业务来源")
    private String projSource;

    @ApiModelProperty("ftp行业分类")
    private String ftpIndustryCategory;

    @ApiModelProperty("风控行业分类")
    private String riskControlIndustryClassify;

    @ApiModelProperty("业务类型（租赁类型）")
    private String leaseType;

    @ApiModelProperty("项目主办id")
    private Long sponsorUserId;

    @ApiModelProperty("项目主办名称")
    private String sponsorUserName;

    @ApiModelProperty("业务部门id")
    private Long belongDeptId;

    @ApiModelProperty("业务部门名称")
    private String belongDeptName;

    @ApiModelProperty("评估主体id")
    private Long evaluationSubjectId;

    @ApiModelProperty("省份")
    private String province;

    @ApiModelProperty("省份名称")
    private String provinceName;

    @ApiModelProperty("城市")
    private String city;

    @ApiModelProperty("城市名称")
    private String cityName;

    @ApiModelProperty("区、县")
    private String district;

    @ApiModelProperty("区、县名称")
    private String districtName;

    @ApiModelProperty("授信金额")
    private Long creditAmount;

    @ApiModelProperty("计划投放日")
    private LocalDate planPayDate;

    @ApiModelProperty("已投放金额")
    private Long paidAmount;

    @ApiModelProperty("拟投放金额")
    private Long planPayAmount;

    @ApiModelProperty("期限")
    private Integer termMonth;

    @ApiModelProperty("项目保证金")
    private Long deposit;

    @ApiModelProperty("项目手续费")
    private Long consultingFee;

    @ApiModelProperty("合同利率")
    private Integer contractInterestRate;

    @ApiModelProperty("irr")
    private Integer irr;

    @ApiModelProperty("ftp")
    private Integer ftp;

    /**
     * IRR-FTP差值
     */
    @ApiModelProperty("RR-FTP差值")
    private Integer irrFtpDiff;

    @ApiModelProperty("本周项目计划节点")
    private String planActionThisWeek;

    @ApiModelProperty("本周项目实际节点")
    private String actualActionThisWeek;

    @ApiModelProperty("下周项目计划节点")
    private String planActionNextWeek;

    @ApiModelProperty("项目进展状态")
    private String projectProgress;

    @ApiModelProperty("系统流程阶段")
    private String rzyProcessStage;

    /**
     * 运营进度反馈
     */
    @ApiModelProperty("运营进度反馈")
    private String yunyingFeedback;

    @ApiModelProperty("运营优先级")
    private Integer yunyingPriority;

    @ApiModelProperty("是否纳入资金计划")
    private String bringIntoFundPlan;

    @ApiModelProperty("意见及反馈")
    private String suggestion;

}
