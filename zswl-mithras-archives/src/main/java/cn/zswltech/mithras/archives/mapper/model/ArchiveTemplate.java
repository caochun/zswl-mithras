package cn.zswltech.mithras.archives.mapper.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @TableName archive_template
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="archive_template")
@Data
public class ArchiveTemplate extends BaseModel implements Serializable {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 模版名称
     */
    @TableField(value = "template_name")
    private String templateName;

    /**
     * 适用业务类型
     */
    @TableField(value = "template_type")
    private String templateType;

    /**
     * 禁用 启用
     */
    @TableField(value = "status")
    private String status;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
