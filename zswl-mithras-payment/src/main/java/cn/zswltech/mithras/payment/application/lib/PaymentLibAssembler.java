package cn.zswltech.mithras.payment.application.lib;

import cn.zswltech.mithras.api.payment.dto.PaymentDetailRsp;
import cn.zswltech.mithras.api.payment.dto.PaymentQuestionListRsp;
import cn.zswltech.mithras.payment.model.PaymentBaseInfo;
import cn.zswltech.mithras.payment.model.PaymentBaseInfoLib;
import cn.zswltech.mithras.payment.model.PaymentQuestionnaireAnswer;

/**
 * Assembly boundary for payment lib handlers.
 */
public interface PaymentLibAssembler {

    PaymentBaseInfoLib baseInfoEntity2Lib(PaymentBaseInfo entity);

    PaymentBaseInfo baseInfoLib2Entity(PaymentBaseInfoLib lib);

    PaymentDetailRsp baseInfoLib2DetailRsp(PaymentBaseInfoLib lib);

    PaymentQuestionListRsp questionnaireAnswer2Rsp(PaymentQuestionnaireAnswer answer);
}
