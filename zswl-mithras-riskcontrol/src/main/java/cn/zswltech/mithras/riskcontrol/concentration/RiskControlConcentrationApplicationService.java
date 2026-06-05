package cn.zswltech.mithras.riskcontrol.concentration;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.riskcontrol.FRIRsp;
import cn.zswltech.mithras.dto.riskcontrol.RiskControlConcentrationAllRelateRSP;
import cn.zswltech.mithras.dto.riskcontrol.RiskControlConcentrationClientListREQ;
import cn.zswltech.mithras.dto.riskcontrol.RiskControlConcentrationClientListRSP;
import cn.zswltech.mithras.dto.riskcontrol.RiskControlConcentrationGroupListREQ;
import cn.zswltech.mithras.dto.riskcontrol.RiskControlConcentrationGroupListRSP;
import cn.zswltech.mithras.dto.riskcontrol.RiskControlConcentrationRelateListREQ;

public interface RiskControlConcentrationApplicationService {

    PageR<RiskControlConcentrationClientListRSP> listClient(RiskControlConcentrationClientListREQ req);

    PageR<RiskControlConcentrationGroupListRSP> listGroup(RiskControlConcentrationGroupListREQ req);

    PageR<RiskControlConcentrationClientListRSP> listRelate(RiskControlConcentrationRelateListREQ req);

    RiskControlConcentrationAllRelateRSP listAllRelate(RiskControlConcentrationRelateListREQ req);

    FRIRsp fri();
}
