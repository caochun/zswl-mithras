package cn.zswltech.mithras.others.service.third;

import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.finance.service.third.jk.JinKongMonthlyReportService;
import org.junit.Test;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

/**
 * @author dingqi
 * @date 2023/8/8
 * @description
 */
public class JinKongApiHandlerTest extends ApplicationTest {
    @Resource
    private JinKongMonthlyReportService jinKongMonthlyReportService;

    @Test
    public void reportBcmMfTest() {
        LocalDate targetDate = LocalDate.of(2025,9,30);
//        jinKongMonthlyReportService.jinKongSyncAsset(targetDate);
        jinKongMonthlyReportService.jinKongSyncProfit(targetDate);
        jinKongMonthlyReportService.jinKongSyncCashFlow(targetDate);
    }
}
