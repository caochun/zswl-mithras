package cn.zswltech.mithras.kpi.controller;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.kpi.KpiProjectDistributionBaseInfoApi;
import cn.zswltech.mithras.kpi.application.KpiProjectDistributionBaseInfoApplicationService;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import cn.zswltech.mithras.dto.kpi.*;

@RestController
public class KpiProjectDistributionBaseInfoController implements KpiProjectDistributionBaseInfoApi {
    @Resource
    private KpiProjectDistributionBaseInfoApplicationService kpiProjectDistributionBaseInfoApplicationService;

    @Override
    public R<KpiProjectDistributionBaseInfoRSP> detail(KpiProjectDistributionBaseInfoREQ req) {
        return kpiProjectDistributionBaseInfoApplicationService.detail(req);
    }

    @Override
    public R<Void> modify(KpiProjectDistributionBaseInfoModifyREQ req) {
        return kpiProjectDistributionBaseInfoApplicationService.modify(req);
    }

    @Override
    public R<KpiProjectDistributionGetProcessRSP> getProcess(KpiProjectDistributionBaseInfoREQ req) {
        return kpiProjectDistributionBaseInfoApplicationService.getProcess(req);
    }

}
