package cn.zswltech.mithras.kpi.controller;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.kpi.KpiProjGuessExportApi;
import cn.zswltech.mithras.kpi.application.KpiProjGuessExportApplicationService;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import cn.zswltech.mithras.dto.kpi.*;

@RestController
public class KpiProjGuessExportController implements KpiProjGuessExportApi {
    @Resource
    private KpiProjGuessExportApplicationService kpiProjGuessExportApplicationService;

    @Override
    public R<Void> contractList(KpiProjGuessIndexREQ req) {
        return kpiProjGuessExportApplicationService.contractList(req);
    }

    @Override
    public R<Void> timeList(KpiProjGuessIndexREQ req) {
        return kpiProjGuessExportApplicationService.timeList(req);
    }

    @Override
    public R<Void> deptList(KpiProjGuessIndexREQ req) {
        return kpiProjGuessExportApplicationService.deptList(req);
    }

    @Override
    public R<Void> peopleList(KpiProjGuessIndexREQ req) {
        return kpiProjGuessExportApplicationService.peopleList(req);
    }

    @Override
    public R<Void> contractDetail(KpiProjGuessDetailREQ req) {
        return kpiProjGuessExportApplicationService.contractDetail(req);
    }

    @Override
    public R<Void> peopleDetail(KpiProjGuessDetailREQ req) {
        return kpiProjGuessExportApplicationService.peopleDetail(req);
    }

}
