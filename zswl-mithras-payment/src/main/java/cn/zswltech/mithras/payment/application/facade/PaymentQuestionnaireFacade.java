package cn.zswltech.mithras.payment.application.facade;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.payment.application.PaymentQuestionnaireApplicationService;
import cn.zswltech.mithras.api.payment.dto.PaymentQuestionListReq;
import cn.zswltech.mithras.api.payment.dto.PaymentQuestionListRsp;
import cn.zswltech.mithras.api.payment.dto.PaymentQuestionModifyReq;
import cn.zswltech.mithras.payment.application.PaymentQuestionnaireAnswerService;
import cn.zswltech.mithras.payment.dto.PaymentQuestionnaireModifyReq;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/15 11:43
 */
@Service
public class PaymentQuestionnaireFacade implements PaymentQuestionnaireApplicationService {
    @Resource
    private PaymentQuestionnaireAnswerService service;

    @Override

    public R<List<PaymentQuestionListRsp>> list(PaymentQuestionListReq req) {
        return R.ok(service.list(req));
    }

    @Override
    public R<Void> modify(List<PaymentQuestionModifyReq> reqs) {
        PaymentQuestionnaireModifyReq questionnaireModifyReq =
                new PaymentQuestionnaireModifyReq();
        if(ObjectUtil.isNotEmpty(reqs)){
            questionnaireModifyReq.setPaymentId(reqs.get(0).getPaymentId());
            questionnaireModifyReq.setReqs(reqs);
            service.modify(questionnaireModifyReq);
        }
        return R.ok();
    }
}
