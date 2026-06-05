package cn.zswltech.mithras.others.service.controller.riskcontrol;

import cn.hutool.core.collection.ListUtil;
import cn.zswltech.mithras.service.application.riskcontrol.RiskControlOpinionMonitorFacade;
import cn.zswltech.mithras.riskcontrol.opinion.RiskControlOpinionMonitor;
import cn.zswltech.mithras.web.MithrasApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * @author yibin
 */

@Rollback
@SpringBootTest(classes = MithrasApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
class RiskControlOpinionMonitorWebTest {
    @Resource
    private RiskControlOpinionMonitorFacade controller;

    @Test
    void notice2BizPersonIfNeeded() {
        ArrayList<RiskControlOpinionMonitor> list = ListUtil.toList(new RiskControlOpinionMonitor()
//                .setId(RandomUtil.randomLong())
                        .setId(999L)
                        .setLinkAddress("http://www.baidu.com")
                        .setChiName("某某集团")
                        .setTitle("绯闻")
                        .setCreditCode("DIWNII987S")
                        .setMajorOrgCode("XISKI")
                        .setInfoPublDate(LocalDateTime.now())
                        .setWarnStar(3)
                        .setWarnLevel(3)
        );
        controller.notice2BizPersonIfNeeded(list);
    }
}
