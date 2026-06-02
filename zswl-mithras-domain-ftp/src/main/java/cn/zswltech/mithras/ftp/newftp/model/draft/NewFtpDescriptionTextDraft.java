package cn.zswltech.mithras.ftp.newftp.model.draft;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.mapper.tag.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * ftp编辑区文本描述
 * @TableName new_ftp_description_text_draft
 */
@TableName(value ="new_ftp_description_text_draft")
@Data
public class NewFtpDescriptionTextDraft extends BaseModel implements IEntity, Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 主表id
     */
    @TableField(value = "ftp_id")
    private Long ftpId;

    /**
     * 说明类型
     */
    @TableField(value = "desc_type")
    private String descType;

    /**
     * 内容
     */
    @TableField(value = "desc_content")
    private String descContent;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public void setMainId(Long id) {
        ftpId = id;
    }

    @Override
    public Long getMainId() {
        return ftpId;
    }
}