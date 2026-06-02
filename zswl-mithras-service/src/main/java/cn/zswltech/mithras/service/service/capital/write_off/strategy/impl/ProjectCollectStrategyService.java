package cn.zswltech.mithras.service.service.capital.write_off.strategy.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.dto.capital.write_off.*;
import cn.zswltech.mithras.service.config.redis.RedisDistLock;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.capital.*;
import cn.zswltech.mithras.service.enums.capital.write_off.FinanceFlowTypeEnum;
import cn.zswltech.mithras.service.enums.capital.write_off.FinanceWriteOffStatusEnum;
import cn.zswltech.mithras.service.enums.capital.write_off.WriteOffAccountTypeEnum;
import cn.zswltech.mithras.service.enums.capital.write_off.WriteOffBusinessModelEnum;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionBaseInfo;
import cn.zswltech.mithras.third.mapper.model.FinanceFlowMatchResult;
import cn.zswltech.mithras.third.mapper.model.FinanceFlowRecord;
import cn.zswltech.mithras.third.mapper.model.FinanceFlowTabMainInfo;
import cn.zswltech.mithras.third.mapper.model.FinanceFlowTabRecord;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.capital.FinanceFlowAutoWriteOffService;
import cn.zswltech.mithras.service.service.capital.write_off.impl.ProjectCollectWriteOffServiceImpl;
import cn.zswltech.mithras.service.service.capital.write_off.strategy.ManualWriteOffStrategyInterface;
import cn.zswltech.mithras.service.service.collection.CollectionBaseInfoService;
import cn.zswltech.mithras.third.service.FinanceFlowMatchResultService;
import cn.zswltech.mithras.service.service.third.FinanceFlowRecordService;
import cn.zswltech.mithras.third.service.FinanceFlowTabMainInfoService;
import cn.zswltech.mithras.third.service.FinanceFlowTabRecordService;
import cn.zswltech.mithras.service.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.testng.internal.MapList;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author bigbear
 * @date 2024/9/20 09:54
 * @description
 */
@Slf4j
@Service
public class ProjectCollectStrategyService implements ManualWriteOffStrategyInterface {

    private static final String MANUAL_WRITE_OFF_LOCK_KEY = "MANUAL_WRITE_OFF_LOCK_KEY";
    @Resource
    private RedisDistLock redisDistLock;
    @Resource
    private ProjectCollectStrategyService thisService;
    @Resource
    private FinanceFlowRecordService financeFlowRecordService;
    @Resource
    private FinanceFlowTabMainInfoService financeFlowTabMainInfoService;
    @Resource
    private FinanceFlowTabRecordService financeFlowTabRecordService;
    @Resource
    private FinanceFlowMatchResultService financeFlowMatchResultService;
    @Resource
    private ProjectCollectWriteOffServiceImpl projectCollectWriteOffService;
    @Resource
    private FinanceFlowAutoWriteOffService financeFlowAutoWriteOffService;
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;

