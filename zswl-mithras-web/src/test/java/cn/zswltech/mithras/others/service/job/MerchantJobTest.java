package cn.zswltech.mithras.others.service.job;

import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.service.job.MerchantXxlJob;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2022/9/15
 * @description
 */
public class MerchantJobTest extends ApplicationTest {
    @Resource
    private MerchantXxlJob merchantXxlJob;

    @Test
    public void demoJobHandlerTest() {
        merchantXxlJob.demoJobHandler();
    }
}
