package cn.zswltech.mithras.liquidity.service.cal.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * LiquidityMismatchCalculatorBo
 *
 * @author chenyifei
 * @since 2024/12/21
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LiquidityMismatchCalculatorBo extends LiquidityBaseCalculatorBo{

    /**
     * 预测区间-开始
     */
    private LocalDate queryDateStart;

    /**
     * 预测区间-结束
     */
    private LocalDate queryDateEnd;

}
