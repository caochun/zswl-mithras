package cn.zswltech.mithras.metric.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetricCustomerSnapshot {

    private Long id;

    private String clientName;

    private String uscCode;
}
