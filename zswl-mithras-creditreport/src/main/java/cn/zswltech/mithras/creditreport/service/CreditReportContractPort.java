package cn.zswltech.mithras.creditreport.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface CreditReportContractPort {

    List<Long> listContractIdsByProjReviewIds(List<Long> projReviewIds);

    Map<Long, LocalDate> getContractExpirationDateByRent(List<Long> contractIds);

    List<Long> listClientIdsByContractId(Long contractId);
}
