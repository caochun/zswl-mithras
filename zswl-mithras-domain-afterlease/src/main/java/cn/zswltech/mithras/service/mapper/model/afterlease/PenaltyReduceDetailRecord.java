package cn.zswltech.mithras.service.mapper.model.afterlease;

import cn.zswltech.mithras.service.mapper.model.BaseModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * @description 罚息减免明细表
 * @author vico
 * @date 2024-08-21
 */
@Data
public class PenaltyReduceDetailRecord extends BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
    * 收款明细id
    */
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("reduce_base_id")
    private Long reduceBaseId;

    @TableField("collection_id")
    private Long collectionId;

    /**
    * 客户id
    */
    @TableField("client_id")
    private Long clientId;

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
    * 借据id
    */
    @TableField("receipt_id")
    private Long receiptId;

    /**
    * 借据编号
    */
    @TableField("receipt_code")
    private String receiptCode;

    /**
    * 期项
    */
    @TableField("phase")
    private Integer phase;

    /**
    * 合同金额
    */
    @TableField("apply_credit_amount")
    private Long applyCreditAmount;

    /**
    * 计划收款金额
    */
    @TableField("plan_collection_amount")
    private Long planCollectionAmount;

    /**
    * 计划收款日期
    */
    @TableField("plan_collection_date")
    private LocalDate planCollectionDate;

    /**
    * 实收金额
    */
    @TableField("collection_amount")
    private Long collectionAmount;

    /**
    * 罚息截止日
    */
    @TableField("penalty_close_date")
    private LocalDate penaltyCloseDate;

    /**
    * 应收罚息
    */
    @TableField("penalty_interest")
    private Long penaltyInterest;

    /**
    * 申请减免罚息
    */
    @TableField("reduce_penalty_interest")
    private Long reducePenaltyInterest;

}
