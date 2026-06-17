package cn.zswltech.mithras.application.orchestration.adapter.metric;

import cn.zswltech.mithras.finance.service.third.jk.JinKongMonthlyReportService;
import cn.zswltech.mithras.metric.application.job.JinKongMonthlyReportPort;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;

@Component
public class JinKongMonthlyReportPortAdapter implements JinKongMonthlyReportPort {

    @Resource
    private JinKongMonthlyReportService jinKongMonthlyReportService;

    @Override
    public void syncAccountBalanceData(int year, int month) {
        jinKongMonthlyReportService.syncAccountBalanceData(year, month);
    }

    @Override
    public void syncBcmFflexfiledAssistMfByDept(int year, int month) {
        jinKongMonthlyReportService.syncBcmFflexfiledAssistMfByDept(year, month);
    }

    @Override
    public void jinKongSyncAsset(LocalDate localDate) {
        jinKongMonthlyReportService.jinKongSyncAsset(localDate);
    }

    @Override
    public void jinKongSyncProfit(LocalDate localDate) {
        jinKongMonthlyReportService.jinKongSyncProfit(localDate);
    }

    @Override
    public void jinKongSyncCashFlow(LocalDate localDate) {
        jinKongMonthlyReportService.jinKongSyncCashFlow(localDate);
    }
}
