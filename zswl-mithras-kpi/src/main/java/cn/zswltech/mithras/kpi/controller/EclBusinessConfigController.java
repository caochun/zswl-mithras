package cn.zswltech.mithras.kpi.controller;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.kpi.EclBusinessConfigApi;
import cn.zswltech.mithras.kpi.application.EclBusinessConfigApplicationService;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import cn.zswltech.mithras.dto.kpi.*;

@RestController
public class EclBusinessConfigController implements EclBusinessConfigApi {
    @Resource
    private EclBusinessConfigApplicationService eclBusinessConfigApplicationService;

    @Override
    public R<Void> modify(EclBusinessConfigModifyREQ req) {
        return eclBusinessConfigApplicationService.modify(req);
    }

    @Override
    public R<PageR<EclBusinessConfigListRSP>> list(EclBusinessConfigListREQ req) {
        return eclBusinessConfigApplicationService.list(req);
    }

    @Override
    public R<EclBusinessConfigDetailRSP> detail(EclBusinessConfigDetailREQ req) {
        return eclBusinessConfigApplicationService.detail(req);
    }

    @Override
    public R<PageR<EclBusinessConfigListRSP>> versionList(EclBusinessConfigListREQ req) {
        return eclBusinessConfigApplicationService.versionList(req);
    }

    @Override
    public R<EclBusinessConfigDetailRSP> versionDetail(EclBusinessConfigDetailREQ req) {
        return eclBusinessConfigApplicationService.versionDetail(req);
    }

}
