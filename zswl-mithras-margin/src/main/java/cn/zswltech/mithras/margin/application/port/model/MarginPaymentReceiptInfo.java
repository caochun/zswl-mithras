package cn.zswltech.mithras.margin.application.port.model;

import lombok.Data;

@Data
public class MarginPaymentReceiptInfo {
    private Long id;
    private Long contractId;
    private Long receiptId;
}
