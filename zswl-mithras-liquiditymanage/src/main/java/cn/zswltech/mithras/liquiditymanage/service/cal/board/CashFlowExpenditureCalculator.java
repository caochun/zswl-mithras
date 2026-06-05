package cn.zswltech.mithras.liquiditymanage.service.cal.board;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.zswltech.mithras.dto.liquiditymanage.base.LiquidityColorVo;
import cn.zswltech.mithras.dto.liquiditymanage.liquidityIndex.LiquidityBoardDetailRSP;
import cn.zswltech.mithras.liquiditymanage.enums.LiquidityColorEnum;
import cn.zswltech.mithras.liquiditymanage.enums.LiquidityIndexType;
import cn.zswltech.mithras.liquiditymanage.mapper.model.AccountBalanceBaseInfo;
import cn.zswltech.mithras.liquiditymanage.service.LiquidityIndicatorBoardHolder;
import cn.zswltech.mithras.liquiditymanage.service.cal.AbstractLiquidityCalculator;
import cn.zswltech.mithras.liquiditymanage.service.cal.bo.LiquidityBoardCalculatorBo;
import cn.zswltech.mithras.service.util.LongUtil;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;


/**
 * 现金流支出(元)
 *
 * 账务偿还+刚性支出
 * @author chenyifei
 * @since 2024/12/20
 */
@Component
public class CashFlowExpenditureCalculator extends AbstractLiquidityCalculator<LiquidityBoardCalculatorBo> {

    @Override
    public void calculate(Object obj, LiquidityBoardCalculatorBo bo) {
        LiquidityBoardDetailRSP rsp = (LiquidityBoardDetailRSP)obj;
        BigDecimal debtRepayment = Optional.ofNullable(rsp.getDebtRepayment()).map(LiquidityColorVo::getValue).orElse(BigDecimal.ZERO);
        BigDecimal rigidExpenditure = Optional.ofNullable(rsp.getRigidExpenditure()).map(LiquidityColorVo::getValue).orElse(BigDecimal.ZERO);
        BigDecimal result = debtRepayment.add(rigidExpenditure);
        ReflectUtil.setFieldValue(obj, indexName(), new LiquidityColorVo(result, LiquidityColorEnum.BLACK.name(), 1));
    }

    @Override
    public LiquidityIndexType model() {
        return LiquidityIndexType.LIQUIDITY_BOARD;
    }

    @Override
    public String indexName() {
        return "cashFlowExpenditure";
    }

    @Override
    public int sort() {
        return 20;
    }
}
