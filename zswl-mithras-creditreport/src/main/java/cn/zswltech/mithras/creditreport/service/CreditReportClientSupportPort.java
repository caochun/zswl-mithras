package cn.zswltech.mithras.creditreport.service;

import cn.zswltech.mithras.customer.application.bo.ClientBusinessHistoryBO;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CreditReportClientSupportPort {

    Set<Long> findTargetClientIdsByUserId(Long userId);

    Set<Long> findTargetClientIdsByDeptIds(List<Long> deptIds);

    Map<Long, ClientBusinessHistoryBO> compareBusiness(List<Long> clientIds);
}
