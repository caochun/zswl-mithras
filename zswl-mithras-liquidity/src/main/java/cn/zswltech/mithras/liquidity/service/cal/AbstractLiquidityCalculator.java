package cn.zswltech.mithras.liquidity.service.cal;

import cn.zswltech.mithras.liquidity.enums.LiquidityIndexType;
import cn.zswltech.mithras.liquidity.service.cal.bo.LiquidityBaseCalculatorBo;

/**
 * 流动性管理指标计算统一接口，一个指标一个类
 *
 * @author chenyifei
 * @since 2024/12/12
 */
public abstract class AbstractLiquidityCalculator<T extends LiquidityBaseCalculatorBo> {


    /**
     * 指标计算
     * @return
     */
    public abstract void calculate(Object obj, T bo);

    /**
     * 指标归属
     * @return
     */
    public abstract LiquidityIndexType model();

    /**
     * 指标名称
     * @return
     */
    public abstract String indexName();

    /**
     * 执行顺序
     * @return
     */
    public int sort(){
        return 10;
    }

}
