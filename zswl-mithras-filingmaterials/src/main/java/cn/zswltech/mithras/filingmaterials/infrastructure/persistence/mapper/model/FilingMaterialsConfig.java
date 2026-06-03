package cn.zswltech.mithras.filingmaterials.infrastructure.persistence.mapper.model;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 
 * @author lllin
 * @TableName filing_materials 资料归档配置表
 * @date 2025-12-3
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="filing_materials_config")
@Data
public class FilingMaterialsConfig extends BaseModel implements Serializable {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 归档类型
     */
    @TableField(value = "filing_type")
    private String filingType;

    /**
     * 业务类型
     */
    @TableField(value = "business_type")
    private String businessType;

    /**
     * 是否启用
     */
    @TableField(value = "enable_flag")
    private int enableFlag;

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;

}