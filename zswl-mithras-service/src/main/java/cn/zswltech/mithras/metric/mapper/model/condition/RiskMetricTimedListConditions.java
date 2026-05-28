package cn.zswltech.mithras.metric.mapper.model.condition;

import lombok.Data;

import java.time.LocalDate;

/**
 * @author yibin
 */
@Data
public class RiskMetricTimedListConditions {

    private LocalDate dataTime;
    private String projType;
    private String industryType;
    private String level5Type;
    private String clientName;
    private String belongGroupName;
    private Boolean related;
}
