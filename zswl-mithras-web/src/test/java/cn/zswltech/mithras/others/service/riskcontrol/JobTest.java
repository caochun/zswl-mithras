package cn.zswltech.mithras.others.service.riskcontrol;

import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.riskcontrol.job.RiskControlJob;
import cn.zswltech.mithras.riskcontrol.job.RiskControlStrategyFullComputeJob;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/6/6 13:40
 */
public class JobTest extends ApplicationTest {
    @Resource
    private RiskControlStrategyFullComputeJob riskControlStrategyFullComputeJob;
    @Resource
    private RiskControlJob riskControlJob;

    @Test
    public void test() {
        try {
            riskControlStrategyFullComputeJob.fullComputeMetricJobHandler();
            Thread.sleep(30000L);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void startWarnFlowJob() {
        riskControlJob.startWarnFlowJob();
    }
}
