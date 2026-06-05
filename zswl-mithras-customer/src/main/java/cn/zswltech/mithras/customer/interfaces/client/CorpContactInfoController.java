package cn.zswltech.mithras.customer.interfaces.client;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.contactinfo.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import cn.zswltech.mithras.api.client.CorpContactInfoApi;
import cn.zswltech.mithras.customer.application.client.api.CorpContactInfoApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class CorpContactInfoController implements CorpContactInfoApi {
    @Resource
    private CorpContactInfoApplicationService corpContactInfoApplicationService;

    @Override
    public R<Void> add(@RequestBody @Valid CorpContactAddInfoREQ req) {
        return corpContactInfoApplicationService.add(req);
    }

    @Override
    public R<Void> modify(@RequestBody @Valid CorpContactInfoModifyREQ req) {
        return corpContactInfoApplicationService.modify(req);
    }

    @Override
    public R<PageR<CorpContactInfoListRSP>> list(@RequestBody @Valid CorpContactInfoListREQ req) {
        return corpContactInfoApplicationService.list(req);
    }

    @Override
    public R<PageR<CorpContactInfoListRSP>> listOld(@RequestBody @Valid CorpContactInfoListREQ req) {
        return corpContactInfoApplicationService.listOld(req);
    }

    @Override
    public R<Void> remove(@RequestBody @Valid CorpContactInfoRemoveREQ req) {
        return corpContactInfoApplicationService.remove(req);
    }
}
