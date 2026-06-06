package cn.zswltech.mithras.kpi.controller;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.kpi.KpiParameterBaseApi;
import cn.zswltech.mithras.dto.kpi.KpiParameterBaseAddREQ;
import cn.zswltech.mithras.dto.kpi.KpiParameterBaseCommonREQ;
import cn.zswltech.mithras.dto.kpi.KpiParameterBaseCopyREQ;
import cn.zswltech.mithras.dto.kpi.KpiParameterBaseListREQ;
import cn.zswltech.mithras.dto.kpi.KpiParameterBaseListRSP;
import cn.zswltech.mithras.kpi.application.KpiParameterBaseApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class KpiParameterBaseController implements KpiParameterBaseApi {

    @Resource
    private KpiParameterBaseApplicationService kpiParameterBaseApplicationService;

    @Override
    public R<Long> add(KpiParameterBaseAddREQ req) {
        return kpiParameterBaseApplicationService.add(req);
    }

    @Override
    public R<Void> effect(KpiParameterBaseCommonREQ req) {
        return kpiParameterBaseApplicationService.effect(req);
    }

    @Override
    public R<Void> close(KpiParameterBaseCommonREQ req) {
        return kpiParameterBaseApplicationService.close(req);
    }

    @Override
    public R<PageR<KpiParameterBaseListRSP>> list(KpiParameterBaseListREQ req) {
        return kpiParameterBaseApplicationService.list(req);
    }

    @Override
    public R<Void> remove(KpiParameterBaseCommonREQ req) {
        return kpiParameterBaseApplicationService.remove(req);
    }

    @Override
    public R<Long> copy(KpiParameterBaseCopyREQ req) {
        return kpiParameterBaseApplicationService.copy(req);
    }
}
