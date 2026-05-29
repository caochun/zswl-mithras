package cn.zswltech.mithras.service.mapper.model.kpi;

import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.common.enums.ProcessStatus;
import cn.zswltech.mithras.common.model.BaseModel;
import cn.zswltech.mithras.common.model.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2023/6/16
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("kpi_project_distribution")
public class KpiProjectDistribution extends BaseModel implements IEntity {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    @TableField(value = "id")
    private Long id;

    /**
     * 合同id
     */
    @TableField(value = "contract_id")
    private Long contractId;

    /**
     * 是否已分配 {@link YesOrNoNumberEnum#getCode()}
     */
    @TableField(value = "distribution_status")
    private Integer distributionStatus;

    /**
     * 审批状态 {@link ProcessStatus#name()}
     */
    @TableField(value = "approval_status")
    private String approvalStatus;

    @Override
    public void setMainId(Long id) {
        this.id = id;
    }

    @Override
    public Long getMainId() {
        return this.id;
    }
}
