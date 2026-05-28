package cn.zswltech.mithras.service.service.lib.payment.handler;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.api.payment.dto.PaymentQuestionListRsp;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentQuestionnaireAnswer;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentQuestionnaireAnswerLib;
import cn.zswltech.mithras.service.service.payment.PaymentQuestionnaireAnswerService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static cn.zswltech.mithras.service.service.lib.payment.handler.PaymentInfoModule.QUESTIONNAIRE;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/20 15:17
 */
@Service
public class PaymentQuestionAnswerHandler
        extends PaymentAbstractHandler<PaymentQuestionnaireAnswerLib, PaymentQuestionnaireAnswer, PaymentQuestionListRsp>{

    @Resource
    private PaymentQuestionnaireAnswerService answerService;
    @Override
    protected PaymentQuestionnaireAnswerLib entity2Lib(PaymentQuestionnaireAnswer f) {
        return BeanUtil.copyProperties(f, PaymentQuestionnaireAnswerLib.class);
    }

    @Override
    protected PaymentQuestionnaireAnswer lib2Entity(PaymentQuestionnaireAnswerLib t) {
        return BeanUtil.copyProperties(t, PaymentQuestionnaireAnswer.class);
    }

    @Override
    protected PaymentQuestionListRsp lib2Rsp(PaymentQuestionnaireAnswerLib f) {
        PaymentQuestionListRsp rsp = answerService.join(lib2Entity(f));
        rsp.setId(f.getOriginId());
        return rsp;
    }

    @Override
    public PaymentInfoModule getSubModule() {
        return QUESTIONNAIRE;
    }

    @Override
    public boolean needHandle(Long mainId) {
        return true;
    }
}
