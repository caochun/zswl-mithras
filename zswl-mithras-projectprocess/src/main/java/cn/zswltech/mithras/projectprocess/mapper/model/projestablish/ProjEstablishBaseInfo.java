package cn.zswltech.mithras.projectprocess.mapper.model.projestablish;

import cn.zswltech.mithras.foundation.enums.common.RecordStatus;
import cn.zswltech.mithras.projectprocess.enums.projreview.ProjRegionalClassify;
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
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * @author luyi
 * @description 立项基本信息表
 * @date 2022-07-19
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SponsorField(value = "projSponsorUserId", belongDeptField = "bizDeptId")
public class ProjEstablishBaseInfo extends BaseModel implements Serializable, IEntity, IStateMachineEntity {

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
    @IncludeNull
    private String leaseTypes;

    /**
     * 风控行业分类
     */
    @TableField(value = "risk_control_industry_classify")
    private String riskControlIndustryClassify;

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
    @IncludeNull
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
     * 地区分类
     * {@link ProjRegionalClassify#name()}
     **/
    @TableField("regional_project_classify")
    private String regionalProjectClassify;

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
     * @deprecated 转让方支持手填，使用 {@link ProjEstablishBaseInfo#assignor} 代替
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
     * 债权人列表 项目类型为保理时
     */
    @TableField("creditor_info")
    private String creditorInfo;

    /**
     * 债权人id
     *
     * @deprecated 债权人由单个变为多个 使用新字段（JSON） {@link ProjEstablishBaseInfo#creditorInfo}
     */
    @Deprecated
    @TableField("creditor_client_id")
    private Long creditorClientId;

    /**
     * 债权人存量风险敞口
     *
     * @deprecated 债权人由单个变为多个 使用新字段（JSON） {@link ProjEstablishBaseInfo#creditorInfo}
     */
    @Deprecated
    @TableField("creditor_stock_risk_exposure")
    private Long creditorStockRiskExposure;

    /**
     * 债务人信息。（项目类型为保理时）
     */
    @TableField("debtor_info")
    @IncludeNull
    private String debtorInfo;

    /**
     * 担保人信息
     */
    @TableField("guarantee_info")
    @IncludeNull
    private String guaranteeInfo;

    /**
     * 质押人信息
     */
    @TableField("pledgor_info")
    @IncludeNull
    private String pledgorInfo;

    /**
     * 抵押人信息
     */
    @TableField("mortgagor_info")
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
     * 风控经理id ,json 多选
     */
    @TableField("risk_control_manager_id")
    private String riskControlManagerId;

    /**
     * 立项状态
     * {@link RecordStatus#name()}
     **/
    @TableField("proj_establish_status")
    private String projEstablishStatus;

    @TableField("proj_establish_process_status")
    private String projEstablishProcessStatus;

    @TableField("type_seq_id")
    private Long typeSeqId;

    @TableField("remark")
    private String remark;

    @TableField("rating_update_time")
    private Date ratingUpdateTime;

    @TableField(value = "evaluation_subject_rating_score")
    private String evaluationSubjectRatingScore;

    @TableField(value = "evaluation_subject_rating_score_id")
    private Long evaluationSubjectRatingScoreId;

    @TableField(value = "main_lessee_rating_score")
    private String mainLesseeRatingScore;

    @TableField(value = "main_lessee_rating_score_id")
    private Long mainLesseeRatingScoreId;

    @Override
    public Long getMainId() {
        return getId();
    }

    @Override
    public void setMainId(Long id) {
        setId(id);
    }


    @Override
    public ProcessStatus getProcessStatus() {
        return ProjProcessState.of(projEstablishProcessStatus);
    }

    @Override
    public void setProcessStatus(ProcessStatus processState) {
        setProjEstablishProcessStatus(processState.name());
    }

    @Override
    public RecordStatus getRecordStatus() {
        return RecordStatus.of(projEstablishStatus);
    }

    @Override
    public void setRecordStatus(RecordStatus recordStatus) {
        setProjEstablishStatus(recordStatus.name());
    }

    @Override
    public String getProcessStatusFieldName() {
        return "proj_establish_process_status";
    }

    @Override
    public String getRecordStatusFieldName() {
        return "proj_establish_status";
    }
}
