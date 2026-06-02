package cn.zswltech.mithras.ftp.newftp.model.draft;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.mapper.tag.IEntity;
import cn.zswltech.mithras.ftp.newftp.model.config.NewFtpMonthlyGuidanceTemplateConfig;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * ftp报价编辑区表
 * @TableName new_ftp_monthly_guidance_template_draft
 */
@TableName(value ="new_ftp_monthly_guidance_template_draft")
@Data
public class NewFtpMonthlyGuidanceTemplateDraft extends NewFtpMonthlyGuidanceTemplateConfig implements IEntity, Serializable {

    /**
     * 所属的主数据id
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