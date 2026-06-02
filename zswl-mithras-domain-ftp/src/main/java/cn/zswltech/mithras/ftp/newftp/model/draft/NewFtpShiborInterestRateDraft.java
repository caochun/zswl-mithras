package cn.zswltech.mithras.ftp.newftp.model.draft;

import cn.zswltech.mithras.ftp.enums.FtpFrequency;
import cn.zswltech.mithras.service.mapper.tag.IEntity;
import cn.zswltech.mithras.ftp.newftp.model.config.NewFtpShiborInterestRateConfig;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 一年期shibor利率编辑区表
 * @TableName new_ftp_shibor_interest_rate_draft
 */
@TableName(value ="new_ftp_shibor_interest_rate_draft")
@Data
public class NewFtpShiborInterestRateDraft extends NewFtpShiborInterestRateConfig implements IEntity, Serializable {


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