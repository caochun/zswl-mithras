package cn.zswltech.mithras.service.service.newftp.model.draft;

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
 * ftp指导报价扩展编辑区表（下半部分）
 * @TableName new_ftp_monthly_guidance_ext_draft
 */
@TableName(value ="new_ftp_monthly_guidance_ext_draft")
@Data
public class NewFtpMonthlyGuidanceExtDraft extends BaseModel implements IEntity, Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属的主数据id
     */
    @TableField(value = "ftp_id")
    private Long ftpId;

    /**
     * 一年期（含）以内
     */
    @TableField(value = "one_year")
    private Integer oneYear;

    /**
     * 一至三年期（含）
     */
    @TableField(value = "one_to_three_year")
    private Integer oneToThreeYear;

    /**
     * 三年以上
     */
    @TableField(value = "more_than_three_year")
    private Integer moreThanThreeYear;

    /**
     * 卖出价  
     */
    @TableField(value = "selling_price")
    private Integer sellingPrice;

    /**
     * 买入价  
     */
    @TableField(value = "buying_price")
    private String buyingPrice;

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