    @Override
    public WriteOffBusinessModelEnum getWriteOffBusinessModel() {
        return WriteOffBusinessModelEnum.PROJECT_COLLECT;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void manualWriteOff(String batchNumber) {
        log.info("project collect manual write off, requestParam = {}", batchNumber);
        List<FinanceFlowTabMainInfo> mainInfos = financeFlowTabMainInfoService.list(Wrappers.<FinanceFlowTabMainInfo>lambdaQuery()
                .eq(FinanceFlowTabMainInfo::getIsExpired, YesOrNoNumberEnum.NO.getCode())
                .eq(FinanceFlowTabMainInfo::getBatchNumber, batchNumber));
        Assert.isTrue(CollUtil.isNotEmpty(mainInfos), () -> MithrasException.newException(ResultMsg.RECORD_NOT_EXIST));

        // 找到所有的tab银行流水信息，然后根据mainId分组
        Map<Long, List<FinanceFlowTabRecord>> tabFlowRecordListMap = financeFlowTabRecordService.list(Wrappers.<FinanceFlowTabRecord>lambdaQuery()
                .in(FinanceFlowTabRecord::getMainId, mainInfos.stream().map(FinanceFlowTabMainInfo::getId).collect(Collectors.toList()))
        ).stream().collect(Collectors.groupingBy(FinanceFlowTabRecord::getMainId));
        List<Long> allFlowRecordIds = tabFlowRecordListMap.values().stream().flatMap(Collection::stream).map(FinanceFlowTabRecord::getFinanceFlowId).collect(Collectors.toList());
        // 找到所有的tab账单流水信息，然后根据mainId分组
        Map<Long, List<FinanceFlowMatchResult>> tabFlowMatchResultListMap = financeFlowMatchResultService.list(Wrappers.<FinanceFlowMatchResult>lambdaQuery()
                .in(FinanceFlowMatchResult::getMainId, mainInfos.stream().map(FinanceFlowTabMainInfo::getId).collect(Collectors.toList()))
        ).stream().collect(Collectors.groupingBy(FinanceFlowMatchResult::getMainId));
        // 将Tab分类
        Map<String, Map<String, List<FinanceFlowTabMainInfo>>> tabGroupMap = new HashMap<>(16);
        Map<String, List<FinanceFlowTabMainInfo>> otherNameMap = mainInfos.stream().collect(Collectors.groupingBy(FinanceFlowTabMainInfo::getBankAccountName));
        otherNameMap.forEach((otherName, financeFlowTabRecordList) -> {
            // 将每个来款账户下的账户分类
            tabGroupMap.put(otherName, financeFlowTabRecordList.stream().collect(Collectors.groupingBy(FinanceFlowTabMainInfo::getAccountType)));
        });

        List<FinanceFlowTabMainInfo> earlyWriteOffList = new LinkedList<>();
        List<FinanceFlowTabMainInfo> lateWriteOffList = new LinkedList<>();
        // 至此，已经将所有tab按照来款账户和账户类型进行了分类收集
        tabGroupMap.forEach((otherName, accountTypeMap) -> {
            // 1、查看当前来款账户是不是存在监管户
            if (accountTypeMap.containsKey(WriteOffAccountTypeEnum.SUPERVISION_ACCOUNT.name())) {
                // 1.1、存在监管户
                List<FinanceFlowTabMainInfo> financeFlowTabMainInfos = accountTypeMap.get(WriteOffAccountTypeEnum.SUPERVISION_ACCOUNT.name());
                AtomicReference<Boolean> isSupervisionAccountMatch = new AtomicReference<>(true);
                financeFlowTabMainInfos.forEach(financeFlowTabMainInfo -> {
                    if (YesOrNoNumberEnum.YES.getCode().equals(financeFlowTabMainInfo.getIsPerfectMatch())) {
                        if (thisService.checkIsExistPenaltyAndFlowDateIsBeforeNow(financeFlowTabMainInfo, tabFlowRecordListMap, tabFlowMatchResultListMap)) {
                            earlyWriteOffList.add(financeFlowTabMainInfo);
                        } else {
                            isSupervisionAccountMatch.getAndSet(false);
                        }
                    } else {
                        thisService.checkIsExistPenaltyAndFlowDateIsBeforeNow(financeFlowTabMainInfo, tabFlowRecordListMap, tabFlowMatchResultListMap);
                        isSupervisionAccountMatch.getAndSet(false);
                    }
                });
                if (Boolean.TRUE.equals(isSupervisionAccountMatch.get())) {
                    // 1.2、监管账户都是完美匹配，将所有普通户加入晚核销列表
                    List<FinanceFlowTabMainInfo> financeFlowTabMainInfoList = Optional.ofNullable(accountTypeMap.get(WriteOffAccountTypeEnum.ORDINARY_ACCOUNT.name())).orElse(Collections.emptyList());
                    for (FinanceFlowTabMainInfo financeFlowTabMainInfo : financeFlowTabMainInfoList) {
                        if (thisService.checkIsExistPenaltyAndFlowDateIsBeforeNow(financeFlowTabMainInfo, tabFlowRecordListMap, tabFlowMatchResultListMap)) {
                            lateWriteOffList.add(financeFlowTabMainInfo);
                        }
                    }
                }
            } else {
                // 1.2、不存在监管户
                // 如果流水存在交易日期在今天之前，需要判断当前待核销账单不存在罚息
                List<FinanceFlowTabMainInfo> financeFlowTabMainInfoList = accountTypeMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
                for (FinanceFlowTabMainInfo financeFlowTabMainInfo : financeFlowTabMainInfoList) {
                    if (thisService.checkIsExistPenaltyAndFlowDateIsBeforeNow(financeFlowTabMainInfo, tabFlowRecordListMap, tabFlowMatchResultListMap)) {
                        lateWriteOffList.add(financeFlowTabMainInfo);
                    }
                }
            }
        });

        List<Long> mainTabIds = mainInfos.stream().map(FinanceFlowTabMainInfo::getId).collect(Collectors.toList());
        Map<Long, List<FinanceFlowTabRecord>> bankFlowGroup = financeFlowTabRecordService.list(Wrappers.<FinanceFlowTabRecord>lambdaQuery().in(FinanceFlowTabRecord::getMainId, mainTabIds))
                .stream().collect(Collectors.groupingBy(FinanceFlowTabRecord::getMainId));
        Map<Long, List<FinanceFlowMatchResult>> businessFlowGroup = financeFlowMatchResultService.list(Wrappers.<FinanceFlowMatchResult>lambdaQuery().in(FinanceFlowMatchResult::getMainId, mainTabIds))
                .stream().collect(Collectors.groupingBy(FinanceFlowMatchResult::getMainId));
        List<Long> writeOffedBankFlowIds = new LinkedList<>();
        if (CollUtil.isNotEmpty(earlyWriteOffList)) {
            earlyWriteOffList.forEach(financeFlowTabMainInfo -> {
                List<FinanceFlowTabRecord> financeFlowTabRecordList = bankFlowGroup.get(financeFlowTabMainInfo.getId());
                List<FinanceFlowMatchResult> financeFlowMatchResultList = businessFlowGroup.get(financeFlowTabMainInfo.getId());
                writeOffedBankFlowIds.addAll(projectCollectWriteOffService.actualWriteOff(financeFlowTabRecordList, financeFlowMatchResultList));
            });
        }
        if (CollUtil.isNotEmpty(lateWriteOffList)) {
            lateWriteOffList.forEach(financeFlowTabMainInfo -> {
                List<FinanceFlowTabRecord> financeFlowTabRecordList = bankFlowGroup.get(financeFlowTabMainInfo.getId());
                List<FinanceFlowMatchResult> financeFlowMatchResultList = businessFlowGroup.get(financeFlowTabMainInfo.getId());
                writeOffedBankFlowIds.addAll(projectCollectWriteOffService.actualWriteOff(financeFlowTabRecordList, financeFlowMatchResultList));
            });
        }
        log.info("project collect manual write off, writeOffedBankFlowIds = {}", writeOffedBankFlowIds);
        if (CollUtil.isNotEmpty(writeOffedBankFlowIds)) {
            // 更新银行流水状态
            financeFlowRecordService.lambdaUpdate()
                    .in(FinanceFlowRecord::getId, writeOffedBankFlowIds)
                    .set(FinanceFlowRecord::getSurplusAmount, 0)
                    .set(FinanceFlowRecord::getWriteOffType, FinanceWriteOffTypeEnum.AUTO_WRITE_OFF.name())
                    .set(FinanceFlowRecord::getFinancingFlowType, BankFlowCenterTypeEnum.PROCESSED_PROJ_SIDE.name())
                    .set(FinanceFlowRecord::getFinancingProjectType, BankFlowWriteOffTypeEnum.PROJ_SIDE.name())
                    .set(FinanceFlowRecord::getWriteOffStatus, FinancingFlowWriteOffStatusEnum.COMPLETE_WRITE_OFF.name())
                    .update();
            // 通知何康林这些银行流水已经核销完毕，需要主动推送苍穹
            for (Long writeOffedBankFlowId : writeOffedBankFlowIds) {
                financeFlowAutoWriteOffService.writeOffNotice(Collections.singletonList(writeOffedBankFlowId));
            }
        }
        // 全部操作完成后，需要释放本次所有的流水
        financeFlowRecordService.lambdaUpdate()
                .set(FinanceFlowRecord::getShowInList, YesOrNoNumberEnum.YES.getCode())
                .in(FinanceFlowRecord::getId, allFlowRecordIds)
                .update();
    }

    @Transactional(rollbackFor = Throwable.class)
    public boolean checkIsExistPenaltyAndFlowDateIsBeforeNow(FinanceFlowTabMainInfo financeFlowTabMainInfo,
                                                             Map<Long, List<FinanceFlowTabRecord>> tabFlowRecordListMap,
                                                             Map<Long, List<FinanceFlowMatchResult>> tabFlowMatchResultListMap) {
        // 如果流水存在交易日期在今天之前，需要判断当前待核销账单不存在罚息
        boolean dateIsBeforeNow = tabFlowRecordListMap.get(financeFlowTabMainInfo.getId()).stream().anyMatch(obj -> obj.getBizDate().isBefore(LocalDate.now()));
        List<FinanceFlowMatchResult> financeFlowMatchResultList = tabFlowMatchResultListMap.get(financeFlowTabMainInfo.getId());
        boolean existPenalty = false;
        if (CollUtil.isNotEmpty(financeFlowMatchResultList)) {
            existPenalty = financeFlowMatchResultList.stream().anyMatch(obj -> obj.getCashFlowItem().equals(CollectionWriteOffOrderEnum.PENALTY_INTEREST.name()) && obj.getThisWriteOffAmount() > 0);
        }
        boolean result = !dateIsBeforeNow || !existPenalty;
        if (!result) {
            // 不核销了，需要归还流水
            financeFlowRecordService.lambdaUpdate()
                    .in(FinanceFlowRecord::getId, tabFlowRecordListMap.get(financeFlowTabMainInfo.getId()).stream().map(FinanceFlowTabRecord::getFinanceFlowId).collect(Collectors.toList()))
                    .set(FinanceFlowRecord::getShowInList, YesOrNoNumberEnum.YES.getCode())
                    .update();
        }
        return result;
    }

    @Override
    public CheckBeforeImportRSP checkBeforeImport(CheckBeforeImportREQ req) {
        // 当前分支应该只允许添加项目收款流水，如果存在付款的流水，应该提示用户
        List<FinanceFlowRecord> flowRecordList = financeFlowRecordService.listByIds(req.getBankFlowIds());
        boolean isEmpty = flowRecordList.stream()
                .filter(bo -> Objects.nonNull(bo.getCreditamount()))
                .noneMatch(bo -> bo.getDebitamount() > 0);
        Assert.isTrue(isEmpty, () -> MithrasException.newException("当前只允许选择项目端收款类型的流水，请检查后重试"));
        // 如果存在流水是核销完毕的直接报错
        boolean isOk = flowRecordList.stream().anyMatch(f -> f.getWriteOffStatus().equals(FinancingFlowWriteOffStatusEnum.COMPLETE_WRITE_OFF.name()));
        Assert.isFalse(isOk, () -> MithrasException.newException("当前所选流水存在【核销完毕】的流水，请刷新列表后重试"));

        CheckBeforeImportRSP rsp = new CheckBeforeImportRSP();
        // 流水是合法的，开始做分组，并且寻找是否存在同来款账户未选中的流水
        Map<String, List<FinanceFlowRecord>> flowGroupByOtherName = flowRecordList.stream().collect(Collectors.groupingBy(FinanceFlowRecord::getOppunit));
        // 找到库中当前所选的来款账户的所有流水并且分组
        Map<String, List<FinanceFlowRecord>> inDbGroup = financeFlowRecordService.list(Wrappers.<FinanceFlowRecord>lambdaQuery()
                .eq(FinanceFlowRecord::getFinancingFlowType, BankFlowCenterTypeEnum.PROCESSING_CENTER.name())
                .gt(FinanceFlowRecord::getCreditamount, 0)
                .eq(FinanceFlowRecord::getShowInList, YesOrNoNumberEnum.YES.getCode())
                .in(FinanceFlowRecord::getOppunit, flowGroupByOtherName.keySet())
        ).stream().collect(Collectors.groupingBy(FinanceFlowRecord::getOppunit));
        Map<String, List<BankFlowCenterListBO>> unSelectedBankFlowMap = new HashMap<>();
        flowGroupByOtherName.forEach((otherName, flowList) -> {
            List<FinanceFlowRecord> financeFlowRecords = inDbGroup.get(otherName);
            //Assert.notEmpty(financeFlowRecords, () -> MithrasException.newException("当前来款账户下没有未核销的收款流水，请检查后重试"));
            if (CollUtil.isEmpty(financeFlowRecords)) {
                return;
            }
            // 遍历当前选中的流水，如果存在未选中的，放到结果集中，提示用户
            flowList.forEach(bo -> financeFlowRecords.removeIf(bankFlow -> bankFlow.getId().equals(bo.getId())));
            if (CollUtil.isNotEmpty(financeFlowRecords)) {
                unSelectedBankFlowMap.put(otherName, this.buildBankFlowCenterListNoticeBoList(financeFlowRecords));
            }
        });
        rsp.setUnSelectedBankFlowMap(unSelectedBankFlowMap);
        return rsp;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    @SuppressWarnings("unchecked")
    public List<FlowMatchResultRSP> flowMatchResult(CheckBeforeImportREQ req) {
        log.info("project collect manual write off, flowMatchResult requestParam = {}", req);
        try {
            // 获取锁
            redisDistLock.tryLockWithoutReleaseTime(MANUAL_WRITE_OFF_LOCK_KEY, 5000);
            // 开始生成批次号，同一时间只允许生成一个批次号
            String batchNumber = generateBatchNumber();
            // 检查流水是不是已经被占用了
            List<FinanceFlowRecord> flowRecordList = financeFlowRecordService.list(Wrappers.<FinanceFlowRecord>lambdaQuery()
                    .in(FinanceFlowRecord::getId, req.getBankFlowIds())
                    .eq(FinanceFlowRecord::getLogicDeleteFlag, YesOrNoNumberEnum.NO.getCode())
                    .eq(FinanceFlowRecord::getShowInList, YesOrNoNumberEnum.YES.getCode()));
            if (CollUtil.isEmpty(flowRecordList) || flowRecordList.size() != req.getBankFlowIds().size()) {
                throw MithrasException.newException("当前流水已经被占用，请检查后重试");
            }
            // 将流水占用，防止流水重复使用，这里使用redis延迟回调保障占用流水后用户不操作，系统自动释放
            financeFlowRecordService.lambdaUpdate()
                    .set(FinanceFlowRecord::getShowInList, YesOrNoNumberEnum.NO.getCode())
                    .in(FinanceFlowRecord::getId, flowRecordList.stream().map(FinanceFlowRecord::getId).collect(Collectors.toList()))
                    .update();

            //开始生成匹配结果
            Object bankFlowRecordGrouping = projectCollectWriteOffService.domainBankFlowRecordGrouping(flowRecordList);
            List<CollectionBaseInfo> businessFlowRecordList = (List<CollectionBaseInfo>) projectCollectWriteOffService.getBusinessFlowRecordList(flowRecordList, req.getPlusDays());
            Map<Long, List<FinanceFlowMatchResult>> flowRecordGrouping = projectCollectWriteOffService.domainFlowRecordGrouping(businessFlowRecordList);
            List<Triple<FinanceFlowTabMainInfo, List<FinanceFlowTabRecord>, List<FinanceFlowMatchResult>>> matchResultList = projectCollectWriteOffService.match(bankFlowRecordGrouping, flowRecordGrouping);

            List<Long> needReleaseFlowIds = new ArrayList<>(req.getBankFlowIds());
            List<FlowMatchResultRSP> flowMatchResultRspList = new LinkedList<>();
            matchResultList.forEach(triple -> {
                // 6、获取结果排序、入库
                FinanceFlowTabMainInfo financeFlowTabMainInfo = triple.getLeft();
                List<FinanceFlowTabRecord> financeFlowTabRecordList = triple.getMiddle();
                List<Long> collect = financeFlowTabRecordList.stream().map(FinanceFlowTabRecord::getFinanceFlowId).collect(Collectors.toList());
                // 匹配到的需要从待释放流水列表中删除
                needReleaseFlowIds.removeIf(collect::contains);
                List<FinanceFlowMatchResult> financeFlowMatchResults = triple.getRight();
                financeFlowTabMainInfo.setWriteOffStatus(FinanceWriteOffStatusEnum.NO_WRITE_OFF.name());
                financeFlowTabMainInfo.setBatchNumber(batchNumber);
                financeFlowTabMainInfoService.save(financeFlowTabMainInfo);
                // 6.1 填充流水关联ID
                if (CollUtil.isNotEmpty(financeFlowTabRecordList)) {
                    financeFlowTabRecordList.forEach(financeFlowTabRecord -> financeFlowTabRecord.setMainId(financeFlowTabMainInfo.getId()));
                    financeFlowTabRecordService.saveBatch(financeFlowTabRecordList);
                }
                // 6.2 填充账单关联ID
                if (CollUtil.isNotEmpty(financeFlowMatchResults)) {
                    financeFlowMatchResults.forEach(financeFlowMatchResult -> financeFlowMatchResult.setMainId(financeFlowTabMainInfo.getId()));
                    financeFlowMatchResultService.saveBatch(financeFlowMatchResults);
                }

                // 7、匹配金额，将多余的账单核销金额设置为0
                if (CollUtil.isNotEmpty(financeFlowMatchResults) && CollUtil.isNotEmpty(financeFlowTabRecordList)) {
                    projectCollectWriteOffService.matchAmount(financeFlowTabRecordList, financeFlowMatchResults);
                }

                // 检查是否完美匹配
                thisService.reCheckIsPerfectMatch(financeFlowTabMainInfo);
                FinanceFlowTabMainInfo tabMainInfoServiceById = financeFlowTabMainInfoService.getById(financeFlowTabMainInfo.getId());
                // 重新将账单排序
                financeFlowMatchResults = projectCollectWriteOffService.sortBusinessFlowRecord(financeFlowMatchResults);
                // 8、构建返回结果
                flowMatchResultRspList.add(buildFlowMatchResultRsp(tabMainInfoServiceById, financeFlowMatchResults, financeFlowTabRecordList));
            });
            if (CollUtil.isEmpty(flowMatchResultRspList)) {
                financeFlowRecordService.lambdaUpdate()
                        .set(FinanceFlowRecord::getShowInList, YesOrNoNumberEnum.YES.getCode())
                        .in(FinanceFlowRecord::getId, req.getBankFlowIds())
                        .update();
                throw MithrasException.newException("当前流水没有匹配到任何账单，请检查后重试");
            }
            // 填充本次是否核销
            thisService.checkIsWriteOff(flowMatchResultRspList);
            // 未匹配上的直接释放掉
            if (CollUtil.isNotEmpty(needReleaseFlowIds)) {
                financeFlowRecordService.lambdaUpdate()
                        .set(FinanceFlowRecord::getShowInList, YesOrNoNumberEnum.YES.getCode())
                        .in(FinanceFlowRecord::getId, needReleaseFlowIds)
                        .update();
                log.info("银行流水id列表：[{}]未匹配到业务流水，直接释放！", needReleaseFlowIds);
            }
            return flowMatchResultRspList;
        } catch (Exception e) {
            financeFlowRecordService.lambdaUpdate()
                    .set(FinanceFlowRecord::getShowInList, YesOrNoNumberEnum.YES.getCode())
                    .in(FinanceFlowRecord::getId, req.getBankFlowIds())
                    .update();
            log.error("project collect manual write off, lock fail, errorMsg = {}", e.getMessage(), e);
            throw MithrasException.newException(e.getMessage());
        } finally {
            redisDistLock.unlock(MANUAL_WRITE_OFF_LOCK_KEY);
        }

    }

    public void checkIsWriteOff(List<FlowMatchResultRSP> flowMatchResultRspList) {
        log.info("project collect manual write off, checkIsWriteOff start, pram={}", flowMatchResultRspList);
        if (CollUtil.isNotEmpty(flowMatchResultRspList)) {
            List<FinanceFlowTabMainInfo> mainInfos = financeFlowTabMainInfoService.listByIds(flowMatchResultRspList.stream().map(FlowMatchResultRSP::getId).collect(Collectors.toList()));
            // 过滤出监管账户，并且根据来款账户名称做映射
            Map<String, List<FinanceFlowTabMainInfo>> supervisionAccountMap = mainInfos.stream().filter(obj -> Objects.equals(WriteOffAccountTypeEnum.SUPERVISION_ACCOUNT.name(), obj.getAccountType()))
                    .collect(Collectors.groupingBy(FinanceFlowTabMainInfo::getBankAccountName));
            Map<Long, FinanceFlowTabMainInfo> mainInfoMap = mainInfos.stream().collect(Collectors.toMap(FinanceFlowTabMainInfo::getId, Function.identity(), (a, b) -> a));
            // 找到所有的tab银行流水信息，然后根据mainId分组
            Map<Long, List<FinanceFlowTabRecord>> tabFlowRecordListMap = financeFlowTabRecordService.list(Wrappers.<FinanceFlowTabRecord>lambdaQuery()
                    .in(FinanceFlowTabRecord::getMainId, mainInfos.stream().map(FinanceFlowTabMainInfo::getId).collect(Collectors.toList()))
            ).stream().collect(Collectors.groupingBy(FinanceFlowTabRecord::getMainId));
            // 找到所有的tab账单流水信息，然后根据mainId分组
            Map<Long, List<FinanceFlowMatchResult>> tabFlowMatchResultListMap = financeFlowMatchResultService.list(Wrappers.<FinanceFlowMatchResult>lambdaQuery()
                    .in(FinanceFlowMatchResult::getMainId, mainInfos.stream().map(FinanceFlowTabMainInfo::getId).collect(Collectors.toList()))
            ).stream().collect(Collectors.groupingBy(FinanceFlowMatchResult::getMainId));
            log.info("project collect manual write off, checkIsWriteOff, supervisionAccountMap={}, tabFlowRecordListMap={}," +
                    " tabFlowMatchResultListMap={}", supervisionAccountMap, tabFlowRecordListMap, tabFlowMatchResultListMap);
            // 开始遍历
            flowMatchResultRspList.forEach(rsp -> {
                // 如果流水存在交易日期在今天之前，需要判断当前待核销账单不存在罚息
                boolean dateIsBeforeNow = tabFlowRecordListMap.get(rsp.getId()).stream().anyMatch(obj -> obj.getBizDate().isBefore(LocalDate.now()));
                boolean existPenalty = false;
                List<FinanceFlowMatchResult> financeFlowMatchResults = tabFlowMatchResultListMap.get(rsp.getId());
                if (CollUtil.isNotEmpty(financeFlowMatchResults)) {
                    existPenalty = financeFlowMatchResults.stream().anyMatch(obj -> obj.getCashFlowItem().equals(CollectionWriteOffOrderEnum.PENALTY_INTEREST.name()) && obj.getThisWriteOffAmount() > 0);
                }
                // 如果是监管户
                if (Objects.equals(WriteOffAccountTypeEnum.SUPERVISION_ACCOUNT.name(), rsp.getAccountType())) {
                    rsp.setIsWriteOff((!dateIsBeforeNow || !existPenalty) && Boolean.TRUE.equals(rsp.getPerfectMatch()));
                } else {
                    // 普通账户
                    if (!dateIsBeforeNow || !existPenalty) {
                        // 如果存在监管户，需要看监管户是否核销
                        FinanceFlowTabMainInfo tabInfo = mainInfoMap.get(rsp.getId());
                        if (Objects.isNull(tabInfo)) {
                            rsp.setIsWriteOff(true);
                            return;
                        }
                        List<FinanceFlowTabMainInfo> supervisionList = supervisionAccountMap.get(tabInfo.getBankAccountName());
                        if (CollUtil.isNotEmpty(supervisionList)) {
                            boolean existBeforeFlow = supervisionList.stream().anyMatch(e -> tabFlowRecordListMap.get(e.getId()).stream().anyMatch(obj -> obj.getBizDate().isBefore(LocalDate.now())));
                            boolean existPenaltyInterest;
                            List<FinanceFlowMatchResult> matchResults = tabFlowMatchResultListMap.get(rsp.getId());
                            if (CollUtil.isNotEmpty(matchResults)) {
                                existPenaltyInterest = matchResults.stream().anyMatch(obj -> obj.getCashFlowItem().equals(CollectionWriteOffOrderEnum.PENALTY_INTEREST.name()) && obj.getThisWriteOffAmount() > 0);
                            } else {
                                existPenaltyInterest = false;
                            }
                            rsp.setIsWriteOff(supervisionList.stream().noneMatch(obj -> obj.getIsPerfectMatch() == 0 || !(!existBeforeFlow || !existPenaltyInterest)));
                        } else {
                            rsp.setIsWriteOff(true);
                        }
                    } else {
                        rsp.setIsWriteOff(true);
                    }
                }
            });
        }
        List<Long> needWriteOffList = flowMatchResultRspList.stream().filter(FlowMatchResultRSP::getIsWriteOff).map(FlowMatchResultRSP::getId).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(needWriteOffList)) {
            financeFlowTabMainInfoService.lambdaUpdate()
                    .set(FinanceFlowTabMainInfo::getIsWriteOff, 1)
                    .in(FinanceFlowTabMainInfo::getId, needWriteOffList)
                    .update();
        }
        List<Long> noWriteOffList = flowMatchResultRspList.stream().filter(obj -> !obj.getIsWriteOff()).map(FlowMatchResultRSP::getId).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(noWriteOffList)) {
            financeFlowTabMainInfoService.lambdaUpdate()
                    .set(FinanceFlowTabMainInfo::getIsWriteOff, 0)
                    .in(FinanceFlowTabMainInfo::getId, noWriteOffList)
                    .update();
        }
    }

