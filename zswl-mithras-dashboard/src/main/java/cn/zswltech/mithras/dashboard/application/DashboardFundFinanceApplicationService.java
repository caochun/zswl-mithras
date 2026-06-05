package cn.zswltech.mithras.dashboard.application;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.dashboard.*;

import java.util.List;

public interface DashboardFundFinanceApplicationService {

    List<DashboardFundFinanceStatisticsRSP> statisticsList(DashboardFundFinanceBaseREQ req) throws Exception;

    List<DashboardFundFinanceRepayRSP> listRepay(DashboardFundFinanceRepayREQ req);

    PageR<DashboardFundFinanceLoanInfoRSP> listLoanInfo(DashboardFundFinanceLoanInfoREQ req);

    List<DashboardFundFinanceCreditInfoRSP> listCredit(DashboardFundFinanceCreditInfoREQ req);

    PageR<DashboardFundFinanceBalanceRSP> listBalance(DashboardFundFinanceBalanceREQ req);

    PageR<DashboardFundFinanceFundsRSP> costFunds(DashboardFundFinanceFundsREQ req);
}
