package cn.zswltech.mithras.application.orchestration.dashboard;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.dashboard.*;
import cn.zswltech.mithras.finance.view.service.DashboardFundFinanceDataProvider;
import cn.zswltech.mithras.finance.view.service.*;
import cn.zswltech.mithras.finance.view.service.dto.DashboardFundFinanceCreditSnapshotData;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.basedata.enums.BaseDataBankAccountTypeEnum;
import cn.zswltech.mithras.dashboard.enums.DashboardCardGroupEnum;
import cn.zswltech.mithras.fund.enums.financing.FinancingTypeEnum;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingBizTypeEnum;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingStatusEnum;
import cn.zswltech.mithras.fund.enums.receiptrepay.CashFlowState;
import cn.zswltech.mithras.fund.directfinancing.mapper.model.FundDirectFinancingPledgeInfo;
import cn.zswltech.mithras.fund.directfinancing.mapper.FundDirectFinancingPledgeInfoMapper;
import cn.zswltech.mithras.dashboard.mapper.DashboardFundFinanceMapper;
import cn.zswltech.mithras.fund.mapper.financing.FundFinancingBaseInfoMapper;
import cn.zswltech.mithras.fund.mapper.financing.FundFinancingPledgeInfoMapper;
import cn.zswltech.mithras.fund.mapper.receiptrepay.FundReceiptFlowDetailMapper;
import cn.zswltech.mithras.fund.mapper.receiptrepay.FundReceiptRepayBaseInfoMapper;
import cn.zswltech.mithras.fund.mapper.receiptrepay.FundReceiptRepayCashFlowMapper;
import cn.zswltech.mithras.dashboard.mapper.model.*;
import cn.zswltech.mithras.fund.mapper.model.FundCredit;
import cn.zswltech.mithras.fund.mapper.model.FundOrganization;
import cn.zswltech.mithras.fund.mapper.model.financing.FundFinancingCreditRef;
import cn.zswltech.mithras.fund.mapper.model.financing.FundFinancingPledgeInfo;
import cn.zswltech.mithras.fund.mapper.model.receiptrepay.FundReceiptFlowDetail;
import cn.zswltech.mithras.fund.mapper.model.receiptrepay.FundReceiptRepayBaseInfo;
import cn.zswltech.mithras.fund.mapper.model.receiptrepay.FundReceiptRepayCashFlow;
import cn.zswltech.mithras.foundation.util.Util;
import cn.zswltech.mithras.credit.creditlimit.service.bo.CreditLimitDetailBO;
import cn.zswltech.mithras.application.orchestration.fund.FundCreditService;
import cn.zswltech.mithras.fund.application.financing.FundFinancingCreditRefService;
import cn.zswltech.mithras.fund.application.organization.FundOrganizationService;
import cn.zswltech.mithras.application.orchestration.fund.receiptrepay.FundReceiptFlowDetailService;
import cn.zswltech.mithras.application.orchestration.fund.receiptrepay.FundReceiptRepayBaseInfoService;
import cn.zswltech.mithras.foundation.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.hutool.extra.spring.SpringUtil.getBean;

/**
 * @author yangxiong
 * @date 2024/6/26/16:18
 * @description
 */
@Slf4j
@Service
public class DashboardFundFinanceService implements DashboardFundFinanceDataProvider, cn.zswltech.mithras.dashboard.application.DashboardFundFinanceApplicationService {
    @Resource
    private DashboardFundFinanceMapper dashboardFundFinanceMapper;
    @Resource
    private FundFinancingBaseInfoMapper fundFinancingBaseInfoMapper;
    @Resource
    private DashboardFvFinancingCostSnapshotService financingCostSnapshotService;
    @Resource
    private DashboardFvFinanceInfoSnapshotService financeInfoSnapshotService;
    @Resource
    private DashboardFvRepayPrincipalInterestSnapshotService repayPrincipalInterestSnapshotService;
    @Resource
    private DashboardFvCardSnapshotService cardSnapshotService;
    @Resource
    private DashboardFvCreditInfoSnapshotService creditInfoSnapshotService;
    @Resource
    private FundFinancingCreditRefService financingCreditRefService;
    @Resource
    private FundOrganizationService organizationService;
    @Resource
    private FundReceiptRepayBaseInfoService receiptRepayBaseInfoService;
    @Resource
    private FundReceiptFlowDetailService fundReceiptFlowDetailService;

    public List<DashboardFundFinanceStatisticsRSP> statisticsList(DashboardFundFinanceBaseREQ req) throws Exception {
        DashboardFundFinanceBaseREQ allActualReq = BeanUtil.copyProperties(req, DashboardFundFinanceBaseREQ.class);
        DashboardFundFinanceBaseREQ monthActualReq = BeanUtil.copyProperties(req, DashboardFundFinanceBaseREQ.class);
        DashboardFundFinanceBaseREQ yearActualReq = BeanUtil.copyProperties(req, DashboardFundFinanceBaseREQ.class);
        if (!req.getQueryDate().isEqual(LocalDate.now())) {
            List<DashboardFundFinanceStatisticsRSP> statisticsRspList = cardSnapshotService.statisticsList(req.getQueryDate());
            if (CollUtil.isNotEmpty(statisticsRspList)) {
                return statisticsRspList.stream().sorted(Comparator.comparing(DashboardFundFinanceStatisticsRSP::getSort)).collect(Collectors.toList());
            }
        }
        // 融资情况（存量）
        CompletableFuture<DashboardFundFinanceStatisticsRSP> loanCf = CompletableFuture.supplyAsync(() -> {
            allActualReq.setIsThisMonth(0);
            allActualReq.setIsThisYear(0);
            return loanStatistics(allActualReq);
        });
        // 还本付息
        CompletableFuture<DashboardFundFinanceStatisticsRSP> repayCf = CompletableFuture.supplyAsync(() -> this.repayStatistics(req));
        // 授信情况
        CompletableFuture<DashboardFundFinanceStatisticsRSP> creditCf = CompletableFuture.supplyAsync(() -> this.creditStatistics(req));
        // 融资情况（本年新增）
        CompletableFuture<DashboardFundFinanceStatisticsRSP> loanThisYearCf = CompletableFuture.supplyAsync(() -> {
            yearActualReq.setIsThisYear(1);
            yearActualReq.setIsThisMonth(0);
            return loanStatistics(yearActualReq);
        });
        // 融资情况（本月新增）
        CompletableFuture<DashboardFundFinanceStatisticsRSP> loanThisMonthCf = CompletableFuture.supplyAsync(() -> {
            monthActualReq.setIsThisYear(0);
            monthActualReq.setIsThisMonth(1);
            return loanStatistics(monthActualReq);
        });
        // 资金成本
        CompletableFuture<DashboardFundFinanceStatisticsRSP> costCf = CompletableFuture.supplyAsync(() -> this.costStatistics(req));
        DashboardFundFinanceStatisticsRSP rsp1 = repayCf.get();
        DashboardFundFinanceStatisticsRSP rsp2 = loanCf.get();
        DashboardFundFinanceStatisticsRSP rsp3 = creditCf.get();
        DashboardFundFinanceStatisticsRSP rsp4 = loanThisYearCf.get();
        DashboardFundFinanceStatisticsRSP rsp5 = costCf.get();
        DashboardFundFinanceStatisticsRSP rsp6 = loanThisMonthCf.get();
        return ListUtil.of(rsp1, rsp3, rsp5, rsp2, rsp4, rsp6).stream()
                .peek(e -> e.setSort(Optional.ofNullable(DashboardCardGroupEnum.ofName(e.getGroupCode()))
                        .map(DashboardCardGroupEnum::getSort).orElse(0)))
                .sorted(Comparator.comparingInt(DashboardFundFinanceStatisticsRSP::getSort))
                .collect(Collectors.toList());
    }

