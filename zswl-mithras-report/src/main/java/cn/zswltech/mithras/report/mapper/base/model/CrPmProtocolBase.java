package cn.zswltech.mithras.report.mapper.base.model;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 征信报送-抵质押协议表
 *
 * @author wangchuanhao
 * @date 2022/10/14 11:32 AM
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
@Deprecated
@EqualsAndHashCode(callSuper = true)
public class CrPmProtocolBase extends CrBaseModel implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 借据编号
     */
    @TableField("payment_apply_code")
    private String paymentApplyCode;

    /**
     * 借据本金(单位：0.0001元)
     */
    @TableField(value = "apply_payment_amount", updateStrategy = FieldStrategy.IGNORED)
    private Long applyPaymentAmount;

    /**
     * 质押合同编号
     */
    @TableField(value = "pm_contract_code", updateStrategy = FieldStrategy.IGNORED)
    private String pmContractCode;

}
