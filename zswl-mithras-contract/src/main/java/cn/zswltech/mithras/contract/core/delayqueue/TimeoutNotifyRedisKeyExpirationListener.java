package cn.zswltech.mithras.contract.core.delayqueue;

import cn.zswltech.mithras.contract.event.timeout.TimeoutNotifyEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 *
 * @author: jackerhe
 * @date: 2023/5/11 2:07 下午
 **/
@Slf4j
@Component
public class TimeoutNotifyRedisKeyExpirationListener extends KeyExpirationEventMessageListener {

    @Resource
    private ApplicationEventPublisher eventPublisher;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public TimeoutNotifyRedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expireKey = message.toString();
        if (!expireKey.startsWith(DelayQueueService.HOST_ADDRESS)){
            return ;
        }
        TimeoutNotifyEvent e = generateTimeoutNotifyEvent(expireKey);
        if (e != null) {
            eventPublisher.publishEvent(e);
        }
    }

    @SuppressWarnings("rawtypes")
    private TimeoutNotifyEvent generateTimeoutNotifyEvent(String key) {
        String[] split = key.split(":");
        TimeoutNotifyEvent timeoutNotifyEvent;
        if(split.length > 2){
            timeoutNotifyEvent = new TimeoutNotifyEvent(key, split[1], split[2]);
        } else {
            timeoutNotifyEvent = new TimeoutNotifyEvent(key, split[0], split[1]);
        }
        return timeoutNotifyEvent;
    }

}
