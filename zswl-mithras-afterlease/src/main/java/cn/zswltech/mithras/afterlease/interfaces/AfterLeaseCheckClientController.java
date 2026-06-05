package cn.zswltech.mithras.afterlease.interfaces;

import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckClientApplicationService;
import cn.zswltech.mithras.api.afterlease.AfterLeaseCheckClientApi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SelectRSP;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckClientDeptInfoRSP;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckClientInfoRSP;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckClientListGroupRSP;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckClientListREQ;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckClientListRSP;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckClientModifyReq;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckClientSaveREQ;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckClientSelectREQ;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckClientSelectRSP;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckLedgerListREQ;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckLedgerListRSP;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckReportTypeChangeREQ;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckReportVersionRSP;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
public class AfterLeaseCheckClientController implements AfterLeaseCheckClientApi {

    @Resource
    private AfterLeaseCheckClientApplicationService afterLeaseCheckClientApplicationService;

    @Override
    public R<AfterLeaseCheckClientInfoRSP> getInfoById(@Valid SinglePkREQ req) {
        return afterLeaseCheckClientApplicationService.getInfoById(req);
    }

    @Override
    public R<List<AfterLeaseCheckClientSelectRSP>> queryClient(@Valid AfterLeaseCheckClientSelectREQ req) {
        return afterLeaseCheckClientApplicationService.queryClient(req);
    }

    @Override
    public R<Void> submitApproval(@Valid SinglePkREQ req) {
        return afterLeaseCheckClientApplicationService.submitApproval(req);
    }

    @Override
    public R<AfterLeaseCheckReportVersionRSP> changeReportType(@Valid AfterLeaseCheckReportTypeChangeREQ req) {
        return afterLeaseCheckClientApplicationService.changeReportType(req);
    }

    @Override
    public R<List<AfterLeaseCheckClientDeptInfoRSP>> listAfterLeaseCanCheckGroupByDept(SinglePkREQ req) {
        return afterLeaseCheckClientApplicationService.listAfterLeaseCanCheckGroupByDept(req);
    }

    @Override
    public R<List<AfterLeaseCheckClientListRSP>> list(@Valid AfterLeaseCheckClientListREQ req) {
        return afterLeaseCheckClientApplicationService.list(req);
    }

    @Override
    public R<Void> save(@Valid AfterLeaseCheckClientSaveREQ req) {
        return afterLeaseCheckClientApplicationService.save(req);
    }

    @Override
    public R<Void> remove(@Valid SinglePkREQ singlePkREQ) {
        return afterLeaseCheckClientApplicationService.remove(singlePkREQ);
    }

    @Override
    public R<List<AfterLeaseCheckClientListRSP>> listNotQuarterPlanProject(@Valid SinglePkREQ req) {
        return afterLeaseCheckClientApplicationService.listNotQuarterPlanProject(req);
    }

    @Override
    public R<List<AfterLeaseCheckClientListGroupRSP>> listQuarterPlanProject(@Valid SinglePkREQ req) {
        return afterLeaseCheckClientApplicationService.listQuarterPlanProject(req);
    }

    @Override
    public R<Void> modifyPlanClient(@Valid AfterLeaseCheckClientModifyReq req) {
        return afterLeaseCheckClientApplicationService.modifyPlanClient(req);
    }

    @Override
    public R<List<SelectRSP>> listRiskManager() {
        return afterLeaseCheckClientApplicationService.listRiskManager();
    }

    @Override
    public R<PageR<AfterLeaseCheckLedgerListRSP>> queryCheckPlanLedgerList(@Valid AfterLeaseCheckLedgerListREQ req) {
        return afterLeaseCheckClientApplicationService.queryCheckPlanLedgerList(req);
    }
}
