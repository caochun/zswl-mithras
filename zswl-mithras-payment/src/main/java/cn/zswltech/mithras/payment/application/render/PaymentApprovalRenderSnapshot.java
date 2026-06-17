package cn.zswltech.mithras.payment.application.render;

import lombok.Builder;
import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
@Builder
public class PaymentApprovalRenderSnapshot {

    private String bizDeptName;

    private String sponsorName;

    private Long projReviewId;

    private String projectType;

    private String lesseeName;

    private Long applyCreditAmount;

    private Long earnestMoney;

    private Integer leaseMonthCount;

    private Long consultingFee;

    private Long nominalPrice;

    private String legalPerson;

    private String consultingContractCode;

    @Builder.Default
    private List<String> guarantorContractCodes = Collections.emptyList();

    @Builder.Default
    private List<String> mortgageContractCodes = Collections.emptyList();
}
