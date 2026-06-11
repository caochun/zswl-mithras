package cn.zswltech.mithras.workbench.mapper.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @description 工作台-龙虎雷达图
 * @author zhaozhengkang
 * @date 2023-05-10
 */
@Data
public class WorkbenchRadarChartMetric extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * metric_name
    */
    @TableField("metric_name")
    private String metricName;

    /**
    * dept_scop
    */
    @TableField("dept_scop")
    private String deptScop;

    /**
     * value
     */
    @TableField("value")
    private String value;

    /**
     * unit
     */
    @TableField("unit")
    private String unit;

    public String identity() {
        return this.deptScop + "_" + this.metricName;
    }

}
