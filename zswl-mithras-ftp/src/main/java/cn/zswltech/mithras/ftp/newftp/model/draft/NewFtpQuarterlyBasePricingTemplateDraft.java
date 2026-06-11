package cn.zswltech.mithras.ftp.newftp.model.draft;

import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import cn.zswltech.mithras.ftp.newftp.model.config.NewFtpQuarterlyBasePricingTemplateConfig;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 季度指导基础定价编辑区表
 * @TableName new_ftp_quarterly_base_pricing_template_draft
 */
@TableName(value ="new_ftp_quarterly_base_pricing_template_draft")
@Data
public class NewFtpQuarterlyBasePricingTemplateDraft extends NewFtpQuarterlyBasePricingTemplateConfig implements IEntity, Serializable {

    /**
     * 所属指引id
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