package cn.zswltech.mithras.capital.rule;

import cn.zswltech.mithras.capital.rule.model.CapitalBankFlowSyncDiff;
import cn.zswltech.mithras.capital.rule.model.CapitalBankFlowSyncSnapshot;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CapitalBankFlowSyncRuleService {

    public CapitalBankFlowSyncDiff diffByBillNo(List<CapitalBankFlowSyncSnapshot> savedFlows,
                                                List<CapitalBankFlowSyncSnapshot> remoteFlows) {
        if (savedFlows == null) {
            savedFlows = Collections.emptyList();
        }
        if (remoteFlows == null) {
            remoteFlows = Collections.emptyList();
        }

        Set<String> savedBillNos = savedFlows.stream()
                .map(CapitalBankFlowSyncSnapshot::getBillNo)
                .collect(Collectors.toSet());
        Set<String> remoteBillNos = remoteFlows.stream()
                .map(CapitalBankFlowSyncSnapshot::getBillNo)
                .collect(Collectors.toSet());

        List<CapitalBankFlowSyncSnapshot> addedFlows = remoteFlows.stream()
                .filter(flow -> flow != null && !savedBillNos.contains(flow.getBillNo()))
                .collect(Collectors.toList());

        List<Long> deletedFlowIds = savedFlows.stream()
                .filter(flow -> flow != null && !remoteBillNos.contains(flow.getBillNo()))
                .map(CapitalBankFlowSyncSnapshot::getId)
                .collect(Collectors.toList());

        return new CapitalBankFlowSyncDiff(addedFlows, deletedFlowIds);
    }
}
