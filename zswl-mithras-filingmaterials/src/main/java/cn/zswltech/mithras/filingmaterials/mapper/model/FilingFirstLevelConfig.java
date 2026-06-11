package cn.zswltech.mithras.filingmaterials.mapper.model;

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
 * @TableName filing_materials 资料归档目录配置
 * @date 2025-12-3
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="filing_first_level_config")
@Data
public class FilingFirstLevelConfig extends BaseModel implements Serializable {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联的配置id
     */
    @TableField(value = "filing_materials_config_id")
    private Long filingMaterialsConfigId;

    /**
     * 目录编码
     */
    @TableField(value = "dir_code")
    private String dirCode;
    /**
     * 目录名称
     */
    @TableField(value = "dir_name")
    private String dirName;

    /**
     * 生成规则（0 = 条件生成，1 = 直接生成）
     */
    @TableField(value = "fixed_flag")
    private int fixedFlag;

    /**
     * 条件标识（is_fixed=0 时必填）,与filing_condition_config关联
     */
    @TableField(value = "condition_key")
    private String conditionKey;

    /**
     * 排序号
     */
    @TableField(value = "sort_code")
    private Long sortCode;

    /**
     * 是否启用
     */
    @TableField(value = "enable_flag")
    private int enableFlag;
    /**
     * 是否必传（0非必传，1必传）
     */
    @TableField(value = "require_flag")
    private int requireFlag;
}