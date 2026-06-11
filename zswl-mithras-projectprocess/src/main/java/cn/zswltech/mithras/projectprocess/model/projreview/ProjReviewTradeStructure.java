package cn.zswltech.mithras.projectprocess.model.projreview;

import cn.zswltech.mithras.projectprocess.enums.TradeStructureRoleEnum;
import cn.zswltech.mithras.foundation.persistence.model.BaseModelWithLogicDelete;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author dingqi
 * @date 2025/7/24
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("proj_review_trade_structure")
public class ProjReviewTradeStructure extends BaseModelWithLogicDelete {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    @TableField(value = "id")
    private Long id;

    /**
     * 评审id
     */
    @TableField(value = "proj_review_id")
    private Long projReviewId;

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