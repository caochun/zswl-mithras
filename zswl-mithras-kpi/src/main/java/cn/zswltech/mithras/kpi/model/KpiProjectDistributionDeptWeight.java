package cn.zswltech.mithras.kpi.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author bigbear
 * @description 绩效考核-项目分配表（新）-部门分配比重信息
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("kpi_project_distribution_dept_weight")
public class KpiProjectDistributionDeptWeight extends BaseModel implements IEntity {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    @TableField(value = "id")
    private Long id;

    /**
     * 项目分配表id
     */
    @TableField(value = "project_distribution_id")
    private Long projectDistributionId;

    /**
     * 分配比重类型
     */
    @TableField(value = "weight_type")
    private String weightType;

    /**
     * 分配比重归属目标
     */
    @TableField(value = "weight_target", updateStrategy = FieldStrategy.IGNORED)
    private Long weightTarget;

    /**
     * 分配比重数值
     */
    @TableField(value = "wight_value", updateStrategy = FieldStrategy.IGNORED)
    private Integer weightValue;

    @Override
    public void setMainId(Long id) {
        this.projectDistributionId = id;
    }

    @Override
    public Long getMainId() {
        return this.projectDistributionId;
    }
}
