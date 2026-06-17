package cn.zswltech.mithras.assetclassify.application.port;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface AssetClassifyContractFactPort {

    List<AssetClassifyContractSnapshot> listContractsByIds(Collection<Long> contractIds);

    Map<Long, Integer> countRemainingPhasesByContractIds(Collection<Long> contractIds, LocalDate cashFlowDateAfter);

    Set<Long> listExistingReceiptIds(Collection<Long> receiptIds);
}
