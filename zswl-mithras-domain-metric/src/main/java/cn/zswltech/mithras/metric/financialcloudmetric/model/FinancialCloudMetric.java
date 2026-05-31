package cn.zswltech.mithras.metric.financialcloudmetric.model;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhaozhengkang
 * @description financial_cloud_metric
 * @date 2023-04-12
 */
@Data
public class FinancialCloudMetric extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 一级分类
     */
    @TableField("one_level_type")
    private String oneLevelType;

    /**
     * 二级分类
     */
    @TableField("two_level_type")
    private String twoLevelType;

    /**
     * 指标名称
     */
    @TableField("metric_name")
    private String metricName;

    /**
     * 指标大类
     */
    @TableField("metric_first_type")
    private String metricFirstType;

    /**
     * 指标小类
     */
    @TableField("metric_second_type")
    private String metricSecondType;

    /**
     * 指标单位
     */
    @TableField("unit")
    private String unit;

    /**
     * 报送频率
     */
    @TableField("frequency")
    private String frequency;

    @TableField("metric_code")
    private String metricCode;

    @TableField("sort_no")
    private Integer sortNo;
}
