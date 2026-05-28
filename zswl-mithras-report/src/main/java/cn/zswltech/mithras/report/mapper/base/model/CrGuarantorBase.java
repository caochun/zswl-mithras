package cn.zswltech.mithras.report.mapper.base.model;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import lombok.*;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.experimental.Accessors;

/**
 * @author wang
 * @description 征信报送-保证表
 * @date 2022-10-08
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CrGuarantorBase extends CrBaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 客户id
     */
    @TableField("client_id")
    private Long clientId;

    /**
     * 客户名称
     */
    @TableField(value = "client_name")
    private String clientName;

    /**
     * 借据编号
     */
    @TableField("payment_apply_code")
    private String paymentApplyCode;

    /**
     * 客户分类
     */
    @TableField(value = "client_type")
    private String clientType;

    /**
     * 身份标识类型
     */
    @TableField(value = "guarantor_id_type", updateStrategy = FieldStrategy.IGNORED)
    private String guarantorIdType;

    /**
     * 身份标识号码
     */
    @TableField(value = "guarantor_id", updateStrategy = FieldStrategy.IGNORED)
    private String guarantorId;

    /**
     * 客户类型
     */
    @TableField(value = "client_class", updateStrategy = FieldStrategy.IGNORED)
    private String clientClass;

    /**
     * 还款责任金额（单位：0.0001元）
     */
    @TableField(value = "repay_liability_amount", updateStrategy = FieldStrategy.IGNORED)
    private Long repayLiabilityAmount;

    /**
     * 联保标志
     */
    @TableField(value = "joint_guarantor_flag", updateStrategy = FieldStrategy.IGNORED)
    private String jointGuarantorFlag;

    /**
     * 保证合同编号
     */
    @TableField(value = "guarante_contract_code", updateStrategy = FieldStrategy.IGNORED)
    private String guaranteContractCode;

    /**
     * 保证合同编号
     */
    @TableField(value = "guarante_contract_code_2", updateStrategy = FieldStrategy.IGNORED)
    private String guaranteContractCode2;

    /**
     * 付款id
     */
    @TableField(value = "payment_id")
    private Long paymentId;

    /**
     * @param source tenantry / guarantor
     * @return
     */
    public String genBusinessKey(String source) {
        return String.format("%s_%s_%s_%s_%s", paymentApplyCode, getContractId(), paymentId, clientId, source);
    }


}
