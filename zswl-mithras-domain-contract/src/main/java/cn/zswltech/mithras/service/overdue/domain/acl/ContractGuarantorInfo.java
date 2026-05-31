package cn.zswltech.mithras.service.overdue.domain.acl;

import lombok.Data;

import java.time.LocalDate;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2024/10/25 17:28
 */
@Data
public class ContractGuarantorInfo {

    private Long id;

    private String letterCode;

    private String contractCode;

    private String guarantorIds;

    private String guarantorContractCode;

    private String guarantorType;

    /**
     * 承租人
     */
    private String lesseeNames;

    private Long lateCharge;

    private Long overdueAmount;

    private Integer overdueDays;

    private String phases;

    private LocalDate genDate;
}
