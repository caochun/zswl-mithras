package cn.zswltech.mithras.associationreport.application;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface AssociationReportClientSupportPort {

    List<AssociationReportClientSnapshot> listEffectiveCorporationClients();

    AssociationReportClientSnapshot getClient(Long clientId);

    String findMatchedShareholderName(Long clientId, List<String> shareholderNames);

    Map<Long, Long> getClientRemainingPrincipalMap(List<Long> clientIds, LocalDate targetDate);

    Map<Long, Long> clientStockRiskExposureMap(List<Long> clientIds, LocalDate targetDate);
}
