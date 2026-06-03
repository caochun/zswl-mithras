package cn.zswltech.mithras.service.service.liquiditymanage;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.thread.NamedThreadFactory;
import cn.zswltech.mithras.dto.basedata.BaseDataBankAccountQueryREQ;
import cn.zswltech.mithras.dto.liquiditymanage.base.ParameterBaseDetailRSP;
import cn.zswltech.mithras.dto.liquiditymanage.base.ParameterIndexDetailRSP;
import cn.zswltech.mithras.dto.liquiditymanage.liquidityIndex.LiquidityBoardDetailREQ;
import cn.zswltech.mithras.dto.liquiditymanage.liquidityIndex.LiquidityIndexDetailREQ;
import cn.zswltech.mithras.dto.liquiditymanage.liquidityIndex.LiquidityMismatchDetailREQ;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.capital.domain.enums.FinanceCashFlowItemEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.service.enums.fund.financing.FundFinancingStatusEnum;
import cn.zswltech.mithras.service.enums.fund.liquidity.FundParameterConfigType;
import cn.zswltech.mithras.service.enums.fund.liquidity.LiquidityIndexType;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingBaseInfo;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingPledgeInfo;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingRepayActual;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingBaseInfoService;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingPledgeInfoService;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingRepayActualService;
import cn.zswltech.mithras.service.mapper.fund.financing.FundFinancingPledgeInfoMapper;
import cn.zswltech.mithras.service.mapper.model.basedata.BaseDataBankAccount;
import cn.zswltech.mithras.service.mapper.model.basedata.BaseDataSpecialDate;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionBaseInfo;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionRecordInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractRentActual;
import cn.zswltech.mithras.service.mapper.model.fund.FundCredit;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingPayAccount;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingPledgeInfo;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingRepayActual;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptFlowDetail;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptFlowPlan;
import cn.zswltech.mithras.service.mapper.model.fund.receiptrepay.FundReceiptRepayBaseInfo;
import cn.zswltech.mithras.service.mapper.model.liquiditymanage.AccountBalanceBaseInfo;
import cn.zswltech.mithras.service.mapper.model.liquiditymanage.FundFinancingAccountSetting;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.basedata.BaseDataBankAccountService;
import cn.zswltech.mithras.service.service.basedata.BaseDataSpecialDateService;
import cn.zswltech.mithras.service.service.capital.write_off.bo.FundPledgeSupervisedBO;
import cn.zswltech.mithras.service.service.collection.CollectionBaseInfoService;
import cn.zswltech.mithras.service.service.collection.CollectionRecordInfoService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractRentActualService;
import cn.zswltech.mithras.service.service.fund.FundCreditService;
import cn.zswltech.mithras.service.service.fund.FundOrganizationService;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingBaseInfoService;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingPayAccountService;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingPledgeInfoService;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingRepayActualService;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptFlowDetailService;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptFlowPlanService;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptRepayBaseInfoService;
import cn.zswltech.mithras.service.service.fund.receiptrepay.FundReceiptRepayCashFlowService;
import cn.zswltech.mithras.service.service.lib.fund.financing.FundFinancingPayAccountLibService;
import cn.zswltech.mithras.service.service.monthly.MonthlyManagementBaseInfoService;
import cn.zswltech.mithras.service.service.projlifecycle.ProjectLifecycleService;
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
    private FundFinancingRepayActualService financingRepayActualService;
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
        LiquidityIndicatorHolder.BASE_DATA_BANK_ACCOUNT = baseDataBankAccountService.accountQuery(new BaseDataBankAccountQueryREQ()).stream().collect(Collectors.toMap(BaseDataBankAccount::getId, Function.identity()));
        LiquidityIndicatorHolder.BASE_DATA_SPECIAL_DATE = baseDataSpecialDateService.findAllSpecialDate().stream().collect(Collectors.toMap(BaseDataSpecialDate::getSpecialDate, Function.identity()));
        LiquidityIndicatorHolder.FUND_FINANCING_ACCOUNT_SETTING = accountSettingService.list().stream().collect(Collectors.groupingBy(FundFinancingAccountSetting::getAccountId));
        LiquidityIndicatorHolder.DEFAULT_ACCOUNT = LiquidityIndicatorHolder.BASE_DATA_BANK_ACCOUNT.values().stream().filter(f -> Objects.equals(f.getAccountNumber(), "1202021219900394595")).findFirst().orElse(new BaseDataBankAccount());
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
        LiquidityIndicatorHolder.CONTRACT_BASE_INFO =  contractBaseInfoList.stream().collect(Collectors.toMap(ContractBaseInfo::getId, Function.identity()));
        LiquidityIndicatorHolder.CONTRACT_IS_OVERDUE = SpringContextHolder.getBean(MonthlyManagementBaseInfoService.class).contractIsOverdue(contractIdList, now);
        LiquidityIndicatorHolder.COLLECTION_BASE_INFO = collectionBaseInfoList.stream().collect(Collectors.groupingBy(CollectionBaseInfo::getPlanCollectionDate));
        LiquidityIndicatorHolder.COLLECTION_BASE_INFO_MAX_PHASE = collectionBaseInfoList.stream().collect(Collectors.toMap(
                CollectionBaseInfo::getContractId, CollectionBaseInfo::getPhase, (m1, m2) -> m1 > m2 ? m1 : m2
        ));

        LiquidityIndicatorHolder.COLLECTION_RECORD_INFO = collectionRecordInfoService.listByCollectionIds(LiquidityIndicatorHolder.COLLECTION_BASE_INFO.values()
                .stream().flatMap(Collection::stream).map(CollectionBaseInfo::getId).collect(Collectors.toList())).stream().collect(Collectors.groupingBy(CollectionRecordInfo::getCollectionId));
        LiquidityIndicatorHolder.CONTRACT_ACCOUNT = getContractAccountMap(contractIdList);

        /**
         * 资金端
         */
        // 间融
        List<FundFinancingBaseInfo> financingBaseInfoList = financingBaseInfoService.list(Wrappers.<FundFinancingBaseInfo>lambdaQuery().eq(FundFinancingBaseInfo::getFinancingStatus, FundFinancingStatusEnum.CARRY_INTEREST.name()));
        List<Long> financingIdList = financingBaseInfoList.stream().map(FundFinancingBaseInfo::getId).collect(Collectors.toList());
        List<FundFinancingRepayActual> financingRepayActualList = financingRepayActualService.list(Wrappers.<FundFinancingRepayActual>lambdaQuery().in(FundFinancingRepayActual::getFinancingId, financingIdList));

        LiquidityIndicatorHolder.FUND_FINANCING_BASE_INFO = financingBaseInfoList.stream().collect(Collectors.toMap(FundFinancingBaseInfo::getId, Function.identity()));
        LiquidityIndicatorHolder.FUND_FINANCING_PAY_ACCOUNT = financingPayAccountService.listByFinancingIds(financingIdList).stream().collect(Collectors.groupingBy(FundFinancingPayAccount::getBankAccountId));
