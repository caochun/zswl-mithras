package cn.zswltech.mithras.ftp.newftp.model.draft;

import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import cn.zswltech.mithras.ftp.newftp.model.config.NewFtpParameterSettingConfig;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import lombok.Data;

/**
 * ftp参数设定编辑区表
 * @TableName new_ftp_parameter_setting_draft
 */
@TableName(value ="new_ftp_parameter_setting_draft")
@Data
public class NewFtpParameterSettingDraft extends NewFtpParameterSettingConfig implements IEntity, Serializable {

    /**
     * ftp ID
     */
    @TableField(value = "ftp_id")
    private Long ftpId;

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