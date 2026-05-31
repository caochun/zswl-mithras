package cn.zswltech.mithras.metric.mapper.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author yibin
 */
@Data
@TableName("risk_metric_dict")
public class RiskMetricDict {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("dict_type")
    private String dictType;

    @TableField("dict_key")
    private String dictKey;

    @TableField("dict_value")
    private String dickValue;

    @TableField("dict_desc")
    private String dictDesc;
}
