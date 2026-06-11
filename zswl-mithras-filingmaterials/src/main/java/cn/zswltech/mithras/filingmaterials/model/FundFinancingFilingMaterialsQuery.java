package cn.zswltech.mithras.filingmaterials.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FundFinancingFilingMaterialsQuery {
    /**
     * 融资编号
     */
    private String financingCode;

    /**
     * 归档类型
     */
    private String filingType;

    /**
     * 业务类型
     */
    private String bizType;

    /**
     * 融资机构id
     */
    private String organizationId;

    /**
     * 融资状态
     */
    private String financingStatus;

    /**
     * 资金经理id
     */
    private Long fundManagerId;

    /**
     * 归档标识
     */
    private Boolean archiveFlag;

    /**
     * 起息日-从
     */
    private LocalDate carryInterestTimeFrom;

    /**
     * 起息日-到
     */
    private LocalDate carryInterestTimeTo;

    /**
     * 到期日-从
     */
    private LocalDate endDateFrom;

    /**
     * 到期日-到
     */
    private LocalDate endDateTo;

    /**
     * 归档日期-从
     */
    private LocalDateTime archiveDateFrom;

    /**
     * 归档日期-到
     */
    private LocalDateTime archiveDateTo;
}
