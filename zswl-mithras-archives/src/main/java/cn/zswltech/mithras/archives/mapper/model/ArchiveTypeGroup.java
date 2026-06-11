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
 * @TableName archive_type_group
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="archive_type_group")
@Data
public class ArchiveTypeGroup extends BaseModel implements Serializable {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 资料类型
     */
    @TableField(value = "group_name")
    private String groupName;


    /**
     * 模版id
     */
    @TableField(value = "template_id")
    private Long templateId;

    /**
     * 排序
     */
    @TableField(value = "sort")
    private Integer sort;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
