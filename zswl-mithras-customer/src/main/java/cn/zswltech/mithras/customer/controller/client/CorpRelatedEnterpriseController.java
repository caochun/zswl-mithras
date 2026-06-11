package cn.zswltech.mithras.customer.controller.client;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.relatedenterprise.CorpRelatedEnterpriseAddREQ;
import cn.zswltech.mithras.dto.client.relatedenterprise.CorpRelatedEnterpriseListREQ;
import cn.zswltech.mithras.dto.client.relatedenterprise.CorpRelatedEnterpriseListRSP;
import cn.zswltech.mithras.dto.client.relatedenterprise.CorpRelatedEnterpriseModifyREQ;
import cn.zswltech.mithras.dto.client.relatedenterprise.CorpRelatedEnterpriseRemoveREQ;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import cn.zswltech.mithras.api.client.CorpRelatedEnterpriseApi;
import cn.zswltech.mithras.customer.application.client.CorpRelatedEnterpriseApplicationService;
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
