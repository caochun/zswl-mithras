package cn.zswltech.mithras.service.mapper.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author zhaozhengkang
 */
@Data
public class ContractListSelectDTO extends BaseAuthDTO {

    private Long clientId;

    private String projName;

    private String contractCode;

    private String bizType;

    private Long bizDeptId;

    private Long projSponsorUserId;

    private List<Long> projCosponsorUserIdList;

    private Long projCosponsorUserId;

    private String contractStatus;

    private List<String> contractStatuses;

    private String contractProcessStatus;

    private LocalDateTime createFrom;

    private LocalDateTime createTo;

    private LocalDateTime updateFrom;

    private LocalDateTime updateTo;

    private Long planedPaidAmountFrom;

    private Long planedPaidAmountTo;

    private LocalDate planedPaidDateFrom;

    private LocalDate plListdDateTo;

    private LocalDate planedPaidDateTo;

}
