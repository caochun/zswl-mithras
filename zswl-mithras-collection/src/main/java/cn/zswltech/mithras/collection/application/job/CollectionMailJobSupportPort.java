package cn.zswltech.mithras.collection.application.job;

import cn.zswltech.mithras.dto.afterlease.RentCollectionBaseInfo;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface CollectionMailJobSupportPort {

    String getGracePeriod(Long contractId, Long paymentId, Integer phase);

    CollectionMailContractInfo getContractById(Long contractId);

    Set<Long> getUserIdsByRole(String roleCode);

    Set<String> getUserEmailSet(Set<Long> userIds);

    CollectionMailClientInfo getClientById(Long clientId);

    List<CollectionMailContactInfo> listCorpContactInfo(Long clientId);

    String getLatestContractVersion(Long contractId);

    String getReportedLesseeNames(Long contractId, String version);

    boolean isWorkDay(LocalDate date);

    int countWorkdayNumber(LocalDate start, LocalDate end);

    Long getNominalPrice(Long contractId, String version);

    void sendCollectionRentEmail(Set<String> receivers, Set<String> carbonCopies, RentCollectionBaseInfo data);

    void sendRentExpireEmail(Set<String> receivers, Set<String> carbonCopies, RentCollectionBaseInfo data);
}
