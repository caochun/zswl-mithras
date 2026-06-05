package cn.zswltech.mithras.kpi.controller;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.kpi.KpiProjectDistributionWeightApi;
import cn.zswltech.mithras.kpi.application.KpiProjectDistributionWeightApplicationService;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import cn.zswltech.mithras.dto.kpi.*;

@RestController
public class KpiProjectDistributionWeightController implements KpiProjectDistributionWeightApi {
    @Resource
    private KpiProjectDistributionWeightApplicationService kpiProjectDistributionWeightApplicationService;

    @Override
    public R<KpiProjectDistributionWeightRSP> detail(KpiProjectDistributionWeightREQ req) {
        return kpiProjectDistributionWeightApplicationService.detail(req);
    }

    @Override
    public R<Void> save(KpiProjectDistributionWeightSaveREQ req) {
        return kpiProjectDistributionWeightApplicationService.save(req);
    }

    @Override
    public R<Void> test() {
        return kpiProjectDistributionWeightApplicationService.test();
    }

}
