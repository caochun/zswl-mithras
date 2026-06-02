package cn.zswltech.mithras.riskcontrol.eventbus;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * @author zhaozhengkang
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MetricComputeEvent extends SubscribeEvent {
    /**
     * 财报因子查询日期
     */
    private LocalDate factorQueryDate;
    /**
     * 计算的快照日期
     */
    private LocalDate snapshotDate;
}
