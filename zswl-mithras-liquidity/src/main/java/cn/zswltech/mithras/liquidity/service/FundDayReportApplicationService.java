package cn.zswltech.mithras.liquidity.service;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.liquiditymanage.dayreport.AccountBalanceListREQ;
import cn.zswltech.mithras.dto.liquiditymanage.dayreport.AccountBalanceListRSP;
import cn.zswltech.mithras.dto.liquiditymanage.dayreport.DayReportIndicatorListREQ;
import cn.zswltech.mithras.dto.liquiditymanage.dayreport.DayReportIndicatorListRSP;
import cn.zswltech.mithras.dto.liquiditymanage.dayreport.RentIncomeListREQ;
import cn.zswltech.mithras.dto.liquiditymanage.dayreport.RentIncomeListRSP;
import cn.zswltech.mithras.dto.liquiditymanage.dayreport.RepayPrincipalInterestListREQ;
import cn.zswltech.mithras.dto.liquiditymanage.dayreport.RepayPrincipalInterestListRSP;

import java.util.List;

public interface FundDayReportApplicationService {

    DayReportIndicatorListRSP dayReportIndicatorList(DayReportIndicatorListREQ req);

    AccountBalanceListRSP accountBalanceList(AccountBalanceListREQ req);

    List<RentIncomeListRSP> rentIncomeList(RentIncomeListREQ req);

    PageR<RepayPrincipalInterestListRSP> repayPrincipalInterestList(RepayPrincipalInterestListREQ req);
}
