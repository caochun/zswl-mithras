package cn.zswltech.mithras.basedata.controller;

import cn.zswltech.mithras.api.basedata.BaseDataBankAccountApi;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.basedata.application.bankaccount.BaseDataBankAccountApplicationService;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.basedata.BaseDataBankAccountDetailRSP;
import cn.zswltech.mithras.dto.basedata.BaseDataBankAccountListREQ;
import cn.zswltech.mithras.dto.basedata.BaseDataBankAccountListRSP;
import cn.zswltech.mithras.dto.basedata.BaseDataBankAccountSaveREQ;
import cn.zswltech.mithras.dto.basedata.ContractAccountPayListREQ;
import cn.zswltech.mithras.dto.basedata.ContractAccountPayListRSP;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
public class BaseDataBankAccountController implements BaseDataBankAccountApi {

    @Resource
    private BaseDataBankAccountApplicationService baseDataBankAccountApplicationService;

    @Override
    public R<Long> save(@Valid BaseDataBankAccountSaveREQ baseDataBankAccountSaveREQ) {
        return baseDataBankAccountApplicationService.save(baseDataBankAccountSaveREQ);
    }

    @Override
    public R<List<BaseDataBankAccountListRSP>> list(@Valid BaseDataBankAccountListREQ req) {
        return baseDataBankAccountApplicationService.list(req);
    }

    @Override
    public R<BaseDataBankAccountDetailRSP> detail(SinglePkREQ req) {
        return baseDataBankAccountApplicationService.detail(req);
    }

    @Override
    public R<Void> delete(@Valid SinglePkREQ singlePkREQ) {
        return baseDataBankAccountApplicationService.delete(singlePkREQ);
    }

    @Override
    public R<List<ContractAccountPayListRSP>> list(@Valid ContractAccountPayListREQ req) {
        return baseDataBankAccountApplicationService.list(req);
    }

    @Override
    public R<BaseDataBankAccountDetailRSP> init() {
        return baseDataBankAccountApplicationService.init();
    }
}
