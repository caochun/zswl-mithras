package cn.zswltech.mithras.factory.model;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.mapper.tag.IEntity;
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
public class RatingReportRecord extends BaseModel implements Serializable, IEntity {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("report_id")
    private Long reportId;

    @TableField("service_code")
    private String serviceCode;

    // 定性 ratingQualitativeRSP
    @TableField("qualitative")
    private String qualitative;

    // 定量 ratingQuantitativeRSP
    @TableField("quantitative")
    private String quantitative;

    // 调整事项
    @TableField("adjust_event")
    private String adjustEvent;

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
