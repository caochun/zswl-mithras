package cn.zswltech.mithras.metric.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricCorpCommerceSnapshot {

    private Long clientId;

    private String industryType;

    private Long belongGroupClientId;
}
