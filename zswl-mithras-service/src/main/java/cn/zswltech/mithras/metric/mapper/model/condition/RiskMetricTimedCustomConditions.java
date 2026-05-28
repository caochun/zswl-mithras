package cn.zswltech.mithras.metric.mapper.model.condition;

import lombok.Data;

import java.util.List;

/**
 * @author yibin
 */
@Data
public class RiskMetricTimedCustomConditions {

    private List<String> provCodeList;

    private List<String> cityCodeList;
    private List<String> industryTypeList;
}
