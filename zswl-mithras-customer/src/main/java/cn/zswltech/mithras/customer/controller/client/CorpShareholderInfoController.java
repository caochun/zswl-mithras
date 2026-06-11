package cn.zswltech.mithras.customer.controller.client;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.shareholder.*;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import cn.zswltech.mithras.api.client.CorpShareholderInfoApi;
import cn.zswltech.mithras.customer.application.client.CorpShareholderInfoApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class CorpShareholderInfoController implements CorpShareholderInfoApi {
    @Resource
    private CorpShareholderInfoApplicationService corpShareholderInfoApplicationService;

    @Override
    public R<Void> add(@RequestBody @Valid CorpShareholderInfoAddREQ req) {
        return corpShareholderInfoApplicationService.add(req);
    }

    @Override
    public R<Void> modify(@RequestBody @Valid CorpShareholderInfoModifyREQ req) {
        return corpShareholderInfoApplicationService.modify(req);
    }

    @Override
    public R<Void> remove(@RequestBody @Valid CorpShareholderInfoRemoveREQ req) throws Exception {
        return corpShareholderInfoApplicationService.remove(req);
    }

    @Override
    public R<PageR<CorpShareholderInfoListRSP>> list(@RequestBody @Valid CorpShareholderInfoListREQ req) {
        return corpShareholderInfoApplicationService.list(req);
    }
}
