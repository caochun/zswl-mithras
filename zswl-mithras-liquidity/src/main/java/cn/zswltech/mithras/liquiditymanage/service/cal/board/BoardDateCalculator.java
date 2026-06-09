package cn.zswltech.mithras.liquiditymanage.service.cal.board;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.zswltech.mithras.dto.liquiditymanage.base.LiquidityColorVo;
import cn.zswltech.mithras.liquiditymanage.enums.LiquidityColorEnum;
import cn.zswltech.mithras.liquiditymanage.enums.LiquidityIndexType;
import cn.zswltech.mithras.liquiditymanage.mapper.model.AccountBalanceBaseInfo;
import cn.zswltech.mithras.liquiditymanage.service.cal.AbstractLiquidityCalculator;
import cn.zswltech.mithras.liquiditymanage.service.cal.bo.LiquidityBoardCalculatorBo;
import cn.zswltech.mithras.service.util.LongUtil;
import org.springframework.stereotype.Component;

import java.util.Map;


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
