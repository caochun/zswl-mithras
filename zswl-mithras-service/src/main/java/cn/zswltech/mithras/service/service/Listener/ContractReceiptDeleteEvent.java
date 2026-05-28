package cn.zswltech.mithras.service.service.Listener;

import cn.zswltech.mithras.service.mapper.model.contract.ContractReceipt;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author dingqi
 * @date 2023/4/3
 * @description
 */
@Getter
public class ContractReceiptDeleteEvent extends ApplicationEvent {
    private static final long serialVersionUID = -1779703411138850756L;

    public ContractReceiptDeleteEvent(Object source) {
        super(source);
    }

    public Class<ContractReceipt> sourceClz() {
        return ContractReceipt.class;
    }
}
