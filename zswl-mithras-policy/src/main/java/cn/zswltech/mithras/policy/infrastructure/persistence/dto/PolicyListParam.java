package cn.zswltech.mithras.policy.infrastructure.persistence.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class PolicyListParam {
    private String insuranceCompany;

    private Long clientId;

    private String projName;

    private String contractCode;

    private String policyCode;

    private LocalDate insuranceStartDateFrom;

    private LocalDate insuranceStartDateTo;

    private LocalDate insuranceEndDateFrom;

    private LocalDate insuranceEndDateTo;

    private Long projSponsorUserId;

    private Long projCosponsorUserId;

    private String approvalStatus;

    private String paymentApprovalStatus;

    private List<Long> policyIds;

    private List<Long> paymentPolicyIds;

    private Boolean isAllJob;
    private Long currentUserId;

    private String renewInsuranceFlag;

    private String policyStatus;

    private Long authId;

    private List<Long> authDeptIds;

    private String dataStatus;
}
