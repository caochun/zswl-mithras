package cn.zswltech.mithras.ftp.newftp.model.draft;

import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import cn.zswltech.mithras.ftp.newftp.model.config.NewFtpGuaranteeCostPricingConfig;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import lombok.Data;

/**
 * 担保成本配置表
 * @TableName new_ftp_guarantee_cost_pricing_draft
 */
@TableName(value ="new_ftp_guarantee_cost_pricing_draft")
@Data
public class NewFtpGuaranteeCostPricingDraft extends NewFtpGuaranteeCostPricingConfig implements IEntity, Serializable {

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

    public Integer getActualCurrentAverage() {
        return this.getHandCurrentAverage() == null ? this.getCurrentAverage() : this.getHandCurrentAverage();
    }
}