package cn.zswltech.mithras.ftp.newftp.service.config;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.PageReq;
import cn.zswltech.mithras.dto.newftp.NewFtpFinancingCostPricingListRSP;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingBizTypeEnum;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingStatusEnum;
import cn.zswltech.mithras.ftp.newftp.enums.TermRange;
import cn.zswltech.mithras.fund.directfinancing.mapper.model.FundDirectFinancingBaseInfo;
import cn.zswltech.mithras.fund.directfinancing.mapper.FundDirectFinancingBaseInfoMapper;
import cn.zswltech.mithras.fund.mapper.lib.financing.FundFinancingBaseInfoLibMapper;
import cn.zswltech.mithras.fund.mapper.lib.financing.FundFinancingPlanLibMapper;
import cn.zswltech.mithras.fund.mapper.model.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.fund.mapper.model.financing.FundFinancingBaseInfoLib;
import cn.zswltech.mithras.fund.mapper.model.financing.FundFinancingPlan;
import cn.zswltech.mithras.fund.mapper.model.financing.FundFinancingPlanLib;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.basedata.service.BaseDataLprService;
import cn.zswltech.mithras.ftp.newftp.mapper.config.NewFtpFinancingCostPricingConfigMapper;
import cn.zswltech.mithras.ftp.newftp.model.config.NewFtpFinancingCostPricingConfig;
import cn.zswltech.mithras.ftp.newftp.service.job.NewFtpPricingJobService;
import cn.zswltech.mithras.ftp.newftp.service.NewFtpBaseInfoService;
import cn.zswltech.mithras.ftp.newftp.utils.DateUtil;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author yangxiong
 * @description 针对表【new_ftp_financing_cost_pricing_config(融资成本配置表)】的数据库操作Service实现
 * @createDate 2024-03-22 14:00:37
 */
@Service
public class NewFtpFinancingCostPricingConfigService extends ServiceImpl<NewFtpFinancingCostPricingConfigMapper, NewFtpFinancingCostPricingConfig> implements NewFtpPricingJobService {

    @Resource
    private FundFinancingBaseInfoLibMapper fundFinancingBaseInfoLibMapper;
    @Resource
    private FundFinancingPlanLibMapper fundFinancingPlanLibMapper;
    @Resource
    private NewFtpBaseInfoService baseInfoService;
    @Resource
    private BaseDataLprService baseDataLprService;
    @Resource
    private FundDirectFinancingBaseInfoMapper directFinancingBaseInfoMapper;
    @Resource
    private NewFtpGuaranteeCostPricingConfigService guaranteeCostPricingConfigService;

    @Override
    public NewFtpFinancingCostPricingConfig latestFinancingCostPricingConfig() {
        return this.getOne(Wrappers.<NewFtpFinancingCostPricingConfig>lambdaQuery()
                .orderByDesc(NewFtpFinancingCostPricingConfig::getMonth)
                .last(StringUtil.mysqlLimitOne()));
    }

    @Override
    public void addFinancingCostPricingConfig(LocalDate localDate) {
        this.add(localDate);
    }

    @Override
    public void addGuaranteeCostPricingConfig(LocalDate localDate) {
        guaranteeCostPricingConfigService.addConfig(localDate);
    }

    /**
     * 添加或者更新
     */
    @Transactional(rollbackFor = Throwable.class)
    public void add(LocalDate localDate) {
        List<NewFtpFinancingCostPricingConfig> adds = new ArrayList<>();
        LocalDate nowMonth = localDate.with(TemporalAdjusters.firstDayOfMonth());
        //查询已有数据
        Map<String, NewFtpFinancingCostPricingConfig> range2bean = baseMapper.selectList(Wrappers.<NewFtpFinancingCostPricingConfig>lambdaQuery()
                        .eq(NewFtpFinancingCostPricingConfig::getMonth, nowMonth))
                .stream().collect(Collectors.toMap(NewFtpFinancingCostPricingConfig::getTermRange,
                        Function.identity(), (a, b) -> a));

        //加权平均成本：（∑1+ ∑2）÷ ∑（间融的【融资金额】+直融的【融资金额】
        //月度
        LocalDate monthBegin = localDate.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate monthEnd = localDate.with(TemporalAdjusters.lastDayOfMonth());
        handlerEntity(monthBegin, monthEnd, adds, nowMonth, range2bean, "month");
        NewFtpFinancingCostPricingConfigService costPricingConfigService = SpringContextHolder.getBean(NewFtpFinancingCostPricingConfigService.class);
        if (CollUtil.isNotEmpty(adds)) {
            costPricingConfigService.saveOrUpdateBatch(adds);
        }

        List<NewFtpFinancingCostPricingConfig> list = costPricingConfigService.list(Wrappers.<NewFtpFinancingCostPricingConfig>lambdaQuery()
                .eq(NewFtpFinancingCostPricingConfig::getMonth, nowMonth));

        LocalDate quarterBegin = DateUtil.getQuarterBegin(nowMonth);
        LocalDate quarterEnd = DateUtil.getQuarterEnd(nowMonth);
        adds = handlerEntity(quarterBegin, quarterEnd, list, nowMonth, range2bean, "quarter");
        if (CollUtil.isNotEmpty(adds)) {
            costPricingConfigService.saveOrUpdateBatch(adds);
        }

        LocalDate yearBegin = nowMonth.with(TemporalAdjusters.firstDayOfYear());
        LocalDate yearEnd = nowMonth.with(TemporalAdjusters.lastDayOfMonth());
        adds = handlerEntity(yearBegin, yearEnd, list, nowMonth, range2bean, "year");
        if (CollUtil.isNotEmpty(adds)) {
            costPricingConfigService.saveOrUpdateBatch(adds);
        }
    }

