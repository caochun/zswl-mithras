package cn.zswltech.mithras.payment.interfaces.dto;

import cn.zswltech.mithras.api.payment.dto.PaymentQuestionModifyReq;
import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/24 11:09
 */
@Data
public class PaymentQuestionnaireModifyReq {
    private Long paymentId;
    private List<PaymentQuestionModifyReq> reqs;
}
