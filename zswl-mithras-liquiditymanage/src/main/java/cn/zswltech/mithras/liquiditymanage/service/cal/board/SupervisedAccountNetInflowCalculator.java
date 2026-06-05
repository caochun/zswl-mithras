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
public class SupervisedAccountNetInflowCalculator extends AbstractLiquidityCalculator<LiquidityBoardCalculatorBo> {
    @Override
    public void calculate(Object obj, LiquidityBoardCalculatorBo bo) {
        LiquidityColorVo supervisedAccountFunds = (LiquidityColorVo) ReflectUtil.getFieldValue(obj, "supervisedAccountFunds");
        log.info("supervisedAccountFunds:{}", supervisedAccountFunds);
        // 公式为当前的 监管户资金余额 - 开始查询时间的监管户余额
        if (bo.getQueryDate().isBefore(bo.getQueryDateStart())) {
            return;
        }
        log.info("supervisedAccountNetInflow LiquidityBoardCalculatorBo = {}", bo);
        BigDecimal supervisedAccountNetInflow = supervisedAccountFunds.getValue().subtract(bo.getSuperviseAndNonSuperviseBalance().getKey().getValue());
        ReflectUtil.setFieldValue(obj, indexName(), new LiquidityColorVo(supervisedAccountNetInflow, LiquidityColorEnum.BLACK.name(), 3));
    }

    @Override
    public LiquidityIndexType model() {
        return LiquidityIndexType.LIQUIDITY_BOARD;
    }

    @Override
    public String indexName() {
        return "supervisedAccountNetInflow";
    }

    @Override
    public int sort() {
        return 99999;
    }
}