    private List<NewFtpFinancingCostPricingConfig> handlerEntity(LocalDate beginDate, LocalDate endDate, List<NewFtpFinancingCostPricingConfig> adds,
                                                                 LocalDate nowMonth, Map<String, NewFtpFinancingCostPricingConfig> range2bean, String type) {
        AtomicReference<BigDecimal> yearOneYearCost = new AtomicReference<>(BigDecimal.ZERO);
        AtomicReference<BigDecimal> yearOneYearCostTotal = new AtomicReference<>(BigDecimal.ZERO);
        AtomicReference<BigDecimal> yearOneYear2ThreeCost = new AtomicReference<>(BigDecimal.ZERO);
        AtomicReference<BigDecimal> yearOneYear2ThreeCostTotal = new AtomicReference<>(BigDecimal.ZERO);
        AtomicReference<BigDecimal> yearThree2FiveYearCost = new AtomicReference<>(BigDecimal.ZERO);
        AtomicReference<BigDecimal> yearThree2FiveYearCostTotal = new AtomicReference<>(BigDecimal.ZERO);

        //查询间接融资模块
        List<FundFinancingBaseInfoLib> yearFundFinancingBaseInfoLibs = fundFinancingBaseInfoLibMapper.selectList(Wrappers.<FundFinancingBaseInfoLib>lambdaQuery()
//                .eq(FundFinancingBaseInfo::getFinancingStatus, FundFinancingStatusEnum.CARRY_INTEREST.name())
                .between(FundFinancingBaseInfo::getActualLoanDate, beginDate, endDate)
                .notIn(FundFinancingBaseInfo::getBusinessType, Arrays.asList(
                        FundFinancingBizTypeEnum.BANK_ACCEPTANCE.name(),
                        FundFinancingBizTypeEnum.LETTER_OF_CREDIT.name(),
                        FundFinancingBizTypeEnum.COMMERCE_ACCEPTANCE.name()))
                .eq(FundFinancingBaseInfoLib::getVersionType, VersionTypeConstants.NORMAL));

        Map<Long, FundFinancingBaseInfoLib> yearFundFinancingBaseInfoLibMap = new HashMap<>(8);
        if (CollUtil.isNotEmpty(yearFundFinancingBaseInfoLibs)) {
            yearFundFinancingBaseInfoLibs.stream().collect(Collectors.groupingBy(FundFinancingBaseInfoLib::getOriginId))
                    .forEach((id, libs) -> {
                        libs.sort(Comparator.comparing(FundFinancingBaseInfoLib::getVersion));
                        yearFundFinancingBaseInfoLibMap.put(id, libs.get(libs.size() - 1));
                    });
        }
        List<FundFinancingPlanLib> yearPlanLibs = new ArrayList<>();
        yearFundFinancingBaseInfoLibMap.forEach((k, v) -> {
            FundFinancingPlanLib fundFinancingPlanLib = fundFinancingPlanLibMapper.selectOne(Wrappers.<FundFinancingPlanLib>lambdaQuery()
                    .eq(FundFinancingPlanLib::getVersion, v.getVersion())
                    .eq(FundFinancingPlan::getFinancingId, k)
                    .eq(FundFinancingPlanLib::getVersionType, VersionTypeConstants.NORMAL)
                    .last(StringUtil.mysqlLimitOne()));
            yearPlanLibs.add(fundFinancingPlanLib);
        });

        if (CollUtil.isNotEmpty(yearPlanLibs)) {
            yearPlanLibs.stream().filter(a -> Objects.nonNull(a.getFinancingMonth()))
                    .filter(a -> Objects.nonNull(a.getFinancingAmount()))
                    .filter(a -> Objects.nonNull(a.getComprehensiveInterestRate()))
                    .forEach(one -> {
                        if (0 < one.getFinancingMonth() && one.getFinancingMonth() <= 12) {
                            yearOneYearCost.updateAndGet(v -> yearOneYearCost.get().add(BigDecimal.valueOf(one.getFinancingAmount())
                                    .multiply(BigDecimal.valueOf(one.getComprehensiveInterestRate()))));
                            yearOneYearCostTotal.updateAndGet(v -> yearOneYearCostTotal.get().add(BigDecimal.valueOf(one.getFinancingAmount())));
                        }
                        if (12 < one.getFinancingMonth() && one.getFinancingMonth() <= 36) {
                            yearOneYear2ThreeCost.updateAndGet(v -> yearOneYear2ThreeCost.get().add(BigDecimal.valueOf(one.getFinancingAmount())
                                    .multiply(BigDecimal.valueOf(one.getComprehensiveInterestRate()))));
                            yearOneYear2ThreeCostTotal.updateAndGet(v -> yearOneYear2ThreeCostTotal.get().add(BigDecimal.valueOf(one.getFinancingAmount())));
                        }
                        if (one.getFinancingMonth() > 36) {
                            yearThree2FiveYearCost.updateAndGet(v -> yearThree2FiveYearCostTotal.get().add(BigDecimal.valueOf(one.getFinancingAmount())
                                    .multiply(BigDecimal.valueOf(one.getComprehensiveInterestRate()))));
                            yearThree2FiveYearCostTotal.updateAndGet(v -> yearThree2FiveYearCostTotal.get().add(BigDecimal.valueOf(one.getFinancingAmount())));
                        }
                    });
        }

        //查询直接融资模块
        List<FundDirectFinancingBaseInfo> yearInfoList = directFinancingBaseInfoMapper.selectList(Wrappers.<FundDirectFinancingBaseInfo>lambdaQuery()
//                .eq(FundDirectFinancingBaseInfo::getFinancingStatus, FundFinancingStatusEnum.CARRY_INTEREST.name())
                .between(FundDirectFinancingBaseInfo::getDurationFrom, beginDate, endDate));
        yearInfoList = yearInfoList.stream().filter(a -> Objects.nonNull(a.getDurationFrom()))
                .filter(a -> Objects.nonNull(a.getDurationTo()))
                .collect(Collectors.toList());

        if (CollUtil.isNotEmpty(yearInfoList)) {
            yearInfoList.forEach(baseInfo -> {
                long betweenMonths = LocalDateTimeUtil.between(baseInfo.getDurationFrom().atStartOfDay(),
                        baseInfo.getDurationTo().atStartOfDay(), ChronoUnit.MONTHS);
                if (0 < betweenMonths && betweenMonths <= 12) {
                    yearOneYearCost.updateAndGet(v -> yearOneYearCost.get()
                            .add(BigDecimal.valueOf(baseInfo.getFinancingAmount())
                                    .multiply(BigDecimal.valueOf(10000))
                                    .multiply(BigDecimal.valueOf(baseInfo.getComprehensiveFinancingCost()))));
                    yearOneYearCostTotal.updateAndGet(v -> yearOneYearCostTotal.get()
                            .add(BigDecimal.valueOf(baseInfo.getFinancingAmount())
                            .multiply(BigDecimal.valueOf(10000))));
                }
                if (12 < betweenMonths && betweenMonths <= 36) {
                    yearOneYear2ThreeCost.updateAndGet(v -> yearOneYear2ThreeCost.get()
                            .add(BigDecimal.valueOf(baseInfo.getFinancingAmount())
                                    .multiply(BigDecimal.valueOf(10000))
                                    .multiply(BigDecimal.valueOf(baseInfo.getComprehensiveFinancingCost()))));
                    yearOneYear2ThreeCostTotal.updateAndGet(v -> yearOneYear2ThreeCostTotal.get()
                            .add(BigDecimal.valueOf(baseInfo.getFinancingAmount())
                            .multiply(BigDecimal.valueOf(10000))));
                }
                if (betweenMonths > 36) {
                    yearThree2FiveYearCost.updateAndGet(v -> yearThree2FiveYearCostTotal.get()
                            .add(BigDecimal.valueOf(baseInfo.getFinancingAmount())
                                    .multiply(BigDecimal.valueOf(10000))
                                    .multiply(BigDecimal.valueOf(baseInfo.getComprehensiveFinancingCost()))));
                    yearThree2FiveYearCostTotal.updateAndGet(v -> yearThree2FiveYearCostTotal.get()
                            .add(BigDecimal.valueOf(baseInfo.getFinancingAmount())
                            .multiply(BigDecimal.valueOf(10000))));
                }
            });
        }

        //月均值
        long oneYearAvg = getAvg(yearOneYearCost, yearOneYearCostTotal);
        long oneYear2ThreeAvg = getAvg(yearOneYear2ThreeCost, yearOneYear2ThreeCostTotal);
        long three2FiveYearAvg = getAvg(yearThree2FiveYearCost, yearThree2FiveYearCostTotal);

        List<NewFtpFinancingCostPricingConfig> list = new ArrayList<>();

        list.add(buildNewFtpFinancingCost(adds, TermRange.ONE_YEAR.name(), nowMonth, oneYearAvg, type, range2bean.get(TermRange.ONE_YEAR.name())));
        list.add(buildNewFtpFinancingCost(adds, TermRange.ONE_TO_THREE_YEARS.name(), nowMonth, oneYear2ThreeAvg, type, range2bean.get(TermRange.ONE_TO_THREE_YEARS.name())));
        list.add(buildNewFtpFinancingCost(adds, TermRange.MORE_THAN_THREE_YEARS.name(), nowMonth, three2FiveYearAvg, type, range2bean.get(TermRange.MORE_THAN_THREE_YEARS.name())));

        adds.clear();
        adds.addAll(list);
        return adds;
    }

