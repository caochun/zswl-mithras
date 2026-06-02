package cn.zswltech.mithras.contract.overdue.domain.acl;

import lombok.Data;

import java.time.LocalDate;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/25 14:14
 */
@Data
public class ContractLesseeInfo {
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
