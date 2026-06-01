package cn.zswltech.mithras.metric.mapper.model.condition;

import lombok.Data;

import java.time.LocalDate;

/**
 * @author yibin
 */
@Data
public class RiskMetricValueListConditions {
    private String metricName;
    private String metricCode;
    private Boolean needReport;
    private LocalDate dataTime;
}
