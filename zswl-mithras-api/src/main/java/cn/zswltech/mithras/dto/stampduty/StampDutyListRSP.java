package cn.zswltech.mithras.dto.stampduty;

import cn.zswltech.mithras.api.common.PageR;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author luyujie
 * @date 2026/1/24
 * @description
 */
@Data
@ApiModel("印花税缴纳明细列表-返回体")
public class StampDutyListRSP {
    @ApiModelProperty("列表记录")
    private PageR<StampDutyListRSP.StampDutyList> records;

    @ApiModelProperty("合同列表记录")
    private PageR<StampDutyListRSP.ContractList> contracts;

    @ApiModelProperty("融资合同列表记录")
    private PageR<StampDutyListRSP.FundContractList> fundContracts;

    @Data
    public static class StampDutyList {
        @ApiModelProperty("id序列号")
        private Long id;

        @ApiModelProperty("序列号")
        private Long sortNo;

        @ApiModelProperty("关联id")
        private Long belongId;

        @ApiModelProperty("申报税目名称")
        private String name;

        @ApiModelProperty("业务部门id")
        private Long belongOrgId;

        @ApiModelProperty("业务部门名称")
        private String belongOrgName;

        @ApiModelProperty("客户id、机构id")
        private Long clientId;

        @ApiModelProperty("客户名称/融资机构")
        private String clientName;

        @ApiModelProperty("合同编号/融资编号")
        private String belongCode;

        @ApiModelProperty("借据id")
        private Long receiptId;

        @ApiModelProperty("借据编号")
        private String receiptCode;

        @ApiModelProperty("实际起租日")
        private LocalDate startDate;

        @ApiModelProperty("不含税租金")
        private Long rent;

        @ApiModelProperty("不含税手续费")
        private Long commission;

        @ApiModelProperty("不含税咨询费")
        private Long consultingFee;

        @ApiModelProperty("金额")
        private Long amount;

        @ApiModelProperty("印花税率")
        private String taxRate;

        @ApiModelProperty("印花税")
        private String stampDuty;

        @ApiModelProperty("来源类型")
        private String scoure;

        @ApiModelProperty("是否删除")
        private String isDelete;

        @ApiModelProperty("create_by")
        private Long createBy;

        @ApiModelProperty("create_time")
        private LocalDateTime createTime;

        @ApiModelProperty("update_by")
        private Long updateBy;

        @ApiModelProperty("update_time")
        private LocalDateTime updateTime;

        @ApiModelProperty("备注")
        private String remark;

    }

    @Data
    public static class ContractList {
        /**
         * id
         */
        @ApiModelProperty("id")
        private Long id;

        /**
         * 客户id
         **/
        @ApiModelProperty("client_id")
        private Long clientId;

        /**
         * 合同编号
         */
        @ApiModelProperty("contract_code")
        private String contractCode;

        /**
         * 咨询合同编号
         */
        @ApiModelProperty("consulting_contract_code")
        private String consultingContractCode;

        /**
         * 剩余可用额度(元)
         */
        @ApiModelProperty("remain_available_quota")
        private Long remainAvailableQuota;

        /**
         * 项目名称
         */
        @ApiModelProperty("proj_name")
        private String projName;

        /**
         * 项目编号
         */
        @ApiModelProperty("proj_code")
        private String projCode;

        /**
         * 业务类型。租赁、保理、转租赁
         */
        @ApiModelProperty("biz_type")
        private String bizType;

        /**
         * 租赁类型。直租、回租、经营性租赁
         */
        @ApiModelProperty("lease_type")
        private String leaseType;

        /**
         * 保理类型
         */
        @ApiModelProperty("factoring_type")
        private String factoringType;

        /**
         * 转让类型
         */
        @ApiModelProperty("zr_type")
        private String zrType;

        /**
         * 转让方
         */
        @ApiModelProperty("assignor")
        private String assignor;

        /**
         * 项目类型：公共事业类、省内国（央）企、其他
         *
         * @deprecated 项目评审该字段作废
         */
        @ApiModelProperty("project_type")
        private String projectType;

        /**
         * todo 风险等级->项目类型
         */
        @ApiModelProperty("risk_level")
        private String riskLevel;

        /**
         * 项目来源：存量翻单、渠道介绍、自主开发
         */
        @ApiModelProperty("proj_source")
        private String projSource;

        /**
         * 资金用途
         */
        @ApiModelProperty("funds_purpose")
        private String fundsPurpose;

        /**
         * 项目背景
         */
        @ApiModelProperty("proj_background")
        private String projBackground;

        /**
         * 项目主办用户id
         */
        @ApiModelProperty("proj_sponsor_user_id")
        private Long projSponsorUserId;

        /**
         * 项目协办方用户id列表
         */
        @ApiModelProperty("proj_cosponsor_user_ids")
        private String projCosponsorUserIds;

        /**
         * 业务部门id
         */
        @ApiModelProperty("biz_dept_id")
        private Long bizDeptId;

        /**
         * 业务部门负责人id
         */
        @ApiModelProperty("biz_dept_leader_id")
        private Long bizDeptLeaderId;

        /**
         * 业务分管领导id
         */
        @ApiModelProperty("biz_division_leader_id")
        private Long bizDivisionLeaderId;

        /**
         * 关联的评审id
         */
        @ApiModelProperty("proj_review_id")
        private Long projReviewId;

        /**
         * 合同状态
         */
        @ApiModelProperty("contract_status")
        private String contractStatus;

        /**
         * 流程状态
         */
        @ApiModelProperty("contract_process_status")
        private String contractProcessStatus;

        /**
         * 流程变更子类型状态
         */
        @ApiModelProperty("contract_process_change_status")
        private String contractProcessChangeStatus;

