package cn.zswltech.mithras.others.service.riskcontrol;

import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.service.service.riskcontrol.RiskControlConcentrationClientService;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/3/2 13:40
 */
public class RiskControlConcentrationClientServiceTest extends ApplicationTest {
    @Resource
    private RiskControlConcentrationClientService riskControlConcentrationClientService;

    @Test
    public void testList() {
        riskControlConcentrationClientService.riskControlConcentrationClientJobHandler();
    }
}
