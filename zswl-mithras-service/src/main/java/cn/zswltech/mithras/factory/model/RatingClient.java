package cn.zswltech.mithras.factory.model;

import cn.zswltech.mithras.common.model.BaseModel;
import cn.zswltech.mithras.common.model.IEntity;
import cn.zswltech.mithras.common.annotation.IncludeNull;
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
@TableName("rating_client")
public class RatingClient extends BaseModel implements Serializable, IEntity {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("report_id")
    private Long reportId;

    @TableField("snapshot_id")
    private Long snapshotId;

    @TableField("client_id")
    private Long clientId;

    @TableField("client_code")
    private String clientCode;

    @TableField("client_name")
    private String clientName;

    @TableField("uscc")
    private String uscc;

    @TableField("model_code")
    private String modelCode;

    @TableField("model_name")
    private String modelName;

    @TableField("score")
    private String score;

    @TableField("final_score")
    private String finalScore;

    @TableField("model_score")
    private String modelScore;

    @TableField("belong_dept_id")
    private Long belongDeptId;

    @TableField("belong_sponsor_user_id")
    private Long belongSponsorUserId;

    @TableField("process_status")
    private String processStatus;

    @TableField("rating_status")
    private Boolean ratingStatus;

    @TableField("overturn")
    private Boolean overturn;

    @TableField("overturn_score")
    private String overturnScore;

    @TableField("adjust")
    private Boolean adjust;

    @TableField("adjust_score")
    private String adjustScore;

    @TableField("effect_time")
    private LocalDate effectTime;

    @TableField("abandon_time")
    private LocalDate abandonTime;

    @TableField("deleted")
    private Boolean deleted;

    @TableField("pd")
    private String pd;

    @TableField("rating_type")
    private String ratingType;

    @Override
    public void setMainId(Long id) {
        setId(id);
    }

    @Override
    public Long getMainId() {
        return getId();
    }
}
