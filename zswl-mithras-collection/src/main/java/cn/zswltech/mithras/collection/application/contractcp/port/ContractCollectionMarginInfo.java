package cn.zswltech.mithras.collection.application.contractcp.port;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ContractCollectionMarginInfo {

    private Long id;

    private Long contractId;

    private String marginCode;

    private Long collectionAmount;

    private Long planMarginAmount;

    private LocalDate planMarginDate;
}
