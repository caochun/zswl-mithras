package cn.zswltech.mithras.factory.model;

import cn.zswltech.mithras.common.model.BaseModel;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author dingqi
 * @date 2025/9/12
 * @description 借据信息-底表
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("contract_receipt_bottom")
public class ContractReceiptBottom extends BaseModel {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    @TableField(value = "id")
    private Long id;

    /**
     * 借据ID
     */
    @TableField(value = "receipt_id")
    private Long receiptId;

    /**
     * 借据编号
     */
    @TableField(value = "receipt_code")
    private String receiptCode;

    /**
     * 合同id
     */
    @TableField(value = "contract_id")
    private Long contractId;

    /**
     * 合同编号
     */
    @TableField(value = "contract_code")
    private String contractCode;

    /**
     * 项目名称
     */
    @TableField(value = "proj_name")
    private String projName;

    /**
     * 项目编号
     */
    @TableField(value = "proj_code")
    private String projCode;

    /**
     * 项目主办用户id
     */
    @TableField(value = "proj_sponsor_user_id")
    private Long projSponsorUserId;

    /**
     * 业务部门id
     */
    @TableField(value = "biz_dept_id")
    private Long bizDeptId;

    /**
     * 业务部门负责人id
     */
    @TableField(value = "biz_dept_leader_id")
    private Long bizDeptLeaderId;

    /**
     * 是否协同业务 0否，1是
     */
    @TableField(value = "collaborative_flag")
    private Integer collaborativeFlag;

    /**
     * 考核部门id
     */
    @TableField(value = "assess_dept_id")
    private Long assessDeptId;

    /**
     * 主承租人ID
     */
    @TableField(value = "client_id")
    private Long clientId;

    /**
     * 企业规模-借据维度
     */
    @TableField(value = "org_scale")
    private String orgScale;

    /**
     * 合同状态
     */
    @TableField(value = "contract_status")
    private String contractStatus;

    /**
     * 是否逾期 0未逾期，1逾期
     */
    @TableField(value = "overdue_flag")
    private Integer overdueFlag;

    /**
     * 国标行业分类
     */
    @TableField(value = "industry_type")
    private String industryType;

    /**
     * 国标行业分类名称，完整名称 / 分割
     */
    @TableField(value = "industry_type_name")
    private String industryTypeName;

    /**
     * 风控行业分类
     */
    @TableField(value = "risk_control_industry_classify")
    private String riskControlIndustryClassify;

    /**
     * 评估主体区域 - 省
     */
    @TableField(value = "province")
    private String province;

    /**
     * 评估主体区域 - 市
     */
    @TableField(value = "city")
    private String city;

    /**
     * 评估主体区域 - 区
     */
    @TableField(value = "district")
    private String district;

    /**
     * 评估主体区域 - 详细地址
     */
    @TableField(value = "assessment_area")
    private String assessmentArea;

    /**
     * 是否山区 0否，1是
     */
    @TableField(value = "mountain_area_flag")
    private Integer mountainAreaFlag;

    /**
     * 业务类型。租赁、保理、转租赁
     */
    @TableField(value = "biz_type")
    private String bizType;

    /**
     * 租赁类型。直租、回租、经营性租赁
     */
    @TableField(value = "lease_type")
    private String leaseType;

    /**
     * 保理类型
     */
    @TableField(value = "factoring_type")
    private String factoringType;

    /**
     * 收入分摊方式
     */
    @TableField(value = "income_confirm_type")
    private String incomeConfirmType;

    /**
     * 租赁物类型
     */
    @TableField(value = "lease_item_types")
    private String leaseItemTypes;

    /**
     * 租赁物类型名称-转化后
     */
    @TableField(value = "lease_item_type_name")
    private String leaseItemTypeName;

    /**
     * 投放日
     */
    @TableField(value = "first_payment_date")
    private LocalDate firstPaymentDate;

    /**
     * 借据起租日期
     */
    @TableField(value = "receipt_start_date")
    private LocalDate receiptStartDate;

    /**
     * 实际结束日
     */
    @TableField(value = "actual_finish_date")
    private LocalDateTime actualFinishDate;

