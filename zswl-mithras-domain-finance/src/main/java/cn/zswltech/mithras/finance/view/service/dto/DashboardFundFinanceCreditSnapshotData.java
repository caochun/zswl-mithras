package cn.zswltech.mithras.finance.view.service.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DashboardFundFinanceCreditSnapshotData {

    private Long id;

    private String creditCode;

    private String organizationName;

    private String businessType;

    private Long totalCreditLimit;

    private Long usedCreditLimit;

    private Integer recyclable;

    private LocalDate deadline;
}
