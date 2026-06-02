package cn.zswltech.mithras.ftp.newftp.model.config;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 一年期shibor利率波动配置表
 * @TableName new_ftp_shibor_interest_rate_pricing_config
 */
@TableName(value ="new_ftp_shibor_interest_rate_pricing_config")
@Data
public class NewFtpShiborInterestRatePricingConfig extends BaseModel implements Serializable {
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
     * ftp定价
     */
    @TableField(value = "ftp_pricing")
    private Integer ftpPricing;

    /**
     * 当月平均值
     */
    @TableField(value = "average")
    private Integer average;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}