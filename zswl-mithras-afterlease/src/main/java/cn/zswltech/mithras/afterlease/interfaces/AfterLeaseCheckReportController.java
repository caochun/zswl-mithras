package cn.zswltech.mithras.afterlease.interfaces;

import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.afterlease.application.AfterLeaseCheckReportApplicationService;
import cn.zswltech.mithras.api.afterlease.AfterLeaseCheckReportApi;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.MultiplePkREQ;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckReportBaseREQ;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckReportBaseRSP;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckReportCSREQ;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckReportCSRSP;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckReportDownloadREQ;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckReportFileREQ;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckReportFinanceREQ;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckReportFinanceRSP;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckReportFinanceSaveREQ;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckReportNonPublicExtraREQ;
import cn.zswltech.mithras.dto.afterlease.AfterLeaseCheckReportNonPublicExtraRSP;
import cn.zswltech.mithras.dto.projreview.report.ProjReviewReportListRSP;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
public class AfterLeaseCheckReportController implements AfterLeaseCheckReportApi {

    @Resource
    private AfterLeaseCheckReportApplicationService afterLeaseCheckReportApplicationService;

    @Override
    public R<Long> saveReportBase(@Valid AfterLeaseCheckReportBaseREQ req) {
        return afterLeaseCheckReportApplicationService.saveReportBase(req);
    }

    @Override
    public R<AfterLeaseCheckReportBaseRSP> getReportBase(@Valid SinglePkREQ req) {
        return afterLeaseCheckReportApplicationService.getReportBase(req);
    }

    @Override
    public R<Void> saveReportContent(@Valid AfterLeaseCheckReportCSREQ req) {
        return afterLeaseCheckReportApplicationService.saveReportContent(req);
    }

    @Override
    public R<AfterLeaseCheckReportCSRSP> getReportContent(@Valid SinglePkREQ req) {
        return afterLeaseCheckReportApplicationService.getReportContent(req);
    }

    @Override
    public R<Void> saveReportSummary(@Valid AfterLeaseCheckReportCSREQ req) {
        return afterLeaseCheckReportApplicationService.saveReportSummary(req);
    }

    @Override
    public R<AfterLeaseCheckReportCSRSP> getReportSummary(@Valid SinglePkREQ req) {
        return afterLeaseCheckReportApplicationService.getReportSummary(req);
    }

    @Override
    public R<List<AfterLeaseCheckReportNonPublicExtraRSP>> getNonPublicExtra(@Valid SinglePkREQ req) {
        return afterLeaseCheckReportApplicationService.getNonPublicExtra(req);
    }

    @Override
    public R<Void> saveNonPublicExtra(@Valid AfterLeaseCheckReportNonPublicExtraREQ req) {
        return afterLeaseCheckReportApplicationService.saveNonPublicExtra(req);
    }

    @Override
    public R<Void> uploadNonPublicReportFile(@Valid AfterLeaseCheckReportFileREQ req) {
        return afterLeaseCheckReportApplicationService.uploadNonPublicReportFile(req);
    }

    @Override
    public R<List<Pair<String, List<ProjReviewReportListRSP>>>> listNonPublicReportFile(@Valid SinglePkREQ singlePkREQ) {
        return afterLeaseCheckReportApplicationService.listNonPublicReportFile(singlePkREQ);
    }

    @Override
    public void downloadSingleReport(@Valid SinglePkREQ req) {
        afterLeaseCheckReportApplicationService.downloadSingleReport(req);
    }

    @Override
    public void downloadBatchReport(@Valid MultiplePkREQ req) {
        afterLeaseCheckReportApplicationService.downloadBatchReport(req);
    }

    @Override
    public void downloadDeptReport(@Valid AfterLeaseCheckReportDownloadREQ req) {
        afterLeaseCheckReportApplicationService.downloadDeptReport(req);
    }

    @Override
    public R<AfterLeaseCheckReportFinanceRSP> getReportFinanceSnapshot(@Valid AfterLeaseCheckReportFinanceREQ req) {
        return afterLeaseCheckReportApplicationService.getReportFinanceSnapshot(req);
    }

    @Override
    public R<Void> saveReportFinanceSnapshot(@Valid AfterLeaseCheckReportFinanceSaveREQ req) {
        return afterLeaseCheckReportApplicationService.saveReportFinanceSnapshot(req);
    }

    @Override
    public R<List<Pair<String, List<ProjReviewReportListRSP>>>> listFinanceDataFile(@Valid SinglePkREQ req) {
        return afterLeaseCheckReportApplicationService.listFinanceDataFile(req);
    }
}
