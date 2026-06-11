package cn.zswltech.mithras.finance.service.dashboard;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.dao.OrgDOMapper;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.dto.dashboard.*;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.kpi.enums.BelongTypeEnum;
import cn.zswltech.mithras.kpi.enums.BusinessTypeEnum;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.finance.mapper.finance.FinanceProjectProfitDetailMapper;
import cn.zswltech.mithras.finance.mapper.finance.FinanceProjectProfitMapper;
import cn.zswltech.mithras.kpi.mapper.PerformanceBaseInfoMapper;
import cn.zswltech.mithras.kpi.mapper.PerformanceMainInfoMapper;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.finance.mapper.model.finance.FinanceProjectProfit;
import cn.zswltech.mithras.finance.mapper.model.finance.FinanceProjectProfitDetail;
import cn.zswltech.mithras.kpi.model.PerformanceBaseInfo;
import cn.zswltech.mithras.kpi.model.PerformanceMainInfo;
import cn.zswltech.mithras.payment.mapper.model.PaymentActualDetail;
import cn.zswltech.mithras.payment.mapper.PaymentActualDetailMapper;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.hutool.extra.spring.SpringUtil.getBean;

/**
 * @author yangxiong
 * @date 2024/7/1/10:29
 * @description
 */
@Slf4j
@Service
public class DashboardPerformanceService implements cn.zswltech.mithras.dashboard.application.DashboardPerformanceApplicationService {

    @Resource
    private PerformanceMainInfoMapper performanceMainInfoMapper;
    @Resource
    private PerformanceBaseInfoMapper performanceBaseInfoMapper;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private FinanceProjectProfitDetailMapper financeProjectProfitDetailMapper;

    public DeptPerformanceRSP deptPerformance() throws ExecutionException, InterruptedException {
        DeptPerformanceRSP rsp = new DeptPerformanceRSP();
        //找到最新一年的数据
        PerformanceMainInfo performanceMainInfo = performanceMainInfoMapper.selectOne(Wrappers.<PerformanceMainInfo>lambdaQuery()
                .eq(PerformanceMainInfo::getStatus, YesOrNoNumberEnum.YES.getCode())
                .le(PerformanceMainInfo::getYear, LocalDateTime.now().getYear())
                .orderByDesc(PerformanceMainInfo::getYear)
                .last(StringUtil.mysqlLimitOne()));
        if (Objects.isNull(performanceMainInfo)) {
            return rsp;
        }

        //找到部门的基础数据
        List<PerformanceBaseInfo> performanceBaseInfos = performanceBaseInfoMapper.selectList(Wrappers.<PerformanceBaseInfo>lambdaQuery()
                .eq(PerformanceBaseInfo::getMainId, performanceMainInfo.getId())
                .eq(PerformanceBaseInfo::getBelongType, BelongTypeEnum.DEPARTMENT.name()));

        if (CollectionUtils.isEmpty(performanceBaseInfos)) {
            return rsp;
        }
        AccountVO loginInfo = AccountUtil.getLoginInfo();
        if (Objects.isNull(loginInfo)) {
            throw new MithrasException(ResultMsg.USER_NOT_LOGIN);
        }
        //速度慢，使用多线程加快
        CompletableFuture<String> cardFuture = CompletableFuture.supplyAsync(() -> fillCardList(rsp, performanceBaseInfos, loginInfo, Math.toIntExact(performanceMainInfo.getYear())));
        CompletableFuture<String> deptFuture = CompletableFuture.supplyAsync(() -> fillDeptInfo(rsp, performanceBaseInfos, performanceMainInfo.getYear()));
        cardFuture.get();
        deptFuture.get();
        return rsp;
    }

