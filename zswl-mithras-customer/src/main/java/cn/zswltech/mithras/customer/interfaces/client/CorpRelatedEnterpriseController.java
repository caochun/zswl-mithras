package cn.zswltech.mithras.customer.interfaces.client;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.relatedenterprise.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import cn.zswltech.mithras.api.client.CorpRelatedEnterpriseApi;
import cn.zswltech.mithras.customer.application.client.api.CorpRelatedEnterpriseApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class CorpRelatedEnterpriseController implements CorpRelatedEnterpriseApi {
    @Resource
    private CorpRelatedEnterpriseApplicationService corpRelatedEnterpriseApplicationService;

    @Override
    public R<Void> add(@RequestBody @Valid CorpRelatedEnterpriseAddREQ req) {
        return corpRelatedEnterpriseApplicationService.add(req);
    }

    @Override
    public R<Void> modify(@RequestBody @Valid CorpRelatedEnterpriseModifyREQ req) {
        return corpRelatedEnterpriseApplicationService.modify(req);
    }

    @Override
    public R<Void> remove(@RequestBody @Valid CorpRelatedEnterpriseRemoveREQ req) {
        return corpRelatedEnterpriseApplicationService.remove(req);
    }

    @Override
    public R<PageR<CorpRelatedEnterpriseListRSP>> list(@RequestBody @Valid CorpRelatedEnterpriseListREQ req) {
        return corpRelatedEnterpriseApplicationService.list(req);
    }
}
