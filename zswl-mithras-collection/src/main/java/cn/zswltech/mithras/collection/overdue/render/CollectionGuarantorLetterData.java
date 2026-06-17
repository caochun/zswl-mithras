package cn.zswltech.mithras.collection.overdue.render;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CollectionGuarantorLetterData {

    private Long id;

    private String letterCode;

    private String contractCode;

    private String guarantorIds;

    private String guarantorContractCode;

    private String guarantorType;

    private String lesseeNames;

    private Long lateCharge;

    private Long overdueAmount;

    private Integer overdueDays;

    private String phases;

    private LocalDate genDate;
}
