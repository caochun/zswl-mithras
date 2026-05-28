package cn.zswltech.mithras.dto.flow.form;

import lombok.Data;

import java.util.List;

/**
 * @author dingqi
 * @date 2023/9/1
 * @description
 */
@Data
public class SetPaymentFtpFormRSP {
    private Long contractId;
    private List<PaymentData> paymentList;

    @Data
    public static class PaymentData {
        private Long paymentId;
        private String paymentCode;
        private Long actualPayAmount;
        private Integer cashFtp;
        private Boolean canEditCashFtp;
        private Integer billFtp;
        private Boolean canEditBillFtp;
    }
}
