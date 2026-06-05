package cn.zswltech.mithras.riskcontrol.controller;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.riskcontrol.RiskControlOpinionMonitorApi;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.projestablish.baseinfo.ClientIdREQ;
import cn.zswltech.mithras.dto.riskcontrol.opinion.*;
import javax.validation.Valid;
import java.util.*;
import cn.zswltech.mithras.riskcontrol.application.RiskControlOpinionMonitorApplicationService;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;

@RestController
public class RiskControlOpinionMonitorController implements RiskControlOpinionMonitorApi {
    @Resource
    private RiskControlOpinionMonitorApplicationService riskControlOpinionMonitorApplicationService;

    @Override
    public R<Void> handleOpinion(RiskControlOpinionHandleREQ req) {
        return riskControlOpinionMonitorApplicationService.handleOpinion(req);
    }

    @Override
    public R<Void> fixAdvisement(RiskControlOpinionHandleREQ req) {
        return riskControlOpinionMonitorApplicationService.fixAdvisement(req);
    }

    @Override
    public R<PageR<RiskControlOpinionMonitorListRSP>> list(RiskControlOpinionMonitorListREQ req) {
        return riskControlOpinionMonitorApplicationService.list(req);
    }

    @Override
    public R<Void> notice(@Valid RiskControlOpinionNoticeReq req) {
        return riskControlOpinionMonitorApplicationService.notice(req);
    }

    @Override
    public R<Void> send(RiskControlOpinionSendReq req) {
        return riskControlOpinionMonitorApplicationService.send(req);
    }

    @Override
    public R<PageR<UnresolvedClientOpinionRSP>> unresolvedList(UnresolvedClientOpinionREQ req) {
        return riskControlOpinionMonitorApplicationService.unresolvedList(req);
    }

    @Override
    public R<RiskControlOpinionMonitorListRSP> detail(SinglePkREQ req) {
        return riskControlOpinionMonitorApplicationService.detail(req);
    }

    @Override
    public R<String> view(SinglePkREQ req) {
        return riskControlOpinionMonitorApplicationService.view(req);
    }

    @Override
    public R<Integer> countByClientId(@Valid ClientIdREQ req) {
        return riskControlOpinionMonitorApplicationService.countByClientId(req);
    }

    @Override
    public R<Void> close(OpinionMonitorCloseREQ req) {
        return riskControlOpinionMonitorApplicationService.close(req);
    }

    @Override
    public R<RiskControlOpinionManualRSP> confirm(ClientIdREQ req) {
        return riskControlOpinionMonitorApplicationService.confirm(req);
    }

    @Override
    public R<Void> save(RiskControlOpinionManualDetailREQ req) {
        return riskControlOpinionMonitorApplicationService.save(req);
    }

    @Override
    public R<Void> submit(RiskControlOpinionManualDetailREQ req) {
        return riskControlOpinionMonitorApplicationService.submit(req);
    }

    @Override
    public R<Void> delete(SinglePkREQ req) {
        return riskControlOpinionMonitorApplicationService.delete(req);
    }

}
