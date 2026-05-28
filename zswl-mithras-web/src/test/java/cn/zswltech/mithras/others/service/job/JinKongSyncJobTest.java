package cn.zswltech.mithras.others.service.job;

import cn.zswltech.mithras.others.service.ApplicationTest;
import cn.zswltech.mithras.service.job.JinKongSyncJob;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2023/8/9
 * @description
 */
public class JinKongSyncJobTest extends ApplicationTest {
    @Resource
    private JinKongSyncJob jinKongSyncJob;

    @Test
    public void syncGZKBTest() {
        jinKongSyncJob.syncGZKB();
    }

    @Test
    public void syncAccountBalanceTest() {
        jinKongSyncJob.syncAccountBalance();
    }

    @Test
    public void syncTest() {
        jinKongSyncJob.jinKongSyncAssetJob();
        jinKongSyncJob.jinKongSyncProfitJob();
        jinKongSyncJob.jinKongSyncCashflowJob();
    }

}
