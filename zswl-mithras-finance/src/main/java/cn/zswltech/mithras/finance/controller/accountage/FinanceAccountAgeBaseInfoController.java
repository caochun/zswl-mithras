package cn.zswltech.mithras.finance.controller.accountage;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.finance.FinanceAccountAgeBaseInfoApi;
import cn.zswltech.mithras.dto.finance.accountage.*;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import cn.zswltech.mithras.finance.application.accountage.api.FinanceAccountAgeBaseInfoApplicationService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FinanceAccountAgeBaseInfoController implements FinanceAccountAgeBaseInfoApi {
    @Resource
    private FinanceAccountAgeBaseInfoApplicationService financeAccountAgeBaseInfoApplicationService;

    @Override
    public R<Long> add(FinanceAccountAgeBaseInfoAddREQ req) {
        return financeAccountAgeBaseInfoApplicationService.add(req);
    }

    @Override
    public R<Void> close(FinanceAccountAgeBaseInfoCloseREQ req) {
        return financeAccountAgeBaseInfoApplicationService.close(req);
    }

    @Override
    public R<PageR<FinanceAccountAgeBaseInfoListRSP>> list(FinanceAccountAgeBaseInfoListREQ req) {
        return financeAccountAgeBaseInfoApplicationService.list(req);
    }

    @Override
    public R<FinanceAccountAgeBaseInfoDetailRSP> detail(@Valid FinanceAccountAgeBaseInfoDetailREQ req) {
        return financeAccountAgeBaseInfoApplicationService.detail(req);
    }

    @Override
    public R<Void> remove(FinanceAccountAgeBaseInfoRemoveREQ req) {
        return financeAccountAgeBaseInfoApplicationService.remove(req);
    }

    @Override
    public R<Void> effect(@Valid FinanceAccountAgeBaseInfoRemoveREQ req) {
        return financeAccountAgeBaseInfoApplicationService.effect(req);
    }

    @Override
    public R<FinanceAccountAgeCountRSP> count(@Valid FinanceAccountAgeBaseInfoDetailREQ req) {
        return financeAccountAgeBaseInfoApplicationService.count(req);
    }
}
