package cn.zswltech.mithras.service.mapper.model.dashboard;

import lombok.Data;

import java.time.LocalDate;

/**
 * @author dingqi
 * @date 2024/6/26
 * @description
 */
@Data
public class DashboardProjectPledgeResult {
    private Integer isDirect;
    private Long financingId;
    private Long contractId;
    private String contractCode;
    private String contractStatus;
    private String orgName;
    private Long financingAmount;
    private LocalDate durationFrom;
    private LocalDate durationTo;
    private Integer isPledge;
    private Integer isSupervise;
    private Long bizDeptId;
    private Long projSponsorUserId;
    private String accountName;
    private String accountBank;
    private String accountNumber;

    /**
     * DK（间融） ZR（直融）
     */
    private String type;
    private String projName;
    private String businessType;
    private String financingCode;
    private String financingStatus;
    private Long totalPayAmount;
    private Long planCollectionAmount;
    private Long collectionAmount;
    private Long principal;
    private Long collectionPrincipal;



}
