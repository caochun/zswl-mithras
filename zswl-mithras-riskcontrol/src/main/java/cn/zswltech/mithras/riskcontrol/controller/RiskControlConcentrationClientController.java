package cn.zswltech.mithras.riskcontrol.controller;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.riskcontrol.RiskControlConcentrationClientApi;
import cn.zswltech.mithras.dto.riskcontrol.*;
import cn.zswltech.mithras.riskcontrol.concentration.RiskControlConcentrationApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author zhaozhengkang
 * @description 客户集中度
 * @date 2023-02-27
 */
@RestController
public class RiskControlConcentrationClientController implements RiskControlConcentrationClientApi {

    @Resource
    private RiskControlConcentrationApplicationService riskControlConcentrationService;

    @Override
    public R<PageR<RiskControlConcentrationClientListRSP>> listClient(RiskControlConcentrationClientListREQ req) {
        return R.ok(riskControlConcentrationService.listClient(req));
    }

    @Override
    public R<PageR<RiskControlConcentrationGroupListRSP>> listGroup(RiskControlConcentrationGroupListREQ req) {
        return R.ok(riskControlConcentrationService.listGroup(req));
    }

    @Override
    public R<PageR<RiskControlConcentrationClientListRSP>> listRelate(RiskControlConcentrationRelateListREQ req) {
        return R.ok(riskControlConcentrationService.listRelate(req));
    }

    @Override
    public R<RiskControlConcentrationAllRelateRSP> listAllRelate(RiskControlConcentrationRelateListREQ req) {
        return R.ok(riskControlConcentrationService.listAllRelate(req));
    }

    @Override
    public R<FRIRsp> fri() {
        return R.ok(riskControlConcentrationService.fri());
    }
}
