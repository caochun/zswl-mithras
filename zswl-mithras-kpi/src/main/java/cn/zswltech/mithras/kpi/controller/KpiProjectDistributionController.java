package cn.zswltech.mithras.kpi.controller;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.kpi.KpiProjectDistributionApi;
import cn.zswltech.mithras.kpi.application.KpiProjectDistributionApplicationService;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import cn.zswltech.mithras.dto.kpi.*;
import java.util.List;

@RestController
public class KpiProjectDistributionController implements KpiProjectDistributionApi {
    @Resource
    private KpiProjectDistributionApplicationService kpiProjectDistributionApplicationService;

    @Override
    public R<PageR<KpiProjectDistributionListRSP>> pageList(KpiProjectDistributionListREQ req) {
        return kpiProjectDistributionApplicationService.pageList(req);
    }

    @Override
    public R<Void> submit(KpiProjectDistributionSubmitREQ req) {
        return kpiProjectDistributionApplicationService.submit(req);
    }

    @Override
    public R<List<KpiProjectDistributionPrevRSP>> prev(KpiProjectDistributionPrevREQ req) {
        return kpiProjectDistributionApplicationService.prev(req);
    }

    @Override
    public R<List<KpiProjectDistributionHistoryRSP>> history(KpiProjectDistributionHistoryREQ req) {
        return kpiProjectDistributionApplicationService.history(req);
    }

    @Override
    public R<Void> importHistoryData(KpiProjectDistributionImportREQ req) {
        return kpiProjectDistributionApplicationService.importHistoryData(req);
    }

    @Override
    public R<Void> init() {
        return kpiProjectDistributionApplicationService.init();
    }

    @Override
    public void export(KpiProjectDistributionListREQ req) {
        kpiProjectDistributionApplicationService.export(req);
    }

}
