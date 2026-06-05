package cn.zswltech.mithras.afterlease.interfaces;

import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckPlanBaseApplicationService;
import cn.zswltech.mithras.api.afterlease.AfterLeaseCheckPlanBaseApi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseAssetStrategyModifyREQ;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseAssetStrategyREQ;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseAssetStrategyRSP;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseAuditFlowPreREQ;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseAuditFlowPreRSP;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckPlanBaseAddREQ;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckPlanBaseModifyREQ;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckPlanCommonlyDetailREQ;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckPlanCommonlyDetailRSP;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckPlanDetailRSP;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckPlanListREQ;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckPlanListRSP;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckPlanPublishREQ;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckSummaryReportRSP;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseClientPlanRSP;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
public class AfterLeaseCheckPlanBaseController implements AfterLeaseCheckPlanBaseApi {

    @Resource
    private AfterLeaseCheckPlanBaseApplicationService afterLeaseCheckPlanBaseApplicationService;

    @Override
    public R<PageR<AfterLeaseCheckPlanListRSP>> listCheckPlanWithPage(AfterLeaseCheckPlanListREQ req) {
        return afterLeaseCheckPlanBaseApplicationService.listCheckPlanWithPage(req);
    }

    @Override
    public R<Void> close(@Valid SinglePkREQ req) {
        return afterLeaseCheckPlanBaseApplicationService.close(req);
    }

    @Override
    public R<Long> add(@Valid AfterLeaseCheckPlanBaseAddREQ req) {
        return afterLeaseCheckPlanBaseApplicationService.add(req);
    }

    @Override
    public R<Long> modify(@Valid AfterLeaseCheckPlanBaseModifyREQ req) {
        return afterLeaseCheckPlanBaseApplicationService.modify(req);
    }

    @Override
    public R<Void> cancel(@Valid SinglePkREQ req) {
        return afterLeaseCheckPlanBaseApplicationService.cancel(req);
    }

    @Override
    public R<Void> publish(@Valid AfterLeaseCheckPlanPublishREQ req) {
        return afterLeaseCheckPlanBaseApplicationService.publish(req);
    }

    @Override
    public R<AfterLeaseCheckPlanDetailRSP> detail(@Valid SinglePkREQ req) {
        return afterLeaseCheckPlanBaseApplicationService.detail(req);
    }

    @Override
    public R<AfterLeaseCheckPlanCommonlyDetailRSP> commonlyDetail(@Valid AfterLeaseCheckPlanCommonlyDetailREQ req) {
        return afterLeaseCheckPlanBaseApplicationService.commonlyDetail(req);
    }

    @Override
    public R<List<AfterLeaseCheckSummaryReportRSP>> listSummaryReport(@Valid SinglePkREQ req) {
        return afterLeaseCheckPlanBaseApplicationService.listSummaryReport(req);
    }

    @Override
    public R<Void> finish(@Valid SinglePkREQ req) {
        return afterLeaseCheckPlanBaseApplicationService.finish(req);
    }

    @Override
    public R<Void> cuiban(@Valid SinglePkREQ req) {
        return afterLeaseCheckPlanBaseApplicationService.cuiban(req);
    }

    @Override
    public R<AfterLeaseAuditFlowPreRSP> afterLeaseAuditPreSelect(@Valid AfterLeaseAuditFlowPreREQ request) {
        return afterLeaseCheckPlanBaseApplicationService.afterLeaseAuditPreSelect(request);
    }

    @Override
    public R<AfterLeaseClientPlanRSP> afterLeaseClientPlan(@Valid AfterLeaseAuditFlowPreREQ request) {
        return afterLeaseCheckPlanBaseApplicationService.afterLeaseClientPlan(request);
    }

    @Override
    public R<PageR<AfterLeaseAssetStrategyRSP>> afterLeaseAssetStrategys(AfterLeaseAssetStrategyREQ req) {
        return afterLeaseCheckPlanBaseApplicationService.afterLeaseAssetStrategys(req);
    }

    @Override
    public R<Void> afterLeaseAssetStrategyModify(@Valid AfterLeaseAssetStrategyModifyREQ req) {
        return afterLeaseCheckPlanBaseApplicationService.afterLeaseAssetStrategyModify(req);
    }
}
