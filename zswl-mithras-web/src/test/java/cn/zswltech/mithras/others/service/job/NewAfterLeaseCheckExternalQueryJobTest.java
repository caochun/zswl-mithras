package cn.zswltech.mithras.others.service.job;

import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.afterlease.job.AfterLeaseCheckExternalQueryJob;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/11/19 17:18
 */
public class NewAfterLeaseCheckExternalQueryJobTest extends ApplicationTest {
    @Resource
    private AfterLeaseCheckExternalQueryJob queryJob;
    @Test
    public void demoJobHandlerTest() {
        queryJob.createExternalQuery();
    }
}
