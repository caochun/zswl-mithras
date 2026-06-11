package cn.zswltech.mithras.budget.mapper.model;

import cn.zswltech.mithras.budget.enums.BudgetConfigTypeEnum;
import cn.zswltech.mithras.foundation.persistence.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @description 预算管理-参数设置
 * @author vico
 * @date 2025-04-11
 */
@Data
public class BudgetParameterConfig extends BaseModelWithLogicDelete implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 主键id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 参数key {@link BudgetConfigTypeEnum#name()}
    */
    @TableField("config_key")
    private String configKey;

    /**
    * 参数name
    */
    @TableField("config_name")
    private String configName;

    /**
    * 参数value（json）
    */
    @TableField("config_value")
    private String configValue;

}
