package cn.zswltech.mithras.kpi.controller;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.kpi.KpiProjectDistributionDeptLaunchWeightApi;
import cn.zswltech.mithras.kpi.application.KpiProjectDistributionDeptLaunchWeightApplicationService;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import cn.zswltech.mithras.dto.kpi.*;

@RestController
public class KpiProjectDistributionDeptLaunchWeightController implements KpiProjectDistributionDeptLaunchWeightApi {
    @Resource
    private KpiProjectDistributionDeptLaunchWeightApplicationService kpiProjectDistributionDeptLaunchWeightApplicationService;

    @Override
    public R<Void> add(KpiProjectDistributionDeptLaunchWeightAddREQ req) {
        return kpiProjectDistributionDeptLaunchWeightApplicationService.add(req);
    }

    @Override
    public R<Void> modify(KpiProjectDistributionDeptLaunchWeightModifyREQ req) {
        return kpiProjectDistributionDeptLaunchWeightApplicationService.modify(req);
    }

    @Override
    public R<PageR<KpiProjectDistributionDeptLaunchWeightListRSP>> list(KpiProjectDistributionDeptLaunchWeightListREQ req) {
        return kpiProjectDistributionDeptLaunchWeightApplicationService.list(req);
    }

    @Override
    public R<Void> remove(KpiProjectDistributionDeptLaunchWeightRemoveREQ req) {
        return kpiProjectDistributionDeptLaunchWeightApplicationService.remove(req);
    }

}
