package cn.zswltech.mithras.assetclassify.application.port;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface AssetClassifyPaymentFactPort {

    List<AssetClassifyPaymentBaseSnapshot> listPaymentsByReceiptIds(Collection<Long> receiptIds);

    Map<Long, Long> getWrittenOffPaidAmountByPaymentIds(Collection<Long> paymentIds);

    Map<Long, Long> getWrittenOffOrPartWrittenOffPaidAmountByPaymentIds(Collection<Long> paymentIds);
}
