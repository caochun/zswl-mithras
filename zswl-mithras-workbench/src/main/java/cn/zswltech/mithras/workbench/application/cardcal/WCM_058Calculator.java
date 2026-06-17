package cn.zswltech.mithras.workbench.application.cardcal;

import cn.zswltech.mithras.foundation.util.LongUtil;
import cn.zswltech.mithras.workbench.application.cardcal.model.WorkbenchProjReviewPrice;
import cn.zswltech.mithras.workbench.application.port.cardcal.WorkbenchCardProjReviewPort;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

/**
 * @description: 本年项目手续费率  本年评审通过项目手续费总额/项目总额 报价方案-手续费/申报授信金额
 * @author: zhaozhengkang
 * @date: 2023/5/15 14:24
 */
@Component
public class WCM_058Calculator implements CardCalculator {
    @Resource
    private WorkbenchCardProjReviewPort workbenchCardProjReviewPort;

    @Override
    public String metricCode() {
        return "WCM_058";
    }

    @Override
    public String calculate() {
        List<WorkbenchProjReviewPrice> prices = workbenchCardProjReviewPort.listTakeEffectReviewPrices(
                LocalDate.now().with(TemporalAdjusters.firstDayOfYear()).atStartOfDay());
        if (prices.isEmpty()) {
            return "0.00";
        }

        BigDecimal fee = BigDecimal.ZERO;
        BigDecimal applyCreditAmount = BigDecimal.ZERO;
        for (WorkbenchProjReviewPrice price : prices) {
            fee = fee.add(new BigDecimal(LongUtil.null2zero(price.getConsultingFee())));
            applyCreditAmount = applyCreditAmount.add(new BigDecimal(LongUtil.null2zero(price.getApplyCreditAmount())));
        }
        if (BigDecimal.ZERO.compareTo(applyCreditAmount) == 0) {
            return "0.00";
        }

        return fee.divide(applyCreditAmount, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100L)).setScale(2).toString();
    }
}
