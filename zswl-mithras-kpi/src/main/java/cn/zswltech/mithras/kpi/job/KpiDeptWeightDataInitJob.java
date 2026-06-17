package cn.zswltech.mithras.kpi.job;

import cn.zswltech.mithras.kpi.application.port.KpiDeptWeightDataInitJobPort;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author bigbear
 * @date 2025/4/16 10:04
 * @description
 */
@Slf4j
@Component
public class KpiDeptWeightDataInitJob {
    @Resource
    private KpiDeptWeightDataInitJobPort kpiDeptWeightDataInitJobPort;

    @XxlJob("kpiDeptWeightDataInitJob")
    public void kpiDeptWeightDataInitJob() {
        kpiDeptWeightDataInitJobPort.initDeptWeightData();
    }
}
