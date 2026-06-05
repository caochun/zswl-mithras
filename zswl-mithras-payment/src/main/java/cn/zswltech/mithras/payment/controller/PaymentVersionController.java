package cn.zswltech.mithras.payment.controller;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.payment.PaymentVersionApi;
import cn.zswltech.mithras.api.payment.version.PaymentEffectREQ;
import cn.zswltech.mithras.api.payment.version.PaymentVersionDiffREQ;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListREQ;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import javax.validation.Valid;
import java.util.List;
import cn.zswltech.mithras.payment.application.PaymentVersionApplicationService;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

@RestController
public class PaymentVersionController implements PaymentVersionApi {
    @Resource
    private PaymentVersionApplicationService paymentVersionApplicationService;

    @Override
    public R<Void> effect(PaymentEffectREQ req) {
        return paymentVersionApplicationService.effect(req);
    }

    @Override
    public R<PageR<CommonVersionListRSP>> list(CommonVersionListREQ req) {
        return paymentVersionApplicationService.list(req);
    }

    @Override
    public R<CommonVersionDiffRSP> comparePreVersion(PaymentVersionDiffREQ req) {
        return paymentVersionApplicationService.comparePreVersion(req);
    }

    @Override
    public R<Boolean> checkClientOpinion(PaymentEffectREQ req) {
        return paymentVersionApplicationService.checkClientOpinion(req);
    }

    @Override
    public R<Boolean> checkProjectDistribution(@Valid PaymentEffectREQ req) {
        return paymentVersionApplicationService.checkProjectDistribution(req);
    }

}
