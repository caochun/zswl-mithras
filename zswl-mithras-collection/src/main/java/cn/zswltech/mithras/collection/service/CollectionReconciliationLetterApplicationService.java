package cn.zswltech.mithras.collection.service;

import cn.zswltech.mithras.dto.collection.CollectionReconciliationLetterREQ;
import cn.zswltech.mithras.collection.service.bo.ReconciliationLetterBO;

import java.io.OutputStream;
import java.time.LocalDate;
import java.util.Map;

public interface CollectionReconciliationLetterApplicationService {

    void exportExcel(CollectionReconciliationLetterREQ req, OutputStream outputStream);

    Map<Long, ReconciliationLetterBO> statisticsClientsLetter(LocalDate localDateTime);
}
