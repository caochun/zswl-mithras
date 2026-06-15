package cn.zswltech.mithras.assetclassify.application.port;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;

public interface AssetClassifyMarginAmountPort {

    Map<Long, Long> getAmountByReceiptIds(Collection<Long> receiptIds, LocalDate actualDate);
}
