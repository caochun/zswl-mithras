package cn.zswltech.mithras.workbench.mapper.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @description 工作台-柱状图指标
 * @author zhaozhengkang
 * @date 2023-05-09
 */
@Data
@Accessors(chain = true)
public class WorkbenchBarChartMetric extends BaseModel implements Serializable {

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
    * 范围 本周/本月/本季度/本年
    */
    @TableField("scop")
    private String scop;

    /**
    * 部门code
    */
    @TableField("dept_code")
    private String deptCode;

    /**
     * 指标值
     */
    @TableField("value")
    private String value;

    /**
     * 指标单位
     */
    @TableField("unit")
    private String unit;


    public String identity() {
        return String.join("-", deptCode, scop, metricName);
    }

}
