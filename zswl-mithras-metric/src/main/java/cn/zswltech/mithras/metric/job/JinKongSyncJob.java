package cn.zswltech.mithras.metric.job;

import cn.zswltech.mithras.metric.application.job.JinKongSyncJobService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class JinKongSyncJob {

    @Resource
    private JinKongSyncJobService jinKongSyncJobService;

    @XxlJob("syncGZKB")
    public void syncGZKB() {
        jinKongSyncJobService.syncGZKB(XxlJobHelper.getJobParam());
    }

    @XxlJob("syncAccountBalance")
    public void syncAccountBalance() {
        jinKongSyncJobService.syncAccountBalance(XxlJobHelper.getJobParam());
    }

    @XxlJob("jinKongSyncAssetJob")
    public void jinKongSyncAssetJob() {
        jinKongSyncJobService.jinKongSyncAssetJob(XxlJobHelper.getJobParam());
    }

    @XxlJob("jinKongSyncProfitJob")
    public void jinKongSyncProfitJob() {
        jinKongSyncJobService.jinKongSyncProfitJob(XxlJobHelper.getJobParam());
    }

    @XxlJob("jinKongSyncCashflowJob")
    public void jinKongSyncCashflowJob() {
        jinKongSyncJobService.jinKongSyncCashflowJob(XxlJobHelper.getJobParam());
    }
}
