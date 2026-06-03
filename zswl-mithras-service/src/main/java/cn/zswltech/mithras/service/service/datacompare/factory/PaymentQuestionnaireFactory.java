package cn.zswltech.mithras.service.service.datacompare.factory;

import cn.zswltech.mithras.api.payment.dto.PaymentQuestionListRsp;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.mapper.lib.CommonVersionMapper;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.lib.PaymentQuestionnaireAnswerLibMapper;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentQuestionnaireAnswer;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentQuestionnaireAnswerLib;
import cn.zswltech.mithras.service.service.datacompare.AbstractDataCompare;
import cn.zswltech.mithras.service.service.datacompare.EditdataCompareFactory;
import cn.zswltech.mithras.service.service.datacompare.compare.DefaultDataCompare;
import cn.zswltech.mithras.service.service.lib.payment.handler.PaymentQuestionAnswerHandler;
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
        return new DefaultDataCompare<PaymentQuestionnaireAnswer, PaymentQuestionnaireAnswerLib, PaymentQuestionListRsp>(rsps, libMapper, handler, commonVersionMapper, BusinessModuleEnum.PAYMENT.name(), version);
    }
}
