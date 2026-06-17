package cn.zswltech.mithras.creditreport.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CreditReportClientSupportPort {

    Set<Long> findTargetClientIdsByUserId(Long userId);

    Set<Long> findTargetClientIdsByDeptIds(List<Long> deptIds);

    Map<Long, CreditReportClientBusinessSnapshot> compareBusiness(List<Long> clientIds);

    Map<Long, CreditReportClientBusinessSnapshot> getHistoryBusiness(List<Long> clientIds);

    CreditReportClientSnapshot getClient(Long clientId);

    List<CreditReportClientSnapshot> listCorporationClients(List<Long> clientIds);

    Map<Long, CreditReportClientCommerceSnapshot> listCommerceSnapshots(List<Long> clientIds);
}
