package cn.zswltech.mithras.financeprojectdistribution.mapper.model;

import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.foundation.persistence.tag.IEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author lllin
 * @date2025/12/19
 * @description 项目利润分配表-部门分配比重信息
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("finance_project_distribution_dept_weight")
public class FinanceProjectDistributionDeptWeight extends BaseModel implements IEntity {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    @TableField(value = "id")
    private Long id;

    /**
     * 项目利润分配表id
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
