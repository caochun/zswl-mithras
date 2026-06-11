package cn.zswltech.mithras.contract.model.contract;

import cn.zswltech.mithras.foundation.persistence.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2025/9/17
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("contract_trade_structure")
public class ContractTradeStructure extends BaseModelWithLogicDelete {
    @TableId(type = IdType.AUTO)
    @TableField(value = "id")
    private Long id;

    @TableField(value = "contract_id")
    private Long contractId;

    @TableField(value = "role")
    private String role;

    @TableField(value = "client_id")
    private Long clientId;
}
