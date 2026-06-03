package cn.zswltech.mithras.service.service.monthly;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.monthly.*;
import cn.zswltech.mithras.dto.third.financial.ThirdFinancialWithdrawREQ;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.controller.third.FinancialController;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.capital.BankFlowCenterTypeEnum;
import cn.zswltech.mithras.collection.enums.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.contract.enums.contract.IncomeConfirmTypeEnum;
import cn.zswltech.mithras.monthly.enums.MonthlyManagementStatusEnum;
import cn.zswltech.mithras.monthly.enums.MonthlyModuleTypeEnum;
import cn.zswltech.mithras.monthly.enums.StampDutyTypeEnum;
import cn.zswltech.mithras.third.enums.ExceptionSourceENUM;
import cn.zswltech.mithras.third.enums.FinancialDevUrlENUM;
import cn.zswltech.mithras.collection.mapper.CollectionBaseInfoMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractIncomeSharing;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractReceipt;
import cn.zswltech.mithras.monthly.mapper.model.*;
import cn.zswltech.mithras.third.mapper.model.FinanceFlowRecord;
import cn.zswltech.mithras.monthly.mapper.FundsDailyCostMapper;
import cn.zswltech.mithras.monthly.mapper.MonthlyManagementBaseInfoMapper;
import cn.zswltech.mithras.monthly.mapper.MonthlyStampDutyMapper;
import cn.zswltech.mithras.monthly.mapper.MonthlyUserRecordMapper;
import cn.zswltech.mithras.monthly.service.MonthlyFinanceStampDutyService;
import cn.zswltech.mithras.monthly.service.MonthlyProjStampDutyService;
import cn.zswltech.mithras.third.mapper.FinanceFlowRecordMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.collection.CollectionBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractIncomeSharingService;
import cn.zswltech.mithras.service.service.contract.ContractReceiptService;
import cn.zswltech.mithras.service.util.StringUtil;
import cn.zswltech.mithras.service.util.ThreadPoolUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.hutool.extra.spring.SpringUtil.getBean;

/**
 * @author yangxiong
 * @description 针对表【monthly_management_base_info(月结管理主表)】的数据库操作Service实现
 * @createDate 2024-07-24 15:12:50
 */
@Slf4j
@Service
public class MonthlyManagementBaseInfoService extends ServiceImpl<MonthlyManagementBaseInfoMapper, MonthlyManagementBaseInfo> {

    private static final String BEGINNING_ITEM_TEXT = "期初余额";
    private static final String DIFF_ITEM_TEXT = "差额";
    @Resource
    private MonthlyManagementBaseInfoMapper monthlyManagementBaseInfoMapper;
    @Resource
    private ContractIncomeSharingService contractIncomeSharingService;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private FundsDailyCostMapper fundsDailyCostMapper;
    @Resource
    private MonthlyStampDutyMapper monthlyStampDutyMapper;
    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;
    @Resource
    private MonthlyManagementAirRecordService airRecordService;
    @Resource
    private MonthlyManagementCostRecordService costRecordService;
    @Resource
    private MonthlyManagementRpRecordService rpRecordService;
    @Resource
    private MonthlyFinanceStampDutyService financeStampDutyService;
    @Resource
    private MonthlyProjStampDutyService projStampDutyService;
    @Resource
    private MonthlyManagementBaseInfoService baseInfoService;
    @Resource
    private FinanceFlowRecordMapper financeFlowRecordMapper;
    @Resource
    private MonthlySendCqService monthlySendCqService;

    public PageR<MonthlyListRSP> listPage(MonthlyListREQ req) {
        Page<MonthlyManagementBaseInfo> page = new Page<>(req.getPage(), req.getPageSize());
        Page<MonthlyManagementBaseInfo> baseInfoPage = monthlyManagementBaseInfoMapper.selectPage(page, Wrappers.<MonthlyManagementBaseInfo>lambdaQuery().orderByDesc(MonthlyManagementBaseInfo::getYear).orderByDesc(MonthlyManagementBaseInfo::getMonth));
        if (CollectionUtils.isEmpty(baseInfoPage.getRecords())) {
            return PageR.empty(req.getPage(), req.getPageSize());
        }
        List<MonthlyListRSP> rspList = new ArrayList<>(baseInfoPage.getRecords().size());
        for (MonthlyManagementBaseInfo info : baseInfoPage.getRecords()) {
            MonthlyListRSP rsp = new MonthlyListRSP();
            BeanUtil.copyProperties(info, rsp);
            rsp.setYearAndMonth(String.format("%d-%02d", info.getYear(), info.getMonth()));
            rsp.setId(info.getMainId());
            rspList.add(rsp);
        }
        return PageR.of(rspList, baseInfoPage.getTotal());
    }

    /**
     * 添加月结管理主表
     *
     * @param mainId 主表id
     */
    @Transactional(rollbackFor = Throwable.class)
    public void fillMonthlyBaseInfo(Long mainId) {
        //校验一下这个月结主表信息是否存在
        MonthlyManagementBaseInfo monthlyManagementBaseInfo = baseInfoService.getOne(Wrappers.<MonthlyManagementBaseInfo>lambdaQuery()
                .eq(MonthlyManagementBaseInfo::getMainId, mainId)
                .last(StringUtil.mysqlLimitOne()));
        if (ObjectUtil.isNull(monthlyManagementBaseInfo)) {
            throw new MithrasException("月结主表信息不存在");
        }
        long rpCount = 0L;
        long rpCountExcludeTax = 0L;
        long airCount = 0L;
        long airCountExcludeTax = 0L;
        long stampDutyCount = 0L;
        long costCount = 0L;
        long costCountExcludeTax = 0L;

        // AIR 处理
        List<MonthlyManagementAirRecord> airRecordList = airRecordService.list(Wrappers.<MonthlyManagementAirRecord>lambdaQuery()
                .in(MonthlyManagementAirRecord::getMainId, mainId)
                .eq(MonthlyManagementAirRecord::getIsEffect, YesOrNoNumberEnum.YES.getCode())
                .eq(MonthlyManageBaseModel::getIsConfirmed, YesOrNoNumberEnum.YES.getCode())
                .eq(MonthlyManagementAirRecord::getIsSendCq, YesOrNoNumberEnum.YES.getCode()));
        if (!CollectionUtils.isEmpty(airRecordList)) {
            for (MonthlyManagementAirRecord airRecord : airRecordList) {
                airCount += airRecord.getIncomeSum();
                airCountExcludeTax += airRecord.getIncomeWithoutTaxSum();
            }
        }
        // rp处理
        List<MonthlyManagementRpRecord> rpRecordList = rpRecordService.list(Wrappers.<MonthlyManagementRpRecord>lambdaQuery()
                .in(MonthlyManagementRpRecord::getMainId, mainId)
                .eq(MonthlyManagementRpRecord::getIsEffect, YesOrNoNumberEnum.YES.getCode())
                .eq(MonthlyManagementRpRecord::getIsConfirmed, YesOrNoNumberEnum.YES.getCode())
                .eq(MonthlyManagementRpRecord::getIsSendCq, YesOrNoNumberEnum.YES.getCode()));
        if (!CollectionUtils.isEmpty(rpRecordList)) {
            for (MonthlyManagementRpRecord rpRecord : rpRecordList) {
                rpCount += rpRecord.getIncomeSum();
                rpCountExcludeTax += rpRecord.getIncomeWithoutTaxSum();
            }
        }

        // 成本计提处理
        List<MonthlyManagementCostRecord> costRecordList = costRecordService.list(Wrappers.<MonthlyManagementCostRecord>lambdaQuery()
                .in(MonthlyManagementCostRecord::getMainId, mainId)
                .eq(MonthlyManagementCostRecord::getIsEffect, YesOrNoNumberEnum.YES.getCode())
                .eq(MonthlyManagementCostRecord::getIsConfirmed, YesOrNoNumberEnum.YES.getCode())
                .eq(MonthlyManagementCostRecord::getIsSendCq, YesOrNoNumberEnum.YES.getCode()));

        if (!CollectionUtils.isEmpty(costRecordList)) {
            for (MonthlyManagementCostRecord costRecord : costRecordList) {
                costCount += costRecord.getTotalCapitalCost();
                costCountExcludeTax += costRecord.getTotalCapitalCostAfterTax();
            }
        }

        // 印花税处理
        LocalDate beginDate = LocalDate.of(monthlyManagementBaseInfo.getYear(), monthlyManagementBaseInfo.getMonth(), 1);
        LocalDate closeDate = beginDate.with(TemporalAdjusters.lastDayOfMonth());
        List<MonthlyStampDuty> list = monthlyStampDutyMapper.selectList(Wrappers.<MonthlyStampDuty>lambdaQuery()
                .eq(MonthlyStampDuty::getDeleted, false)
                .eq(MonthlyStampDuty::getIsConfirmed, true)
                .lt(MonthlyStampDuty::getDate, beginDate)
                .ge(MonthlyStampDuty::getDate, closeDate)
                .orderByDesc(MonthlyStampDuty::getConfirmTime));
        if (!CollectionUtils.isEmpty(list)) {
            stampDutyCount = list.stream().mapToLong(MonthlyStampDuty::getStampDuty).sum();
        }

        MonthlyManagementBaseInfo baseInfo = new MonthlyManagementBaseInfo();
        BeanUtil.copyProperties(monthlyManagementBaseInfo, baseInfo);
        baseInfo.setAirCount(airCount)
                .setAirCountExcludeTax(airCountExcludeTax)
                .setRpCount(rpCount)
                .setRpCountExcludeTax(rpCountExcludeTax)
                .setStampDutyCount(stampDutyCount)
                .setCostCount(costCount)
                .setCostCountExcludeTax(costCountExcludeTax)
                .setConfirmDate(LocalDate.now());
        //保留版本，借用逻辑删除
        monthlyManagementBaseInfoMapper.deleteById(monthlyManagementBaseInfo.getId());
        baseInfo.setStatus(MonthlyManagementStatusEnum.CONFIRMED.name());
        baseInfo.setCloseDate(monthlyManagementBaseInfo.getCloseDate());
        baseInfo.setMainId(monthlyManagementBaseInfo.getMainId());
        monthlyManagementBaseInfoMapper.insert(baseInfo);
    }

