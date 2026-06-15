package cn.zswltech.mithras.margin.application.port;

import cn.zswltech.mithras.margin.application.port.model.MarginPaymentReceiptInfo;

import java.util.Collection;
import java.util.List;

public interface MarginPaymentReceiptPort {
    List<MarginPaymentReceiptInfo> listByReceiptIds(Collection<Long> receiptIds);
}
