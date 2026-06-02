package cn.zswltech.mithras.ftp.newftp.model.config;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 融资成本配置表
 * @TableName new_ftp_financing_cost_pricing_config
 */
@TableName(value ="new_ftp_financing_cost_pricing_config")
@Data
public class NewFtpFinancingCostPricingConfig extends BaseModel implements Serializable {
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
     * 当期均值
     */
    @TableField(value = "current_average")
    private Integer currentAverage;

    /**
     * 手工当期均值
     */
    @TableField(value = "hand_current_average")
    private Integer handCurrentAverage;

    /**
     * 当季均值
     */
    @TableField(value = "current_quarter_average")
    private Integer currentQuarterAverage;

    /**
     * 手工当季均值
     */
    @TableField(value = "hand_current_quarter_average")
    private Integer handCurrentQuarterAverage;

    /**
     * 当年均值
     */
    @TableField(value = "annual_average")
    private Integer annualAverage;

    /**
     * 手工当年均值
     */
    @TableField(value = "hand_annual_average")
    private Integer handAnnualAverage;

    /**
     * ftp定价
     */
    @TableField(value = "ftp_pricing")
    private Integer ftpPricing;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}