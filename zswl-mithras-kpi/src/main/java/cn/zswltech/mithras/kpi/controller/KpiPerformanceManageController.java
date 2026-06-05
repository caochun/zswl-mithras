package cn.zswltech.mithras.kpi.controller;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.kpi.KpiPerformanceManageApi;
import cn.zswltech.mithras.kpi.application.KpiPerformanceManageApplicationService;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import cn.zswltech.mithras.dto.kpi.*;

@RestController
public class KpiPerformanceManageController implements KpiPerformanceManageApi {
    @Resource
    private KpiPerformanceManageApplicationService kpiPerformanceManageApplicationService;

    @Override
    public R<PageR<KpiPerformanceManageMainListRSP>> mainList(KpiPerformanceManageMainListREQ req) {
        return kpiPerformanceManageApplicationService.mainList(req);
    }

    @Override
    public R<KpiPerformanceManageMainDetailRSP> mainDetail(KpiPerformanceManageMainDetailREQ req) {
        return kpiPerformanceManageApplicationService.mainDetail(req);
    }

    @Override
    public R<Void> add(KpiPerformanceManageAddREQ req) {
        return kpiPerformanceManageApplicationService.add(req);
    }

    @Override
    public void export(KpiPerformanceManageExportREQ req) {
        kpiPerformanceManageApplicationService.export(req);
    }

    @Override
    public R<KpiPerformanceManageListRSP> list(KpiPerformanceManageListREQ req) {
        return kpiPerformanceManageApplicationService.list(req);
    }

    @Override
    public R<Void> importKpiPerformance(KpiPerformanceManageImportREQ req) {
        return kpiPerformanceManageApplicationService.importKpiPerformance(req);
    }

    @Override
    public R<Void> modifyStatus(KpiPerformanceManageModifyREQ req) {
        return kpiPerformanceManageApplicationService.modifyStatus(req);
    }

}
