package cn.zswltech.mithras.service.mapper.model.fund.financing;

import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.fund.financing.FundFinancingBizTypeEnum;
import cn.zswltech.mithras.service.enums.fund.financing.FundFinancingStatusEnum;
import cn.zswltech.mithras.service.enums.fund.financing.FundFinancingTimeLimitTypeEnum;
import cn.zswltech.mithras.common.model.BaseModel;
import cn.zswltech.mithras.common.model.IEntity;
import cn.zswltech.mithras.common.annotation.IncludeNull;
import cn.zswltech.mithras.service.service.fund.financing.fms.IFundFinancingStateMachineEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("fund_financing_base_info")
public class FundFinancingBaseInfo extends BaseModel implements IEntity, IFundFinancingStateMachineEntity {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;
//    /**
//     * 授信id 授信与兼容关系转变多对一 使用fund_financing_credit_ref记录
//     */
//    @TableField("credit_id")
//    private Long creditId;
//    /**
//     * 授信编号
//     */
//    @TableField("credit_code")
//    private String creditCode;
    /**
     * 序列号
     */
    @TableField("sequence")
    private Integer sequence;
    /**
     * 融资编号
     */
    @TableField("financing_code")
    private String financingCode;
//    /**
//     * 融资机构id
//     */
//    @TableField("organization_id")
//    private String organizationIds;
//    /**
//     * 融资机构名称
//     */
//    @TableField("organization_name")
//    private String organizationNames;
    /**
     * 总授信额度
     */
    @TableField("total_credit_limit")
    private Long totalCreditLimit;
    /**
     * 剩余授信额度
     */
    @TableField("remaining_credit_limit")
    private Long remainingCreditLimit;
    /**
     * 期限类型 {@link FundFinancingTimeLimitTypeEnum#name()}
     */
    @TableField("time_limit_type")
    private String timeLimitType;
    /**
     * 业务类型 {@link FundFinancingBizTypeEnum#name()}
     */
    @TableField("business_type")
    private String businessType;
    /**
     * 担保信息 {@link GuaranteeInfo}
     */
    @IncludeNull
    @TableField("guarantee_info")
    private String guaranteeInfo;
    /**
     * 资金用途
     */
    @TableField("funds_purpose")
    private String fundsPurpose;
    /**
     * 备注
     */
    @IncludeNull
    @TableField("remark")
    private String remark;
    /**
     * 资金经理id
     */
    @TableField("fund_manager_id")
    private Long fundManagerId;
    /**
     * 所属部门id
     */
    @TableField("dept_id")
    private Long deptId;
    /**
     * 部门负责人id
     */
    @TableField("biz_header_id")
    private Long bizHeaderId;
    /**
     * 分管领导id
     */
    @TableField("leader_id")
    private Long leaderId;
    /**
     * 融资状态 {@link FundFinancingStatusEnum#name()}
     */
    @TableField("financing_status")
    private String financingStatus;
    /**
     * 审批状态 {@link }
     */
    @TableField("approval_status")
    private String approvalStatus;
    /**
     * 变更子类型
     */
    @IncludeNull
    @TableField("change_sub_type")
    private String changeSubType;
    /**
     * 计划贷款时间
     */
    @TableField("plan_loan_date")
    private LocalDate planLoanDate;
    /**
     * 实际贷款日期
     */
    @TableField("actual_loan_date")
    private LocalDate actualLoanDate;
    /**
     * 实际到期日期
     */
    @TableField("actual_expire_date")
    private LocalDate actualExpireDate;
    /**
     * 融资金额
     */
    @TableField("financing_amount")
    private Long financingAmount;
    /**
     * 是否有质押 {@link YesOrNoNumberEnum#name()}
     */
    @TableField("has_pledge_info")
    private Integer hasPledgeInfo;
    /**
     * 还款日
     **/
    @TableField("repay_day")
    private Integer repayDay;

    /**
     * 核销状态
     */
    @TableField("write_off_status")
    private String writeOffStatus;

    /**
     * 是否期初一次性收息 0-否 1-是
     */
    @TableField(value = "initial_interest_received_once")
    private Integer initialInterestReceivedOnce;

    /**
     * 融资机构信息{@link OrganizationInfo}
     */
    @TableField(value = "organization_info")
    private String organizationInfo;


    @Override
    public void setMainId(Long id) {
        this.id = id;
    }

    @Override
    public Long getMainId() {
        return this.id;
    }

    @Override
    public String getProcessStatus() {
        return this.approvalStatus;
    }

    @Override
    public void setProcessStatus(String processState) {
        this.approvalStatus = processState;
    }

    @Override
    public String getRecordStatus() {
        return financingStatus;
    }

    @Override
    public void setRecordStatus(String recordStatus) {
        this.financingStatus = recordStatus;
    }

    @Override
    public String getSecondRecordStatus() {
        return this.changeSubType;
    }

    @Override
    public void setSecondRecordStatus(String recordStatus) {
        this.changeSubType = recordStatus;
    }

    @Override
    public String getProcessStatusFieldName() {
        return "approval_status";
    }

    @Override
    public String getRecordStatusFieldName() {
        return "financing_status";
    }

    @Override
    public String getSecondRecordStatusFieldName() {
        return "change_sub_type";
    }

    @Data
    public static class GuaranteeInfo {

        private Long organizationId;

        private String organizationName;
        /**
         * 担保机构id
         */
        private Long guaranteeAgencyId;
        /**
         * 担保机构名称
         */
        private String guaranteeAgencyName;
        /**
         * 担保金额
         */
        private Long guaranteeAmount;
        /**
         * 剩余担保额度
         */
        private Long remainingGuaranteeAmount;
    }

    @Data
    public static class OrganizationInfo {

        private Long organizationId;

        private String organizationName;

        private Long organizationAmount;

        private Long remainingCreditAmount;

    }
}