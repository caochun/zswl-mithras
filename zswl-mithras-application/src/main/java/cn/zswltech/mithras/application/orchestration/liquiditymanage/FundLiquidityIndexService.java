package cn.zswltech.mithras.application.orchestration.liquiditymanage;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailRSP;
import cn.zswltech.mithras.dto.liquiditymanage.base.LiquidityColorVo;
import cn.zswltech.mithras.dto.liquiditymanage.dayreport.RepayPrincipalInterestListREQ;
import cn.zswltech.mithras.dto.liquiditymanage.liquidityindex.*;
import cn.zswltech.mithras.foundation.enums.CashFlowItemEnum;
import cn.zswltech.mithras.basedata.enums.BaseDataBankAccountTypeEnum;
import cn.zswltech.mithras.capital.enums.FinanceCashFlowItemEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.fund.enums.financing.FinancingTypeEnum;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingAccountTypeEnum;
import cn.zswltech.mithras.fund.directfinancing.model.FundDirectFinancingBaseInfo;
import cn.zswltech.mithras.fund.directfinancing.model.FundDirectFinancingPledgeInfo;
import cn.zswltech.mithras.application.orchestration.fund.direct.service.FundDirectFinancingBaseInfoService;
import cn.zswltech.mithras.fund.mapper.financing.FundFinancingBaseInfoMapper;
import cn.zswltech.mithras.basedata.mapper.model.BaseDataBankAccount;
import cn.zswltech.mithras.basedata.mapper.model.BaseDataSpecialDate;
import cn.zswltech.mithras.collection.model.CollectionBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractRentActual;
import cn.zswltech.mithras.contract.model.contract.ContractTenantry;
import cn.zswltech.mithras.fund.model.FundOrganization;
import cn.zswltech.mithras.fund.model.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.fund.model.financing.FundFinancingPledgeInfo;
import cn.zswltech.mithras.fund.model.receiptrepay.FundReceiptFlowPlan;
import cn.zswltech.mithras.fund.model.receiptrepay.FundReceiptRepayBaseInfo;
import cn.zswltech.mithras.liquidity.model.FundFinancingAccountSetting;
import cn.zswltech.mithras.liquidity.service.FundLiquidityIndexApplicationService;
import cn.zswltech.mithras.liquidity.service.LiquidityIndicatorMismatchHolder;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.basedata.service.BaseDataSpecialDateService;
import cn.zswltech.mithras.collection.application.CollectionBaseInfoService;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.contract.core.ContractTenantryService;
import cn.zswltech.mithras.fund.application.organization.FundOrganizationService;
import cn.zswltech.mithras.liquidity.service.cal.AbstractLiquidityCalculator;
import cn.zswltech.mithras.liquidity.service.cal.bo.LiquidityBoardCalculatorBo;
import cn.zswltech.mithras.liquidity.service.cal.bo.LiquidityDailyMaxBalanceCalculatorBo;
import cn.zswltech.mithras.liquidity.service.cal.bo.LiquidityIndexCalculatorBo;
import cn.zswltech.mithras.liquidity.service.cal.board.DailyMaxAvailableBalanceCalculator;
import cn.zswltech.mithras.fund.application.financing.dto.RepayPrincipalInterestDto;
import cn.zswltech.mithras.application.orchestration.monthly.MonthlyManagementBaseInfoService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projlifecycle.ProjectLifecycleService;
import cn.zswltech.mithras.foundation.util.BigDecimalUtil;
import cn.zswltech.mithras.foundation.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cn.zswltech.mithras.liquidity.service.LiquidityIndicatorHolder.*;

/**
 * FundLiquidityIndexService
 *
 * @author chenyifei
 * @since 2024/12/18
 */
@Slf4j
@Service
public class FundLiquidityIndexService implements FundLiquidityIndexApplicationService {

    @Autowired
    private List<AbstractLiquidityCalculator<LiquidityIndexCalculatorBo>> calculatorIndexList;
    @Autowired
    private List<AbstractLiquidityCalculator<LiquidityBoardCalculatorBo>> calculatorBoardList;
    @Resource
    private DailyMaxAvailableBalanceCalculator dailyMaxAvailableBalanceCalculator;
    @Resource
    private LiquidityDataService liquidityDataService;
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
    private FundFinancingAccountSettingService fundFinancingAccountSettingService;
    @Resource
    private FundFinancingBaseInfoMapper fundFinancingBaseInfoMapper;
    @Resource
    private FundOrganizationService fundOrganizationService;
    @Resource
    private MonthlyManagementBaseInfoService monthlyManagementBaseInfoService;
    @Resource
    private ProjectLifecycleService projectLifecycleService;
    @Resource
    private BaseDataSpecialDateService baseDataSpecialDateService;
    @Resource
    private FundDirectFinancingBaseInfoService fundDirectFinancingBaseInfoService;

