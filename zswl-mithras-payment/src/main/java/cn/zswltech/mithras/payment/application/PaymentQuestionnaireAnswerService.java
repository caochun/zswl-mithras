package cn.zswltech.mithras.payment.application;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.payment.dto.PaymentQuestionListReq;
import cn.zswltech.mithras.api.payment.dto.PaymentQuestionListRsp;
import cn.zswltech.mithras.api.payment.dto.PaymentQuestionModifyReq;
import cn.zswltech.mithras.payment.application.convert.PaymentQuestionConverter;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.PaymentQuestionnaireAnswerMapper;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.PaymentQuestionnaireMapper;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentQuestionnaire;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentQuestionnaireAnswer;
import cn.zswltech.mithras.payment.interfaces.dto.PaymentQuestionnaireModifyReq;
import cn.zswltech.mithras.service.others.MithrasException;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhaozhengkang
 * @description payment_questionnaire_answer
 * @date 2022-08-15
 */
@Service
public class PaymentQuestionnaireAnswerService
        extends ServiceImpl<PaymentQuestionnaireAnswerMapper, PaymentQuestionnaireAnswer>
        implements PaymentUpdateAdvice {
    @Resource
    private PaymentQuestionnaireMapper questionnaireMapper;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Resource
    private PaymentModifyAuthPort paymentModifyAuthPort;
    @Resource
    private PaymentQuestionConverter converter;
    @Value("${app.payment.questionnaire.version}")
    private String version;

    private final Map<Long, PaymentQuestionnaire> questionnaireCache = new HashMap<>();

    @PostConstruct
    public void init() {
        List<PaymentQuestionnaire> questionnaires = questionnaireMapper.selectList(
                Wrappers.<PaymentQuestionnaire>lambdaQuery().eq(PaymentQuestionnaire::getVersion, version)
                        .orderBy(true, true, PaymentQuestionnaire::getSeqCode));
        for (PaymentQuestionnaire questionnaire : questionnaires) {
            questionnaireCache.put(questionnaire.getId(), questionnaire);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void create(Long paymentId) {
        List<PaymentQuestionnaireAnswer> questionAnswers = new ArrayList<>();
        for (PaymentQuestionnaire question : questionnaireCache.values()) {
            PaymentQuestionnaireAnswer questionAnswer = new PaymentQuestionnaireAnswer();
            questionAnswer.setPaymentId(paymentId);
            questionAnswer.setQuestionId(question.getId());
            questionAnswers.add(questionAnswer);
        }
        saveBatch(questionAnswers);
    }

    public List list(PaymentQuestionListReq req) {
        List<PaymentQuestionnaireAnswer> answers = baseMapper.selectList(
                Wrappers.<PaymentQuestionnaireAnswer>lambdaQuery().eq(PaymentQuestionnaireAnswer::getPaymentId, req.getPaymentId()));
        List<PaymentQuestionListRsp> res = new ArrayList<>();
        if (ObjectUtil.isEmpty(answers)) {
            for (PaymentQuestionnaire question : questionnaireCache.values()) {
                PaymentQuestionListRsp rsp = converter.join(question, null);
                res.add(rsp);
            }
            return res;
        }
        for (PaymentQuestionnaireAnswer answer : answers) {
            res.add(join(answer));
        }
        return res;
    }

    public PaymentQuestionListRsp join(PaymentQuestionnaireAnswer answer) {
        PaymentQuestionnaire question = questionnaireCache.get(answer.getQuestionId());
        PaymentQuestionListRsp rsp = converter.join(question, answer);
        return rsp;
    }

    public void modify(PaymentQuestionnaireModifyReq req) {
        paymentModifyAuthPort.checkModify(req.getPaymentId());
        PaymentBaseInfo baseInfo = paymentBaseInfoMapper.selectById(req.getPaymentId());
        saveCheck(baseInfo);
        for (PaymentQuestionModifyReq answer : req.getReqs()) {
            if (null == answer.getQuestionAnswer()) {
                throw new MithrasException("请完善问卷调查后再提交审批");
            }
        }
        saveOrUpdateBatch(converter.modifyReqToEntities(req.getReqs()));
        recordStatus(req.getPaymentId());
    }
}
