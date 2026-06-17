package cn.zswltech.mithras.metric.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RiskStrategyCurrentValueSnapshot {

    private String metricCode;

    private Long currentValueOne;

    private String valueUnitOne;
}
