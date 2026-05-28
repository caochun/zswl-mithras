package cn.zswltech.mithras.service.service.liquiditymanage.cal.bo;

import cn.zswltech.mithras.service.mapper.model.liquiditymanage.AccountBalanceBaseInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * LiquidityAccountManualCalculatorBo
 *
 * @author chenyifei
 * @since 2024/12/18
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LiquidityAccountManualCalculatorBo extends LiquidityBaseCalculatorBo{

    private LocalDate localDate;

    private Long accountBankId;

    /**
     * 前一日的指标
     */
    private AccountBalanceBaseInfo accountBalanceBeforeDay;

    public LiquidityAccountManualCalculatorBo(LocalDate localDate, Long accountBankId) {
        this.localDate = localDate;
        this.accountBankId = accountBankId;
    }
}
