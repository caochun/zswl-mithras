package cn.zswltech.mithras.metric.mapper.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author luyi
 * @description 指标因子
 * @date 2022-12-16
 */
@Data
@TableName("risk_metric_factor")
public class RiskMetricFactor implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * factor_name
     */
    @TableField("factor_name")
    private String factorName;

    @TableField("factor_code")
    private String factorCode;

    /**
     * factor_date
     */
    @TableField("factor_date")
    private LocalDate factorDate;

    /**
     * factor_table
     */
    @TableField("factor_table")
    private String factorTable;

    /**
     * factor_value
     */
    @TableField("factor_value")
    private Long factorValue;

    @TableField("factor_json")
    private String factorJson;

    /**
     * 因子来源
     */
    @TableField("factor_source")
    private String factorSource;

    @TableField("create_time")
    private LocalDateTime createTime;

}
