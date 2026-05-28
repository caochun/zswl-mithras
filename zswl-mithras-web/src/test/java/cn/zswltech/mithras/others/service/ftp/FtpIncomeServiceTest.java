package cn.zswltech.mithras.others.service.ftp;

import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.dto.interestPay.InterestPayREQ;
import cn.zswltech.mithras.dto.monthly.MonthlyCostREQ;
import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.service.enums.fund.financing.FinancingTypeEnum;
import cn.zswltech.mithras.service.job.FtpIncomeJob;
import cn.zswltech.mithras.service.service.ftp.FtpIncomeBaseInfoService;
import cn.zswltech.mithras.service.service.interestPay.InterestPayService;
import cn.zswltech.mithras.service.service.monthly.FundsDailyCostMainService;
import cn.zswltech.mithras.service.service.monthly.MonthlyManageService;
import org.junit.Test;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;


public class FtpIncomeServiceTest extends ApplicationTest {

    @Resource
    private FtpIncomeBaseInfoService ftpIncomeBaseInfoService;
    @Resource
    private FtpIncomeJob ftpIncomeJob;
    @Resource
    private MonthlyManageService monthlyManageService;

    @Test
    public void refreshTotalCostTest() {
        for (int i = 0; i < 11; i++) {
            LocalDate date = LocalDate.of(2025, i + 1, 1);
            SpringUtil.getBean(FundsDailyCostMainService.class).refreshTotalCapitalCost(LocalDate.of(date.getYear(), date.getMonthValue(), date.lengthOfMonth()));
        }
    }

    @Test
    public void interestPayCalculateTest() {
        String s = "2025-11";
        InterestPayREQ req = new InterestPayREQ();
        req.setStartYearAndMonth(s);
        req.setEndYearAndMonth(s);
        SpringUtil.getBean(InterestPayService.class).calculate(req);
    }

    @Test
    public void ftpIncomeBaseInfoService() throws Exception {
        ftpIncomeBaseInfoService.createOrUpdate(251015L, FinancingTypeEnum.DIRECT.name(), null);
    }

    @Test
    public void ftpIncomeMaintenanceJob() {
        MonthlyCostREQ req = new MonthlyCostREQ();
        req.setFinancingId(251005L);
        req.setFinancingType("ZR");
        req.setYearAndMonth("2026-02");
        monthlyManageService.calculateDailyInterest2(req);
        //ftpIncomeJob.ftpIncomeMaintenanceJob();
    }

    @Test
    public void refreshFinancingRateTest() {
        Map<String, Integer> map = new HashMap<>();
        map.put("DK202304140008-08", 30000);
        map.put("DK202306200035-04", 26500);
        map.put("DK202304140027-07", 31000);
        SpringUtil.getBean(FundsDailyCostMainService.class).refreshFinancingRate(map);
    }

}