    private String fillDeptInfo(DeptPerformanceRSP rsp, List<PerformanceBaseInfo> performanceBaseInfos, Long year) {
        List<DeptShipSortPerformanceRSP> dataList = new ArrayList<>();
        Map<Long, PerformanceBaseInfo> baseInfoMap = performanceBaseInfos.stream().filter(performanceBaseInfo -> Objects.equals(performanceBaseInfo.getBusinessType(), BusinessTypeEnum.DEPT_TOTAL.name()))
                .collect(Collectors.toMap(PerformanceBaseInfo::getBelongDeptId, Function.identity(), (a, b) -> a));
        List<OrgDO> dos = getBean(OrgDOMapper.class).selectByIds(new ArrayList<>(baseInfoMap.keySet()), 1);
        Map<Long, String> orgMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(dos)) {
            orgMap = dos.stream().collect(Collectors.toMap(OrgDO::getId, OrgDO::getName, (a, b) -> a));
        }
        //查询实际投放实际收入和利润
        //设置当前年度时间
        LocalDate dateTime = LocalDate.of(Math.toIntExact(year), 1, 1);
        LocalDateTime beginTime = dateTime.with(TemporalAdjusters.firstDayOfYear()).atStartOfDay();

        //查到实际投放
        Map<Long, String> deptActualAdvertisingAmountMap = new HashMap<>();
        Map<Long, String> deptActualIncomeAmountMap = new HashMap<>();
        Map<Long, String> deptActualProfitAmountMap = new HashMap<>();
        List<ContractBaseInfo> contractBaseInfos = contractBaseInfoMapper.selectList(Wrappers.<ContractBaseInfo>lambdaQuery()
                .in(ContractBaseInfo::getBizDeptId, baseInfoMap.keySet()));
        if (!CollectionUtils.isEmpty(contractBaseInfos)) {
            //收入和利润
            FinanceProjectProfit financeProjectProfit = getBean(FinanceProjectProfitMapper.class).selectOne(Wrappers.<FinanceProjectProfit>lambdaQuery()
                    .eq(FinanceProjectProfit::getYear, year)
                    .orderByDesc(FinanceProjectProfit::getMonth)
                    .last(StringUtil.mysqlLimitOne()));
            if (Objects.isNull(financeProjectProfit)) {
                log.error("项目利润不存在");
                throw new MithrasException("项目利润不存在");
            }
            List<FinanceProjectProfitDetail> financeProjectProfitDetails = financeProjectProfitDetailMapper.selectList(Wrappers.<FinanceProjectProfitDetail>lambdaQuery()
                    .eq(FinanceProjectProfitDetail::getProjectProfitId, financeProjectProfit.getId())
                    .in(FinanceProjectProfitDetail::getContractId, contractBaseInfos.stream().map(ContractBaseInfo::getId).collect(Collectors.toList())));
            if (!CollectionUtils.isEmpty(financeProjectProfitDetails)) {
                rsp.setDataUpdateTime(financeProjectProfit.getUpdateTime().format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
                Map<Long, List<FinanceProjectProfitDetail>> longListMap = financeProjectProfitDetails.stream().filter(obj -> Objects.nonNull(obj.getAssessDeptId()))
                        .collect(Collectors.groupingBy(FinanceProjectProfitDetail::getAssessDeptId));
                longListMap.forEach((deptId, v) -> {
                    if (!CollectionUtils.isEmpty(v)) {
                        long actualProfitAmount = v.stream().mapToLong(FinanceProjectProfitDetail::getTotalProfitThisYear).summaryStatistics().getSum();
                        long actualIncomeAmount = v.stream().mapToLong(FinanceProjectProfitDetail::getTotalIncomeThisYear).summaryStatistics().getSum();
                        deptActualProfitAmountMap.put(deptId, String.valueOf(actualProfitAmount));
                        deptActualIncomeAmountMap.put(deptId, String.valueOf(actualIncomeAmount));
                    }
                });
                LocalDateTime endTime = beginTime.with(TemporalAdjusters.lastDayOfYear()).toLocalDate().atTime(LocalTime.MAX);
                List<PaymentActualDetail> paymentActualDetails = getBean(PaymentActualDetailMapper.class).selectList(Wrappers.<PaymentActualDetail>lambdaQuery()
                        .between(PaymentActualDetail::getPaidInDate, beginTime, endTime)
                        .in(PaymentActualDetail::getContractId, financeProjectProfitDetails.stream().map(FinanceProjectProfitDetail::getContractId).collect(Collectors.toList())));
                if (!CollectionUtils.isEmpty(paymentActualDetails)) {
                    longListMap.forEach((k, v) -> {
                        List<PaymentActualDetail> detailList = paymentActualDetails.stream()
                                .filter(paymentActualDetail -> v.stream().map(FinanceProjectProfitDetail::getContractId).collect(Collectors.toList()).contains(paymentActualDetail.getContractId()))
                                .collect(Collectors.toList());
                        if (!CollectionUtils.isEmpty(detailList)) {
                            long actualAdvertisingAmount = detailList.stream().mapToLong(PaymentActualDetail::getPaidInAmount).summaryStatistics().getSum();
                            deptActualAdvertisingAmountMap.put(k, String.valueOf(actualAdvertisingAmount));
                        }
                    });
                }
                LocalDateTime updateTime = LocalDate.of(financeProjectProfit.getYear(), financeProjectProfit.getMonth(), 1).plusMonths(1).atStartOfDay().minusSeconds(1);
                rsp.setDataUpdateTime(updateTime.format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
            }
        }

        for (Map.Entry<Long, PerformanceBaseInfo> entry : baseInfoMap.entrySet()) {
            DeptShipSortPerformanceRSP deptPerformanceListVO = new DeptShipSortPerformanceRSP();
            deptPerformanceListVO.setDeptId(entry.getKey());
            deptPerformanceListVO.setDeptName(orgMap.get(entry.getKey()));
            String revenueTarget = BigDecimal.valueOf(entry.getValue().getRevenueTarget())
                    .divide(BigDecimal.valueOf(100000000), 2, RoundingMode.HALF_UP).toPlainString();
            deptPerformanceListVO.setRevenueTarget(revenueTarget);
            String profitTarget = BigDecimal.valueOf(entry.getValue().getProfitTarget())
                    .divide(BigDecimal.valueOf(100000000), 2, RoundingMode.HALF_UP).toPlainString();
            deptPerformanceListVO.setProfitTarget(profitTarget);
            String advertisingTarget = BigDecimal.valueOf(entry.getValue().getAdvertisingAmount())
                    .divide(BigDecimal.valueOf(100000000), 2, RoundingMode.HALF_UP).toPlainString();
            deptPerformanceListVO.setAdvertisingTarget(advertisingTarget);
            deptPerformanceListVO.setPerformanceId(entry.getValue().getId());

            deptPerformanceListVO.setAdvertisingAmount("0");
            if (Objects.nonNull(deptActualAdvertisingAmountMap.get(entry.getKey()))) {
                BigDecimal deliveryAmount = new BigDecimal(deptActualAdvertisingAmountMap.get(entry.getKey()));
                String deliveryAchievementRate = deliveryAmount.multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(entry.getValue().getAdvertisingAmount()), 2, RoundingMode.HALF_UP).toPlainString();
                deptPerformanceListVO.setDeliveryAchievementRate(deliveryAchievementRate);
                deptPerformanceListVO.setAdvertisingAmount(deliveryAmount.divide(BigDecimal.valueOf(100000000), 2, RoundingMode.HALF_UP).toPlainString());
            }
            deptPerformanceListVO.setProfitAmount("0");
            if (Objects.nonNull(deptActualProfitAmountMap.get(entry.getKey()))) {
                BigDecimal profitAmount = new BigDecimal(deptActualProfitAmountMap.get(entry.getKey()));
                String profitAchievementRate = profitAmount.multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(entry.getValue().getProfitTarget()), 2, RoundingMode.HALF_UP).toPlainString();
                deptPerformanceListVO.setProfitAchievementRate(profitAchievementRate);
                deptPerformanceListVO.setProfitAmount(profitAmount.divide(BigDecimal.valueOf(100000000), 2, RoundingMode.HALF_UP).toPlainString());
            }
            deptPerformanceListVO.setIncomeAmount("0");
            if (Objects.nonNull(deptActualIncomeAmountMap.get(entry.getKey()))) {
                BigDecimal revenueAmount = new BigDecimal(deptActualIncomeAmountMap.get(entry.getKey()));
                String revenueAchievementRate = revenueAmount.multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(entry.getValue().getRevenueTarget()), 2, RoundingMode.HALF_UP).toPlainString();
                deptPerformanceListVO.setRevenueAchievementRate(revenueAchievementRate);
                deptPerformanceListVO.setIncomeAmount(revenueAmount.divide(BigDecimal.valueOf(100000000), 2, RoundingMode.HALF_UP).toPlainString());
            }
            dataList.add(deptPerformanceListVO);
        }

        //分别将几个利率排名
        dataList = dataList.stream().peek(deptPerformanceListVO -> {
                    if (Objects.isNull(deptPerformanceListVO.getDeliveryAchievementRate())) {
                        deptPerformanceListVO.setDeliveryAchievementRate("0");
                    }
                    deptPerformanceListVO.setDeliveryAchievementRateDouble(Double.valueOf(deptPerformanceListVO.getDeliveryAchievementRate()));
                }).sorted(Comparator.comparingDouble(DeptShipSortPerformanceRSP::getDeliveryAchievementRateDouble).reversed())
                .collect(Collectors.toList());
        for (int i = 0; i < dataList.size(); i++) {
            DeptShipSortPerformanceRSP deptPerformanceListVO = dataList.get(i);
            deptPerformanceListVO.setDeliveryAchievementRateSort(String.valueOf(i + 1));
        }
        dataList = dataList.stream().peek(deptPerformanceListVO -> {
                    if (Objects.isNull(deptPerformanceListVO.getRevenueAchievementRate())) {
                        deptPerformanceListVO.setRevenueAchievementRate("0");
                    }
                    deptPerformanceListVO.setRevenueAchievementRateDouble(Double.valueOf(deptPerformanceListVO.getRevenueAchievementRate()));
                }).sorted(Comparator.comparingDouble(DeptShipSortPerformanceRSP::getRevenueAchievementRateDouble).reversed())
                .collect(Collectors.toList());
        for (int i = 0; i < dataList.size(); i++) {
            DeptShipSortPerformanceRSP deptPerformanceListVO = dataList.get(i);
            deptPerformanceListVO.setRevenueAchievementRateSort(String.valueOf(i + 1));
        }

        dataList = dataList.stream().peek(deptPerformanceListVO -> {
                    if (Objects.isNull(deptPerformanceListVO.getProfitAchievementRate())) {
                        deptPerformanceListVO.setProfitAchievementRate("0");
                    }
                    deptPerformanceListVO.setProfitAchievementRateDouble(Double.valueOf(deptPerformanceListVO.getProfitAchievementRate()));
                }).sorted(Comparator.comparingDouble(DeptShipSortPerformanceRSP::getProfitAchievementRateDouble).reversed())
                .collect(Collectors.toList());
        for (int i = 0; i < dataList.size(); i++) {
            DeptShipSortPerformanceRSP deptPerformanceListVO = dataList.get(i);
            deptPerformanceListVO.setProfitAchievementRateSort(String.valueOf(i + 1));
        }

        dataList.sort(Comparator.comparing(DeptShipSortPerformanceRSP::getDeliveryAchievementRateSort)
                .thenComparing(DeptShipSortPerformanceRSP::getRevenueAchievementRateSort)
                .thenComparing(DeptShipSortPerformanceRSP::getProfitAchievementRateSort));
        rsp.setDataList(dataList);
        return "ok";
    }

    private String fillCardList(DeptPerformanceRSP rsp, List<PerformanceBaseInfo> performanceBaseInfos, AccountVO loginInfo, int year) {
        List<Long> deptIds = performanceMainInfoMapper.getDeptHead(loginInfo.getId());
        if (CollectionUtils.isEmpty(deptIds)) {
            return "ok";
        }
        //设置当前年度时间
        LocalDateTime beginTime = LocalDate.of(year, 1, 1).with(TemporalAdjusters.firstDayOfYear()).atStartOfDay();
        LocalDateTime endTime = beginTime.with(TemporalAdjusters.lastDayOfYear()).toLocalDate().atTime(LocalTime.MAX);
        List<CardListVo> cardList = new ArrayList<>();
        //累计投放
        CardListVo advertising = new CardListVo();
        advertising.setType("投放");
        performanceBaseInfos = performanceBaseInfos.stream()
                .filter(performanceBaseInfo -> deptIds.contains(performanceBaseInfo.getBelongDeptId()))
                .filter(performanceBaseInfo -> BusinessTypeEnum.DEPT_TOTAL.name().equals(performanceBaseInfo.getBusinessType()))
                .collect(Collectors.toList());
        long advertisingAmount = performanceBaseInfos.stream()
                .mapToLong(PerformanceBaseInfo::getAdvertisingAmount).summaryStatistics().getSum();
        advertising.setTargetBD(BigDecimal.valueOf(advertisingAmount).divide(BigDecimal.valueOf(100000000), 2, RoundingMode.HALF_UP));
        advertising.setTarget(advertising.getTargetBD().toPlainString());
        //查到实际投放，合同取项目利润表中的合同
        FinanceProjectProfit financeProjectProfit = getBean(FinanceProjectProfitMapper.class).selectOne(Wrappers.<FinanceProjectProfit>lambdaQuery()
                .eq(FinanceProjectProfit::getYear, year)
                .orderByDesc(FinanceProjectProfit::getMonth)
                .last(StringUtil.mysqlLimitOne()));
        if (Objects.isNull(financeProjectProfit)) {
            log.error("项目利润不存在");
            throw new MithrasException("项目利润不存在");
        }
        List<FinanceProjectProfitDetail> financeProjectProfitDetails = financeProjectProfitDetailMapper.selectList(Wrappers.<FinanceProjectProfitDetail>lambdaQuery()
                .eq(FinanceProjectProfitDetail::getProjectProfitId, financeProjectProfit.getId())
                .in(FinanceProjectProfitDetail::getAssessDeptId, deptIds));
        List<ContractBaseInfo> contractBaseInfos = new LinkedList<>();
        if (!CollectionUtils.isEmpty(financeProjectProfitDetails)) {
            List<Long> contractIds = financeProjectProfitDetails.stream().map(FinanceProjectProfitDetail::getContractId).collect(Collectors.toList());
            contractBaseInfos = contractBaseInfoMapper.selectBatchIds(contractIds);
        }
        advertising.setAccumulatedAmount("0.00");
        if (!CollectionUtils.isEmpty(contractBaseInfos)) {
            List<PaymentActualDetail> paymentActualDetails = getBean(PaymentActualDetailMapper.class).selectList(Wrappers.<PaymentActualDetail>lambdaQuery()
                    .between(PaymentActualDetail::getPaidInDate, beginTime, endTime)
                    .in(PaymentActualDetail::getContractId, contractBaseInfos.stream().map(ContractBaseInfo::getId).collect(Collectors.toList())));
            if (!CollectionUtils.isEmpty(paymentActualDetails)) {
                long actualAdvertisingAmount = paymentActualDetails.stream().mapToLong(PaymentActualDetail::getPaidInAmount).summaryStatistics().getSum();
                String actualAdvertisingTarget = BigDecimal.valueOf(actualAdvertisingAmount).divide(BigDecimal.valueOf(100000000), 2, RoundingMode.HALF_UP).toPlainString();
                advertising.setAccumulatedAmount(actualAdvertisingTarget);
            }
        }
        //设置完成率
        String advertisingRate = "0";
        if (BigDecimal.ZERO.compareTo(advertising.getTargetBD()) != 0 && ObjectUtil.isNotEmpty(advertising.getAccumulatedAmount())) {
            advertisingRate = new BigDecimal(advertising.getAccumulatedAmount())
                    .multiply(BigDecimal.valueOf(100))
                    .divide(advertising.getTargetBD(), 2, RoundingMode.HALF_UP)
                    .toPlainString();
        }
        advertising.setCompletionRate(advertisingRate);
        cardList.add(advertising);

        //累计收入
        CardListVo income = new CardListVo();
        income.setType("收入");
        long sum = performanceBaseInfos.stream().mapToLong(PerformanceBaseInfo::getRevenueTarget).summaryStatistics().getSum();
        income.setTargetBD(BigDecimal.valueOf(sum).divide(BigDecimal.valueOf(100000000), 2, RoundingMode.HALF_UP));
        income.setTarget(income.getTargetBD().toPlainString());

        //累计利润
        CardListVo profit = new CardListVo();
        profit.setType("利润");
        long profitAmount = performanceBaseInfos.stream().mapToLong(PerformanceBaseInfo::getProfitTarget).summaryStatistics().getSum();
        profit.setTargetBD(BigDecimal.valueOf(profitAmount).divide(BigDecimal.valueOf(100000000), 2, RoundingMode.HALF_UP));
        profit.setTarget(profit.getTargetBD().toPlainString());
        //查询实际的利润
        profit.setAccumulatedAmount("0.00");
        if (!CollectionUtils.isEmpty(contractBaseInfos)) {
            if (!CollectionUtils.isEmpty(financeProjectProfitDetails)) {
                long actualProfitAmount = financeProjectProfitDetails.stream().mapToLong(FinanceProjectProfitDetail::getTotalProfitThisYear).summaryStatistics().getSum();
                long actualIncomeAmount = financeProjectProfitDetails.stream().mapToLong(FinanceProjectProfitDetail::getTotalIncomeThisYear).summaryStatistics().getSum();
                String actualProfitTarget = BigDecimal.valueOf(actualProfitAmount).divide(BigDecimal.valueOf(100000000), 2, RoundingMode.HALF_UP).toPlainString();
                String actualIncomeTarget = BigDecimal.valueOf(actualIncomeAmount).divide(BigDecimal.valueOf(100000000), 2, RoundingMode.HALF_UP).toPlainString();
                profit.setAccumulatedAmount(actualProfitTarget);
                income.setAccumulatedAmount(actualIncomeTarget);
            }
        }
        // 设置完成率
        String profitRate = "0";
        if (BigDecimal.ZERO.compareTo(profit.getTargetBD()) != 0 && ObjectUtil.isNotEmpty(profit.getAccumulatedAmount())) {
            profitRate = new BigDecimal(profit.getAccumulatedAmount())
                    .multiply(BigDecimal.valueOf(100))
                    .divide(profit.getTargetBD(), 2, RoundingMode.HALF_UP)
                    .toPlainString();
        }

        //计算完成率
        String incomeRate = "0";
        if (BigDecimal.ZERO.compareTo(income.getTargetBD()) != 0 && ObjectUtil.isNotEmpty(income.getAccumulatedAmount())) {
            incomeRate = new BigDecimal(income.getAccumulatedAmount())
                    .multiply(BigDecimal.valueOf(100))
                    .divide(income.getTargetBD(), 2, RoundingMode.HALF_UP)
                    .toPlainString();
        }
        income.setCompletionRate(incomeRate);
        cardList.add(income);

        profit.setCompletionRate(profitRate);
        cardList.add(profit);
        rsp.setCardList(cardList);
        return "ok";
    }


    public List<PersonalPerformanceRSP> personalPerformance() {
        //TODO 暂时还没有数据
        return Collections.emptyList();
    }

    public KpiDeptShipSortPerformanceRSP deptShipSortPerformance() {
        DeptPerformanceRSP rsp = null;
        try {
            rsp = this.deptPerformance();
        } catch (MithrasException e) {
            throw e;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("获取部门业绩排名数据失败", e);
        } catch (Exception e) {
            log.error("获取部门业绩排名数据失败", e);
        }
        List<DeptShipSortPerformanceRSP> dataList = new ArrayList<>();
        KpiDeptShipSortPerformanceRSP performanceRsp = new KpiDeptShipSortPerformanceRSP();
        if (Objects.nonNull(rsp)) {
            dataList = rsp.getDataList();
            performanceRsp.setDataUpdateTime(rsp.getDataUpdateTime());
        }
        List<DeptShipSortPerformanceRSP> list = new ArrayList<>();
        if (!CollectionUtils.isEmpty(dataList)) {
            //拷贝数据
            dataList.forEach(item -> {
                DeptShipSortPerformanceRSP dto = new DeptShipSortPerformanceRSP();
                BeanUtil.copyProperties(item, dto);
                list.add(dto);
            });
        }
        performanceRsp.setDataList(list);
        //其实这里的数据和上面的数据是一样的，只是少了卡片的部分，所以只需考虑提取通用部分加工一下就好了
        return performanceRsp;
    }

    public List<DeptInSortPerformanceRSP> deptInSortPerformance() {
        return Collections.emptyList();
    }
}
