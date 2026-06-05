package cn.zswltech.mithras.service.service.dashboard;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.dto.dashboard.*;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.assetclassify.domain.enums.AssetClassifyResultEnum;
import cn.zswltech.mithras.service.enums.common.ProjectBizType;
import cn.zswltech.mithras.dashboard.application.DashboardAuthQueryHelper;
import cn.zswltech.mithras.dashboard.application.DashboardProjectService;
import cn.zswltech.mithras.dashboard.domain.enums.DashboardCardGroupEnum;
import cn.zswltech.mithras.dashboard.domain.enums.DashboardPledgeTypeEnum;
import cn.zswltech.mithras.fund.domain.enums.financing.FundFinancingStatusEnum;
import cn.zswltech.mithras.kpi.enums.KpiProjectClassifyEnum;
import cn.zswltech.mithras.projectprocess.enums.projreview.ProjRegionalClassify;
import cn.zswltech.mithras.riskcontrol.common.RiskControlIndustryClassify;
import cn.zswltech.mithras.dashboard.infrastructure.persistence.mapper.DashboardProjectInfoMapper;
import cn.zswltech.mithras.basedata.mapper.model.BaseDataBankAccount;
import cn.zswltech.mithras.dashboard.infrastructure.persistence.mapper.model.*;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.service.service.basedata.BaseDataBankAccountService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2024/6/18
 * @description
 */
@Service
public class DashboardProjectInfoService extends DashboardProjectService implements cn.zswltech.mithras.dashboard.application.DashboardProjectInfoApplicationService {
    @Resource
    private DashboardProjectInfoMapper dashboardProjectInfoMapper;

    public List<DashboardProjectFinanceStatisticsRSP> financeStatisticsList() throws Exception {
        AccountVO accountVO = AccountUtil.getLoginInfo();
        CompletableFuture<DashboardProjectFinanceStatisticsRSP> rentInThisMonthResult = CompletableFuture.supplyAsync(() -> rentInThisMonthStatisticsFinanceGroup(accountVO));
        CompletableFuture<DashboardProjectFinanceStatisticsRSP> overdueResult = CompletableFuture.supplyAsync(() -> overdueStatisticsFinanceGroup(accountVO));
        CompletableFuture<DashboardProjectFinanceStatisticsRSP> provisionResult = CompletableFuture.supplyAsync(() -> provisionStatisticsFinanceGroup(accountVO));
        CompletableFuture<DashboardProjectFinanceStatisticsRSP> noSettleResult = CompletableFuture.supplyAsync(() -> noSettleStatisticsFinanceGroup(accountVO));
        CompletableFuture<DashboardProjectFinanceStatisticsRSP> pledgeResult = CompletableFuture.supplyAsync(() -> pledgeStatisticsFinanceGroup(accountVO));
        DashboardProjectFinanceStatisticsRSP rsp1 = rentInThisMonthResult.get();
        DashboardProjectFinanceStatisticsRSP rsp3 = overdueResult.get();
        DashboardProjectFinanceStatisticsRSP rsp4 = provisionResult.get();
        DashboardProjectFinanceStatisticsRSP rsp5 = noSettleResult.get();
        DashboardProjectFinanceStatisticsRSP rsp6 = pledgeResult.get();
        return ListUtil.of(rsp1, rsp6, rsp3, rsp4, rsp5);
    }

    private DashboardProjectFinanceStatisticsRSP rentInThisMonthStatisticsFinanceGroup(AccountVO accountVO) {
        DashboardProjectInfoRentThisMonthQuery query = new DashboardProjectInfoRentThisMonthQuery();
        query.setAccountVO(accountVO);
        DashboardAuthQueryHelper.fillAuthQuery(query);
        List<DashboardProjectInfoRentThisMonthResult> dbList = dashboardProjectInfoMapper.listRentThisMonth(query);
        DashboardProjectFinanceStatisticsRSP rsp = new DashboardProjectFinanceStatisticsRSP();
        rsp.setGroupCode(DashboardCardGroupEnum.PROJECT_VIEW_FINANCE_RENT_IN_MONTH.name());
        rsp.setGroup(DashboardCardGroupEnum.PROJECT_VIEW_FINANCE_RENT_IN_MONTH.display());
        if (CollectionUtil.isEmpty(dbList)) {
            return rsp;
        }
        long planAmount = 0L;
        long collectionAmount = 0L;
        for (DashboardProjectInfoRentThisMonthResult result : dbList) {
            planAmount += Optional.ofNullable(result.getPlanCollectionAmount()).orElse(0L);
            collectionAmount += Optional.ofNullable(result.getCollectionAmount()).orElse(0L);
        }
        rsp.setQuantity(dbList.size());
        rsp.setPlanRentAmountThisMonth(new ValueUnitDTO(Util.toWanYuanWithoutSplit(planAmount), "万元"));
        rsp.setUncollectionRentAmountThisMonth(new ValueUnitDTO(Util.toWanYuanWithoutSplit(planAmount - collectionAmount), "万元"));
        return rsp;
    }

    private DashboardProjectFinanceStatisticsRSP overdueStatisticsFinanceGroup(AccountVO accountVO) {
        DashboardProjectInfoOverdueQuery query = new DashboardProjectInfoOverdueQuery();
        query.setAccountVO(accountVO);
        DashboardAuthQueryHelper.fillAuthQuery(query);
        List<DashboardProjectInfoOverdueResult> dbList = dashboardProjectInfoMapper.listOverdue(query);
        DashboardProjectFinanceStatisticsRSP rsp = new DashboardProjectFinanceStatisticsRSP();
        rsp.setGroupCode(DashboardCardGroupEnum.PROJECT_VIEW_FINANCE_OVERDUE.name());
        rsp.setGroup(DashboardCardGroupEnum.PROJECT_VIEW_FINANCE_OVERDUE.display());
        if (CollectionUtil.isEmpty(dbList)) {
            return rsp;
        }
        DashboardProjectInfoStatisticsRSP dashboardProjectInfoStatisticsRSP = this.overdueStatistics(accountVO, new DashboardProjectInfoStatisticsListREQ());
        rsp.setQuantity(dashboardProjectInfoStatisticsRSP.getQuantity());
        rsp.setAmount(dashboardProjectInfoStatisticsRSP.getAmount());
        return rsp;
    }

