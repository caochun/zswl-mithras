package cn.zswltech.mithras.metric.mapper.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author luyi
 * @description metrics
 * @date 2022-12-13
 */
@Data
@TableName("risk_metric")
public class RiskMetric implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 指标名称
     */
    @TableField("metric_name")
    private String metricName;

    /**
     * 指标编号
     */
    @TableField("metric_code")
    private String metricCode;

    /**
     * 单位
     */
    @TableField("unit")
    private String unit;

    /**
     * 报送频率
     */
    @TableField("frequency")
    private String frequency;

    /**
     * 币种
     */
    @TableField("currency")
    private String currency;

    @TableField("create_time")
    private LocalDateTime createTime;
    @TableField("create_by")
    private Long createBy;
    @TableField("update_time")
    private LocalDateTime updateTime;
    @TableField("update_by")
    private Long updateBy;

}
