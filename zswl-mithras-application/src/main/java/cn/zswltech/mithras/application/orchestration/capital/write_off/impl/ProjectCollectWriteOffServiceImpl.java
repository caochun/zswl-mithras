package cn.zswltech.mithras.application.orchestration.capital.write_off.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.dto.third.financial.ThirdCollectionRecordREQ;
import cn.zswltech.mithras.foundation.enums.CashFlowItemEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.capital.enums.BankFlowWriteOffTypeEnum;
import cn.zswltech.mithras.capital.enums.CollectionWriteOffOrderEnum;
import cn.zswltech.mithras.capital.enums.FinanceWriteOffTypeEnum;
import cn.zswltech.mithras.capital.enums.FinancingFlowWriteOffStatusEnum;
import cn.zswltech.mithras.capital.enums.writeoff.FinanceFlowTypeEnum;
import cn.zswltech.mithras.capital.enums.writeoff.FinanceWriteOffStatusEnum;
import cn.zswltech.mithras.capital.enums.writeoff.WriteOffAccountTypeEnum;
import cn.zswltech.mithras.capital.enums.writeoff.WriteOffBusinessModelEnum;
import cn.zswltech.mithras.capital.enums.BankFlowCenterTypeEnum;
import cn.zswltech.mithras.payment.enums.PaymentMethod;
import cn.zswltech.mithras.payment.enums.WriteOffTypeEnum;
import cn.zswltech.mithras.collection.mapper.CollectionBaseInfoMapper;
import cn.zswltech.mithras.fund.mapper.financing.FundFinancingPledgeInfoMapper;
import cn.zswltech.mithras.collection.model.CollectionBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractTenantry;
import cn.zswltech.mithras.third.financialshare.model.FinanceFlowMatchResult;
import cn.zswltech.mithras.third.financialshare.model.FinanceFlowRecord;
import cn.zswltech.mithras.third.financialshare.model.FinanceFlowTabMainInfo;
import cn.zswltech.mithras.third.financialshare.model.FinanceFlowTabRecord;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.application.orchestration.capital.FinanceFlowAutoWriteOffService;
import cn.zswltech.mithras.capital.application.writeoff.CommonWriteOffService;
import cn.zswltech.mithras.application.orchestration.capital.write_off.WriteOffCommonService;
import cn.zswltech.mithras.fund.application.financing.bo.FundPledgeSupervisedBO;
import cn.zswltech.mithras.collection.application.CollectionBaseInfoService;
import cn.zswltech.mithras.contract.core.ContractTenantryService;
import cn.zswltech.mithras.third.financialshare.application.FinanceFlowMatchResultService;
import cn.zswltech.mithras.application.orchestration.capital.FinanceFlowRecordService;
import cn.zswltech.mithras.third.financialshare.application.FinanceFlowTabMainInfoService;
import cn.zswltech.mithras.third.financialshare.application.FinanceFlowTabRecordService;
import cn.zswltech.mithras.third.financialshare.application.FinancialService;
import cn.zswltech.mithras.foundation.util.LongUtil;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.foundation.context.SpringContextHolder.getBean;

/**
 * @author bigbear
 * @date 2024/9/24 17:39
 * @description
 */
@Slf4j
@Service
public class ProjectCollectWriteOffServiceImpl implements CommonWriteOffService {

    @Resource
    private ProjectCollectWriteOffServiceImpl thisService;
    @Resource
    private FinancialService financialService;
    @Resource
    private ContractTenantryService contractTenantryService;
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private WriteOffCommonService writeOffCommonService;
    @Resource
    private FundFinancingPledgeInfoMapper financingPledgeInfoMapper;
    @Resource
    private FinanceFlowTabMainInfoService financeFlowTabMainInfoService;
    @Resource
    private FinanceFlowTabRecordService financeFlowTabRecordService;
    @Resource
    private FinanceFlowMatchResultService financeFlowMatchResultService;
    @Resource
    private FinanceFlowRecordService financeFlowRecordService;
    @Resource
    private FinanceFlowAutoWriteOffService financeFlowAutoWriteOffService;

    @Override
    public void autoWriteOffEntryPoint(List<FinanceFlowRecord> allFlowRecordList, int plusDays) {
        // 1、过滤出收款流水
        List<FinanceFlowRecord> domainFlowRecordList = thisService.getDomainFlowRecordList(allFlowRecordList);

        // 2、将收款流水分组
        Object bankFlowRecordGrouping = thisService.domainBankFlowRecordGrouping(domainFlowRecordList);

        // 3、根据领域银行流水获取账单列表, 未拆分到现金流
        List<CollectionBaseInfo> collectBaseInfoList = (List<CollectionBaseInfo>) thisService.getBusinessFlowRecordList(domainFlowRecordList, plusDays);

        // 4、领域账单分组
        Map<Long, List<FinanceFlowMatchResult>> financeFlowRecordMap = thisService.domainFlowRecordGrouping(collectBaseInfoList);

        // 5、流水和账单匹配，这里返回的结果是不带ID的，需要自己手动处理
        List<Triple<FinanceFlowTabMainInfo, List<FinanceFlowTabRecord>, List<FinanceFlowMatchResult>>> matchResultList = thisService.match(bankFlowRecordGrouping, financeFlowRecordMap);

        List<Long> actualWriteOffList = new ArrayList<>();
        matchResultList.forEach(triple -> {
            // 6、获取结果排序、入库
            FinanceFlowTabMainInfo financeFlowTabMainInfo = triple.getLeft();
            List<FinanceFlowTabRecord> financeFlowTabRecordList = triple.getMiddle();
            List<FinanceFlowMatchResult> financeFlowMatchResults = triple.getRight();
            financeFlowTabMainInfo.setWriteOffStatus(FinanceWriteOffStatusEnum.NO_WRITE_OFF.name());
            financeFlowTabMainInfoService.save(financeFlowTabMainInfo);
            Boolean perfectMatch = thisService.checkPerfectMatch(financeFlowTabMainInfo.getId());
            financeFlowTabMainInfo.setIsPerfectMatch(Boolean.TRUE.equals(perfectMatch) ? 1 : 0);
            financeFlowTabMainInfoService.updateById(financeFlowTabMainInfo);

            // 6.1 填充流水关联ID
            financeFlowTabRecordList.forEach(financeFlowTabRecord -> financeFlowTabRecord.setMainId(financeFlowTabMainInfo.getId()));
            financeFlowTabRecordService.saveBatch(financeFlowTabRecordList);
            // 6.2 填充账单关联ID
            financeFlowMatchResults.forEach(financeFlowMatchResult -> financeFlowMatchResult.setMainId(financeFlowTabMainInfo.getId()));
            financeFlowMatchResultService.saveBatch(financeFlowMatchResults);

            // 7、匹配金额，将多余的账单删除
            thisService.matchAmount(financeFlowTabRecordList, financeFlowMatchResults);

            // 8、开始实际核销
            List<Long> actualWriteOffIds = thisService.actualWriteOff(financeFlowTabRecordList, financeFlowMatchResults);

            // 9、更新流水状态
            log.info("核销成功，更新流水状态，推送账单给苍穹，流水ID列表{}", actualWriteOffIds);
            financeFlowRecordService.lambdaUpdate()
                    .in(FinanceFlowRecord::getId, actualWriteOffIds)
                    .set(FinanceFlowRecord::getSurplusAmount, 0)
                    .set(FinanceFlowRecord::getWriteOffType, FinanceWriteOffTypeEnum.AUTO_WRITE_OFF.name())
                    .set(FinanceFlowRecord::getFinancingProjectType, BankFlowWriteOffTypeEnum.PROJ_SIDE.name())
                    .set(FinanceFlowRecord::getWriteOffStatus, FinancingFlowWriteOffStatusEnum.COMPLETE_WRITE_OFF.name())
                    .update();
            actualWriteOffList.addAll(actualWriteOffIds);
        });

        // 10、推送账单给苍穹
        actualWriteOffList.forEach(o -> financeFlowAutoWriteOffService.writeOffNotice(Collections.singletonList(o)));
    }

