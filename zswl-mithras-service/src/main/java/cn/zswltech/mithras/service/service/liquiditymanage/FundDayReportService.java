package cn.zswltech.mithras.service.service.liquiditymanage;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailRSP;
import cn.zswltech.mithras.dto.liquiditymanage.dayReport.*;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.fund.domain.enums.financing.FinancingTypeEnum;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.financing.FundFinancingBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.model.basedata.BaseDataBankAccount;
import cn.zswltech.mithras.service.mapper.model.basedata.BaseDataSpecialDate;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractTenantry;
import cn.zswltech.mithras.fund.infrastructure.persistence.mapper.model.FundOrganization;
import cn.zswltech.mithras.service.mapper.model.liquiditymanage.AccountBalanceBaseInfo;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.basedata.BaseDataBankAccountService;
import cn.zswltech.mithras.service.service.basedata.BaseDataSpecialDateService;
import cn.zswltech.mithras.service.service.collection.CollectionBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractTenantryService;
import cn.zswltech.mithras.service.service.fund.FundOrganizationService;
import cn.zswltech.mithras.fund.application.liquiditymanage.dto.RepayPrincipalInterestDto;
import cn.zswltech.mithras.service.service.monthly.MonthlyManagementBaseInfoService;
import cn.zswltech.mithras.service.service.projlifecycle.ProjectLifecycleService;
import cn.zswltech.mithras.service.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author bigbear
 * @date 2024/12/17 19:15
 * @description
 */
@Slf4j
@Service
public class FundDayReportService {

    @Resource
    private Id2NameService id2NameService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private AccountBalanceBaseInfoService accountBalanceBaseInfoService;
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private ContractTenantryService contractTenantryService;
    @Resource
    private BaseDataSpecialDateService baseDataSpecialDateService;
    @Resource
    private BaseDataBankAccountService baseDataBankAccountService;
    @Resource
    private FundFinancingBaseInfoMapper fundFinancingBaseInfoMapper;
    @Resource
    private FundOrganizationService fundOrganizationService;
    @Resource
    private MonthlyManagementBaseInfoService monthlyManagementBaseInfoService;
    @Resource
    private ProjectLifecycleService projectLifecycleService;

