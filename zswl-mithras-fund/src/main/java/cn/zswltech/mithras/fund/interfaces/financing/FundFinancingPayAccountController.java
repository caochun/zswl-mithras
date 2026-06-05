package cn.zswltech.mithras.fund.interfaces.financing;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.fund.financing.FundFinancingPayAccountApi;
import cn.zswltech.mithras.dto.fund.financing.payaccount.*;
import cn.zswltech.mithras.fund.application.convert.financing.FundFinancingConverter;
import cn.zswltech.mithras.basedata.mapper.model.BaseDataBankAccount;
import cn.zswltech.mithras.fund.application.financing.FundFinancingPayAccountService;
import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import cn.zswltech.mithras.fund.application.financing.api.FundFinancingPayAccountApplicationService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FundFinancingPayAccountController implements FundFinancingPayAccountApi {
    @Resource
    private FundFinancingPayAccountApplicationService fundFinancingPayAccountApplicationService;

    @Override
    public R<List<FundFinancingPayAccountBankREQ>> bankList(@Valid FundFinancingPayAccountBankREQ req) {
        return fundFinancingPayAccountApplicationService.bankList(req);
    }

    @Override
    public R<List<FundFinancingPayAccountBankRSP>> bankInfo(@Valid FundFinancingPayAccountBankREQ req) {
        return fundFinancingPayAccountApplicationService.bankInfo(req);
    }

    @Override
    public R<Void> create(@Valid FundFinancingPayAccountCreateREQ req) {
        return fundFinancingPayAccountApplicationService.create(req);
    }

    @Override
    public R<List<FundFinancingPayAccountListRSP>> list(@Valid FundFinancingPayAccountListREQ req) {
        return fundFinancingPayAccountApplicationService.list(req);
    }

    @Override
    public R<Void> modify(@Valid FundFinancingPayAccountModifyREQ req) {
        return fundFinancingPayAccountApplicationService.modify(req);
    }

    @Override
    public R<Void> delete(@Valid FundFinancingPayAccountREQ req) {
        return fundFinancingPayAccountApplicationService.delete(req);
    }
}
