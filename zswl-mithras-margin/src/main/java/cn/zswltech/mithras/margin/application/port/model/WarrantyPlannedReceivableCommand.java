package cn.zswltech.mithras.margin.application.port.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class WarrantyPlannedReceivableCommand {

    private Long contractId;

    private String contractCode;

    private Long clientId;

    private Long amount;

    private Long totalReceivableAmount;

    private LocalDate planDate;

    private boolean recycle;
}
