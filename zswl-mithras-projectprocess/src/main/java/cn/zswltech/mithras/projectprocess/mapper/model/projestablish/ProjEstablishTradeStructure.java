package cn.zswltech.mithras.projectprocess.mapper.model.projestablish;

import cn.zswltech.mithras.projectprocess.enums.TradeStructureRoleEnum;
import cn.zswltech.mithras.service.mapper.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2025/7/18
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("proj_establish_trade_structure")
public class ProjEstablishTradeStructure extends BaseModelWithLogicDelete {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    @TableField(value = "id")
    private Long id;

    /**
     * 立项id
     */
    @TableField(value = "proj_establish_id")
    private Long projEstablishId;

    /**
     * 交易结构中的角色 {@link TradeStructureRoleEnum#name()}
     */
    @TableField(value = "role")
    private String role;

    /**
     * 客户id
     */
    @TableField(value = "client_id")
    private Long clientId;
}