    @Override
    public LiquidityIndexDetailRSP manageIndex(LiquidityIndexDetailREQ req) {
        liquidityDataService.dataQueryIndex(req);
        LiquidityIndexDetailRSP rsp = new LiquidityIndexDetailRSP();
        LiquidityIndexCalculatorBo bo = BeanUtil.copyProperties(req, LiquidityIndexCalculatorBo.class);
        for (AbstractLiquidityCalculator<LiquidityIndexCalculatorBo> calculator : calculatorIndexList.stream().sorted(Comparator.comparing(AbstractLiquidityCalculator::sort)).collect(Collectors.toList())) {
            calculator.calculate(rsp, bo);
        }
        return rsp;
    }

    @Override
    public LiquidityBoardDetailSumRSP manageBoard(LiquidityBoardDetailREQ req) {
        liquidityDataService.dataQueryBoard(req);
        LiquidityBoardDetailSumRSP result = new LiquidityBoardDetailSumRSP();
        List<LiquidityBoardDetailRSP> rspList = new ArrayList<>();
        List<LiquidityBoardDetailRSP> rspAllList = new ArrayList<>();
        // 从前一天开始计算，在处理结果的时候踢掉
        LocalDate date = req.getQueryDateStart().minusDays(1);
        // 需要计算到的时间
        LocalDate calculateEnd = req.getQueryDateEnd().plusDays(req.getPredictDay() - 1);
        LiquidityBoardCalculatorBo bo = BeanUtil.copyProperties(req, LiquidityBoardCalculatorBo.class);
        boolean firstFlag = true;
        do {
            LiquidityBoardDetailRSP rsp = new LiquidityBoardDetailRSP();
            bo.setQueryDate(date);
            for (AbstractLiquidityCalculator<LiquidityBoardCalculatorBo> calculator : calculatorBoardList.stream().sorted(Comparator.comparing(AbstractLiquidityCalculator::sort)).collect(Collectors.toList())) {
                calculator.calculate(rsp, bo);
            }
            if (!date.isAfter(req.getQueryDateEnd())) {
                rspList.add(rsp);
            }
            // 第一次计算完成将后面需要数据塞到bo中
            if (firstFlag) {
                bo.setSuperviseAndNonSuperviseBalance(Pair.of(rsp.getSupervisedAccountFunds(), rsp.getNonSupervisedAccountFunds()));
                firstFlag = false;
            }
            date = date.plusDays(1);
            rspAllList.add(rsp);
        } while (!date.isAfter(calculateEnd));

        // 可用余额计算
        if (CollectionUtil.isNotEmpty(rspList)) {
            LiquidityDailyMaxBalanceCalculatorBo balanceBo = BeanUtil.copyProperties(bo, LiquidityDailyMaxBalanceCalculatorBo.class);
            balanceBo.setDetailMap(rspAllList.stream().collect(Collectors.toMap(LiquidityBoardDetailRSP::getDate, Function.identity())));
            for (LiquidityBoardDetailRSP detailRsp : rspList) {
                balanceBo.setQueryDate(detailRsp.getDate());
                dailyMaxAvailableBalanceCalculator.calculate(detailRsp, balanceBo);
            }
        }
        rspList.removeIf(o -> o.getDate().equals(req.getQueryDateStart().minusDays(1)));
        result.setList(rspList);
        result.setSum(boardSumHandle(rspList));
        return result;
    }

