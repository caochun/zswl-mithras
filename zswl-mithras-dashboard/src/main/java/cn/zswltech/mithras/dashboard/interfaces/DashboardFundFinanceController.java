package cn.zswltech.mithras.dashboard.interfaces;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.dashboard.DashboardFundFinanceApi;
import cn.zswltech.mithras.dto.dashboard.*;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.dashboard.application.DashboardFundFinanceApplicationService;
import cn.zswltech.mithras.dashboard.application.util.DashboardHelpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.*;

import static cn.zswltech.mithras.dashboard.application.util.DashboardHelpUtil.RECORDS;
import static cn.zswltech.mithras.dashboard.application.util.DashboardHelpUtil.SUM_DATE;

/**
 * @author yangxiong
 * @date 2024/6/26/16:17
 * @description
 */
@Slf4j
@RestController
public class DashboardFundFinanceController implements DashboardFundFinanceApi {

    @Resource
    private DashboardFundFinanceApplicationService dashboardFundFinanceService;

    private static final Set<String> LIST_LOAN_INFO_SET;

    private static final Set<String> LIST_BALANCE_SET;

    private static final Set<String> LIST_COST_SET;


    static {
        LIST_LOAN_INFO_SET = new HashSet<>();
        LIST_LOAN_INFO_SET.add("loanAmount");
        LIST_LOAN_INFO_SET.add("balanceAmount");

        LIST_BALANCE_SET = new HashSet<>();
        LIST_BALANCE_SET.add("loanAmount");
        LIST_BALANCE_SET.add("commission");
        LIST_BALANCE_SET.add("remainingAmount");

        LIST_COST_SET = new HashSet<>();
        LIST_COST_SET.add("loanAmount");
        LIST_COST_SET.add("remainingPrincipleAmount");
    }

    @Override
    public R<List<DashboardFundFinanceStatisticsRSP>> statisticsList(DashboardFundFinanceBaseREQ req) {
        try {
            return R.ok(dashboardFundFinanceService.statisticsList(req));
        } catch (Exception e) {
            log.error("【统一工作台-融资视图-统计】发生未知异常", e);
            throw new MithrasException("系统繁忙，请稍后再试");
        }
    }

    @Override
    public R<Map<String, Object>> listRepay(DashboardFundFinanceRepayREQ req) {
        HashMap<String, Object> map = new HashMap<>();
        List<DashboardFundFinanceRepayRSP> dashboardFundFinanceRepayRSPS = dashboardFundFinanceService.listRepay(req);
        map.put(RECORDS, dashboardFundFinanceRepayRSPS);
        try {
            Set<String> ignoreFieldSet = new HashSet<>();
            ignoreFieldSet.add("rentPlanCollectionAmount");
            map.put(SUM_DATE, DashboardHelpUtil.countValueUnitDTO(dashboardFundFinanceRepayRSPS, new DashboardFundFinanceRepayRSP(), ignoreFieldSet));
        } catch (Exception e) {
            log.warn("DashboardFundFinanceController listRepay count error ", e);
        }
        return R.ok(map);
    }

    @Override
    public R<Map<String, Object>> listLoanInfo(DashboardFundFinanceLoanInfoREQ req) {
        HashMap<String, Object> map = new HashMap<>();
        PageR<DashboardFundFinanceLoanInfoRSP> dashboardFundFinanceLoanInfoRSPPageR = dashboardFundFinanceService.listLoanInfo(req);
        req.setPage(1);
        req.setPageSize(Integer.MAX_VALUE);
        map.put(RECORDS, dashboardFundFinanceLoanInfoRSPPageR);

        try {
            map.put(SUM_DATE, DashboardHelpUtil.countString(dashboardFundFinanceService.listLoanInfo(req).getList(), LIST_LOAN_INFO_SET, new DashboardFundFinanceLoanInfoRSP()));
        } catch (Exception e) {
            log.warn("DashboardFundFinanceController listLoanInfo count error ", e);
        }
        return R.ok(map);
    }

    @Override
    public R<Map<String, Object>> listCredit(DashboardFundFinanceCreditInfoREQ req) {
        HashMap<String, Object> map = new HashMap<>();
        List<DashboardFundFinanceCreditInfoRSP> dashboardFundFinanceCreditInfoRSPS = dashboardFundFinanceService.listCredit(req);
        map.put(RECORDS, dashboardFundFinanceCreditInfoRSPS);
        try {
            map.put(SUM_DATE, DashboardHelpUtil.countValueUnitDTO(dashboardFundFinanceCreditInfoRSPS, new DashboardFundFinanceCreditInfoRSP()));
        } catch (Exception e) {
            log.warn("DashboardFundFinanceController listCredit count error ", e);
        }
        return R.ok(map);
    }

    @Override
    public R<Map<String, Object>> listBalance(DashboardFundFinanceBalanceREQ req) {
        HashMap<String, Object> map = new HashMap<>();
        PageR<DashboardFundFinanceBalanceRSP> dashboardFundFinanceBalanceRSPPageR = dashboardFundFinanceService.listBalance(req);
        //DashboardFundFinanceBalanceREQ allREQ = new DashboardFundFinanceBalanceREQ();
        req.setPage(1);
        req.setPageSize(Integer.MAX_VALUE);
        map.put(RECORDS, dashboardFundFinanceBalanceRSPPageR);
        try {
            map.put(SUM_DATE, DashboardHelpUtil.countString(dashboardFundFinanceService.listBalance(req).getList(), LIST_BALANCE_SET, new DashboardFundFinanceBalanceRSP()));
        } catch (Exception e) {
            log.warn("DashboardFundFinanceController listBalance count error ", e);
        }
        return R.ok(map);
    }

    @Override
    public R<Map<String, Object>> costFunds(DashboardFundFinanceFundsREQ req) {
        HashMap<String, Object> map = new HashMap<>();
        PageR<DashboardFundFinanceFundsRSP> dashboardFundFinanceFundsRSPPageR = dashboardFundFinanceService.costFunds(req);
        req.setPage(1);
        req.setPageSize(Integer.MAX_VALUE);
        map.put(RECORDS, dashboardFundFinanceFundsRSPPageR);
        try {
            List<DashboardFundFinanceFundsRSP> list = dashboardFundFinanceService.costFunds(req).getList();
            DashboardFundFinanceFundsRSP sumDate = DashboardHelpUtil.countValueUnitDTO(list, new DashboardFundFinanceFundsRSP());
            sumDate.setComprehensiveInterestRate(null);
            map.put(SUM_DATE, sumDate);
        } catch (Exception e) {
            log.warn("DashboardFundFinanceController listBalance count error ", e);
        }
        return R.ok(map);
    }
}
