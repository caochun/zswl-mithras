package cn.zswltech.mithras.capital.rule.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Capital-owned view of bank flow fields used by local flow rules.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CapitalBankFlowSnapshot {

    private Long id;

    private String oppositeUnit;

    private String description;

    private String oppositeBankNumber;
}
