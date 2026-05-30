package cn.zswltech.mithras.service.service.workbench.cardcal;

import cn.zswltech.mithras.service.enums.fund.receiptrepay.CashFlowState;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptRepayCashFlow;
import cn.zswltech.mithras.service.facade.fund.FundFacade;
import cn.zswltech.mithras.service.util.LongUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
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
    @Resource
    private FundFacade fundFacade;

    @Override
    public String metricCode() {
        return "WCM_054";
    }

    @Override
    public String calculate() {
        LocalDate now = LocalDate.now();
        LocalDate start = now.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate end = start.plusMonths(1).minusDays(1);
        List<FundReceiptRepayCashFlow> cashFlows = fundFacade.listCashFlow(
                Wrappers.<FundReceiptRepayCashFlow>lambdaQuery()
                        .ge(FundReceiptRepayCashFlow::getRepayDate, start)
                        .le(FundReceiptRepayCashFlow::getRepayDate, end));
        BigDecimal repaid = BigDecimal.ZERO;
        BigDecimal remain = BigDecimal.ZERO;
        for (FundReceiptRepayCashFlow cashFlow : cashFlows) {
            if (CashFlowState.WRITTEN_OFF.name().equals(cashFlow.getWriteOffState())) {
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