    private FlowMatchResultRSP buildFlowMatchResultRsp(FinanceFlowTabMainInfo mainInfo,
                                                       List<FinanceFlowMatchResult> financeFlowMatchResults,
                                                       List<FinanceFlowTabRecord> financeFlowTabRecordList) {
        FlowMatchResultRSP rsp = new FlowMatchResultRSP();
        rsp.setId(mainInfo.getId());
        rsp.setBatchNumber(mainInfo.getBatchNumber());
        rsp.setAccountType(mainInfo.getAccountType());
        rsp.setPerfectMatch(mainInfo.getIsPerfectMatch() == 1);
        rsp.setIsWriteOff(mainInfo.getIsWriteOff() == 1);
        rsp.setBankFlowAmountSum(CollUtil.isNotEmpty(financeFlowTabRecordList) ? financeFlowTabRecordList.stream().mapToLong(FinanceFlowTabRecord::getSurplusAmount).sum() : 0);
        rsp.setBusinessFlowAmountSum(CollUtil.isNotEmpty(financeFlowMatchResults) ? financeFlowMatchResults.stream().mapToLong(FinanceFlowMatchResult::getShouldWriteOffAmount).sum() : 0);
        if (WriteOffAccountTypeEnum.SUPERVISION_ACCOUNT.name().equals(mainInfo.getAccountType())) {
            String replace = mainInfo.getSuperviseAccountNumber().replace(" ", "");
            rsp.setTabName(mainInfo.getBankAccountName() + "-" + replace.substring(replace.length() - 4));
        } else {
            rsp.setTabName(mainInfo.getBankAccountName() + "-其他" + WriteOffAccountTypeEnum.ORDINARY_ACCOUNT.getDisplay());
        }
        // 设置银行流水列表 这里可以优化
        List<FinanceFlowRecord> financeFlowRecords = financeFlowRecordService.listByIds(financeFlowTabRecordList.stream().map(FinanceFlowTabRecord::getFinanceFlowId).collect(Collectors.toList()));

        if (CollUtil.isNotEmpty(financeFlowRecords)) {
            List<BankFlowCenterListBO> bankFlowList = buildBankFlowCenterListViewBoList(financeFlowRecords);
            bankFlowList.sort(Comparator.comparing(BankFlowCenterListBO::getTransactionDate)
                    .thenComparing(BankFlowCenterListBO::getCollectionAmount)
                    .thenComparing(BankFlowCenterListBO::getId));
            rsp.setBankFlowList(bankFlowList);
        }

        // 构建业务流水列表
        rsp.setBusinessFlowList(Collections.emptyList());
        if (CollUtil.isNotEmpty(financeFlowMatchResults)) {
            List<FinanceFlowMatchResultRSP> businessFlowList = buildBusinessMatchRsp(financeFlowMatchResults);
            Map<Long, String> clientId2NameMap = SpringUtil.getBean(Id2NameService.class).clientId2Name(businessFlowList.stream().map(FinanceFlowMatchResultRSP::getClientId).collect(Collectors.toList()));
            businessFlowList.forEach(financeFlowMatchResultRsp -> financeFlowMatchResultRsp.setClientName(clientId2NameMap.get(financeFlowMatchResultRsp.getClientId())));
            rsp.setBusinessFlowList(businessFlowList);
        }
        return rsp;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void deleteBankFlow(DeleteFlowREQ req) {
        log.info("project collect manual write off, delete bank flow, requestParam = {}", req);
        FinanceFlowTabMainInfo mainInfo = financeFlowTabMainInfoService.getById(req.getFinanceFlowTabMainInfoId());
        Assert.notNull(mainInfo, () -> MithrasException.newException(ResultMsg.RECORD_NOT_EXIST));

        // 查看删除的流水是否存在
        List<FinanceFlowTabRecord> flowTabRecordList = financeFlowTabRecordService.list(Wrappers.<FinanceFlowTabRecord>lambdaQuery()
                .eq(FinanceFlowTabRecord::getMainId, mainInfo.getId())
                .in(FinanceFlowTabRecord::getFinanceFlowId, req.getFlowIdList()));
        Assert.isTrue(CollUtil.isNotEmpty(flowTabRecordList), () -> MithrasException.newException("非法操作，当前tab不存在已选中流水"));
        Assert.isTrue(flowTabRecordList.size() == req.getFlowIdList().size(), () -> MithrasException.newException("非法操作，存在欲删除流水不属于当前Tab，请检查"));

        financeFlowTabRecordService.remove(Wrappers.<FinanceFlowTabRecord>lambdaQuery()
                .eq(FinanceFlowTabRecord::getMainId, mainInfo.getId())
                .in(FinanceFlowTabRecord::getFinanceFlowId, req.getFlowIdList()));
        financeFlowRecordService.lambdaUpdate()
                .set(FinanceFlowRecord::getShowInList, YesOrNoNumberEnum.YES.getCode())
                .in(FinanceFlowRecord::getId, req.getFlowIdList())
                .update();
        // 检查是否完美匹配
        thisService.reCheckIsPerfectMatch(mainInfo);

        // 重新匹配金额
        thisService.matchAmountAgain(mainInfo);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void reCheckIsPerfectMatch(FinanceFlowTabMainInfo mainInfo) {
        // 一开始不是完美匹配，需要重新计算是不是完美匹配
        boolean perfectMatch = projectCollectWriteOffService.checkPerfectMatch(mainInfo.getId());
        financeFlowTabMainInfoService.lambdaUpdate()
                .set(FinanceFlowTabMainInfo::getIsPerfectMatch, perfectMatch ? YesOrNoNumberEnum.YES.getCode() : YesOrNoNumberEnum.NO.getCode())
                .eq(FinanceFlowTabMainInfo::getId, mainInfo.getId())
                .update();

    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void addBankFlow(AddFlowREQ req) {
        log.info("project collect manual write off, add bank flow, requestParam = {}", req);
        FinanceFlowTabMainInfo mainInfo = financeFlowTabMainInfoService.getById(req.getFinanceFlowTabMainInfoId());
        Assert.notNull(mainInfo, () -> MithrasException.newException(ResultMsg.RECORD_NOT_EXIST));

        // 检查当前流水是否在当前Tab页已经存在
        List<FinanceFlowTabRecord> flowTabRecordList = financeFlowTabRecordService.list(Wrappers.<FinanceFlowTabRecord>lambdaQuery()
                .eq(FinanceFlowTabRecord::getMainId, mainInfo.getId())
                .in(FinanceFlowTabRecord::getFinanceFlowId, req.getFlowIdList()));
        Assert.isTrue(CollUtil.isEmpty(flowTabRecordList), () -> MithrasException.newException("当前tab已存在所选流水，请检查后重试"));

        // 添加到当前tab页
        List<FinanceFlowRecord> financeFlowRecords = financeFlowRecordService.listByIds(req.getFlowIdList());
        financeFlowTabRecordService.saveBatch(financeFlowRecords.stream().map(financeFlowRecord -> FinanceFlowTabRecord.builder()
                .mainId(mainInfo.getId())
                .financeFlowId(financeFlowRecord.getId())
                .financeFlowType(FinanceFlowTypeEnum.RECEIPT.name())
                .bizDate(LocalDate.parse(financeFlowRecord.getBizdate()))
                .bizAmount(LongUtil.other2Long(String.valueOf(financeFlowRecord.getCreditamount())))
                .surplusAmount(Objects.nonNull(financeFlowRecord.getSurplusAmount()) ? financeFlowRecord.getSurplusAmount() : LongUtil.other2Long(String.valueOf(financeFlowRecord.getCreditamount())))
                .bankFlowNo(financeFlowRecord.getBillno())
                .otherAccountName(financeFlowRecord.getOppunit())
                .otherAccountNumber(financeFlowRecord.getOppbanknumber())
                .build()
        ).collect(Collectors.toList()));

        // 检查是否完美匹配
        thisService.reCheckIsPerfectMatch(mainInfo);

        // 重新匹配金额
        thisService.matchAmountAgain(mainInfo);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updateBusinessFlow(UpdateBusinessFlowREQ req) {
        log.info("project collect manual write off, update business flow, requestParam = {}", req);
        FinanceFlowTabMainInfo tabMainInfo = financeFlowTabMainInfoService.getById(req.getFinanceFlowTabMainInfoId());
        Assert.notNull(tabMainInfo, () -> MithrasException.newException(ResultMsg.RECORD_NOT_EXIST));

        // 查看当前业务流水是否存在
        FinanceFlowMatchResult matchResult = financeFlowMatchResultService.getById(req.getId());
        Assert.notNull(matchResult, () -> MithrasException.newException("非法操作，当前tab不存在欲修改的业务流水"));

        // 更新的时候，设置为非系统生成
        financeFlowMatchResultService.lambdaUpdate()
                .eq(FinanceFlowMatchResult::getId, matchResult.getId())
                .set(FinanceFlowMatchResult::getIsSystemGenerate, YesOrNoNumberEnum.NO.getCode())
                .set(FinanceFlowMatchResult::getThisWriteOffAmount, req.getThisWriteOffAmount())
                .update();
        thisService.reCheckIsPerfectMatch(tabMainInfo);

        // 重新匹配金额
        thisService.matchAmountAgain(tabMainInfo);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void matchAmountAgain(FinanceFlowTabMainInfo tabMainInfo) {
        // 拿到当前tab页来款账户的所有tab页面
        Map<Long, FinanceFlowTabMainInfo> tabMainInfoMap = financeFlowTabMainInfoService.list(Wrappers.<FinanceFlowTabMainInfo>lambdaQuery()
                        .eq(FinanceFlowTabMainInfo::getBankAccountName, tabMainInfo.getBankAccountName())
                        .eq(FinanceFlowTabMainInfo::getBatchNumber, tabMainInfo.getBatchNumber()))
                .stream().collect(Collectors.toMap(FinanceFlowTabMainInfo::getId, Function.identity(), (a, b) -> a));
        // 拿到当前tab页来款账户的所有tab页面
        Map<Long, List<FinanceFlowTabRecord>> tabFlowRecordListMap = financeFlowTabRecordService.list(Wrappers.<FinanceFlowTabRecord>lambdaQuery()
                        .in(FinanceFlowTabRecord::getMainId, tabMainInfoMap.keySet()))
                .stream().collect(Collectors.groupingBy(FinanceFlowTabRecord::getMainId));
        // 拿到当前tab页来款账户的所有tab页面的匹配信息映射
        Map<Long, List<FinanceFlowMatchResult>> financeFlowMatchResultsMap = financeFlowMatchResultService.list(Wrappers.<FinanceFlowMatchResult>lambdaQuery()
                        .in(FinanceFlowMatchResult::getMainId, tabMainInfoMap.keySet()))
                .stream().collect(Collectors.groupingBy(FinanceFlowMatchResult::getMainId));
        List<FlowMatchResultRSP> resList = new LinkedList<>();
        tabMainInfoMap.forEach((mainId, financeFlowTabMainInfo) -> {
            List<FinanceFlowTabRecord> financeFlowTabRecords = tabFlowRecordListMap.get(mainId);
            List<FinanceFlowMatchResult> financeFlowMatchResults = financeFlowMatchResultsMap.get(mainId);
            projectCollectWriteOffService.matchAmount(financeFlowTabRecords, financeFlowMatchResults);
            resList.add(buildFlowMatchResultRsp(tabMainInfoMap.get(mainId), financeFlowMatchResults, financeFlowTabRecords));
        });
        thisService.checkIsWriteOff(resList);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void addBusinessFlow(AddBusinessFlowREQ req) {
        log.info("project collect manual write off, add business flow, requestParam = {}", req);
        FinanceFlowTabMainInfo tabMainInfo = financeFlowTabMainInfoService.getById(req.getFinanceFlowTabMainInfoId());
        Assert.notNull(tabMainInfo, () -> MithrasException.newException(ResultMsg.RECORD_NOT_EXIST));
        // 找到实际的应收账单
        List<CollectionBaseInfo> collectionBaseInfos = collectionBaseInfoService.listByIds(req.getCollectionIds());
        log.info("collectionBaseInfos = {}", collectionBaseInfos);
        Assert.notEmpty(collectionBaseInfos, () -> MithrasException.newException("未找到对应的应收账单"));
        Map<Long, List<FinanceFlowMatchResult>> businessFlowList = projectCollectWriteOffService.domainFlowRecordGrouping(collectionBaseInfos);
        Assert.notEmpty(businessFlowList, () -> MithrasException.newException("租金往来方不存在，请联系管理员处理"));
        // 保存之后做一次匹配
        List<FinanceFlowMatchResult> matchResults = businessFlowList.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
        matchResults.forEach(o -> o.setMainId(tabMainInfo.getId()));
        financeFlowMatchResultService.saveBatch(matchResults);
        thisService.matchAmountAgain(tabMainInfo);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void deleteBusinessFlow(DeleteBusinessFlowREQ req) {
        log.info("project collect manual write off, delete business flow, requestParam = {}", req);
        FinanceFlowTabMainInfo tabMainInfo = financeFlowTabMainInfoService.getById(req.getFinanceFlowTabMainInfoId());
        Assert.notNull(tabMainInfo, () -> MithrasException.newException(ResultMsg.RECORD_NOT_EXIST));

        // 找到当前业务流水列表, 然后判断数据大小是否符合预期
        List<FinanceFlowMatchResult> financeFlowMatchResultList = financeFlowMatchResultService.list(Wrappers.<FinanceFlowMatchResult>lambdaQuery()
                .eq(FinanceFlowMatchResult::getMainId, tabMainInfo.getId())
                .in(FinanceFlowMatchResult::getId, req.getBusinessFlowIdList()));
        Assert.isTrue(CollUtil.isNotEmpty(financeFlowMatchResultList), () -> MithrasException.newException("非法操作，当前tab不存在欲删除的业务流水"));
        Assert.isTrue(financeFlowMatchResultList.size() == req.getBusinessFlowIdList().size(), () -> MithrasException.newException("非法操作，存在欲删除的业务流水不属于当前Tab，请检查"));

        financeFlowMatchResultService.remove(Wrappers.<FinanceFlowMatchResult>lambdaQuery()
                .eq(FinanceFlowMatchResult::getMainId, tabMainInfo.getId())
                .in(FinanceFlowMatchResult::getId, req.getBusinessFlowIdList()));

        thisService.reCheckIsPerfectMatch(tabMainInfo);

        // 重新匹配金额
        thisService.matchAmountAgain(tabMainInfo);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void reMatch(RematchTabREQ req) {
        log.info("project collect manual write off, rematch tab, requestParam = {}", req);
        FinanceFlowTabMainInfo tabMainInfo = financeFlowTabMainInfoService.getById(req.getFinanceFlowTabMainInfoId());
        Assert.notNull(tabMainInfo, () -> MithrasException.newException(ResultMsg.RECORD_NOT_EXIST));

        // 找到当前的所有银行流水
        List<FinanceFlowTabRecord> flowTabRecordList = financeFlowTabRecordService.list(Wrappers.<FinanceFlowTabRecord>lambdaQuery().eq(FinanceFlowTabRecord::getMainId, tabMainInfo.getId()));
        if (CollUtil.isEmpty(flowTabRecordList)) {
            throw MithrasException.newException("未找到任何银行流水，请检查数据！");
        }
        // 找到所有的合同编号
        List<FinanceFlowMatchResult> financeFlowMatchResults = financeFlowMatchResultService.getBaseMapper().selectWithLogicDelete(tabMainInfo.getId());
        if (CollUtil.isEmpty(financeFlowMatchResults)) {
            throw MithrasException.newException("未找到任何业务流水，请检查数据！");
        }
        // 根据合同查找应收计划
        List<String> contractCodeList = financeFlowMatchResults.stream().map(FinanceFlowMatchResult::getSourceBusinessCode).collect(Collectors.toList());
        List<CollectionBaseInfo> collectionBaseInfos = collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                // 注意：目前核销只取租金、提前终止补偿金、名义价款（留购金）、后续全部放开的话要把此条件去掉
                .or(i -> i.and(queryWrapper -> queryWrapper.gt(CollectionBaseInfo::getPhase, 0)
                                .in(CollectionBaseInfo::getContractCode, contractCodeList)
                                .le(CollectionBaseInfo::getPlanCollectionDate, LocalDateTimeUtil.now().plusDays(req.getPlusDays()))
                                .in(CollectionBaseInfo::getCashFlowItem, Collections.singletonList(CashFlowItemEnum.RENT.name()))
                                .isNotNull(CollectionBaseInfo::getCode))
                        .or(queryWrapper -> queryWrapper.in(CollectionBaseInfo::getContractCode, contractCodeList)
                                .le(CollectionBaseInfo::getPlanCollectionDate, LocalDateTimeUtil.now().plusDays(req.getPlusDays()))
                                .in(CollectionBaseInfo::getCashFlowItem, Arrays.asList(CashFlowItemEnum.EARLY_STOP_COMPENSATION.name(), CashFlowItemEnum.NOMINAL_PRICE.name()))
                                .isNotNull(CollectionBaseInfo::getCode))));

        // 过滤掉已经核销完毕的
        List<CollectionBaseInfo> noCollectBaseInfoList = Optional.ofNullable(collectionBaseInfos).map(
                // 租金没还完或者罚息没还完的都要返回
                collectionBaseInfoList -> collectionBaseInfoList.stream()
                        .filter(collectionBaseInfo -> LongUtil.null2zero(collectionBaseInfo.getPlanCollectionAmount()) - LongUtil.null2zero(collectionBaseInfo.getCollectionAmount()) > 0
                                || LongUtil.null2zero(collectionBaseInfo.getPenaltyInterest()) - LongUtil.null2zero(collectionBaseInfo.getCollectionPenaltyInterest()) > 0)
                        .collect(Collectors.toList())
        ).orElse(Collections.emptyList());

        log.info("[收款]获取到的未收款基本信息列表：【{}】\n[收款]获取到的未收款基本信息列表大小：【{}】", noCollectBaseInfoList, noCollectBaseInfoList.size());
        Map<Long, List<FinanceFlowMatchResult>> newMatchResultMap = projectCollectWriteOffService.domainFlowRecordGrouping(noCollectBaseInfoList);
        // 删除当前tab的所有业务流水
        financeFlowMatchResultService.remove(Wrappers.<FinanceFlowMatchResult>lambdaQuery().eq(FinanceFlowMatchResult::getMainId, tabMainInfo.getId()));
        // 重新添加
        List<FinanceFlowMatchResult> matchResultList = newMatchResultMap.values().stream().flatMap(Collection::stream)
                .peek(financeFlowMatchResult -> financeFlowMatchResult.setMainId(tabMainInfo.getId())).collect(Collectors.toList());
        financeFlowMatchResultService.saveBatch(matchResultList);
        projectCollectWriteOffService.matchAmount(flowTabRecordList, matchResultList);

        // 判断是否完美匹配
        Boolean perfectMatch = projectCollectWriteOffService.checkPerfectMatch(tabMainInfo.getId());
        tabMainInfo.setIsPerfectMatch(Boolean.TRUE.equals(perfectMatch) ? 1 : 0);
        financeFlowTabMainInfoService.updateById(tabMainInfo);

        thisService.matchAmountAgain(tabMainInfo);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void deleteTab(DeleteTabREQ req) {
        log.info("project collect manual write off, delete tab, requestParam = {}", req);
        FinanceFlowTabMainInfo tabMainInfo = financeFlowTabMainInfoService.getById(req.getFinanceFlowTabMainInfoId());
        Assert.notNull(tabMainInfo, () -> MithrasException.newException(ResultMsg.RECORD_NOT_EXIST));

        // 1、删除业务流水
        financeFlowMatchResultService.remove(Wrappers.<FinanceFlowMatchResult>lambdaQuery().eq(FinanceFlowMatchResult::getMainId, tabMainInfo.getId()));
        List<FinanceFlowTabRecord> financeFlowTabRecords = financeFlowTabRecordService.list(Wrappers.<FinanceFlowTabRecord>lambdaQuery().eq(FinanceFlowTabRecord::getMainId, tabMainInfo.getId()));
        // 2、删除银行流水
        financeFlowMatchResultService.remove(Wrappers.<FinanceFlowMatchResult>lambdaQuery().eq(FinanceFlowMatchResult::getMainId, tabMainInfo.getId()));
        financeFlowRecordService.lambdaUpdate()
                .set(FinanceFlowRecord::getShowInList, YesOrNoNumberEnum.YES.getCode())
                .in(FinanceFlowRecord::getId, financeFlowTabRecords.stream().map(FinanceFlowTabRecord::getFinanceFlowId).collect(Collectors.toList()))
                .update();
        // 3、删除Tab
        financeFlowTabMainInfoService.removeById(tabMainInfo.getId());
    }

    @Override
    public FlowMatchResultRSP singleTab(SingleTabREQ req) {
        log.info("project collect manual write off, single tab, requestParam = {}", req);
        // 找到当前tab
        FinanceFlowTabMainInfo tabMainInfo = financeFlowTabMainInfoService.getById(req.getFinanceFlowTabMainInfoId());
        Assert.notNull(tabMainInfo, () -> MithrasException.newException(ResultMsg.RECORD_NOT_EXIST));
        // 找到当前tab的银行流水
        List<FinanceFlowTabRecord> financeFlowTabRecordList = financeFlowTabRecordService.list(Wrappers.<FinanceFlowTabRecord>lambdaQuery().eq(FinanceFlowTabRecord::getMainId, tabMainInfo.getId()));
        // 找到当前tab的业务流水
        List<FinanceFlowMatchResult> financeFlowMatchResultList = financeFlowMatchResultService.list(Wrappers.<FinanceFlowMatchResult>lambdaQuery().eq(FinanceFlowMatchResult::getMainId, tabMainInfo.getId()));
        // 将账单重新排序
        financeFlowMatchResultList = projectCollectWriteOffService.sortBusinessFlowRecord(financeFlowMatchResultList);
        return buildFlowMatchResultRsp(tabMainInfo, financeFlowMatchResultList, financeFlowTabRecordList);
    }
}
