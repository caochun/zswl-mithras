package cn.zswltech.mithras.others.job;

import cn.zswltech.mithras.metric.application.job.JinKongSyncJobService;
import cn.zswltech.mithras.others.service.ApplicationTest;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2023/8/9
 * @description
 */
public class JinKongSyncJobTest extends ApplicationTest {
    @Resource
    private JinKongSyncJobService jinKongSyncJobService;

    @Test
    public void syncGZKBTest() {
        jinKongSyncJobService.syncGZKB(null);
    }

    @Test
    public void syncAccountBalanceTest() {
        jinKongSyncJobService.syncAccountBalance(null);
    }

    @Test
    public void syncTest() {
        jinKongSyncJobService.jinKongSyncAssetJob(null);
        jinKongSyncJobService.jinKongSyncProfitJob(null);
        jinKongSyncJobService.jinKongSyncCashflowJob(null);
    }

}
