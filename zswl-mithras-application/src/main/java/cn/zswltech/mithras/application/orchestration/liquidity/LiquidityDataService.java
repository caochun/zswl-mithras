package cn.zswltech.mithras.application.orchestration.liquidity;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.thread.NamedThreadFactory;
import cn.zswltech.mithras.dto.basedata.BaseDataBankAccountQueryREQ;
import cn.zswltech.mithras.dto.liquiditymanage.base.ParameterBaseDetailRSP;
import cn.zswltech.mithras.dto.liquiditymanage.base.ParameterIndexDetailRSP;
import cn.zswltech.mithras.dto.liquiditymanage.liquidityindex.LiquidityBoardDetailREQ;
import cn.zswltech.mithras.dto.liquiditymanage.liquidityindex.LiquidityIndexDetailREQ;
import cn.zswltech.mithras.dto.liquiditymanage.liquidityindex.LiquidityMismatchDetailREQ;
import cn.zswltech.mithras.foundation.enums.CashFlowItemEnum;
import cn.zswltech.mithras.capital.enums.FinanceCashFlowItemEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingStatusEnum;
import cn.zswltech.mithras.liquidity.enums.FundParameterConfigType;
import cn.zswltech.mithras.fund.directfinancing.persistence.model.FundDirectFinancingBaseInfo;
import cn.zswltech.mithras.fund.directfinancing.persistence.model.FundDirectFinancingPledgeInfo;
import cn.zswltech.mithras.fund.directfinancing.persistence.model.FundDirectFinancingRepayActual;
import cn.zswltech.mithras.application.orchestration.fund.direct.service.FundDirectFinancingBaseInfoService;
import cn.zswltech.mithras.application.orchestration.fund.direct.service.FundDirectFinancingPledgeInfoService;
import cn.zswltech.mithras.application.orchestration.fund.direct.service.FundDirectFinancingRepayActualService;
import cn.zswltech.mithras.fund.persistence.mapper.financing.FundFinancingPledgeInfoMapper;
import cn.zswltech.mithras.basedata.persistence.model.BaseDataBankAccount;
import cn.zswltech.mithras.basedata.persistence.model.BaseDataSpecialDate;
import cn.zswltech.mithras.collection.model.CollectionBaseInfo;
import cn.zswltech.mithras.collection.model.CollectionRecordInfo;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractRentActual;
import cn.zswltech.mithras.fund.persistence.model.credit.FundCredit;
import cn.zswltech.mithras.fund.persistence.model.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.fund.persistence.model.financing.FundFinancingPayAccount;
import cn.zswltech.mithras.fund.persistence.model.financing.FundFinancingPledgeInfo;
import cn.zswltech.mithras.fund.persistence.model.organization.FundOrganization;
import cn.zswltech.mithras.fund.persistence.model.receiptrepay.FundReceiptFlowDetail;
import cn.zswltech.mithras.fund.persistence.model.receiptrepay.FundReceiptFlowPlan;
import cn.zswltech.mithras.fund.persistence.model.receiptrepay.FundReceiptRepayBaseInfo;
import cn.zswltech.mithras.liquidity.snapshot.LiquidityCollectionPlanSnapshot;
import cn.zswltech.mithras.liquidity.snapshot.LiquidityCollectionRecordSnapshot;
import cn.zswltech.mithras.liquidity.snapshot.LiquidityContractBaseSnapshot;
import cn.zswltech.mithras.liquidity.snapshot.LiquidityContractRentSnapshot;
import cn.zswltech.mithras.liquidity.snapshot.LiquidityCreditLimitSnapshot;
import cn.zswltech.mithras.liquidity.snapshot.LiquidityBankAccountSnapshot;
import cn.zswltech.mithras.liquidity.snapshot.LiquidityDirectFinancingSnapshot;
import cn.zswltech.mithras.liquidity.snapshot.LiquidityDirectFinancingRepayActualSnapshot;
import cn.zswltech.mithras.liquidity.snapshot.LiquidityFinancingOrganizationSnapshot;
import cn.zswltech.mithras.liquidity.snapshot.LiquidityFinancingPayAccountSnapshot;
import cn.zswltech.mithras.liquidity.snapshot.LiquidityFinancingPledgeSnapshot;
import cn.zswltech.mithras.liquidity.snapshot.LiquidityFinancingSnapshot;
import cn.zswltech.mithras.liquidity.snapshot.LiquidityFundReceiptFlowDetailSnapshot;
import cn.zswltech.mithras.liquidity.snapshot.LiquidityFundReceiptFlowPlanSnapshot;
import cn.zswltech.mithras.liquidity.snapshot.LiquidityFundReceiptRepaySnapshot;
import cn.zswltech.mithras.liquidity.snapshot.LiquiditySpecialDateSnapshot;
import cn.zswltech.mithras.liquidity.persistence.model.AccountBalanceBaseInfo;
import cn.zswltech.mithras.liquidity.persistence.model.FundFinancingAccountSetting;
import cn.zswltech.mithras.liquidity.service.FundParameterConfigService;
import cn.zswltech.mithras.liquidity.service.LiquidityIndicatorBoardHolder;
import cn.zswltech.mithras.liquidity.service.LiquidityIndicatorHolder;
import cn.zswltech.mithras.liquidity.service.LiquidityIndicatorIndexHolder;
import cn.zswltech.mithras.liquidity.service.LiquidityIndicatorMismatchHolder;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.basedata.service.BaseDataBankAccountService;
import cn.zswltech.mithras.basedata.service.BaseDataSpecialDateService;
import cn.zswltech.mithras.fund.application.financing.model.FundPledgeSupervisedBO;
import cn.zswltech.mithras.collection.application.CollectionBaseInfoService;
import cn.zswltech.mithras.application.orchestration.collection.CollectionRecordInfoService;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.contract.core.ContractRentActualService;
import cn.zswltech.mithras.application.orchestration.fund.FundCreditService;
import cn.zswltech.mithras.fund.application.organization.FundOrganizationService;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingBaseInfoService;
import cn.zswltech.mithras.fund.application.financing.FundFinancingPayAccountService;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingPledgeInfoService;
import cn.zswltech.mithras.application.orchestration.fund.receiptrepay.FundReceiptFlowDetailService;
import cn.zswltech.mithras.application.orchestration.fund.receiptrepay.FundReceiptFlowPlanService;
import cn.zswltech.mithras.application.orchestration.fund.receiptrepay.FundReceiptRepayBaseInfoService;
import cn.zswltech.mithras.application.orchestration.fund.receiptrepay.FundReceiptRepayCashFlowService;
import cn.zswltech.mithras.application.orchestration.finance.monthly.MonthlyManagementBaseInfoService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projlifecycle.ProjectLifecycleService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * LiquidityDataService
 *  涉及业务数据查询-原本地缓存设计废除，计算数据前需根据模块实时查询数据
 * @author chenyifei
 * @since 2024/12/18
 */
