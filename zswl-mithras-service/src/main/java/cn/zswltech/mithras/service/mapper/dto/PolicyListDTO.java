package cn.zswltech.mithras.service.mapper.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PolicyListDTO {

    //保单id
    private Long id;
    /**
     * 保单编号
     */
    private String policyCode;

    private Long projId;

    private String projName;

    private Long contractId;

    private Long paymentId;

    private String contractCode;

    private Long clientId;

    private LocalDate insuranceStartDate;

    private LocalDate insuranceEndDate;

    private String renewInsuranceFlag;

    private Long policyAmount;

    private String insuranceCompany;

    private Long projSponsorUserId;

    private String projCosponsorUserIds;

    private List<Long> projCosponsorUserIdList;

    private String approvalStatus;

    private String policyStatus;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String dataSource;

    private String identificationInformation;

    private String renewalRelationship ;
}
