package cn.zswltech.mithras.kpi.controller;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.kpi.KpiProvisionBaseInfoApi;
import cn.zswltech.mithras.kpi.application.KpiProvisionBaseInfoApplicationService;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import cn.zswltech.mithras.dto.kpi.*;

@RestController
public class KpiProvisionBaseInfoController implements KpiProvisionBaseInfoApi {
    @Resource
    private KpiProvisionBaseInfoApplicationService kpiProvisionBaseInfoApplicationService;

    @Override
    public R<KpiProvisionBaseInfoAddRSP> add(KpiProvisionBaseInfoAddREQ req) {
        return kpiProvisionBaseInfoApplicationService.add(req);
    }

    @Override
    public R<Void> refresh(KpiProvisionBaseInfoDetailREQ req) {
        return kpiProvisionBaseInfoApplicationService.refresh(req);
    }

    @Override
    public R<Void> effect(KpiProvisionBaseInfoDetailREQ req) {
        return kpiProvisionBaseInfoApplicationService.effect(req);
    }

    @Override
    public R<Void> modify(KpiProvisionBaseInfoModifyREQ req) {
        return kpiProvisionBaseInfoApplicationService.modify(req);
    }

    @Override
    public R<PageR<KpiProvisionBaseInfoListRSP>> list(KpiProvisionBaseInfoListREQ req) {
        return kpiProvisionBaseInfoApplicationService.list(req);
    }

    @Override
    public R<KpiProvisionBaseInfoDetailRSP> detail(KpiProvisionBaseInfoDetailREQ req) {
        return kpiProvisionBaseInfoApplicationService.detail(req);
    }

    @Override
    public R<Void> detailExport(KpiProvisionBaseInfoDetailExportREQ req) {
        return kpiProvisionBaseInfoApplicationService.detailExport(req);
    }

}
