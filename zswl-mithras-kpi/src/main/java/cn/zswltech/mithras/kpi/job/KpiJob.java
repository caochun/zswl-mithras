package cn.zswltech.mithras.kpi.job;

import cn.zswltech.mithras.kpi.application.port.KpiProjectBonusJobPort;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author dingqi
 * @date 2023/6/20
 * @description
 */
@Slf4j
@Component
public class KpiJob {
    @Resource
    private KpiProjectBonusJobPort kpiProjectBonusJobPort;

    @XxlJob("calculateKpiProjectBonus")
    public void calculateKpiProjectBonus() {
        kpiProjectBonusJobPort.calculateKpiProjectBonus(XxlJobHelper.getJobParam());
    }
}
