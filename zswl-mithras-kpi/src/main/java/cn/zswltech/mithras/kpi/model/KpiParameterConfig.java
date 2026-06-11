package cn.zswltech.mithras.kpi.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("kpi_parameter_config")
public class KpiParameterConfig extends BaseModel {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;
    /**
     * 参数code
     */
    @TableField("config_code")
    private String configCode;
    /**
     * 参数描述
     */
    @TableField("config_desc")
    private String configDesc;
    /**
     * 参数值
     */
    @TableField("config_value")
    private String configValue;

    @TableField("parameter_base_id")
    private Long parameterBaseId;
}