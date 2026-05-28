package cn.zswltech.mithras.service.service.newftp.model.draft;

import cn.zswltech.mithras.service.mapper.tag.IEntity;
import cn.zswltech.mithras.service.service.newftp.model.config.NewFtpShiborInterestRatePricingConfig;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import lombok.Data;

/**
 * 一年期shibor利率波动编辑区表
 * @TableName new_ftp_shibor_interest_rate_pricing_draft
 */
@TableName(value ="new_ftp_shibor_interest_rate_pricing_draft")
@Data
public class NewFtpShiborInterestRatePricingDraft extends NewFtpShiborInterestRatePricingConfig implements IEntity, Serializable {

    /**
     * 关联台账记录ID
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