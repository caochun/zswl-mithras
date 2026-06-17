package cn.zswltech.mithras.margin.application.port.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class MarginCollectionSnapshot {

    private Long id;

    private Long contractId;

    private String marginCode;

    private Long collectionAmount;

    private Long planMarginAmount;

    private LocalDate planMarginDate;
}
