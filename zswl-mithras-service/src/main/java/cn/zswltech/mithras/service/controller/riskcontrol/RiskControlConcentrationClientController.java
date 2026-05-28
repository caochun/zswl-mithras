package cn.zswltech.mithras.service.controller.riskcontrol;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.riskcontrol.RiskControlConcentrationClientApi;
import cn.zswltech.mithras.dto.riskcontrol.*;
import cn.zswltech.mithras.service.service.riskcontrol.RiskControlConcentrationClientService;
import cn.zswltech.mithras.service.service.riskcontrol.RiskControlConcentrationGroupService;
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
    private RiskControlConcentrationClientService riskControlConcentrationClientService;

    @Resource
    private RiskControlConcentrationGroupService riskControlConcentrationGroupService;

    @Override
    public R<PageR<RiskControlConcentrationClientListRSP>> listClient(RiskControlConcentrationClientListREQ req) {
        return R.ok(riskControlConcentrationClientService.listClient(req));
    }

    @Override
    public R<PageR<RiskControlConcentrationGroupListRSP>> listGroup(RiskControlConcentrationGroupListREQ req) {
        return R.ok(riskControlConcentrationGroupService.list(req));
    }

    @Override
    public R<PageR<RiskControlConcentrationClientListRSP>> listRelate(RiskControlConcentrationRelateListREQ req) {
        return R.ok(riskControlConcentrationClientService.listRelate(req));
    }

    @Override
    public R<RiskControlConcentrationAllRelateRSP> listAllRelate(RiskControlConcentrationRelateListREQ req) {
        return R.ok(riskControlConcentrationClientService.listAllRelate(req));
    }

    @Override
    public R<FRIRsp> fri() {
        return R.ok(riskControlConcentrationClientService.fri());
    }
}