    @Override
    public List<LiquidityMismatchDetailRSP> manageMismatch(LiquidityMismatchDetailREQ req) {
        /**
         * 数据范围：融资合同中关联质押or监管的资产合同对应融资合同每期还款金额期间内，租金合计值小于融资合同的当期还本付息金额
         */
        List<LiquidityMismatchDetailRSP> rspList = new ArrayList<>();
        liquidityDataService.dataQueryMismatch(req);
        // 实际租金表，还本付息维度
        Map<Long, List<ContractRentActual>> contractActualMap = buildContractActualByReceiptId();
        if (CollectionUtil.isEmpty(contractActualMap)) {
            return rspList;
        }
        List<Long> financingIdList = Stream.of(LiquidityIndicatorMismatchHolder.FUND_FINANCING_PLEDGE_INFO.keySet(), LiquidityIndicatorMismatchHolder.FUND_DIRECT_FINANCING_PLEDGE_INFO.keySet()).flatMap(Collection::stream).collect(Collectors.toList());
        // 还本付息维度实际贷款日期
        Map<Long, LocalDate> receiptMap = LiquidityIndicatorMismatchHolder.FUND_RECEIPT_REPAY_BASE_INFO.values().stream().filter(f -> financingIdList.contains(f.getFinancingId()))
                .collect(Collectors.toMap(FundReceiptRepayBaseInfo::getId, receipt -> {
                    if (receipt.getFinancingType() == null) {
                        return LiquidityIndicatorMismatchHolder.FUND_FINANCING_BASE_INFO.get(receipt.getFinancingId()).getActualLoanDate();
                    } else {
                        return LiquidityIndicatorMismatchHolder.FUND_DIRECT_FINANCING_BASE_INFO.get(receipt.getFinancingId()).getCarryInterestTime();
                    }
                }));
        Map<Long, List<FundReceiptFlowPlan>> flowPlanMap = LiquidityIndicatorMismatchHolder.FUND_RECEIPT_FLOW_PLAN.values().stream().map(Map::values).flatMap(Collection::stream).flatMap(Collection::stream).filter(f -> {
            return receiptMap.containsKey(f.getReceiptRepayId()) &&
                    Objects.equals(f.getCashFlowItem(), FinanceCashFlowItemEnum.REPAY.name());
        }).collect(Collectors.groupingBy(FundReceiptFlowPlan::getReceiptRepayId));

        for (Map.Entry<Long, List<FundReceiptFlowPlan>> entry : flowPlanMap.entrySet()) {
            List<ContractRentActual> contractRentActualList = contractActualMap.get(entry.getKey());
            if (CollectionUtil.isNotEmpty(contractRentActualList)) {
                List<FundReceiptFlowPlan> flowPlanListSort = entry.getValue().stream().sorted(Comparator.comparing(FundReceiptFlowPlan::getCashFlowPhase)).collect(Collectors.toList());
                LocalDate startDate = flowPlanListSort.get(0).getCashFlowPhase() != 1 ? flowPlanListSort.get(0).getCashFlowDate() : receiptMap.get(entry.getKey());
                // 合同下的现金流维度
                for (FundReceiptFlowPlan flowPlan : flowPlanListSort) {
                    if (LongUtil.null2zero(flowPlan.getPrincipalAmount()) == 0L) {
                        // 不含本金不参与错配逻辑
                        continue;
                    }
                    // 现金流时间是否在查询范围内, 若不在则跳过
                    if (!flowPlan.getCashFlowDate().isBefore(req.getQueryDateStart()) && !flowPlan.getCashFlowDate().isAfter(req.getQueryDateEnd())) {
                        LocalDate finalStartDate = startDate;
                        List<ContractRentActual> matchContractRentActualList = contractRentActualList.stream().filter(f -> {
                            // 匹配到租金表中的应还日期
                            return f.getCashFlowDate().isAfter(finalStartDate) && !f.getCashFlowDate().isAfter(flowPlan.getCashFlowDate());
                        }).collect(Collectors.toList());
                        if (CollectionUtil.isNotEmpty(matchContractRentActualList)) {
                            long rentSum = matchContractRentActualList.stream().mapToLong(m -> LongUtil.null2zero(m.getRent())).sum();
                            if (LongUtil.null2zero(flowPlan.getPrincipalAmount()) + LongUtil.null2zero(flowPlan.getInterestAmount()) <= rentSum) {
                                // 未错配
                                continue;
                            }
                        }
                        rspList.add(buildResult(flowPlan, matchContractRentActualList));
                    }
                    startDate = flowPlan.getCashFlowDate();
                }

            }
        }
        // 现金流流出时间正序排序
        rspList = rspList.stream().filter(Objects::nonNull).sorted(Comparator.comparing(LiquidityMismatchDetailRSP::getCashOutflowTime)).collect(Collectors.toList());
        return rspList;
    }

    public LiquidityMismatchDetailRSP buildResult(FundReceiptFlowPlan flowPlan, List<ContractRentActual> matchContractRentActualList) {
        LiquidityMismatchDetailRSP rsp = new LiquidityMismatchDetailRSP();

        List<LiquidityMismatchDetailRSP.LiquidityMismatchCashInFlow> cashInFlowList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(matchContractRentActualList)) {
            cashInFlowList = matchContractRentActualList.stream().map(item -> {
                LiquidityMismatchDetailRSP.LiquidityMismatchCashInFlow cashInFlow = new LiquidityMismatchDetailRSP.LiquidityMismatchCashInFlow();
                cashInFlow.setCashInflowAmount(item.getRent());
                cashInFlow.setCashInflowTime(item.getCashFlowDate());
                ContractBaseInfo contractBaseInfo = LiquidityIndicatorMismatchHolder.CONTRACT_BASE_INFO.get(item.getContractId());
                cashInFlow.setPledgeContractCode(Optional.ofNullable(contractBaseInfo).map(ContractBaseInfo::getContractCode).orElse(null));
                return cashInFlow;
            }).collect(Collectors.toList());
        }

