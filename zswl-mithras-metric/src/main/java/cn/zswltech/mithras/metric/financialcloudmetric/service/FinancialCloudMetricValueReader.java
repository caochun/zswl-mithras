package cn.zswltech.mithras.metric.financialcloudmetric.service;

import cn.zswltech.mithras.metric.financialcloudmetric.model.FinancialCloudMetricValue;

import java.time.LocalDate;

public interface FinancialCloudMetricValueReader {

    FinancialCloudMetricValue getMetricValue(String metricCode, LocalDate dataTime);
}
