package cn.zswltech.mithras.workbench.application.cardcal;

import cn.zswltech.mithras.foundation.util.LongUtil;
import cn.zswltech.mithras.workbench.application.cardcal.model.WorkbenchFundRepayCashFlow;
import cn.zswltech.mithras.workbench.application.port.cardcal.WorkbenchCardFundRepayPort;
import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

/**
 * @description: 本月已还/剩余
 * @author: zhaozhengkang
 * @date: 2023/5/15 14:24
 */
@Component
public class WCM_054Calculator implements CardCalculator {
    private static final String WRITTEN_OFF = "WRITTEN_OFF";

    @Resource
    private WorkbenchCardFundRepayPort workbenchCardFundRepayPort;

    @Override
    public String metricCode() {
        return "WCM_054";
    }

    @Override
    public String calculate() {
        LocalDate now = LocalDate.now();
        LocalDate start = now.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate end = start.plusMonths(1).minusDays(1);
        List<WorkbenchFundRepayCashFlow> cashFlows = workbenchCardFundRepayPort.listRepayCashFlows(start, end);
        BigDecimal repaid = BigDecimal.ZERO;
        BigDecimal remain = BigDecimal.ZERO;
        for (WorkbenchFundRepayCashFlow cashFlow : cashFlows) {
            if (WRITTEN_OFF.equals(cashFlow.getWriteOffState())) {
                repaid = repaid.add(new BigDecimal(LongUtil.null2zero(cashFlow.getRepayAmount())));
            } else {
                remain = remain.add(new BigDecimal(LongUtil.null2zero(cashFlow.getRepayAmount())));
            }
        }
        List<PieRes> res = new ArrayList<>();
        res.add(new PieRes("已还", repaid.divide(new BigDecimal(100000000L), 2, RoundingMode.HALF_UP).toString()));
        res.add(new PieRes("剩余", remain.divide(new BigDecimal(100000000L), 2, RoundingMode.HALF_UP).toString()));
        return JSON.toJSONString(res);
    }
}
