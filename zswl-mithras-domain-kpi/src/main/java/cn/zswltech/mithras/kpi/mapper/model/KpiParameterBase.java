package cn.zswltech.mithras.kpi.mapper.model;
import cn.zswltech.mithras.service.enums.common.RecordStatus;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @description 绩效考核-参数设置基本表
 * @author vico
 * @date 2024-09-21
 */
@Data
public class KpiParameterBase extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 生效月份
    */
    @TableField("effect_month")
    private LocalDate effectMonth;

    /**
     * 参数来源
     **/
    @TableField("source")
    private String source;

    /**
    * 参数状态
     * {@link RecordStatus#name(}
    */
    @TableField("parameter_status")
    private String parameterStatus;

}
