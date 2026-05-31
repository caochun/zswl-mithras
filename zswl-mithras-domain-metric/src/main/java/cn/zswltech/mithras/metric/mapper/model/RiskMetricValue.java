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
 * @description metrics
 * @date 2022-12-13
 */
@Data
@TableName("risk_metric_value")
public class RiskMetricValue implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("risk_metric_id")
    private Long riskMetricId;

    @TableField("metric_code")
    private String metricCode;

    /**
     * 当期值
     */
    @TableField("metric_value")
    private Long metricValue;

    @TableField("metric_value_adjusted")
    private Long metricValueAdjusted;

    /**
     * 数据日期
     */
    @TableField("data_time")
    private LocalDate dataTime;

    /**
     * 数据来源
     */
    @TableField("data_source")
    private String dataSource;

    @TableField("status")
    private String status;

    @TableField("need_report")
    private Boolean needReport;

    @TableField("create_time")
    private LocalDateTime createTime;
    @TableField("create_by")
    private Long createBy;
    @TableField("update_time")
    private LocalDateTime updateTime;
    @TableField("update_by")
    private Long updateBy;

    //非数据字段
    @TableField(value = "metric_name", exist = false)
    private String metricName;
    @TableField(value = "unit", exist = false)
    private String unit;
    @TableField(value = "frequency", exist = false)
    private String frequency;


}
