package cn.zswltech.mithras.customer.event;

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
        private String businessModule;
        private String bizId;
        private String remark;

        public ClientViewAuthorityInfo(Object businessModule, String bizId, String remark) {
            this.businessModule = businessModule == null ? null : businessModule.toString();
            this.bizId = bizId;
            this.remark = remark;
        }
    }
}
