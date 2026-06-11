package cn.zswltech.mithras.projectprocess.model.projpricing;

import cn.zswltech.mithras.foundation.enums.common.RecordStatus;
import cn.zswltech.mithras.projectprocess.enums.projpricing.FtpIndustryCategoryEnum;
import cn.zswltech.mithras.projectprocess.enums.projpricing.ProjectManageLevelEnum;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.foundation.persistence.model.SponsorField;
import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import cn.zswltech.mithras.foundation.persistence.plugin.IncludeNull;
import cn.zswltech.mithras.foundation.state.IStateMachineEntity;
import cn.zswltech.mithras.foundation.state.ProcessStatus;
import cn.zswltech.mithras.foundation.state.ProjProcessState;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 项目定价基本信息表
 * </p>
 *
 * @author chenyifei
 * @since 2024-08-16
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("proj_pricing_base_info")
@SponsorField(value = "projSponsorUserId", belongDeptField = "bizDeptId")
public class ProjPricingBaseInfo extends BaseModel implements Serializable, IEntity, IStateMachineEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("proj_review_id")
    private Long projReviewId;

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
     * 下拉框选项：公共事业类、省内国（央）企、其他。
内容决定后续审批流审批权限。

     */
    @TableField("project_type")
    private String projectType;

    /**
     * 项目分类，可选项：鼓励类，适度支持类，谨慎类，工程机械类（厂商担保模式），集团内协同业务
     */
    @TableField("project_classify")
    private String projectClassify;

    /**
     * 地区项目分类
     */
    @TableField("regional_project_classify")
    private String regionalProjectClassify;

    /**
     * 区域划分
     */
    @IncludeNull
    @TableField("regional_division")
    private String regionalDivision;

    /**
     * 审批类型
     */
    @TableField("approval_type")
    private String approvalType;

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
     * 存量翻单、渠道介绍、自主开发
     */
    @TableField("proj_source")
    private String projSource;

    /**
     * 评估主体ID
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
     */
    @TableField("transferor_client_id")
    private Long transferorClientId;

    /**
     * 承租人列表
     */
    @TableField("lessee_info")
    private String lesseeInfo;

    /**
     * 债权人id
     */
    @TableField("creditor_client_id")
    private Long creditorClientId;

    /**
     * 债权人存量风险敞口
     */
    @TableField("creditor_stock_risk_exposure")
    private Long creditorStockRiskExposure;

    /**
     * 债务人信息。（项目类型为保理时）
     */
    @TableField("debtor_info")
    private String debtorInfo;

    /**
     * 担保人信息
     */
    @TableField("guarantee_info")
    private String guaranteeInfo;

    /**
     * 质押人信息
     */
    @TableField("pledgor_info")
    private String pledgorInfo;

    /**
     * 抵押人信息
     */
    @TableField("mortgagor_info")
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

    @TableField("legal_manager_user_id")
    private Long legalManagerUserId;

    /**
     * 定价状态
     */
    @TableField("proj_pricing_status")
    private String projPricingStatus;

    /**
     * 流程状态
     */
    @TableField("proj_pricing_process_status")
    private String projPricingProcessStatus;

    /**
     * 冗余报价方案字段
     */
    @TableField("declared_amount")
    private Long declaredAmount;

    /**
     * 关联的立项ID
     */
    @TableField("proj_establish_id")
    private Long projEstablishId;

    /**
     * 创建人、发起人
     */
    @TableField("create_by")
    private Long createBy;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 最后更新人id
     */
    @TableField("update_by")
    private Long updateBy;

    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 债权人信息
     */
    @TableField("creditor_info")
    private String creditorInfo;

    /**
     * 转让方（转租赁类型时）
     */
    @TableField("assignor")
    private String assignor;

    /**
     * 数据来源类型，区分普通立项(PROJ_ESTABLISH)和集团授信(GROUP_CREDIT_REVIEW)
     */
    @TableField("relation_data_type")
    private String relationDataType;

    /**
     * 集团授信评审id
     */
    @TableField("group_credit_review_id")
    private Long groupCreditReviewId;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 企业性质
     */
    @TableField("enterprise_nature")
    private String enterpriseNature;

    /**
     * 风控行业分类
     */
    @TableField("risk_control_industry_classify")
    private String riskControlIndustryClassify;

    /**
     * FTP行业分类 {@link FtpIndustryCategoryEnum#name()}
     */
    @TableField("ftp_industry_category")
    private String ftpIndustryCategory;

    /**
     * 项目管理层级 {@link ProjectManageLevelEnum#name()}
     */
    @IncludeNull
    @TableField("project_manage_level")
    private String projectManageLevel;

    /**
     * 是否AAA评级
     */
    @IncludeNull
    @TableField("is_AAA")
    private Integer isAAA;


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
        return ProjProcessState.of(projPricingProcessStatus);
    }


    @Override
    public void setProcessStatus(ProcessStatus processState) {
        setProjPricingStatus(processState.name());
    }

    @Override
    public RecordStatus getRecordStatus() {
        return RecordStatus.of(projPricingStatus);
    }

    @Override
    public void setRecordStatus(RecordStatus recordStatus) {
        setProjPricingStatus(recordStatus.name());
    }

    @Override
    public String getProcessStatusFieldName() {
        return "proj_pricing_process_status";
    }

    @Override
    public String getRecordStatusFieldName() {
        return "proj_pricing_status";
    }

    @Override
    public void reset() {
        super.reset();
        this.id = null;
    }
}
