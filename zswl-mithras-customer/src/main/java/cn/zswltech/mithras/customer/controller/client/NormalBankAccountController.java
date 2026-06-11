package cn.zswltech.mithras.customer.controller.client;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.normal.*;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import cn.zswltech.mithras.api.client.NormalBankAccountApi;
import cn.zswltech.mithras.customer.application.client.NormalBankAccountApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class NormalBankAccountController implements NormalBankAccountApi {
    @Resource
    private NormalBankAccountApplicationService normalBankAccountApplicationService;

    @Override
    public R<Void> add(@RequestBody @Valid NormalBankAccountAddREQ req) {
        return normalBankAccountApplicationService.add(req);
    }

    @Override
    public R<Void> modify(@RequestBody @Valid NormalBankAccountModifyREQ req) {
        return normalBankAccountApplicationService.modify(req);
    }

    @Override
    public R<Void> remove(@RequestBody @Valid NormalBankAccountRemoveREQ req) {
        return normalBankAccountApplicationService.remove(req);
    }

    @Override
    public R<PageR<NormalBankAccountListRSP>> list(@RequestBody @Valid NormalBankAccountListREQ req) {
        return normalBankAccountApplicationService.list(req);
    }
}
