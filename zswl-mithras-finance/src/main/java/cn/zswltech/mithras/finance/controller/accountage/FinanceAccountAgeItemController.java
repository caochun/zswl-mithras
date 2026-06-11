package cn.zswltech.mithras.finance.controller.accountage;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.finance.FinanceAccountAgeItemApi;
import cn.zswltech.mithras.dto.finance.accountage.*;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import cn.zswltech.mithras.finance.application.accountage.api.FinanceAccountAgeItemApplicationService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FinanceAccountAgeItemController implements FinanceAccountAgeItemApi {
    @Resource
    private FinanceAccountAgeItemApplicationService financeAccountAgeItemApplicationService;

    @Override
    public R<Void> add(FinanceAccountAgeItemAddREQ req) {
        return financeAccountAgeItemApplicationService.add(req);
    }

    @Override
    public R<Void> modify(FinanceAccountAgeItemModifyREQ req) {
        return financeAccountAgeItemApplicationService.modify(req);
    }

    @Override
    public R<PageR<FinanceAccountAgeItemListRSP>> list(FinanceAccountAgeItemListREQ req) {
        return financeAccountAgeItemApplicationService.list(req);
    }

    @Override
    public R<Void> remove(FinanceAccountAgeItemRemoveREQ req) {
        return financeAccountAgeItemApplicationService.remove(req);
    }

    @Override
    public R<Void> regeneration(@Valid FinanceAccountAgeItemRemoveREQ req) {
        return financeAccountAgeItemApplicationService.regeneration(req);
    }

    @Override
    public R<Void> sendRemote(@Valid FinanceAccountAgeItemRemoveREQ req) {
        return financeAccountAgeItemApplicationService.sendRemote(req);
    }
}