    /**
     * 合同期限-租赁期限月数
     */
    @TableField(value = "lease_month_count")
    private Integer leaseMonthCount;

    /**
     * 还款频率。按月，按季，按年，不规则
     */
    @TableField(value = "repay_rate")
    private String repayRate;

    /**
     * 申报授信金额-合同金额
     */
    @TableField(value = "apply_credit_amount")
    private Long applyCreditAmount;

    /**
     * 项目首期租金
     */
    @TableField(value = "proj_down_payment")
    private Long projDownPayment;

    /**
     * 融资金额 合同金额-首期租金
     */
    @TableField(value = "financing_amount")
    private Long financingAmount;

    /**
     * 保证金
     */
    @TableField(value = "earnest_money")
    private Long earnestMoney;

    /**
     * 服务费/咨询费
     */
    @TableField(value = "consulting_fee")
    private Long consultingFee;

    /**
     * 手续费
     */
    @TableField(value = "commission")
    private Long commission;

    /**
     * 名义货价
     */
    @TableField(value = "nominal_price")
    private Long nominalPrice;

    /**
     * 咨询费率 咨询服务费/合同金额
     */
    @TableField(value = "consulting_fee_rate")
    private String consultingFeeRate;

    /**
     * 租赁利率 = lpr + 加点
     */
    @TableField(value = "lpr_rate")
    private Integer lprRate;

    /**
     * FTP利率
     */
    @TableField(value = "ftp_rate")
    private Integer ftpRate;

    /**
     * 实际xirr
     */
    @TableField(value = "actual_xirr")
    private Integer actualXirr;

    /**
     * 实际irr
     */
    @TableField(value = "actual_irr")
    private Integer actualIrr;

    /**
     * 租金总额
     */
    @TableField(value = "rent_amount")
    private Long rentAmount;

    /**
     * 剩余租金
     */
    @TableField(value = "remain_rent")
    private Long remainRent;

    /**
     * 剩余本金
     */
    @TableField(value = "remain_principal")
    private Long remainPrincipal;

    /**
     * 剩余利息
     */
    @TableField(value = "remain_interest")
    private Long remainInterest;

    /**
     * 剩余保证金
     */
    @TableField(value = "remain_earnest_money")
    private Long remainEarnestMoney;

    /**
     * 风险敞口
     */
    @TableField(value = "risk_exposure")
    private Long riskExposure;

    /**
     * 当前逾期天数
     */
    @TableField(value = "overdue_days")
    private Integer overdueDays;

    /**
     * 逾期租金
     */
    @TableField(value = "overdue_rent")
    private Long overdueRent;

    /**
     * 逾期本金
     */
    @TableField(value = "overdue_principal")
    private Long overduePrincipal;

    /**
     * 逾期利息
     */
    @TableField(value = "overdue_interest")
    private Long overdueInterest;

    /**
     * 罚息
     */
    @TableField(value = "penalty_interest")
    private Long penaltyInterest;

    /**
     * 五级分类结果
     */
    @TableField(value = "classify_result")
    private String classifyResult;

    /**
     * 拨备计提比例
     */
    @TableField(value = "award_ratio")
    private Long awardRatio;

    /**
     * 剩余年限（年）
     */
    @TableField(value = "remaining_years")
    private String remainingYears;

    /**
     * 印花税
     */
    @TableField(value = "stamp_duty")
    private Long stampDuty;

    /**
     * 是否监管 0否，1是
     */
    @TableField(value = "supervise_flag")
    private Integer superviseFlag;

    /**
     * 是否质押 0否，1是
     */
    @TableField(value = "pledge_flag")
    private Integer pledgeFlag;

    /**
     * 租金往来方
     */
    @TableField(value = "rent_concat_account")
    private String rentConcatAccount;

    /**
     * 租金回款账号
     */
    @TableField(value = "rent_collection_account")
    private String rentCollectionAccount;

    /**
     * 是否山区
     */
    @TableField(value = "mountain_flag")
    private Integer mountainFlag;

    /**
     * xirr
     */
    @TableField(value = "xirr")
    private Double xirr;

    /**
     * 首付款标志，0不包括，1包括
     */
    @TableField(value = "down_payment_type")
    private Integer downPaymentType;
}
