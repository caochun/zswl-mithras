package cn.zswltech.mithras.riskcontrol.application.port;

import java.time.LocalDate;

public interface RiskControlContractFactPort {

    Long maxSingleContractClientApplyCreditAmountBefore(LocalDate snapshotDate);
}
