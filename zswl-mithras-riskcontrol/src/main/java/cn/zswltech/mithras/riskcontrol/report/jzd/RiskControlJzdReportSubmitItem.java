package cn.zswltech.mithras.riskcontrol.report.jzd;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class RiskControlJzdReportSubmitItem {

    private String bizType;

    private String subjectName;

    private BigDecimal bizTotalAmount;

    private BigDecimal bizRestAmount;

    private String name;

    private Boolean sameIndustryCustomer;

    private Integer economicComposition;

    private String bizDept;

    private LocalDate bizDateStart;

    private LocalDate bizDateEnd;

    private BigDecimal ensure;

    private String guaranteeName;

    private BigDecimal decrease;

    private Integer overdueDay;

    private BigDecimal overdueAmount;

    private Byte assetsCategory;
}
