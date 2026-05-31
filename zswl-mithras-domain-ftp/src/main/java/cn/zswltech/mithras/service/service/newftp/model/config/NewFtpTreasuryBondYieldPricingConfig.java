package cn.zswltech.mithras.service.service.newftp.model.config;

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
 * 十年国债收益波动情况配置表
 * @TableName new_ftp_treasury_bond_yield_pricing_config
 */
@TableName(value ="new_ftp_treasury_bond_yield_pricing_config")
@Data
public class NewFtpTreasuryBondYieldPricingConfig extends BaseModel implements Serializable {
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
     * 波动幅度
     */
    @TableField(value = "fluctuation_range")
    private Integer fluctuationRange;

    /**
     * FTP计价
     */
    @TableField(value = "ftp_pricing")
    private Integer ftpPricing;

    /**
     * 当月均值
     */
    @TableField(value = "average")
    private Integer average;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}