package cn.zswltech.mithras.service.mapper.model.archives;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @TableName archives_management
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="archives_management")
@Data
public class ArchivesManagement extends BaseModel implements Serializable {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 项目id
     */
    @TableField(value = "proj_id")
    private Long projId;

    /**
     * 模版id
     */
    @TableField(value = "template_id")
    private Long templateId;

    /**
     * 归档状态
     */
    @TableField(value = "status")
    private String status;

    /**
     * 审批状态
     */
    @TableField(value = "flow_status")
    private String flowStatus;

    /**
     * 任务类型：0 系统 1 手动
     */
    @TableField(value = "type")
    private Integer type;

    /**
     * 任务编号
     */
    @TableField(value = "archives_code")
    private String archivesCode;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}