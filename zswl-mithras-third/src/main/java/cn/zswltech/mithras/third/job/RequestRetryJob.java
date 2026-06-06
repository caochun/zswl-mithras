package cn.zswltech.mithras.third.job;

import cn.zswltech.mithras.third.application.job.RequestRetryJobService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 统一接口请求失败重试任务
 *
 * @author jackerhe
 * @date 2023/3/31 6:12 下午
 */
@Component
@Slf4j
public class RequestRetryJob {

    @Resource
    private RequestRetryJobService requestRetryJobService;

    /**
     * 苍穹相关请求重试任务
     */
    @XxlJob("requestRetryCQHandler")
    public void doJobHandler() {
        requestRetryJobService.doJobHandler();
    }
}