    private DashboardProjectFinanceStatisticsRSP provisionStatisticsFinanceGroup(AccountVO accountVO) {
        DashboardProvisionQuery query = new DashboardProvisionQuery();
        query.setAccountVO(accountVO);
        DashboardAuthQueryHelper.fillAuthQuery(query);
        List<DashboardProvisionResult> dbList = dashboardProjectInfoMapper.listProvision(query);
        DashboardProjectFinanceStatisticsRSP rsp = new DashboardProjectFinanceStatisticsRSP();
        rsp.setGroupCode(DashboardCardGroupEnum.PROJECT_VIEW_FINANCE_PROVISION.name());
        rsp.setGroup(DashboardCardGroupEnum.PROJECT_VIEW_FINANCE_PROVISION.display());
        if (CollectionUtil.isEmpty(dbList)) {
            return rsp;
        }
        long totalExposure = 0L;
        long totalProvision = 0L;
        for (DashboardProvisionResult result : dbList) {
            totalExposure += Optional.ofNullable(result.getExposure()).orElse(0L);
            totalProvision += Optional.ofNullable(result.getProvisionBalance()).orElse(0L);
        }
        rsp.setTotalExposure(new ValueUnitDTO(Util.toWanYuanWithoutSplit(totalExposure), "万元"));
        rsp.setProvisionBalanceAmount(new ValueUnitDTO(Util.toWanYuanWithoutSplit(totalProvision), "万元"));
        return rsp;
    }

    private DashboardProjectFinanceStatisticsRSP noSettleStatisticsFinanceGroup(AccountVO accountVO) {
        DashboardProjectPayNoSettleQuery query = new DashboardProjectPayNoSettleQuery();
        query.setAccountVO(accountVO);
        DashboardAuthQueryHelper.fillAuthQuery(query);
        List<DashboardProjectPayNoSettleResult> dbList = dashboardProjectInfoMapper.listPayNoSettle(query);
        DashboardProjectFinanceStatisticsRSP rsp = new DashboardProjectFinanceStatisticsRSP();
        rsp.setGroupCode(DashboardCardGroupEnum.PROJECT_VIEW_FINANCE_NO_SETTLE.name());
        rsp.setGroup(DashboardCardGroupEnum.PROJECT_VIEW_FINANCE_NO_SETTLE.display());
        if (CollectionUtil.isEmpty(dbList)) {
            return rsp;
        }
        long totalPlanRentAmount = 0L;
        long totalCollectionRentAmount = 0L;
        for (DashboardProjectPayNoSettleResult result : dbList) {
            totalPlanRentAmount += Optional.ofNullable(result.getPlanRentAmount()).orElse(0L);
            totalCollectionRentAmount += Optional.ofNullable(result.getActualRentAmount()).orElse(0L);
        }
        rsp.setQuantity(dbList.size());
        rsp.setRentBalanceAmount(new ValueUnitDTO(Util.toWanYuanWithoutSplit(totalPlanRentAmount - totalCollectionRentAmount), "万元"));
        return rsp;
    }

    private DashboardProjectFinanceStatisticsRSP pledgeStatisticsFinanceGroup(AccountVO accountVO) {
        DashboardProjectPledgeQuery query = new DashboardProjectPledgeQuery();
        query.setAccountVO(accountVO);
        DashboardAuthQueryHelper.fillAuthQuery(query);
        List<DashboardProjectPledgeResult> resultList = dashboardProjectInfoMapper.listProjPledge(query);
        this.replacePledgeBankInfo(resultList);
        DashboardProjectFinanceStatisticsRSP rsp = new DashboardProjectFinanceStatisticsRSP();
        int pledge = 0;
        int supervise = 0;
        int quantity = 0;
        if(CollectionUtil.isNotEmpty(resultList)) {
            pledge = resultList.stream().mapToInt(DashboardProjectPledgeResult::getIsPledge).sum();
            supervise = resultList.stream().mapToInt(DashboardProjectPledgeResult::getIsSupervise).sum();
            quantity = resultList.size();
        }
        rsp.setGroup(DashboardCardGroupEnum.PROJECT_VIEW_FINANCE_PLEDGE.display());
        rsp.setGroupCode(DashboardCardGroupEnum.PROJECT_VIEW_FINANCE_PLEDGE.name());
        rsp.setQuantity(quantity);
        rsp.setPledgeQuantity(pledge);
        rsp.setSuperviseQuantity(supervise);
        return rsp;
    }

    private void replacePledgeBankInfo(List<DashboardProjectPledgeResult> resultList) {
        BaseDataBankAccount defaultBankAccount = SpringUtil.getBean(BaseDataBankAccountService.class).getDefaultAccount();
        resultList.forEach(e -> {
            // 非监管或者融资状态不符合的，如果监管账户存在值则修改为基本户
            if (Objects.equals(e.getIsSupervise(), YesOrNoNumberEnum.NO.getCode()) || !StrUtil.equalsAny(e.getFinancingStatus(), FundFinancingStatusEnum.NEW.name(), FundFinancingStatusEnum.EFFECT.name(), FundFinancingStatusEnum.CARRY_INTEREST.name())) {
                if (StrUtil.isNotBlank(e.getAccountName())) {
                    e.setAccountName(defaultBankAccount.getAccountName());
                }
                if (StrUtil.isNotBlank(e.getAccountNumber())) {
                    e.setAccountNumber(defaultBankAccount.getAccountNumber());
                }
                if (StrUtil.isNotBlank(e.getAccountBank())) {
                    e.setAccountBank(defaultBankAccount.getAccountBank());
                }
            }
        });
    }

    public List<DashboardProjectInfoStatisticsRSP> statisticsList(DashboardProjectInfoStatisticsListREQ req) {
        List<DashboardProjectInfoStatisticsRSP> result = new LinkedList<>();
        AccountVO accountVO = AccountUtil.getLoginInfo();
        result.add(this.settleInThreeStatistics(accountVO, req));
        result.add(this.overdueStatistics(accountVO, req));
        result.add(this.rentInThisMonthStatistics(accountVO, req));
        return result;
    }

