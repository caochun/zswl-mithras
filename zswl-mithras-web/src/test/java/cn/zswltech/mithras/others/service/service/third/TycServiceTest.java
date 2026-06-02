package cn.zswltech.mithras.others.service.service.third;

import cn.zswltech.mithras.third.service.TycService;
import cn.zswltech.mithras.third.service.model.MithrasBaseInfo;
import cn.zswltech.mithras.third.service.model.MithrasShareholderInfo;
import cn.zswltech.mithras.web.MithrasApplication;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author yibin
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MithrasApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TycServiceTest {

    @Resource
    private TycService tycService;

    @Test
    void relatedEnterpriseInfo() {
        MithrasBaseInfo mithrasBaseInfo = tycService.baseInfo("9141018268818293XM");
        System.out.println(mithrasBaseInfo);
    }

    @Test
    void shareholderInfo() {
        List<MithrasShareholderInfo> infos = tycService.shareholderInfo("91440300079808749T");
        System.out.println(infos);
    }

    @Test
    void baseInfo() {
    }
}