package cn.zswltech.mithras.policy.dto.persistence;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PaymentPolicyEndTimeDTO {

    private Long projId;

    private LocalDate maxDate;

    private String paymentCode;
}
