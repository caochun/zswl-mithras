package cn.zswltech.mithras.service.service.newftp.model.config;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * lpr配置模版表
 * @TableName new_ftp_lpr_pricing_config
 */
@TableName(value ="new_ftp_lpr_pricing_config")
@Data
public class NewFtpLprPricingConfig extends BaseModel implements Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 月份
     */
    @TableField(value = "month")
    private LocalDate month;

    /**
     * 期限
     */
    @TableField(value = "term_range")
    private String termRange;

    /**
     * lpr计价值
     */
    @TableField(value = "lpr")
    private Integer lpr;

    /**
     * lpr定价
     */
    @TableField(value = "lpr_pricing")
    private Integer lprPricing;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}