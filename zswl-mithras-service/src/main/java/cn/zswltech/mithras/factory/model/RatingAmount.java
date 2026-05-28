package cn.zswltech.mithras.factory.model;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.mapper.tag.IEntity;
import cn.zswltech.mithras.service.plugin.IncludeNull;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("rating_amount")
public class RatingAmount extends BaseModel implements Serializable, IEntity {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("report_id")
    @IncludeNull
    private Long reportId;

    @TableField("snapshot_id")
    @IncludeNull
    private Long snapshotId;

    @TableField("client_id")
    private Long clientId;

    @TableField("client_usc_code")
    private String clientUscCode;

    @TableField("proj_review_id")
    private Long projReviewId;

    @TableField("proj_code")
    private String projCode;

    @TableField("proj_name")
    private String projName;

    @TableField("evaluation_subject_id")
    private Long evaluationSubjectId;

    @TableField("evaluation_subject_Name")
    private String evaluationSubjectName;

    @TableField("evaluation_subject_uscc")
    private String evaluationSubjectUscc;

    @TableField("proj_system")
    private Boolean projSystem;

    @TableField("material_lease_item")
    private Boolean materialLeaseItem;

    @TableField("model_code")
    private String modelCode;

    @TableField("model_name")
    private String modelName;

    @TableField("client_quota")
    private String clientQuota;

    @TableField("belong_dept_id")
    private Long belongDeptId;

    @TableField("belong_sponsor_user_id")
    private Long belongSponsorUserId;

    @TableField("process_status")
    private String processStatus;

    @TableField("rating_status")
    private Boolean ratingStatus;

    @TableField("relation_proj_info")
    private String relationProjInfo;

    @TableField("is_real_estate_adjust")
    private Boolean isRealEstateAdjust;

    @TableField("is_stock_rights_adjust")
    private Boolean isStockRightsAdjust;

    @TableField("effect_time")
    private LocalDate effectTime;

    @TableField("abandon_time")
    private LocalDate abandonTime;

    @TableField("deleted")
    private Boolean deleted;

    @Override
    public void setMainId(Long id) {
        setId(id);
    }

    @Override
    public Long getMainId() {
        return getId();
    }
}
