package cn.zswltech.mithras.ftp.newftp.model.draft;

import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import cn.zswltech.mithras.ftp.newftp.model.config.NewFtpTreasuryBondYieldPricingConfig;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import lombok.Data;

/**
 * 十年国债收益波动情况编辑区表
 * @TableName new_ftp_treasury_bond_yield_pricing_draft
 */
@TableName(value ="new_ftp_treasury_bond_yield_pricing_draft")
@Data
public class NewFtpTreasuryBondYieldPricingDraft extends NewFtpTreasuryBondYieldPricingConfig implements IEntity, Serializable {

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