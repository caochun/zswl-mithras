package cn.zswltech.mithras.service.service.payment.model;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import static cn.hutool.core.text.CharSequenceUtil.isBlank;
import static cn.hutool.core.util.ObjectUtil.isNull;
import static cn.zswltech.mithras.service.others.MithrasException.err;

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
        if (isNull(paymentId) || isNull(receiptId) || isBlank(receiptCode)) {
            log.warn("参数不正确, {}", this);
            err("参数不正确");
        }
    }
}
