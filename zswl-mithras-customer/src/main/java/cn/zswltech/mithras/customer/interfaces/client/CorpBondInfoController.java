package cn.zswltech.mithras.customer.interfaces.client;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.bondinfo.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import cn.zswltech.mithras.api.client.CorpBondInfoApi;
import cn.zswltech.mithras.customer.application.client.api.CorpBondInfoApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class CorpBondInfoController implements CorpBondInfoApi {
    @Resource
    private CorpBondInfoApplicationService corpBondInfoApplicationService;

    @Override
    public R<Void> add(@RequestBody @Valid CorpBondInfoAddREQ req) {
        return corpBondInfoApplicationService.add(req);
    }

    @Override
    public R<Void> modify(@RequestBody @Valid CorpBondInfoModifyREQ req) {
        return corpBondInfoApplicationService.modify(req);
    }

    @Override
    public R<Void> remove(@RequestBody @Valid CorpBondInfoRemoveREQ req) {
        return corpBondInfoApplicationService.remove(req);
    }

    @Override
    public R<PageR<CorpBondInfoListRSP>> list(@RequestBody @Valid CorpBondInfoListREQ req) {
        return corpBondInfoApplicationService.list(req);
    }
}
