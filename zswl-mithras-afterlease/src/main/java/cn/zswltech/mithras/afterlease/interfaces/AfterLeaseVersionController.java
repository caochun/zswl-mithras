package cn.zswltech.mithras.afterlease.interfaces;

import cn.zswltech.mithras.afterlease.application.AfterLeaseVersionApplicationService;
import cn.zswltech.mithras.api.afterlease.AfterLeaseVersionApi;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseAdjustDetailREQ;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCancelREQ;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
public class AfterLeaseVersionController implements AfterLeaseVersionApi {

    @Resource
    private AfterLeaseVersionApplicationService afterLeaseVersionApplicationService;

    @Override
    public R<Void> effect(@Valid AfterLeaseAdjustDetailREQ req) {
        return afterLeaseVersionApplicationService.effect(req);
    }

    @Override
    public R<Void> adjustCancel(@Valid AfterLeaseCancelREQ req) {
        return afterLeaseVersionApplicationService.adjustCancel(req);
    }
}
