package cn.zswltech.mithras.finance.controller;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.finance.FinanceProjectProfitApi;
import cn.zswltech.mithras.finance.application.FinanceProjectProfitApplicationService;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import cn.zswltech.mithras.dto.PageReq;
import cn.zswltech.mithras.dto.finance.*;

@RestController
public class FinanceProjectProfitController implements FinanceProjectProfitApi {
    @Resource
    private FinanceProjectProfitApplicationService financeProjectProfitApplicationService;

    @Override
    public R<PageR<FinanceProjectProfitRSP>> pageList(PageReq req) {
        return financeProjectProfitApplicationService.pageList(req);
    }

    @Override
    public R<Void> confirm(FinanceProfitConfirmREQ req) {
        return financeProjectProfitApplicationService.confirm(req);
    }

    @Override
    public R<PageR<FinanceProjectProfitDetailRSP>> detailPageList(FinanceProjectProfitDetailREQ req) {
        return financeProjectProfitApplicationService.detailPageList(req);
    }

    @Override
    public R<Void> profitCalculation(FinanceProjectCalculationREQ req) {
        return financeProjectProfitApplicationService.profitCalculation(req);
    }

}
