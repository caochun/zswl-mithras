package cn.zswltech.mithras.afterlease.controller;

import cn.zswltech.mithras.afterlease.application.AfterLeaseAdjustInfoApplicationService;
import cn.zswltech.mithras.api.afterlease.AfterLeaseAdjustInfoApi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseAdjustDetailREQ;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseAdjustDetailRSP;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseAdjustInfoAddREQ;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseAdjustInfoAddRSP;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseAdjustInfoListREQ;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseAdjustInfoListRSP;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseAdjustInfoModifyREQ;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
public class AfterLeaseAdjustInfoController implements AfterLeaseAdjustInfoApi {

    @Resource
    private AfterLeaseAdjustInfoApplicationService afterLeaseAdjustInfoApplicationService;

    @Override
    public R<AfterLeaseAdjustInfoAddRSP> add(@Valid AfterLeaseAdjustInfoAddREQ req) {
        return afterLeaseAdjustInfoApplicationService.add(req);
    }

    @Override
    public R<Void> modify(@Valid AfterLeaseAdjustInfoModifyREQ req) {
        return afterLeaseAdjustInfoApplicationService.modify(req);
    }

    @Override
    public R<PageR<AfterLeaseAdjustInfoListRSP>> list(@Valid AfterLeaseAdjustInfoListREQ req) {
        return afterLeaseAdjustInfoApplicationService.list(req);
    }

    @Override
    public R<AfterLeaseAdjustDetailRSP> detail(@Valid AfterLeaseAdjustDetailREQ req) {
        return afterLeaseAdjustInfoApplicationService.detail(req);
    }
}