        /**
         * 概算起租日
         **/
        @ApiModelProperty("estimated_lease_date")
        private LocalDate estimatedLeaseDate;

        /**
         * 实际起租日
         **/
        @ApiModelProperty("actual_lease_date")
        private LocalDate actualLeaseDate;

        /**
         * 实际结束日
         */
        @ApiModelProperty("actual_finish_date")
        private LocalDate actualFinishDate;

        /**
         * 计划付款时间-合同生效时间
         **/
        @ApiModelProperty("payment_plan_date")
        private LocalDate paymentPlanDate;

        /**
         * 计划付款金额-合同金额
         */
        @ApiModelProperty("apply_credit_amount")
        private Long applyCreditAmount;

        /**
         * 支付申请次数
         **/
        @ApiModelProperty("payment_count")
        private Long paymentCount;

        @ApiModelProperty("risk_control_manager_id")
        private Long riskControlManagerId;

        /**
         * 项目分类
         **/
        @ApiModelProperty("proj_item")
        private String projItem;

        /**
         * 额度是否可循环
         */
        @ApiModelProperty("credit_amount_loop")
        private Integer creditAmountLoop;

        /**
         * 本年度合同序号
         */
        @ApiModelProperty("sequence")
        private Integer sequence;

        /**
         * 合同创建年限
         */
        @ApiModelProperty("contract_year")
        private Integer contractYear;

        /**
         * 合同结清时间
         */
        @ApiModelProperty("settle_time")
        private LocalDateTime settleTime;

        /**
         * 逾期催收状态 0可催收，1不可催收
         **/
        @ApiModelProperty("overdue_collection_flag")
        private Long overdueCollectionFlag;

        @ApiModelProperty("remark")
        private String remark;

        /**
         * 变更说明
         */
        @ApiModelProperty("change_remark")
        private String changeRemark;

        /**
         * 风控行业分类
         */
        @ApiModelProperty(value = "risk_control_industry_classify")
        private String riskControlIndustryClassify;

        @ApiModelProperty(value = "item_list_header")
        private String itemListHeader;

        @ApiModelProperty(value = "lease_item_info_id")
        private Long leaseItemInfoId;

        @ApiModelProperty(value = "adjust_remark")
        private String adjustRemark;

        @ApiModelProperty(value = "income_confirm_type")
        private String incomeConfirmType;

        /**
         * 是否签约
         */
        @ApiModelProperty(value = "is_signed")
        private Integer isSigned;

        /**
         * 是否中登初始登记保存
         */
        @ApiModelProperty("is_save_register")
        private Integer isSaveRegister;

        /**
         * 租赁物类型
         **/
        @ApiModelProperty("lease_item_types")
        private String leaseItemTypes;

    }

    @Data
    public static class FundContractList {
        /**
         * 主键id
         */
        @ApiModelProperty("id")
        private Long id;
        /**
         * 序列号
         */
        @ApiModelProperty("sequence")
        private Integer sequence;
        /**
         * 融资编号
         */
        @ApiModelProperty("financing_code")
        private String financingCode;
        /**
         * 总授信额度
         */
        @ApiModelProperty("total_credit_limit")
        private Long totalCreditLimit;
        /**
         * 剩余授信额度
         */
        @ApiModelProperty("remaining_credit_limit")
        private Long remainingCreditLimit;
        /**
         * 期限类型
         */
        @ApiModelProperty("time_limit_type")
        private String timeLimitType;
        /**
         * 业务类型
         */
        @ApiModelProperty("business_type")
        private String businessType;
        /**
         * 担保信息
         */
        @ApiModelProperty("guarantee_info")
        private String guaranteeInfo;
        /**
         * 资金用途
         */
        @ApiModelProperty("funds_purpose")
        private String fundsPurpose;
        /**
         * 备注
         */
        @ApiModelProperty("remark")
        private String remark;
        /**
         * 资金经理id
         */
        @ApiModelProperty("fund_manager_id")
        private Long fundManagerId;
        /**
         * 所属部门id
         */
        @ApiModelProperty("dept_id")
        private Long deptId;
        /**
         * 部门负责人id
         */
        @ApiModelProperty("biz_header_id")
        private Long bizHeaderId;
        /**
         * 分管领导id
         */
        @ApiModelProperty("leader_id")
        private Long leaderId;
        /**
         * 融资状态
         */
        @ApiModelProperty("financing_status")
        private String financingStatus;
        /**
         * 审批状态 {@link }
         */
        @ApiModelProperty("approval_status")
        private String approvalStatus;
        /**
         * 变更子类型
         */
        @ApiModelProperty("change_sub_type")
        private String changeSubType;
        /**
         * 计划贷款时间
         */
        @ApiModelProperty("plan_loan_date")
        private LocalDate planLoanDate;
        /**
         * 实际贷款日期
         */
        @ApiModelProperty("actual_loan_date")
        private LocalDate actualLoanDate;
        /**
         * 实际到期日期
         */
        @ApiModelProperty("actual_expire_date")
        private LocalDate actualExpireDate;
        /**
         * 融资金额
         */
        @ApiModelProperty("financing_amount")
        private Long financingAmount;
        /**
         * 是否有质押
         */
        @ApiModelProperty("has_pledge_info")
        private Integer hasPledgeInfo;
        /**
         * 还款日
         **/
        @ApiModelProperty("repay_day")
        private Integer repayDay;

        /**
         * 核销状态
         */
        @ApiModelProperty("write_off_status")
        private String writeOffStatus;

        /**
         * 是否期初一次性收息 0-否 1-是
         */
        @ApiModelProperty("initial_interest_received_once")
        private Integer initialInterestReceivedOnce;

    }

}

