package cn.zswltech.mithras.capital.service;

import cn.zswltech.mithras.capital.service.model.CapitalBankFlowSaveDecision;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CapitalBankFlowSaveRuleService {

    public CapitalBankFlowSaveDecision resolveNewFlowIds(Collection<Long> remoteFlowIds,
                                                        Collection<Long> savedFlowIds) {
        if (remoteFlowIds == null) {
            remoteFlowIds = Collections.emptyList();
        }
        Set<Long> savedIds = savedFlowIds == null ? Collections.emptySet() : savedFlowIds.stream()
                .collect(Collectors.toSet());
        Set<Long> newFlowIds = remoteFlowIds.stream()
                .filter(id -> !savedIds.contains(id))
                .collect(Collectors.toSet());

        return new CapitalBankFlowSaveDecision(newFlowIds, YesOrNoNumberEnum.YES.getCode());
    }
}
