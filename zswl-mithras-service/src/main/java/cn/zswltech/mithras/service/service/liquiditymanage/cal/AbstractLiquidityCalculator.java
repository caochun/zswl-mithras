package cn.zswltech.mithras.service.service.liquiditymanage.cal;

import cn.zswltech.mithras.liquiditymanage.service.cal.bo.LiquidityBaseCalculatorBo;

/**
 * Compatibility bridge for calculators that have not moved out of service yet.
 */
public abstract class AbstractLiquidityCalculator<T extends LiquidityBaseCalculatorBo>
        extends cn.zswltech.mithras.liquiditymanage.service.cal.AbstractLiquidityCalculator<T> {
}