    public Set<Long> listWriteOffAdvancedRPReceiptIds(LocalDate queryStartDate) {
        List<ContractBaseInfo> contractBaseInfoList = contractBaseInfoMapper.selectList(Wrappers.<ContractBaseInfo>lambdaQuery()
                .in(ContractBaseInfo::getContractStatus, Arrays.asList(ContractStatus.START_RENT.name(), ContractStatus.SETTLE.name()))
                .eq(ContractBaseInfo::getIncomeConfirmType, IncomeConfirmTypeEnum.RP.name()));
        if (CollectionUtil.isEmpty(contractBaseInfoList)) {
            return Collections.emptySet();
        }
        // 查询目标日期及之后的核销完毕的租金
        LambdaQueryWrapper<CollectionBaseInfo> query = Wrappers.lambdaQuery();
        query.in(CollectionBaseInfo::getContractId, contractBaseInfoList.stream().map(ContractBaseInfo::getId).collect(Collectors.toList()));
        query.ge(CollectionBaseInfo::getPlanCollectionDate, queryStartDate);
        query.eq(CollectionBaseInfo::getWriteOffStatus, CollectionWriteOffStatusEnum.WRITE_OFF_COMPLETED.name());
        query.eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name());
        query.gt(CollectionBaseInfo::getPhase, 0);
        List<CollectionBaseInfo> list = SpringUtil.getBean(CollectionBaseInfoService.class).list(query);
        if (CollectionUtil.isEmpty(list)) {
            return Collections.emptySet();
        }
        return list.stream().map(CollectionBaseInfo::getReceiptId).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    /**
     * 获取合同
     *
     * @param incomeConfirmTypeEnum
     * @param month
     * @param overdue               true:获取逾期的合同，false:获取不逾期的合同，null:我全都要
     * @return
     */
    public Map<Long, ContractBaseInfo> getContract(IncomeConfirmTypeEnum incomeConfirmTypeEnum, LocalDate month, Boolean overdue) {
        Map<Long, ContractBaseInfo> result = new HashMap<>();
        List<ContractBaseInfo> list = contractBaseInfoMapper.selectList(Wrappers.<ContractBaseInfo>lambdaQuery()
                .in(ContractBaseInfo::getContractStatus, Arrays.asList(ContractStatus.START_RENT.name(), ContractStatus.SETTLE.name()))
                .eq(ContractBaseInfo::getIncomeConfirmType, incomeConfirmTypeEnum.name()));
        if (CollUtil.isNotEmpty(list)) {
            if (Objects.nonNull(overdue)) {
                List<Long> contractIdList = list.stream().map(ContractBaseInfo::getId).collect(Collectors.toList());
                List<Long> contractIdOverdue = baseInfoService.contractIsOverdue(contractIdList, month);
                list = list.stream().filter(contract -> overdue.equals(contractIdOverdue.contains(contract.getId()))).collect(Collectors.toList());
            }
            result = list.stream().collect(Collectors.toMap(ContractBaseInfo::getId, Function.identity()));
        }
        return result;
    }

