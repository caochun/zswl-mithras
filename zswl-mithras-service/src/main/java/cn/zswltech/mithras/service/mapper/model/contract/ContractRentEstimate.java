package cn.zswltech.mithras.service.mapper.model.contract;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.mapper.tag.IEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2022/8/12
 * @description 合同明细-概算租金
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("contract_rent_estimate")
public class ContractRentEstimate extends BaseModel implements IEntity {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    /**
     * 合同id
     */
    @TableField("contract_id")
    private Long contractId;

    /**
     * 日期
     */
    @TableField("cash_flow_date")
    private LocalDate cashFlowDate;

    /**
     * 期项
     */
    @TableField("cash_flow_phase")
    private Integer cashFlowPhase;

    /**
     * 租金
     */
    @TableField("rent")
    private Long rent;

    /**
     * 本金
     */
    @TableField("principal")
    private Long principal;

    /**
     * 利息
     */
    @TableField("interest")
    private Long interest;

    /**
     * 剩余本金
     */
    @TableField("remaining_principal")
    private Long remainingPrincipal;

    @Override
    public void setMainId(Long id) {
        this.contractId = id;
    }

    @Override
    public Long getMainId() {
        return contractId;
    }
}
