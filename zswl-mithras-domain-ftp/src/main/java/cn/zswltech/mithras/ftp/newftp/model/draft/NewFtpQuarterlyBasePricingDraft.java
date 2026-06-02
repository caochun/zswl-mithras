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
 * 季度指导基础定价编辑区表
 * @TableName new_ftp_quarterly_base_pricing_draft
 */
@TableName(value ="new_ftp_quarterly_base_pricing_draft")
@Data
public class NewFtpQuarterlyBasePricingDraft extends BaseModel implements IEntity, Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属指引id
     */
    @TableField(value = "ftp_id")
    private Long ftpId;

    /**
     * 资产行业分类
     */
    @TableField(value = "asset_industry_classify")
    private String assetIndustryClassify;

    /**
     * 地区分类
     */
    @TableField(value = "regional_classify")
    private String regionalClassify;

    /**
     * 期项范围
     */
    @TableField(value = "term_range")
    private String termRange;

    /**
     * 客户主体分类
     */
    @TableField(value = "customer_entity_classify")
    private String customerEntityClassify;

    /**
     * 利率值
     */
    @TableField(value = "value")
    private Integer value;

    /**
     * 在excel中的坐标
     */
    @TableField(value = "location")
    private String location;

    /**
     * 模版ID
     */
    @TableField(value = "template_id")
    private Long templateId;

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