    private DashboardProjectInfoStatisticsRSP settleInThreeStatistics(AccountVO accountVO, DashboardProjectInfoStatisticsListREQ req) {
        DashboardProjectInfoSettleInThreeMonthQuery query = new DashboardProjectInfoSettleInThreeMonthQuery();
        query.setQueryDateFrom(LocalDate.now());
        query.setQueryDateTo(query.getQueryDateFrom().plusDays(90));
        query.setAccountVO(accountVO);
        query.setPermissionType(req.getPermissionType());
        DashboardAuthQueryHelper.fillAuthQuery(query);
        List<DashboardProjectInfoSettleInThreeMonthResult> dbList = dashboardProjectInfoMapper.listSettleInThreeMonth(query);
        Set<Long> projReviewIds = new HashSet<>();
        long totalAmount = 0L;
        for (DashboardProjectInfoSettleInThreeMonthResult dbResult : dbList) {
            projReviewIds.add(dbResult.getProjReviewId());
            long planPrincipal = Optional.ofNullable(dbResult.getTotalPlanPrincipal()).orElse(0L);
            long planInterest = Optional.ofNullable(dbResult.getTotalPlanInterest()).orElse(0L);
            long collectionPrincipal = Optional.ofNullable(dbResult.getTotalCollectionPrincipal()).orElse(0L);
            long collectionInterest = Optional.ofNullable(dbResult.getTotalCollectionInterest()).orElse(0L);
            totalAmount += planPrincipal + planInterest - collectionPrincipal - collectionInterest;
        }
        return new DashboardProjectInfoStatisticsRSP(DashboardCardGroupEnum.PROJECT_VIEW_INFO_SETTLE_IN_THREE_MONTH.name(), DashboardCardGroupEnum.PROJECT_VIEW_INFO_SETTLE_IN_THREE_MONTH.display(), projReviewIds.size(), new ValueUnitDTO(Util.toWanYuanWithoutSplit(totalAmount), "万元"));
    }

    private DashboardProjectInfoStatisticsRSP overdueStatistics(AccountVO accountVO, DashboardProjectInfoStatisticsListREQ req) {
        DashboardProjectInfoOverdueQuery query = new DashboardProjectInfoOverdueQuery();
        query.setAccountVO(accountVO);
        DashboardAuthQueryHelper.fillAuthQuery(query);
        query.setPermissionType(req.getPermissionType());
        List<DashboardProjectInfoOverdueResult> dbList = dashboardProjectInfoMapper.listOverdue(query);
        Set<Long> projReviewIds = new HashSet<>();
        long totalAmount = 0L;
        for (DashboardProjectInfoOverdueResult dbResult : dbList) {
            projReviewIds.add(dbResult.getProjReviewId());
            long planRent = Optional.ofNullable(dbResult.getPlanCollectionAmount()).orElse(0L);
            long collectionRent = Optional.ofNullable(dbResult.getCollectionAmount()).orElse(0L);
            totalAmount += planRent - collectionRent;
        }
        return new DashboardProjectInfoStatisticsRSP(DashboardCardGroupEnum.PROJECT_VIEW_INFO_OVERDUE.name(), DashboardCardGroupEnum.PROJECT_VIEW_INFO_OVERDUE.display(), projReviewIds.size(), new ValueUnitDTO(Util.toWanYuanWithoutSplit(totalAmount), "万元"));
    }

    private DashboardProjectInfoStatisticsRSP rentInThisMonthStatistics(AccountVO accountVO, DashboardProjectInfoStatisticsListREQ req) {
        DashboardProjectInfoRentThisMonthQuery query = new DashboardProjectInfoRentThisMonthQuery();
        query.setAccountVO(accountVO);
        DashboardAuthQueryHelper.fillAuthQuery(query);
        query.setPermissionType(req.getPermissionType());
        List<DashboardProjectInfoRentThisMonthResult> dbList = dashboardProjectInfoMapper.listRentThisMonth(query);
        Set<Long> projReviewIds = new HashSet<>();
        long totalAmount = 0L;
        for (DashboardProjectInfoRentThisMonthResult dbResult : dbList) {
            projReviewIds.add(dbResult.getProjReviewId());
            long planRent = Optional.ofNullable(dbResult.getPlanCollectionAmount()).orElse(0L);
            long collectionRent = Optional.ofNullable(dbResult.getCollectionAmount()).orElse(0L);
            totalAmount += planRent -collectionRent;
        }
        return new DashboardProjectInfoStatisticsRSP(DashboardCardGroupEnum.PROJECT_VIEW_INFO_RENT_IN_MONTH.name(), DashboardCardGroupEnum.PROJECT_VIEW_INFO_RENT_IN_MONTH.display(), projReviewIds.size(), new ValueUnitDTO(Util.toWanYuanWithoutSplit(totalAmount), "万元"));
    }

