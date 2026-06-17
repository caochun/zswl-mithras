package cn.zswltech.mithras.riskcontrol.application.port;

import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class RiskControlRelatedTransactionQuery {
    private Set<Long> clientIds;
    private long page;
    private long pageSize;
    private String contractCode;
    private Long transactionAmountFrom;
    private Long transactionAmountTo;
    private LocalDate transactionDateFrom;
    private LocalDate transactionDateTo;
}
