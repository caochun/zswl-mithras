package cn.zswltech.mithras.others.service;

import cn.zswltech.mithras.dto.finance.FinanceProjectCalculationREQ;
import cn.zswltech.mithras.service.service.finance.FinanceProjectProfitDetailService;
import org.junit.Test;

import javax.annotation.Resource;
import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2024/8/13
 * @description
 */
public class FinanceProjectProfitDetailServiceTest extends ApplicationTest {
    @Resource
    private FinanceProjectProfitDetailService financeProjectProfitDetailService;

    @Test
    public void calculateProfitTest() {
        FinanceProjectCalculationREQ req = new FinanceProjectCalculationREQ();
        req.setYearAndMonth(LocalDate.of(2024, 6, 1));
        financeProjectProfitDetailService.profitCalculation(req);
    }
}
