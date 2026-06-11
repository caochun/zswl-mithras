package cn.zswltech.mithras.document.mapper.model;

import cn.zswltech.mithras.document.enums.FileTemplateKeyEnum;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author yibin
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("file_template")
@Accessors(chain = true)
public class FileTemplate extends BaseModel {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("filename")
    private String filename;

    private String templateType;

    /**
     * 文件模板key {@link FileTemplateKeyEnum}
     */
    @TableField("file_template_key")
    private String fileTemplateKey;

    /**
     * 合同面签是否需要展示 0：不需要 1：需要
     */
    @TableField(value = "face_sign_show_flag")
    private Integer faceSignShowFlag;

    /**
     * 过时的
     */
    private Boolean outdated;

}
