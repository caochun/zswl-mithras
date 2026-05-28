package cn.zswltech.mithras.metric.financialcloudmetric.service;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.metric.enums.risk.index.RiskMetricFrequency;
import cn.zswltech.mithras.metric.financialcloudmetric.mapper.FinancialCloudMetricMapper;
import cn.zswltech.mithras.metric.financialcloudmetric.model.FinancialCloudMetric;
import cn.zswltech.mithras.metric.financialcloudmetric.model.FinancialCloudMetricValue;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhaozhengkang
 * @description 指标计算及创建，每月1号0点执行
 * @date 2023-04-12
 */
@Service
public class FinancialCloudMetricService extends ServiceImpl<FinancialCloudMetricMapper, FinancialCloudMetric> {

    @Resource
    private FinancialCloudMetricValueService metricValueService;


    @XxlJob("financialCloudMetricCalculateHandler")
    public void financialCloudMetricCalculateHandler() {
        // 不传在参数默认计算本月的数据，传参则计算指定月份的数据
        LocalDate dataTime = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        String jobParam = XxlJobHelper.getJobParam();
        if (ObjectUtil.isNotEmpty(jobParam)) {
            dataTime = LocalDate.parse(jobParam, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    .with(TemporalAdjusters.firstDayOfMonth());
        }
        List<FinancialCloudMetricValue> metricValues = metricValueService.list(
                Wrappers.<FinancialCloudMetricValue>lambdaQuery()
                        .eq(FinancialCloudMetricValue::getDataTime, dataTime));
        if (ObjectUtil.isNotEmpty(metricValues)) {
            metricValueService.calc(dataTime);
        }
    }


    @XxlJob("financialCloudMetricJobHandler")
    public void financialCloudMetricJobHandler(LocalDate dataTime) {
        if (dataTime == null) {
            dataTime = LocalDate.now();
        }
        String jobParam = XxlJobHelper.getJobParam();
        if (ObjectUtil.isNotEmpty(jobParam)) {
            dataTime = LocalDate.parse(jobParam, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    .with(TemporalAdjusters.firstDayOfMonth());
        }
        // 创建当月的指标
        LocalDate thisMonth = dataTime.with(TemporalAdjusters.firstDayOfMonth());
        // 按项目的指标不需要创建，计算过程中动态插入
        Map<String, List<FinancialCloudMetric>> frequencyMap =
                baseMapper.selectList(Wrappers.<FinancialCloudMetric>lambdaQuery()
                                .ne(FinancialCloudMetric::getOneLevelType, "按项目"))
                        .stream().collect(Collectors.groupingBy(FinancialCloudMetric::getFrequency));
        List<FinancialCloudMetricValue> metricValues = new ArrayList<>();
        // 创建月度报送的数据
        List<FinancialCloudMetric> monthMetrics = frequencyMap.get(RiskMetricFrequency.MONTH.name());
        // 避免于都重复创建
        List<FinancialCloudMetricValue> thisMonthValues = metricValueService.list(
                Wrappers.<FinancialCloudMetricValue>lambdaQuery()
                        .eq(FinancialCloudMetricValue::getDataTime, thisMonth)
                        .in(FinancialCloudMetricValue::getMetricId,
                                monthMetrics.stream().map(FinancialCloudMetric::getId).collect(Collectors.toList())));
        if (ObjectUtil.isNotEmpty(thisMonthValues)) {
            return;
        }
        monthMetrics.forEach(monthMetric -> {
            FinancialCloudMetricValue metricValue = metricValueService.buildMetricValue(monthMetric, thisMonth);
            metricValues.add(metricValue);
        });
        // 如有必要，创建年度报送的数据
        List<FinancialCloudMetric> yearMetrics = frequencyMap.get(RiskMetricFrequency.YEAR.name());
        List<Long> yearMetricIds = yearMetrics.stream().map(FinancialCloudMetric::getId).collect(Collectors.toList());
        List<FinancialCloudMetricValue> yearMetricValues = metricValueService.list(Wrappers.<FinancialCloudMetricValue>lambdaQuery().
                in(FinancialCloudMetricValue::getMetricId, yearMetricIds)
                .eq(FinancialCloudMetricValue::getDataTime, LocalDate.of(dataTime.getYear(), 1, 1)));
        if (ObjectUtil.isEmpty(yearMetricValues)) {
            for (FinancialCloudMetric yearMetric : yearMetrics) {
                FinancialCloudMetricValue metricValue = metricValueService.buildMetricValue(yearMetric,
                        LocalDate.of(dataTime.getYear(), 1, 1));
                metricValues.add(metricValue);
            }
        }
        metricValueService.saveBatch(metricValues);
    }

    /**
     *
     */
    public Map<String, Set<String>> pulldownList() {
        List<FinancialCloudMetric> metrics = baseMapper.selectList(Wrappers.<FinancialCloudMetric>lambdaQuery());
        Set<String> metricFirstType = metrics.stream().map(FinancialCloudMetric::getMetricFirstType).collect(Collectors.toSet());
        Set<String> metricSecondType = metrics.stream().map(FinancialCloudMetric::getMetricSecondType).collect(Collectors.toSet());
        Map<String, Set<String>> res = new HashMap<>();
        res.put("metricFirstType", metricFirstType);
        res.put("metricSecondType", metricSecondType);
        return res;
    }
}