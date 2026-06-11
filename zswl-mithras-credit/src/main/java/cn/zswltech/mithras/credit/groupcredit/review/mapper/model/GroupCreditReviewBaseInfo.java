package cn.zswltech.mithras.credit.groupcredit.review.mapper.model;
import cn.zswltech.mithras.foundation.annotation.NotCompareColumn;
import cn.zswltech.mithras.foundation.enums.common.RecordStatus;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import cn.zswltech.mithras.foundation.persistence.plugin.IncludeNull;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @description 集团授信评审基本信息表
 * @author wangchuanhao
 * @date 2022-11-11
 */
@Data
public class GroupCreditReviewBaseInfo extends BaseModel implements Serializable, IEntity {

    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 集团授信立项基本信息表id
    */
    @TableField("group_credit_establish_id")
    private Long groupCreditEstablishId;

    /**
    * 授信主体客户id
    */
    @TableField("client_id")
    private Long clientId;

    /**
    * 授信主体存量风险敞口
    */
    @TableField("client_risk_exposure")
    private Long clientRiskExposure;

    /**
    * 授信名称
    */
    @TableField("proj_name")
    private String projName;

    /**
    * 项目编号
    */
    @TableField("proj_code")
    private String projCode;

    /**
    * 授信说明
    */
    @TableField("proj_background")
    @IncludeNull
    private String projBackground;

    /**
    * 申报授信金额
    */
    @TableField("apply_credit_amount")
    private Long applyCreditAmount;

    /**
     * 项目批复金额
     */
    @TableField("project_approval_amount")
    private Long projectApprovalAmount;

    /**
    * 额度有效期限月数
    */
    @TableField("valid_month_count")
    private Integer validMonthCount;

    /**
    * 额度是否可循环
    */
    @TableField("credit_amount_loop")
    private Integer creditAmountLoop;

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
    * 法务经理id
    */
    @TableField("legal_manager_user_id")
    private Long legalManagerUserId;

    /**
    * 立项状态
     * {@link RecordStatus#name()}
    */
    @TableField("group_credit_review_status")
    @NotCompareColumn
    private String groupCreditReviewStatus;

    /**
    * 流程状态
    */
    @TableField("group_credit_review_process_status")
    @NotCompareColumn
    private String groupCreditReviewProcessStatus;

    /**
     * 项目类型：公共事业类、省内国（央）企、其他。n内容决定后续审批流审批权限。
     * @deprecated 使用风控行业分类替代该字段
     */
    @Deprecated
    @TableField("project_type")
    private String projectType;

    @TableField(value = "rating_update_time")
    private LocalDateTime ratingUpdateTime;

    @TableField(value = "client_rating_score")
    private String clientRatingScore;

    @TableField(value = "client_rating_score_id")
    private Long clientRatingScoreId;

    /**
     * 风控行业分类
     * 该字段用于保存审批流的历史记录
     */
    @TableField("risk_control_industry_classify")
    private String riskControlIndustryClassify;

    @Override
    public void setMainId(Long id) {
        this.id = id;
    }

    @Override
    public Long getMainId() {
        return id;
    }
}