    public DayReportIndicatorListRSP dayReportIndicatorList(DayReportIndicatorListREQ req) {
        // 当日数据与昨日数据做计算
        LocalDate today = LocalDate.now();
        List<AccountBalanceBaseInfo> accountBalanceBaseInfos = accountBalanceBaseInfoService.list(Wrappers.<AccountBalanceBaseInfo>lambdaQuery()
                .between(AccountBalanceBaseInfo::getDate, today.minusDays(1), today));
        DayReportIndicatorListRSP rsp = new DayReportIndicatorListRSP();
        if (CollUtil.isEmpty(accountBalanceBaseInfos)) {
            rsp.setDate(today.format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
            return rsp;
        }
        // 先将数据分成昨天和今天的数据
        List<AccountBalanceBaseInfo> yesterdayList = accountBalanceBaseInfos.stream().filter(a -> a.getDate().equals(today.minusDays(1))).collect(Collectors.toList());
        List<AccountBalanceBaseInfo> todayList = accountBalanceBaseInfos.stream().filter(a -> a.getDate().equals(today)).collect(Collectors.toList());
        // 开始计算结果并返回
        // 日初余额 逻辑：账户明细表中上一日全部账户结余（实际）之和 无结余（实际）则取结余（预估）
        long beginOfDayBalance = yesterdayList.stream().mapToLong(a -> a.getActualBalanceAmount() != null ?
                a.getActualBalanceAmount() : LongUtil.null2zero(a.getEstimateBalanceAmount())).summaryStatistics().getSum();
        rsp.setDawnOfDayBalance(beginOfDayBalance);

        List<BaseDataBankAccount> nonSuperviseAccountList = baseDataBankAccountService.listByIdsAndType(yesterdayList.stream().map(AccountBalanceBaseInfo::getAccountId).collect(Collectors.toList()), false);
        // 没有非监管账户的情况下默认为0
        if (CollUtil.isEmpty(nonSuperviseAccountList)) {
            rsp.setDawnOfDayNonSupervisionBalance(0L);
            rsp.setDawnOfDayNonRestrictedBalance(0L);
            rsp.setEndOfDayNonSupervisionBalance(0L);
            rsp.setEndOfDayNonRestrictedBalance(0L);
        } else {
            // 日初非监管户余额 逻辑：账户明细表中上一日账户性质=非监管户的结余（实际）之和 无结余（实际）则取结余（预估）
            List<Long> accountIds = nonSuperviseAccountList.stream().map(BaseDataBankAccount::getId).collect(Collectors.toList());
            long beginOfDayNonSuperviseBalance = yesterdayList.stream().filter(a -> accountIds.contains(a.getAccountId()))
                    .mapToLong(a -> a.getActualBalanceAmount() != null ? a.getActualBalanceAmount() : LongUtil.null2zero(a.getEstimateBalanceAmount())).summaryStatistics().getSum();
            rsp.setDawnOfDayNonSupervisionBalance(beginOfDayNonSuperviseBalance);
            // 日初非受限余额 逻辑：账户明细表中上一日账户性质=非监管户的结余（实际）-结余受限（预估）之和 无结余（实际）则取结余（预估）
            long beginOfDayNonLimitedBalance = yesterdayList.stream().filter(a -> accountIds.contains(a.getAccountId()))
                    .mapToLong(a -> {
                        Long l = Objects.isNull(a.getEstimateBalanceLimitEditAmount()) ? LongUtil.null2zero(a.getEstimateBalanceLimitAmount()) : LongUtil.null2zero(a.getEstimateBalanceLimitEditAmount());
                        return a.getActualBalanceAmount() != null ? LongUtil.null2zero(a.getActualBalanceAmount()) - l : LongUtil.null2zero(a.getEstimateBalanceAmount()) - l;
                    }).summaryStatistics().getSum();
            rsp.setDawnOfDayNonRestrictedBalance(beginOfDayNonLimitedBalance);

            // 日终非监管户余额 逻辑：账户明细表中当日账户性质=非监管户的结余（实际）之和 无结余（实际）则取结余（预估）
            long endOfDayNonSuperviseBalance = todayList.stream().filter(a -> accountIds.contains(a.getAccountId()))
                    .mapToLong(a -> a.getActualBalanceAmount() != null ? LongUtil.null2zero(a.getActualBalanceAmount()) : LongUtil.null2zero(a.getEstimateBalanceAmount())).summaryStatistics().getSum();
            rsp.setEndOfDayNonSupervisionBalance(endOfDayNonSuperviseBalance);

            // 日终非受限余额 逻辑：账户明细表中当日账户性质=非监管户的结余（实际）-结余受限（预估）之和 无结余（实际）则取结余（预估）
            long endOfDayNonLimitedBalance = todayList.stream().filter(a -> accountIds.contains(a.getAccountId()))
                    .mapToLong(a -> {
                        Long l = Objects.isNull(a.getEstimateBalanceLimitEditAmount()) ? LongUtil.null2zero(a.getEstimateBalanceLimitAmount()) : LongUtil.null2zero(a.getEstimateBalanceLimitEditAmount());
                        return a.getActualBalanceAmount() != null ? LongUtil.null2zero(a.getActualBalanceAmount()) - l : LongUtil.null2zero(a.getEstimateBalanceAmount()) - l;
                    }).summaryStatistics().getSum();
            rsp.setEndOfDayNonRestrictedBalance(endOfDayNonLimitedBalance);
        }

        // 今日计划租金流入 逻辑：账户明细表中当日全部账户的租金流入之和
        long todayPlanRentIncome = todayList.stream().mapToLong(a -> LongUtil.null2zero(a.getRentReflowAmount())).summaryStatistics().getSum();
        rsp.setTodayPlanRentIncome(todayPlanRentIncome);

        // 今日计划还本付息 逻辑：账户明细表中当日全部账户的还本付息+还本付息（调整值）支出之和
        long todayPlanRepayIncome = todayList.stream().mapToLong(a -> LongUtil.null2zero(a.getRepayAmount()) + LongUtil.null2zero(a.getRepayEditAmount())).summaryStatistics().getSum();
        rsp.setTodayPlanRepayPrincipalInterest(todayPlanRepayIncome);

        // 日终余额 逻辑：账户明细表中当日全部账户结余（实际）之和 无结余（实际）则取结余（预估）
        long endOfDayBalance = todayList.stream().mapToLong(a -> a.getActualBalanceAmount() != null ?
                LongUtil.null2zero(a.getActualBalanceAmount()) : LongUtil.null2zero(a.getEstimateBalanceAmount())).summaryStatistics().getSum();
        rsp.setEndOfDayBalance(endOfDayBalance);
        rsp.setDate(LocalDate.now().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
        return rsp;
    }

    public AccountBalanceListRSP accountBalanceList(AccountBalanceListREQ req) {
        // 首先找到当前时间的所有账户余额记录
        List<AccountBalanceBaseInfo> accountBalanceBaseInfos = accountBalanceBaseInfoService.list(Wrappers.<AccountBalanceBaseInfo>lambdaQuery().eq(AccountBalanceBaseInfo::getDate, LocalDate.now().minusDays(1)));
        if (CollUtil.isEmpty(accountBalanceBaseInfos)) {
            return null;
        }

        AccountBalanceListRSP rsp = new AccountBalanceListRSP();
        List<AccountBalanceListRSP.AccountBalanceVo> supervisionList = new LinkedList<>();
        List<AccountBalanceListRSP.AccountBalanceVo> unSupervisionList = new LinkedList<>();
        // 然后找到所有的监管账户
        List<BaseDataBankAccount> supervisionAccountList = baseDataBankAccountService.listByIdsAndType(accountBalanceBaseInfos.stream().map(AccountBalanceBaseInfo::getAccountId).collect(Collectors.toList()), true);
        if (CollUtil.isEmpty(supervisionAccountList)) {
            rsp.setSupervisionAccountBalanceList(Collections.emptyList());
            // 剩下的全部是非监管账户
            accountBalanceBaseInfos.stream().collect(Collectors.groupingBy(AccountBalanceBaseInfo::getAccountBank))
                    .forEach((k, v) -> {
                        AccountBalanceListRSP.AccountBalanceVo vo = getAccountBalanceVo(v);
                        unSupervisionList.add(vo);
                    });
            rsp.setNonSupervisionAccountBalanceList(unSupervisionList.stream().sorted(Comparator.comparing(AccountBalanceListRSP.AccountBalanceVo::getAccountBalance).reversed()).collect(Collectors.toList()));
            return rsp;
        } else {
            List<Long> supervisionAccountIds = supervisionAccountList.stream().map(BaseDataBankAccount::getId).collect(Collectors.toList());
            accountBalanceBaseInfos.stream().filter(a -> supervisionAccountIds.contains(a.getAccountId()))
                    .collect(Collectors.groupingBy(AccountBalanceBaseInfo::getAccountBank)).forEach((k, v) -> {
                        AccountBalanceListRSP.AccountBalanceVo vo = getAccountBalanceVo(v);
                        supervisionList.add(vo);
                    });
            rsp.setSupervisionAccountBalanceList(supervisionList.stream().sorted(Comparator.comparing(AccountBalanceListRSP.AccountBalanceVo::getAccountBalance).reversed()).collect(Collectors.toList()));
            // 剩下的全部是非监管账户
            accountBalanceBaseInfos.stream().filter(a -> !supervisionAccountIds.contains(a.getAccountId()))
                    .collect(Collectors.groupingBy(AccountBalanceBaseInfo::getAccountBank)).forEach((k, v) -> {
                        AccountBalanceListRSP.AccountBalanceVo vo = getAccountBalanceVo(v);
                        unSupervisionList.add(vo);
                    });
            rsp.setNonSupervisionAccountBalanceList(unSupervisionList.stream().sorted(Comparator.comparing(AccountBalanceListRSP.AccountBalanceVo::getAccountBalance).reversed()).collect(Collectors.toList()));
            return rsp;
        }
    }

    private static AccountBalanceListRSP.AccountBalanceVo getAccountBalanceVo(List<AccountBalanceBaseInfo> v) {
        AccountBalanceListRSP.AccountBalanceVo vo = new AccountBalanceListRSP.AccountBalanceVo();
        vo.setBankName(v.get(0).getAccountBank());
        long accountBalance = v.stream().mapToLong(e -> e.getActualBalanceAmount() != null
                ? LongUtil.null2zero(e.getActualBalanceAmount()) : LongUtil.null2zero(e.getEstimateBalanceAmount())).summaryStatistics().getSum();
        vo.setAccountBalance(accountBalance);
        return vo;
    }

    public List<RentIncomeListRSP> rentIncomeList(RentIncomeListREQ req) {
        // 资产合同状态=起租，租金支付日在今日的资产合同（如租金支付日是节假日，支付日为节前最后一个工作日）==》2025/1/21修改为和账户余额表逻辑一致
        Map<LocalDate, BaseDataSpecialDate> specialDateMap = baseDataSpecialDateService.findAllSpecialDate().stream().collect(Collectors.toMap(BaseDataSpecialDate::getSpecialDate, Function.identity()));
        List<LocalDate> localDateList = baseDataSpecialDateService.handleHoliday(specialDateMap, LocalDate.now());
        if (CollUtil.isEmpty(localDateList)) {
            return Collections.emptyList();
        }
        // 资产合同状态=起息，租金应付日=当天，逾期状态=未逾期，租金回款账户=此账户
        List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery().eq(ContractBaseInfo::getContractStatus, ContractStatus.START_RENT.name()));
        List<Long> contractIdList = contractBaseInfoList.stream().map(ContractBaseInfo::getId).collect(Collectors.toList());
        List<CollectionBaseInfo> collectionBaseInfoList = collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .in(CollectionBaseInfo::getContractId, contractIdList)
                .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                .gt(CollectionBaseInfo::getPhase, 0));
        Map<Long, Integer> maxPhaseMap = collectionBaseInfoList.stream().collect(Collectors.toMap(
                CollectionBaseInfo::getContractId, CollectionBaseInfo::getPhase, (m1, m2) -> m1 > m2 ? m1 : m2
        ));
        // 下面这个接口比较慢，使用异步加快速度
        CompletableFuture<List<Long>> completableFuture = CompletableFuture.supplyAsync(() -> Optional.of(monthlyManagementBaseInfoService.contractIsOverdue(contractIdList, LocalDate.now())).orElse(new ArrayList<>()));
        collectionBaseInfoList = Optional.of(collectionBaseInfoList).map(e -> e.stream().filter(obj -> localDateList.contains(obj.getPlanCollectionDate())).collect(Collectors.toList())).orElse(new ArrayList<>());
        Map<LocalDate, List<CollectionBaseInfo>> localDateListMap = new HashMap<>();
        localDateListMap.put(LocalDate.now(), collectionBaseInfoList);
        List<Long> contractIsOverdue = completableFuture.join();
        collectionBaseInfoList = Optional.of(localDateListMap).map(n -> n.get(LocalDate.now()))
                .map(m -> m.stream().filter(f -> !contractIsOverdue.contains(f.getContractId()))
                        .collect(Collectors.toList())).orElse(null);
        if (CollUtil.isEmpty(collectionBaseInfoList)) {
            // 这次说明是真没有数据
            return Collections.emptyList();
        }

        Map<Long, ContractPriceDetailRSP> contractPriceMap = projectLifecycleService.getContractPriceMap(contractIdList);
        collectionBaseInfoList = collectionBaseInfoList.stream().peek(m -> {
            Integer maxPhase = maxPhaseMap.get(m.getContractId());
            if (Objects.equals(m.getPhase(), maxPhase)) {
                // 扣除保证金
                ContractPriceDetailRSP contractPriceDetail = contractPriceMap.get(m.getContractId());
                Long earnestMoney = LongUtil.null2zero(contractPriceDetail.getEarnestMoney());
                long result = LongUtil.null2zero(m.getPlanCollectionAmount()) - LongUtil.null2zero(earnestMoney);
                m.setPlanCollectionAmount(result >= 0 ? result : 0);
            }
        }).collect(Collectors.toList());
        List<ContractTenantry> tenantryList = contractTenantryService.list(Wrappers.<ContractTenantry>lambdaQuery()
                .in(ContractTenantry::getContractId, collectionBaseInfoList.stream().map(CollectionBaseInfo::getContractId).collect(Collectors.toList())));
        Map<Long, ContractTenantry> tenantryMap = new HashMap<>();
        Map<Long, String> clientIdNameMap = new HashMap<>();
        if (CollUtil.isNotEmpty(tenantryList)) {
            tenantryList.forEach(tenantry -> tenantryMap.put(tenantry.getContractId(), tenantry));
            clientIdNameMap = id2NameService.clientId2Name(tenantryList.stream().map(ContractTenantry::getLesseeId).collect(Collectors.toList()));
        }
        // 构建结果返回
        List<RentIncomeListRSP> rspList = new ArrayList<>();
        for (CollectionBaseInfo collectionBaseInfo : collectionBaseInfoList) {
            RentIncomeListRSP rsp = new RentIncomeListRSP();
            rsp.setContractId(collectionBaseInfo.getContractId());
            rsp.setContractCode(collectionBaseInfo.getContractCode());
            Long shouldPayAmount = LongUtil.null2zero(collectionBaseInfo.getPlanCollectionAmount());
            rsp.setShouldPayAmount(BigDecimal.valueOf(shouldPayAmount).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP).toPlainString());
            Long paidAmount = LongUtil.null2zero(collectionBaseInfo.getCollectionAmount());
            rsp.setPaidAmount(BigDecimal.valueOf(paidAmount).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP).toPlainString());
            rsp.setUnpaidAmount(BigDecimal.valueOf(shouldPayAmount - paidAmount).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP).toPlainString());
            rsp.setExpireDate(collectionBaseInfo.getPlanCollectionDate().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
            rsp.setTenantName(Objects.nonNull(tenantryMap.get(collectionBaseInfo.getContractId())) ? clientIdNameMap.get(tenantryMap.get(collectionBaseInfo.getContractId()).getLesseeId()) : null);
            rsp.setTenantId(Objects.nonNull(tenantryMap.get(collectionBaseInfo.getContractId())) ? tenantryMap.get(collectionBaseInfo.getContractId()).getLesseeId() : null);
            rspList.add(rsp);
        }
        return rspList;
    }

    public Pair<LocalDate, LocalDate> buildQueryDatePair() {
        LocalDate lastHoliday = LocalDate.now().plusDays(1);
        while (!baseDataSpecialDateService.isWorkDay(lastHoliday)) {
            lastHoliday = lastHoliday.plusDays(1);
        }
        return Pair.of(LocalDate.now(), lastHoliday.minusDays(1));
    }

    public PageR<RepayPrincipalInterestListRSP> repayPrincipalInterestList(RepayPrincipalInterestListREQ req) {
        // 数据范围：融资合同状态=起租，还本付息的支付日在今日的融资合同（无需考虑节假日）
        LocalDate now = LocalDate.now();
        req.setQueryDateFrom(now);
        req.setQueryDateTo(now);
        Page<RepayPrincipalInterestDto> queryPage = new Page<>(req.getPage(), req.getPageSize());
        Page<RepayPrincipalInterestDto> page = fundFinancingBaseInfoMapper.selectRepayPrincipalInterestList(queryPage, req);
        if (CollUtil.isEmpty(page.getRecords())) {
            return PageR.empty(req.getPage(), req.getPageSize());
        }
        // 间接融资的机构因为是一对多的，二次处理
        List<Long> financingIdList = page.getRecords().stream().filter(e -> Objects.nonNull(e.getFinancingId()))
                .filter(e -> FinancingTypeEnum.INDIRECT.name().equals(e.getType()))
                .map(RepayPrincipalInterestDto::getFinancingId).collect(Collectors.toList());
        Map<Long, List<FundOrganization>> organizationMap = fundOrganizationService.getBatchByFinancingId(financingIdList);
        List<RepayPrincipalInterestListRSP> rspList = page.getRecords().stream().map(e -> {
            RepayPrincipalInterestListRSP rsp = new RepayPrincipalInterestListRSP();
            rsp.setFinancingCode(e.getFinancingCode());
            rsp.setFinancingAmount(e.getFinancingAmount());
            rsp.setFinancingId(e.getFinancingId());
            rsp.setShouldPayInterest(BigDecimal.valueOf(LongUtil.null2zero(e.getShouldPayInterest()))
                    .divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP).toPlainString());
            rsp.setShouldPayPrincipal(BigDecimal.valueOf(LongUtil.null2zero(e.getShouldPayPrincipal()))
                    .divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP).toPlainString());
            rsp.setShouldPayAmount(BigDecimal.valueOf(LongUtil.null2zero(e.getShouldPayAmount()))
                    .divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP).toPlainString());
            rsp.setExpireDate(e.getDueDate().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
            if (FinancingTypeEnum.INDIRECT.name().equals(e.getType())) {
                List<FundOrganization> fundOrganizations = organizationMap.get(e.getFinancingId());
                if (CollUtil.isNotEmpty(fundOrganizations)) {
                    rsp.setFinancingOrgName(fundOrganizations.stream().map(FundOrganization::getOrganizationName).collect(Collectors.toList()));
                }
            } else {
                rsp.setFinancingOrgName(Collections.singletonList(e.getOrganizationName()));
            }
            return rsp;
        }).collect(Collectors.toList());
        return PageR.of(rspList, page.getTotal(), req.getPage(), req.getPageSize());
    }
}
