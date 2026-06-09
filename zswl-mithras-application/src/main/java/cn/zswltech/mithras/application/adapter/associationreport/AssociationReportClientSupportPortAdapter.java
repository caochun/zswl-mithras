package cn.zswltech.mithras.application.adapter.associationreport;

import cn.zswltech.mithras.associationreport.application.AssociationReportClientSupportPort;
import cn.zswltech.mithras.service.service.client.ClientService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
public class AssociationReportClientSupportPortAdapter implements AssociationReportClientSupportPort {

    @Resource
    private ClientService clientService;

    @Override
    public Map<Long, Long> getClientRemainingPrincipalMap(List<Long> clientIds, LocalDate targetDate) {
        return clientService.getClientRemainingPrincipalMap(clientIds, targetDate);
    }

    @Override
    public Map<Long, Long> clientStockRiskExposureMap(List<Long> clientIds, LocalDate targetDate) {
        return clientService.clientStockRiskExposureMap(clientIds, targetDate);
    }
}