    @Override
    public List<FinanceFlowRecord> getDomainFlowRecordList(List<FinanceFlowRecord> allFlowRecordList) {
        if (CollUtil.isEmpty(allFlowRecordList)) {
            return Collections.emptyList();
        }
        // 1、过滤到收款流水
        List<FinanceFlowRecord> collectFlowRecords = allFlowRecordList.stream().filter(financeFlowRecord -> financeFlowRecord.getCreditamount() != null)
                .filter(financeFlowRecord -> financeFlowRecord.getCreditamount() > 0)
                .filter(financeFlowRecord -> Objects.equals(financeFlowRecord.getFinancingFlowType(), BankFlowCenterTypeEnum.PROCESSING_CENTER.name()))
                .filter(financeFlowRecord -> YesOrNoNumberEnum.NO.getCode().equals(financeFlowRecord.getLogicDeleteFlag()))
                .collect(Collectors.toList());

        // 2、所有的租金往来方的名称
        List<String> rentContractAccountNameList = contractTenantryService.list(Wrappers.<ContractTenantry>lambdaQuery()
                        .select(ContractTenantry::getRentConcatAccountName))
                .stream().map(ContractTenantry::getRentConcatAccountName).filter(Objects::nonNull).distinct().collect(Collectors.toList());

        // 3、过滤掉资金端的收款数据和别的无关收款数据
        return collectFlowRecords.stream().filter(financeFlowRecord -> rentContractAccountNameList.contains(financeFlowRecord.getOppunit())).collect(Collectors.toList());
    }

    @Override
    public Object domainBankFlowRecordGrouping(List<FinanceFlowRecord> domainFlowRecordList) {
        if (CollUtil.isEmpty(domainFlowRecordList)) {
            return Collections.emptyMap();
        }
        // 项目端-收款返回结构如下：Map<来款方名称, Map<收款账号, List<FinanceFlowRecord>>>
        Map<String, Map<String, List<FinanceFlowRecord>>> resultMap = new HashMap<>(16);

        // 1、将流水先根据【来款账户】分组
        domainFlowRecordList.stream().collect(Collectors.groupingBy(FinanceFlowRecord::getOppunit))
                .forEach((otherName, financeFlowRecordList) ->
                        // 2、再根据【收款账号】分组
                        resultMap.put(otherName, financeFlowRecordList.stream().collect(Collectors.groupingBy(FinanceFlowRecord::getAccountbankBankaccountnumber)))
                );
        log.info("domainBankFlowRecordGrouping resultMap = {}", resultMap);

        // 如果存在租金往来方是空，直接报错
        if (resultMap.containsKey("")) {
            throw MithrasException.newException("【对方户名】存在空值，请检查数据！");
        }
        return resultMap;
    }

    @Override
    public void sortBankFlowRecord(List<FinanceFlowRecord> domainFlowRecordList) {
        if (CollUtil.isEmpty(domainFlowRecordList)) {
            return;
        }
        log.info("before sort: domainBankFlowRecordList size = {}, \n domainFlowRecordList = {}", domainFlowRecordList.size(), domainFlowRecordList);
        // 项目端银行流水的排序，优先级：交易日期、金额、ID
        domainFlowRecordList.sort(Comparator.comparing(FinanceFlowRecord::getBizdate)
                .thenComparing(FinanceFlowRecord::getCreditamount).thenComparing(FinanceFlowRecord::getId));
        log.info("after sort: domainBankFlowRecordList size = {}, \n domainFlowRecordList = {}", domainFlowRecordList.size(), domainFlowRecordList);
    }

    @Override
    public List<?> getBusinessFlowRecordList(List<FinanceFlowRecord> domainFlowRecordList, int plusDays) {
        if (CollUtil.isEmpty(domainFlowRecordList)) {
            return Collections.emptyList();
        }

        Map<Long, ContractTenantry> contractTenantryMap = writeOffCommonService.getcontractTeantryMap(domainFlowRecordList);
        log.info("getBusinessFlowRecordList contractTenantryMap = {}", contractTenantryMap);
        if (CollUtil.isEmpty(contractTenantryMap)) {
            throw MithrasException.newException("未找到任何租金往来方，请检查数据！");
        }
        // 项目端找到未收款的应收账单
        List<CollectionBaseInfo> collectionBaseInfos = collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                // 注意：目前核销只取租金、提前终止补偿金、名义价款（留购金）、后续全部放开的话要把此条件去掉
                .or(i -> i.and(queryWrapper -> queryWrapper.gt(CollectionBaseInfo::getPhase, 0)
                                .in(CollectionBaseInfo::getContractId, contractTenantryMap.values().stream().map(ContractTenantry::getContractId).collect(Collectors.toList()))
                                .le(CollectionBaseInfo::getPlanCollectionDate, LocalDateTimeUtil.now().plusDays(plusDays))
                                .in(CollectionBaseInfo::getCashFlowItem, Collections.singletonList(CashFlowItemEnum.RENT.name()))
                                .isNotNull(CollectionBaseInfo::getCode))
                        .or(queryWrapper -> queryWrapper.in(CollectionBaseInfo::getContractId, contractTenantryMap.values().stream().map(ContractTenantry::getContractId).collect(Collectors.toList()))
                                .le(CollectionBaseInfo::getPlanCollectionDate, LocalDateTimeUtil.now().plusDays(plusDays))
                                .in(CollectionBaseInfo::getCashFlowItem, Arrays.asList(CashFlowItemEnum.EARLY_STOP_COMPENSATION.name(), CashFlowItemEnum.NOMINAL_PRICE.name()))
                                .isNotNull(CollectionBaseInfo::getCode))));