        FundReceiptRepayBaseInfo receiptRepayBaseInfo = LiquidityIndicatorMismatchHolder.FUND_RECEIPT_REPAY_BASE_INFO.get(flowPlan.getReceiptRepayId());
        if (receiptRepayBaseInfo.getFinancingType() == null) {
            // 间融
            FundFinancingBaseInfo financingBaseInfo = LiquidityIndicatorMismatchHolder.FUND_FINANCING_BASE_INFO.get(receiptRepayBaseInfo.getFinancingId());
            List<FundOrganization> organizationList = LiquidityIndicatorMismatchHolder.FUND_ORGANIZATION_INFO.get(receiptRepayBaseInfo.getFinancingId());
            if (CollectionUtil.isNotEmpty(organizationList)) {
                rsp.setOrganizationId(organizationList.stream().map(FundOrganization::getId).collect(Collectors.toList()));
                rsp.setOrganizationName(organizationList.stream().map(FundOrganization::getOrganizationName).collect(Collectors.toList()));
            }
            rsp.setFinancingCode(financingBaseInfo.getFinancingCode());
        } else {
            // 直融
            FundDirectFinancingBaseInfo directFinancingBaseInfo = LiquidityIndicatorMismatchHolder.FUND_DIRECT_FINANCING_BASE_INFO.get(receiptRepayBaseInfo.getFinancingId());
            rsp.setOrganizationName(Collections.singletonList(directFinancingBaseInfo.getProductName()));
            rsp.setFinancingCode(directFinancingBaseInfo.getFinancingCode());
        }
        rsp.setFinancingId(receiptRepayBaseInfo.getFinancingId());
        rsp.setPhase(flowPlan.getCashFlowPhase());
        rsp.setCashOutflowAmount(LongUtil.null2zero(flowPlan.getPrincipalAmount()) + LongUtil.null2zero(flowPlan.getInterestAmount()));
        rsp.setCashOutflowTime(flowPlan.getCashFlowDate());
        rsp.setCashInFlowList(cashInFlowList);
        if (CollectionUtil.isNotEmpty(cashInFlowList)) {
            long amount = cashInFlowList.stream().mapToLong(m -> LongUtil.null2zero(m.getCashInflowAmount())).sum();
            LiquidityMismatchDetailRSP.LiquidityMismatchCashInFlow cashInFlow = cashInFlowList.stream().max(Comparator.comparing(LiquidityMismatchDetailRSP.LiquidityMismatchCashInFlow::getCashInflowTime)).orElse(new LiquidityMismatchDetailRSP.LiquidityMismatchCashInFlow());
            rsp.setCashInflowAmount(amount);
            rsp.setCashInflowTime(cashInFlow.getCashInflowTime());
        }
        return rsp;
    }


    public Map<Long, List<ContractRentActual>> buildContractActualByReceiptId() {
        // 质押监管明细
//        Map<Long, FundFinancingPledgeInfo> inDirectPledgeMap = LiquidityIndicatorMismatchHolder.FUND_FINANCING_PLEDGE_INFO.values().stream().flatMap(Collection::stream)
//                .collect(Collectors.toMap(FundFinancingPledgeInfo::getContractId, Function.identity()));
        Map<Long, List<FundFinancingPledgeInfo>> inDirectPledgeMapTemp = LiquidityIndicatorMismatchHolder.FUND_FINANCING_PLEDGE_INFO.values().stream().flatMap(Collection::stream)
                .collect(Collectors.groupingBy(FundFinancingPledgeInfo::getContractId));
        Map<Long, FundFinancingPledgeInfo> inDirectPledgeMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(inDirectPledgeMapTemp)) {
            for (Map.Entry<Long, List<FundFinancingPledgeInfo>> entry : inDirectPledgeMapTemp.entrySet()) {
                List<FundFinancingPledgeInfo> list = entry.getValue();
                if (list.size() == 1) {
                    inDirectPledgeMap.put(entry.getKey(), list.get(0));
                } else {
                    // 优先取监管，再取质押
                    List<FundFinancingPledgeInfo> superviseList = list.stream().filter(e -> Objects.nonNull(e.getIsSupervise()) && e.getIsSupervise()).collect(Collectors.toList());
                    List<FundFinancingPledgeInfo> pledgeList = list.stream().filter(e -> Objects.nonNull(e.getIsPledge()) && e.getIsPledge()).collect(Collectors.toList());
                    if (CollectionUtil.isNotEmpty(superviseList)) {
                        inDirectPledgeMap.put(entry.getKey(), superviseList.get(0));
                    } else if (CollectionUtil.isNotEmpty(pledgeList)) {
                        inDirectPledgeMap.put(entry.getKey(), pledgeList.get(0));
                    }
                }
            }
        }
