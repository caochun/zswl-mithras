package cn.zswltech.mithras.metric.controller;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.metric.MetricSelectApi;
import cn.zswltech.mithras.dto.SelectRSP;
import cn.zswltech.mithras.metric.enums.risk.index.*;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yibin
 */
@RestController
public class MetricSelectController implements MetricSelectApi {

    @Override
    public R<Map<String, List<SelectRSP>>> allSelect() {
        Map<String, List<SelectRSP>> m = new HashMap<>(8);
        m.put("riskMetricOrg", Arrays.stream(RiskMetricOrg.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList()));
        m.put("riskMetricCurrency", Arrays.stream(RiskMetricCurrency.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList()));
        m.put("riskMetricFrequency", Arrays.stream(RiskMetricFrequency.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList()));
        m.put("riskMetricType", Arrays.stream(RiskMetricType.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList()));
        m.put("riskMetricUnit", Arrays.stream(RiskMetricUnit.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList()));
        m.put("riskMetricDataSource", Arrays.stream(RiskMetricDataSource.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList()));
        m.put("riskMetricStatus", Arrays.stream(RiskMetricStatus.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList()));
        m.put("riskMetricLevel5", Arrays.stream(RiskMetricLevel5.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList()));
        m.put("riskMetricFactorTable", Arrays.stream(RiskMetricFactorTable.values()).map(e -> new SelectRSP(e.display, e.name())).collect(Collectors.toList()));

        return R.ok(m);
    }


}
