package cn.zswltech.mithras.third.dataminer.persistence.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @description dm财报科目字段映射表
 * @author dingqi
 * @date 2024-04-01
 */
@Data
@TableName("dm_subject_field_mapping")
public class DmSubjectFieldMapping implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * dm字段名称
    */
    @TableField("dm_field_name")
    private String dmFieldName;

    /**
    * dm字段描述
    */
    @TableField("dm_field_comment")
    private String dmFieldComment;

    /**
    * 融租易客户财报科目类型
    */
    @TableField("rzy_subject_type")
    private String rzySubjectType;

    /**
    * 融租易客户财报科目代码
    */
    @TableField("rzy_subject_code")
    private String rzySubjectCode;

}
