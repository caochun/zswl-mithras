package cn.zswltech.mithras.riskcontrol.controller;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.riskcontrol.RiskWarnMonitorApi;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.riskcontrol.opinion.*;
import javax.validation.Valid;
import java.util.List;
import cn.zswltech.mithras.riskcontrol.application.RiskWarnMonitorApplicationService;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

@RestController
public class RiskWarnMonitorController implements RiskWarnMonitorApi {
    @Resource
    private RiskWarnMonitorApplicationService riskWarnMonitorApplicationService;

    @Override
    public R<PageR<RiskWarnMonitorOpinionListRSP>> opinionList(@Valid RiskWarnMonitorOpinionListREQ req) {
        return riskWarnMonitorApplicationService.opinionList(req);
    }

    @Override
    public R<PageR<RiskWarnMonitorWarnListRSP>> warnList(@Valid RiskWarnMonitorWarnListREQ req) {
        return riskWarnMonitorApplicationService.warnList(req);
    }

    @Override
    public R<RiskWarnMonitorWarnDetailRSP> warnDetail(@Valid SinglePkREQ req) {
        return riskWarnMonitorApplicationService.warnDetail(req);
    }

    @Override
    public R<RiskWarnMonitorStatisticsRSP> statistics() {
        return riskWarnMonitorApplicationService.statistics();
    }

    @Override
    public R<List<RiskWarnMonitorQuantityChangeRSP>> quantityChange() {
        return riskWarnMonitorApplicationService.quantityChange();
    }

    @Override
    public R<List<RiskWarnMonitorTypeChangeRSP>> typeChange() {
        return riskWarnMonitorApplicationService.typeChange();
    }

    @Override
    public R<Void> warnModify(@Valid RiskControlWarnModifyREQ req) {
        return riskWarnMonitorApplicationService.warnModify(req);
    }

}
