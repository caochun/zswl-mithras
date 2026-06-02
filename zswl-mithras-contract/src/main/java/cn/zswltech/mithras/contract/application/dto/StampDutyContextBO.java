package cn.zswltech.mithras.contract.application.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author dingqi
 * @date 2025/11/17
 * @description
 */
@Data
public class StampDutyContextBO {
    private BigDecimal rentWithoutTax;
    private BigDecimal serviceFeeWithoutTax;
    private BigDecimal actualPayAmount;
    private BigDecimal taxRateZL;
    private BigDecimal taxRateMM;
    private BigDecimal taxZL;
    private BigDecimal taxMM;
    private BigDecimal tax;
}
