package cn.zswltech.mithras.liquiditymanage.service.cal.board;

import cn.hutool.core.util.ReflectUtil;
import cn.zswltech.mithras.dto.liquiditymanage.base.LiquidityColorVo;
import cn.zswltech.mithras.liquiditymanage.enums.LiquidityColorEnum;
import cn.zswltech.mithras.liquiditymanage.enums.LiquidityIndexType;
import cn.zswltech.mithras.liquiditymanage.service.cal.AbstractLiquidityCalculator;
import cn.zswltech.mithras.liquiditymanage.service.cal.bo.LiquidityBoardCalculatorBo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @author bigbear
 * @date 2025/2/27 19:10
 * @description
 */
@Slf4j
@Component
public class NonSupervisedAccountNetInflowCalculator extends AbstractLiquidityCalculator<LiquidityBoardCalculatorBo> {
    @Override
    public void calculate(Object obj, LiquidityBoardCalculatorBo bo) {
        LiquidityColorVo nonSupervisedAccountFunds = (LiquidityColorVo)ReflectUtil.getFieldValue(obj, "nonSupervisedAccountFunds");
        // 公式为当前的 非监管户资金余额 - 开始查询时间的非监管户余额
        if (bo.getQueryDate().isBefore(bo.getQueryDateStart())) {
            return;
        }
        BigDecimal nonSupervisedAccountNetInflow = nonSupervisedAccountFunds.getValue().subtract(bo.getSuperviseAndNonSuperviseBalance().getValue().getValue());
        ReflectUtil.setFieldValue(obj, indexName(), new LiquidityColorVo(nonSupervisedAccountNetInflow, LiquidityColorEnum.BLACK.name(), 3));
    }

    @Override
    public LiquidityIndexType model() {
        return LiquidityIndexType.LIQUIDITY_BOARD;
    }

    @Override
    public String indexName() {
        return "nonSupervisedAccountNetInflow";
    }

    @Override
    public int sort() {
        return 99999;
    }
}
