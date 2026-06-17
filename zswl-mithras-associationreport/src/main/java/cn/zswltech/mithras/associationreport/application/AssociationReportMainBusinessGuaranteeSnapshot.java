package cn.zswltech.mithras.associationreport.application;

import lombok.Builder;
import lombok.Value;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Value
@Builder
public class AssociationReportMainBusinessGuaranteeSnapshot {

    List<String> guarantorNames;

    List<String> mortgageDescriptions;

    List<String> pledgeDescriptions;

    public boolean hasGuarantor() {
        return !safeGuarantorNames().isEmpty();
    }

    public boolean hasMortgage() {
        return !safeMortgageDescriptions().isEmpty();
    }

    public boolean hasPledge() {
        return !safePledgeDescriptions().isEmpty();
    }

    public List<String> safeGuarantorNames() {
        return Optional.ofNullable(guarantorNames).orElse(Collections.emptyList());
    }

    public List<String> safeMortgageDescriptions() {
        return Optional.ofNullable(mortgageDescriptions).orElse(Collections.emptyList());
    }

    public List<String> safePledgeDescriptions() {
        return Optional.ofNullable(pledgeDescriptions).orElse(Collections.emptyList());
    }
}
