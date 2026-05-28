package cn.zswltech.mithras.service.service.liquiditymanage.cal.bo;

import cn.zswltech.mithras.service.mapper.model.liquiditymanage.AccountBalanceBaseInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * LiquidityCalculatorBo
 *
 * @author chenyifei
 * @since 2024/12/15
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LiquidityAccountCalculatorBo extends LiquidityBaseCalculatorBo{

    private LocalDate localDate;

    private Long accountBankId;

}
