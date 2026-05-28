package cn.zswltech.mithras.service.job;

import cn.hutool.core.date.StopWatch;
import cn.zswltech.mithras.service.service.client.ClientReleaseRemindService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class ClientReleaseRemindJob {

    @Resource
    private ClientReleaseRemindService releaseRemindService;


    /**
     *
     */
    @XxlJob("ClientReleaseRemindJob")
    public void ClientReleaseRemindJob() {
        try {
            log.info("ClientReleaseRemindJob start");
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            String jobParam = XxlJobHelper.getJobParam();
            //jobParam = "6782";
            releaseRemindService.clientReleaseRemind(jobParam);
            stopWatch.stop();
            log.info("ClientReleaseRemindJob end!!! 耗时={}s", stopWatch.prettyPrint(TimeUnit.SECONDS));
        } catch (Exception e) {
            log.error("ClientReleaseRemindJob 执行异常，e={}", e);
        }
    }
}