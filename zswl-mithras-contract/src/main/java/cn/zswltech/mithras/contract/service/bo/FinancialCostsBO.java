package cn.zswltech.mithras.contract.service.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @author yupengfei
 * @date 2024/4/15 11:26
 */
@Data
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class FinancialCostsBO {
    /**
     * 税率
     */
    private BigDecimal taxRate;

    /**
     * 本金总额
     */
    private Long capitalSum;

    /**
     * 利息总额
     */
    private Long interestSum;

    /**
     * 租金总额
     */
    private Long rentSum;

    /**
     * 不含税利息
     */
    private BigDecimal excludingInterestTax;

    /**
     * 税额
     */
    private BigDecimal tax;

    /**
     * 不含税租金
     */
    private BigDecimal rentExcludingTax;

    /**
     * 印花税
     */
//    private BigDecimal stampDuty;
    private StampDutyContextBO stampDutyContextBO;
}
