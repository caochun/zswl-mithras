package cn.zswltech.mithras.ftp.newftp.model.config;

import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 担保成本配置表
 * @TableName new_ftp_guarantee_cost_pricing_config
 */
@TableName(value ="new_ftp_guarantee_cost_pricing_config")
@Data
public class NewFtpGuaranteeCostPricingConfig extends BaseModel implements Serializable {
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
     * 当期均值
     */
    @TableField(value = "current_average")
    private Integer currentAverage;

    /**
     * 手工档期均值
     */
    @TableField(value = "hand_current_average")
    private Integer handCurrentAverage;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}