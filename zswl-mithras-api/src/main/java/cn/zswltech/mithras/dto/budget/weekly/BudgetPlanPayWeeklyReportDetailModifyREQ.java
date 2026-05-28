package cn.zswltech.mithras.dto.budget.weekly;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
/**
 * @description 预算管理-投放计划（月度）-项目周报-详情
 * @author vico
 * @date 2025-04-11
 */
@Data
@ApiModel("预算管理-投放计划（月度）-项目周报-详情编辑-请求体")
public class BudgetPlanPayWeeklyReportDetailModifyREQ {

    /**
    * 主键id
    */
    @ApiModelProperty(value = "主键id")
    private Long id;

    /**
    * 业务来源
    */
    @ApiModelProperty(value = "业务来源")
    private String projSource;

    /**
    * ftp行业分类
    */
    @ApiModelProperty(value = "ftp行业分类")
    private String ftpIndustryCategory;

    /**
    * 风控行业分类
    */
    @ApiModelProperty(value = "风控行业分类")
    private String riskControlIndustryClassify;

    /**
    * 业务类型（租赁类型）
    */
    @ApiModelProperty(value = "业务类型（租赁类型）")
    private String leaseType;

    /**
    * 项目主办
    */
    @ApiModelProperty(value = "项目主办")
    private Long sponsorUserId;

    /**
    * 业务部门
    */
    @ApiModelProperty(value = "业务部门")
    private Long belongDeptId;

    /**
    * 评估主体id
    */
    @ApiModelProperty(value = "评估主体id")
    private Long evaluationSubjectId;

    /**
    * 国家
    */
    @ApiModelProperty(value = "国家")
    private String country;

    /**
    * 省份
    */
    @ApiModelProperty(value = "省份")
    private String province;

    /**
    * 城市
    */
    @ApiModelProperty(value = "城市")
    private String city;

    /**
    * 区、县
    */
    @ApiModelProperty(value = "区、县")
    private String district;

    /**
    * 授信金额
    */
    @ApiModelProperty(value = "授信金额")
    private Long creditAmount;

    /**
    * 计划投放日
    */
    @ApiModelProperty(value = "计划投放日")
    private LocalDateTime planPayDate;

    /**
    * 已投放金额
    */
    @ApiModelProperty(value = "已投放金额")
    private Long paidAmount;

    /**
    * 拟投放金额
    */
    @ApiModelProperty(value = "拟投放金额")
    private Long planPayAmount;

    /**
    * 期限
    */
    @ApiModelProperty(value = "期限")
    private Integer termMonth;

    /**
    * 项目保证金
    */
    @ApiModelProperty(value = "项目保证金")
    private Long deposit;


    @ApiModelProperty("项目手续费")
    private Long commission;

    /**
    * 合同利率
    */
    @ApiModelProperty(value = "合同利率")
    private Integer contractInterestRate;

    /**
    * irr
    */
    @ApiModelProperty(value = "irr")
    private Integer irr;

    /**
    * ftp
    */
    @ApiModelProperty(value = "ftp")
    private Integer ftp;

    /**
     * IRR-FTP差值
     */
    @ApiModelProperty("RR-FTP差值")
    private Integer irrFtpDiff;

    /**
    * 本周项目计划节点
    */
    @ApiModelProperty(value = "本周项目计划节点")
    private String planActionThisWeek;

    /**
    * 本周项目实际节点
    */
    @ApiModelProperty(value = "本周项目实际节点")
    private String actualActionThisWeek;

    /**
    * 下周项目计划节点
    */
    @ApiModelProperty(value = "下周项目计划节点")
    private String planActionNextWeek;

    /**
    * 项目进展状态
    */
    @ApiModelProperty(value = "项目进展状态")
    private String projectProgress;

    /**
     * 运营进度反馈
     */
    @ApiModelProperty("运营进度反馈")
    private String yunyingFeedback;

    /**
    * 运营优先级
    */
    @ApiModelProperty(value = "运营优先级")
    private Integer yunyingPriority;

    /**
    * 是否纳入资金计划
    */
    @ApiModelProperty(value = "是否纳入资金计划")
    private String bringIntoFundPlan;

    /**
    * 意见及反馈
    */
    @ApiModelProperty(value = "意见及反馈")
    private String suggestion;

}