    public SettleInThreeMonSumRSP listSettleInThreeMonth(DashboardProjectInfoSettleInThreeMonthREQ req) {
        DashboardProjectInfoSettleInThreeMonthQuery query = new DashboardProjectInfoSettleInThreeMonthQuery();
        SettleInThreeMonSumRSP.SumData sumData = new SettleInThreeMonSumRSP.SumData();
        query.setAccountVO(AccountUtil.getLoginInfo());
        query.setClientId(req.getClientId());
        query.setContractCode(req.getContractCode());
        query.setDeadlineFrom(req.getDeadlineFrom());
        query.setDeadlineTo(req.getDeadlineTo());
        query.setBizDeptId(req.getBizDeptId());
        query.setProjSponsorUserId(req.getProjSponsorUserId());
        query.setQueryDateFrom(LocalDate.now());
        query.setQueryDateTo(query.getQueryDateFrom().plusDays(90));
        query.setPermissionType(req.getPermissionType());
        DashboardAuthQueryHelper.fillAuthQuery(query);
        query.setIds(req.getIds());
        SettleInThreeMonSumRSP settleInThreeRsp = new SettleInThreeMonSumRSP();
        List<DashboardProjectInfoSettleInThreeMonthResult> dbList = dashboardProjectInfoMapper.listSettleInThreeMonth(query);
        if (CollectionUtil.isEmpty(dbList)) {
            List<DashboardProjectInfoSettleInThreeMonthRSP> empty = new ArrayList<>();
            settleInThreeRsp.setList(empty);
            settleInThreeRsp.setSumData(sumData);
            return settleInThreeRsp;
        }
        List<DashboardProjectInfoSettleInThreeMonthRSP> rspList = this.buildRspList(dbList, dbResult -> {
            DashboardProjectInfoSettleInThreeMonthRSP rsp = new DashboardProjectInfoSettleInThreeMonthRSP();
            rsp.setContractId(dbResult.getContractId());
            rsp.setProjName(dbResult.getClientName());
            rsp.setContractCode(dbResult.getContractCode());
            rsp.setLastRentPlanCollectionDate(dbResult.getLastRentPlanDate());
            rsp.setDeadline(dbResult.getLastRentPlanDate());
            if (Objects.nonNull(dbResult.getLastRentPlanDate())) {
                long m = LocalDateTimeUtil.between(LocalDate.now().atStartOfDay(), dbResult.getLastRentPlanDate().atStartOfDay(), ChronoUnit.MONTHS);
                rsp.setRemainingDuration(new ValueUnitDTO(String.valueOf(m < 0 ? 0 : m), "月"));
            }
            long earnestBalance = Optional.ofNullable(dbResult.getEarnestBalance()).orElse(0L);
            long collectionPrincipal = Optional.ofNullable(dbResult.getTotalCollectionPrincipal()).orElse(0L);
            long collectionInterest = Optional.ofNullable(dbResult.getTotalCollectionInterest()).orElse(0L);
            long planPrincipal = Optional.ofNullable(dbResult.getTotalPlanPrincipal()).orElse(0L);
            long planInterest = Optional.ofNullable(dbResult.getTotalPlanInterest()).orElse(0L);
            rsp.setCollectionAmount(new ValueUnitDTO(Util.toYuanWithoutSplit(collectionInterest + collectionPrincipal), "元"));
            rsp.setRemainingAmount(new ValueUnitDTO(Util.toYuanWithoutSplit(planPrincipal + planInterest - collectionPrincipal - collectionInterest), "元"));
            rsp.setEarnestBalanceAmount(new ValueUnitDTO(Util.toYuanWithoutSplit(earnestBalance), "元"));
            rsp.setCollectionPrincipalAmount(new ValueUnitDTO(Util.toYuanWithoutSplit(collectionPrincipal), "元"));
            rsp.setCollectionInterestAmount(new ValueUnitDTO(Util.toYuanWithoutSplit(collectionInterest), "元"));
            rsp.setPrincipalBalanceAmount(new ValueUnitDTO(Util.toYuanWithoutSplit(planPrincipal - collectionPrincipal), "元"));
            rsp.setInterestBalanceAmount(new ValueUnitDTO(Util.toYuanWithoutSplit(planInterest - collectionInterest), "元"));
            return rsp;
        });
        this.fillGuarantorInfo(rspList);
        //数量/金融 合计
        BigDecimal collectionAmount = rspList.stream().filter(c -> ObjectUtil.isNotEmpty(c.getCollectionAmount()))
                .map(e -> new BigDecimal(ObjectUtil.isNotEmpty(e.getCollectionAmount().getValue()) ? e.getCollectionAmount().getValue() : "0"))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal remainingAmount = rspList.stream().filter(c -> ObjectUtil.isNotEmpty(c.getRemainingAmount()))
                .map(e -> new BigDecimal(ObjectUtil.isNotEmpty(e.getRemainingAmount().getValue()) ? e.getRemainingAmount().getValue() : "0"))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal earnestBalanceAmount = rspList.stream().filter(c -> ObjectUtil.isNotEmpty(c.getEarnestBalanceAmount()))
                .map(e -> new BigDecimal(ObjectUtil.isNotEmpty(e.getEarnestBalanceAmount().getValue()) ? e.getEarnestBalanceAmount().getValue() : "0"))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal collectionPrincipalAmount = rspList.stream().filter(c -> ObjectUtil.isNotEmpty(c.getCollectionPrincipalAmount()))
                .map(e -> new BigDecimal(ObjectUtil.isNotEmpty(e.getCollectionPrincipalAmount().getValue()) ? e.getCollectionPrincipalAmount().getValue() : "0"))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal collectionInterestAmount = rspList.stream().filter(c -> ObjectUtil.isNotEmpty(c.getCollectionInterestAmount()))
                .map(e -> new BigDecimal(ObjectUtil.isNotEmpty(e.getCollectionInterestAmount().getValue()) ? e.getCollectionInterestAmount().getValue() : "0"))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal principalBalanceAmount = rspList.stream().filter(c -> ObjectUtil.isNotEmpty(c.getPrincipalBalanceAmount()))
                .map(e -> new BigDecimal(ObjectUtil.isNotEmpty(e.getPrincipalBalanceAmount().getValue()) ? e.getPrincipalBalanceAmount().getValue() : "0"))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal interestBalanceAmount = rspList.stream().filter(c -> ObjectUtil.isNotEmpty(c.getInterestBalanceAmount()))
                .map(e -> new BigDecimal(ObjectUtil.isNotEmpty(e.getInterestBalanceAmount().getValue()) ? e.getInterestBalanceAmount().getValue() : "0"))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        sumData.setCollectionAmount(collectionAmount);
        sumData.setRemainingAmount(remainingAmount);
        sumData.setEarnestBalanceAmount(earnestBalanceAmount);
        sumData.setCollectionPrincipalAmount(collectionPrincipalAmount);
        sumData.setCollectionInterestAmount(collectionInterestAmount);
        sumData.setPrincipalBalanceAmount(principalBalanceAmount);
        sumData.setInterestBalanceAmount(interestBalanceAmount);
        settleInThreeRsp.setList(rspList);
        settleInThreeRsp.setSumData(sumData);
        return settleInThreeRsp;
    }

