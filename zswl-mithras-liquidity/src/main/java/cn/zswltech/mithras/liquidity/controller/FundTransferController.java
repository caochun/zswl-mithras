package cn.zswltech.mithras.liquidity.controller;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.liquiditymanage.FundTransferApi;
import cn.zswltech.mithras.dto.liquiditymanage.fundtransfer.*;
import cn.zswltech.mithras.liquidity.application.FundTransferApplicationService;
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
    private FundTransferApplicationService fundTransferService;

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
        fundTransferService.financingRepayInfoInAdvance();
    }

    @Override
    public R<PageR<AccountDepositedAmountDetail>> accountCurrentDaily(@Valid FundTransferAccountCurrentDailyREQ req) {
        return R.ok(fundTransferService.accountCurrentDaily(req));
    }
}
