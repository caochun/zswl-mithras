package cn.zswltech.mithras.policy.controller.info;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.policy.PolicyInfoVersionApi;
import cn.zswltech.mithras.dto.policy.PolicyInfoCancelREQ;
import cn.zswltech.mithras.dto.policy.PolicyInfoEffectREQ;
import cn.zswltech.mithras.dto.policy.PolicyInfoVersionDiffREQ;
import cn.zswltech.mithras.dto.version.CommonVersionDiffRSP;
import cn.zswltech.mithras.dto.version.CommonVersionListREQ;
import cn.zswltech.mithras.dto.version.CommonVersionListRSP;
import cn.zswltech.mithras.policy.application.info.PolicyInfoVersionApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
public class PolicyInfoVersionController implements PolicyInfoVersionApi {

    @Resource
    private PolicyInfoVersionApplicationService policyInfoVersionApplicationService;

    @Override
    public R<Void> effect(@Valid PolicyInfoEffectREQ req) {
        return policyInfoVersionApplicationService.effect(req);
    }

    @Override
    public R<Void> cancel(@Valid PolicyInfoCancelREQ req) {
        return policyInfoVersionApplicationService.cancel(req);
    }

    @Override
    public R<PageR<CommonVersionListRSP>> list(@Valid CommonVersionListREQ req) {
        return policyInfoVersionApplicationService.list(req);
    }

    @Override
    public R<CommonVersionDiffRSP> comparePreVersion(@Valid PolicyInfoVersionDiffREQ req) {
        return policyInfoVersionApplicationService.comparePreVersion(req);
    }
}
