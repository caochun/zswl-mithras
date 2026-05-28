package cn.zswltech.mithras.metric.financialcloudmetric.mapper;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/4/18 14:45
 */
@Data
public class FinancialCloudMetricValueQuery {
    private String metricName;
    private LocalDate dataTime;
    private LocalDate firstMonth;

    private String metricFirstType;
    private String metricSecondType;
    private String frequency;

    private List<Long> metricIds;
}
