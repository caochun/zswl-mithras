package cn.zswltech.mithras.report.mapper.base.model;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import lombok.*;

import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.experimental.Accessors;

/**
 * @description 征信报送-质押表
 * @author wang
 * @date 2022-10-08
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class CrPledgeBase extends CrBaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 客户id
     */
    @TableField("client_id")
    private Long clientId;

    /**
     * 借据编号
     */
    @TableField("payment_apply_code")
    private String paymentApplyCode;

    /**
     * 借据本金(单位：0.0001元)
     */
    @TableField(value = "apply_payment_amount")
    private Long applyPaymentAmount;

    /**
    * 质押合同编号
    */
    @TableField(value = "pledge_contract_code", updateStrategy = FieldStrategy.IGNORED)
    private String pledgeContractCode;

    /**
     * 质押合同编号
     */
    @TableField(value = "pledge_contract_code_2", updateStrategy = FieldStrategy.IGNORED)
    private String pledgeContractCode2;

    /**
    * 最高额担保标识（0否，1是）
    */
    @TableField(value = "max_flag", updateStrategy = FieldStrategy.IGNORED)
    private Integer maxFlag;

    /**
    * 序号
    */
    @TableField(value = "sequence", updateStrategy = FieldStrategy.IGNORED)
    private String sequence;

    /**
    * 质押物种类
    */
    @TableField(value = "type", updateStrategy = FieldStrategy.IGNORED)
    private String type;

    /**
    * 质物价值
    */
    @TableField(value = "assessed_value", updateStrategy = FieldStrategy.IGNORED)
    private Long assessedValue;

    /**
    * 出质人身份类别
    */
    @TableField(value = "pledge_type", updateStrategy = FieldStrategy.IGNORED)
    private String pledgeType;

    /**
    * 出质人名称
    */
    @TableField(value = "pledge_name", updateStrategy = FieldStrategy.IGNORED)
    private String pledgeName;

    /**
    * 出质人身份标识类型
    */
    @TableField(value = "pledge_id_type", updateStrategy = FieldStrategy.IGNORED)
    private String pledgeIdType;

    /**
    * 出质人身份标识号码
    */
    @TableField(value = "pledge_id", updateStrategy = FieldStrategy.IGNORED)
    private String pledgeId;

    /**
     * 付款id
     */
    @TableField(value = "payment_id")
    private Long paymentId;

    public String genBusinessKey(Long contractPledgeItemId) {
        return String.format("%s_%s_%s", paymentApplyCode, paymentId, contractPledgeItemId);
    }

}
