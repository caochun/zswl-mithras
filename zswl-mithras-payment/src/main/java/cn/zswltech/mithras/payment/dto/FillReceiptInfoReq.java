package cn.zswltech.mithras.payment.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import static cn.zswltech.mithras.foundation.exception.MithrasException.err;

/**
 * @author yibin
 */
@Slf4j
@Data
@Accessors(chain = true)
public class FillReceiptInfoReq {

    private Long paymentId;
    private Long receiptId;
    private String receiptCode;


    public void valid() {
        if (paymentId == null || receiptId == null || receiptCode == null || receiptCode.trim().isEmpty()) {
            log.warn("参数不正确, {}", this);
            err("参数不正确");
        }
    }
}
