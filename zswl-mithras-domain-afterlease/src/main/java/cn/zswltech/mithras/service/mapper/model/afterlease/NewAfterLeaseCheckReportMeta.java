package cn.zswltech.mithras.service.mapper.model.afterlease;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.mapper.tag.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author dingqi
 * @date 2022/12/16
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("new_after_lease_check_report_meta")
public class NewAfterLeaseCheckReportMeta extends BaseModel implements Serializable, IEntity {
    private static final long serialVersionUID = 2796660719738731116L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("check_plan_client_id")
    private Long checkPlanClientId;

    @TableField("report_type")
    private String reportType;

    @TableField("report_template_version")
    private String reportTemplateVersion;

    @Override
    public void setMainId(Long id) {
        this.checkPlanClientId = id;
    }

    @Override
    public Long getMainId() {
        return this.checkPlanClientId;
    }
}
