package cn.zswltech.mithras.service.service.Listener;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

/**
 * @author dingqi
 * @date 2024/3/5
 * @description
 */
@Getter
@Setter
public class SystemSwitchRefreshEvent extends ApplicationEvent {
    public SystemSwitchRefreshEvent(Object source) {
        super(source);
    }
}
