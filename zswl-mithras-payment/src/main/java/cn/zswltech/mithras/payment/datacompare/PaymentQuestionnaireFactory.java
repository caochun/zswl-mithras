package cn.zswltech.mithras.payment.datacompare;

import cn.zswltech.mithras.api.payment.dto.PaymentQuestionListRsp;
import cn.zswltech.mithras.foundation.persistence.mapper.CommonVersionMapper;
import cn.zswltech.mithras.payment.mapper.lib.PaymentQuestionnaireAnswerLibMapper;
import cn.zswltech.mithras.payment.mapper.model.PaymentQuestionnaireAnswer;
import cn.zswltech.mithras.payment.mapper.model.PaymentQuestionnaireAnswerLib;
import cn.zswltech.mithras.foundation.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.foundation.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.foundation.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.payment.application.lib.handler.PaymentQuestionAnswerHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/22 11:25
 */
@Service("paymentQuestionnaire")
public class PaymentQuestionnaireFactory implements EditdataCompareFactory {
    @Resource
    private PaymentQuestionnaireAnswerLibMapper libMapper;
    @Resource
    private PaymentQuestionAnswerHandler handler;
    @Resource
    private CommonVersionMapper commonVersionMapper;

    @Override
    public AbstractDataCompare createCompare(List rsps, String version) {
        return new DefaultDataCompare<PaymentQuestionnaireAnswer, PaymentQuestionnaireAnswerLib, PaymentQuestionListRsp>(rsps, libMapper, handler, commonVersionMapper, "PAYMENT", version);
    }
}