    private DashboardFundFinanceStatisticsRSP repayStatistics(DashboardFundFinanceBaseREQ req) {
        DashboardFundRepayQuery query = new DashboardFundRepayQuery();
        query.setRepayDateFrom(req.getQueryDate().with(TemporalAdjusters.firstDayOfMonth()));
        query.setRepayDateTo(req.getQueryDate().with(TemporalAdjusters.lastDayOfMonth()));
        List<DashboardFundRepayResult> dbList = dashboardFundFinanceMapper.listRepay(query);
        long planRepay = 0L;
        long repay = 0L;
        long expireInThreeDays = 0L;
        long expireInSevenDays = 0L;
        if (CollectionUtil.isNotEmpty(dbList)) {
            Map<String, List<DashboardFundRepayResult>> map = dbList.stream().collect(Collectors.groupingBy(a -> a.getFinancingCode() + a.getRepayDate().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN))));
            for (Map.Entry<String, List<DashboardFundRepayResult>> listEntry : map.entrySet()) {
                DashboardFundRepayResult dbResult = listEntry.getValue().get(0);
                planRepay += Optional.ofNullable(dbResult.getPrincipalAmount()).orElse(0L) + Optional.ofNullable(dbResult.getInterestAmount()).orElse(0L);
                repay += Optional.ofNullable(dbResult.getPrincipalAmount()).orElse(0L)
                        + Optional.ofNullable(dbResult.getInterestAmount()).orElse(0L) - Optional.ofNullable(dbResult.getRepayAmount()).orElse(0L);
                if (CharSequenceUtil.equalsAny(dbResult.getCashFlowWriteOffState(), CashFlowState.NO_WRITE_OFF.name(), CashFlowState.WRITE_OFF_ING.name())) {
                    long days = LocalDateTimeUtil.between(req.getQueryDate().atStartOfDay(), dbResult.getRepayDate().atStartOfDay(), ChronoUnit.DAYS);
                    if (days >= 0) {
                        if (days <= 2) {
                            expireInThreeDays += Optional.ofNullable(dbResult.getRepayAmount()).orElse(0L);
                            expireInSevenDays += Optional.ofNullable(dbResult.getRepayAmount()).orElse(0L);
                        } else if (days <= 6) {
                            expireInSevenDays += Optional.ofNullable(dbResult.getRepayAmount()).orElse(0L);
                        }
                    }
                }
            }
        }
        //计算本月已经还款的金额,从资金核销明细fund_receipt_flow_detail取值
        long repayAmount = 0L;
        List<String> cashFlowCodeList = dbList.stream().map(DashboardFundRepayResult::getCashFlowCode).collect(Collectors.toList());
        if(CollectionUtil.isNotEmpty(cashFlowCodeList)){
            List<FundReceiptFlowDetail> flowDetailList = fundReceiptFlowDetailService.listByCashFlowCodes(cashFlowCodeList);
            for (FundReceiptFlowDetail fundReceiptFlowDetail : flowDetailList) {
                repayAmount += fundReceiptFlowDetail.getPrincipalAmount();
                repayAmount += fundReceiptFlowDetail.getInterestAmount();
            }
        }
        DashboardFundFinanceStatisticsRSP rsp = new DashboardFundFinanceStatisticsRSP();
        rsp.setGroupCode(DashboardCardGroupEnum.FUND_FINANCE_REPAY.name());
        rsp.setGroup(DashboardCardGroupEnum.FUND_FINANCE_REPAY.display());
        rsp.setRepayTotalAmount(new ValueUnitDTO(Util.toWanYuanWithoutSplit(planRepay), "万元"));
        rsp.setRepayBalanceAmount(new ValueUnitDTO(Util.toWanYuanWithoutSplit(planRepay - repayAmount), "万元"));
        rsp.setRepayInThreeDays(new ValueUnitDTO(Util.toWanYuanWithoutSplit(expireInThreeDays), "万元"));
        rsp.setRepayInSevenDays(new ValueUnitDTO(Util.toWanYuanWithoutSplit(expireInSevenDays), "万元"));
        return rsp;
    }

    private DashboardFundFinanceStatisticsRSP loanStatistics(DashboardFundFinanceBaseREQ query) {
        DashboardFundFinanceLoanInfoREQ req = new DashboardFundFinanceLoanInfoREQ();
        req.setPage(1);
        // 需要修改成递归获取所有数据
        req.setPageSize(5000);
        req.setIsThisYear(query.getIsThisYear());
        req.setIsThisMonth(query.getIsThisMonth());
        PageR<DashboardFundFinanceLoanInfoRSP> pageR = this.listLoanInfo(req);
        while (CollUtil.isNotEmpty(pageR.getList()) && (long) req.getPageSize() * req.getPage() < pageR.getTotal()) {
            req.setPage(req.getPage() + 1);
            PageR<DashboardFundFinanceLoanInfoRSP> tmpPage = this.listLoanInfo(req);
            pageR.getList().addAll(tmpPage.getList());
        }

        DashboardFundFinanceStatisticsRSP rsp = new DashboardFundFinanceStatisticsRSP();
        if (query.getIsThisYear() == 1) {
            rsp.setGroupCode(DashboardCardGroupEnum.FUND_FINANCE_LOAN_THIS_YEAR.name());
            rsp.setGroup(DashboardCardGroupEnum.FUND_FINANCE_LOAN_THIS_YEAR.display());
        } else if (query.getIsThisMonth() == 1) {
            rsp.setGroupCode(DashboardCardGroupEnum.FUND_FINANCE_LOAN_THIS_MONTH.name());
            rsp.setGroup(DashboardCardGroupEnum.FUND_FINANCE_LOAN_THIS_MONTH.display());
        } else {
            rsp.setGroupCode(DashboardCardGroupEnum.FUND_FINANCE_LOAN.name());
            rsp.setGroup(DashboardCardGroupEnum.FUND_FINANCE_LOAN.display());
        }
        if (CollUtil.isEmpty(pageR.getList())) {
            rsp.setQuantity(0);
            rsp.setTotalAmount(new ValueUnitDTO("0", "万元"));
            rsp.setBalanceAmount(new ValueUnitDTO("0", "万元"));
            rsp.setAverageCostFunds(new ValueUnitDTO("0", "%"));
            rsp.setAverageInterestRate(new ValueUnitDTO("0", "%"));
            return rsp;
        }
        BigDecimal totalFinancingAmount = pageR.getList().stream().map(obj -> new BigDecimal(obj.getLoanAmount())).reduce(BigDecimal::add).get();
        BigDecimal totalBalanceAmount = pageR.getList().stream().map(obj -> new BigDecimal(obj.getBalanceAmount())).reduce(BigDecimal::add).get();
        rsp.setQuantity(pageR.getList().size());
        BigDecimal contractAvgRate = new BigDecimal(0);
        BigDecimal costAvgRate = new BigDecimal(0);
        for (DashboardFundFinanceLoanInfoRSP loanInfoRSP : pageR.getList()) {
            if (req.getIsThisYear() == 1 || req.getIsThisMonth() == 1) {
                contractAvgRate = contractAvgRate.add(new BigDecimal(loanInfoRSP.getInterestRate()).multiply(new BigDecimal(loanInfoRSP.getLoanAmount())).divide(totalFinancingAmount, 20, RoundingMode.HALF_UP));
                costAvgRate = costAvgRate.add(new BigDecimal(loanInfoRSP.getComprehensiveFinancingCost()).multiply(new BigDecimal(loanInfoRSP.getLoanAmount())).divide(totalFinancingAmount, 20, RoundingMode.HALF_UP));
            } else {
                contractAvgRate = contractAvgRate.add(new BigDecimal(loanInfoRSP.getInterestRate()).multiply(new BigDecimal(loanInfoRSP.getBalanceAmount())).divide(totalBalanceAmount, 20, RoundingMode.HALF_UP));
                costAvgRate = costAvgRate.add(new BigDecimal(loanInfoRSP.getComprehensiveFinancingCost()).multiply(new BigDecimal(loanInfoRSP.getBalanceAmount())).divide(totalBalanceAmount, 20, RoundingMode.HALF_UP));
            }
        }
        rsp.setAverageCostFunds(new ValueUnitDTO(costAvgRate.setScale(2, RoundingMode.HALF_UP).toPlainString(), "%"));
        rsp.setAverageInterestRate(new ValueUnitDTO(contractAvgRate.setScale(2, RoundingMode.HALF_UP).toPlainString(), "%"));
        rsp.setTotalAmount(new ValueUnitDTO(totalFinancingAmount.divide(new BigDecimal(10000), 2, RoundingMode.HALF_UP).toPlainString(), "万元"));
        rsp.setBalanceAmount(new ValueUnitDTO(totalBalanceAmount.divide(new BigDecimal(10000), 2, RoundingMode.HALF_UP).toPlainString(), "万元"));
        return rsp;
    }

    private DashboardFundFinanceStatisticsRSP creditStatistics(DashboardFundFinanceBaseREQ req) {
        DashboardFundCreditQuery query = new DashboardFundCreditQuery();
        query.setQueryDate(req.getQueryDate());
        List<DashboardFundCreditResult> dbList = dashboardFundFinanceMapper.listCredit(query);
        long totalCreditAmount = 0L;
        long usedCreditAmount = 0L;
        // 取出所有授信ID计算
        Set<Long> creditIds = dbList.stream().map(DashboardFundCreditResult::getId).collect(Collectors.toSet());
        List<FundCredit> fundCreditList = null;
        if (CollUtil.isNotEmpty(creditIds)) {
            fundCreditList = SpringUtil.getBean(FundCreditService.class).listByIds(creditIds);
        }
        Map<Long, CreditLimitDetailBO> creditLimitDetailBoMap = getBean(FundCreditService.class).queryLimitDetailBatch(fundCreditList, false);
        if (CollectionUtil.isNotEmpty(dbList)) {
            for (DashboardFundCreditResult dbResult : dbList) {
                totalCreditAmount += Optional.ofNullable(dbResult.getTotalCreditLimit()).orElse(0L);
                CreditLimitDetailBO creditLimitDetailBO = creditLimitDetailBoMap.get(dbResult.getId());
                // 复用授信列表逻辑，已使用额度统一都按照可循环计算
                usedCreditAmount += Optional.ofNullable(creditLimitDetailBO).map(CreditLimitDetailBO::getOccupyTotalLimit).orElse(0L);
            }
        }
        DashboardFundFinanceStatisticsRSP rsp = new DashboardFundFinanceStatisticsRSP();
        rsp.setGroupCode(DashboardCardGroupEnum.FUND_FINANCE_CREDIT.name());
        rsp.setGroup(DashboardCardGroupEnum.FUND_FINANCE_CREDIT.display());
        rsp.setQuantity(dbList.size());
        rsp.setTotalAmount(new ValueUnitDTO(Util.toWanYuanWithoutSplit(totalCreditAmount), "万元"));
        rsp.setUsedAmount(new ValueUnitDTO(Util.toWanYuanWithoutSplit(usedCreditAmount), "万元"));
        return rsp;
    }

    private DashboardFundFinanceStatisticsRSP costStatistics(DashboardFundFinanceBaseREQ req) {
        // 借款余额成本 = Σ[实际综合成本(票面加权利率)*(借款余额)/ Σ借款余额) ]
        // 本年新增借款成本 = Σ[实际综合成本(票面加权利率)*(本年新增贷款金额)/ Σ本年新增贷款金额)]
        // 本月新增借款成本 = Σ[实际综合成本(票面加权利率)*(本月新增贷款金额)/ Σ本月新增贷款金额)]

        DashboardFundFinanceStatisticsRSP rsp = new DashboardFundFinanceStatisticsRSP();
        DashboardFundCostQuery query = new DashboardFundCostQuery();
        Page<DashboardFundCostResult> page = new Page<>(1, 10000);
        Page<DashboardFundCostResult> resultList = dashboardFundFinanceMapper.listCost(page, query);
        // 分类
        List<DashboardFundCostResult> zrList = new LinkedList<>();
        List<DashboardFundCostResult> zrThisYearList = new LinkedList<>();
        List<DashboardFundCostResult> jrList = new LinkedList<>();
        List<DashboardFundCostResult> jrThisYearList = new LinkedList<>();
        LocalDate yearFrom = LocalDate.now().with(TemporalAdjusters.firstDayOfYear());
        LocalDate yearTo = LocalDate.now().with(TemporalAdjusters.lastDayOfYear());
        for (DashboardFundCostResult result : resultList.getRecords()) {
            if (StrUtil.isBlank(result.getFinancingTypeCode())) {
                continue;
            }
            if (Objects.equals(result.getFinancingTypeCode(), "DIRECT")) {
                // 直接融
                zrList.add(result);
                if (Objects.nonNull(result.getActualLoanDate()) && !result.getActualLoanDate().isBefore(yearFrom) && !result.getActualLoanDate().isAfter(yearTo)) {
                    zrThisYearList.add(result);
                }
            }
            if (Objects.equals(result.getFinancingTypeCode(), "INDIRECT")) {
                // 间接融资
                jrList.add(result);
                if (Objects.nonNull(result.getActualLoanDate()) && !result.getActualLoanDate().isBefore(yearFrom) && !result.getActualLoanDate().isAfter(yearTo)) {
                    jrThisYearList.add(result);
                }
            }
        }
        long zrLoanCost = 0L;
        long zrLoanCostThisYear = 0L;
        long jrLoanCost = 0L;
        long jrLoanCostThisYear = 0L;
        if (CollectionUtil.isNotEmpty(zrList)) {
            zrLoanCost = this.calcCost(zrList, false);
        }
        if (CollectionUtil.isNotEmpty(zrThisYearList)) {
            zrLoanCostThisYear = this.calcCost(zrThisYearList, true);
        }
        if (CollectionUtil.isNotEmpty(jrList)) {
            jrLoanCost = this.calcCost(jrList, false);
        }
        if (CollectionUtil.isNotEmpty(jrThisYearList)) {
            jrLoanCostThisYear = this.calcCost(jrThisYearList, true);
        }
        rsp.setGroupCode(DashboardCardGroupEnum.FOND_FINANCE_COST_FOUNDS.name());
        rsp.setGroup(DashboardCardGroupEnum.FOND_FINANCE_COST_FOUNDS.display());
        rsp.setCostFundsZR(new ValueUnitDTO(Util.toYuanWithoutSplit(zrLoanCost), "%"));
        rsp.setCostFundsZRThisYear(new ValueUnitDTO(Util.toYuanWithoutSplit(zrLoanCostThisYear), "%"));
        rsp.setCostFundsJR(new ValueUnitDTO(Util.toYuanWithoutSplit(jrLoanCost), "%"));
        rsp.setCostFundsJRThisYear(new ValueUnitDTO(Util.toYuanWithoutSplit(jrLoanCostThisYear), "%"));
        return rsp;
    }

    private long calcCost(List<DashboardFundCostResult> resultList, boolean useLoanAmount) {
        BigDecimal totalBalanceAmount = BigDecimal.valueOf(resultList.stream().mapToLong(DashboardFundCostResult::getRemainingPrincipleAmount).sum());
        BigDecimal totalLoanAmount = BigDecimal.valueOf(resultList.stream().mapToLong(DashboardFundCostResult::getLoanAmount).sum());
        // 借贷余额成本
        BigDecimal result = BigDecimal.ZERO;
        for (DashboardFundCostResult item : resultList) {
            if (useLoanAmount) {
                BigDecimal multiply = BigDecimal.valueOf(item.getComprehensiveInterestRate()).multiply(BigDecimal.valueOf(item.getLoanAmount()));
                result = result.add(multiply.divide(totalLoanAmount, 20, RoundingMode.HALF_UP));
            } else {
                BigDecimal multiply = BigDecimal.valueOf(item.getComprehensiveInterestRate()).multiply(BigDecimal.valueOf(item.getRemainingPrincipleAmount()));
                result = result.add(multiply.divide(totalBalanceAmount, 20, RoundingMode.HALF_UP));
            }
        }
        return result.setScale(6, RoundingMode.HALF_UP).longValue();
    }

    public PageR<DashboardFundFinanceLoanInfoRSP> listLoanInfo(DashboardFundFinanceLoanInfoREQ req) {
        Page<DashboardFundFinanceLoanInfoRSP> page = new Page<>(req.getPage(), req.getPageSize());
        // 如果查询的是本年或者本月，则查询的融资状态为起息和结清，否则查询的融资状态为起息
        if (req.getIsThisYear() == YesOrNoNumberEnum.YES.getCode() || req.getIsThisMonth() == YesOrNoNumberEnum.YES.getCode()) {
            req.setFinancingStatusList(ListUtil.toList(FundFinancingStatusEnum.CARRY_INTEREST.name(), FundFinancingStatusEnum.SETTLE.name()));
        } else {
            req.setFinancingStatusList(ListUtil.toList(FundFinancingStatusEnum.CARRY_INTEREST.name()));
        }
        if (!req.getQueryDate().isEqual(LocalDate.now())) {
            PageR<DashboardFundFinanceLoanInfoRSP> pageR = financeInfoSnapshotService.listLoanInfo(req);
            if (CollUtil.isNotEmpty(pageR.getList())) {
                return pageR;
            }
        }
        if (Objects.equals(req.getIsThisYear(), YesOrNoNumberEnum.YES.getCode())) {
            req.setActualLoanDateFrom(req.getQueryDate().with(TemporalAdjusters.firstDayOfYear()));
            req.setActualLoanDateTo(req.getQueryDate().with(TemporalAdjusters.lastDayOfYear()));
        }
        if (Objects.equals(req.getIsThisMonth(), YesOrNoNumberEnum.YES.getCode())) {
            req.setActualLoanDateFrom(req.getQueryDate().with(TemporalAdjusters.firstDayOfMonth()));
            req.setActualLoanDateTo(req.getQueryDate().with(TemporalAdjusters.lastDayOfMonth()));
        }
        Page<DashboardFundFinanceLoanInfoRSP> pageList = fundFinancingBaseInfoMapper.listLoanInfo(page, req);
        if (CollUtil.isEmpty(pageList.getRecords())) {
            return PageR.empty(req.getPage(), req.getPageSize());
        }
        pageList.getRecords().forEach(e -> {
            if (Objects.nonNull(e.getActualLoanDate())) {
                e.setActualLoanDateStr(LocalDateTimeUtil.format(e.getActualLoanDate(), DatePattern.NORM_DATE_PATTERN));
            }
            if (Objects.nonNull(e.getActualExpireDate())) {
                e.setActualExpireDateStr(LocalDateTimeUtil.format(e.getActualExpireDate(), DatePattern.NORM_DATE_PATTERN));
            }
        });
        List<DashboardFundFinanceLoanInfoRSP> loanInfoList = pageList.getRecords();
        fillOtherInfo(loanInfoList);
        return PageR.of(loanInfoList, pageList.getTotal());
    }

    private void fillOtherInfo(List<DashboardFundFinanceLoanInfoRSP> loanInfoList) {
        Set<Long> directIds = loanInfoList.stream()
                .filter(a -> Objects.nonNull(a.getIdKey()))
                .filter(a -> a.getIdKey().startsWith(FinancingTypeEnum.DIRECT.name()))
                .map(a -> a.getIdKey().substring(a.getIdKey().lastIndexOf('_') + 1))
                .map(Long::valueOf)
                .collect(Collectors.toSet());
        List<FundDirectFinancingPledgeInfo> fundDirectFinancingPledgeInfos = null;
        if (CollUtil.isNotEmpty(directIds)) {
            fundDirectFinancingPledgeInfos = getBean(FundDirectFinancingPledgeInfoMapper.class).selectList(Wrappers.<FundDirectFinancingPledgeInfo>lambdaQuery()
                    .in(FundDirectFinancingPledgeInfo::getFinancingId, directIds)
                    .eq(FundDirectFinancingPledgeInfo::getIsPledge, YesOrNoNumberEnum.YES.getCode()));
        }
        Map<Long, List<FundDirectFinancingPledgeInfo>> listMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(fundDirectFinancingPledgeInfos)) {
            listMap = fundDirectFinancingPledgeInfos.stream()
                    .collect(Collectors.groupingBy(FundDirectFinancingPledgeInfo::getFinancingId));
        }

        Set<Long> inDirectIds = loanInfoList.stream()
                .filter(a -> Objects.nonNull(a.getIdKey()))
                .filter(a -> a.getIdKey().startsWith(FinancingTypeEnum.INDIRECT.name()))
                .map(a -> a.getIdKey().substring(a.getIdKey().lastIndexOf('_') + 1))
                .map(Long::valueOf)
                .collect(Collectors.toSet());
        List<FundFinancingPledgeInfo> fundFinancingPledgeInfos = null;
        if (CollUtil.isNotEmpty(inDirectIds)) {
            fundFinancingPledgeInfos = getBean(FundFinancingPledgeInfoMapper.class).selectList(Wrappers.<FundFinancingPledgeInfo>lambdaQuery()
                    .in(FundFinancingPledgeInfo::getFinancingId, inDirectIds)
                    .eq(FundFinancingPledgeInfo::getIsPledge, YesOrNoNumberEnum.YES.getCode()));
        }
        Map<Long, List<FundFinancingPledgeInfo>> map = new HashMap<>();
        if (!CollectionUtils.isEmpty(fundFinancingPledgeInfos)) {
            map = fundFinancingPledgeInfos.stream().collect(Collectors.groupingBy(FundFinancingPledgeInfo::getFinancingId));
        }
        // 间融需要额外处理机构信息
        Map<Long, List<String>> orgNameListMap = this.getOrgNameListMap(inDirectIds);
        for (DashboardFundFinanceLoanInfoRSP dto : loanInfoList) {
            Long financingId = Long.valueOf(dto.getIdKey().substring(dto.getIdKey().lastIndexOf('_') + 1));
            List<FundFinancingPledgeInfo> pledgeInfos = map.get(financingId);
            if (CollUtil.isNotEmpty(pledgeInfos) && FinancingTypeEnum.INDIRECT.name().equals(dto.getFinancingTypeCode())) {
                dto.setRelatedContractCodeList(pledgeInfos.stream().map(FundFinancingPledgeInfo::getContractCode).collect(Collectors.toList()));
            }
            if (FinancingTypeEnum.INDIRECT.name().equals(dto.getFinancingTypeCode())) {
                List<String> orgNameList = orgNameListMap.get(financingId);
                if (CollectionUtil.isNotEmpty(orgNameList)) {
                    dto.setOrgName(CharSequenceUtil.join("、", orgNameList));
                }
            }
            List<FundDirectFinancingPledgeInfo> financingPledgeInfos = listMap.get(financingId);
            if (CollUtil.isNotEmpty(financingPledgeInfos) && FinancingTypeEnum.DIRECT.name().equals(dto.getFinancingTypeCode())) {
                dto.setRelatedContractCodeList(financingPledgeInfos.stream().map(FundDirectFinancingPledgeInfo::getContractCode).collect(Collectors.toList()));
            }
        }
    }

    public Map<Long, List<String>> getOrgNameListMap(Collection<Long> inDirectIds) {
        // 间融需要额外处理机构信息
        Map<Long, List<String>> orgNameListMap = new HashMap<>(256);
        if (CollectionUtil.isNotEmpty(inDirectIds)) {
            List<FundFinancingCreditRef> fundFinancingCreditRefList = getBean(FundFinancingCreditRefService.class).list(Wrappers.<FundFinancingCreditRef>lambdaQuery().in(FundFinancingCreditRef::getFinancingId, inDirectIds));
            if (CollectionUtil.isNotEmpty(fundFinancingCreditRefList)) {
                Map<Long, List<FundFinancingCreditRef>> creditRefMap = fundFinancingCreditRefList.stream().collect(Collectors.groupingBy(FundFinancingCreditRef::getFinancingId));
                Set<Long> orgIds = fundFinancingCreditRefList.stream().map(FundFinancingCreditRef::getOrganizationId).collect(Collectors.toSet());
                List<FundOrganization> fundOrganizationList = SpringUtil.getBean(FundOrganizationService.class).listByIds(orgIds);
                Map<Long, FundOrganization> orgMap = fundOrganizationList.stream().collect(Collectors.toMap(FundOrganization::getId, e -> e));
                for (Map.Entry<Long, List<FundFinancingCreditRef>> entry : creditRefMap.entrySet()) {
                    List<String> nameList = new LinkedList<>();
                    for (FundFinancingCreditRef fundFinancingCreditRef : entry.getValue()) {
                        FundOrganization fundOrganization = orgMap.get(fundFinancingCreditRef.getOrganizationId());
                        if (Objects.nonNull(fundOrganization)) {
                            nameList.add(fundOrganization.getOrganizationName());
                        }
                    }
                    orgNameListMap.put(entry.getKey(), nameList);
                }
            }
        }
        return orgNameListMap;
    }

    public List<DashboardFundFinanceRepayRSP> listRepay(DashboardFundFinanceRepayREQ req) {
        DashboardFundRepayQuery query = new DashboardFundRepayQuery();
        query.setWriteOffState(req.getWriteOffState());
        query.setWriteOffStateList(req.getWriteOffStateList());
        query.setFinancingCode(req.getFinancingCode());
        query.setFinancingName(req.getOrgName());
        query.setRepayDateFrom(req.getQueryDate().with(TemporalAdjusters.firstDayOfMonth()));
        query.setRepayDateTo(req.getQueryDate().with(TemporalAdjusters.lastDayOfMonth()));
        query.setIds(req.getIds());
        if (!req.getQueryDate().isEqual(LocalDate.now())) {
            List<DashboardFundFinanceRepayRSP> financeRepayRspList = repayPrincipalInterestSnapshotService.listRepay(query);
            if (CollUtil.isNotEmpty(financeRepayRspList)) {
                return financeRepayRspList;
            }
        }
        List<DashboardFundRepayResult> dbList = dashboardFundFinanceMapper.listRepay(query);
        if (CollectionUtil.isEmpty(dbList)) {
            return Collections.emptyList();
        }
        // 批量查询融资机构
        Map<Long, List<FundFinancingCreditRef>> orgMap = financingCreditRefService.queryBatchByFinancingId(dbList.stream()
                .filter(item -> item.getIsDirect() == 0)
                .map(DashboardFundRepayResult::getFinancingId).collect(Collectors.toList()));
        Set<Long> orgIds = orgMap.values().stream().flatMap(Collection::stream).map(FundFinancingCreditRef::getOrganizationId).collect(Collectors.toSet());
        Map<Long, String> orgIdNameMap = organizationService.getNamesByIds(orgIds);
        Map<String, List<DashboardFundRepayResult>> tempMap = dbList.stream().collect(Collectors.groupingBy(DashboardFundRepayResult::getIdKey));
        // 查询直融的剩余本金
        List<Long> financingIds = dbList.stream().filter(item -> item.getIsDirect() == 1)
                .map(DashboardFundRepayResult::getFinancingId).distinct().collect(Collectors.toList());
        Map<Long, Long> fundReceiptRepayCashFlowMap = new HashMap<>();
        if (CollUtil.isNotEmpty(financingIds)) {
            fundReceiptRepayCashFlowMap = receiptRepayBaseInfoService.queryRemainingAmount(financingIds, FinancingTypeEnum.DIRECT);
        }
        List<DashboardFundFinanceRepayRSP> resultList = new ArrayList<>(tempMap.size());
        Map<Long, Long> finalFundReceiptRepayCashFlowMap = fundReceiptRepayCashFlowMap;
        tempMap.forEach((idKey, list) -> {
            DashboardFundFinanceRepayRSP rsp = new DashboardFundFinanceRepayRSP();
            DashboardFundRepayResult repayResult = list.get(list.size() - 1);
            rsp.setReceiptRepayCashFlowId(repayResult.getId());
            rsp.setIsDirect(repayResult.getIsDirect());
            rsp.setFinancingId(repayResult.getFinancingId());
            rsp.setFinancingCode(repayResult.getFinancingCode());
            rsp.setWriteOffState(repayResult.getCashFlowWriteOffState());
            rsp.setWriteOffStateDisplay(Optional.ofNullable(CashFlowState.of(repayResult.getCashFlowWriteOffState())).map(CashFlowState::display).orElse(""));
            rsp.setOrgName(repayResult.getFinancingName());
            rsp.setLoanAmount(Optional.ofNullable(repayResult.getFinancingAmount())
                    .map(e -> new ValueUnitDTO(Util.toWanYuanWithoutSplit(repayResult.getFinancingAmount()), "万元"))
                    .orElse(new ValueUnitDTO("0", "万元")));
            if (YesOrNoNumberEnum.NO.getCode().equals(repayResult.getIsDirect())) {
                String yuanWithoutSplit = Util.toWanYuanWithoutSplit(LongUtil.null2zero(repayResult.getRemainingPrincipalAmount()));
                rsp.setLoanBalanceAmount(new ValueUnitDTO(yuanWithoutSplit, "万元"));
            } else {
                String yuanWithoutSplit = Util.toWanYuanWithoutSplit(LongUtil.null2zero(finalFundReceiptRepayCashFlowMap.get(repayResult.getFinancingId())));
                rsp.setLoanBalanceAmount(new ValueUnitDTO(yuanWithoutSplit, "万元"));
            }
            // 再次设置间融的机构名称
            if (Objects.equals(YesOrNoNumberEnum.NO.getCode(), repayResult.getIsDirect())) {
                Set<FundFinancingCreditRef> collect = list.stream().map(DashboardFundRepayResult::getFinancingId)
                        .map(orgMap::get).flatMap(Collection::stream).collect(Collectors.toSet());
                List<String> collected = collect.stream().map(FundFinancingCreditRef::getOrganizationId)
                        .map(orgIdNameMap::get).collect(Collectors.toList());
                rsp.setOrgName(String.join(",", collected));
            }

            //按照融资将期项分组
            Map<String, DashboardFundRepayResult> financingMap = list.stream().collect(Collectors.toMap(obj -> obj.getFinancingId() +
                    obj.getRepayDate().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)), Function.identity(), (k1, k2) -> k1));
            if (!CollectionUtils.isEmpty(financingMap)) {
//                long repayAmount = financingMap.values().stream().filter(obj -> Objects.nonNull(obj.getRepayAmount()))
//                        .mapToLong(DashboardFundRepayResult::getRepayAmount).summaryStatistics().getSum();
//                rsp.setRepayTotalAmount(new ValueUnitDTO(Util.toYuanWithoutSplit(repayAmount), "元"));

                long repayPrincipalAmount = financingMap.values().stream().filter(obj -> Objects.nonNull(obj.getPrincipalAmount()))
                        .mapToLong(DashboardFundRepayResult::getPrincipalAmount).summaryStatistics().getSum();
                rsp.setRepayPrincipalAmount(new ValueUnitDTO(Util.toYuanWithoutSplit(repayPrincipalAmount), "元"));

                long repayInterestAmount = financingMap.values().stream().filter(obj -> Objects.nonNull(obj.getInterestAmount()))
                        .mapToLong(DashboardFundRepayResult::getInterestAmount).summaryStatistics().getSum();
                rsp.setRepayInterestAmount(new ValueUnitDTO(Util.toYuanWithoutSplit(repayInterestAmount), "元"));
                rsp.setRepayTotalAmount(new ValueUnitDTO(Util.toYuanWithoutSplit(repayInterestAmount + repayPrincipalAmount), "元"));

                //本月已还金额
                long actualRetryAmount = financingMap.values().stream().filter(obj -> Objects.nonNull(obj.getTotalPayAmount()))
                        .mapToLong(DashboardFundRepayResult::getTotalPayAmount).summaryStatistics().getSum();
                rsp.setActualRepayAmount(new ValueUnitDTO(Util.toYuanWithoutSplit(actualRetryAmount), "元"));

                // 本月未还金额
                long repayBalanceAmount = LongUtil.null2zero(repayInterestAmount) +LongUtil.null2zero(repayPrincipalAmount) - LongUtil.null2zero(actualRetryAmount);
                rsp.setRepayBalanceAmount(new ValueUnitDTO(Util.toYuanWithoutSplit(Math.max(repayBalanceAmount, 0L)), "元"));
            }
            rsp.setRepayDate(CharSequenceUtil.join(",", list.stream()
                    .map(DashboardFundRepayResult::getRepayDate)
                    .filter(Objects::nonNull)
                    .map(obj -> obj.format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN))).collect(Collectors.toSet())));
            rsp.setActualRepayDate(CharSequenceUtil.join(",", list.stream()
                    .map(DashboardFundRepayResult::getMinPayDate)
                    .filter(Objects::nonNull)
                    .map(obj -> obj.format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN))).collect(Collectors.toSet())));
            List<DashboardFundFinanceRepayRSP.SubListInfo> subList = list.stream().map(obj -> {
                DashboardFundFinanceRepayRSP.SubListInfo sub = new DashboardFundFinanceRepayRSP.SubListInfo();
                sub.setRelatedProjName(obj.getProjName());
                sub.setRelatedContractCode(obj.getContractCode());
                if (Objects.nonNull(obj.getTotalPlanRentAmount())) {
                    sub.setRentPlanCollectionAmount(new ValueUnitDTO(Util.toYuanWithoutSplit(obj.getTotalPlanRentAmount()), "元"));
                }
                sub.setRentPlanCollectionDate(obj.getPlanCollectionRentDate());
                sub.setBankAccountTypeCode(obj.getBankAccountType());
                sub.setBankAccountTypeDisplay(Optional.ofNullable(BaseDataBankAccountTypeEnum.find(obj.getBankAccountType())).map(BaseDataBankAccountTypeEnum::display).orElse(""));
                return sub;
            }).collect(Collectors.toList());
            rsp.setSubListInfo(subList);
            resultList.add(rsp);
        });
        return resultList;
    }

    public List<DashboardFundFinanceCreditInfoRSP> listCredit(DashboardFundFinanceCreditInfoREQ req) {
        DashboardFundCreditQuery query = new DashboardFundCreditQuery();
        query.setBusinessType(req.getFinancingBizType());
        query.setCreditCode(req.getCreditCode());
        query.setOrganizationName(req.getOrgName());
        query.setQueryDate(req.getQueryDate());
        query.setIds(req.getIds());
        if (!req.getQueryDate().isEqual(LocalDate.now())) {
            List<DashboardFundFinanceCreditInfoRSP> creditInfoRspList = creditInfoSnapshotService.listCredit(query);
            if (CollectionUtil.isNotEmpty(creditInfoRspList)) {
                return creditInfoRspList;
            }
        }
        List<DashboardFundCreditResult> dbList = dashboardFundFinanceMapper.listCredit(query);
        if (CollectionUtil.isEmpty(dbList)) {
            return Collections.emptyList();
        }
        // 取出所有授信ID计算
        Set<Long> creditIds = dbList.stream().map(DashboardFundCreditResult::getId).collect(Collectors.toSet());
        List<FundCredit> fundCreditList = null;
        if (CollUtil.isNotEmpty(creditIds)) {
            fundCreditList = SpringUtil.getBean(FundCreditService.class).listByIds(creditIds);
        }
        Map<Long, CreditLimitDetailBO> creditLimitDetailBoMap = getBean(FundCreditService.class).queryLimitDetailBatch(fundCreditList, false);
        return dbList.stream().map(e -> {
            DashboardFundFinanceCreditInfoRSP rsp = new DashboardFundFinanceCreditInfoRSP();
            rsp.setId(e.getId());
            rsp.setCreditCode(e.getCreditCode());
            rsp.setOrgName(e.getOrganizationName());
            rsp.setFinancingBizTypeCode(e.getBusinessType());
            rsp.setFinancingBizTypeDisplay(Optional.ofNullable(FundFinancingBizTypeEnum.finaByName(e.getBusinessType())).map(FundFinancingBizTypeEnum::display).orElse(""));
            if (Objects.nonNull(e.getTotalCreditLimit())) {
                rsp.setCreditTotalAmount(new ValueUnitDTO(Util.toWanYuanWithoutSplit(e.getTotalCreditLimit()), "万元"));
            }
            CreditLimitDetailBO creditLimitDetail = creditLimitDetailBoMap.get(e.getId());
            // 复用授信列表逻辑，已使用额度统一都按照可循环计算
            Long usedLimit = Optional.ofNullable(creditLimitDetail).map(CreditLimitDetailBO::getOccupyTotalLimit).orElse(0L);
            rsp.setCreditUsedAmount(new ValueUnitDTO(Util.toWanYuanWithoutSplit(usedLimit), "万元"));
            rsp.setIsCycle(e.getRecyclable());
            rsp.setDeadline(e.getDeadline());
            return rsp;
        }).collect(Collectors.toList());
    }

    @Deprecated
    public PageR<DashboardFundFinanceBalanceRSP> listBalance(DashboardFundFinanceBalanceREQ req) {
        Page<DashboardFundFinanceBalanceRSP> page = new Page<>(req.getPage(), req.getPageSize());
        Page<DashboardFundFinanceBalanceRSP> pageList = fundFinancingBaseInfoMapper.listBalance(page, req);
        if (CollectionUtils.isEmpty(pageList.getRecords())) {
            return PageR.empty(req.getPage(), req.getPageSize());
        }

        //填充抵质押合同号，和未还本金
        List<DashboardFundFinanceBalanceRSP> rspList = pageList.getRecords();
        fillInfo(rspList);
        return PageR.of(rspList, pageList.getTotal());
    }

    private void fillInfo(List<DashboardFundFinanceBalanceRSP> rspList) {
        Set<Long> inDirectIds = rspList.stream()
                .filter(a -> Objects.nonNull(a.getIdKey()))
                .filter(a -> a.getIdKey().startsWith(FinancingTypeEnum.INDIRECT.name()))
                .map(a -> a.getIdKey().substring(a.getIdKey().lastIndexOf('_') + 1))
                .map(Long::valueOf)
                .collect(Collectors.toSet());

        List<FundFinancingPledgeInfo> fundFinancingPledgeInfos = null;
        if (CollUtil.isNotEmpty(inDirectIds)) {
            fundFinancingPledgeInfos = getBean(FundFinancingPledgeInfoMapper.class).selectList(Wrappers.<FundFinancingPledgeInfo>lambdaQuery()
                    .in(FundFinancingPledgeInfo::getFinancingId, inDirectIds)
                    .eq(FundFinancingPledgeInfo::getIsPledge, YesOrNoNumberEnum.YES.getCode()));
        }
        Map<Long, List<FundFinancingPledgeInfo>> map = new HashMap<>();
        if (!CollectionUtils.isEmpty(fundFinancingPledgeInfos)) {
            map = fundFinancingPledgeInfos.stream().collect(Collectors.groupingBy(FundFinancingPledgeInfo::getFinancingId));
        }

        Set<Long> directIds = rspList.stream()
                .filter(a -> Objects.nonNull(a.getIdKey()))
                .filter(a -> a.getIdKey().startsWith(FinancingTypeEnum.DIRECT.name()))
                .map(a -> a.getIdKey().substring(a.getIdKey().lastIndexOf('_') + 1))
                .map(Long::valueOf)
                .collect(Collectors.toSet());

        List<FundDirectFinancingPledgeInfo> fundDirectFinancingPledgeInfos = null;
        if (CollUtil.isNotEmpty(directIds)) {
            fundDirectFinancingPledgeInfos = getBean(FundDirectFinancingPledgeInfoMapper.class).selectList(Wrappers.<FundDirectFinancingPledgeInfo>lambdaQuery()
                    .in(FundDirectFinancingPledgeInfo::getFinancingId, directIds)
                    .eq(FundDirectFinancingPledgeInfo::getIsPledge, YesOrNoNumberEnum.YES.getCode()));
        }
        Map<Long, List<FundDirectFinancingPledgeInfo>> listMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(fundDirectFinancingPledgeInfos)) {
            listMap = fundDirectFinancingPledgeInfos.stream()
                    .collect(Collectors.groupingBy(FundDirectFinancingPledgeInfo::getFinancingId));
        }

        for (DashboardFundFinanceBalanceRSP dto : rspList) {
            //填充合同编号
            List<FundFinancingPledgeInfo> pledgeInfos = map.get(Long.valueOf(dto.getIdKey().substring(dto.getIdKey().lastIndexOf('_') + 1)));
            if (CollUtil.isNotEmpty(pledgeInfos) && FinancingTypeEnum.INDIRECT.name().equals(dto.getFinancingTypeCode())) {
                dto.setRelatedContractCodeList(pledgeInfos.stream().map(FundFinancingPledgeInfo::getContractCode).collect(Collectors.toList()));
            }
            List<FundDirectFinancingPledgeInfo> financingPledgeInfos = listMap.get(Long.valueOf(dto.getIdKey().substring(dto.getIdKey().lastIndexOf('_') + 1)));
            if (CollUtil.isNotEmpty(financingPledgeInfos) && FinancingTypeEnum.DIRECT.name().equals(dto.getFinancingTypeCode())) {
                dto.setRelatedContractCodeList(financingPledgeInfos.stream().map(FundDirectFinancingPledgeInfo::getContractCode).collect(Collectors.toList()));
            }
        }

        //填充剩余金额和剩余期限和剩余利息
        List<String> financingCodeList = rspList.stream().map(o -> o.getFinancingCode() + 'F').collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(financingCodeList)) {
            //因为列表展示的是起息的数据，所以这里不应该为空
            List<FundReceiptRepayBaseInfo> fundReceiptRepayBaseInfos = getBean(FundReceiptRepayBaseInfoMapper.class).selectList(Wrappers.<FundReceiptRepayBaseInfo>lambdaQuery()
                    .in(FundReceiptRepayBaseInfo::getReceiptRepayCode, financingCodeList));
            if (!CollectionUtils.isEmpty(fundReceiptRepayBaseInfos)) {
                Map<String, FundReceiptRepayBaseInfo> baseInfoMap = fundReceiptRepayBaseInfos.stream().collect(Collectors.toMap(FundReceiptRepayBaseInfo::getReceiptRepayCode, Function.identity(), (a, b) -> b));
                List<Long> baseIds = fundReceiptRepayBaseInfos.stream().map(FundReceiptRepayBaseInfo::getId).collect(Collectors.toList());
                Map<Long, List<FundReceiptRepayCashFlow>> mapByReceiptId = getBean(FundReceiptRepayCashFlowMapper.class).selectList(Wrappers.<FundReceiptRepayCashFlow>lambdaQuery()
                                .in(FundReceiptRepayCashFlow::getReceiptRepayId, baseIds)
                                .notIn(FundReceiptRepayCashFlow::getWriteOffState, CashFlowState.WRITTEN_OFF.name(), CashFlowState.BEYOND_WRITTEN_OFF.name()))
                        .stream().collect(Collectors.groupingBy(FundReceiptRepayCashFlow::getReceiptRepayId));

                Map<Long, List<FundReceiptRepayCashFlow>> cashFlowsIdMap = getBean(FundReceiptRepayCashFlowMapper.class).selectList(Wrappers.<FundReceiptRepayCashFlow>lambdaQuery()
                                .in(FundReceiptRepayCashFlow::getReceiptRepayId, baseIds))
                        .stream().collect(Collectors.groupingBy(FundReceiptRepayCashFlow::getReceiptRepayId));

                rspList.forEach(obj -> {
                    FundReceiptRepayBaseInfo fundReceiptRepayBaseInfo = baseInfoMap.get(obj.getFinancingCode() + 'F');
                    if (Objects.nonNull(fundReceiptRepayBaseInfo)) {
                        List<FundReceiptRepayCashFlow> fundReceiptRepayCashFlows = mapByReceiptId.get(fundReceiptRepayBaseInfo.getId());
                        if (!CollectionUtils.isEmpty(fundReceiptRepayCashFlows)) {
                            //排序
                            fundReceiptRepayCashFlows.sort(Comparator.comparing(FundReceiptRepayCashFlow::getPhase));
                            //剩余期限（月）
                            long months = LocalDateTimeUtil.between(LocalDateTimeUtil.now(),
                                    fundReceiptRepayCashFlows.get(fundReceiptRepayCashFlows.size() - 1).getRepayDate().atStartOfDay(), ChronoUnit.MONTHS);
                            obj.setRemainingDuration(months + "");

                            //剩余未还本金
                            List<FundReceiptFlowDetail> fundReceiptFlowDetails = getBean(FundReceiptFlowDetailMapper.class).selectList(Wrappers.<FundReceiptFlowDetail>lambdaQuery()
                                    .in(FundReceiptFlowDetail::getCashFlowCode, fundReceiptRepayCashFlows.stream().map(FundReceiptRepayCashFlow::getCashFlowCode).collect(Collectors.toList())));
                            long writeOffPrincipalAmount = fundReceiptFlowDetails.stream()
                                    .filter(o -> Objects.nonNull(o.getPrincipalAmount()))
                                    .mapToLong(FundReceiptFlowDetail::getPrincipalAmount).summaryStatistics().getSum();
                            long writeOffInterestAmount = fundReceiptFlowDetails.stream()
                                    .filter(o -> Objects.nonNull(o.getInterestAmount()))
                                    .mapToLong(FundReceiptFlowDetail::getInterestAmount).summaryStatistics().getSum();
                            BigDecimal principal = new BigDecimal(obj.getLoanAmount()).subtract(LongUtil.tenThousand2Dollar(writeOffPrincipalAmount + ""));
                            List<FundReceiptRepayCashFlow> flowList = cashFlowsIdMap.get(fundReceiptRepayBaseInfo.getId());
                            BigDecimal interest = BigDecimal.ZERO;
                            if (!CollectionUtils.isEmpty(flowList)) {
                                long allInterest = flowList.stream().mapToLong(a -> LongUtil.null2zero(a.getInterestAmount())).summaryStatistics().getSum();
                                interest = new BigDecimal(allInterest).subtract(BigDecimal.valueOf(writeOffInterestAmount));
                            }
                            obj.setRemainingAmount(principal.multiply(BigDecimal.valueOf(10000)).toPlainString());
                            obj.setRemainingInterestAmount(interest.toPlainString());
                        }
                    }
                });
            }
        }
    }

    @Override
    public List<DashboardFundFinanceCreditSnapshotData> listCreditSnapshotData(LocalDate queryDate) {
        DashboardFundCreditQuery query = new DashboardFundCreditQuery();
        query.setQueryDate(queryDate);
        List<DashboardFundCreditResult> dbList = dashboardFundFinanceMapper.listCredit(query);
        if (CollectionUtil.isEmpty(dbList)) {
            return Collections.emptyList();
        }
        Set<Long> creditIds = dbList.stream().map(DashboardFundCreditResult::getId).collect(Collectors.toSet());
        List<FundCredit> fundCreditList = null;
        if (CollUtil.isNotEmpty(creditIds)) {
            fundCreditList = SpringUtil.getBean(FundCreditService.class).listByIds(creditIds);
        }
        Map<Long, CreditLimitDetailBO> creditLimitDetailBoMap = getBean(FundCreditService.class).queryLimitDetailBatch(fundCreditList, false);
        return dbList.stream().map(e -> {
            DashboardFundFinanceCreditSnapshotData data = new DashboardFundFinanceCreditSnapshotData();
            data.setId(e.getId());
            data.setCreditCode(e.getCreditCode());
            data.setOrganizationName(e.getOrganizationName());
            data.setBusinessType(e.getBusinessType());
            data.setTotalCreditLimit(e.getTotalCreditLimit());
            data.setUsedCreditLimit(Optional.ofNullable(creditLimitDetailBoMap.get(e.getId()))
                    .map(CreditLimitDetailBO::getOccupyTotalLimit).orElse(0L));
            data.setRecyclable(e.getRecyclable());
            data.setDeadline(e.getDeadline());
            return data;
        }).collect(Collectors.toList());
    }

    public PageR<DashboardFundFinanceFundsRSP> costFunds(DashboardFundFinanceFundsREQ req) {
        DashboardFundCostQuery query = BeanUtil.copyProperties(req, DashboardFundCostQuery.class);
        if (YesOrNoNumberEnum.YES.getCode().equals(req.getIsThisYear())) {
            query.setStartDate(LocalDate.now().with(TemporalAdjusters.firstDayOfYear()));
            query.setEndDate(LocalDate.now().with(TemporalAdjusters.lastDayOfYear()));
        }
        if (!req.getQueryDate().isEqual(LocalDate.now())) {
            PageR<DashboardFundFinanceFundsRSP> listedCost = financingCostSnapshotService.listCost(query);
            if (CollUtil.isNotEmpty(listedCost.getList())) {
                // 手工排序
                List<DashboardFundFinanceFundsRSP> lastRspList = new ArrayList<>();
                List<DashboardFundFinanceFundsRSP> have = listedCost.getList().stream().filter(obj -> !obj.getRemainingPrincipleAmount().getValue().equals("0.00")).collect(Collectors.toList());
                List<DashboardFundFinanceFundsRSP> noHave = listedCost.getList().stream().filter(obj -> obj.getRemainingPrincipleAmount().getValue().equals("0.00")).collect(Collectors.toList());
                if (CollUtil.isNotEmpty(have)) {
                    lastRspList.addAll(have);
                }
                if (CollUtil.isNotEmpty(noHave)) {
                    lastRspList.addAll(noHave);
                }
                listedCost.setList(lastRspList);
                // 手工排序
                List<DashboardFundFinanceFundsRSP> lastList = new ArrayList<>();
                List<DashboardFundFinanceFundsRSP> have0 = listedCost.getList().stream().filter(obj -> !obj.getRemainingPrincipleAmount().getValue().equals("0.00")).collect(Collectors.toList());
                List<DashboardFundFinanceFundsRSP> noHave1 = listedCost.getList().stream().filter(obj -> obj.getRemainingPrincipleAmount().getValue().equals("0.00")).collect(Collectors.toList());
                if (CollUtil.isNotEmpty(have0)) {
                    lastList.addAll(have0);
                }
                if (CollUtil.isNotEmpty(noHave1)) {
                    lastList.addAll(noHave1);
                }
                // 修改分页逻辑，正确处理最后一页不足pageSize的情况
                int fromIndex = (req.getPage() - 1) * req.getPageSize();
                int toIndex = Math.min(fromIndex + req.getPageSize(), lastList.size());
                // 检查起始索引是否超出列表范围
                if (fromIndex > lastList.size()) {
                    return PageR.of(new ArrayList<>(), lastList.size(), req.getPage(), req.getPageSize());
                }

                List<DashboardFundFinanceFundsRSP> list = lastList.subList(fromIndex, toIndex);
                return PageR.of(list, listedCost.getTotal(), req.getPage(), req.getPageSize());
            }
        }
        Page<DashboardFundCostResult> page = new Page<>(1, 10000);
        Page<DashboardFundCostResult> resultList = dashboardFundFinanceMapper.listCost(page, query);
        if (CollUtil.isEmpty(resultList.getRecords())) {
            return PageR.empty(req.getPage(), req.getPageSize());
        }
        Set<Long> indirectIds = resultList.getRecords().stream().filter(e -> e.getFinancingCode().contains("DK")).map(DashboardFundCostResult::getFinancingId).collect(Collectors.toSet());
        Map<Long, List<String>> orgNameListMap = this.getOrgNameListMap(indirectIds);

        List<DashboardFundFinanceFundsRSP> rspList = resultList.getRecords().stream().map(result -> {
            DashboardFundFinanceFundsRSP rsp = new DashboardFundFinanceFundsRSP();
            rsp.setFinancingCode(result.getFinancingCode());
            rsp.setFinancingId(result.getFinancingId());
            if (Objects.nonNull(result.getActualLoanDate())) {
                rsp.setActualLoanDateStr(LocalDateTimeUtil.format(result.getActualLoanDate(), DatePattern.NORM_DATE_PATTERN));
            }
            if (Objects.nonNull(result.getActualExpireDate())) {
                rsp.setActualExpireDateStr(LocalDateTimeUtil.format(result.getActualExpireDate(), DatePattern.NORM_DATE_PATTERN));
            }
            rsp.setOrgName(result.getOrgName());
            FinancingTypeEnum financingType = result.getFinancingCode().contains("ZR") ? FinancingTypeEnum.DIRECT : FinancingTypeEnum.INDIRECT;
            rsp.setFinancingTypeCode(financingType.name());
            rsp.setFinancingTypeDisplay(financingType.getDisplay());
            rsp.setComprehensiveInterestRate(new ValueUnitDTO(Util.toYuanWithoutSplit(result.getComprehensiveInterestRate()), "%"));
            rsp.setRepayWay(result.getRepayWay());
            rsp.setFinancingMonth(result.getFinancingMonth());
            if (rsp.getFinancingTypeCode().equals(FinancingTypeEnum.DIRECT.name())) {
                result.setLoanAmount(result.getLoanAmount());
            }
            if (FinancingTypeEnum.INDIRECT.name().equals(rsp.getFinancingTypeCode())) {
                List<String> orgNameList = orgNameListMap.get(rsp.getFinancingId());
                if (CollectionUtil.isNotEmpty(orgNameList)) {
                    rsp.setOrgName(CharSequenceUtil.join("、", orgNameList));
                }
            }
            rsp.setLoanAmount(new ValueUnitDTO(Util.toYuanWithoutSplit(result.getLoanAmount()), "元"));
            rsp.setRemainingPrincipleAmount(new ValueUnitDTO(Util.toYuanWithoutSplit(result.getRemainingPrincipleAmount()), "元"));
            return rsp;
        }).collect(Collectors.toList());
        fillOtherInfoFunds(rspList);
        // 手工排序
        List<DashboardFundFinanceFundsRSP> lastList = new ArrayList<>();
        List<DashboardFundFinanceFundsRSP> have = rspList.stream().filter(obj -> !obj.getRemainingPrincipleAmount().getValue().equals("0.00")).collect(Collectors.toList());
        List<DashboardFundFinanceFundsRSP> noHave = rspList.stream().filter(obj -> obj.getRemainingPrincipleAmount().getValue().equals("0.00")).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(have)) {
            lastList.addAll(have);
        }
        if (CollUtil.isNotEmpty(noHave)) {
            lastList.addAll(noHave);
        }
        // 修改分页逻辑，正确处理最后一页不足pageSize的情况
        int fromIndex = (req.getPage() - 1) * req.getPageSize();
        int toIndex = Math.min(fromIndex + req.getPageSize(), lastList.size());
        // 检查起始索引是否超出列表范围
        if (fromIndex > lastList.size()) {
            return PageR.of(new ArrayList<>(), lastList.size(), req.getPage(), req.getPageSize());
        }

        List<DashboardFundFinanceFundsRSP> list = lastList.subList(fromIndex, toIndex);
        return PageR.of(list, page.getTotal(), req.getPage(), req.getPageSize());
    }


    private void fillOtherInfoFunds(List<DashboardFundFinanceFundsRSP> loanInfoList) {
        Set<Long> directIds = loanInfoList.stream()
                .filter(a -> a.getFinancingTypeCode().equals(FinancingTypeEnum.DIRECT.name()))
                .map(DashboardFundFinanceFundsRSP::getFinancingId)
                .collect(Collectors.toSet());
        List<FundDirectFinancingPledgeInfo> fundDirectFinancingPledgeInfos = null;
        if (CollUtil.isNotEmpty(directIds)) {
            fundDirectFinancingPledgeInfos = getBean(FundDirectFinancingPledgeInfoMapper.class).selectList(Wrappers.<FundDirectFinancingPledgeInfo>lambdaQuery()
                    .in(FundDirectFinancingPledgeInfo::getFinancingId, directIds)
                    .eq(FundDirectFinancingPledgeInfo::getIsPledge, YesOrNoNumberEnum.YES.getCode()));
        }
        Map<Long, List<FundDirectFinancingPledgeInfo>> listMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(fundDirectFinancingPledgeInfos)) {
            listMap = fundDirectFinancingPledgeInfos.stream()
                    .collect(Collectors.groupingBy(FundDirectFinancingPledgeInfo::getFinancingId));
        }

        Set<Long> inDirectIds = loanInfoList.stream()
                .filter(a -> a.getFinancingTypeCode().equals(FinancingTypeEnum.INDIRECT.name()))
                .map(DashboardFundFinanceFundsRSP::getFinancingId)
                .collect(Collectors.toSet());
        List<FundFinancingPledgeInfo> fundFinancingPledgeInfos = null;
        if (CollUtil.isNotEmpty(inDirectIds)) {
            fundFinancingPledgeInfos = getBean(FundFinancingPledgeInfoMapper.class).selectList(Wrappers.<FundFinancingPledgeInfo>lambdaQuery()
                    .in(FundFinancingPledgeInfo::getFinancingId, inDirectIds)
                    .eq(FundFinancingPledgeInfo::getIsPledge, YesOrNoNumberEnum.YES.getCode()));
        }
        Map<Long, List<FundFinancingPledgeInfo>> map = new HashMap<>();
        if (!CollectionUtils.isEmpty(fundFinancingPledgeInfos)) {
            map = fundFinancingPledgeInfos.stream().collect(Collectors.groupingBy(FundFinancingPledgeInfo::getFinancingId));
        }

        for (DashboardFundFinanceFundsRSP dto : loanInfoList) {
            List<FundFinancingPledgeInfo> pledgeInfos = map.get(dto.getFinancingId());
            if (CollUtil.isNotEmpty(pledgeInfos) && FinancingTypeEnum.INDIRECT.name().equals(dto.getFinancingTypeCode())) {
                dto.setRelatedContractCodeList(pledgeInfos.stream().map(FundFinancingPledgeInfo::getContractCode).collect(Collectors.toList()));
            }
            List<FundDirectFinancingPledgeInfo> financingPledgeInfos = listMap.get(dto.getFinancingId());
            if (CollUtil.isNotEmpty(financingPledgeInfos) && FinancingTypeEnum.DIRECT.name().equals(dto.getFinancingTypeCode())) {
                dto.setRelatedContractCodeList(financingPledgeInfos.stream().map(FundDirectFinancingPledgeInfo::getContractCode).collect(Collectors.toList()));
            }
        }
    }
}
