package cn.zswltech.mithras.customer.controller.client;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.client.bankaccount.CorpBankAccountAddREQ;
import cn.zswltech.mithras.dto.client.bankaccount.CorpBankAccountListREQ;
import cn.zswltech.mithras.dto.client.bankaccount.CorpBankAccountListRSP;
import cn.zswltech.mithras.dto.client.bankaccount.CorpBankAccountModifyREQ;
import cn.zswltech.mithras.dto.client.bankaccount.CorpBankAccountRemoveREQ;
import cn.zswltech.mithras.dto.client.bankaccount.CorpVersionedBankAccountListREQ;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;
import cn.zswltech.mithras.api.client.CorpBankAccountApi;
import cn.zswltech.mithras.customer.application.client.CorpBankAccountApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class CorpBankAccountController implements CorpBankAccountApi {
    @Resource
    private CorpBankAccountApplicationService corpBankAccountApplicationService;

    @Override
    public R<Void> add(@RequestBody @Valid CorpBankAccountAddREQ req) {
        return corpBankAccountApplicationService.add(req);
    }

    @Override
    public R<Void> modify(@RequestBody @Valid CorpBankAccountModifyREQ req) {
        return corpBankAccountApplicationService.modify(req);
    }

    @Override
    public R<Void> remove(@RequestBody @Valid CorpBankAccountRemoveREQ req) {
        return corpBankAccountApplicationService.remove(req);
    }

    @Override
    public R<PageR<CorpBankAccountListRSP>> list(@RequestBody @Valid CorpBankAccountListREQ req) {
        return corpBankAccountApplicationService.list(req);
    }

    @Override
    public R<PageR<CorpBankAccountListRSP>> versionedList(@RequestBody @Valid CorpVersionedBankAccountListREQ req) {
        return corpBankAccountApplicationService.versionedList(req);
    }
}
