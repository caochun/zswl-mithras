package cn.zswltech.mithras.liquiditymanage.service;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.liquiditymanage.dayReport.AccountBalanceListREQ;
import cn.zswltech.mithras.dto.liquiditymanage.dayReport.AccountBalanceListRSP;
import cn.zswltech.mithras.dto.liquiditymanage.dayReport.DayReportIndicatorListREQ;
import cn.zswltech.mithras.dto.liquiditymanage.dayReport.DayReportIndicatorListRSP;
import cn.zswltech.mithras.dto.liquiditymanage.dayReport.RentIncomeListREQ;
import cn.zswltech.mithras.dto.liquiditymanage.dayReport.RentIncomeListRSP;
import cn.zswltech.mithras.dto.liquiditymanage.dayReport.RepayPrincipalInterestListREQ;
import cn.zswltech.mithras.dto.liquiditymanage.dayReport.RepayPrincipalInterestListRSP;

import java.util.List;

public interface FundDayReportApplicationService {

    DayReportIndicatorListRSP dayReportIndicatorList(DayReportIndicatorListREQ req);

    AccountBalanceListRSP accountBalanceList(AccountBalanceListREQ req);

    List<RentIncomeListRSP> rentIncomeList(RentIncomeListREQ req);

    PageR<RepayPrincipalInterestListRSP> repayPrincipalInterestList(RepayPrincipalInterestListREQ req);
}
