package cn.zswltech.mithras.payment.application;

import cn.zswltech.mithras.foundation.enums.common.ProcessStatus;
import cn.zswltech.mithras.foundation.enums.common.RecordStatus;

public interface PaymentStatusUpdatePort {

    boolean hasRelatedProcess(Long paymentId);

    void recordPaymentStatus(Long paymentId, RecordStatus paymentStatus, ProcessStatus processStatus);
}
