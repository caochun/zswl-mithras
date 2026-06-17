package cn.zswltech.mithras.others.job;

import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.finance.application.port.ProfitCalculateJobPort;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2023/6/28
 * @description
 */
public class ProfitCalculateJobTest extends ApplicationTest {
    @Resource
    private ProfitCalculateJobPort profitCalculateJobService;

    @Test
    public void calculateTest() {
        profitCalculateJobService.profitCalculate(null);
    }
}
