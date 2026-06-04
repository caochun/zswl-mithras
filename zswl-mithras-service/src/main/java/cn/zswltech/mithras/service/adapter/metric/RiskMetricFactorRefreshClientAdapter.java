package cn.zswltech.mithras.service.adapter.metric;

import cn.zswltech.mithras.metric.service.RiskMetricFactorRefreshClient;
import cn.zswltech.mithras.service.job.JinKongSyncJob;
import cn.zswltech.mithras.service.service.third.jk.JinKongMonthlyReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;

@Slf4j
@Component
public class RiskMetricFactorRefreshClientAdapter implements RiskMetricFactorRefreshClient {

    @Resource
    private JinKongMonthlyReportService jinKongMonthlyReportService;
    @Resource
    private JinKongSyncJob jinKongSyncJob;

    @Override
    public boolean refreshAsset(LocalDate date) {
        try {
            jinKongMonthlyReportService.jinKongSyncAsset(date);
            return true;
        } catch (Exception e) {
            log.error("资产负债表:", e);
            return false;
        }
    }

    @Override
    public boolean refreshProfit(LocalDate date) {
        try {
            jinKongMonthlyReportService.jinKongSyncProfit(date);
            return true;
        } catch (Exception e) {
            log.error("利润表:", e);
            return false;
        }
    }

    @Override
    public boolean refreshCashFlow(LocalDate date) {
        try {
            jinKongMonthlyReportService.jinKongSyncCashFlow(date);
            return true;
        } catch (Exception e) {
            log.error("现金流量表:", e);
            return false;
        }
    }

    @Override
    public boolean refreshSubjectBalance(LocalDate date) {
        try {
            jinKongMonthlyReportService.syncAccountBalanceData(date.getYear(), date.getMonthValue());
            return true;
        } catch (Exception e) {
            log.error("科目余额表:", e);
            return false;
        }
    }

    @Override
    public void testProfitSync() {
        jinKongSyncJob.jinKongSyncProfitJob();
    }
}
