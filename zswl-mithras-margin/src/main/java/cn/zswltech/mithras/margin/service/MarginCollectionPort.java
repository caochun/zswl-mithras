package cn.zswltech.mithras.margin.service;

import cn.zswltech.mithras.margin.service.model.MarginCollectionInfo;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface MarginCollectionPort {
    List<MarginCollectionInfo> listEarnestMoneyWrittenOffByContractIds(Collection<Long> contractIds);

    Map<Long, Long> sumCollectionRecordAmountByCollectionIdsBefore(Collection<Long> collectionIds, LocalDate actualDate);
}
