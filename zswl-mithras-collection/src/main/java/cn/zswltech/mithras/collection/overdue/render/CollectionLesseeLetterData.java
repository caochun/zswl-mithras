package cn.zswltech.mithras.collection.overdue.render;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CollectionLesseeLetterData {

    private Long id;

    private String letterCode;

    private String contractCode;

    private Long lesseeId;

    private String lesseeName;

    private String lesseeType;

    private String address;

    private Long lateCharge;

    private Long overdueAmount;

    private Integer overdueDays;

    private String phases;

    private String projectSponsorName;

    private String projectSponsorPhone;

    private LocalDate genDate;

    private Long collectionId;
}
