package cn.zswltech.mithras.afterlease.mapper.model;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 租后-罚息减免基本表
 * @author vico
 * @date 2022-11-19
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Deprecated
public class CollectionPenaltyReductionInfo extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
    * 合同id
    */
    @TableField("contract_id")
    private Long contractId;

    /**
    * 罚息减免金额
    */
    @TableField("penalty_interest_deduction_amount")
    private Long penaltyInterestDeductionAmount;

    /**
     * 罚息减免剩余金额
     */
    @TableField("penalty_interest_surplus_amount")
    private Long penaltyInterestSurplusAmount;

    /**
    * 原因简述
    */
    @TableField("reason_explain")
    private String reasonExplain;

    /**
    * 流程状态
    */
    @TableField("process_status")
    private String processStatus;

    /**
    * 罚息减免状态 RecordStatus
    */
    @TableField("collection_status")
    private String collectionStatus;

}