        // 过滤掉已经核销完毕的
        List<CollectionBaseInfo> noCollectBaseInfoList = Optional.ofNullable(collectionBaseInfos).map(
                // 租金没还完或者罚息没还完的都要返回
                collectionBaseInfoList -> collectionBaseInfoList.stream()
                        .filter(collectionBaseInfo -> LongUtil.null2zero(collectionBaseInfo.getPlanCollectionAmount()) - LongUtil.null2zero(collectionBaseInfo.getCollectionAmount()) > 0
                                || LongUtil.null2zero(collectionBaseInfo.getPenaltyInterest()) - LongUtil.null2zero(collectionBaseInfo.getCollectionPenaltyInterest()) > 0)
                        .sorted(Comparator.comparing(CollectionBaseInfo::getCreateTime))
                        .collect(Collectors.toList())
        ).orElse(Collections.emptyList());
        log.info("[收款]获取到的未收款基本信息列表：【{}】\n[收款]获取到的未收款基本信息列表大小：【{}】", noCollectBaseInfoList, noCollectBaseInfoList.size());
        return noCollectBaseInfoList;
    }

    @Override
    public Map<Long, List<FinanceFlowMatchResult>> domainFlowRecordGrouping(List<?> businessFlowRecords) {
        if (CollUtil.isEmpty(businessFlowRecords)) {
            return Collections.emptyMap();
        }
        Map<Long, List<FinanceFlowMatchResult>> resultMap = new HashMap<>(16);
        // 这里的账单分组不以合同为纬度，而是按照【租金往来方】分组，解决单客户多合同问题
        List<CollectionBaseInfo> collectionBaseInfoList = (List<CollectionBaseInfo>) businessFlowRecords;
        // 1、找到当前流水账单中【租金往来方】
        Map<String, List<ContractTenantry>> contractTenantryGroupByRentAccountName = contractTenantryService.list(Wrappers.<ContractTenantry>lambdaQuery()
                        .in(ContractTenantry::getContractId, collectionBaseInfoList.stream().map(CollectionBaseInfo::getContractId).collect(Collectors.toList()))
                ).stream().filter(contractTenantry -> contractTenantry.getRentConcatAccountName() != null)
                .collect(Collectors.groupingBy(ContractTenantry::getRentConcatAccountName));
        if (CollUtil.isEmpty(contractTenantryGroupByRentAccountName)) {
            throw MithrasException.newException("未找到任何租金往来方，请检查数据！");
        }

        // 2、根据【租金往来方】分组，返回结果
        contractTenantryGroupByRentAccountName.forEach((rentAccountName, contractTenantryList) -> {
            for (ContractTenantry contractTenantry : contractTenantryList) {
                List<FinanceFlowMatchResult> financeFlowMatchResultList = collectionBaseInfoList.stream().filter(o -> Objects.equals(contractTenantry.getContractId(), o.getContractId()))
                        .map(thisService::buildCollectCashFlow).flatMap(List::stream).collect(Collectors.toList());
                resultMap.put(contractTenantry.getContractId(), financeFlowMatchResultList);
            }
        });
        return resultMap;
    }

    private List<FinanceFlowMatchResult> buildCollectCashFlow(CollectionBaseInfo collectionBaseInfo) {
        List<FinanceFlowMatchResult> resultList = new ArrayList<>();
        FinanceFlowMatchResult rsp = FinanceFlowMatchResult.builder().build();
        // 封装数据
        rsp.setCashFlowItem(collectionBaseInfo.getCashFlowItem());
        rsp.setCashFlowCode(collectionBaseInfo.getCode());
        rsp.setIsSystemGenerate(YesOrNoNumberEnum.YES.getCode());
        rsp.setClientId(collectionBaseInfo.getClientId());
        rsp.setSourceBusinessCode(collectionBaseInfo.getContractCode());
        rsp.setShouldWriteOffTime(LocalDate.parse(LocalDateTimeUtil.format(collectionBaseInfo.getPlanCollectionDate(), DatePattern.NORM_DATE_PATTERN)));
        rsp.setSourceId(collectionBaseInfo.getId());
        // 不同的现金流都要构建待核销数据，都根据实收和应收判断，还需要加入期项对特殊现金流做自定义处理
        long noPayPrincipal = LongUtil.null2zero(collectionBaseInfo.getPrincipal()) - LongUtil.null2zero(collectionBaseInfo.getCollectionPrincipal());
        if (collectionBaseInfo.getPhase() > 0 && CashFlowItemEnum.RENT.name().equals(collectionBaseInfo.getCashFlowItem()) && noPayPrincipal > 0) {
            // 本金
            FinanceFlowMatchResult principal = BeanUtil.copyProperties(rsp, FinanceFlowMatchResult.class);
            principal.setShouldWriteOffAmount(LongUtil.null2zero(collectionBaseInfo.getPrincipal()));
            principal.setNoWriteOffAmount(noPayPrincipal);
            principal.setThisWriteOffAmount(noPayPrincipal);
            principal.setCashFlowItem(CollectionWriteOffOrderEnum.PRINCIPAL.name());
            resultList.add(principal);
        }

        long noPayInterest = LongUtil.null2zero(collectionBaseInfo.getInterest()) - LongUtil.null2zero(collectionBaseInfo.getCollectionInterest());
        if (collectionBaseInfo.getPhase() > 0 && CashFlowItemEnum.RENT.name().equals(collectionBaseInfo.getCashFlowItem()) && noPayInterest > 0) {
            // 利息
            FinanceFlowMatchResult interest = BeanUtil.copyProperties(rsp, FinanceFlowMatchResult.class);
            interest.setShouldWriteOffAmount(LongUtil.null2zero(collectionBaseInfo.getInterest()));
            interest.setNoWriteOffAmount(noPayInterest);
            interest.setThisWriteOffAmount(noPayInterest);
            interest.setCashFlowItem(CollectionWriteOffOrderEnum.INTEREST.name());
            resultList.add(interest);
        }

        long noPayPenaltyInterest = LongUtil.null2zero(collectionBaseInfo.getPenaltyInterest()) - LongUtil.null2zero(collectionBaseInfo.getCollectionPenaltyInterest());
        if (collectionBaseInfo.getPhase() > 0 && CashFlowItemEnum.RENT.name().equals(collectionBaseInfo.getCashFlowItem()) && noPayPenaltyInterest > 0) {
            // 罚息
            FinanceFlowMatchResult penaltyInterest = BeanUtil.copyProperties(rsp, FinanceFlowMatchResult.class);
            penaltyInterest.setShouldWriteOffAmount(LongUtil.null2zero(collectionBaseInfo.getPenaltyInterest()));
            penaltyInterest.setNoWriteOffAmount(noPayPenaltyInterest);
            penaltyInterest.setThisWriteOffAmount(noPayPenaltyInterest);
            penaltyInterest.setCashFlowItem(CollectionWriteOffOrderEnum.PENALTY_INTEREST.name());
            resultList.add(penaltyInterest);
        }

        // 本次只做本金、利息、罚息、提前终止补偿金和名义价款（留购金）的核销，其他不处理
        /*long noPayFirstInterest = LongUtil.null2zero(collectionBaseInfo.getInterest()) - LongUtil.null2zero(collectionBaseInfo.getCollectionInterest());
        if (collectionBaseInfo.getPhase() == 0 && CashFlowItemEnum.RENT.name().equals(collectionBaseInfo.getCashFlowItem())) {
            // 首期利息
            FinanceFlowMatchResult firstInterest = BeanUtil.copyProperties(rsp, FinanceFlowMatchResult.class);
            firstInterest.setShouldWriteOffAmount(LongUtil.null2zero(collectionBaseInfo.getInterest()));
            firstInterest.setNoWriteOffAmount(noPayFirstInterest);
            firstInterest.setThisWriteOffAmount(noPayFirstInterest);
            firstInterest.setCashFlowItem(CollectionWriteOffOrderEnum.FIRST_INSTALLMENT_INTEREST.name());
            resultList.add(firstInterest);
        }*/

        /*CashFlowItemEnum.EARNEST_MONEY.name(), CashFlowItemEnum.COMMISSION.name(), CashFlowItemEnum.FIRST_RENT.name(),
                CashFlowItemEnum.OTHERAMOUNT.name(), CashFlowItemEnum.RETENTION_MONEY.name()*/
        if (CharSequenceUtil.equalsAny(collectionBaseInfo.getCashFlowItem(), CashFlowItemEnum.EARLY_STOP_COMPENSATION.name(), CashFlowItemEnum.NOMINAL_PRICE.name())) {
            // 其它类型应收款
            FinanceFlowMatchResult otherCashFlow = BeanUtil.copyProperties(rsp, FinanceFlowMatchResult.class);
            otherCashFlow.setNoWriteOffAmount(LongUtil.null2zero(collectionBaseInfo.getPlanCollectionAmount()) - LongUtil.null2zero(collectionBaseInfo.getCollectionAmount()));
            otherCashFlow.setShouldWriteOffAmount(LongUtil.null2zero(collectionBaseInfo.getPlanCollectionAmount()));
            otherCashFlow.setThisWriteOffAmount(otherCashFlow.getNoWriteOffAmount());
            otherCashFlow.setCashFlowItem(Optional.ofNullable(CollectionWriteOffOrderEnum.of(collectionBaseInfo.getCashFlowItem())).map(CollectionWriteOffOrderEnum::name).orElse(null));
            resultList.add(otherCashFlow);
        }
        return resultList;
    }

    @Override
    public List<FinanceFlowMatchResult> sortBusinessFlowRecord(List<FinanceFlowMatchResult> businessFlowRecords) {
        if (CollUtil.isEmpty(businessFlowRecords)) {
            return businessFlowRecords;
        }
        log.info("before sort: businessFlowRecords size = {}, \n businessFlowRecords = {}", businessFlowRecords.size(), businessFlowRecords);
        // 收款现金流核销排序，先按照业务流水的时间排序，再根据费用项排序，这里的^v^!!!租金的需要特殊处理!!!^v^
        // 排序方案：先暴力根据应收日期排序，排完序之后将本息放到一个map中缓存，在实际核销业务流水的时候根据时间先后核销
        // 如果遇到罚息，需要从缓存中获取当前应收日期之后的本息是否存在，如果不存在才进行核销！
        // 因为应收创建时间是合同起租，所以根据合同创建时间和应收记录的创建时间维度只需要根据应收表的ID排序即可
        businessFlowRecords.sort(Comparator.comparing(FinanceFlowMatchResult::getShouldWriteOffTime)
                .thenComparing(matchResult -> Objects.requireNonNull(CollectionWriteOffOrderEnum.of(matchResult.getCashFlowItem())).getSort())
                .thenComparing(FinanceFlowMatchResult::getThisWriteOffAmount)
                .thenComparing(FinanceFlowMatchResult::getSourceId));

        // 暴力排序之后再找出所有的本息，并缓存起来
        Map<LocalDate, FinanceFlowMatchResult> principalInterestCacheMap = new HashMap<>();
        List<FinanceFlowMatchResult> penaltyInterestList = new LinkedList<>();
        businessFlowRecords.forEach(businessFlowRecord -> {
            if (businessFlowRecord.getCashFlowItem().equals(CollectionWriteOffOrderEnum.PRINCIPAL.name()) ||
                    businessFlowRecord.getCashFlowItem().equals(CollectionWriteOffOrderEnum.INTEREST.name())) {
                // 因为key重复会覆盖，场景在这个地方并没有关系
                principalInterestCacheMap.put(businessFlowRecord.getShouldWriteOffTime(), businessFlowRecord);
            }
            if (businessFlowRecord.getCashFlowItem().equals(CollectionWriteOffOrderEnum.PENALTY_INTEREST.name())) {
                penaltyInterestList.add(businessFlowRecord);
            }
        });

        if (CollUtil.isEmpty(penaltyInterestList) || principalInterestCacheMap.isEmpty()) {
            // 不存在本息或者罚息，直接返回
            return businessFlowRecords;
        }

        // 存在罚息在本息之前，需要再次手工排序
        businessFlowRecords.removeIf(penaltyInterestList::contains);
        List<FinanceFlowMatchResult> resultList = new LinkedList<>();
        // 拿到缓存中的最后一个本息的日期
        LocalDate lastPrincipalInterestDate = principalInterestCacheMap.keySet().stream().max(LocalDate::compareTo).orElse(null);
        Assert.notNull(lastPrincipalInterestDate, () -> MithrasException.newException("【项目端收款】存在罚息，但是没有找到对应的本息应还日期，自动核销停止?v?"));
        AtomicReference<Boolean> flag = new AtomicReference<>(true);
        AtomicInteger index = new AtomicInteger(0);
        businessFlowRecords.forEach(businessFlowRecord -> {
            if ((businessFlowRecord.getShouldWriteOffTime().isAfter(lastPrincipalInterestDate) || index.get() == businessFlowRecords.size() - 1) && Boolean.TRUE.equals(flag.get())) {
                // 找到第一个在罚息日期之后的现金流
                resultList.add(businessFlowRecord);
                resultList.addAll(penaltyInterestList);
                flag.getAndSet(false);
            } else {
                resultList.add(businessFlowRecord);
            }
            index.incrementAndGet();
        });
        return resultList;
    }

    @Override
    public List<Triple<FinanceFlowTabMainInfo, List<FinanceFlowTabRecord>, List<FinanceFlowMatchResult>>> match(Object domainFlowRecordMap, Map<Long, List<FinanceFlowMatchResult>> businessFlowRecordMap) {
        if (CollUtil.isEmpty(businessFlowRecordMap) || Objects.isNull(domainFlowRecordMap)) {
            log.warn("domainFlowRecordMap or businessFlowRecordMap is empty！！！");
            return Collections.emptyList();
        }
        List<Triple<FinanceFlowTabMainInfo, List<FinanceFlowTabRecord>, List<FinanceFlowMatchResult>>> resultList = new LinkedList<>();
        if (!(domainFlowRecordMap instanceof Map)) {
            log.error("domainFlowRecordMap is not Map, domainFlowRecordMap = {}", domainFlowRecordMap);
            throw new IllegalArgumentException("domainFlowRecordMap is not Map");
        }
        Map<String, Map<String, List<FinanceFlowRecord>>> flowRecordMap = (Map<String, Map<String, List<FinanceFlowRecord>>>) domainFlowRecordMap;
        Map<String, List<ContractTenantry>> tmp = contractTenantryService.list(Wrappers.<ContractTenantry>lambdaQuery()
                        .in(ContractTenantry::getRentConcatAccountName, flowRecordMap.keySet()))
                .stream().collect(Collectors.groupingBy(ContractTenantry::getRentConcatAccountName));
        Map<String, List<ContractTenantry>> rentAccountNameMap = new HashMap<>();
        tmp.forEach((otherName, flowMap) -> {
            Map<Long, List<ContractTenantry>> map = flowMap.stream().collect(Collectors.groupingBy(ContractTenantry::getContractId));
            flowMap = map.values().stream().map(contractTenantryList -> contractTenantryList.get(0)).collect(Collectors.toList());
            rentAccountNameMap.put(otherName, flowMap);
        });
        List<FundPledgeSupervisedBO> supervisedBoList = financingPledgeInfoMapper.getFundSupervisedBo(rentAccountNameMap.values().stream().flatMap(Collection::stream)
                .map(ContractTenantry::getContractId).collect(Collectors.toList()));
        Map<Long, FundPledgeSupervisedBO> pledgeSupervisedBoMap = CollUtil.isEmpty(supervisedBoList) ? new HashMap<>() :
                supervisedBoList.stream().filter(e -> Objects.equals(e.getIsSupervise(), Boolean.TRUE))
                        .collect(Collectors.toMap(FundPledgeSupervisedBO::getContractId, Function.identity(), (a, b) -> a));
        for (Map.Entry<String, Map<String, List<FinanceFlowRecord>>> otherNameFlowMap : flowRecordMap.entrySet()) {
            // 这个来款账户对应合同主承租人的租金往来方，找到相关的合同结合，再判断合同集合在融资模块是否是被监管的账户
            String otherName = otherNameFlowMap.getKey();
            Map<String, List<FinanceFlowRecord>> tmpMap = otherNameFlowMap.getValue();
            Map<String, List<FinanceFlowRecord>> map = new HashMap<>();
            tmpMap.forEach((k, v) -> map.put(k.replace(" ", ""), v));
            List<ContractTenantry> contractTenantryList = rentAccountNameMap.get(otherName);
            if (CollUtil.isEmpty(contractTenantryList)) {
                log.error("【项目端收款】-来款方{}未找到租金往来方，请检查数据是否正确！", otherName);
                continue;
            }
            Triple<FinanceFlowTabMainInfo, List<FinanceFlowTabRecord>, List<FinanceFlowMatchResult>> normalAccountTriple;
            List<ContractTenantry> supervisedContractTenantryList = new LinkedList<>();
            List<ContractTenantry> normalContractTenantryList = new LinkedList<>();
            for (ContractTenantry contractTenantry : contractTenantryList) {
                // 将租金往来方按照有没有被监管的合同分组
                if (Objects.nonNull(pledgeSupervisedBoMap.get(contractTenantry.getContractId()))) {
                    supervisedContractTenantryList.add(contractTenantry);
                } else {
                    normalContractTenantryList.add(contractTenantry);
                }
            }

            if (CollUtil.isNotEmpty(supervisedContractTenantryList)) {
                // 按照监管账户分组
                Map<String, List<FundPledgeSupervisedBO>> groupByAccountMap = pledgeSupervisedBoMap.values().stream().filter(e -> StrUtil.isNotBlank(e.getAccountNumber())).collect(Collectors.groupingBy(e -> e.getAccountNumber().replace(" ", "")));
                for (Map.Entry<String, List<FundPledgeSupervisedBO>> entry : groupByAccountMap.entrySet()) {
                    // 银行流水信息
                    List<FinanceFlowRecord> financeFlowRecords = map.get(entry.getKey());
                    if (CollectionUtil.isEmpty(financeFlowRecords)) {
                        // 说明对应监管账户没有银行流水
                        for (FundPledgeSupervisedBO bo : entry.getValue()) {
                            businessFlowRecordMap.remove(bo.getContractId());
                        }
                        continue;
                    }
                    // 构建tab主信息
                    FinanceFlowTabMainInfo financeFlowTabMainInfo = FinanceFlowTabMainInfo.builder()
                            .bankAccountName(otherName)
                            .accountType(WriteOffAccountTypeEnum.SUPERVISION_ACCOUNT.name())
                            .businessModel(WriteOffBusinessModelEnum.PROJECT_COLLECT.name())
                            .bankAccountNumber(financeFlowRecords.get(0).getAccountbankBankaccountnumber())
                            .superviseAccountName(entry.getValue().get(0).getAccountName())
                            .superviseAccountNumber(entry.getValue().get(0).getAccountNumber())
                            .build();
                    // 构建tab流水信息
                    List<FinanceFlowTabRecord> financeFlowTabRecords = financeFlowRecords.stream().map(this::buildProjectCollectTabRecord).collect(Collectors.toList());
                    // 构建tab匹配信息
                    List<FinanceFlowMatchResult> financeFlowMatchResults = new LinkedList<>();
                    for (FundPledgeSupervisedBO bo : entry.getValue()) {
                        List<FinanceFlowMatchResult> flowMatchResults = businessFlowRecordMap.get(bo.getContractId());
                        if (CollUtil.isNotEmpty(flowMatchResults)) {
                            financeFlowMatchResults.addAll(flowMatchResults);
                        }
                        businessFlowRecordMap.remove(bo.getContractId());
                    }
                    resultList.add(Triple.of(financeFlowTabMainInfo, financeFlowTabRecords, financeFlowMatchResults));
                    // map中移除已经使用的银行流水
                    map.remove(entry.getKey());
                }
                // 下面注释的代码存在2个问题（调整后的逻辑在上面）：
                // 1、一个租金往来方对应多个监管账户时，会出现监管账户对应到普通账户tab页中的问题
                // 2、一个租金往来方对应多个监管账户时，匹配结果的tab页面上只会展示一个监管账户的tab页（所有不同监管账户的流水均在这一个tab页中）
//                Set<FinanceFlowRecord> flowList = new HashSet<>();
//                for (ContractTenantry tenantry : supervisedContractTenantryList) {
//                    FundPledgeSupervisedBO tmpBo = pledgeSupervisedBoMap.get(tenantry.getContractId());
//                    List<FinanceFlowRecord> recordList = map.get(tmpBo.getAccountNumber().replace(" ", ""));
//                    if (CollUtil.isNotEmpty(recordList)) {
//                        flowList.addAll(recordList);
//                    }
//                }
//                if (CollUtil.isNotEmpty(flowList)) {
//                    FundPledgeSupervisedBO bo = pledgeSupervisedBoMap.get(supervisedContractTenantryList.get(0).getContractId());
//                    List<FinanceFlowRecord> financeFlowRecords = new ArrayList<>(flowList);
//                    // 被监管的账户，生成一个新的tab数据
//                    if (CollUtil.isEmpty(financeFlowRecords)) {
//                        log.warn("【项目端收款】监管账户{}没有对应的来款账户，账号：{}，请检查数据是否正确！", bo.getAccountName(), bo.getAccountNumber());
//                        continue;
//                    }
//                    // 构建tab主信息
//                    FinanceFlowTabMainInfo financeFlowTabMainInfo = FinanceFlowTabMainInfo.builder()
//                            .bankAccountName(otherName)
//                            .accountType(WriteOffAccountTypeEnum.SUPERVISION_ACCOUNT.name())
//                            .businessModel(WriteOffBusinessModelEnum.PROJECT_COLLECT.name())
//                            .bankAccountNumber(financeFlowRecords.get(0).getAccountbankBankaccountnumber())
//                            .superviseAccountName(bo.getAccountName())
//                            .superviseAccountNumber(bo.getAccountNumber())
//                            .build();
//                    // 构建tab流水信息
//                    List<FinanceFlowTabRecord> financeFlowTabRecords = financeFlowRecords.stream().map(this::buildProjectCollectTabRecord).collect(Collectors.toList());
//                    // 构建tab匹配信息
//                    List<FinanceFlowMatchResult> financeFlowMatchResults = new LinkedList<>();
//                    for (ContractTenantry tenantry : supervisedContractTenantryList) {
//                        List<FinanceFlowMatchResult> flowMatchResults = businessFlowRecordMap.get(tenantry.getContractId());
//                        if (CollUtil.isNotEmpty(flowMatchResults)) {
//                            financeFlowMatchResults.addAll(flowMatchResults);
//                        }
//                        businessFlowRecordMap.remove(tenantry.getContractId());
//                    }
//                    resultList.add(Triple.of(financeFlowTabMainInfo, financeFlowTabRecords, financeFlowMatchResults));
//                    map.remove(bo.getAccountNumber().replace(" ", ""));
//                } else {
//                    for (ContractTenantry tenantry : supervisedContractTenantryList) {
//                        businessFlowRecordMap.remove(tenantry.getContractId());
//                    }
//                }
            }
            if (CollUtil.isNotEmpty(normalContractTenantryList) && CollUtil.isNotEmpty(map)) {
                FinanceFlowTabMainInfo financeFlowTabMainInfo;
                List<FinanceFlowTabRecord> financeFlowTabRecords;
                // 构建tab主信息
                financeFlowTabMainInfo = FinanceFlowTabMainInfo.builder()
                        .bankAccountName(otherName)
                        .accountType(WriteOffAccountTypeEnum.ORDINARY_ACCOUNT.name())
                        .businessModel(WriteOffBusinessModelEnum.PROJECT_COLLECT.name())
                        .build();
                // 构建tab流水信息
                financeFlowTabRecords = map.values().stream().flatMap(Collection::stream).map(this::buildProjectCollectTabRecord).collect(Collectors.toList());
                // 构建tab匹配信息
                List<FinanceFlowMatchResult> tmpFinanceFlowMatchResults = new LinkedList<>();
                for (ContractTenantry contractTenantry : contractTenantryList) {
                    List<FinanceFlowMatchResult> flowMatchResults = businessFlowRecordMap.get(contractTenantry.getContractId());
                    if (CollUtil.isNotEmpty(flowMatchResults)) {
                        tmpFinanceFlowMatchResults.addAll(flowMatchResults);
                    }
                }
                normalAccountTriple = Triple.of(financeFlowTabMainInfo, financeFlowTabRecords, tmpFinanceFlowMatchResults);
                // 单个来款账户只有一个普通账户
                resultList.add(normalAccountTriple);
            }
        }
        return resultList;
    }

    private FinanceFlowTabRecord buildProjectCollectTabRecord(FinanceFlowRecord financeFlowRecord) {
        return FinanceFlowTabRecord.builder()
                .financeFlowType(FinanceFlowTypeEnum.RECEIPT.name())
                .financeFlowId(financeFlowRecord.getId())
                .bizDate(financeFlowRecord.getBiztime().toLocalDate())
                .bizAmount(LongUtil.other2Long(String.valueOf(financeFlowRecord.getCreditamount())))
                .otherAccountName(financeFlowRecord.getOppunit())
                .otherAccountNumber(financeFlowRecord.getOppbanknumber())
                .surplusAmount(Objects.isNull(financeFlowRecord.getSurplusAmount()) ? LongUtil.other2Long(String.valueOf(financeFlowRecord.getCreditamount())) : financeFlowRecord.getSurplusAmount())
                .build();
    }


    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void matchAmount(List<FinanceFlowTabRecord> financeFlowTabRecords, List<FinanceFlowMatchResult> financeFlowMatchResults) {
        log.info("【项目端-收款】开始匹配金额，tab流水集合：[{}]，匹配结果集合：[{}]", JSONUtil.parse(financeFlowTabRecords), JSONUtil.parse(financeFlowMatchResults));
        if (CollUtil.isEmpty(financeFlowTabRecords) || CollUtil.isEmpty(financeFlowMatchResults)) {
            throw MithrasException.newException("【项目端收款】匹配金额失败，tab流水集合为空，匹配结果集合为空，你可以尝试删除整个tab页");
        }
        // 1、排序
        financeFlowTabRecords.sort(Comparator.comparing(FinanceFlowTabRecord::getBizDate)
                .thenComparing(FinanceFlowTabRecord::getSurplusAmount)
                .thenComparing(FinanceFlowTabRecord::getId));
        financeFlowMatchResults = thisService.sortBusinessFlowRecord(financeFlowMatchResults);

        // 设置本次核销金额
        financeFlowMatchResults.forEach(financeFlowMatchResult -> {
            // 如果是手工改过的
            if (financeFlowMatchResult.getIsSystemGenerate() == 1) {
                financeFlowMatchResult.setThisWriteOffAmount(financeFlowMatchResult.getNoWriteOffAmount());
            } else {
                financeFlowMatchResult.setThisWriteOffAmount(financeFlowMatchResult.getThisWriteOffAmount());
            }
        });

        List<FinanceFlowMatchResult> needUpdateList = new LinkedList<>();

        // 2、开始递归匹配
        AtomicInteger businessFlowIndex = new AtomicInteger(0);
        AtomicInteger financeFlowIndex = new AtomicInteger(0);
        AtomicLong singleBusinessAmount = new AtomicLong(0);
        boolean noStop = true;
        while (noStop) {
            FinanceFlowTabRecord financeFlowTabRecord = financeFlowTabRecords.get(financeFlowIndex.get());
            FinanceFlowMatchResult financeFlowMatchResult = financeFlowMatchResults.get(businessFlowIndex.get());

            // 够还
            if (financeFlowTabRecord.getSurplusAmount() >= financeFlowMatchResult.getThisWriteOffAmount() && financeFlowMatchResult.getThisWriteOffAmount() > 0) {
                financeFlowTabRecord.setSurplusAmount(financeFlowTabRecord.getSurplusAmount() - financeFlowMatchResult.getThisWriteOffAmount());
                // 将银行流水放回去
                financeFlowTabRecords.set(financeFlowIndex.get(), financeFlowTabRecord);
                // 继续走下一条业务流水
                businessFlowIndex.incrementAndGet();
                singleBusinessAmount.set(0);
            } else if (financeFlowTabRecord.getSurplusAmount() < financeFlowMatchResult.getThisWriteOffAmount() && financeFlowMatchResult.getThisWriteOffAmount() > 0) {
                // 不够还
                financeFlowIndex.incrementAndGet();
                financeFlowMatchResult.setThisWriteOffAmount(financeFlowMatchResult.getThisWriteOffAmount() - financeFlowTabRecord.getSurplusAmount());
                // 将账单放回去
                financeFlowMatchResults.set(businessFlowIndex.get(), financeFlowMatchResult);
                singleBusinessAmount.addAndGet(financeFlowTabRecord.getSurplusAmount());
            } else {
                // 当前需要核销的金额为0，继续走下一条业务流水
                businessFlowIndex.incrementAndGet();
            }
            if (financeFlowIndex.get() >= financeFlowTabRecords.size()) {
                // 银行流水已经全部耗尽
                noStop = false;
                // 如果最后那条业务流水没有被完全核销，则将本次核销金额设置为最后的银行流水金额
                FinanceFlowMatchResult result = financeFlowMatchResults.get(businessFlowIndex.get());
                if (result.getThisWriteOffAmount() > 0) {
                    result.setThisWriteOffAmount(singleBusinessAmount.get());
                    needUpdateList.add(result);
                    businessFlowIndex.incrementAndGet();
                } else {
                    result.setThisWriteOffAmount(0L);
                    needUpdateList.add(result);
                }
                if (CollUtil.isNotEmpty(needUpdateList)) {
                    financeFlowMatchResultService.updateBatchById(needUpdateList);
                }
                // 如果还存在流水，将本次核销金额设置为0
                List<FinanceFlowMatchResult> needSetAmount2ZeroList = new LinkedList<>();
                while (businessFlowIndex.get() < financeFlowMatchResults.size()) {
                    FinanceFlowMatchResult matchResult = financeFlowMatchResults.get(businessFlowIndex.getAndIncrement());
                    needSetAmount2ZeroList.add(matchResult);
                }
                if (CollUtil.isNotEmpty(needSetAmount2ZeroList)) {
                    financeFlowMatchResultService.lambdaUpdate()
                            .set(FinanceFlowMatchResult::getThisWriteOffAmount, 0L)
                            .in(FinanceFlowMatchResult::getId, needSetAmount2ZeroList.stream().map(FinanceFlowMatchResult::getId).collect(Collectors.toList()))
                            .update();
                }
            }
            if (businessFlowIndex.get() >= financeFlowMatchResults.size()) {
                // 业务流水已经全部耗尽
                noStop = false;
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public List<Long> actualWriteOff(List<FinanceFlowTabRecord> financeFlowTabRecords, List<FinanceFlowMatchResult> financeFlowMatchResults) {
        log.info("【项目端-收款】开始执行核销，匹配结果集合：[{}]", JSONUtil.parse(financeFlowMatchResults));
        if (CollUtil.isEmpty(financeFlowMatchResults)) {
            throw MithrasException.newException("【项目端-收款】核销失败，匹配结果集合为空");
        }

        List<Long> resultList = new LinkedList<>();
        Map<Long, FinanceFlowRecord> financeFlowRecordMap = financeFlowRecordService.listByIds(financeFlowTabRecords.stream().map(FinanceFlowTabRecord::getFinanceFlowId).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(FinanceFlowRecord::getId, Function.identity(), (a, b) -> a));
        // 1、排序
        financeFlowTabRecords.sort(Comparator.comparing(FinanceFlowTabRecord::getBizDate)
                .thenComparing(FinanceFlowTabRecord::getSurplusAmount)
                .thenComparing(FinanceFlowTabRecord::getId));
        financeFlowMatchResults = thisService.sortBusinessFlowRecord(financeFlowMatchResults);

        // 2、开始递归匹配
        AtomicInteger businessFlowIndex = new AtomicInteger(0);
        AtomicInteger financeFlowIndex = new AtomicInteger(0);
        List<FinanceFlowMatchResult> updateList = new LinkedList<>();
        List<ThirdCollectionRecordREQ> needWriteOffReqList = new LinkedList<>();
        boolean noStop = true;
        while (noStop) {
            FinanceFlowTabRecord financeFlowTabRecord = financeFlowTabRecords.get(financeFlowIndex.get());
            FinanceFlowMatchResult financeFlowMatchResult = financeFlowMatchResults.get(businessFlowIndex.get());

            // 够还
            if (financeFlowTabRecord.getSurplusAmount() >= financeFlowMatchResult.getThisWriteOffAmount() && financeFlowMatchResult.getThisWriteOffAmount() > 0) {
                financeFlowTabRecord.setSurplusAmount(financeFlowTabRecord.getSurplusAmount() - financeFlowMatchResult.getThisWriteOffAmount());
                List<Long> flowIds = JSONUtil.toList(CharSequenceUtil.isBlank(financeFlowMatchResult.getFinanceFlowIdList()) ? "[]" : financeFlowMatchResult.getFinanceFlowIdList(), Long.class);
                flowIds.add(financeFlowTabRecord.getFinanceFlowId());
                financeFlowMatchResult.setFinanceFlowIdList(flowIds.toString());
                List<String> bankFlowNoList = JSONUtil.toList(CharSequenceUtil.isBlank(financeFlowMatchResult.getBankFlowNoList()) ? "[]" : financeFlowMatchResult.getBankFlowNoList(), String.class);
                bankFlowNoList.add(financeFlowTabRecord.getBankFlowNo());
                financeFlowMatchResult.setBankFlowNoList(bankFlowNoList.toString());
                ThirdCollectionRecordREQ req = thisService.buildThirdCollectionRecordReq(financeFlowMatchResult, financeFlowMatchResult.getThisWriteOffAmount(), financeFlowRecordMap.get(financeFlowTabRecord.getFinanceFlowId()));
                needWriteOffReqList.add(req);
                // 将银行流水放回去
                financeFlowTabRecords.set(financeFlowIndex.get(), financeFlowTabRecord);

                // 如果已经到最后一条流水了，需要单独更新该条流水状态
                // 继续走下一条业务流水
                businessFlowIndex.incrementAndGet();
                if (businessFlowIndex.get() == financeFlowMatchResults.size() && financeFlowTabRecord.getSurplusAmount() > 0) {
                    financeFlowRecordService.lambdaUpdate()
                            .eq(FinanceFlowRecord::getId, financeFlowTabRecord.getFinanceFlowId())
                            .set(FinanceFlowRecord::getShowInList, YesOrNoNumberEnum.YES.getCode())
                            .set(FinanceFlowRecord::getSurplusAmount, financeFlowTabRecord.getSurplusAmount())
                            .set(FinanceFlowRecord::getWriteOffType, FinanceWriteOffTypeEnum.AUTO_WRITE_OFF.name())
                            .set(FinanceFlowRecord::getFinancingProjectType, BankFlowWriteOffTypeEnum.PROJ_SIDE.name())
                            .set(FinanceFlowRecord::getFinancingFlowType, BankFlowCenterTypeEnum.PROCESSING_CENTER.name())
                            .set(FinanceFlowRecord::getWriteOffStatus, FinancingFlowWriteOffStatusEnum.PART_WRITE_OFF.name())
                            .update();
                    noStop = false;
                }
                if (financeFlowTabRecord.getSurplusAmount() == 0) {
                    resultList.add(financeFlowTabRecord.getFinanceFlowId());
                }
                updateList.add(financeFlowMatchResult);
            } else if (financeFlowTabRecord.getSurplusAmount() < financeFlowMatchResult.getThisWriteOffAmount() && financeFlowMatchResult.getThisWriteOffAmount() > 0) {
                // 不够还
                List<Long> flowIds = JSONUtil.toList(CharSequenceUtil.isBlank(financeFlowMatchResult.getFinanceFlowIdList()) ? "[]" : financeFlowMatchResult.getFinanceFlowIdList(), Long.class);
                flowIds.add(financeFlowTabRecord.getFinanceFlowId());
                financeFlowMatchResult.setFinanceFlowIdList(flowIds.toString());
                List<String> bankFlowNoList = JSONUtil.toList(CharSequenceUtil.isBlank(financeFlowMatchResult.getBankFlowNoList()) ? "[]" : financeFlowMatchResult.getBankFlowNoList(), String.class);
                bankFlowNoList.add(financeFlowTabRecord.getBankFlowNo());
                financeFlowMatchResult.setBankFlowNoList(bankFlowNoList.toString());
                financeFlowIndex.incrementAndGet();
                ThirdCollectionRecordREQ req = thisService.buildThirdCollectionRecordReq(financeFlowMatchResult, financeFlowTabRecord.getSurplusAmount(), financeFlowRecordMap.get(financeFlowTabRecord.getFinanceFlowId()));
                needWriteOffReqList.add(req);
                financeFlowMatchResult.setThisWriteOffAmount(financeFlowMatchResult.getThisWriteOffAmount() - financeFlowTabRecord.getSurplusAmount());
                // 将账单放回去
                financeFlowMatchResults.set(businessFlowIndex.get(), financeFlowMatchResult);
                financeFlowTabRecord.setSurplusAmount(0L);
                resultList.add(financeFlowTabRecord.getFinanceFlowId());
            } else {
                // 说明当前需要核销的钱是小于等于0的，直接跳过
                businessFlowIndex.incrementAndGet();
            }
            if (financeFlowIndex.get() >= financeFlowTabRecords.size() || businessFlowIndex.get() >= financeFlowMatchResults.size()) {
                // 业务账单或者银行流水已经全部耗尽
                noStop = false;
            }
        }
        if (CollUtil.isNotEmpty(updateList)) {
            log.info("【项目端-收款】更新匹配结果，匹配结果集合：[{}]", JSONUtil.parse(updateList));
            financeFlowMatchResultService.updateBatchById(updateList);
        }

        if (CollUtil.isNotEmpty(needWriteOffReqList)) {
            log.info("【项目端-收款】开始写入核销流水，待写入流水集合：[{}]", JSONUtil.parse(needWriteOffReqList));
            needWriteOffReqList.forEach(req -> financialService.collectionRecode(req));
        }
        return resultList;
    }

    public ThirdCollectionRecordREQ buildThirdCollectionRecordReq(FinanceFlowMatchResult financeFlowMatchResult, Long writeOffAmount, FinanceFlowRecord flow) {
        ThirdCollectionRecordREQ collectionRecordReq = new ThirdCollectionRecordREQ();
        collectionRecordReq.setCollectionAmount(LongUtil.tenThousand2Dollar(String.valueOf(writeOffAmount)));
        String cashFlowItem = financeFlowMatchResult.getCashFlowItem();
        if (CharSequenceUtil.equalsAny(cashFlowItem, CollectionWriteOffOrderEnum.PENALTY_INTEREST.name(),
                CollectionWriteOffOrderEnum.INTEREST.name(), CollectionWriteOffOrderEnum.PRINCIPAL.name(), CollectionWriteOffOrderEnum.FIRST_INSTALLMENT_INTEREST.name())) {
            //这里需要处理各自的核销金额
            if (CharSequenceUtil.equals(cashFlowItem, CollectionWriteOffOrderEnum.PENALTY_INTEREST.name())) {
                collectionRecordReq.setPenaltyInterest(LongUtil.tenThousand2Dollar(String.valueOf(writeOffAmount)));
            }
            if (CharSequenceUtil.equals(cashFlowItem, CollectionWriteOffOrderEnum.PRINCIPAL.name())) {
                collectionRecordReq.setPrincipal(LongUtil.tenThousand2Dollar(String.valueOf(writeOffAmount)));
            }
            if (CharSequenceUtil.equals(cashFlowItem, CollectionWriteOffOrderEnum.INTEREST.name())) {
                collectionRecordReq.setInterest(LongUtil.tenThousand2Dollar(String.valueOf(writeOffAmount)));
            }
            if (CharSequenceUtil.equals(cashFlowItem, CollectionWriteOffOrderEnum.FIRST_INSTALLMENT_INTEREST.name())) {
                collectionRecordReq.setInterest(LongUtil.tenThousand2Dollar(String.valueOf(writeOffAmount)));
            }
            cashFlowItem = CashFlowItemEnum.RENT.name();
        }
        collectionRecordReq.setCollectionDate(flow.getBiztime().toLocalDate());
        collectionRecordReq.setCollectionType(PaymentMethod.WY.display);
        CollectionBaseInfo collectionBaseInfo = getBean(CollectionBaseInfoMapper.class).selectOne(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .eq(CollectionBaseInfo::getCode, financeFlowMatchResult.getCashFlowCode())
                .eq(CollectionBaseInfo::getCashFlowItem, cashFlowItem)
                .last(StringUtil.mysqlLimitOne()));
        collectionRecordReq.setCollectionCode(collectionBaseInfo.getCode());
        collectionRecordReq.setCashFlowItem(cashFlowItem);
        collectionRecordReq.setDataSource(WriteOffTypeEnum.AUTO_RECORD.display());
        collectionRecordReq.setInvoiceFlag(0);
        collectionRecordReq.setOurAccountBank(flow.getAccountbankAcctname());
        collectionRecordReq.setOurAccountName(flow.getAccountbankName());
        collectionRecordReq.setOurAccountNumber(flow.getAccountbankBankaccountnumber());
        collectionRecordReq.setPknumber(UUID.fastUUID().toString());
        collectionRecordReq.setFinanceFlowId(flow.getId());
        collectionRecordReq.setBankDetailNo(flow.getBillno());
        return collectionRecordReq;
    }
}
