package cn.zswltech.mithras.ftp.newftp.model.config;

import cn.zswltech.mithras.ftp.enums.FtpBusinessVersion;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.mapper.tag.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * ftp报价模版配置表
 * @TableName new_ftp_monthly_guidance_template_config
 */
@TableName(value ="new_ftp_monthly_guidance_template_config")
@Data
public class NewFtpMonthlyGuidanceTemplateConfig extends BaseModel implements Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 风控行业分类
     */
    @TableField(value = "risk_industry_classify")
    private String riskIndustryClassify;

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
     * ftp值
     */
    @TableField(value = "value")
    private Integer value;

    /**
     * 在excel中的坐标
     */
    @TableField(value = "location")
    private String location;

    /**
     * 业务版本 {@link FtpBusinessVersion#name()}
     */
    @TableField(value = "ftp_business_version")
    private String ftpBusinessVersion;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}