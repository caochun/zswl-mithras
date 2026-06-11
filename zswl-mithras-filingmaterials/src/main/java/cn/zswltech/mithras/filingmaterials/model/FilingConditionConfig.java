package cn.zswltech.mithras.filingmaterials.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 
 * @author lllin
 * @TableName filing_materials 资料归档条件配置表
 * @date 2025-12-3
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="filing_condition_config")
@Data
public class FilingConditionConfig extends BaseModel implements Serializable {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 条件编码（与枚举类 DirConditionKeyEnum 的 code 严格一致，是目录与条件关联的核心）
     */
    @TableField(value = "condition_key")
    private String conditionKey;

    /**
     * 条件描述
     */
    @TableField(value = "condition_desc")
    private String conditionDesc;

    /**
     * 是否启用
     */
    @TableField(value = "enable_flag")
    private int enableFlag;

}