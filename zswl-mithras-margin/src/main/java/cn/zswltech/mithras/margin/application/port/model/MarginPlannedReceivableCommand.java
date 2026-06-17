package cn.zswltech.mithras.margin.application.port.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class MarginPlannedReceivableCommand {

    private Long contractId;

    private String contractCode;

    private Long clientId;

    private Long amount;

    private Long totalReceivableAmount;

    private LocalDate createPlanDate;

    private LocalDate updatePlanDate;

    private boolean recycle;
}