@Service
@Slf4j
public class LiquidityDataService {

    @Resource
    private BaseDataBankAccountService baseDataBankAccountService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private FundFinancingBaseInfoService financingBaseInfoService;
    @Resource
    private FundDirectFinancingBaseInfoService directFinancingBaseInfoService;
    @Resource
    private FundReceiptRepayBaseInfoService receiptRepayBaseInfoService;
    @Resource
    private FundReceiptRepayCashFlowService receiptRepayCashFlowService;
    @Resource
    private FundReceiptFlowPlanService receiptFlowPlanService;
    @Resource
    private FundFinancingPayAccountService financingPayAccountService;
    @Resource
    private FundDirectFinancingRepayActualService directFinancingRepayActualService;
    @Resource
    private ContractRentActualService contractRentActualService;
    @Resource
    private ProjectLifecycleService projectLifecycleService;
    @Resource
    private BaseDataSpecialDateService baseDataSpecialDateService;
    @Resource
    private FundFinancingAccountSettingService accountSettingService;
    @Resource
    private FundFinancingPledgeInfoMapper financingPledgeInfoMapper;
    @Resource
    private AccountBalanceBaseInfoService accountBalanceBaseInfoService;
    @Resource
    private FundReceiptFlowDetailService receiptFlowDetailService;
    @Resource
    private CollectionRecordInfoService collectionRecordInfoService;
    @Resource
    private FundDirectFinancingPledgeInfoService directFinancingPledgeInfoService;
    @Resource
    private FundFinancingPledgeInfoService financingPledgeInfoService;
    @Resource
    private FundParameterConfigService fundParameterConfigService;
    @Resource
    private FundCreditService fundCreditService;
    @Resource
    private FundOrganizationService organizationService;


    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 15, 120, TimeUnit.SECONDS, new LinkedBlockingDeque<>(100), new NamedThreadFactory("MithrasLiquidityIndexThread-", false));

    /**
     * 统一数据集
     */
    public void dataQueryAccount(){
        StopWatch sw = new StopWatch();
        sw.start("账户余额表-数据构建");
        LocalDate now = LocalDate.now();
        LiquidityIndicatorHolder.BASE_DATA_BANK_ACCOUNT = toBankAccountSnapshots(baseDataBankAccountService.accountQuery(new BaseDataBankAccountQueryREQ()))
                .stream().collect(Collectors.toMap(LiquidityBankAccountSnapshot::getId, Function.identity()));
        LiquidityIndicatorHolder.BASE_DATA_SPECIAL_DATE = toSpecialDateSnapshots(baseDataSpecialDateService.findAllSpecialDate())
                .stream().collect(Collectors.toMap(LiquiditySpecialDateSnapshot::getSpecialDate, Function.identity()));
        LiquidityIndicatorHolder.FUND_FINANCING_ACCOUNT_SETTING = accountSettingService.list().stream().collect(Collectors.groupingBy(FundFinancingAccountSetting::getAccountId));
        LiquidityIndicatorHolder.DEFAULT_ACCOUNT = LiquidityIndicatorHolder.BASE_DATA_BANK_ACCOUNT.values().stream().filter(f -> Objects.equals(f.getAccountNumber(), "1202021219900394595")).findFirst().orElse(new LiquidityBankAccountSnapshot());
        LiquidityIndicatorHolder.ACCOUNT_BALANCE_BASE_INFO = Optional.ofNullable(accountBalanceBaseInfoService.list()).map(item -> item.stream().collect(Collectors.groupingBy(AccountBalanceBaseInfo::getDate,
                Collectors.toMap(AccountBalanceBaseInfo::getAccountId, Function.identity(), (m1, m2) -> m2)))).orElse(new HashMap<>());
        settingDataUpdate();
        LiquidityIndicatorHolder.ACCOUNT_PARAMETER_CONFIG_CALCULATE_MONTH = fundParameterConfigService.getByConfigCode(FundParameterConfigType.ACCOUNT_BALANCE_CALCULATE_TIME.getDisplay());
        LiquidityIndicatorHolder.ACCOUNT_PARAMETER_CONFIG_START_TIME = fundParameterConfigService.getByConfigCode(FundParameterConfigType.ACCOUNT_BALANCE_START_TIME.getDisplay());
        /**
         * 资产端
         */
        List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery().eq(ContractBaseInfo::getContractStatus, ContractStatus.START_RENT.name()));
        List<Long> contractIdList = contractBaseInfoList.stream().map(ContractBaseInfo::getId).collect(Collectors.toList());
        List<CollectionBaseInfo> collectionBaseInfoList = collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .in(CollectionBaseInfo::getContractId, contractIdList)
                .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                .gt(CollectionBaseInfo::getPhase, 0));

