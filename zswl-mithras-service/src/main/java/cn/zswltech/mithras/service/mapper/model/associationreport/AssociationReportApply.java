package cn.zswltech.mithras.service.mapper.model.associationreport;
import cn.zswltech.mithras.service.mapper.tag.IEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import lombok.EqualsAndHashCode;

/**
 * @description 金融局报表申请表
 * @author hspcadmin
 * @date 2025-09-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("association_report_apply")
public class AssociationReportApply extends BaseModel implements Serializable, IEntity {

    private static final long serialVersionUID = 1L;

    /**
    * 主键id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 申请的报表实例唯一标识，多个以逗号分隔
    */
    @TableField("report_instance_ids")
    private String reportInstanceIds;

    /**
    * 审批状态
    */
    @TableField("approval_status")
    private String approvalStatus;

    /**
    * 逻辑删除，0-未删除，1-已删除
    */
    @TableField("deleted")
    private Integer deleted;

    @Override
    public void setMainId(Long id) {
        setId(id);
    }

    @Override
    public Long getMainId() {
        return getId();
    }
}
