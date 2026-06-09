package cn.zswltech.mithras.liquiditymanage.controller;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.liquiditymanage.FundDayReportApi;
import cn.zswltech.mithras.dto.liquiditymanage.dayReport.*;
import cn.zswltech.mithras.liquiditymanage.service.FundDayReportApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author bigbear
 * @date 2024/12/17 18:56
 * @description
 */
@Slf4j
@RestController
public class FundDayReportController implements FundDayReportApi {

    @Resource
    private FundDayReportApplicationService fundDayReportService;

    @Override
    public R<DayReportIndicatorListRSP> dayReportIndicatorList(DayReportIndicatorListREQ req) {
        return R.ok(fundDayReportService.dayReportIndicatorList(req));
    }

    @Override
    public R<AccountBalanceListRSP> accountBalanceList(AccountBalanceListREQ req) {
        return R.ok(fundDayReportService.accountBalanceList(req));
    }

    @Override
    public R<List<RentIncomeListRSP>> rentIncomeList(RentIncomeListREQ req) {
        return R.ok(fundDayReportService.rentIncomeList(req));
    }

    @Override
    public R<PageR<RepayPrincipalInterestListRSP>> repayPrincipalInterestList(RepayPrincipalInterestListREQ req) {
        return R.ok(fundDayReportService.repayPrincipalInterestList(req));
    }
}
