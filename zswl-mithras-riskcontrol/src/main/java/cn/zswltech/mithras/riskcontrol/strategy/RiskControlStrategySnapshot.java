package cn.zswltech.mithras.riskcontrol.strategy;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/6/6 10:59
 */
@Data
public class RiskControlStrategySnapshot extends BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 数据快照日期
     */
    @TableField("date")
    private LocalDate date;
    /**
     * 指标id
     */
    @TableField("metric_id")
    private Long metricId;
    /**
     * 值1
     */
    @TableField("value_one")
    private Long valueOne;
    /**
     * 值2
     */
    @TableField("value_two")
    private Long valueTwo;

    @TableField("quick_context")
    private String quickContext;
}
