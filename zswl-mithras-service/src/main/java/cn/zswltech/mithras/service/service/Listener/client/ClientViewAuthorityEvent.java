package cn.zswltech.mithras.service.service.Listener.client;

import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import lombok.*;
import org.springframework.context.ApplicationEvent;

/**
 * @author dingqi
 * @date 2024/9/18
 * @description
 */
@Getter
public class ClientViewAuthorityEvent extends ApplicationEvent {
    private final ClientViewAuthorityInfo info;

    public ClientViewAuthorityEvent(ClientViewAuthorityInfo info) {
        super(info);
        this.info = info;
    }

    @AllArgsConstructor
    @Getter
    @Setter
    public static class ClientViewAuthorityInfo {
        private BusinessModuleEnum businessModule;
        private String bizId;
        private String remark;
    }
}
