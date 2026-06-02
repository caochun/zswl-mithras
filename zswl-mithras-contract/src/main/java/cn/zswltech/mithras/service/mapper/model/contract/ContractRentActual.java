package cn.zswltech.mithras.service.mapper.model.contract;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.service.mapper.tag.IEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2022/8/12
 * @description 合同明细-实际租金
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("contract_rent_actual")
public class ContractRentActual extends BaseModel implements IEntity {
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
     * 借据id
     */
    @TableField("receipt_id")
    private Long receiptId;

    /**
     * 现金流编号
     */
    @TableField("cash_flow_code")
    private String cashFlowCode;

    /**
     * 日期
     */
    @TableField(value = "cash_flow_date", updateStrategy = FieldStrategy.IGNORED)
    private LocalDate cashFlowDate;

    /**
     * 期项
     */
    @TableField("cash_flow_phase")
    private Integer cashFlowPhase;

    /**
     * 租金
     */
    @TableField(value = "rent", updateStrategy = FieldStrategy.IGNORED)
    private Long rent;

    /**
     * 本金
     */
    @TableField(value = "principal", updateStrategy = FieldStrategy.IGNORED)
    private Long principal;

    /**
     * 利息
     */
    @TableField(value = "interest", updateStrategy = FieldStrategy.IGNORED)
    private Long interest;

    /**
     * 剩余本金
     */
    @TableField(value = "remaining_principal", updateStrategy = FieldStrategy.IGNORED)
    private Long remainingPrincipal;

    /**
     * 实收日期
     */
    @TableField(exist = false)
    private LocalDate collectionDate;

    /**
     * 实收金额
     */
    @TableField(exist = false)
    private Long collectionAmount;

    @Override
    public void setMainId(Long id) {
        this.contractId = id;
    }

    @Override
    public Long getMainId() {
        return contractId;
    }
}
