package cn.zswltech.mithras.budget.mapper.model;
import cn.zswltech.mithras.foundation.persistence.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @description 预算管理-投放计划（月度）-项目周报-详情
 * @author vico
 * @date 2025-04-11
 */
@Data
public class BudgetPlanPayWeeklyReportDetail extends BaseModelWithLogicDelete implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 最后一次操作人id
     */
    @TableField("last_operate_user_id")
    private Long lastOperateUserId;

    /**
     * 预算计划id
     */
    @TableField("budget_plan_id")
    private Long budgetPlanId;

    /**
     * 投放计划id
     */
    @TableField("budget_plan_pay_id")
    private Long budgetPlanPayId;

    /**
     * 项目周报id
     */
    @TableField("budget_plan_weekly_report_id")
    private Long budgetPlanWeeklyReportId;

    /**
     * 项目评审id
     */
    @TableField("proj_review_id")
    private Long projReviewId;

    /**
     * 项目编号
     */
    @TableField("proj_code")
    private String projCode;

    /**
     * 客户id
     */
    @TableField("client_id")
    private Long clientId;

    /**
     * 客户名称
     */
    @TableField("client_name")
    private String clientName;

    /**
     * 业务来源
     */
    @TableField("proj_source")
    private String projSource;

    /**
     * ftp行业分类
     */
    @TableField("ftp_industry_category")
    private String ftpIndustryCategory;

    /**
     * 风控行业分类
     */
    @TableField("risk_control_industry_classify")
    private String riskControlIndustryClassify;

    /**
     * 业务类型（租赁类型）
     */
    @TableField("lease_type")
    private String leaseType;

    /**
     * 项目主办
     */
    @TableField("sponsor_user_id")
    private Long sponsorUserId;

    /**
     * 业务负责人
     */
    @TableField("biz_dept_leader_id")
    private Long bizDeptLeaderId;

    /**
     * 业务部门
     */
    @TableField("belong_dept_id")
    private Long belongDeptId;

    /**
     * 评估主体id
     */
    @TableField("evaluation_subject_id")
    private Long evaluationSubjectId;

    /**
     * 国家
     */
    @TableField("country")
    private String country;

    /**
     * 省份
     */
    @TableField("province")
    private String province;

    /**
     * 城市
     */
    @TableField("city")
    private String city;

    /**
     * 区、县
     */
    @TableField("district")
    private String district;

    /**
     * 授信金额
     */
    @TableField("credit_amount")
    private Long creditAmount;

    /**
     * 计划投放日
     */
    @TableField("plan_pay_date")
    private LocalDate planPayDate;

    /**
     * 已投放金额
     */
    @TableField("paid_amount")
    private Long paidAmount;

    /**
     * 拟投放金额
     */
    @TableField("plan_pay_amount")
    private Long planPayAmount;

    /**
     * 期限
     */
    @TableField("term_month")
    private Integer termMonth;

    /**
     * 项目保证金
     */
    @TableField("deposit")
    private Long deposit;

    /**
     * 咨询服务费
     */
    @TableField("consulting_fee")
    private Long consultingFee;

    /**
     * 合同利率
     */
    @TableField("contract_interest_rate")
    private Integer contractInterestRate;

    /**
     * irr
     */
    @TableField("irr")
    private Integer irr;

    /**
     * xirr
     */
    @TableField("xirr")
    private Double xirr;

    /**
     * ftp
     */
    @TableField("ftp")
    private Integer ftp;

    /**
     * IRR-FTP差值
     */
    @TableField("irr_ftp_diff")
    private Integer irrFtpDiff;

    /**
     * 本周项目计划节点
     */
    @TableField("plan_action_this_week")
    private String planActionThisWeek;

    /**
     * 本周项目实际节点
     */
    @TableField("actual_action_this_week")
    private String actualActionThisWeek;

    /**
     * 下周项目计划节点
     */
    @TableField("plan_action_next_week")
    private String planActionNextWeek;

    /**
     * 项目进展状态
     */
    @TableField("project_progress")
    private String projectProgress;

    /**
     * 系统流程阶段
     */
    @TableField("rzy_process_stage")
    private String rzyProcessStage;

    /**
     * 运营进度反馈
     */
    @TableField("yunying_feedback")
    private String yunyingFeedback;

    /**
     * 运营优先级
     */
    @TableField("yunying_priority")
    private Integer yunyingPriority;

    /**
     * 是否纳入资金计划
     */
    @TableField("bring_into_fund_plan")
    private String bringIntoFundPlan;

    /**
     * 意见及反馈
     */
    @TableField("suggestion")
    private String suggestion;

    /**
     * 业务负责人是否确认
     */
    @TableField("is_businesshead_confirm")
    private Integer isBusinessheadConfirm;

    /**
     * 业务负责人确认时间
     */
    @TableField("businesshead_confirm_date")
    private LocalDate businessheadConfirmDate;

    /**
     * 分管领导是否确认
     */
    @TableField("is_leaderincharge_confirm")
    private Integer isLeaderinchargeConfirm;


    /**
     * 最后审批人是否确认
     */
    @TableField("is_last_confirm")
    private Integer isLastConfirm;

    @Override
    public void reset() {
        super.reset();
        this.id = null;
    }

}
