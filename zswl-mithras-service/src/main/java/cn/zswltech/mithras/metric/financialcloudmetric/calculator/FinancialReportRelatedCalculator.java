package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.zswltech.mithras.metric.mapper.RiskMetricFactorMapper;
import cn.zswltech.mithras.metric.mapper.model.RiskMetricFactor;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/4/24 15:42
 */
public abstract class FinancialReportRelatedCalculator implements FinancialCloudMetricCalculator {

    @Resource
    private RiskMetricFactorMapper metricFactorMapper;

    protected RiskMetricFactor getFactor(String name, String table, LocalDate dateTime) {
        RiskMetricFactor riskMetricFactor = metricFactorMapper.selectOne(Wrappers.<RiskMetricFactor>lambdaQuery()
                .eq(RiskMetricFactor::getFactorName, name)
                .eq(RiskMetricFactor::getFactorTable, table)
                .le(RiskMetricFactor::getFactorDate, dateTime.with(TemporalAdjusters.lastDayOfMonth()))
                .ge(RiskMetricFactor::getFactorDate, dateTime.with(TemporalAdjusters.firstDayOfMonth()))
                .last("limit 1"));
        if (riskMetricFactor == null) {
            throw new MissingFactorException("财报" + table + "缺少信息:" + name + "，请补充。");
        }
        return riskMetricFactor;
    }
}
