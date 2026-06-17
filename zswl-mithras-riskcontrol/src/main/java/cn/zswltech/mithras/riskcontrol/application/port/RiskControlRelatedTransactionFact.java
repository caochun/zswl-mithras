package cn.zswltech.mithras.riskcontrol.application.port;

import lombok.Data;

import java.time.LocalDate;

@Data
public class RiskControlRelatedTransactionFact {
    private Long clientId;
    private String contractCode;
    private String cashFlowCode;
    private Long transactionAmount;
    private LocalDate transactionDate;
}
