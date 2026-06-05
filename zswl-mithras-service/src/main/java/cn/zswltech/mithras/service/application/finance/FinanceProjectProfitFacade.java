package cn.zswltech.mithras.service.application.finance;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.finance.application.FinanceProjectProfitApplicationService;
import cn.zswltech.mithras.dto.PageReq;
import cn.zswltech.mithras.dto.finance.*;
import cn.zswltech.mithras.service.service.finance.FinanceProjectProfitDetailService;
import cn.zswltech.mithras.finance.service.FinanceProjectProfitService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author dingqi
 * @date 2023/6/16
 * @description
 */
@Service
public class FinanceProjectProfitFacade implements FinanceProjectProfitApplicationService {
    @Resource
    private FinanceProjectProfitService financeProjectProfitService;
    @Resource
    private FinanceProjectProfitDetailService financeProjectProfitDetailService;

    @Override
    public R<PageR<FinanceProjectProfitRSP>> pageList(@Valid PageReq req) {
        return R.ok(financeProjectProfitService.pageList(req));
    }

    @Override
    public R<Void> confirm(FinanceProfitConfirmREQ req) {
        financeProjectProfitService.confirm(req);
        return R.ok();
    }

    @Override
    public R<PageR<FinanceProjectProfitDetailRSP>> detailPageList(@Valid FinanceProjectProfitDetailREQ req) {
        return R.ok(financeProjectProfitDetailService.pageList(req));
    }

    @Override
    public R<Void> profitCalculation(@Valid FinanceProjectCalculationREQ req) {
        financeProjectProfitDetailService.profitCalculation(req);
        return R.ok();
    }
}