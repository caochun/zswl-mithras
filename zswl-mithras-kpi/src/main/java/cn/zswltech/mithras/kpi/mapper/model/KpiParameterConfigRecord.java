package cn.zswltech.mithras.kpi.mapper.model;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @description 绩效考核-参数设置-计算记录表
 * @author vico
 * @date 2024-09-21
 */
@Data
public class KpiParameterConfigRecord extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("batch_number")
    private int batchNumber;

    @TableField("calculate_date")
    private LocalDate calculateDate;

    /**
    * 参数基本表id
    */
    @TableField("parameter_base_id")
    private Long parameterBaseId;

    @TableField("parameter_config_id")
    private Long parameterConfigId;

    /**
    * 项目测算表id
    */
    @TableField("kpi_proj_guess_id")
    private Long kpiProjGuessId;

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

}