//        LiquidityIndicatorHolder.FUND_FINANCING_REPAY_ACTUAL = financingRepayActualList.stream().collect(Collectors.groupingBy(FundFinancingRepayActual::getRepayDate));


        // 直融
        List<FundDirectFinancingBaseInfo> directFinancingBaseInfoList = directFinancingBaseInfoService.list(Wrappers.<FundDirectFinancingBaseInfo>lambdaQuery().eq(FundDirectFinancingBaseInfo::getFinancingStatus, FundFinancingStatusEnum.CARRY_INTEREST.name()));
        List<Long> directFinancingIdList = directFinancingBaseInfoList.stream().map(FundDirectFinancingBaseInfo::getId).collect(Collectors.toList());
        List<FundDirectFinancingRepayActual> directFinancingRepayActualList = directFinancingRepayActualService.list(Wrappers.<FundDirectFinancingRepayActual>lambdaQuery().in(FundDirectFinancingRepayActual::getFinancingId, directFinancingIdList));

        LiquidityIndicatorHolder.FUND_DIRECT_FINANCING_BASE_INFO = directFinancingBaseInfoList.stream().collect(Collectors.toMap(FundDirectFinancingBaseInfo::getId, Function.identity()));
        LiquidityIndicatorHolder.FUND_DIRECT_FINANCING_REPAY_ACTUAL = directFinancingRepayActualList.stream().collect(Collectors.groupingBy(FundDirectFinancingRepayActual::getRepayDate));

        List<FundReceiptRepayBaseInfo> repayBaseInfoList = receiptRepayBaseInfoService.list(Wrappers.<FundReceiptRepayBaseInfo>lambdaQuery().in(FundReceiptRepayBaseInfo::getFinancingId, Stream.of(financingIdList, directFinancingIdList).flatMap(Collection::stream).collect(Collectors.toList())));
        LiquidityIndicatorHolder.FUND_RECEIPT_REPAY_BASE_INFO = repayBaseInfoList.stream().collect(Collectors.toMap(FundReceiptRepayBaseInfo::getId, Function.identity()));
        List<FundReceiptFlowPlan> flowPlanList = receiptFlowPlanService.list(Wrappers.<FundReceiptFlowPlan>lambdaQuery().in(FundReceiptFlowPlan::getReceiptRepayId, LiquidityIndicatorHolder.FUND_RECEIPT_REPAY_BASE_INFO.keySet())
                .eq(FundReceiptFlowPlan::getCashFlowItem, FinanceCashFlowItemEnum.REPAY.name()));
        LiquidityIndicatorHolder.FUND_RECEIPT_FLOW_PLAN = flowPlanList.stream().collect(Collectors.groupingBy(FundReceiptFlowPlan::getCashFlowDate));

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
            LiquidityIndicatorIndexHolder.COLLECTION_BASE_INFO = collectionBaseInfoList.stream().collect(Collectors.groupingBy(CollectionBaseInfo::getPlanCollectionDate));
            LiquidityIndicatorIndexHolder.COLLECTION_RECORD_INFO = collectionRecordInfoService.listByCollectionIds(LiquidityIndicatorIndexHolder.COLLECTION_BASE_INFO.values()
                    .stream().flatMap(Collection::stream).map(CollectionBaseInfo::getId).collect(Collectors.toList())).stream().collect(Collectors.groupingBy(CollectionRecordInfo::getCollectionId));

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
                LiquidityIndicatorIndexHolder.FUND_FINANCING_PLEDGE_INFO = financingPledgeInfoService.list(Wrappers.<FundFinancingPledgeInfo>lambdaQuery().in(FundFinancingPledgeInfo::getFinancingId, financingIdList)).stream()
                        .filter(a -> Objects.equals(a.getIsPledge(), Boolean.TRUE) || Objects.equals(a.getIsSupervise(), Boolean.TRUE))
                        .collect(Collectors.groupingBy(FundFinancingPledgeInfo::getFinancingId));
                List<FundCredit> effectCreditList = fundCreditService.list(Wrappers.<FundCredit>lambdaQuery().eq(FundCredit::getEffective, Boolean.TRUE));
                LiquidityIndicatorIndexHolder.CREDIT_LIMIT_DETAIL = fundCreditService.queryLimitDetailBatch(effectCreditList, false);
            });

            // 直融
            CompletableFuture<Void> directFuture = CompletableFuture.runAsync(() -> {
                LiquidityIndicatorIndexHolder.FUND_DIRECT_FINANCING_PLEDGE_INFO = directFinancingPledgeInfoService.list(Wrappers.<FundDirectFinancingPledgeInfo>lambdaQuery().in(FundDirectFinancingPledgeInfo::getFinancingId, directFinancingIdList)).stream()
                        .filter(a -> Objects.equals(a.getIsPledge(), Boolean.TRUE) || Objects.equals(a.getIsSupervise(), Boolean.TRUE))
                        .collect(Collectors.groupingBy(FundDirectFinancingPledgeInfo::getFinancingId));
            });

            // 还本付息
            CompletableFuture<Void> repayFuture = CompletableFuture.runAsync(() -> {
                List<FundReceiptRepayBaseInfo> repayBaseInfoList = receiptRepayBaseInfoService.list(Wrappers.<FundReceiptRepayBaseInfo>lambdaQuery().in(FundReceiptRepayBaseInfo::getFinancingId, Stream.of(financingIdList, directFinancingIdList).flatMap(Collection::stream).collect(Collectors.toList())));
                Map<Long, FundReceiptRepayBaseInfo> repayBaseInfoMap = repayBaseInfoList.stream().collect(Collectors.toMap(FundReceiptRepayBaseInfo::getId, Function.identity()));

                List<FundReceiptFlowPlan> flowPlanList = receiptFlowPlanService.list(Wrappers.<FundReceiptFlowPlan>lambdaQuery().in(FundReceiptFlowPlan::getReceiptRepayId, repayBaseInfoMap.keySet()));

                LiquidityIndicatorIndexHolder.FUND_RECEIPT_FLOW_PLAN = flowPlanList.stream().collect(Collectors.groupingBy(FundReceiptFlowPlan::getCashFlowDate, Collectors.groupingBy(FundReceiptFlowPlan::getCashFlowItem)));

                Map<String, List<FundReceiptFlowDetail>> flowDetailList = receiptFlowDetailService.listByReceiptRepayIds(flowPlanList.stream().map(FundReceiptFlowPlan::getReceiptRepayId).collect(Collectors.toList()));
                LiquidityIndicatorIndexHolder.FUND_RECEIPT_FLOW_DETAIL = flowDetailList.values().stream().flatMap(Collection::stream).collect(Collectors.groupingBy(FundReceiptFlowDetail::getReceiptRepayId,
                        Collectors.groupingBy(FundReceiptFlowDetail::getCashFlowCode)));
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

            LiquidityIndicatorBoardHolder.COLLECTION_RECORD_INFO = collectionRecordInfoService.listByCollectionIds(collectionBaseInfoMap.values()
                    .stream().flatMap(Collection::stream).map(CollectionBaseInfo::getId).collect(Collectors.toList())).stream().collect(Collectors.groupingBy(CollectionRecordInfo::getCollectionId));

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
                LiquidityIndicatorMismatchHolder.CONTRACT_RENT_ACTUAL = rentActualList.stream().collect(Collectors.groupingBy(ContractRentActual::getCashFlowDate));
                LiquidityIndicatorMismatchHolder.CONTRACT_BASE_INFO = contractBaseInfoList.stream().collect(Collectors.toMap(ContractBaseInfo::getId, Function.identity()));
            });

            CompletableFuture<Void> future22 = CompletableFuture.runAsync(() -> {
                List<CollectionBaseInfo> collectionBaseInfoList = collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                        .in(CollectionBaseInfo::getContractId, contractIdList)
                        .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                        .gt(CollectionBaseInfo::getPhase, 0));
                Map<Long, List<CollectionBaseInfo>> collectionBaseInfoMap = collectionBaseInfoList.stream().collect(Collectors.groupingBy(CollectionBaseInfo::getContractId));
                LiquidityIndicatorMismatchHolder.COLLECTION_RECORD_INFO = collectionRecordInfoService.listByCollectionIds(collectionBaseInfoMap.values()
                        .stream().flatMap(Collection::stream).map(CollectionBaseInfo::getId).collect(Collectors.toList())).stream().collect(Collectors.groupingBy(CollectionRecordInfo::getCollectionId));
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
                LiquidityIndicatorMismatchHolder.FUND_FINANCING_BASE_INFO = financingBaseInfoList.stream().collect(Collectors.toMap(FundFinancingBaseInfo::getId, Function.identity()));
                LiquidityIndicatorMismatchHolder.FUND_FINANCING_PLEDGE_INFO = financingPledgeInfoService.list(Wrappers.<FundFinancingPledgeInfo>lambdaQuery().in(FundFinancingPledgeInfo::getFinancingId, financingIdList)).stream()
                        .filter(a -> Objects.equals(a.getIsPledge(), Boolean.TRUE) || Objects.equals(a.getIsSupervise(), Boolean.TRUE))
                        .collect(Collectors.groupingBy(FundFinancingPledgeInfo::getFinancingId));
                LiquidityIndicatorMismatchHolder.FUND_ORGANIZATION_INFO = organizationService.getBatchByFinancingId(financingIdList);
            });

            CompletableFuture<Void> directFuture = CompletableFuture.runAsync(() -> {
                // 直融
                LiquidityIndicatorMismatchHolder.FUND_DIRECT_FINANCING_BASE_INFO = directFinancingBaseInfoList.stream().collect(Collectors.toMap(FundDirectFinancingBaseInfo::getId, Function.identity()));
                LiquidityIndicatorMismatchHolder.FUND_DIRECT_FINANCING_PLEDGE_INFO = directFinancingPledgeInfoService.list(Wrappers.<FundDirectFinancingPledgeInfo>lambdaQuery().in(FundDirectFinancingPledgeInfo::getFinancingId, directFinancingIdList)).stream()
                        .filter(a -> Objects.equals(a.getIsPledge(), Boolean.TRUE) || Objects.equals(a.getIsSupervise(), Boolean.TRUE))
                        .collect(Collectors.groupingBy(FundDirectFinancingPledgeInfo::getFinancingId));
            });

            CompletableFuture<Void> repayFuture = CompletableFuture.runAsync(() -> {
                // 还本付息
                List<FundReceiptRepayBaseInfo> repayBaseInfoList = receiptRepayBaseInfoService.list(Wrappers.<FundReceiptRepayBaseInfo>lambdaQuery().in(FundReceiptRepayBaseInfo::getFinancingId, Stream.of(financingIdList, directFinancingIdList).flatMap(Collection::stream).collect(Collectors.toList())));
                LiquidityIndicatorMismatchHolder.FUND_RECEIPT_REPAY_BASE_INFO = repayBaseInfoList.stream().collect(Collectors.toMap(FundReceiptRepayBaseInfo::getId, Function.identity()));
                List<FundReceiptFlowPlan> flowPlanList = receiptFlowPlanService.list(Wrappers.<FundReceiptFlowPlan>lambdaQuery().in(FundReceiptFlowPlan::getReceiptRepayId, LiquidityIndicatorMismatchHolder.FUND_RECEIPT_REPAY_BASE_INFO.keySet()));

                LiquidityIndicatorMismatchHolder.FUND_RECEIPT_FLOW_PLAN = flowPlanList.stream().collect(Collectors.groupingBy(FundReceiptFlowPlan::getCashFlowDate, Collectors.groupingBy(FundReceiptFlowPlan::getCashFlowItem)));
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
