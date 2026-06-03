package cn.zswltech.mithras.archives.infrastructure.persistence.model;

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
 * @TableName archive_file_type
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="archive_file_type")
@Data
public class ArchiveFileType extends BaseModel implements Serializable {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 文档类型
     */
    @TableField(value = "type_name")
    private String typeName;

    /**
     * 0 不必传 1 必传
     */
    @TableField(value = "need")
    private Integer need;

    /**
     * 分组id
     */
    @TableField(value = "group_id")
    private Long groupId;

    /**
     * 排序
     */
    @TableField(value = "sort")
    private Integer sort;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}