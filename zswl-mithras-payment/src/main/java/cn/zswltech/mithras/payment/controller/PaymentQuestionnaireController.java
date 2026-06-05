package cn.zswltech.mithras.payment.controller;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.payment.PaymentQuestionnaireApi;
import cn.zswltech.mithras.api.payment.dto.PaymentQuestionListReq;
import cn.zswltech.mithras.api.payment.dto.PaymentQuestionListRsp;
import cn.zswltech.mithras.api.payment.dto.PaymentQuestionModifyReq;
import java.util.List;
import cn.zswltech.mithras.payment.application.PaymentQuestionnaireApplicationService;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

@RestController
public class PaymentQuestionnaireController implements PaymentQuestionnaireApi {
    @Resource
    private PaymentQuestionnaireApplicationService paymentQuestionnaireApplicationService;

    @Override
    public R<List<PaymentQuestionListRsp>> list(PaymentQuestionListReq req) {
        return paymentQuestionnaireApplicationService.list(req);
    }

    @Override
    public R<Void> modify(List<PaymentQuestionModifyReq> reqs) {
        return paymentQuestionnaireApplicationService.modify(reqs);
    }

}
