package cn.zswltech.mithras.riskcontrol.report.gljy;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class RiskControlRelationTradeSubmitItem {
    private BigDecimal amount;
    private String description;
    private Integer importantReason;
    private Integer level;
    private String opinion;
    private String purpose;
    private String risk;
    private String tradeCategoryParentName;
    private String tradeCategoryName;
    private LocalDate tradeDate;
    private BigDecimal tradePartyAssets;
    private String tradePartyName;
    private String subjectPartyName;
}
