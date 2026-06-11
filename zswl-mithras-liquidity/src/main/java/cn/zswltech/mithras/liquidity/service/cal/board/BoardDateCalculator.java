package cn.zswltech.mithras.liquidity.service.cal.board;

import cn.hutool.core.util.ReflectUtil;
import cn.zswltech.mithras.liquidity.enums.LiquidityIndexType;
import cn.zswltech.mithras.liquidity.service.cal.AbstractLiquidityCalculator;
import cn.zswltech.mithras.liquidity.service.cal.bo.LiquidityBoardCalculatorBo;
import org.springframework.stereotype.Component;

/**
 * 数据时点
 *
 * @author chenyifei
 * @since 2024/12/21
 */
@Component
public class BoardDateCalculator extends AbstractLiquidityCalculator<LiquidityBoardCalculatorBo> {

    @Override
    public void calculate(Object obj, LiquidityBoardCalculatorBo bo) {
        ReflectUtil.setFieldValue(obj, indexName(), bo.getQueryDate());
    }

    @Override
    public LiquidityIndexType model() {
        return LiquidityIndexType.LIQUIDITY_BOARD;
    }

    @Override
    public String indexName() {
        return "date";
    }

}
