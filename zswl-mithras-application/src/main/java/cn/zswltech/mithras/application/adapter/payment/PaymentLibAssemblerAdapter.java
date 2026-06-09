package cn.zswltech.mithras.application.adapter.payment;

import cn.zswltech.mithras.api.payment.dto.PaymentDetailRsp;
import cn.zswltech.mithras.api.payment.dto.PaymentQuestionListRsp;
import cn.zswltech.mithras.payment.application.PaymentQuestionnaireAnswerService;
import cn.zswltech.mithras.payment.application.lib.PaymentLibAssembler;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfoLib;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentQuestionnaireAnswer;
import cn.zswltech.mithras.payment.application.convert.PaymentConvert;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class PaymentLibAssemblerAdapter implements PaymentLibAssembler {

    @Resource
    private PaymentConvert paymentConvert;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private PaymentQuestionnaireAnswerService paymentQuestionnaireAnswerService;

    @Override
    public PaymentBaseInfoLib baseInfoEntity2Lib(PaymentBaseInfo entity) {
        return paymentConvert.entity2Lib(entity);
    }

    @Override
    public PaymentBaseInfo baseInfoLib2Entity(PaymentBaseInfoLib lib) {
        return paymentConvert.lib2Entity(lib);
    }

    @Override
    public PaymentDetailRsp baseInfoLib2DetailRsp(PaymentBaseInfoLib lib) {
        return paymentBaseInfoService.joinContractInfo(null, null, baseInfoLib2Entity(lib));
    }

    @Override
    public PaymentQuestionListRsp questionnaireAnswer2Rsp(PaymentQuestionnaireAnswer answer) {
        return paymentQuestionnaireAnswerService.join(answer);
    }
}
