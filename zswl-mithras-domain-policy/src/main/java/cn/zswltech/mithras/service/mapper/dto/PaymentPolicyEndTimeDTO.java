package cn.zswltech.mithras.service.mapper.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PaymentPolicyEndTimeDTO {

    private Long projId;

    private LocalDate maxDate;

    private String paymentCode;
}
