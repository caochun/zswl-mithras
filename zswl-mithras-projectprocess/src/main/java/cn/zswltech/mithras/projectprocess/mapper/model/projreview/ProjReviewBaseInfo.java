package cn.zswltech.mithras.projectprocess.mapper.model.projreview;

import cn.zswltech.mithras.foundation.enums.common.RecordStatus;
import cn.zswltech.mithras.projectprocess.enums.projpricing.FtpIndustryCategoryEnum;
import cn.zswltech.mithras.projectprocess.enums.projreview.ProjRegionalClassify;
import cn.zswltech.mithras.projectprocess.enums.projreview.ProjRegionalDivisionEnum;
import cn.zswltech.mithras.projectprocess.enums.projreview.ProjectClassify;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.foundation.persistence.model.SponsorField;
import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import cn.zswltech.mithras.foundation.persistence.plugin.IncludeNull;
import cn.zswltech.mithras.foundation.state.IStateMachineEntity;
import cn.zswltech.mithras.foundation.state.ProcessStatus;
import cn.zswltech.mithras.foundation.state.ProjProcessState;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zhaozhengkang
 * @description 立项基本信息
 * @date 2022-08-01
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SponsorField(value = "projSponsorUserId", belongDeptField = "bizDeptId")
public class ProjReviewBaseInfo extends BaseModel implements Serializable, IEntity, IStateMachineEntity {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 客户id
     */
    @TableField("client_id")
    private Long clientId;

    /**
     * 业务类型。租赁、保理、转租赁
     */
    @TableField("biz_type")
    private String bizType;

    /**
     * 项目名称
     */
    @TableField("proj_name")
    private String projName;

    /**
     * 下拉框选项：公共事业类、省内国（央）企、其他。n内容决定后续审批流审批权限。
     * @deprecated 客户的风控行业分类替换该字段功能
     */
    @Deprecated
    @TableField("project_type")
    private String projectType;

    /**
     * 行业分类
     * {@link ProjectClassify#name()}
     */
    @TableField("project_classify")
    @IncludeNull
    private String projectClassify;

    /**
     * 地区分类
     * {@link ProjRegionalClassify#name()}
     **/
    @TableField("regional_project_classify")
    private String regionalProjectClassify;

    /**
     * 区域划分
     * {@link ProjRegionalDivisionEnum#name()}
     **/
    @IncludeNull
    @TableField("regional_division")
    private String regionalDivision;

//    /**
//     * 审批类型
//     */
//    @TableField("approval_type")
//    private String approvalType;

    /**
     * 项目编号
     */
    @TableField("proj_code")
    private String projCode;

    /**
     * 租赁类型。直租、回租、经营性租赁
     */
    @TableField("lease_types")
    private String leaseTypes;

    /**
     * 保理类型。有追明保理、无追明保理、有追暗保理
     */
    @TableField("factoring_types")
    private String factoringTypes;

    /**
     * 转让类型。有追、无追
     */
    @TableField("zr_types")
    private String zrTypes;

    /**
     * 冗余报价方案字段
     */
    @TableField("declared_amount")
    private Long declaredAmount;

    /**
     * 存量翻单、渠道介绍、自主开发
     */
    @TableField("proj_source")
    private String projSource;

    /**
     * 评估主体ID
     **/
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
    @TableField(value = "province")
    @IncludeNull
    private String province;

    /**
     * 城市
     */
    @TableField(value = "city")
    @IncludeNull
    private String city;

    /**
     * 区、县
     */
    @TableField(value = "district")
    @IncludeNull
    private String district;

    /**
     * 资金用途
     */
    @TableField("funds_purpose")
    private String fundsPurpose;

    /**
     * 项目背景
     */
    @TableField("proj_background")
    private String projBackground;

    /**
     * 转让方。（项目类型为租赁时）
     *
     * @deprecated 转让方支持手填，使用 {@link ProjReviewBaseInfo#assignor} 代替
     */
    @Deprecated
    @TableField("transferor_client_id")
    private Long transferorClientId;

    /**
     * 转让方（转租赁类型时）
     */
    @IncludeNull
    @TableField("assignor")
    private String assignor;

    /**
     * 承租人列表
     */
    @TableField("lessee_info")
    private String lesseeInfo;

    /**
     * 债权人列表
     */
    @TableField("creditor_info")
    private String creditorInfo;

    /**
     * 债权人id
     *
     * @deprecated 债权人由单个变为多个 使用新字段（JSON） {@link ProjReviewBaseInfo#creditorInfo}
     */
    @Deprecated
    @TableField("creditor_client_id")
    private Long creditorClientId;

    /**
     * 债权人存量风险敞口
     *
     * @deprecated 债权人存量风险敞口放入JSON字段 {@link ProjReviewBaseInfo#creditorInfo}
     */
    @Deprecated
    @TableField("creditor_stock_risk_exposure")
    private Long creditorStockRiskExposure;

