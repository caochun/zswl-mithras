package cn.zswltech.mithras.projectprocess.excel.payment;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface PaymentCashFlowQueryPort {
    List<PaymentBaseInfoData> listPaymentBaseInfoByReceiptId(Long receiptId);

    List<PaymentActualDetailData> listActualDetailByPaymentId(Long paymentId);

    Map<Long, List<PaymentActualDetailData>> mapActualDetailByPaymentIds(Collection<Long> paymentIds);
}