//        Map<Long, FundDirectFinancingPledgeInfo> directPledgeMap = LiquidityIndicatorMismatchHolder.FUND_DIRECT_FINANCING_PLEDGE_INFO.values().stream().flatMap(Collection::stream)
//                .collect(Collectors.toMap(FundDirectFinancingPledgeInfo::getContractId, Function.identity()));
        Map<Long, List<FundDirectFinancingPledgeInfo>> directPledgeMapTemp = LiquidityIndicatorMismatchHolder.FUND_DIRECT_FINANCING_PLEDGE_INFO.values().stream().flatMap(Collection::stream)
                .collect(Collectors.groupingBy(FundDirectFinancingPledgeInfo::getContractId));
        Map<Long, FundDirectFinancingPledgeInfo> directPledgeMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(directPledgeMapTemp)) {
            for (Map.Entry<Long, List<FundDirectFinancingPledgeInfo>> entry : directPledgeMapTemp.entrySet()) {
                List<FundDirectFinancingPledgeInfo> list = entry.getValue();
                if (list.size() == 1) {
                    directPledgeMap.put(entry.getKey(), list.get(0));
                } else {
                    // 优先取监管，再取质押
                    List<FundDirectFinancingPledgeInfo> superviseList = list.stream().filter(e -> Objects.nonNull(e.getIsSupervise()) && e.getIsSupervise()).collect(Collectors.toList());
                    List<FundDirectFinancingPledgeInfo> pledgeList = list.stream().filter(e -> Objects.nonNull(e.getIsPledge()) && e.getIsPledge()).collect(Collectors.toList());
                    if (CollectionUtil.isNotEmpty(superviseList)) {
                        directPledgeMap.put(entry.getKey(), superviseList.get(0));
                    } else if (CollectionUtil.isNotEmpty(pledgeList)) {
                        directPledgeMap.put(entry.getKey(), pledgeList.get(0));
                    }
                }
            }
        }

        Set<Long> contractIdList = Stream.of(Optional.ofNullable(inDirectPledgeMap).map(Map::keySet).orElse(Collections.emptySet()), Optional.ofNullable(directPledgeMap).map(Map::keySet).orElse(Collections.emptySet()))
                .flatMap(Collection::stream).collect(Collectors.toSet());

        Map<Long, Long> idByReceiptIdInDirectMap = LiquidityIndicatorMismatchHolder.FUND_RECEIPT_REPAY_BASE_INFO.values().stream().filter(f -> Objects.isNull(f.getFinancingType()))
                .collect(Collectors.toMap(FundReceiptRepayBaseInfo::getFinancingId, FundReceiptRepayBaseInfo::getId));
        Map<Long, Long> idByReceiptIdDirectMap = LiquidityIndicatorMismatchHolder.FUND_RECEIPT_REPAY_BASE_INFO.values().stream().filter(f -> Objects.equals(f.getFinancingType(), FinancingTypeEnum.DIRECT.name()))
                .collect(Collectors.toMap(FundReceiptRepayBaseInfo::getFinancingId, FundReceiptRepayBaseInfo::getId));

        if (CollectionUtil.isEmpty(contractIdList)) {
            return null;
        }
        // 资产合同与还本付息映射关系
        Map<Long, Long> receiptIdMap = contractIdList.stream().collect(Collectors.toMap(Function.identity(), contractId -> {
            FundFinancingPledgeInfo pledgeInfoInDirect = inDirectPledgeMap.get(contractId);
            FundDirectFinancingPledgeInfo pledgeInfoDirect = directPledgeMap.get(contractId);
            Long inDirectReceiptId = Optional.ofNullable(pledgeInfoInDirect).map(FundFinancingPledgeInfo::getFinancingId).map(idByReceiptIdInDirectMap::get).orElse(null);
            Long directReceiptId = Optional.ofNullable(pledgeInfoDirect).map(FundDirectFinancingPledgeInfo::getFinancingId).map(idByReceiptIdDirectMap::get).orElse(null);
            return Optional.ofNullable(inDirectReceiptId).orElse(directReceiptId);
        }));

        // 取租金表，通过监管质押数据改为还本付息维度
        return LiquidityIndicatorMismatchHolder.CONTRACT_RENT_ACTUAL.values().stream()
                .filter(Objects::nonNull).flatMap(Collection::stream)
                .filter(f -> contractIdList.contains(f.getContractId())).collect(Collectors.groupingBy(m -> receiptIdMap.get(m.getContractId())));
    }

    private LiquidityBoardDetailRSP boardSumHandle(List<LiquidityBoardDetailRSP> detailList) {
        LiquidityBoardDetailRSP sumRsp = new LiquidityBoardDetailRSP();
        if (CollectionUtil.isNotEmpty(detailList)) {
            for (LiquidityBoardDetailRSP detail : detailList) {
                for (Field field : ReflectUtil.getFields(LiquidityBoardDetailRSP.class)) {
                    if (field.getType() == LiquidityColorVo.class) {
                        LiquidityColorVo detailVo = (LiquidityColorVo) ReflectUtil.getFieldValue(detail, field);
                        LiquidityColorVo sumVo = (LiquidityColorVo) Optional.ofNullable(ReflectUtil.getFieldValue(sumRsp, field)).orElse(new LiquidityColorVo());
                        LiquidityColorVo liquidityColorVo = new LiquidityColorVo(BigDecimalUtil.null2zero(sumVo.getValue()).add(BigDecimalUtil.null2zero(detailVo.getValue())), null);
                        ReflectUtil.setFieldValue(sumRsp, field, liquidityColorVo);
                    }
                }
            }
        }
        return sumRsp;
    }

    @Override
    public List<LiquidityBoardRentIncomeRSP> manageRentIncome(LiquidityBoardRentIncomeREQ req) {
        // 首先异步计算真正生效的日期区间
        CompletableFuture<List<LocalDate>> listCompletableFuture = CompletableFuture.supplyAsync(() -> getEffectDateList(req));
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
        List<CollectionBaseInfo> actualCollectionBaseInfoList = new ArrayList<>();
        List<LocalDate> dateList;
        try {
            dateList = listCompletableFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("流动性看板-租金流入获取日期区间失败", e);
            return Collections.emptyList();
        }
        if (CollUtil.isEmpty(dateList)) {
            return Collections.emptyList();
        }
        if (CollUtil.isEmpty(COLLECTION_BASE_INFO)) {
            // 初始化
            try {
                accountBalanceBaseInfoService.init(false, null);
            } catch (Exception e) {
                return Collections.emptyList();
            }
        }
        COLLECTION_BASE_INFO.forEach((k, v) -> {
            if (k.isAfter(req.getQueryDateStart().minusDays(1)) && k.isBefore(req.getQueryDateEnd().plusDays(1))) {
                actualCollectionBaseInfoList.addAll(v);
            }
        });

        List<Long> contractIsOverdue = monthlyManagementBaseInfoService.contractIsOverdue(contractIdList, LocalDate.now());
        collectionBaseInfoList = actualCollectionBaseInfoList.stream().filter(f -> {
            boolean b = f.getPlanCollectionDate().isAfter(dateList.get(0).minusDays(1))
                    && f.getPlanCollectionDate().isBefore(dateList.get(dateList.size() - 1).plusDays(1));
            return !contractIsOverdue.contains(f.getContractId()) && b;
        }).collect(Collectors.toList());
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
        List<LiquidityBoardRentIncomeRSP> rspList = new ArrayList<>();
        for (CollectionBaseInfo collectionBaseInfo : collectionBaseInfoList) {
            LiquidityBoardRentIncomeRSP rsp = new LiquidityBoardRentIncomeRSP();
            rsp.setContractId(collectionBaseInfo.getContractId());
            rsp.setContractCode(collectionBaseInfo.getContractCode());
            rsp.setShouldPayAmount(LongUtil.null2zero(collectionBaseInfo.getPlanCollectionAmount()).toString());
            rsp.setExpireDate(collectionBaseInfo.getPlanCollectionDate().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
            rsp.setTenantName(Objects.nonNull(tenantryMap.get(collectionBaseInfo.getContractId())) ? clientIdNameMap.get(tenantryMap.get(collectionBaseInfo.getContractId()).getLesseeId()) : null);
            rsp.setTenantId(Objects.nonNull(tenantryMap.get(collectionBaseInfo.getContractId())) ? tenantryMap.get(collectionBaseInfo.getContractId()).getLesseeId() : null);
            Long accountId = CONTRACT_ACCOUNT.get(collectionBaseInfo.getContractId());
            BaseDataBankAccount baseDataBankAccount = BASE_DATA_BANK_ACCOUNT.get(accountId);
            if (Objects.nonNull(baseDataBankAccount)) {
                rsp.setIncomeAccount(baseDataBankAccount.getAccountName());
                rsp.setIncomeAccountProperty(Optional.ofNullable(BaseDataBankAccountTypeEnum.find(baseDataBankAccount.getAccountType()))
                        .map(BaseDataBankAccountTypeEnum::getDisplay).orElse(null));
            }
            rspList.add(rsp);
        }
        rspList.sort(Comparator.comparing(LiquidityBoardRentIncomeRSP::getExpireDate));
        return rspList;
    }

    @NotNull
    private List<LocalDate> getEffectDateList(LiquidityBoardRentIncomeREQ req) {
        List<LocalDate> effectLocalDateList = new ArrayList<>();
        Map<LocalDate, BaseDataSpecialDate> specialDateMap = baseDataSpecialDateService.findAllSpecialDate().stream().collect(Collectors.toMap(BaseDataSpecialDate::getSpecialDate, Function.identity()));
        // 如果手动筛选条件不为空，只需要考虑手动筛选的区间可以了
        if (CharSequenceUtil.isNotBlank(req.getExpireDateFrom()) && CharSequenceUtil.isNotBlank(req.getExpireDateTo())) {
            LocalDate expireDateFrom = LocalDate.parse(req.getExpireDateFrom());
            LocalDate expireDateTo = LocalDate.parse(req.getExpireDateTo());
            while (!expireDateFrom.isAfter(expireDateTo)) {
                List<LocalDate> handleHoliday = baseDataSpecialDateService.handleHoliday(specialDateMap, expireDateFrom);
                if (CollUtil.isNotEmpty(handleHoliday)) {
                    effectLocalDateList.addAll(handleHoliday);
                }
                expireDateFrom = expireDateFrom.plusDays(1);
            }
        } else {
            LocalDate queryDateStart = req.getQueryDateStart();
            LocalDate queryDateEnd = req.getQueryDateEnd();
            while (!queryDateStart.isAfter(queryDateEnd)) {
                List<LocalDate> handleHoliday = baseDataSpecialDateService.handleHoliday(specialDateMap, queryDateStart);
                if (CollUtil.isNotEmpty(handleHoliday)) {
                    effectLocalDateList.addAll(handleHoliday);
                }
                queryDateStart = queryDateStart.plusDays(1);
            }
        }
        return effectLocalDateList.stream().distinct().sorted(Comparator.comparing(LocalDate::toEpochDay)).collect(Collectors.toList());
    }

    @Override
    public List<LiquidityBoardRepayPrincipalInterestRSP> manageRepay(LiquidityBoardRepayPrincipalInterestREQ req) {
        Page<RepayPrincipalInterestDto> queryPage = new Page<>(1, 5000);
        RepayPrincipalInterestListREQ queryReq = new RepayPrincipalInterestListREQ();
        queryReq.setQueryDateFrom(req.getQueryDateStart());
        queryReq.setQueryDateTo(req.getQueryDateEnd());
        queryReq.setQueryDateFrom(req.getQueryDateStart());
        queryReq.setQueryDateTo(req.getQueryDateEnd());
        if (CharSequenceUtil.isNotBlank(req.getExpireDateFrom()) && CharSequenceUtil.isNotBlank(req.getExpireDateTo())) {
            queryReq.setQueryDateFrom(LocalDate.parse(req.getExpireDateFrom()));
            queryReq.setQueryDateTo(LocalDate.parse(req.getExpireDateTo()));
        }
        Page<RepayPrincipalInterestDto> repayPrincipalInterestDtoPage = fundFinancingBaseInfoMapper.selectRepayPrincipalInterestList(queryPage, queryReq);
        if (CollUtil.isEmpty(repayPrincipalInterestDtoPage.getRecords())) {
            return Collections.emptyList();
        }

        // 间接融资的机构因为是一对多的，二次处理
        List<Long> financingIdList = repayPrincipalInterestDtoPage.getRecords().stream().filter(e -> Objects.nonNull(e.getFinancingId()))
                .filter(e -> FinancingTypeEnum.INDIRECT.name().equals(e.getType()))
                .map(RepayPrincipalInterestDto::getFinancingId).collect(Collectors.toList());
        Map<Long, List<FundOrganization>> organizationMap = fundOrganizationService.getBatchByFinancingId(financingIdList);
        Map<String, List<FundFinancingAccountSetting>> fundFinancingAccountSettingMap = new HashMap<>();
        List<FundFinancingAccountSetting> fundFinancingAccountSettings = fundFinancingAccountSettingService.list(Wrappers.<FundFinancingAccountSetting>lambdaQuery()
                .in(FundFinancingAccountSetting::getFinancingId, repayPrincipalInterestDtoPage.getRecords().stream().map(RepayPrincipalInterestDto::getFinancingId).collect(Collectors.toList())));
        if (CollUtil.isNotEmpty(fundFinancingAccountSettings)) {
            fundFinancingAccountSettingMap = fundFinancingAccountSettings.stream().collect(Collectors.groupingBy(e -> e.getFinancingId() + (Objects.isNull(e.getFinancingType()) ? "INDIRECT" : e.getFinancingType())));
        }
        // 构建返回结果
        List<LiquidityBoardRepayPrincipalInterestRSP> rspList = new ArrayList<>();
        FundDirectFinancingBaseInfo directFinancingBaseInfo = new FundDirectFinancingBaseInfo();
        for (RepayPrincipalInterestDto e : repayPrincipalInterestDtoPage.getRecords()) {
            LiquidityBoardRepayPrincipalInterestRSP rsp = new LiquidityBoardRepayPrincipalInterestRSP();
            BeanUtil.copyProperties(e, rsp);
            List<FundFinancingAccountSetting> accountSettings = fundFinancingAccountSettingMap.get(e.getFinancingId() + e.getType());
            if (CollUtil.isNotEmpty(accountSettings)) {
                // 本金和利息可能需要分开处理
                Map<String, FundFinancingAccountSetting> map = accountSettings.stream().collect(Collectors.toMap(FundFinancingAccountSetting::getAccountCategory, Function.identity(), (v1, v2) -> v1));
                FundFinancingAccountSetting principal = map.get(FundFinancingAccountTypeEnum.REPAY_PRINCIPAL.name());
                if (Objects.nonNull(principal)) {
                    rsp.setPrincipalOutflowAccount(principal.getAccountBank());
                    rsp.setPrincipalOutflowAccountProperty(Optional.ofNullable(BaseDataBankAccountTypeEnum.find(principal.getAccountType())).map(BaseDataBankAccountTypeEnum::getDisplay).orElse(null));
                }
                FundFinancingAccountSetting interest = map.get(FundFinancingAccountTypeEnum.REPAY_INTEREST.name());
                if (Objects.nonNull(interest)) {
                    rsp.setInterestOutflowAccount(interest.getAccountBank());
                    rsp.setInterestOutflowAccountProperty(Optional.ofNullable(BaseDataBankAccountTypeEnum.find(interest.getAccountType())).map(BaseDataBankAccountTypeEnum::getDisplay).orElse(null));
                }
                FundFinancingAccountSetting principalAndInterest = map.get(FundFinancingAccountTypeEnum.REPAY_PRINCIPAL_AND_INTEREST.name());
                if (Objects.nonNull(principalAndInterest)) {
                    rsp.setPrincipalOutflowAccount(principalAndInterest.getAccountBank());
                    rsp.setPrincipalOutflowAccountProperty(Optional.ofNullable(BaseDataBankAccountTypeEnum.find(principalAndInterest.getAccountType())).map(BaseDataBankAccountTypeEnum::getDisplay).orElse(null));
                    rsp.setInterestOutflowAccount(principalAndInterest.getAccountBank());
                    rsp.setInterestOutflowAccountProperty(Optional.ofNullable(BaseDataBankAccountTypeEnum.find(principalAndInterest.getAccountType())).map(BaseDataBankAccountTypeEnum::getDisplay).orElse(null));
                }
            }
            if (FinancingTypeEnum.INDIRECT.name().equals(e.getType())) {
                List<FundOrganization> fundOrganizations = organizationMap.get(e.getFinancingId());
                if (CollUtil.isNotEmpty(fundOrganizations)) {
                    rsp.setOrganizationName(fundOrganizations.stream().map(FundOrganization::getOrganizationName).collect(Collectors.toList()));
                }
            } else {
                //融资机构信息改为取根据融资产品的融资编号到直融管理列表取对应的产品名称
                directFinancingBaseInfo = fundDirectFinancingBaseInfoService.getById(e.getFinancingId());
                if(directFinancingBaseInfo!=null){
                    rsp.setOrganizationName(Collections.singletonList(directFinancingBaseInfo.getProductName()));
                }
                //rsp.setOrganizationName(Collections.singletonList(e.getOrganizationName()));
            }
            rsp.setExpireDate(e.getDueDate().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
            rspList.add(rsp);
        }
        rspList.sort(Comparator.comparing(LiquidityBoardRepayPrincipalInterestRSP::getExpireDate));
        return rspList;
    }
}
