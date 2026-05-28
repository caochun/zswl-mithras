package cn.zswltech.mithras.service.mapper.model.afterlease;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @ClassName CollectionPenaltyReductionRelation
 * 罚息减免期项关系
 * @Author jackerhe
 * @Date 2022/11/21 10:34 上午
 * @Version 1.0
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class CollectionPenaltyReductionRelation extends BaseModel implements Serializable {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 减免基本表id
     */
    @TableField("reduce_id")
    private Long reduceId;

    /**
     * 付款code 借据编号
     */
    @TableField("payment_code")
    private String paymentCode;

    /**
     * 收款编号 现金流编号
     */
    @TableField("code")
    private String code;

    /**
     * 罚息减免剩余金额
     */
    @TableField("reduce_amount")
    private Long reduceAmount;

    /**
     * 罚息减免状态 0失效，1生效
     */
    @TableField("status")
    private Long status;

}
