package cn.zswltech.mithras.service.service.riskcontrol;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.riskcontrol.FRIRsp;
import cn.zswltech.mithras.dto.riskcontrol.RiskControlConcentrationAllRelateRSP;
import cn.zswltech.mithras.dto.riskcontrol.RiskControlConcentrationClientListREQ;
import cn.zswltech.mithras.dto.riskcontrol.RiskControlConcentrationClientListRSP;
import cn.zswltech.mithras.dto.riskcontrol.RiskControlConcentrationGroupListREQ;
import cn.zswltech.mithras.dto.riskcontrol.RiskControlConcentrationGroupListRSP;
import cn.zswltech.mithras.dto.riskcontrol.RiskControlConcentrationRelateListREQ;
import cn.zswltech.mithras.riskcontrol.concentration.RiskControlConcentrationApplicationService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class RiskControlConcentrationApplicationServiceImpl implements RiskControlConcentrationApplicationService {

    @Resource
    private RiskControlConcentrationClientService riskControlConcentrationClientService;
    @Resource
    private RiskControlConcentrationGroupService riskControlConcentrationGroupService;

    @Override
    public PageR<RiskControlConcentrationClientListRSP> listClient(RiskControlConcentrationClientListREQ req) {
        return riskControlConcentrationClientService.listClient(req);
    }

    @Override
    public PageR<RiskControlConcentrationGroupListRSP> listGroup(RiskControlConcentrationGroupListREQ req) {
        return riskControlConcentrationGroupService.list(req);
    }

    @Override
    public PageR<RiskControlConcentrationClientListRSP> listRelate(RiskControlConcentrationRelateListREQ req) {
        return riskControlConcentrationClientService.listRelate(req);
    }

    @Override
    public RiskControlConcentrationAllRelateRSP listAllRelate(RiskControlConcentrationRelateListREQ req) {
        return riskControlConcentrationClientService.listAllRelate(req);
    }

    @Override
    public FRIRsp fri() {
        return riskControlConcentrationClientService.fri();
    }
}
