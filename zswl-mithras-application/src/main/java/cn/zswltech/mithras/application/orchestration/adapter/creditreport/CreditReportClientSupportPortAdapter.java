package cn.zswltech.mithras.application.orchestration.adapter.creditreport;

import cn.zswltech.mithras.creditreport.service.CreditReportClientSupportPort;
import cn.zswltech.mithras.customer.application.client.model.ClientBusinessHistoryBO;
import cn.zswltech.mithras.application.orchestration.client.ClientService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class CreditReportClientSupportPortAdapter implements CreditReportClientSupportPort {

    @Resource
    private ClientService clientService;

    @Override
    public Set<Long> findTargetClientIdsByUserId(Long userId) {
        return clientService.findTargetClientIdsByUserId(userId);
    }

    @Override
    public Set<Long> findTargetClientIdsByDeptIds(List<Long> deptIds) {
        return clientService.findTargetClientIdsByDeptIds(deptIds);
    }

    @Override
    public Map<Long, ClientBusinessHistoryBO> compareBusiness(List<Long> clientIds) {
        return clientService.compareBusiness(clientIds);
    }
}
