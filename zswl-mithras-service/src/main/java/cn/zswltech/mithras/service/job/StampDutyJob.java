package cn.zswltech.mithras.service.job;

import cn.hutool.core.date.StopWatch;
import cn.zswltech.mithras.service.service.stampduty.ReportStampDutyService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 印花税跑批
 * @author: luyujie
 * @date: 2026/01/15 3:47 下午
 **/
@Slf4j
@Component
public class StampDutyJob {
    @Resource
    private ReportStampDutyService stampDutyService;


    /**
     * 每天定时增量更新起租的印花税
     */
    @XxlJob("refreshStampDuty")
    @Transactional(rollbackFor = Throwable.class)
    public void refreshStampDuty() {
        try {
            log.info("refreshStampDuty start");
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            String jobParam = XxlJobHelper.getJobParam();
            stampDutyService.refreshStampDuty(jobParam);
            stopWatch.stop();
            log.info("refreshStampDuty end!!! 耗时={}s", stopWatch.prettyPrint(TimeUnit.SECONDS));
        } catch (Exception e) {
            log.error("refreshStampDuty 执行异常，e={}", e);
        }
    }

}
