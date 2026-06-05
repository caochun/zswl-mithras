package cn.zswltech.mithras.kpi.controller;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.kpi.KpiProjectDistributionDeptWeightApi;
import cn.zswltech.mithras.kpi.application.KpiProjectDistributionDeptWeightApplicationService;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import cn.zswltech.mithras.dto.kpi.*;
import java.util.List;

@RestController
public class KpiProjectDistributionDeptWeightController implements KpiProjectDistributionDeptWeightApi {
    @Resource
    private KpiProjectDistributionDeptWeightApplicationService kpiProjectDistributionDeptWeightApplicationService;

    @Override
    public R<Void> saveDept(KpiProjectDistributionDeptWeightSaveREQ req) {
        return kpiProjectDistributionDeptWeightApplicationService.saveDept(req);
    }

    @Override
    public R<List<KpiProjectDistributionDeptWeightInfo>> prev(KpiProjectDistributionPrevREQ req) {
        return kpiProjectDistributionDeptWeightApplicationService.prev(req);
    }

    @Override
    public R<List<KpiProjectDistributionDeptLaunchWeightInfo>> launchPrev(KpiProjectDistributionPrevREQ req) {
        return kpiProjectDistributionDeptWeightApplicationService.launchPrev(req);
    }

}
