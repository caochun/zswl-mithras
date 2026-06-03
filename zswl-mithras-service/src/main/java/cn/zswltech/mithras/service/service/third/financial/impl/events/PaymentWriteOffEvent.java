package cn.zswltech.mithras.service.service.third.financial.impl.events;

import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentActualDetail;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author yibin
 */
public class PaymentWriteOffEvent extends ApplicationEvent {
    @Getter
    private PaymentActualDetail paymentActualDetail;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public PaymentWriteOffEvent(Object source, PaymentActualDetail paymentActualDetail) {
        super(source);
        this.paymentActualDetail = paymentActualDetail;
    }
}
