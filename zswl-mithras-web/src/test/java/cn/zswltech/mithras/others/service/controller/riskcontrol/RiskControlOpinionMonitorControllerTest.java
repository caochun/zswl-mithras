package cn.zswltech.mithras.others.service.controller.riskcontrol;

import cn.hutool.core.collection.ListUtil;
import cn.zswltech.mithras.dto.riskcontrol.opinion.RiskControlOpinionNoticeReq;
import cn.zswltech.mithras.service.application.riskcontrol.RiskControlOpinionMonitorFacade;
import cn.zswltech.mithras.service.repository.PlatformApiEnum;
import cn.zswltech.mithras.service.repository.PlatformApiHandleFactory;
import cn.zswltech.mithras.service.repository.PlatformApiHandler;
import cn.zswltech.mithras.service.service.riskcontrol.RiskControlOpinionMonitorService;
import cn.zswltech.mithras.third.service.opinion.handle.RiskControlOpinionListHandle;
import cn.zswltech.mithras.third.service.opinion.req.RiskControlOpinionPullListREQ;
import cn.zswltech.mithras.third.service.opinion.resp.RiskControlOpinionPullListRsp;
import cn.zswltech.mithras.web.MithrasApplication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * @author yibin
 */

@Rollback
@SpringBootTest(classes = MithrasApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
class RiskControlOpinionMonitorControllerTest {

    @InjectMocks
    private RiskControlOpinionMonitorFacade riskControlOpinionMonitorController;
    @Mock
    private PlatformApiHandleFactory platformApiHandleFactory;
    @Mock
    private RiskControlOpinionMonitorService riskControlOpinionMonitorService;


    @Test
    @ExtendWith(MockitoExtension.class)
    void notice() {
        PlatformApiHandler<RiskControlOpinionPullListREQ, RiskControlOpinionPullListRsp> handler = Mockito.mock(RiskControlOpinionListHandle.class);
        RiskControlOpinionPullListRsp mockRsp = new RiskControlOpinionPullListRsp();
        mockRsp.setSuccess(true);
        mockRsp.setData(ListUtil.toList(new RiskControlOpinionPullListRsp.RiskControlOpinionListData()
                .setChiName("")
                .setCreditCode("001")
                .setId(1L)
                .setInfoPublDate(LocalDateTime.now())
                .setLinkAddress("http://www.baidu.com")
                .setMajorOrgCode("xxx")
                .setTitle("WARN")
                .setWarnLevel(3)
                .setWarnStar(3)
        ));
        when(platformApiHandleFactory.getPlatformApiHandler(PlatformApiEnum.PO_LIST)).thenReturn(handler);
        when(handler.execute(any())).thenReturn(mockRsp);
//        when(riskControlOpinionMonitorController.notice2BizPersonIfNeeded(any()))
        riskControlOpinionMonitorController.notice(new RiskControlOpinionNoticeReq());
    }
/*
    @Test
    @Rollback
    @SpringBootTest(classes = MithrasApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
    @ActiveProfiles("dev")
    void notice2BizPersonIfNeeded() {
    }*/
}
