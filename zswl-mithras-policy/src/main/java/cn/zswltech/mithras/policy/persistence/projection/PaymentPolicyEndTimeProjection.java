package cn.zswltech.mithras.policy.persistence.projection;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PaymentPolicyEndTimeProjection {

    private Long projId;

    private LocalDate maxDate;

    private String paymentCode;
}
