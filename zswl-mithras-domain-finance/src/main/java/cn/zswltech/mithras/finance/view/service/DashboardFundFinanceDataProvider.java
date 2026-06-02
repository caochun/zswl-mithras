package cn.zswltech.mithras.finance.view.service;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.dashboard.DashboardFundFinanceBaseREQ;
import cn.zswltech.mithras.dto.dashboard.DashboardFundFinanceFundsREQ;
import cn.zswltech.mithras.dto.dashboard.DashboardFundFinanceFundsRSP;
import cn.zswltech.mithras.dto.dashboard.DashboardFundFinanceLoanInfoREQ;
import cn.zswltech.mithras.dto.dashboard.DashboardFundFinanceLoanInfoRSP;
import cn.zswltech.mithras.dto.dashboard.DashboardFundFinanceRepayREQ;
import cn.zswltech.mithras.dto.dashboard.DashboardFundFinanceRepayRSP;
import cn.zswltech.mithras.dto.dashboard.DashboardFundFinanceStatisticsRSP;
import cn.zswltech.mithras.finance.view.service.dto.DashboardFundFinanceCreditSnapshotData;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface DashboardFundFinanceDataProvider {

    List<DashboardFundFinanceStatisticsRSP> statisticsList(DashboardFundFinanceBaseREQ req) throws Exception;

    PageR<DashboardFundFinanceLoanInfoRSP> listLoanInfo(DashboardFundFinanceLoanInfoREQ req);

    List<DashboardFundFinanceRepayRSP> listRepay(DashboardFundFinanceRepayREQ req);

    PageR<DashboardFundFinanceFundsRSP> costFunds(DashboardFundFinanceFundsREQ req);

    Map<Long, List<String>> getOrgNameListMap(Collection<Long> inDirectIds);

    List<DashboardFundFinanceCreditSnapshotData> listCreditSnapshotData(LocalDate queryDate);
}
