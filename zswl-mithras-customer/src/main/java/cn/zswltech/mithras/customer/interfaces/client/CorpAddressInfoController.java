package cn.zswltech.mithras.customer.interfaces.client;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.addressinfo.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import cn.zswltech.mithras.api.client.CorpAddressInfoApi;
import cn.zswltech.mithras.customer.application.client.api.CorpAddressInfoApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class CorpAddressInfoController implements CorpAddressInfoApi {
    @Resource
    private CorpAddressInfoApplicationService corpAddressInfoApplicationService;

    @Override
    public R<Void> add(@RequestBody @Valid CorpAddressInfoAddREQ req) {
        return corpAddressInfoApplicationService.add(req);
    }

    @Override
    public R<Void> modify(@RequestBody @Valid CorpAddressInfoModifyREQ req) {
        return corpAddressInfoApplicationService.modify(req);
    }

    @Override
    public R<PageR<CorpAddressInfoListRSP>> list(@RequestBody @Valid CorpAddressInfoListREQ req) {
        return corpAddressInfoApplicationService.list(req);
    }

    @Override
    public R<Void> remove(@RequestBody @Valid CorpAddressInfoRemoveREQ req) {
        return corpAddressInfoApplicationService.remove(req);
    }
}
