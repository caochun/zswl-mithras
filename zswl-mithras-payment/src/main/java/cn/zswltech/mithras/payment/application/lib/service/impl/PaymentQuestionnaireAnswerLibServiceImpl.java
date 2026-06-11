package cn.zswltech.mithras.payment.application.lib.service.impl;

import cn.zswltech.mithras.payment.mapper.lib.PaymentQuestionnaireAnswerLibMapper;
import cn.zswltech.mithras.payment.mapper.model.PaymentQuestionnaireAnswerLib;
import cn.zswltech.mithras.payment.application.lib.service.PaymentQuestionnaireAnswerLibService;
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
