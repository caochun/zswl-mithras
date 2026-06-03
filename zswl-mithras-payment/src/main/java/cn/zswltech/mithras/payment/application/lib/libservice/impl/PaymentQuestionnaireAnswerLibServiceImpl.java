package cn.zswltech.mithras.payment.application.lib.libservice.impl;

import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.lib.PaymentQuestionnaireAnswerLibMapper;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentQuestionnaireAnswerLib;
import cn.zswltech.mithras.payment.application.lib.libservice.PaymentQuestionnaireAnswerLibService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2022/8/20 15:59
 */
@Service
public class PaymentQuestionnaireAnswerLibServiceImpl
        extends ServiceImpl<PaymentQuestionnaireAnswerLibMapper, PaymentQuestionnaireAnswerLib>
        implements PaymentQuestionnaireAnswerLibService {
}
