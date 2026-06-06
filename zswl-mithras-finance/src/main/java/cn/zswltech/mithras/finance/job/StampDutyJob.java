package cn.zswltech.mithras.finance.job;

import cn.hutool.core.date.StopWatch;
import cn.zswltech.mithras.finance.application.job.StampDutyJobService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 印花税跑批
 *
 * @author: luyujie
 * @date: 2026/01/15 3:47 下午
 **/
@Slf4j
@Component
public class StampDutyJob {

    @Resource
    private StampDutyJobService stampDutyJobService;

    /**
     * 每天定时增量更新起租的印花税
     */
    @XxlJob("refreshStampDuty")
    public void refreshStampDuty() {
        try {
            log.info("refreshStampDuty start");
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            stampDutyJobService.refreshStampDuty(XxlJobHelper.getJobParam());
            stopWatch.stop();
            log.info("refreshStampDuty end!!! 耗时={}s", stopWatch.prettyPrint(TimeUnit.SECONDS));
        } catch (Exception e) {
            log.error("refreshStampDuty 执行异常，e={}", e);
        }
    }
}