    private long getAvg(AtomicReference<BigDecimal> cost, AtomicReference<BigDecimal> costTotal) {
        if (ObjectUtil.isEmpty(cost.get()) || cost.get().equals(BigDecimal.ZERO) || costTotal.get().equals(BigDecimal.ZERO)) {
            return 0;
        }
        return cost.get().divide(costTotal.get(), 20, RoundingMode.HALF_UP).longValue();
    }

    private NewFtpFinancingCostPricingConfig buildNewFtpFinancingCost(List<NewFtpFinancingCostPricingConfig> adds, String name, LocalDate nowMonth, Long avg, String type,
                                                                      NewFtpFinancingCostPricingConfig oldPricing) {

        Map<String, NewFtpFinancingCostPricingConfig> configMap = new HashMap<>();
        if (adds.size() >= 3) {
            configMap = adds.stream().collect(Collectors.toMap(NewFtpFinancingCostPricingConfig::getTermRange, Function.identity(), (a, b) -> a));
        }
        NewFtpFinancingCostPricingConfig ftpFinancingCostPricing = adds.size() < 3 ? new NewFtpFinancingCostPricingConfig() : configMap.get(name);
        //计算
        ftpFinancingCostPricing.setMonth(nowMonth);
        int currentAverage = Integer.parseInt(Optional.ofNullable(avg).orElse(0L) + "");
        if ("month".equals(type)) {
            ftpFinancingCostPricing.setCurrentAverage(currentAverage);
        }
        if ("quarter".equals(type)) {
            ftpFinancingCostPricing.setCurrentQuarterAverage(currentAverage);
        }
        if ("year".equals(type)) {
            ftpFinancingCostPricing.setAnnualAverage(currentAverage);
        }
        ftpFinancingCostPricing.setTermRange(name);
        if (ObjectUtil.isNotEmpty(oldPricing)) {
            ftpFinancingCostPricing.setHandCurrentAverage(oldPricing.getHandCurrentAverage());
            ftpFinancingCostPricing.setHandCurrentQuarterAverage(oldPricing.getHandCurrentQuarterAverage());
            ftpFinancingCostPricing.setHandAnnualAverage(oldPricing.getHandAnnualAverage());
            ftpFinancingCostPricing.setId(oldPricing.getId());
        }
        ftpFinancingCostPricing.setFtpPricing(cal(ftpFinancingCostPricing));
        //上月新增加权平均-上月当年累计新增加权平均
        return ftpFinancingCostPricing;
    }

