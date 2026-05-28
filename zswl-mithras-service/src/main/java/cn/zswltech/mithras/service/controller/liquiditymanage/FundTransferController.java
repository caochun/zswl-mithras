package cn.zswltech.mithras.service.controller.liquiditymanage;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.liquiditymanage.FundTransferApi;
import cn.zswltech.mithras.dto.basedata.BaseDataBankAccountListREQ;
import cn.zswltech.mithras.dto.basedata.BaseDataBankAccountListRSP;
import cn.zswltech.mithras.dto.liquiditymanage.fundTransfer.*;
import cn.zswltech.mithras.service.job.FinancingRepayInfoJob;
import cn.zswltech.mithras.service.service.liquiditymanage.FundTransferService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @author bigbear
 * @date 2024/12/17 18:56
 * @description
 */
@Slf4j
@RestController
public class FundTransferController implements FundTransferApi {

    @Resource
    private FundTransferService fundTransferService;
    @Resource
    private FinancingRepayInfoJob financingRepayInfoJob;

    @Override
    public R<FundTransferListRSP> list(FundTransferListREQ req) {
        return R.ok(fundTransferService.list(req));
    }

    @Override
    public R<FundTransferGraphRSP> graphList(FundTransferGraphREQ req) {
        return R.ok(fundTransferService.graphList(req));
    }

    @Override
    public R<FundTransferDetailListRSP> detail(FundTransferDetailListREQ req) {
        return R.ok(fundTransferService.detailList(req));
    }

    @Override
    public R<FundTransferListDailyRSP> listDaily(FundTransferListDailyREQ req) {
        return R.ok(fundTransferService.listDaily(req));
    }

    @Override
    public R<FundTransferGraphDailyRSP> graphDaily(FundTransferGraphDailyREQ req) {
        return R.ok(fundTransferService.graphDaily(req));
    }

    @Override
    public R<FundTransferCurrentDailyRSP> currentDaily(FundTransferCurrentDailyREQ req) {
        return R.ok(fundTransferService.currentDaily(req));
    }

    @Override
    public R<List<FundTransferBankAccountListRSP>> accountList(FundTransferBankAccountListREQ req) {
        return R.ok(fundTransferService.list(req));
    }

    @Override
    public void test() {
        financingRepayInfoJob.financingRepayInfoInAdvance();
    }

    @Override
    public R<PageR<AccountDepositedAmountDetail>> accountCurrentDaily(@Valid FundTransferAccountCurrentDailyREQ req) {
        return R.ok(fundTransferService.accountCurrentDaily(req));
    }
}
