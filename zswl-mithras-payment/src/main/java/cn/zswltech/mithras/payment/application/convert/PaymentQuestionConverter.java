package cn.zswltech.mithras.payment.application.convert;

import cn.zswltech.mithras.api.payment.dto.PaymentQuestionListRsp;
import cn.zswltech.mithras.api.payment.dto.PaymentQuestionModifyReq;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentQuestionnaire;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentQuestionnaireAnswer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/15 13:49
 */
@Mapper(componentModel = "spring")
public interface PaymentQuestionConverter {
    @Mapping(target = "id", source = "answer.id")
    @Mapping(target = "questionId", source = "question.id")
    @Mapping(target = "seqCode", source = "question.seqCode")
    @Mapping(target = "questionType", source = "question.questionType")
    @Mapping(target = "question", source = "question.question")
    @Mapping(target = "questionAnswer", source = "answer.questionAnswer")
    @Mapping(target = "remarks", source = "answer.remarks")
    @Mapping(target = "paymentId", source = "answer.paymentId")
    PaymentQuestionListRsp join(PaymentQuestionnaire question, PaymentQuestionnaireAnswer answer);

    PaymentQuestionnaireAnswer modifyReqToEntity(PaymentQuestionModifyReq req);
    List<PaymentQuestionnaireAnswer> modifyReqToEntities(List<PaymentQuestionModifyReq> reqs);
}