    public OverDueSumRSP listOverdue(DashboardProjectInfoOverdueREQ req) {
        OverDueSumRSP overDueRsp = new OverDueSumRSP();
        OverDueSumRSP.SumData sumData = new OverDueSumRSP.SumData();
        DashboardProjectInfoOverdueQuery query = new DashboardProjectInfoOverdueQuery();
        query.setAccountVO(AccountUtil.getLoginInfo());
        query.setClientId(req.getClientId());
        query.setContractCode(req.getContractCode());
        query.setProjName(req.getProjName());
        query.setBizDeptId(req.getBizDeptId());
        query.setProjSponsorUserId(req.getProjSponsorUserId());
        query.setPermissionType(req.getPermissionType());
        DashboardAuthQueryHelper.fillAuthQuery(query);
        query.setIds(req.getIds());
        List<DashboardProjectInfoOverdueResult> dbList = dashboardProjectInfoMapper.listOverdue(query);
        if (CollectionUtil.isEmpty(dbList)) {
            List<DashboardProjectInfoOverdueRSP> empty = new ArrayList<>();
            overDueRsp.setList(empty);
            overDueRsp.setSumData(sumData);
            return overDueRsp;
        }
        List<DashboardProjectInfoOverdueRSP> rspList = this.buildRspList(dbList, dbResult -> {
            DashboardProjectInfoOverdueRSP rsp = new DashboardProjectInfoOverdueRSP();
            rsp.setContractId(dbResult.getContractId());
            rsp.setContractCode(dbResult.getContractCode());
            rsp.setCashFlowCode(dbResult.getCashFlowCode());
            rsp.setPhase(dbResult.getPhase());
            long overdueDays = LocalDateTimeUtil.between(dbResult.getPlanCollectionDate().atStartOfDay(), LocalDate.now().atStartOfDay(), ChronoUnit.DAYS);
            rsp.setOverdueDuration(new ValueUnitDTO(String.valueOf(overdueDays), "天"));
            long overdueAmount = Optional.ofNullable(dbResult.getPlanCollectionAmount()).orElse(0L) - Optional.ofNullable(dbResult.getCollectionAmount()).orElse(0L);
            rsp.setOverdueAmount(new ValueUnitDTO(Util.toYuanWithoutSplit(overdueAmount), "元"));
            if (Objects.nonNull(dbResult.getDefaultInterestRate())) {
                BigDecimal b = BigDecimal.valueOf(dbResult.getDefaultInterestRate()).divide(BigDecimal.valueOf(10000L), 2, RoundingMode.HALF_UP);
                rsp.setInterestPenaltyDailyRate(new ValueUnitDTO(b.toPlainString(), "%"));
            }
            long penaltyInterestAmount = Optional.ofNullable(dbResult.getPenaltyInterestAmount()).orElse(0L);
            rsp.setInterestPenaltyAmount(new ValueUnitDTO(Util.toYuanWithoutSplit(penaltyInterestAmount), "元"));
            long penaltyInterestDeductionAmount = Optional.ofNullable(dbResult.getPenaltyInterestDeductionAmount()).orElse(0L);
            rsp.setInterestPenaltyReduceAmount(new ValueUnitDTO(Util.toYuanWithoutSplit(penaltyInterestDeductionAmount), "元"));
            return rsp;
        });
        this.fillGuarantorInfo(rspList);
        //数量/金额 合计
        BigDecimal overdueAmount = rspList.stream().filter(c -> ObjectUtil.isNotEmpty(c.getOverdueAmount()))
                .map(e -> new BigDecimal(ObjectUtil.isNotEmpty(e.getOverdueAmount().getValue()) ? e.getOverdueAmount().getValue() : "0"))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal interestPenaltyAmount = rspList.stream().filter(c -> ObjectUtil.isNotEmpty(c.getInterestPenaltyAmount()))
                .map(e -> new BigDecimal(ObjectUtil.isNotEmpty(e.getInterestPenaltyAmount().getValue()) ? e.getInterestPenaltyAmount().getValue() : "0"))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal interestPenaltyReduceAmount = rspList.stream().filter(c -> ObjectUtil.isNotEmpty(c.getInterestPenaltyReduceAmount()))
                .map(e -> new BigDecimal(ObjectUtil.isNotEmpty(e.getInterestPenaltyReduceAmount().getValue()) ? e.getInterestPenaltyReduceAmount().getValue() : "0"))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        sumData.setOverdueAmount(overdueAmount);
        sumData.setInterestPenaltyAmount(interestPenaltyAmount);
        sumData.setInterestPenaltyReduceAmount(interestPenaltyReduceAmount);
        overDueRsp.setList(rspList);
        overDueRsp.setSumData(sumData);
        return overDueRsp;
    }

