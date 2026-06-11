package cn.zswltech.mithras.rating.model;

import cn.zswltech.mithras.dto.rating.RatingReportApprovalRSP;
import cn.zswltech.mithras.dto.rating.ratingamount.RatingCreditMeasureRSP;
import cn.zswltech.mithras.dto.rating.ratingamount.RatingEvaluateBaseRSP;
import cn.zswltech.mithras.dto.rating.ratingclient.RatingQualitativeRSP;
import cn.zswltech.mithras.dto.rating.ratingclient.RatingQuantitativeRSP;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("rating_report")
public class RatingReport extends BaseModel implements Serializable, IEntity {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("service_code")
    private String serviceCode;

    /**
     * 定性指标
     * @see RatingQualitativeRSP
     */
    @TableField("qualitative")
    private String qualitative;

    /**
     * 定量指标
     * @see RatingQuantitativeRSP
     */
    @TableField("quantitative")
    private String quantitative;

    // 调整事项 RatingQualitativeRSP
    @TableField("adjust_event")
    private String adjustEvent;

    /**
     * 评估基准 List<RatingEvaluateBaseRSP>
     * @see RatingEvaluateBaseRSP
     */
    @TableField("evaluate_base")
    private String evaluateBase;

    /**
     * 增信措施 Map<String,List<RatingCreditMeasureRSP>>
     * @see RatingCreditMeasureRSP
     */
    @TableField("credit_measure")
    private String creditMeasure;

    /**
     * 各指标审批意见
     * @see RatingReportApprovalRSP.RatingApprovalRSP
     */
    @TableField("approval_info")
    private String approvalInfo;

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
