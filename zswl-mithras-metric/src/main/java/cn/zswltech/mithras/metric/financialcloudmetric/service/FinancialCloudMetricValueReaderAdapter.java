package cn.zswltech.mithras.metric.financialcloudmetric.service;

import cn.zswltech.mithras.metric.financialcloudmetric.model.FinancialCloudMetricValue;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;

@Component
public class FinancialCloudMetricValueReaderAdapter implements FinancialCloudMetricValueReader {

    @Resource
    private FinancialCloudMetricValueService metricValueService;

    @Override
    public FinancialCloudMetricValue getMetricValue(String metricCode, LocalDate dataTime) {
        return metricValueService.getMetricValue(metricCode, dataTime);
    }
}
