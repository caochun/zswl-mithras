package cn.zswltech.mithras.contract.mapper.model.contract;

import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * @author dingqi
 * @date 2022/9/14
 * @description
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("contract_remind_record")
public class ContractRemindRecord extends BaseModel {
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
     * 合同编号
     */
    @TableField("contract_code")
    private String contractCode;

    /**
     * 付款id
     */
    @TableField("payment_id")
    private Long paymentId;

    /**
     * 通知间隔 s
     **/
    @TableField("intervals")
    private Long intervals;

    /**
     * 付款编号
     */
    @TableField("payment_code")
    private String paymentCode;

    /**
     * 合同创建者id
     */
    @TableField("contract_creator_id")
    private Long contractCreatorId;

    /**
     * 合同创建时间
     */
    @TableField("contract_create_time")
    private LocalDateTime contractCreateTime;
}
