package cn.zswltech.mithras.assetclassify.application.port;

import java.util.Collection;
import java.util.Map;

public interface AssetClassifyCollectionWriteOffPort {

    Map<Long, Long> getRentPrincipalByReceiptIds(Collection<Long> receiptIds);

    Map<Long, Long> getFirstRentAmountByReceiptIds(Collection<Long> receiptIds);
}
