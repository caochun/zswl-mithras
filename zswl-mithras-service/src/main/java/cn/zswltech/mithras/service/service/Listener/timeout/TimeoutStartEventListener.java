package cn.zswltech.mithras.service.service.Listener.timeout;

import cn.zswltech.mithras.service.event.timeout.TimeoutStartEvent;
import cn.zswltech.mithras.service.service.contract.delayqueue.DelayQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @ClassName TimeoutStartEventListener
 * @Description 超时事件监听
 * @Author jackerhe
 * @Date 2023/4/25 5:36 下午
 * @Version 1.0
 **/
@Component
@Slf4j
public class TimeoutStartEventListener implements ApplicationListener<TimeoutStartEvent> {

    @Resource
    private DelayQueueService delayQueueService;


    @Override
    public void onApplicationEvent(TimeoutStartEvent event) {
        delayQueueService.addTask(event);
    }
}
