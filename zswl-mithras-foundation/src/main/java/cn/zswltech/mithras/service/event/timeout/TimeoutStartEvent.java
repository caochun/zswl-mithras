package cn.zswltech.mithras.service.event.timeout;

import cn.zswltech.mithras.service.enums.TimeoutTypeEnum;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

/**
 * @ClassName TimeoutStartEvent
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/4/25 5:23 下午
 * @Version 1.0
 **/
@Getter
@Setter
public class TimeoutStartEvent extends ApplicationEvent {

    private String key;

    private Long startTime;

    /** {@link TimeoutTypeEnum} **/
    private String type;

    boolean isConver;

    //过期时间 单位 秒
    private long duration;

    public TimeoutStartEvent(Object source) {
        super(source);
    }

    public TimeoutStartEvent(Object source, String key, Long time, Long duration, String type, boolean isConver) {
        super(source);
        this.key = key;
        this.startTime = time;
        this.duration = duration;
        this.type = type;
        this.isConver = isConver;
    }
}
