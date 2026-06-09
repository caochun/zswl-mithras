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
public class TimeoutNotifyEvent extends ApplicationEvent {

    private String key;

    /** {@link TimeoutTypeEnum} **/
    private String type;


    public TimeoutNotifyEvent(Object source) {
        super(source);
    }

    public TimeoutNotifyEvent(Object source, String type, String key) {
        super(source);
        this.key = key;
        this.type = type;
    }
}