//        List<ContractRentActual> rentActualList = contractRentActualService.listByContractIds(contractIdList);

        LiquidityIndicatorHolder.CONTRACT_PRICE_DETAIL = projectLifecycleService.getContractPriceMap(contractIdList);
//        LiquidityIndicatorHolder.CONTRACT_RENT_ACTUAL = rentActualList.stream().collect(Collectors.groupingBy(ContractRentActual::getCashFlowDate));
//        LiquidityIndicatorHolder.CONTRACT_RENT_ACTUAL_MAX_PHASE = rentActualList.stream().collect(Collectors.toMap(
//                ContractRentActual::getContractId, ContractRentActual::getCashFlowPhase, (m1, m2) -> m1 > m2 ? m1 : m2
//        ));
        LiquidityIndicatorHolder.CONTRACT_IS_OVERDUE = SpringContextHolder.getBean(MonthlyManagementBaseInfoService.class).contractIsOverdue(contractIdList, now);
        List<LiquidityCollectionPlanSnapshot> collectionPlanList = toCollectionPlanSnapshots(collectionBaseInfoList);
        LiquidityIndicatorHolder.COLLECTION_BASE_INFO = collectionPlanList.stream().collect(Collectors.groupingBy(LiquidityCollectionPlanSnapshot::getPlanCollectionDate));
        LiquidityIndicatorHolder.COLLECTION_BASE_INFO_MAX_PHASE = collectionBaseInfoList.stream().collect(Collectors.toMap(
                CollectionBaseInfo::getContractId, CollectionBaseInfo::getPhase, (m1, m2) -> m1 > m2 ? m1 : m2
        ));

        LiquidityIndicatorHolder.COLLECTION_RECORD_INFO = toCollectionRecordSnapshots(collectionRecordInfoService.listByCollectionIds(collectionPlanList.stream()
                .map(LiquidityCollectionPlanSnapshot::getId).collect(Collectors.toList()))).stream().collect(Collectors.groupingBy(LiquidityCollectionRecordSnapshot::getCollectionId));
        LiquidityIndicatorHolder.CONTRACT_ACCOUNT = getContractAccountMap(contractIdList);

        /**
         * 资金端
         */
        // 间融
        List<FundFinancingBaseInfo> financingBaseInfoList = financingBaseInfoService.list(Wrappers.<FundFinancingBaseInfo>lambdaQuery().eq(FundFinancingBaseInfo::getFinancingStatus, FundFinancingStatusEnum.CARRY_INTEREST.name()));
        List<Long> financingIdList = financingBaseInfoList.stream().map(FundFinancingBaseInfo::getId).collect(Collectors.toList());
        LiquidityIndicatorHolder.FUND_FINANCING_BASE_INFO = toFinancingSnapshots(financingBaseInfoList).stream()
                .collect(Collectors.toMap(LiquidityFinancingSnapshot::getId, Function.identity()));
        LiquidityIndicatorHolder.FUND_FINANCING_PAY_ACCOUNT = toFinancingPayAccountSnapshots(financingPayAccountService.listByFinancingIds(financingIdList)).stream()
                .collect(Collectors.groupingBy(LiquidityFinancingPayAccountSnapshot::getBankAccountId));


        // 直融
        List<FundDirectFinancingBaseInfo> directFinancingBaseInfoList = directFinancingBaseInfoService.list(Wrappers.<FundDirectFinancingBaseInfo>lambdaQuery().eq(FundDirectFinancingBaseInfo::getFinancingStatus, FundFinancingStatusEnum.CARRY_INTEREST.name()));
        List<Long> directFinancingIdList = directFinancingBaseInfoList.stream().map(FundDirectFinancingBaseInfo::getId).collect(Collectors.toList());
        List<FundDirectFinancingRepayActual> directFinancingRepayActualList = directFinancingRepayActualService.list(Wrappers.<FundDirectFinancingRepayActual>lambdaQuery().in(FundDirectFinancingRepayActual::getFinancingId, directFinancingIdList));

        LiquidityIndicatorHolder.FUND_DIRECT_FINANCING_BASE_INFO = toDirectFinancingSnapshots(directFinancingBaseInfoList).stream()
                .collect(Collectors.toMap(LiquidityDirectFinancingSnapshot::getId, Function.identity()));
        LiquidityIndicatorHolder.FUND_DIRECT_FINANCING_REPAY_ACTUAL = toDirectFinancingRepayActualSnapshots(directFinancingRepayActualList).stream()
                .collect(Collectors.groupingBy(LiquidityDirectFinancingRepayActualSnapshot::getRepayDate));

        List<FundReceiptRepayBaseInfo> repayBaseInfoList = receiptRepayBaseInfoService.list(Wrappers.<FundReceiptRepayBaseInfo>lambdaQuery().in(FundReceiptRepayBaseInfo::getFinancingId, Stream.of(financingIdList, directFinancingIdList).flatMap(Collection::stream).collect(Collectors.toList())));
        LiquidityIndicatorHolder.FUND_RECEIPT_REPAY_BASE_INFO = toFundReceiptRepaySnapshots(repayBaseInfoList).stream()
                .collect(Collectors.toMap(LiquidityFundReceiptRepaySnapshot::getId, Function.identity()));
        List<FundReceiptFlowPlan> flowPlanList = receiptFlowPlanService.list(Wrappers.<FundReceiptFlowPlan>lambdaQuery().in(FundReceiptFlowPlan::getReceiptRepayId, LiquidityIndicatorHolder.FUND_RECEIPT_REPAY_BASE_INFO.keySet())
                .eq(FundReceiptFlowPlan::getCashFlowItem, FinanceCashFlowItemEnum.REPAY.name()));
        LiquidityIndicatorHolder.FUND_RECEIPT_FLOW_PLAN = toFundReceiptFlowPlanSnapshots(flowPlanList).stream()
                .collect(Collectors.groupingBy(LiquidityFundReceiptFlowPlanSnapshot::getCashFlowDate));

        LiquidityIndicatorHolder.NOW = now;
        log.info("账户余额表-数据构建完成: 耗时{}", sw.prettyPrint(TimeUnit.MILLISECONDS));
        sw.stop();

    }

    public void dataQueryIndex(LiquidityIndexDetailREQ req){
        CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
            LiquidityIndicatorIndexHolder.ACCOUNT_BALANCE_BASE_INFO = Optional.ofNullable(accountBalanceBaseInfoService.list()).map(item -> item.stream().collect(Collectors.groupingBy(AccountBalanceBaseInfo::getDate,
                    Collectors.toMap(AccountBalanceBaseInfo::getAccountId, Function.identity(), (m1, m2) -> m2)))).orElse(new HashMap<>());
            settingDataUpdate();
        }, executor);
        /**
         * 资产端
         */
        CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {

            List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery().eq(ContractBaseInfo::getContractStatus, ContractStatus.START_RENT.name()));
            List<Long> contractIdList = contractBaseInfoList.stream().map(ContractBaseInfo::getId).collect(Collectors.toList());

            List<CollectionBaseInfo> collectionBaseInfoList = collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                    .in(CollectionBaseInfo::getContractId, contractIdList)
                    .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                    .gt(CollectionBaseInfo::getPhase, 0));
            List<LiquidityCollectionPlanSnapshot> collectionPlanList = toCollectionPlanSnapshots(collectionBaseInfoList);
            LiquidityIndicatorIndexHolder.COLLECTION_BASE_INFO = collectionPlanList.stream().collect(Collectors.groupingBy(LiquidityCollectionPlanSnapshot::getPlanCollectionDate));
            LiquidityIndicatorIndexHolder.COLLECTION_RECORD_INFO = toCollectionRecordSnapshots(collectionRecordInfoService.listByCollectionIds(collectionPlanList.stream()
                    .map(LiquidityCollectionPlanSnapshot::getId).collect(Collectors.toList()))).stream().collect(Collectors.groupingBy(LiquidityCollectionRecordSnapshot::getCollectionId));

        }, executor);
        /**
         * 资金端
         */
        CompletableFuture<Void> future3 = CompletableFuture.runAsync(() -> {
            List<FundFinancingBaseInfo> financingBaseInfoList = financingBaseInfoService.list(Wrappers.<FundFinancingBaseInfo>lambdaQuery().eq(FundFinancingBaseInfo::getFinancingStatus, FundFinancingStatusEnum.CARRY_INTEREST.name()));
            List<Long> financingIdList = financingBaseInfoList.stream().map(FundFinancingBaseInfo::getId).collect(Collectors.toList());

            List<FundDirectFinancingBaseInfo> directFinancingBaseInfoList = directFinancingBaseInfoService.list(Wrappers.<FundDirectFinancingBaseInfo>lambdaQuery().eq(FundDirectFinancingBaseInfo::getFinancingStatus, FundFinancingStatusEnum.CARRY_INTEREST.name()));
            List<Long> directFinancingIdList = directFinancingBaseInfoList.stream().map(FundDirectFinancingBaseInfo::getId).collect(Collectors.toList());

            // 间融
            CompletableFuture<Void> inDirectFuture = CompletableFuture.runAsync(() -> {
                LiquidityIndicatorIndexHolder.FUND_FINANCING_PLEDGE_INFO = toFinancingPledgeSnapshots(financingPledgeInfoService.list(Wrappers.<FundFinancingPledgeInfo>lambdaQuery().in(FundFinancingPledgeInfo::getFinancingId, financingIdList))).stream()
                        .filter(a -> Objects.equals(a.getPledge(), Boolean.TRUE) || Objects.equals(a.getSupervise(), Boolean.TRUE))
                        .collect(Collectors.groupingBy(LiquidityFinancingPledgeSnapshot::getFinancingId));
                List<FundCredit> effectCreditList = fundCreditService.list(Wrappers.<FundCredit>lambdaQuery().eq(FundCredit::getEffective, Boolean.TRUE));
                LiquidityIndicatorIndexHolder.CREDIT_LIMIT_DETAIL = fundCreditService.queryLimitDetailBatch(effectCreditList, false).entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, entry -> new LiquidityCreditLimitSnapshot(entry.getValue().getTotalLimit(), entry.getValue().getOccupyTotalLimit())));
            });

            // 直融
            CompletableFuture<Void> directFuture = CompletableFuture.runAsync(() -> {
                LiquidityIndicatorIndexHolder.FUND_DIRECT_FINANCING_PLEDGE_INFO = toDirectFinancingPledgeSnapshots(directFinancingPledgeInfoService.list(Wrappers.<FundDirectFinancingPledgeInfo>lambdaQuery().in(FundDirectFinancingPledgeInfo::getFinancingId, directFinancingIdList))).stream()
                        .filter(a -> Objects.equals(a.getPledge(), Boolean.TRUE) || Objects.equals(a.getSupervise(), Boolean.TRUE))
                        .collect(Collectors.groupingBy(LiquidityFinancingPledgeSnapshot::getFinancingId));
            });

            // 还本付息
            CompletableFuture<Void> repayFuture = CompletableFuture.runAsync(() -> {
                List<FundReceiptRepayBaseInfo> repayBaseInfoList = receiptRepayBaseInfoService.list(Wrappers.<FundReceiptRepayBaseInfo>lambdaQuery().in(FundReceiptRepayBaseInfo::getFinancingId, Stream.of(financingIdList, directFinancingIdList).flatMap(Collection::stream).collect(Collectors.toList())));
                Map<Long, FundReceiptRepayBaseInfo> repayBaseInfoMap = repayBaseInfoList.stream().collect(Collectors.toMap(FundReceiptRepayBaseInfo::getId, Function.identity()));

                List<FundReceiptFlowPlan> flowPlanList = receiptFlowPlanService.list(Wrappers.<FundReceiptFlowPlan>lambdaQuery().in(FundReceiptFlowPlan::getReceiptRepayId, repayBaseInfoMap.keySet()));

                List<LiquidityFundReceiptFlowPlanSnapshot> flowPlanSnapshotList = toFundReceiptFlowPlanSnapshots(flowPlanList);
                LiquidityIndicatorIndexHolder.FUND_RECEIPT_FLOW_PLAN = flowPlanSnapshotList.stream()
                        .collect(Collectors.groupingBy(LiquidityFundReceiptFlowPlanSnapshot::getCashFlowDate, Collectors.groupingBy(LiquidityFundReceiptFlowPlanSnapshot::getCashFlowItem)));

                Map<String, List<FundReceiptFlowDetail>> flowDetailList = receiptFlowDetailService.listByReceiptRepayIds(flowPlanList.stream().map(FundReceiptFlowPlan::getReceiptRepayId).collect(Collectors.toList()));
                LiquidityIndicatorIndexHolder.FUND_RECEIPT_FLOW_DETAIL = toFundReceiptFlowDetailSnapshots(flowDetailList.values().stream().flatMap(Collection::stream).collect(Collectors.toList()))
                        .stream().collect(Collectors.groupingBy(LiquidityFundReceiptFlowDetailSnapshot::getReceiptRepayId,
                                Collectors.groupingBy(LiquidityFundReceiptFlowDetailSnapshot::getCashFlowCode)));
            });

            CompletableFuture.allOf(inDirectFuture, directFuture, repayFuture).join();
        }, executor);

        CompletableFuture.allOf(future1, future2, future3).join();
    }

    public void dataQueryBoard(LiquidityBoardDetailREQ req){
        LocalDate now = LocalDate.now();
        CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
            Map<LocalDate, Map<Long, AccountBalanceBaseInfo>> accountBalanceBaseInfoMap = Optional.ofNullable(accountBalanceBaseInfoService.list()).map(item -> item.stream().collect(Collectors.groupingBy(AccountBalanceBaseInfo::getDate,
                    Collectors.toMap(AccountBalanceBaseInfo::getAccountId, Function.identity(), (m1, m2) -> m2)))).orElse(new HashMap<>());
            LiquidityIndicatorBoardHolder.ACCOUNT_BALANCE_BASE_INFO = accountBalanceBaseInfoMap;
            // 看板指标会调用指标的计算逻辑，要给指标数据集也赋值
            LiquidityIndicatorIndexHolder.ACCOUNT_BALANCE_BASE_INFO = accountBalanceBaseInfoMap;
            settingDataUpdate();
        }, executor);

        CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
            List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery().eq(ContractBaseInfo::getContractStatus, ContractStatus.START_RENT.name()));
            List<Long> contractIdList = contractBaseInfoList.stream().map(ContractBaseInfo::getId).collect(Collectors.toList());
            List<CollectionBaseInfo> collectionBaseInfoList = collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                    .in(CollectionBaseInfo::getContractId, contractIdList)
                    .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                    .gt(CollectionBaseInfo::getPhase, 0));
            Map<Long, List<CollectionBaseInfo>> collectionBaseInfoMap = collectionBaseInfoList.stream().collect(Collectors.groupingBy(CollectionBaseInfo::getContractId));

            LiquidityIndicatorBoardHolder.COLLECTION_RECORD_INFO = toCollectionRecordSnapshots(collectionRecordInfoService.listByCollectionIds(collectionBaseInfoMap.values()
                    .stream().flatMap(Collection::stream).map(CollectionBaseInfo::getId).collect(Collectors.toList()))).stream().collect(Collectors.groupingBy(LiquidityCollectionRecordSnapshot::getCollectionId));

            LiquidityIndicatorBoardHolder.NOW = now;
        }, executor);
        CompletableFuture.allOf(future1, future2).join();


    }


    public void dataQueryMismatch(LiquidityMismatchDetailREQ req){
        CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
            LiquidityIndicatorMismatchHolder.ACCOUNT_BALANCE_BASE_INFO = Optional.ofNullable(accountBalanceBaseInfoService.list()).map(item -> item.stream().collect(Collectors.groupingBy(AccountBalanceBaseInfo::getDate,
                    Collectors.toMap(AccountBalanceBaseInfo::getAccountId, Function.identity(), (m1, m2) -> m2)))).orElse(new HashMap<>());
            settingDataUpdate();
        }, executor);
        /**
         * 资产端
         */
        CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
            List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery().eq(ContractBaseInfo::getContractStatus, ContractStatus.START_RENT.name()));
            List<Long> contractIdList = contractBaseInfoList.stream().map(ContractBaseInfo::getId).collect(Collectors.toList());


            CompletableFuture<Void> future21 = CompletableFuture.runAsync(() -> {
                List<ContractRentActual> rentActualList = contractRentActualService.listByContractIds(contractIdList);
                LiquidityIndicatorMismatchHolder.CONTRACT_RENT_ACTUAL = rentActualList.stream()
                        .map(item -> new LiquidityContractRentSnapshot(item.getContractId(), item.getCashFlowDate(), item.getRent()))
                        .collect(Collectors.groupingBy(LiquidityContractRentSnapshot::getCashFlowDate));
                LiquidityIndicatorMismatchHolder.CONTRACT_BASE_INFO = contractBaseInfoList.stream()
                        .collect(Collectors.toMap(ContractBaseInfo::getId, item -> new LiquidityContractBaseSnapshot(item.getContractCode())));
            });

            CompletableFuture<Void> future22 = CompletableFuture.runAsync(() -> {
                List<CollectionBaseInfo> collectionBaseInfoList = collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                        .in(CollectionBaseInfo::getContractId, contractIdList)
                        .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                        .gt(CollectionBaseInfo::getPhase, 0));
                Map<Long, List<CollectionBaseInfo>> collectionBaseInfoMap = collectionBaseInfoList.stream().collect(Collectors.groupingBy(CollectionBaseInfo::getContractId));
                LiquidityIndicatorMismatchHolder.COLLECTION_RECORD_INFO = toCollectionRecordSnapshots(collectionRecordInfoService.listByCollectionIds(collectionBaseInfoMap.values()
                        .stream().flatMap(Collection::stream).map(CollectionBaseInfo::getId).collect(Collectors.toList()))).stream().collect(Collectors.groupingBy(LiquidityCollectionRecordSnapshot::getCollectionId));
            });

            CompletableFuture.allOf(future21, future22).join();

        }, executor);
        /**
         * 资金端
         */
        CompletableFuture<Void> future3 = CompletableFuture.runAsync(() -> {
            List<FundFinancingBaseInfo> financingBaseInfoList = financingBaseInfoService.list(Wrappers.<FundFinancingBaseInfo>lambdaQuery().in(FundFinancingBaseInfo::getFinancingStatus, FundFinancingStatusEnum.CARRY_INTEREST.name(), FundFinancingStatusEnum.SETTLE.name()));
            List<Long> financingIdList = financingBaseInfoList.stream().map(FundFinancingBaseInfo::getId).collect(Collectors.toList());

            List<FundDirectFinancingBaseInfo> directFinancingBaseInfoList = directFinancingBaseInfoService.list(Wrappers.<FundDirectFinancingBaseInfo>lambdaQuery().in(FundDirectFinancingBaseInfo::getFinancingStatus, FundFinancingStatusEnum.CARRY_INTEREST.name(), FundFinancingStatusEnum.SETTLE.name()));
            List<Long> directFinancingIdList = directFinancingBaseInfoList.stream().map(FundDirectFinancingBaseInfo::getId).collect(Collectors.toList());

            CompletableFuture<Void> inDirectFuture = CompletableFuture.runAsync(() -> {
                // 间融
                LiquidityIndicatorMismatchHolder.FUND_FINANCING_BASE_INFO = toFinancingSnapshots(financingBaseInfoList).stream()
                        .collect(Collectors.toMap(LiquidityFinancingSnapshot::getId, Function.identity()));
                LiquidityIndicatorMismatchHolder.FUND_FINANCING_PLEDGE_INFO = toFinancingPledgeSnapshots(financingPledgeInfoService.list(Wrappers.<FundFinancingPledgeInfo>lambdaQuery().in(FundFinancingPledgeInfo::getFinancingId, financingIdList))).stream()
                        .filter(a -> Objects.equals(a.getPledge(), Boolean.TRUE) || Objects.equals(a.getSupervise(), Boolean.TRUE))
                        .collect(Collectors.groupingBy(LiquidityFinancingPledgeSnapshot::getFinancingId));
                LiquidityIndicatorMismatchHolder.FUND_ORGANIZATION_INFO = toFinancingOrganizationSnapshotMap(organizationService.getBatchByFinancingId(financingIdList));
            });

            CompletableFuture<Void> directFuture = CompletableFuture.runAsync(() -> {
                // 直融
                LiquidityIndicatorMismatchHolder.FUND_DIRECT_FINANCING_BASE_INFO = toDirectFinancingSnapshots(directFinancingBaseInfoList).stream()
                        .collect(Collectors.toMap(LiquidityDirectFinancingSnapshot::getId, Function.identity()));
                LiquidityIndicatorMismatchHolder.FUND_DIRECT_FINANCING_PLEDGE_INFO = toDirectFinancingPledgeSnapshots(directFinancingPledgeInfoService.list(Wrappers.<FundDirectFinancingPledgeInfo>lambdaQuery().in(FundDirectFinancingPledgeInfo::getFinancingId, directFinancingIdList))).stream()
                        .filter(a -> Objects.equals(a.getPledge(), Boolean.TRUE) || Objects.equals(a.getSupervise(), Boolean.TRUE))
                        .collect(Collectors.groupingBy(LiquidityFinancingPledgeSnapshot::getFinancingId));
            });

            CompletableFuture<Void> repayFuture = CompletableFuture.runAsync(() -> {
                // 还本付息
                List<FundReceiptRepayBaseInfo> repayBaseInfoList = receiptRepayBaseInfoService.list(Wrappers.<FundReceiptRepayBaseInfo>lambdaQuery().in(FundReceiptRepayBaseInfo::getFinancingId, Stream.of(financingIdList, directFinancingIdList).flatMap(Collection::stream).collect(Collectors.toList())));
                LiquidityIndicatorMismatchHolder.FUND_RECEIPT_REPAY_BASE_INFO = toFundReceiptRepaySnapshots(repayBaseInfoList).stream()
                        .collect(Collectors.toMap(LiquidityFundReceiptRepaySnapshot::getId, Function.identity()));
                List<FundReceiptFlowPlan> flowPlanList = receiptFlowPlanService.list(Wrappers.<FundReceiptFlowPlan>lambdaQuery().in(FundReceiptFlowPlan::getReceiptRepayId, LiquidityIndicatorMismatchHolder.FUND_RECEIPT_REPAY_BASE_INFO.keySet()));

                LiquidityIndicatorMismatchHolder.FUND_RECEIPT_FLOW_PLAN = toFundReceiptFlowPlanSnapshots(flowPlanList).stream()
                        .collect(Collectors.groupingBy(LiquidityFundReceiptFlowPlanSnapshot::getCashFlowDate, Collectors.groupingBy(LiquidityFundReceiptFlowPlanSnapshot::getCashFlowItem)));
            });
            CompletableFuture.allOf(inDirectFuture, directFuture, repayFuture).join();

        }, executor);

        CompletableFuture.allOf(future1, future2, future3).join();

    }


    public void settingDataUpdate(){
        // 配置参数更新
        ParameterBaseDetailRSP parameterBaseDetail = fundParameterConfigService.parameterBaseDetail(null);
        Map<String, ParameterIndexDetailRSP> indexDetailMap = fundParameterConfigService.parameterIndexDetail(null).stream().collect(Collectors.toMap(ParameterIndexDetailRSP::getIndexName, Function.identity()));
        LiquidityIndicatorHolder.PARAMETER_BASE_DETAIL = parameterBaseDetail;
        LiquidityIndicatorHolder.PARAMETER_INDEX_DETAIL = indexDetailMap;

        LiquidityIndicatorIndexHolder.PARAMETER_BASE_DETAIL = parameterBaseDetail;
        LiquidityIndicatorIndexHolder.PARAMETER_INDEX_DETAIL = indexDetailMap;

        LiquidityIndicatorBoardHolder.PARAMETER_BASE_DETAIL = parameterBaseDetail;
    }

    private List<LiquidityCollectionPlanSnapshot> toCollectionPlanSnapshots(List<CollectionBaseInfo> collectionBaseInfoList) {
        if (CollectionUtil.isEmpty(collectionBaseInfoList)) {
            return Collections.emptyList();
        }
        return collectionBaseInfoList.stream()
                .map(item -> new LiquidityCollectionPlanSnapshot(
                        item.getId(),
                        item.getContractId(),
                        item.getCode(),
                        item.getPhase(),
                        item.getPlanCollectionDate(),
                        item.getPlanCollectionAmount()))
                .collect(Collectors.toList());
    }

    private List<LiquidityCollectionRecordSnapshot> toCollectionRecordSnapshots(List<CollectionRecordInfo> collectionRecordInfoList) {
        if (CollectionUtil.isEmpty(collectionRecordInfoList)) {
            return Collections.emptyList();
        }
        return collectionRecordInfoList.stream()
                .map(item -> new LiquidityCollectionRecordSnapshot(
                        item.getCollectionId(),
                        item.getCollectionDate(),
                        item.getCollectionAmount()))
                .collect(Collectors.toList());
    }

    private List<LiquidityFundReceiptRepaySnapshot> toFundReceiptRepaySnapshots(List<FundReceiptRepayBaseInfo> receiptRepayList) {
        if (CollectionUtil.isEmpty(receiptRepayList)) {
            return Collections.emptyList();
        }
        return receiptRepayList.stream()
                .map(item -> new LiquidityFundReceiptRepaySnapshot(
                        item.getId(),
                        item.getFinancingId(),
                        item.getFinancingType()))
                .collect(Collectors.toList());
    }

    private List<LiquidityFundReceiptFlowPlanSnapshot> toFundReceiptFlowPlanSnapshots(List<FundReceiptFlowPlan> flowPlanList) {
        if (CollectionUtil.isEmpty(flowPlanList)) {
            return Collections.emptyList();
        }
        return flowPlanList.stream()
                .map(item -> new LiquidityFundReceiptFlowPlanSnapshot(
                        item.getReceiptRepayId(),
                        item.getCashFlowPhase(),
                        item.getCashFlowCode(),
                        item.getCashFlowItem(),
                        item.getCashFlowDate(),
                        item.getPrincipalAmount(),
                        item.getInterestAmount()))
                .collect(Collectors.toList());
    }

    private List<LiquidityFundReceiptFlowDetailSnapshot> toFundReceiptFlowDetailSnapshots(List<FundReceiptFlowDetail> flowDetailList) {
        if (CollectionUtil.isEmpty(flowDetailList)) {
            return Collections.emptyList();
        }
        return flowDetailList.stream()
                .map(item -> new LiquidityFundReceiptFlowDetailSnapshot(
                        item.getReceiptRepayId(),
                        item.getCashFlowCode(),
                        item.getTotalAmount()))
                .collect(Collectors.toList());
    }

    private List<LiquidityDirectFinancingRepayActualSnapshot> toDirectFinancingRepayActualSnapshots(List<FundDirectFinancingRepayActual> repayActualList) {
        if (CollectionUtil.isEmpty(repayActualList)) {
            return Collections.emptyList();
        }
        return repayActualList.stream()
                .map(item -> new LiquidityDirectFinancingRepayActualSnapshot(
                        item.getFinancingId(),
                        item.getRepayDate(),
                        item.getRepayAmount()))
                .collect(Collectors.toList());
    }

    private List<LiquidityDirectFinancingSnapshot> toDirectFinancingSnapshots(List<FundDirectFinancingBaseInfo> directFinancingBaseInfoList) {
        if (CollectionUtil.isEmpty(directFinancingBaseInfoList)) {
            return Collections.emptyList();
        }
        return directFinancingBaseInfoList.stream()
                .map(item -> new LiquidityDirectFinancingSnapshot(
                        item.getId(),
                        item.getProductName(),
                        item.getFinancingCode(),
                        item.getDirectFinancingType(),
                        item.getCarryInterestTime()))
                .collect(Collectors.toList());
    }

    private List<LiquidityFinancingSnapshot> toFinancingSnapshots(List<FundFinancingBaseInfo> financingBaseInfoList) {
        if (CollectionUtil.isEmpty(financingBaseInfoList)) {
            return Collections.emptyList();
        }
        return financingBaseInfoList.stream()
                .map(item -> new LiquidityFinancingSnapshot(
                        item.getId(),
                        item.getFinancingCode(),
                        item.getActualLoanDate()))
                .collect(Collectors.toList());
    }

    private Map<Long, List<LiquidityFinancingOrganizationSnapshot>> toFinancingOrganizationSnapshotMap(Map<Long, List<FundOrganization>> organizationMap) {
        if (organizationMap == null || organizationMap.isEmpty()) {
            return Collections.emptyMap();
        }
        return organizationMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> Optional.ofNullable(entry.getValue()).orElse(Collections.emptyList()).stream()
                .map(item -> new LiquidityFinancingOrganizationSnapshot(
                        item.getId(),
                        item.getOrganizationName()))
                .collect(Collectors.toList())));
    }

    private List<LiquidityFinancingPayAccountSnapshot> toFinancingPayAccountSnapshots(List<FundFinancingPayAccount> payAccountList) {
        if (CollectionUtil.isEmpty(payAccountList)) {
            return Collections.emptyList();
        }
        return payAccountList.stream()
                .map(item -> new LiquidityFinancingPayAccountSnapshot(
                        item.getFinancingId(),
                        item.getBankAccountId(),
                        item.getAccountCategory()))
                .collect(Collectors.toList());
    }

    private List<LiquidityFinancingPledgeSnapshot> toFinancingPledgeSnapshots(List<FundFinancingPledgeInfo> pledgeInfoList) {
        if (CollectionUtil.isEmpty(pledgeInfoList)) {
            return Collections.emptyList();
        }
        return pledgeInfoList.stream()
                .map(item -> new LiquidityFinancingPledgeSnapshot(
                        item.getFinancingId(),
                        item.getContractId(),
                        item.getIsPledge(),
                        item.getIsSupervise()))
                .collect(Collectors.toList());
    }

    private List<LiquidityFinancingPledgeSnapshot> toDirectFinancingPledgeSnapshots(List<FundDirectFinancingPledgeInfo> pledgeInfoList) {
        if (CollectionUtil.isEmpty(pledgeInfoList)) {
            return Collections.emptyList();
        }
        return pledgeInfoList.stream()
                .map(item -> new LiquidityFinancingPledgeSnapshot(
                        item.getFinancingId(),
                        item.getContractId(),
                        item.getIsPledge(),
                        item.getIsSupervise()))
                .collect(Collectors.toList());
    }

    private List<LiquidityBankAccountSnapshot> toBankAccountSnapshots(List<BaseDataBankAccount> accountList) {
        if (CollectionUtil.isEmpty(accountList)) {
            return Collections.emptyList();
        }
        return accountList.stream()
                .map(item -> new LiquidityBankAccountSnapshot(
                        item.getId(),
                        item.getAccountType(),
                        item.getAccountName(),
                        item.getAccountNumber(),
                        item.getAccountBank()))
                .collect(Collectors.toList());
    }

    private List<LiquiditySpecialDateSnapshot> toSpecialDateSnapshots(List<BaseDataSpecialDate> specialDateList) {
        if (CollectionUtil.isEmpty(specialDateList)) {
            return Collections.emptyList();
        }
        return specialDateList.stream()
                .map(item -> new LiquiditySpecialDateSnapshot(
                        item.getSpecialDate(),
                        item.getSpecialType()))
                .collect(Collectors.toList());
    }



        /**
         * 非监管户返回默认账户，监管户返回监管账户，若账户未匹配到系统中的账户则返回-1
         * @param contractIdList
         * @return key: contractId, value: accountId
         */
    public Map<Long, Long> getContractAccountMap(List<Long> contractIdList) {
        if (CollectionUtil.isEmpty(contractIdList)) {
            return Collections.emptyMap();
        }
        Map<Long , FundPledgeSupervisedBO> fundPledgeSupervisedMap = Optional.ofNullable(financingPledgeInfoMapper.getFundSupervisedBo(contractIdList))
                .map(m -> m.stream().collect(Collectors.toMap(FundPledgeSupervisedBO::getContractId, Function.identity()))).orElse(Collections.emptyMap());
        Map<String, BaseDataBankAccount> accountMap = baseDataBankAccountService.accountQuery(new BaseDataBankAccountQueryREQ())
                .stream().collect(Collectors.toMap(m -> m.getAccountNumber().replaceAll(" ",""), Function.identity()));
        BaseDataBankAccount defaultAccount = accountMap.get("1202021219900394595");
        return contractIdList.stream().collect(Collectors.toMap(Function.identity() ,contractId -> {
            FundPledgeSupervisedBO bo = fundPledgeSupervisedMap.get(contractId);
            if(bo != null){
                return Optional.ofNullable(accountMap.get(bo.getAccountNumber().replaceAll(" ",""))).map(BaseDataBankAccount::getId).orElse(-1L);
            }else {
                return defaultAccount.getId();
            }
        }));

    }

}
