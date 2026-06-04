package cn.zswltech.mithras.contract.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

/**
 * @ClassName ContractPriceChangeEvent
 * @Description 合同金额，核销本金，首期租金发生变化后，调用更新项目下各合同剩余可用额度
 * @Author jackerhe
 * @Date 2022/8/24 4:57 下午
 * @Version 1.0
 **/
@Getter
@Setter
public class ContractPriceChangeEvent extends ApplicationEvent {

    private Long contractId;

    public ContractPriceChangeEvent(Object source, Long contractId) {
        super(source);
        this.contractId = contractId;
    }
}
