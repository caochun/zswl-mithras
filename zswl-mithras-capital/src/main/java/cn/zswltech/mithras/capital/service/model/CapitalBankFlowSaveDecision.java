package cn.zswltech.mithras.capital.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CapitalBankFlowSaveDecision {

    private Set<Long> newFlowIds;

    private Integer defaultShowInList;

    public Set<Long> getNewFlowIds() {
        return newFlowIds == null ? Collections.emptySet() : newFlowIds;
    }
}