    /**
     * 债务人信息。（项目类型为保理时）
     */
    @TableField(value = "debtor_info", updateStrategy = FieldStrategy.IGNORED)
    @IncludeNull
    private String debtorInfo;

    /**
     * 担保人信息
     */
    @TableField(value = "guarantee_info", updateStrategy = FieldStrategy.IGNORED)
    @IncludeNull
    private String guaranteeInfo;

    /**
     * 质押人信息
     */
    @TableField(value = "pledgor_info", updateStrategy = FieldStrategy.IGNORED)
    @IncludeNull
    private String pledgorInfo;

    /**
     * 抵押人信息
     */
    @TableField(value = "mortgagor_info", updateStrategy = FieldStrategy.IGNORED)
    @IncludeNull
    private String mortgagorInfo;


    /**
     * 供应商列表
     */
    @TableField("supplier_info")
    private String supplierInfo;

    /**
     * 项目主办用户id
     */
    @TableField("proj_sponsor_user_id")
    private Long projSponsorUserId;

    /**
     * 项目协办方用户id列表
     */
    @TableField("proj_cosponsor_user_ids")
    @IncludeNull
    private String projCosponsorUserIds;

    /**
     * 业务部门id
     */
    @TableField("biz_dept_id")
    private Long bizDeptId;

    /**
     * 业务部门负责人id
     */
    @TableField("biz_dept_leader_id")
    private Long bizDeptLeaderId;

    /**
     * 业务分管领导id
     */
    @TableField("biz_division_leader_id")
    private Long bizDivisionLeaderId;

    /**
     * 风控经理id
     */
    @TableField("risk_control_manager_id")
    private Long riskControlManagerId;

    /**
     * legal_manager_user_id
     */
    @TableField("legal_manager_user_id")
    private Long legalManagerUserId;

    /**
     * 立项状态
     * {@link RecordStatus#name()}
     */
    @TableField("proj_review_status")
    private String projReviewStatus;

    /**
     * 流程状态
     */
    @TableField("proj_review_process_status")
    private String projReviewProcessStatus;

    @TableField("proj_establish_id")
    private Long projEstablishId;

    /**
     * 数据来源类型，区分普通立项和集团授信
     * {@link cn.zswltech.mithras.projectprocess.enums.projreview.ReviewRelationDataType}
     */
    @TableField("relation_data_type")
    private String relationDataType;

    /**
     * 集团授信评审id
     */
    @TableField("group_credit_review_id")
    private Long groupCreditReviewId;

    /**
     * 最近一次审批通过的irr
     *
     * 2024-08-21 ：定价与评审流程剥离后 不再维护该字段
     */
    @TableField("approval_pass_irr")
    private Integer approvalPassIrr;

    @TableField("remark")
    private String remark;

    /**
     * 企业性质
     * {@link cn.zswltech.mithras.customer.enums.client.EnterpriseNatureEnum}
     */
    @TableField(value = "enterprise_nature")
    private String enterpriseNature;

    /**
     * 风控行业分类
     * 该字段用于保存审批流的历史记录
     */
    @TableField(value = "risk_control_industry_classify")
    private String riskControlIndustryClassify;

    /**
     * 财报不完整原因
     */
    @TableField(value = "subject_item_check_reason")
    private String subjectItemCheckReason;

    /**
     * 评级更新时间
     */
    @TableField(value = "rating_update_time")
    private Date ratingUpdateTime;

    @TableField(value = "evaluation_subject_rating_score")
    private String evaluationSubjectRatingScore;

    @TableField(value = "evaluation_subject_rating_score_id")
    private Long evaluationSubjectRatingScoreId;

    @TableField(value = "main_lessee_rating_score")
    private String mainLesseeRatingScore;

    @TableField(value = "main_lessee_rating_score_id")
    private Long mainLesseeRatingScoreId;

    /**
     * FTP行业分类 {@link FtpIndustryCategoryEnum#name()}
     */
    @TableField("ftp_industry_category")
    private String ftpIndustryCategory;

    @Override
    public void setMainId(Long id) {
        setId(id);
    }

    @Override
    public Long getMainId() {
        return getId();
    }

    @Override
    public ProcessStatus getProcessStatus() {
        return ProjProcessState.of(projReviewProcessStatus);
    }

    @Override
    public void setProcessStatus(ProcessStatus processState) {
        setProjReviewProcessStatus(processState.name());
    }

    @Override
    public RecordStatus getRecordStatus() {
        return RecordStatus.of(projReviewStatus);
    }

    @Override
    public void setRecordStatus(RecordStatus recordStatus) {
        setProjReviewStatus(recordStatus.name());
    }

    @Override
    public String getProcessStatusFieldName() {
        return "proj_review_process_status";
    }

    @Override
    public String getRecordStatusFieldName() {
        return "proj_review_status";
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class SubjectReasonData {
        private Long clientId;
        private String reason;
    }
}