    public List<Long> contractIsOverdue(List<Long> contractIdList, LocalDate targetDate) {
        List<CollectionBaseInfo> collectionBaseInfoList = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .in(CollUtil.isNotEmpty(contractIdList), CollectionBaseInfo::getContractId, contractIdList)
                .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                .gt(CollectionBaseInfo::getPhase, 0)
                .lt(CollectionBaseInfo::getPlanCollectionDate, targetDate));
        Map<Long, List<CollectionBaseInfo>> collectionMap = collectionBaseInfoList.stream().collect(Collectors.groupingBy(CollectionBaseInfo::getContractId));
        List<Long> contractIdResult = new ArrayList<>();
        if (!CollectionUtils.isEmpty(collectionBaseInfoList)) {
            // 逾期合同数量
            for (Map.Entry<Long, List<CollectionBaseInfo>> entry : collectionMap.entrySet()) {
                List<CollectionBaseInfo> collectionBaseInfos = collectionMap.get(entry.getKey());
                long overdueCount = collectionBaseInfos.stream().filter(e -> {
                    long planRent = Optional.ofNullable(e.getPlanCollectionAmount()).orElse(0L);
                    long actualRent = Optional.ofNullable(e.getCollectionAmount()).orElse(0L);
                    return planRent > actualRent;
                }).count();
                if (overdueCount > 0) {
                    // 返回逾期的合同
                    contractIdResult.add(entry.getKey());
                }
            }
        }
        return contractIdResult;
    }

    public LocalDate handleDate(String yearAndMonth) {
        try {
            yearAndMonth = yearAndMonth + "-01";
            return LocalDateTimeUtil.parseDate(yearAndMonth, DatePattern.NORM_DATE_PATTERN);
        } catch (Exception e) {
            throw new MithrasException("无法识别的时间格式");
        }
    }

    public MonthlyManagementBaseInfo getMonthlyManagementBaseInfo(String yearAndMonth) {
        if (CharSequenceUtil.isBlank(yearAndMonth)) {
            throw new MithrasException("日期不能为空");
        }
        yearAndMonth = yearAndMonth + "-01";
        LocalDate localDate = LocalDateTimeUtil.parseDate(yearAndMonth, DatePattern.NORM_DATE_PATTERN);
        //找到当前的主信息
        return this.getOne(Wrappers.<MonthlyManagementBaseInfo>lambdaQuery()
                .eq(MonthlyManagementBaseInfo::getYear, localDate.getYear())
                .eq(MonthlyManagementBaseInfo::getMonth, localDate.getMonthValue()));
    }

    /**
     * 新增月结管理基本信息
     *
     * @param req
     */
    public Long addBaseInfo(MonthlyAddREQ req) {
        //校验通过，查看是否存在已有的月结信息
        MonthlyManagementBaseInfo monthlyManagementBaseInfo = this.getMonthlyManagementBaseInfo(req.getYearAndMonth());
        if (Objects.nonNull(monthlyManagementBaseInfo)) {
            throw new MithrasException("该月结信息已存在");
        }
        //不直接抛异常是因为这样交互方式可以不用变
        MonthlyManagementBaseInfo baseInfo = new MonthlyManagementBaseInfo();
        LocalDate localDate = LocalDateTimeUtil.parseDate(req.getYearAndMonth(), DatePattern.NORM_MONTH_PATTERN);
        baseInfo.setYear(localDate.getYear());
        baseInfo.setMonth(localDate.getMonth().getValue());
        baseInfo.setStatus(MonthlyManagementStatusEnum.NOT_CONFIRM.name());
        baseInfo.setMainId(System.currentTimeMillis());
        monthlyManagementBaseInfoMapper.insert(baseInfo);

        //异步刷新四个TAB的数据 只有主数据不存在的时候才主动刷新
        asyncFreshTabData(baseInfo);
        return baseInfo.getMainId();
    }

    /**
     * 异步刷新数据，单一职责，时机交给调用方，不处理业务逻辑
     *
     * @param baseInfo 月结管理基础信息
     */
    public void asyncFreshTabData(MonthlyManagementBaseInfo baseInfo) {
        MonthlyFreshREQ freshReq = new MonthlyFreshREQ();
        freshReq.setYearAndMonth(String.format("%d-%02d", baseInfo.getYear(), baseInfo.getMonth()));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DatePattern.CHINESE_DATE_TIME_PATTERN);
        CountDownLatch countDownLatch = new CountDownLatch(5);
        // 刷新air
        CompletableFuture.supplyAsync(() -> {
            try {
                StopWatch stopWatch = new StopWatch();
                stopWatch.start("freshMonthlyAirRecord");
                airRecordService.freshMonthlyAirRecord(freshReq);
                stopWatch.stop();
                log.info("异步刷新AIR数据耗时：{}", stopWatch.prettyPrint(TimeUnit.SECONDS));
                countDownLatch.countDown();
                return null;
            } catch (Exception e) {
                countDownLatch.countDown();
                log.error("{}刷新AIR月结数据失败，请关注～～～", LocalDateTime.now().format(formatter), e);
                return null;
            }
        });

        // 刷新rp
        CompletableFuture.supplyAsync(() -> {
            try {
                StopWatch stopWatch = new StopWatch();
                stopWatch.start("freshRpList");
                rpRecordService.freshRpList(freshReq);
                stopWatch.stop();
                log.info("异步刷新RP数据耗时：{}", stopWatch.prettyPrint(TimeUnit.SECONDS));
                countDownLatch.countDown();
                return null;
            } catch (Exception e) {
                countDownLatch.countDown();
                log.error("{}刷新RP月结数据失败，请关注～～～", LocalDateTime.now().format(formatter), e);
                return null;
            }
        });

        // 刷新cost
        CompletableFuture.supplyAsync(() -> {
            try {
                StopWatch stopWatch = new StopWatch();
                stopWatch.start("freshCostList");
                costRecordService.freshCostList(freshReq);
                stopWatch.stop();
                log.info("异步刷新成本数据耗时：{}", stopWatch.prettyPrint(TimeUnit.SECONDS));
                countDownLatch.countDown();
                return null;
            } catch (Exception e) {
                countDownLatch.countDown();
                log.error("{}刷新COST月结数据失败，请关注～～～", LocalDateTime.now().format(formatter), e);
                return null;
            }
        });

        // 刷新资金端的印花税
        CompletableFuture.supplyAsync(() -> {
            try {
                StopWatch stopWatch = new StopWatch();
                stopWatch.start("freshFinanceStampDuty");
                financeStampDutyService.freshFinanceStampDuty(freshReq);
                stopWatch.stop();
                log.info("异步刷新资金端的印花税耗时：{}", stopWatch.prettyPrint(TimeUnit.SECONDS));
                countDownLatch.countDown();
                return null;
            } catch (Exception e) {
                countDownLatch.countDown();
                log.error("{}刷新资金端的印花税数据失败，请关注～～～", LocalDateTime.now().format(formatter), e);
                return null;
            }
        });

        // 刷新项目端的印花税
        CompletableFuture.supplyAsync(() -> {
            try {
                StopWatch stopWatch = new StopWatch();
                stopWatch.start("freshProjStampDuty");
                projStampDutyService.freshProjStampDuty(freshReq);
                stopWatch.stop();
                log.info("异步刷新项目端印花税耗时：{}", stopWatch.prettyPrint(TimeUnit.SECONDS));
                countDownLatch.countDown();
                return null;
            } catch (Exception e) {
                countDownLatch.countDown();
                log.error("{}刷新项目端的印花税数据失败，请关注～～～", LocalDateTime.now().format(formatter), e);
                return null;
            }
        });
        try {
            boolean ok = countDownLatch.await(10, TimeUnit.SECONDS);
            if (!ok) {
                log.error("{}异步刷新数据失败，请关注～～～", LocalDateTime.now().format(formatter));
            }
        } catch (InterruptedException e) {
            log.error("{}", e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
    }


//    public void verification(MonthlyAddREQ req) {
//        LocalDate localDate = LocalDateTimeUtil.parseDate(req.getYearAndMonth(), DatePattern.NORM_MONTH_PATTERN);
//        LocalDate interestStartDate = LocalDate.of(localDate.getYear(), localDate.getMonthValue(), 1);
//        LocalDate interestEndDate = LocalDate.of(localDate.getYear(), localDate.getMonthValue(), localDate.lengthOfMonth());
//        //期初日期
//        List<FundsDailyCost> beginDayList = fundsDailyCostMapper.selectList(Wrappers.<FundsDailyCost>lambdaQuery()
//                .eq(FundsDailyCost::getItemText, BEGINNING_ITEM_TEXT)
//                .gt(FundsDailyCost::getFinancingAmount, 0));
//        boolean findCalDate = false;
//        Map<String, LocalDate> localDateMap = getLastCostDay(beginDayList);
//        for (FundsDailyCost beginDailyCost : beginDayList) {
//            Long financingId = beginDailyCost.getFinancingId();
//            LocalDate lastCostDate = localDateMap.get(financingId + "_" + beginDailyCost.getType());
//            //期初和查询月份在一个月，并且无计算利息
//            if (lastCostDate == null && beginDailyCost.getInterestDate().plusDays(1).equals(interestStartDate)
//                    && beginDailyCost.getInterestDate().isBefore(interestEndDate)) {
//                findCalDate = true;
//            }
//            //期初在日期中间，并且无计算利息
//            if (lastCostDate == null && !beginDailyCost.getInterestDate().isBefore(interestStartDate)
//                    && beginDailyCost.getInterestDate().isBefore(interestEndDate)) {
//                findCalDate = true;
//            }
//
//            //已有计算利息
//            if (lastCostDate != null && interestStartDate.equals(lastCostDate.plusDays(1))) {
//                findCalDate = true;
//            }
//            if (localDate.getMonthValue() >= beginDailyCost.getInterestDate().getMonthValue()
//                    && lastCostDate != null && lastCostDate.getMonthValue() >= localDate.getMonthValue()) {
//                findCalDate = true;
//            }
//            if (findCalDate) {
//                throw new MithrasException(String.format("%d年%d月无成本计提日期", localDate.getYear(), localDate.getMonthValue()));
//            }
//        }
//    }

    private Map<String, LocalDate> getLastCostDay(List<FundsDailyCost> beginDayList) {
        if (beginDayList == null || beginDayList.isEmpty()) {
            return Collections.emptyMap();
        }
        //牺牲空间，换时间
        List<FundsDailyCost> dailyCostList = fundsDailyCostMapper.selectList(Wrappers.<FundsDailyCost>lambdaQuery()
                .eq(FundsDailyCost::getFinancingId, beginDayList.stream().map(FundsDailyCost::getFinancingId).collect(Collectors.toList()))
                .gt(FundsDailyCost::getFinancingAmount, 0)
                .ne(FundsDailyCost::getItemText, BEGINNING_ITEM_TEXT)
                .ne(FundsDailyCost::getItemText, DIFF_ITEM_TEXT));
        Map<String, LocalDate> result = new HashMap<>();
        if (dailyCostList != null && !dailyCostList.isEmpty()) {
            dailyCostList.stream().collect(Collectors.groupingBy(obj -> obj.getFinancingId() + "_" + obj.getType()))
                    .forEach((k, v) -> {
                        List<FundsDailyCost> res = v.stream().sorted(Comparator.comparing(FundsDailyCost::getInterestDate)
                                .reversed()).collect(Collectors.toList());
                        result.put(k, res.get(0) != null ? res.get(0).getInterestDate() : null);
                    });
        }
        return result;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void submit(MonthlySubmitREQ req) {
        MonthlyManagementBaseInfo baseInfo = this.getMonthlyManagementBaseInfo(req.getYearAndMonth());
        if (Objects.isNull(baseInfo)) {
            throw new MithrasException("该月份数据不存在");
        }
        // 本次提交的批次号
        String batchNumber = LocalDateTimeUtil.format(LocalDateTime.now(), DatePattern.PURE_DATETIME_PATTERN);
        // 收入分摊表的数据来源是实际利率法和收入计提
        List<Long> contractIncomeSharingList = new ArrayList<>();
        List<MonthlyManagementAirRecord> airRecordList = airRecordService.list(Wrappers.<MonthlyManagementAirRecord>lambdaQuery()
                .eq(MonthlyManageBaseModel::getIsEffect, YesOrNoNumberEnum.YES.getCode())
                .eq(MonthlyManagementAirRecord::getMainId, baseInfo.getMainId())
                .eq(MonthlyManageBaseModel::getIsSendCq, YesOrNoNumberEnum.NO.getCode())
                .eq(MonthlyManageBaseModel::getIsConfirmed, YesOrNoNumberEnum.NO.getCode())
                .isNull(MonthlyManageBaseModel::getBatchNumber));
        if (CollUtil.isNotEmpty(airRecordList)) {
            contractIncomeSharingList.addAll(airRecordList.stream().map(MonthlyManagementAirRecord::getSourceId).collect(Collectors.toList()));
            airRecordService.lambdaUpdate()
                    .set(MonthlyManageBaseModel::getBatchNumber, batchNumber)
                    .eq(MonthlyManageBaseModel::getMainId, baseInfo.getMainId())
                    .in(MonthlyManagementAirRecord::getReceiptId, airRecordList.stream().map(MonthlyManagementAirRecord::getReceiptId).collect(Collectors.toList()))
                    .update();
        }
        List<MonthlyManagementRpRecord> rpRecordList = rpRecordService.list(Wrappers.<MonthlyManagementRpRecord>lambdaQuery()
                .eq(MonthlyManageBaseModel::getIsEffect, YesOrNoNumberEnum.YES.getCode())
                .eq(MonthlyManageBaseModel::getMainId, baseInfo.getMainId())
                .eq(MonthlyManageBaseModel::getIsSendCq, YesOrNoNumberEnum.NO.getCode())
                .eq(MonthlyManageBaseModel::getIsConfirmed, YesOrNoNumberEnum.NO.getCode())
                .isNull(MonthlyManageBaseModel::getBatchNumber));
        if (CollUtil.isNotEmpty(rpRecordList)) {
            contractIncomeSharingList.addAll(rpRecordList.stream().map(MonthlyManagementRpRecord::getSourceId).collect(Collectors.toList()));
            rpRecordService.lambdaUpdate()
                    .set(MonthlyManageBaseModel::getBatchNumber, batchNumber)
                    .eq(MonthlyManageBaseModel::getMainId, baseInfo.getMainId())
                    .in(MonthlyManagementRpRecord::getReceiptId, rpRecordList.stream().map(MonthlyManagementRpRecord::getReceiptId).collect(Collectors.toList()))
                    .update();
        }

        List<Long> monthlyStampDutyList = new ArrayList<>();
        List<MonthlyProjStampDuty> projStampDutyList = projStampDutyService.list(Wrappers.<MonthlyProjStampDuty>lambdaQuery()
                .eq(MonthlyManageBaseModel::getIsEffect, YesOrNoNumberEnum.YES.getCode())
                .eq(MonthlyProjStampDuty::getMainId, baseInfo.getMainId())
                .eq(MonthlyManageBaseModel::getIsSendCq, YesOrNoNumberEnum.NO.getCode())
                .eq(MonthlyManageBaseModel::getIsConfirmed, YesOrNoNumberEnum.NO.getCode())
                .isNull(MonthlyManageBaseModel::getBatchNumber));
        if (CollUtil.isNotEmpty(projStampDutyList)) {
            monthlyStampDutyList.addAll(projStampDutyList.stream().map(MonthlyProjStampDuty::getSourceId).collect(Collectors.toList()));
            projStampDutyService.lambdaUpdate()
                    .set(MonthlyManageBaseModel::getBatchNumber, batchNumber)
                    .eq(MonthlyManageBaseModel::getMainId, baseInfo.getMainId())
                    .in(MonthlyProjStampDuty::getSourceId, projStampDutyList.stream().map(MonthlyProjStampDuty::getSourceId).collect(Collectors.toList()))
                    .update();
        }

        List<MonthlyFinanceStampDuty> financeStampDutyList = financeStampDutyService.list(Wrappers.<MonthlyFinanceStampDuty>lambdaQuery()
                .eq(MonthlyManageBaseModel::getIsEffect, YesOrNoNumberEnum.YES.getCode())
                .eq(MonthlyFinanceStampDuty::getMainId, baseInfo.getMainId())
                .eq(MonthlyManageBaseModel::getIsSendCq, YesOrNoNumberEnum.NO.getCode())
                .eq(MonthlyManageBaseModel::getIsConfirmed, YesOrNoNumberEnum.NO.getCode())
                .isNull(MonthlyManageBaseModel::getBatchNumber));
        if (CollUtil.isNotEmpty(financeStampDutyList)) {
            monthlyStampDutyList.addAll(financeStampDutyList.stream().map(MonthlyFinanceStampDuty::getSourceId).collect(Collectors.toList()));
            financeStampDutyService.lambdaUpdate()
                    .set(MonthlyManageBaseModel::getBatchNumber, batchNumber)
                    .eq(MonthlyManageBaseModel::getMainId, baseInfo.getMainId())
                    .in(MonthlyFinanceStampDuty::getSourceId, financeStampDutyList.stream().map(MonthlyFinanceStampDuty::getSourceId).collect(Collectors.toList()))
                    .update();
        }

        List<Long> fundsDailyCostList = new ArrayList<>();
        List<MonthlyManagementCostRecord> costRecords = costRecordService.list(Wrappers.<MonthlyManagementCostRecord>lambdaQuery()
                .eq(MonthlyManageBaseModel::getIsEffect, YesOrNoNumberEnum.YES.getCode())
                .eq(MonthlyManagementCostRecord::getMainId, baseInfo.getMainId())
                .eq(MonthlyManageBaseModel::getIsSendCq, YesOrNoNumberEnum.NO.getCode())
                .eq(MonthlyManageBaseModel::getIsConfirmed, YesOrNoNumberEnum.NO.getCode())
                .isNull(MonthlyManageBaseModel::getBatchNumber));
        if (CollUtil.isNotEmpty(costRecords)) {
            fundsDailyCostList.addAll(costRecords.stream().map(MonthlyManagementCostRecord::getSourceId).collect(Collectors.toList()));
            costRecordService.lambdaUpdate()
                    .set(MonthlyManageBaseModel::getBatchNumber, batchNumber)
                    .eq(MonthlyManageBaseModel::getMainId, baseInfo.getMainId())
                    .in(MonthlyManagementCostRecord::getSourceId, costRecords.stream().map(MonthlyManagementCostRecord::getSourceId).collect(Collectors.toList()))
                    .update();
        }

        LocalDate currentTime = LocalDateTimeUtil.parseDate(req.getYearAndMonth(), DatePattern.NORM_MONTH_PATTERN);
        LocalDate beginDay = currentTime.withDayOfMonth(1);
        LocalDate endDay = currentTime.with(TemporalAdjusters.lastDayOfMonth());
        Set<Long> receiptIds = new HashSet<>();
        // 剩余本金法提交后需要将逾期合同的金额设置为0
        Map<Long, ContractBaseInfo> contract = baseInfoService.getContract(IncomeConfirmTypeEnum.RP, endDay, true);
        if (CollectionUtil.isNotEmpty(contract)) {
            List<ContractReceipt> contractReceiptList = SpringUtil.getBean(ContractReceiptService.class).list(Wrappers.<ContractReceipt>lambdaQuery().in(ContractReceipt::getContractId, contract.keySet()));
            receiptIds.addAll(contractReceiptList.stream().map(ContractReceipt::getId).collect(Collectors.toSet()));
        }
        // 剩余本金法提交后需要将存在超前核销完毕的金额置为0
        Set<Long> writeOffAdvancedIds = this.listWriteOffAdvancedRPReceiptIds(endDay.plusDays(1));
        if (CollectionUtil.isNotEmpty(writeOffAdvancedIds)) {
            receiptIds.addAll(writeOffAdvancedIds);
        }
        if (CollectionUtil.isNotEmpty(receiptIds)) {
            List<ContractIncomeSharing> incomeZeroList = contractIncomeSharingService.list(Wrappers.<ContractIncomeSharing>lambdaQuery()
                    .eq(ContractIncomeSharing::getIsConfirmed, false)
                    .ge(ContractIncomeSharing::getIncomeDate, beginDay)
                    .le(ContractIncomeSharing::getIncomeDate, endDay)
                    .in(ContractIncomeSharing::getReceiptId, receiptIds));
            if (CollUtil.isNotEmpty(incomeZeroList)) {
                // fix 这离需要更新中间表的数据
                List<MonthlyManagementAirRecord> airNeedRemoveList = airRecordService.list(Wrappers.<MonthlyManagementAirRecord>lambdaQuery()
                        .in(MonthlyManagementAirRecord::getReceiptId, incomeZeroList.stream().map(ContractIncomeSharing::getReceiptId).collect(Collectors.toList())));
                List<MonthlyManagementRpRecord> rpNeedRemoveList = rpRecordService.list(Wrappers.<MonthlyManagementRpRecord>lambdaQuery()
                        .in(MonthlyManagementRpRecord::getReceiptId, incomeZeroList.stream().map(ContractIncomeSharing::getReceiptId).collect(Collectors.toList())));

                incomeZeroList.forEach(income -> {
                    income.setIncome(0L);
                    income.setIncomeWithoutTax(0L);
                    income.setTax(0L);
                });
                if (CollUtil.isNotEmpty(airNeedRemoveList)) {
                    airRecordService.removeByIds(airNeedRemoveList.stream().map(MonthlyManageBaseModel::getId).collect(Collectors.toList()));
                }
                if (CollUtil.isNotEmpty(rpNeedRemoveList)) {
                    rpRecordService.removeByIds(rpNeedRemoveList.stream().map(MonthlyManageBaseModel::getId).collect(Collectors.toList()));
                }
                contractIncomeSharingService.updateBatchById(incomeZeroList);
            }
        }
        // 更新原始数据
        baseInfoService.updateSourceData(req, contractIncomeSharingList, beginDay, endDay, batchNumber, monthlyStampDutyList, fundsDailyCostList);
        try {
            //前端需要刷新数据这里只能使用同步
            monthlySendCqService.accountApplication(req.getYearAndMonth(), batchNumber);
        } catch (Exception e) {
            log.error("月结同步苍穹错误 {}, {}", req.getYearAndMonth(), batchNumber, e);
        }
    }

    public void updateSourceData(MonthlySubmitREQ req, List<Long> contractIncomeSharingList, LocalDate
            beginDay, LocalDate endDay, String batchNumber, List<Long> monthlyStampDutyList, List<Long> fundsDailyCostList) {
        if (CollUtil.isNotEmpty(contractIncomeSharingList)) {
            contractIncomeSharingService.update(Wrappers.<ContractIncomeSharing>lambdaUpdate()
                    .in(ContractIncomeSharing::getReceiptId, contractIncomeSharingList)
//                    .ge(ContractIncomeSharing::getIncomeDate, beginDay)
                    .le(ContractIncomeSharing::getIncomeDate, endDay)
                    .isNull(ContractIncomeSharing::getConfirmBatch)
                    .eq(ContractIncomeSharing::getIsConfirmed, false)
                    .set(ContractIncomeSharing::getConfirmBatch, batchNumber)
                    .set(ContractIncomeSharing::getConfirmTime, LocalDateTime.now()));
        }
        if (CollUtil.isNotEmpty(monthlyStampDutyList)) {
            List<MonthlyStampDutyFinRSP> finList = financeStampDutyService.finPage(new MonthlyStampDutyFinREQ(req.getYearAndMonth(), true)).getList();
            if (CollUtil.isNotEmpty(finList)) {
                List<Long> collect = finList.stream().map(MonthlyStampDutyFinRSP::getId).collect(Collectors.toList());
                monthlyStampDutyList.addAll(collect);
            }
            monthlyStampDutyMapper.update(null, Wrappers.<MonthlyStampDuty>lambdaUpdate()
                    .in(MonthlyStampDuty::getId, monthlyStampDutyList)
                    .isNull(MonthlyStampDuty::getConfirmBatch)
                    .eq(MonthlyStampDuty::getIsConfirmed, false)
                    .set(MonthlyStampDuty::getConfirmBatch, batchNumber)
                    .set(MonthlyStampDuty::getConfirmTime, LocalDateTime.now()));
        }
        if (CollUtil.isNotEmpty(fundsDailyCostList)) {
            fundsDailyCostMapper.update(null, Wrappers.<FundsDailyCost>lambdaUpdate()
                    .in(FundsDailyCost::getMainId, fundsDailyCostList)
                    .isNull(FundsDailyCost::getConfirmBatch)
                    .eq(FundsDailyCost::getIsConfirmed, false)
                    .set(FundsDailyCost::getConfirmBatch, batchNumber)
                    .set(FundsDailyCost::getConfirmTime, LocalDateTime.now()));
        }
    }


    public PageR<MonthlyStampDutyProjRSP> projPage(MonthlyStampDutyProjREQ req) {
        MonthlyManagementBaseInfo baseInfo = getMonthlyManagementBaseInfo(req.getYearAndMonth());
        if (Objects.isNull(baseInfo)) {
            throw new MithrasException("月结管理" + ResultMsg.RECORD_NOT_EXIST);
        }
        LocalDate startDate = LocalDate.of(baseInfo.getYear(), baseInfo.getMonth(), 1);
        LocalDate endDate = startDate.plusMonths(1).atStartOfDay().minusSeconds(1).toLocalDate();

        Page<MonthlyStampDuty> page = new Page<>(req.getPage(), req.getPageSize());
        List<MonthlyStampDuty> monthlyStampDutyList = monthlyStampDutyMapper.selectPage(page, Wrappers.<MonthlyStampDuty>lambdaQuery()
                        .eq(MonthlyStampDuty::getIsConfirmed, ObjectUtil.isNotEmpty(req.getIsConfirmed()) ? req.getIsConfirmed() : YesOrNoNumberEnum.NO.getCode())
                        .eq(MonthlyStampDuty::getDeleted, false)
                        .in(MonthlyStampDuty::getType, StampDutyTypeEnum.PROJ.name())
                        .ge(MonthlyStampDuty::getDate, startDate)
                        .lt(MonthlyStampDuty::getDate, endDate)
                        .eq(req.getBatchNumber() != null, MonthlyStampDuty::getConfirmBatch, req.getBatchNumber()))
                .getRecords();

        List<MonthlyStampDutyProjRSP> result = new ArrayList<>();
        if (CollUtil.isNotEmpty(monthlyStampDutyList)) {
            result = BeanUtil.copyToList(monthlyStampDutyList, MonthlyStampDutyProjRSP.class);
        }
        result.forEach(rsp -> rsp.setYearAndMonth(String.format("%d-%02d", baseInfo.getYear(), baseInfo.getMonth())));
        return PageR.of(result, page.getTotal());
    }

    @Transactional(rollbackFor = Throwable.class)
    public void updateMiddleTable(String yearAndMonth, String batchNumber) {
        MonthlyManagementBaseInfo baseInfo = getMonthlyManagementBaseInfo(yearAndMonth);
        if (Objects.isNull(baseInfo)) {
            throw new MithrasException("月结管理" + ResultMsg.RECORD_NOT_EXIST);
        }

        // AIR
        Long mainId = baseInfo.getMainId();
        airRecordService.lambdaUpdate()
                .set(MonthlyManageBaseModel::getBatchNumber, batchNumber)
                .set(MonthlyManageBaseModel::getIsConfirmed, YesOrNoNumberEnum.YES.getCode())
                .eq(MonthlyManageBaseModel::getIsEffect, YesOrNoNumberEnum.YES.getCode())
                .set(MonthlyManageBaseModel::getIsSendCq, YesOrNoNumberEnum.YES.getCode())
                .eq(MonthlyManageBaseModel::getMainId, mainId)
                .eq(MonthlyManageBaseModel::getBatchNumber, batchNumber)
                .update();
        airRecordService.lambdaUpdate()
                .set(MonthlyManageBaseModel::getBatchNumber, batchNumber)
                .set(MonthlyManageBaseModel::getIsConfirmed, YesOrNoNumberEnum.YES.getCode())
                .set(MonthlyManageBaseModel::getIsSendCq, YesOrNoNumberEnum.NO.getCode())
                .eq(MonthlyManageBaseModel::getIsEffect, YesOrNoNumberEnum.NO.getCode())
                .eq(MonthlyManageBaseModel::getMainId, mainId)
                .eq(MonthlyManageBaseModel::getBatchNumber, batchNumber)
                .update();

        // RP
        rpRecordService.lambdaUpdate()
                .set(MonthlyManageBaseModel::getBatchNumber, batchNumber)
                .set(MonthlyManageBaseModel::getIsConfirmed, YesOrNoNumberEnum.YES.getCode())
                .eq(MonthlyManageBaseModel::getIsEffect, YesOrNoNumberEnum.YES.getCode())
                .set(MonthlyManageBaseModel::getIsSendCq, YesOrNoNumberEnum.YES.getCode())
                .eq(MonthlyManageBaseModel::getMainId, mainId)
                .eq(MonthlyManageBaseModel::getBatchNumber, batchNumber)
                .update();
        rpRecordService.lambdaUpdate()
                .set(MonthlyManageBaseModel::getBatchNumber, batchNumber)
                .set(MonthlyManageBaseModel::getIsConfirmed, YesOrNoNumberEnum.YES.getCode())
                .set(MonthlyManageBaseModel::getIsSendCq, YesOrNoNumberEnum.NO.getCode())
                .eq(MonthlyManageBaseModel::getIsEffect, YesOrNoNumberEnum.NO.getCode())
                .eq(MonthlyManageBaseModel::getMainId, mainId)
                .eq(MonthlyManageBaseModel::getBatchNumber, batchNumber)
                .update();

        // FIN
        financeStampDutyService.lambdaUpdate()
                .set(MonthlyManageBaseModel::getBatchNumber, batchNumber)
                .set(MonthlyManageBaseModel::getIsConfirmed, YesOrNoNumberEnum.YES.getCode())
                .eq(MonthlyManageBaseModel::getIsEffect, YesOrNoNumberEnum.YES.getCode())
                .set(MonthlyManageBaseModel::getIsSendCq, YesOrNoNumberEnum.YES.getCode())
                .eq(MonthlyManageBaseModel::getMainId, mainId)
                .eq(MonthlyManageBaseModel::getBatchNumber, batchNumber)
                .update();

        financeStampDutyService.lambdaUpdate()
                .set(MonthlyManageBaseModel::getBatchNumber, batchNumber)
                .set(MonthlyManageBaseModel::getIsConfirmed, YesOrNoNumberEnum.YES.getCode())
                .set(MonthlyManageBaseModel::getIsSendCq, YesOrNoNumberEnum.NO.getCode())
                .eq(MonthlyManageBaseModel::getIsEffect, YesOrNoNumberEnum.NO.getCode())
                .eq(MonthlyManageBaseModel::getMainId, mainId)
                .eq(MonthlyManageBaseModel::getBatchNumber, batchNumber)
                .update();

        // PROJ
        projStampDutyService.lambdaUpdate()
                .set(MonthlyManageBaseModel::getBatchNumber, batchNumber)
                .set(MonthlyManageBaseModel::getIsConfirmed, YesOrNoNumberEnum.YES.getCode())
                .eq(MonthlyManageBaseModel::getIsEffect, YesOrNoNumberEnum.YES.getCode())
                .set(MonthlyManageBaseModel::getIsSendCq, YesOrNoNumberEnum.YES.getCode())
                .eq(MonthlyManageBaseModel::getMainId, mainId)
                .eq(MonthlyManageBaseModel::getBatchNumber, batchNumber)
                .update();
        projStampDutyService.lambdaUpdate()
                .set(MonthlyManageBaseModel::getBatchNumber, batchNumber)
                .set(MonthlyManageBaseModel::getIsConfirmed, YesOrNoNumberEnum.YES.getCode())
                .eq(MonthlyManageBaseModel::getIsEffect, YesOrNoNumberEnum.NO.getCode())
                .set(MonthlyManageBaseModel::getIsSendCq, YesOrNoNumberEnum.NO.getCode())
                .eq(MonthlyManageBaseModel::getMainId, mainId)
                .eq(MonthlyManageBaseModel::getBatchNumber, batchNumber)
                .update();

        // COST
        costRecordService.lambdaUpdate()
                .set(MonthlyManageBaseModel::getBatchNumber, batchNumber)
                .set(MonthlyManageBaseModel::getIsConfirmed, YesOrNoNumberEnum.YES.getCode())
                .eq(MonthlyManageBaseModel::getIsEffect, YesOrNoNumberEnum.YES.getCode())
                .set(MonthlyManageBaseModel::getIsSendCq, YesOrNoNumberEnum.YES.getCode())
                .eq(MonthlyManageBaseModel::getMainId, baseInfo.getMainId())
                .eq(MonthlyManageBaseModel::getBatchNumber, batchNumber)
                .update();
        costRecordService.lambdaUpdate()
                .set(MonthlyManageBaseModel::getBatchNumber, batchNumber)
                .set(MonthlyManageBaseModel::getIsConfirmed, YesOrNoNumberEnum.YES.getCode())
                .set(MonthlyManageBaseModel::getIsSendCq, YesOrNoNumberEnum.NO.getCode())
                .eq(MonthlyManageBaseModel::getMainId, baseInfo.getMainId())
                .eq(MonthlyManageBaseModel::getIsEffect, YesOrNoNumberEnum.NO.getCode())
                .eq(MonthlyManageBaseModel::getBatchNumber, batchNumber)
                .update();

        // 更新基本信息
        baseInfoService.fillMonthlyBaseInfo(baseInfo.getMainId());
    }

    @Transactional(rollbackFor = Throwable.class)
    public void updateStatus(MonthlyManagementBaseInfo baseInfo, MonthlyUpdateStatusREQ req) {
        MonthlyModuleTypeEnum moduleTypeEnum = MonthlyModuleTypeEnum.find(req.getTabType());
        if (Objects.isNull(moduleTypeEnum)) {
            throw new MithrasException("tabType" + ResultMsg.RECORD_NOT_EXIST);
        }
        Long mainId = baseInfo.getMainId();
        switch (moduleTypeEnum) {
            case AIR:
                airRecordService.lambdaUpdate()
                        .set(MonthlyManageBaseModel::getIsEffect, req.getStatus())
                        .eq(MonthlyManageBaseModel::getId, req.getRecordId())
                        .eq(MonthlyManageBaseModel::getMainId, mainId)
                        .update();
                break;
            case COST:
                costRecordService.lambdaUpdate()
                        .set(MonthlyManageBaseModel::getIsEffect, req.getStatus())
                        .eq(MonthlyManageBaseModel::getId, req.getRecordId())
                        .eq(MonthlyManageBaseModel::getMainId, mainId)
                        .update();
                break;
            case RP:
                rpRecordService.lambdaUpdate()
                        .set(MonthlyManageBaseModel::getIsEffect, req.getStatus())
                        .eq(MonthlyManageBaseModel::getId, req.getRecordId())
                        .eq(MonthlyManageBaseModel::getMainId, mainId)
                        .update();
                break;
            case STAMP_DUTY_FIN:
                financeStampDutyService.lambdaUpdate()
                        .set(MonthlyManageBaseModel::getIsEffect, req.getStatus())
                        .eq(MonthlyManageBaseModel::getId, req.getRecordId())
                        .eq(MonthlyManageBaseModel::getMainId, mainId)
                        .update();
                break;
            case STAMP_DUTY_PROJ:
                projStampDutyService.lambdaUpdate()
                        .set(MonthlyManageBaseModel::getIsEffect, req.getStatus())
                        .eq(MonthlyManageBaseModel::getId, req.getRecordId())
                        .eq(MonthlyManageBaseModel::getMainId, mainId)
                        .update();
                break;
            default:
                break;
        }
    }


    /**
     * 月结反结算回调
     */
    @Transactional(rollbackFor = Throwable.class)
    public <T extends MonthlyManageBaseModel> void callBackAntiSettlement(MonthlyModuleTypeEnum moduleType, Long recordId) {
        if (Objects.isNull(recordId)) {
            log.error("月结反结算回调，recordId为空");
            return;
        }
        UpdateWrapper<T> updateWrapper = new UpdateWrapper<T>()
                .eq("id", recordId)
                .set("is_send_cq", YesOrNoNumberEnum.NO.getCode())
                .set("is_effect", YesOrNoNumberEnum.NO.getCode())
                .set("is_confirmed", YesOrNoNumberEnum.NO.getCode())
                .set("batch_number", null);

        switch (moduleType) {
            case AIR:
                MonthlyManagementAirRecord airRecord = airRecordService.getById(recordId);
                if (Objects.isNull(airRecord)) {
                    throw new MithrasException("AIR反结算回调，记录不存在");
                }
                airRecordService.update((Wrapper<MonthlyManagementAirRecord>) updateWrapper);
                contractIncomeSharingService.lambdaUpdate()
                        .eq(ContractIncomeSharing::getReceiptId, airRecord.getSourceId())
                        .set(ContractIncomeSharing::getIsConfirmed, YesOrNoNumberEnum.NO.getCode())
                        .set(ContractIncomeSharing::getConfirmBatch, null)
                        .update();
                break;
            case RP:
                MonthlyManagementRpRecord rpRecord = rpRecordService.getById(recordId);
                if (Objects.isNull(rpRecord)) {
                    throw new MithrasException("RP反结算回调，记录不存在");
                }
                rpRecordService.update((Wrapper<MonthlyManagementRpRecord>) updateWrapper);
                contractIncomeSharingService.lambdaUpdate()
                        .eq(ContractIncomeSharing::getReceiptId, rpRecord.getSourceId())
                        .set(ContractIncomeSharing::getIsConfirmed, YesOrNoNumberEnum.NO.getCode())
                        .set(ContractIncomeSharing::getConfirmBatch, null)
                        .update();
                break;
            case COST:
                MonthlyManagementCostRecord costRecord = costRecordService.getById(recordId);
                if (Objects.isNull(costRecord)) {
                    throw new MithrasException("COST反结算回调，记录不存在");
                }
                costRecordService.update((Wrapper<MonthlyManagementCostRecord>) updateWrapper);
                fundsDailyCostMapper.update(null, new LambdaUpdateWrapper<FundsDailyCost>()
                        .set(FundsDailyCost::getIsConfirmed, YesOrNoNumberEnum.NO.getCode())
                        .set(FundsDailyCost::getConfirmBatch, null)
                        .eq(FundsDailyCost::getId, costRecord.getSourceId()));
                break;
            case STAMP_DUTY_FIN:
                MonthlyFinanceStampDuty financeStampDuty = financeStampDutyService.getById(recordId);
                if (Objects.isNull(financeStampDuty)) {
                    throw new MithrasException("STAMP_DUTY_FIN反结算回调，记录不存在");
                }
                financeStampDutyService.update((Wrapper<MonthlyFinanceStampDuty>) updateWrapper);
                monthlyStampDutyMapper.update(null, new LambdaUpdateWrapper<MonthlyStampDuty>()
                        .eq(MonthlyStampDuty::getId, financeStampDuty.getSourceId())
                        .set(MonthlyStampDuty::getIsConfirmed, YesOrNoNumberEnum.NO.getCode())
                        .set(MonthlyStampDuty::getConfirmBatch, null));
                break;
            case STAMP_DUTY_PROJ:
                MonthlyProjStampDuty projStampDuty = projStampDutyService.getById(recordId);
                if (Objects.isNull(projStampDuty)) {
                    throw new MithrasException("STAMP_DUTY_PROJ反结算回调，记录不存在");
                }
                projStampDutyService.update((Wrapper<MonthlyProjStampDuty>) updateWrapper);
                monthlyStampDutyMapper.update(null, new LambdaUpdateWrapper<MonthlyStampDuty>()
                        .eq(MonthlyStampDuty::getId, projStampDuty.getSourceId())
                        .set(MonthlyStampDuty::getIsConfirmed, YesOrNoNumberEnum.NO.getCode())
                        .set(MonthlyStampDuty::getConfirmBatch, null));
                break;
            default:
                break;
        }
    }

    public void close(MonthlyManagementBaseInfo baseInfo) {
        MonthlyManagementBaseInfo info = new MonthlyManagementBaseInfo();
        info.setCloseDate(LocalDateTime.now());
        info.setStatus(MonthlyManagementStatusEnum.CLOSED_AMOUNT.name());
        info.setId(baseInfo.getId());
        baseInfoService.updateById(info);
    }

    // 关账校验
    public void closeCheck(MonthlyManagementBaseInfo baseInfo) {
        //这里还需要校验银行流水是否全部核销完毕
        LocalDate beginDate = LocalDate.of(baseInfo.getYear(), baseInfo.getMonth(), 1);
        LocalDate endDate = beginDate.with(TemporalAdjusters.lastDayOfMonth());
        List<FinanceFlowRecord> financeFlowRecords = financeFlowRecordMapper.selectList(Wrappers.<FinanceFlowRecord>lambdaQuery()
                .eq(FinanceFlowRecord::getFinancingFlowType, BankFlowCenterTypeEnum.PROCESSING_CENTER.name())
                .between(FinanceFlowRecord::getBizdate, beginDate, endDate));
        if (!financeFlowRecords.isEmpty()) {
            throw new MithrasException("银行流水-处理中心中，不允许存在交易日期处于该月份的数据");
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void updateSingle(MonthlyFreshSingleREQ req) {
        MonthlyManagementBaseInfo baseInfo = baseInfoService.getOne(Wrappers.<MonthlyManagementBaseInfo>lambdaQuery()
                .eq(MonthlyManagementBaseInfo::getMainId, req.getMainId()));
        LocalDate now = LocalDate.of(baseInfo.getYear(), baseInfo.getMonth(), 1);
        MonthlyFreshREQ monthlyFreshReq = new MonthlyFreshREQ(now.format(DateTimeFormatter.ofPattern("yyyy-MM")), req.getSingleRecordId(), req.getTabType());
        MonthlyModuleTypeEnum typeEnum = MonthlyModuleTypeEnum.find(req.getTabType());
        if (Objects.isNull(typeEnum)) {
            throw new MithrasException("不支持的TabType");
        }
        switch (typeEnum) {
            case AIR:
                MonthlyManagementAirRecord airRecord = airRecordService.getById(req.getSingleRecordId());
                if (Objects.isNull(airRecord)) {
                    throw new MithrasException("AIR数据更新，记录不存在");
                }
                if (Objects.equals(airRecord.getIsConfirmed(), YesOrNoNumberEnum.YES.getCode())
                        && Objects.equals(airRecord.getIsEffect(), YesOrNoNumberEnum.YES.getCode())
                        && Objects.equals(airRecord.getIsSendCq(), YesOrNoNumberEnum.YES.getCode())) {
                    //这里需要去反结算
                    try {
                        ThirdFinancialWithdrawREQ withdrawReq = new ThirdFinancialWithdrawREQ();
                        withdrawReq.setBusinessKey(airRecord.getId().toString());
                        withdrawReq.setPlatform(FinancialDevUrlENUM.CQ2_ACCOUNT_APPLICATION.name());
                        withdrawReq.setSource(ExceptionSourceENUM.ASSET_SIDE_AIR_ACCOUNT.name());
                        getBean(FinancialController.class).withdraw(Collections.singletonList(withdrawReq));
                    } catch (Exception e) {
                        throw new MithrasException(String.format("更新数据时，反结算失败，模块：%s， 记录ID：%s", typeEnum.name(), req.getSingleRecordId()));
                    }
                }
                airRecordService.freshMonthlyAirRecord(monthlyFreshReq);
                break;
            case COST:
                MonthlyManagementCostRecord costRecord = costRecordService.getById(req.getSingleRecordId());
                if (Objects.isNull(costRecord)) {
                    throw new MithrasException("COST数据更新，记录不存在");
                }
                if (Objects.equals(costRecord.getIsConfirmed(), YesOrNoNumberEnum.YES.getCode())
                        && Objects.equals(costRecord.getIsEffect(), YesOrNoNumberEnum.YES.getCode())
                        && Objects.equals(costRecord.getIsSendCq(), YesOrNoNumberEnum.YES.getCode())) {
                    //这里需要去反结算
                    try {
                        ThirdFinancialWithdrawREQ withdrawReq = new ThirdFinancialWithdrawREQ();
                        withdrawReq.setBusinessKey(costRecord.getId().toString());
                        withdrawReq.setPlatform(FinancialDevUrlENUM.CQ2_ACCOUNT_APPLICATION.name());
                        withdrawReq.setSource(ExceptionSourceENUM.ASSET_SIDE_COST_DK.name());
                        getBean(FinancialController.class).withdraw(Collections.singletonList(withdrawReq));
                    } catch (Exception e) {
                        throw new MithrasException(String.format("更新数据时，反结算失败，模块：%s， 记录ID：%s", typeEnum.name(), req.getSingleRecordId()));
                    }
                }
                costRecordService.freshCostList(monthlyFreshReq);
                break;
            case RP:
                MonthlyManagementRpRecord rpRecord = rpRecordService.getById(req.getSingleRecordId());
                if (Objects.isNull(rpRecord)) {
                    throw new MithrasException("RP数据更新，记录不存在");
                }
                if (Objects.equals(rpRecord.getIsConfirmed(), YesOrNoNumberEnum.YES.getCode())
                        && Objects.equals(rpRecord.getIsEffect(), YesOrNoNumberEnum.YES.getCode())
                        && Objects.equals(rpRecord.getIsSendCq(), YesOrNoNumberEnum.YES.getCode())) {
                    //这里需要去反结算
                    try {
                        ThirdFinancialWithdrawREQ withdrawReq = new ThirdFinancialWithdrawREQ();
                        withdrawReq.setBusinessKey(rpRecord.getId().toString());
                        withdrawReq.setPlatform(FinancialDevUrlENUM.CQ2_ACCOUNT_APPLICATION.name());
                        withdrawReq.setSource(ExceptionSourceENUM.ASSET_SIDE_PR_ACCOUNT.name());
                        getBean(FinancialController.class).withdraw(Collections.singletonList(withdrawReq));
                    } catch (Exception e) {
                        throw new MithrasException(String.format("更新数据时，反结算失败，模块：%s， 记录ID：%s", typeEnum.name(), req.getSingleRecordId()));
                    }
                }
                rpRecordService.freshRpList(monthlyFreshReq);
                break;
            case STAMP_DUTY_FIN:
                MonthlyFinanceStampDuty financeStampDuty = financeStampDutyService.getById(req.getSingleRecordId());
                if (Objects.isNull(financeStampDuty)) {
                    throw new MithrasException("STAMP_DUTY_FIN数据更新，记录不存在");
                }
                if (Objects.equals(financeStampDuty.getIsConfirmed(), YesOrNoNumberEnum.YES.getCode())
                        && Objects.equals(financeStampDuty.getIsEffect(), YesOrNoNumberEnum.YES.getCode())
                        && Objects.equals(financeStampDuty.getIsSendCq(), YesOrNoNumberEnum.YES.getCode())) {
                    //这里需要去反结算
                    try {
                        ThirdFinancialWithdrawREQ withdrawReq = new ThirdFinancialWithdrawREQ();
                        withdrawReq.setBusinessKey(financeStampDuty.getId().toString());
                        withdrawReq.setPlatform(FinancialDevUrlENUM.CQ2_ACCOUNT_APPLICATION.name());
                        withdrawReq.setSource(ExceptionSourceENUM.FINANCE_SIDE_COST_STAMP_DUTY.name());
                        getBean(FinancialController.class).withdraw(Collections.singletonList(withdrawReq));
                    } catch (Exception e) {
                        throw new MithrasException(String.format("更新数据时，反结算失败，模块：%s， 记录ID：%s", typeEnum.name(), req.getSingleRecordId()));
                    }
                }
                financeStampDutyService.freshFinanceStampDuty(monthlyFreshReq);
                break;
            case STAMP_DUTY_PROJ:
                MonthlyProjStampDuty projStampDuty = projStampDutyService.getById(req.getSingleRecordId());
                if (Objects.isNull(projStampDuty)) {
                    throw new MithrasException("STAMP_DUTY_PROJ数据更新，记录不存在");
                }
                if (Objects.equals(projStampDuty.getIsConfirmed(), YesOrNoNumberEnum.YES.getCode())
                        && Objects.equals(projStampDuty.getIsEffect(), YesOrNoNumberEnum.YES.getCode())
                        && Objects.equals(projStampDuty.getIsSendCq(), YesOrNoNumberEnum.YES.getCode())) {
                    //这里需要去反结算
                    try {
                        ThirdFinancialWithdrawREQ withdrawReq = new ThirdFinancialWithdrawREQ();
                        withdrawReq.setBusinessKey(projStampDuty.getId().toString());
                        withdrawReq.setPlatform(FinancialDevUrlENUM.CQ2_ACCOUNT_APPLICATION.name());
                        withdrawReq.setSource(ExceptionSourceENUM.ASSET_SIDE_COST_STAMP_DUTY.name());
                        getBean(FinancialController.class).withdraw(Collections.singletonList(withdrawReq));
                    } catch (Exception e) {
                        throw new MithrasException(String.format("更新数据时，反结算失败，模块：%s， 记录ID：%s", typeEnum.name(), req.getSingleRecordId()));
                    }
                }
                projStampDutyService.freshProjStampDuty(monthlyFreshReq);
                break;
            default:
                break;
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void pushSingle(MonthlySubmitSingleREQ req) {
        MonthlyManagementBaseInfo baseInfo = baseInfoService.getOne(Wrappers.<MonthlyManagementBaseInfo>lambdaQuery()
                .eq(MonthlyManagementBaseInfo::getMainId, req.getMainId())
                .eq(MonthlyManagementBaseInfo::getStatus, MonthlyManagementStatusEnum.CONFIRMED.name())
                .last(StringUtil.mysqlLimitOne()));
        if (Objects.isNull(baseInfo)) {
            throw new MithrasException("推送单条数据失败，主表数据不存在或当前状态不支持");
        }

        MonthlyModuleTypeEnum moduleTypeEnum = MonthlyModuleTypeEnum.find(req.getTabType());
        if (Objects.isNull(moduleTypeEnum)) {
            throw new MithrasException("推送单条数据失败，模块类型不存在");
        }

        String batchNumber = baseInfoService.updateExistedData(req.getId(), moduleTypeEnum);
        String yearAndMonth = LocalDate.of(baseInfo.getYear(), baseInfo.getMonth(), 1).format(DateTimeFormatter.ofPattern(DatePattern.NORM_MONTH_PATTERN));
        monthlySendCqService.accountApplication(yearAndMonth, batchNumber);
    }

    public <T extends MonthlyManageBaseModel> String updateExistedData(Long id, MonthlyModuleTypeEnum moduleType) {
        String batchNumber = LocalDateTime.now().format(DateTimeFormatter.ofPattern(DatePattern.PURE_DATETIME_PATTERN));
        UpdateWrapper<T> updateWrapper = new UpdateWrapper<T>()
                .set("batch_number", batchNumber)
                .set("is_effect", YesOrNoNumberEnum.YES.getCode())
                .set("is_confirmed", YesOrNoNumberEnum.YES.getCode())
                .set("batch_number", batchNumber)
                .eq("id", id);

        switch (moduleType) {
            case AIR:
                MonthlyManagementAirRecord airRecord = airRecordService.getById(id);
                if (Objects.isNull(airRecord)) {
                    throw new MithrasException("更新数据失败，记录不存在");
                }
                contractIncomeSharingService.lambdaUpdate()
                        .eq(ContractIncomeSharing::getReceiptId, airRecord.getSourceId())
                        .set(ContractIncomeSharing::getConfirmBatch, batchNumber)
                        .set(ContractIncomeSharing::getIsConfirmed, YesOrNoNumberEnum.YES.getCode())
                        .set(ContractIncomeSharing::getConfirmTime, LocalDateTime.now())
                        .update();
                airRecordService.update((Wrapper<MonthlyManagementAirRecord>) updateWrapper);
                break;
            case COST:
                MonthlyManagementCostRecord costRecord = costRecordService.getById(id);
                if (Objects.isNull(costRecord)) {
                    throw new MithrasException("更新数据失败，记录不存在");
                }
                fundsDailyCostMapper.update(null, new LambdaUpdateWrapper<FundsDailyCost>()
                        .set(FundsDailyCost::getConfirmBatch, batchNumber)
                        .set(FundsDailyCost::getIsConfirmed, YesOrNoNumberEnum.YES.getCode())
                        .set(FundsDailyCost::getConfirmTime, LocalDateTime.now())
                        .eq(FundsDailyCost::getId, costRecord.getSourceId()));
                costRecordService.update((Wrapper<MonthlyManagementCostRecord>) updateWrapper);
                break;
            case RP:
                MonthlyManagementRpRecord rpRecord = rpRecordService.getById(id);
                if (Objects.isNull(rpRecord)) {
                    throw new MithrasException("更新数据失败，记录不存在");
                }
                contractIncomeSharingService.lambdaUpdate()
                        .eq(ContractIncomeSharing::getReceiptId, rpRecord.getSourceId())
                        .set(ContractIncomeSharing::getConfirmBatch, batchNumber)
                        .set(ContractIncomeSharing::getIsConfirmed, YesOrNoNumberEnum.YES.getCode())
                        .set(ContractIncomeSharing::getConfirmTime, LocalDateTime.now())
                        .update();
                rpRecordService.update((Wrapper<MonthlyManagementRpRecord>) updateWrapper);
                break;
            case STAMP_DUTY_FIN:
                MonthlyFinanceStampDuty stampDutyFin = financeStampDutyService.getById(id);
                if (Objects.isNull(stampDutyFin)) {
                    throw new MithrasException("更新数据失败，记录不存在");
                }
                monthlyStampDutyMapper.update(null, new LambdaUpdateWrapper<MonthlyStampDuty>()
                        .set(MonthlyStampDuty::getConfirmBatch, batchNumber)
                        .set(MonthlyStampDuty::getIsConfirmed, YesOrNoNumberEnum.YES.getCode())
                        .set(MonthlyStampDuty::getConfirmTime, LocalDateTime.now())
                        .eq(MonthlyStampDuty::getId, stampDutyFin.getSourceId()));
                financeStampDutyService.update((Wrapper<MonthlyFinanceStampDuty>) updateWrapper);
                break;
            case STAMP_DUTY_PROJ:
                MonthlyProjStampDuty projStampDuty = projStampDutyService.getById(id);
                if (Objects.isNull(projStampDuty)) {
                    throw new MithrasException("更新数据失败，记录不存在");
                }
                monthlyStampDutyMapper.update(null, new LambdaUpdateWrapper<MonthlyStampDuty>()
                        .set(MonthlyStampDuty::getConfirmBatch, batchNumber)
                        .set(MonthlyStampDuty::getIsConfirmed, YesOrNoNumberEnum.YES.getCode())
                        .set(MonthlyStampDuty::getConfirmTime, LocalDateTime.now())
                        .eq(MonthlyStampDuty::getId, projStampDuty.getSourceId()));
                projStampDutyService.update((Wrapper<MonthlyProjStampDuty>) updateWrapper);
                break;
            default:
                break;
        }
        return batchNumber;
    }
}




