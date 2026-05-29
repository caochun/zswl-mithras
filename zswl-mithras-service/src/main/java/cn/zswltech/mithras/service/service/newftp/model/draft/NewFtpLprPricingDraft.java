package cn.zswltech.mithras.service.service.newftp.model.draft;

import cn.zswltech.mithras.service.enums.ftp.FtpFrequency;
import cn.zswltech.mithras.service.mapper.model.basedata.BaseDataLpr;
import cn.zswltech.mithras.common.model.IEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * lpr配置编辑区表
 * @TableName new_ftp_lpr_pricing_draft
 */
@TableName(value ="new_ftp_lpr_pricing_draft")
@Data
public class NewFtpLprPricingDraft extends BaseDataLpr implements IEntity, Serializable {

    /**
     * 关联台账记录ID
     */
    @TableField(value = "ftp_id")
    private Long ftpId;

    /**
     * 频率
     * {@link FtpFrequency#name()}
     **/
    @TableField(value = "frequency")
    private String frequency;

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