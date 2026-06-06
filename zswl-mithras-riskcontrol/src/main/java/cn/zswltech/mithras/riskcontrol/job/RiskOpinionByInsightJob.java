package cn.zswltech.mithras.riskcontrol.job;

import cn.zswltech.mithras.riskcontrol.application.job.RiskOpinionByInsightJobService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * XXL-Job定时任务：同步慧眼系统舆情数据
 */
@Slf4j
@Component
public class RiskOpinionByInsightJob {
    @Resource
    private RiskOpinionByInsightJobService riskOpinionByInsightJobService;

    @XxlJob("riskControlDataSyncJob")
    public void riskControlDataSyncJo() {
        riskOpinionByInsightJobService.syncOpinionData();
    }
}
