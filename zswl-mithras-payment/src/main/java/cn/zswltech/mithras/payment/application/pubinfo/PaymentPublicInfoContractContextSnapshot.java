package cn.zswltech.mithras.payment.application.pubinfo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentPublicInfoContractContextSnapshot {

    private Long contractId;

    private Long clientId;

    private String originClientType;

    private Long projSponsorUserId;

    private Long projReviewId;
}