    public RentThisMonthSumRSP listRentThisMonth(DashboardProjectInfoRentThisMonthREQ req) {
        RentThisMonthSumRSP rentThisMonthRsp = new RentThisMonthSumRSP();
        RentThisMonthSumRSP.SumData sumData = new RentThisMonthSumRSP.SumData();
        DashboardProjectInfoRentThisMonthQuery query = new DashboardProjectInfoRentThisMonthQuery();
        query.setAccountVO(AccountUtil.getLoginInfo());
        query.setProjName(req.getProjName());
        query.setContractCode(req.getContractCode());
        query.setPlanCollectionDateFrom(req.getPlanCollectionDateFrom());
        query.setPlanCollectionDateTo(req.getPlanCollectionDateTo());
        query.setBizDeptId(req.getBizDeptId());
        query.setProjSponsorUserId(req.getProjSponsorUserId());
        query.setPermissionType(req.getPermissionType());
        DashboardAuthQueryHelper.fillAuthQuery(query);
        query.setIds(req.getIds());
        List<DashboardProjectInfoRentThisMonthResult> dbList = dashboardProjectInfoMapper.listRentThisMonth(query);
        if (CollectionUtil.isEmpty(dbList)) {
            List<DashboardProjectInfoRentThisMonthRSP> emptyList = new ArrayList<>();
            rentThisMonthRsp.setList(emptyList);
            rentThisMonthRsp.setSumData(sumData);
            return rentThisMonthRsp;
        }
        List<DashboardProjectInfoRentThisMonthRSP> rspList = this.buildRspList(dbList, dbResult -> {
            DashboardProjectInfoRentThisMonthRSP rsp = new DashboardProjectInfoRentThisMonthRSP();
            rsp.setContractId(dbResult.getContractId());
            rsp.setContractCode(dbResult.getContractCode());
            rsp.setCashFlowCode(dbResult.getCashFlowCode());
            rsp.setPhase(dbResult.getPhase());
            long planRent = Optional.ofNullable(dbResult.getPlanCollectionAmount()).orElse(0L);
            long planPrincipal = Optional.ofNullable(dbResult.getPrincipal()).orElse(0L);
            long planInterest = Optional.ofNullable(dbResult.getInterest()).orElse(0L);
            long collectionRent = Optional.ofNullable(dbResult.getCollectionAmount()).orElse(0L);
            long collectionPrincipal = Optional.ofNullable(dbResult.getCollectionPrincipal()).orElse(0L);
            long collectionInterest = Optional.ofNullable(dbResult.getCollectionInterest()).orElse(0L);
            long totalPay = Optional.ofNullable(dbResult.getTotalPay()).orElse(0L);
            rsp.setRent(new ValueUnitDTO(Util.toYuanWithoutSplit(planRent), "元"));
            rsp.setCreditAmount(new ValueUnitDTO(Util.toYuanWithoutSplit(totalPay), "元"));
            rsp.setPrincipalAmount(new ValueUnitDTO(Util.toYuanWithoutSplit(planPrincipal), "元"));
            rsp.setInterestAmount(new ValueUnitDTO(Util.toYuanWithoutSplit(planInterest), "元"));
            rsp.setBalanceAmount(new ValueUnitDTO(Util.toYuanWithoutSplit(planRent - collectionRent), "元"));
            rsp.setPrincipalBalanceAmount(new ValueUnitDTO(Util.toYuanWithoutSplit(planPrincipal - collectionPrincipal), "元"));
            rsp.setInterestBalanceAmount(new ValueUnitDTO(Util.toYuanWithoutSplit(planInterest - collectionInterest), "元"));
            rsp.setCollectionPrincipalAmount(new ValueUnitDTO(Util.toYuanWithoutSplit(collectionPrincipal), "元"));
            rsp.setCollectionInterestAmount(new ValueUnitDTO(Util.toYuanWithoutSplit(collectionInterest), "元"));
            rsp.setCollectionRentAmount(new ValueUnitDTO(Util.toYuanWithoutSplit(collectionPrincipal + collectionInterest), "元"));
            rsp.setPlanCollectionDate(dbResult.getPlanCollectionDate());
            rsp.setActualCollectionDate(dbResult.getCollectionDate());
            if (Objects.nonNull(dbResult.getPlanCollectionDate())) {
                // 有实际还款日期的用实际还款日期，没有的用今天
                LocalDate targetDate = Objects.nonNull(dbResult.getCollectionDate()) ? dbResult.getCollectionDate() : LocalDate.now();
                if (targetDate.isAfter(dbResult.getPlanCollectionDate())) {
                    rsp.setIsOverdue(YesOrNoNumberEnum.YES.getCode());
                } else {
                    rsp.setIsOverdue(YesOrNoNumberEnum.NO.getCode());
                }
            }
            return rsp;
        });
        this.fillGuarantorInfo(rspList);
        //数量/金额 合计
        DashboardProjectInfoRentThisMonthQuery allQuery = new DashboardProjectInfoRentThisMonthQuery();
        DashboardAuthQueryHelper.fillAuthQuery(allQuery);
        List<DashboardProjectInfoRentThisMonthResult> allList = dashboardProjectInfoMapper.listRentThisMonth(allQuery);
        List<DashboardProjectInfoRentThisMonthRSP> totalList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(allList)) {
            totalList = this.buildRspList(dbList, dbResult -> {
                DashboardProjectInfoRentThisMonthRSP rsp = new DashboardProjectInfoRentThisMonthRSP();
                long planRent = Optional.ofNullable(dbResult.getPlanCollectionAmount()).orElse(0L);
                long planPrincipal = Optional.ofNullable(dbResult.getPrincipal()).orElse(0L);
                long planInterest = Optional.ofNullable(dbResult.getInterest()).orElse(0L);
                long collectionRent = Optional.ofNullable(dbResult.getCollectionAmount()).orElse(0L);
                long collectionPrincipal = Optional.ofNullable(dbResult.getCollectionPrincipal()).orElse(0L);
                long collectionInterest = Optional.ofNullable(dbResult.getCollectionInterest()).orElse(0L);
                long totalPay = Optional.ofNullable(dbResult.getTotalPay()).orElse(0L);

                rsp.setCreditAmount(new ValueUnitDTO(Util.toYuanWithoutSplit(totalPay), "元"));
                rsp.setRent(new ValueUnitDTO(Util.toYuanWithoutSplit(planRent), "元"));
                rsp.setPrincipalAmount(new ValueUnitDTO(Util.toYuanWithoutSplit(planPrincipal), "元"));
                rsp.setInterestAmount(new ValueUnitDTO(Util.toYuanWithoutSplit(planInterest), "元"));
                rsp.setBalanceAmount(new ValueUnitDTO(Util.toYuanWithoutSplit(planRent - collectionRent), "元"));
                rsp.setPrincipalBalanceAmount(new ValueUnitDTO(Util.toYuanWithoutSplit(planPrincipal - collectionPrincipal), "元"));
                rsp.setInterestBalanceAmount(new ValueUnitDTO(Util.toYuanWithoutSplit(planInterest - collectionInterest), "元"));
                rsp.setCollectionPrincipalAmount(new ValueUnitDTO(Util.toYuanWithoutSplit(collectionPrincipal), "元"));
                rsp.setCollectionInterestAmount(new ValueUnitDTO(Util.toYuanWithoutSplit(collectionInterest), "元"));
                rsp.setCollectionRentAmount(new ValueUnitDTO(Util.toYuanWithoutSplit(collectionPrincipal + collectionInterest), "元"));
                rsp.setPlanCollectionDate(dbResult.getPlanCollectionDate());
                rsp.setActualCollectionDate(dbResult.getCollectionDate());
                return rsp;
            });
        }
        BigDecimal principalAmount = rspList.stream().filter(c -> ObjectUtil.isNotEmpty(c.getPrincipalAmount()))
                .map(e -> new BigDecimal(ObjectUtil.isNotEmpty(e.getPrincipalAmount().getValue()) ? e.getPrincipalAmount().getValue() : "0"))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal balanceAmount = rspList.stream().filter(c -> ObjectUtil.isNotEmpty(c.getBalanceAmount()))
                .map(e -> new BigDecimal(ObjectUtil.isNotEmpty(e.getBalanceAmount().getValue()) ? e.getBalanceAmount().getValue() : "0"))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal currentPrincipal = rspList.stream().filter(c -> ObjectUtil.isNotEmpty(c.getPrincipalAmount()))
                .map(e -> new BigDecimal(ObjectUtil.isNotEmpty(e.getPrincipalAmount().getValue()) ? e.getPrincipalAmount().getValue() : "0"))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal interestAmount = rspList.stream().filter(c -> ObjectUtil.isNotEmpty(c.getInterestAmount()))
                .map(e -> new BigDecimal(ObjectUtil.isNotEmpty(e.getInterestAmount().getValue()) ? e.getInterestAmount().getValue() : "0"))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        sumData.setPrincipalAmount(principalAmount);
        sumData.setBalanceAmount(balanceAmount);
        sumData.setCurrentPrincipal(currentPrincipal);
        sumData.setInterestAmount(interestAmount);
        rentThisMonthRsp.setList(rspList);
        rentThisMonthRsp.setSumData(sumData);
        return rentThisMonthRsp;
    }

    public List<DashboardProjectPayNoSettleRSP> listPayNoSettle(DashboardProjectPayNoSettleREQ req) {
        DashboardProjectPayNoSettleQuery query = new DashboardProjectPayNoSettleQuery();
        query.setProjName(req.getProjName());
        query.setClientId(req.getClientId());
        query.setContractCode(req.getContractCode());
        query.setBizDeptId(req.getBizDeptId());
        query.setProjSponsorUserId(req.getProjSponsorUserId());
        query.setRegionalProjectClassifyCode(req.getRegionalProjectClassifyCode());
        DashboardAuthQueryHelper.fillAuthQuery(query);
        query.setIds(req.getIds());
        List<DashboardProjectPayNoSettleResult> dbList = dashboardProjectInfoMapper.listPayNoSettle(query);
        if (CollectionUtil.isEmpty(dbList)) {
            return Collections.emptyList();
        }
        List<DashboardProjectPayNoSettleRSP> rspList = this.buildRspList(dbList, dbResult -> {
            DashboardProjectPayNoSettleRSP rsp = new DashboardProjectPayNoSettleRSP();
            rsp.setMainId(dbResult.getContractId());
            rsp.setContractId(dbResult.getContractId());
            rsp.setActualPayAmount(new ValueUnitDTO(Util.toYuanWithoutSplit(dbResult.getTotalPayAmount()), "元"));
            long exposure = dbResult.getTotalPayAmount() - Optional.ofNullable(dbResult.getActualFirstRentAmount()).orElse(0L) - Optional.ofNullable(dbResult.getActualPrincipalAmount()).orElse(0L);
            rsp.setStockRiskExposure(new ValueUnitDTO(Util.toYuanWithoutSplit(exposure), "元"));
            rsp.setActualLeaseDate(dbResult.getActualLeaseDate());
            long balance = Optional.ofNullable(dbResult.getPlanRentAmount()).orElse(0L) - Optional.ofNullable(dbResult.getActualRentAmount()).orElse(0L);
            rsp.setBalanceAmount(new ValueUnitDTO(Util.toYuanWithoutSplit(balance), "元"));
            long balancePrincipal = Optional.ofNullable(dbResult.getPlanPrincipalAmount()).orElse(0L) - Optional.ofNullable(dbResult.getActualPrincipalAmount()).orElse(0L);
            rsp.setPrincipalBalanceAmount(new ValueUnitDTO(Util.toYuanWithoutSplit(balancePrincipal), "元"));
            long balanceInterest = Optional.ofNullable(dbResult.getPlanInterestAmount()).orElse(0L) - Optional.ofNullable(dbResult.getActualInterestAmount()).orElse(0L);
            rsp.setInterestBalanceAmount(new ValueUnitDTO(Util.toYuanWithoutSplit(balanceInterest), "元"));
            rsp.setEarnestAmount(new ValueUnitDTO(Util.toYuanWithoutSplit(Optional.ofNullable(dbResult.getEarnestMoney()).orElse(0L)), "元"));
            rsp.setActualPayMonth(dbResult.getMinPayDate());
            if (Objects.nonNull(dbResult.getDuration())) {
                rsp.setLeaseDuration(new ValueUnitDTO(dbResult.getDuration().toString(), "月"));
            }
            if (Objects.nonNull(dbResult.getDeadline())) {
                long remainingDuration = LocalDateTimeUtil.between(LocalDate.now().atStartOfDay(), dbResult.getDeadline().atStartOfDay(), ChronoUnit.MONTHS);
                if (remainingDuration < 0) {
                    remainingDuration = 0L;
                }
                rsp.setRemainingLeaseDuration(new ValueUnitDTO(String.valueOf(remainingDuration), "月"));
            }
            if (Objects.nonNull(dbResult.getEstimateIrr())) {
                BigDecimal b = BigDecimal.valueOf(dbResult.getEstimateIrr()).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP);
                rsp.setIrr(new ValueUnitDTO(b.toPlainString(), "%"));
            }
            rsp.setRegionalProjectClassifyCode(dbResult.getRegionalProjectClassify());
            rsp.setRegionalProjectClassifyDisplay(Optional.ofNullable(ProjRegionalClassify.of(dbResult.getRegionalProjectClassify())).map(ProjRegionalClassify::display).orElse(""));
            rsp.setIsRelated(dbResult.getIsRelated());
            rsp.setContractCode(dbResult.getContractCode());
            rsp.setRiskControlIndustryClassifyCode(dbResult.getRiskControlIndustryClassifyCode());
            rsp.setRiskControlIndustryClassifyDisplay(Optional.ofNullable(RiskControlIndustryClassify.findByName(dbResult.getRiskControlIndustryClassifyCode())).map(RiskControlIndustryClassify::display).orElse(""));
            return rsp;
        });
        this.fillGuarantorInfo(rspList);
        return rspList;
    }

    public List<DashboardProjectProvisionRSP> listProvision(DashboardProjectProvisionREQ req) {
        DashboardProvisionQuery query = new DashboardProvisionQuery();
        query.setClientId(req.getClientId());
        query.setBizDeptId(req.getBizDeptId());
        query.setContractCode(req.getContractCode());
        DashboardAuthQueryHelper.fillAuthQuery(query);
        query.setIds(req.getIds());
        List<DashboardProvisionResult> dbList = dashboardProjectInfoMapper.listProvision(query);
        if (CollectionUtil.isEmpty(dbList)) {
            return Collections.emptyList();
        }
        return dbList.stream().map(dbResult -> {
            DashboardProjectProvisionRSP rsp = new DashboardProjectProvisionRSP();
            rsp.setClientId(dbResult.getClientId());
            rsp.setClientName(dbResult.getClientName());
            rsp.setContractId(dbResult.getContractId());
            rsp.setContractCode(dbResult.getContractCode());
            if (Objects.nonNull(dbResult.getContractAmount())) {
                rsp.setContractAmount(new ValueUnitDTO(Util.toYuanWithoutSplit(dbResult.getContractAmount()), "元"));
            }
            if (Objects.nonNull(dbResult.getRemainingPrincipal())) {
                rsp.setPrincipalBalance(new ValueUnitDTO(Util.toYuanWithoutSplit(dbResult.getRemainingPrincipal()), "元"));
            }
            if (Objects.nonNull(dbResult.getEarnestBalance())) {
                rsp.setEarnest(new ValueUnitDTO(Util.toYuanWithoutSplit(dbResult.getEarnestBalance()), "元"));
            }
            if (Objects.nonNull(dbResult.getExposure())) {
                rsp.setStockRiskExposure(new ValueUnitDTO(Util.toYuanWithoutSplit(dbResult.getExposure()), "元"));
            }
            if (Objects.nonNull(dbResult.getDeadline())) {
                LocalDate now = LocalDate.now();
                long days = LocalDateTimeUtil.between(now.atStartOfDay(), dbResult.getDeadline().atStartOfDay(), ChronoUnit.DAYS);
                long months = LocalDateTimeUtil.between(now.atStartOfDay(), dbResult.getDeadline().atStartOfDay(), ChronoUnit.MONTHS);
                rsp.setRemainingDurationMonths(String.valueOf(Math.max(0, months)));
                rsp.setRemainingDurationYears(BigDecimal.valueOf(days).divide(BigDecimal.valueOf(365), 2, RoundingMode.HALF_UP).toPlainString());
            }
            if (Objects.nonNull(dbResult.getProvisionBalance())) {
                rsp.setProvision(new ValueUnitDTO(Util.toYuanWithoutSplit(dbResult.getProvisionBalance()), "元"));
            }
            rsp.setBizTypeCode(dbResult.getBizTypeCode());
            rsp.setBizTypeDisplay(Optional.ofNullable(ProjectBizType.of(dbResult.getBizTypeCode())).map(ProjectBizType::display).orElse(""));
            rsp.setProjectClassifyCode(dbResult.getProjClassify());
            rsp.setProjectClassifyDisplay(Optional.ofNullable(KpiProjectClassifyEnum.find(dbResult.getProjClassify())).map(KpiProjectClassifyEnum::display).orElse(""));
            rsp.setDeadline(dbResult.getDeadline());
            rsp.setAssetsClassifyCode(dbResult.getRiskLevel());
            rsp.setAssetsClassifyDisplay(Optional.ofNullable(AssetClassifyResultEnum.of(dbResult.getRiskLevel())).map(AssetClassifyResultEnum::display).orElse(""));
            if (Objects.nonNull(dbResult.getWithdrawalRatio())) {
                BigDecimal b = BigDecimal.valueOf(dbResult.getWithdrawalRatio()).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP);
                rsp.setProvisionRate(new ValueUnitDTO(b.toPlainString(), "%"));
            }
            rsp.setBizDeptId(dbResult.getBizDeptId());
            rsp.setBizDeptName(dbResult.getBizDeptName());
            return rsp;
        }).collect(Collectors.toList());
    }

    public List<DashboardProjectPledgeRSP> listPledge(DashboardProjectPledgeREQ req) {
        DashboardProjectPledgeQuery query = BeanUtil.copyProperties(req, DashboardProjectPledgeQuery.class);
        DashboardAuthQueryHelper.fillAuthQuery(query);
        List<DashboardProjectPledgeResult> tempList = dashboardProjectInfoMapper.listProjPledge(query);
        this.replacePledgeBankInfo(tempList);
        List<DashboardProjectPledgeResult> resultList = filterNoPledge(tempList);
        List<DashboardProjectPledgeRSP> rspList = resultList.stream().map(result -> {
            DashboardProjectPledgeRSP rsp = new DashboardProjectPledgeRSP();
            rsp.setBusinessType(result.getBusinessType());
            rsp.setContractCode(result.getContractCode());
            rsp.setProjName(result.getProjName());
            rsp.setFinancingCode(result.getFinancingCode());
            rsp.setFinancingStatus(result.getFinancingStatus());
            rsp.setOrgName(result.getOrgName());
            rsp.setType(result.getType());
            rsp.setContractId(result.getContractId());
            rsp.setFinancingId(result.getFinancingId());
            rsp.setPledgeStatus(result.getIsPledge() == 1 ? (result.getIsSupervise() == 1 ? DashboardPledgeTypeEnum.PLEDGE_SUPERVISE.name() : DashboardPledgeTypeEnum.PLEDGE.name()) :
                    (result.getIsSupervise() == 1 ? DashboardPledgeTypeEnum.SUPERVISE.name() : DashboardPledgeTypeEnum.NEITHER.name()));
            long residualRent = result.getPlanCollectionAmount() - result.getCollectionAmount();
            long remainingPrincipal = result.getPrincipal() - result.getCollectionPrincipal();
            rsp.setRemainingPrincipal(new ValueUnitDTO(Util.toYuanWithoutSplit(remainingPrincipal > 0 ? remainingPrincipal : 0L), "元"));
            rsp.setResidualRent(new ValueUnitDTO(Util.toYuanWithoutSplit(residualRent > 0 ? residualRent : 0L), "元"));
            rsp.setTotalPayAmount(new ValueUnitDTO(Util.toYuanWithoutSplit(result.getTotalPayAmount()), "元"));
            rsp.setAccountName(result.getAccountName());
            rsp.setAccountBank(result.getAccountBank());
            rsp.setAccountNumber(result.getAccountNumber());
            return rsp;
        }).collect(Collectors.toList());

        return rspList;
    }


    private List<DashboardProjectPledgeResult> filterNoPledge(List<DashboardProjectPledgeResult> tempList) {
        Map<String, List<DashboardProjectPledgeResult>> map = new HashMap<>();
        for (DashboardProjectPledgeResult result : tempList) {
            map.putIfAbsent(result.getContractCode(), new ArrayList<>());
            map.get(result.getContractCode()).add(result);
        }
        List<DashboardProjectPledgeResult> resultList = new ArrayList<>();
        for (Map.Entry<String, List<DashboardProjectPledgeResult>> entry : map.entrySet()) {
            List<DashboardProjectPledgeResult> list = entry.getValue();
            if (list != null && !list.isEmpty() && list.size() > 1) {
                Map<Long, List<DashboardProjectPledgeResult>> resMap = new HashMap<>();
                Map<Long, List<DashboardProjectPledgeResult>> neitherMap = new HashMap<>();
                for (DashboardProjectPledgeResult result : list) {
                    if (result.getIsPledge() == 0 && result.getIsSupervise() == 0) {
                        neitherMap.putIfAbsent(result.getContractId(), new ArrayList<>());
                        neitherMap.get(result.getContractId()).add(result);
                    } else {
                        resMap.putIfAbsent(result.getContractId(), new ArrayList<>());
                        resMap.get(result.getContractId()).add(result);
                    }
                }
                if (!resMap.isEmpty()) {
                    for (Map.Entry<Long, List<DashboardProjectPledgeResult>> resEntry : resMap.entrySet()) {
                        resultList.addAll(resEntry.getValue());
                    }
                } else if (resMap.isEmpty() && !neitherMap.isEmpty()) {
                    for (Map.Entry<Long, List<DashboardProjectPledgeResult>> resEntry : neitherMap.entrySet()) {
                        boolean find = false;
                        for (DashboardProjectPledgeResult temp : resEntry.getValue()) {
                            if (StringUtils.isBlank(temp.getFinancingStatus()) && StringUtils.isBlank(temp.getFinancingCode())) {
                                find = true;
                                resultList.add(temp);
                                break;
                            }
                        }
                        if (!find) {
                            resultList.add(resEntry.getValue().get(0));
                        }
                    }
                }
            } else {
                resultList.addAll(list);
            }
        }
        return resultList;
    }
}