    public PageR<NewFtpFinancingCostPricingListRSP> list(PageReq req) {
        //查询条数
        Page<NewFtpFinancingCostPricingConfig> newFtpLprPricingPage = baseMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<NewFtpFinancingCostPricingConfig>lambdaQuery()
                .select(NewFtpFinancingCostPricingConfig::getMonth)
                .groupBy(NewFtpFinancingCostPricingConfig::getMonth)
                .orderByDesc(NewFtpFinancingCostPricingConfig::getMonth));
        //查询所有
        List<LocalDate> months = newFtpLprPricingPage.getRecords().stream().map(NewFtpFinancingCostPricingConfig::getMonth).collect(Collectors.toList());
        if (ObjectUtil.isEmpty(months)) {
            return null;
        }
        List<NewFtpFinancingCostPricingConfig> newFtpFinancingCostPricings = baseMapper.selectList(Wrappers.<NewFtpFinancingCostPricingConfig>lambdaQuery()
                .in(NewFtpFinancingCostPricingConfig::getMonth, months));
        Map<LocalDate, List<NewFtpFinancingCostPricingConfig>> allMap = newFtpFinancingCostPricings.stream().collect(Collectors.groupingBy(NewFtpFinancingCostPricingConfig::getMonth));
        List<NewFtpFinancingCostPricingListRSP> rspList = new ArrayList<>();
        newFtpLprPricingPage.getRecords().forEach(newFtpLprCost -> {
            NewFtpFinancingCostPricingListRSP rsp = new NewFtpFinancingCostPricingListRSP();
            rsp.setMonth(newFtpLprCost.getMonth());
            rsp.setBodyMap(BeanUtil.copyToList(allMap.get(newFtpLprCost.getMonth()), NewFtpFinancingCostPricingListRSP.NewFtpFinancingBody.class).stream().collect(Collectors.toMap(NewFtpFinancingCostPricingListRSP.NewFtpFinancingBody::getTermRange, e -> e)));
            rspList.add(rsp);
        });
        return PageR.of(rspList, newFtpLprPricingPage.getTotal(), newFtpLprPricingPage.getCurrent(), newFtpLprPricingPage.getTotal());
    }

    public Map<String, Integer> getFtpPricing(LocalDate month) {
        Map<String, NewFtpFinancingCostPricingConfig> newFtpFinancingCostPricingMap = baseMapper.selectList(Wrappers.<NewFtpFinancingCostPricingConfig>lambdaQuery()
                .eq(NewFtpFinancingCostPricingConfig::getMonth, month)).stream().collect(Collectors.toMap(NewFtpFinancingCostPricingConfig::getTermRange, Function.identity(), (a, b) -> a));
        Map<String, Integer> range2valueMap = new HashMap<>();
        NewFtpFinancingCostPricingConfig ftpOneFinancingCostPricing = newFtpFinancingCostPricingMap.get(TermRange.ONE_YEAR.name());
        NewFtpFinancingCostPricingConfig ftpOne2ThreeFinancingCostPricing = newFtpFinancingCostPricingMap.get(TermRange.ONE_TO_THREE_YEARS.name());
        NewFtpFinancingCostPricingConfig ftpThree2FiveFinancingCostPricing = newFtpFinancingCostPricingMap.get(TermRange.MORE_THAN_THREE_YEARS.name());
        if (ObjectUtil.isNotEmpty(ftpOneFinancingCostPricing)) {
            range2valueMap.put(TermRange.ONE_YEAR.name(), cal(ftpOneFinancingCostPricing));
        }
        if (ObjectUtil.isNotEmpty(ftpOne2ThreeFinancingCostPricing)) {
            range2valueMap.put(TermRange.ONE_TO_THREE_YEARS.name(), cal(ftpOne2ThreeFinancingCostPricing));
        }
        if (ObjectUtil.isNotEmpty(ftpThree2FiveFinancingCostPricing)) {
            range2valueMap.put(TermRange.MORE_THAN_THREE_YEARS.name(), cal(ftpThree2FiveFinancingCostPricing));
        }
        return range2valueMap;
    }

    private Integer null2Zero(Integer in) {
        return ObjectUtil.isNull(in) ? 0 : in;
    }

    private Integer cal(NewFtpFinancingCostPricingConfig ftpFinancingCostPricing) {
        Integer num = null2Zero(ObjectUtil.isEmpty(ftpFinancingCostPricing.getHandCurrentAverage()) ? ftpFinancingCostPricing.getCurrentAverage() :
                ftpFinancingCostPricing.getHandCurrentAverage()) - null2Zero(ObjectUtil.isEmpty(ftpFinancingCostPricing.getHandAnnualAverage()) ?
                ftpFinancingCostPricing.getAnnualAverage() : ftpFinancingCostPricing.getHandAnnualAverage());
        if (num == 0) {
            return 0;
        } else if (num > 0) {
            return 3000;
        } else {
            return -3000;
        }
    }
}




