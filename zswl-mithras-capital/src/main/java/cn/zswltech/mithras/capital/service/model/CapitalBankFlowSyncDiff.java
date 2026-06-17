package cn.zswltech.mithras.capital.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CapitalBankFlowSyncDiff {

    private List<CapitalBankFlowSyncSnapshot> addedFlows;

    private List<Long> deletedFlowIds;

    public List<CapitalBankFlowSyncSnapshot> getAddedFlows() {
        return addedFlows == null ? Collections.emptyList() : addedFlows;
    }

    public List<Long> getDeletedFlowIds() {
        return deletedFlowIds == null ? Collections.emptyList() : deletedFlowIds;
    }
}
