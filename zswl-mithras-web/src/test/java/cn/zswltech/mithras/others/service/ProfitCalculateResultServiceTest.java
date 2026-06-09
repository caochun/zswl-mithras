package cn.zswltech.mithras.others.service;

import cn.zswltech.mithras.finance.service.profitcalculate.ProfitCalculateResultService;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2023/6/28
 * @description
 */
public class ProfitCalculateResultServiceTest extends ApplicationTest {
    @Resource
    private ProfitCalculateResultService profitCalculateResultService;

    @Test
    public void calculateTest() {
        profitCalculateResultService.calculate(1244L);
    }
}
