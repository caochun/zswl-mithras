package cn.zswltech.mithras.customer.controller.client;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.external.ExternalPageREQ;
import cn.zswltech.mithras.dto.client.external.environment.EnvironmentPenaltyAddREQ;
import cn.zswltech.mithras.dto.client.external.environment.EnvironmentPenaltyModifyREQ;
import cn.zswltech.mithras.dto.client.external.environment.EnvironmentPenaltyRSP;
import cn.zswltech.mithras.dto.client.external.environment.EnvironmentPenaltyRemoveREQ;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import cn.zswltech.mithras.api.client.EnvironmentPenaltyApi;
import cn.zswltech.mithras.customer.application.client.EnvironmentPenaltyApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class EnvironmentPenaltyController implements EnvironmentPenaltyApi {
    @Resource
    private EnvironmentPenaltyApplicationService environmentPenaltyApplicationService;

    @Override
    public R<Void> add(@RequestBody @Valid EnvironmentPenaltyAddREQ req) {
        return environmentPenaltyApplicationService.add(req);
    }

    @Override
    public R<Void> modify(@RequestBody @Valid EnvironmentPenaltyModifyREQ req) {
        return environmentPenaltyApplicationService.modify(req);
    }

    @Override
    public R<PageR<EnvironmentPenaltyRSP>> list(@RequestBody @Valid ExternalPageREQ req) {
        return environmentPenaltyApplicationService.list(req);
    }

    @Override
    public R<Void> remove(@RequestBody @Valid EnvironmentPenaltyRemoveREQ req) {
        return environmentPenaltyApplicationService.remove(req);
    }
}
