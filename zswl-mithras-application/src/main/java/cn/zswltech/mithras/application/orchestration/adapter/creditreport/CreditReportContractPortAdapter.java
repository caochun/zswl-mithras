package cn.zswltech.mithras.application.orchestration.adapter.creditreport;

import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.contract.core.ContractTradeStructureService;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.creditreport.service.CreditReportContractPort;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CreditReportContractPortAdapter implements CreditReportContractPort {

    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractTradeStructureService contractTradeStructureService;

    @Override
    public List<Long> listContractIdsByProjReviewIds(List<Long> projReviewIds) {
        List<ContractBaseInfo> contractBaseInfos = contractBaseInfoService.listByProjReviewIds(projReviewIds);
        if (contractBaseInfos == null) {
            return Collections.emptyList();
        }
        return contractBaseInfos.stream()
                .map(ContractBaseInfo::getId)
                .collect(Collectors.toList());
    }

    @Override
    public Map<Long, LocalDate> getContractExpirationDateByRent(List<Long> contractIds) {
        return contractBaseInfoService.getContractExpirationDateByRent(contractIds);
    }

    @Override
    public List<Long> listClientIdsByContractId(Long contractId) {
        if (contractId == null) {
            return Collections.emptyList();
        }
        return contractTradeStructureService.listClientIdsByContractId(contractId);
    }
}
