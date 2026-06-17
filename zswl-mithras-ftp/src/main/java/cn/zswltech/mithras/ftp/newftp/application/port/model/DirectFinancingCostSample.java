package cn.zswltech.mithras.ftp.newftp.application.port.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DirectFinancingCostSample {

    private LocalDate durationFrom;

    private LocalDate durationTo;

    private Long financingAmount;

    private Long comprehensiveFinancingCost;
}
