package cn.zswltech.mithras.service.mapper.model.workbench;

import cn.zswltech.mithras.common.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhaozhengkang
 * @description 工作台-卡片指标
 * @date 2023-05-09
 */
@Data
public class WorkbenchCardMetric extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 指标名称
     */
    @TableField("metric_name")
    private String metricName;

    /**
     * 统计范围
     */
    @TableField("scope")
    private String scope;

    /**
     * 值
     */
    @TableField("value")
    private String value;

    /**
     * metric_code
     */
    @TableField("metric_code")
    private String metricCode;

    /**
     * 指标单位
     */
    @TableField("unit")
    private String unit;

    /**
     * 关联到的指标code
     */
    @TableField("relate_metric_code")
    private String relateMetricCode;

    /**
     * 是否为饼图
     */
    @TableField("pie_chart")
    private Boolean pieChart;

}
