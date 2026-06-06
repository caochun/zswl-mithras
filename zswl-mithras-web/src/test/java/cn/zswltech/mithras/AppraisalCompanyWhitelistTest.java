package cn.zswltech.mithras;

import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.extension.event.context.ProcessEndContext;
import cn.zswltech.mithras.leaseholdproperty.job.AppraisalWhitelistJob;
import cn.zswltech.mithras.service.flow.listener.endhandler.AppraisalCompanyWhitelistProcessEndHandler;
import cn.zswltech.mithras.web.MithrasApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author dingqi
 * @date 2025/9/4
 * @description
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MithrasApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("uat")
public class AppraisalCompanyWhitelistTest {
    @Test
    public void processEndTest() {
        ProcessResp processResp = SpringUtil.getBean(FlowTaskApiService.class).queryProcessById("6132604");
        ProcessEndContext processEndContext = new ProcessEndContext();
        processEndContext.setProcessInstanceId(processResp.getProcessInstanceId());
        processEndContext.setBusinessKey(processResp.getBusinessKey());
        processEndContext.setModelKey(processResp.getModelKey());
        processEndContext.setEndType(ProcessBusinessStatusEnum.PASS.getType());
        SpringUtil.getBean(AppraisalCompanyWhitelistProcessEndHandler.class).handle(processEndContext);
    }

    @Test
    public void jobTest() {
        SpringUtil.getBean(AppraisalWhitelistJob.class).AppraisalWhitelistDailyJob();
    }
}
