package cn.zswltech.mithras.service.service.riskcontrol.job;

import cn.zswltech.mithras.service.mapper.model.riskcontrol.RiskControlStrategy;
import cn.zswltech.mithras.service.mapper.model.riskcontrol.RiskControlStrategySnapshot;
import cn.zswltech.mithras.service.service.riskcontrol.RiskControlStrategyService;
import cn.zswltech.mithras.service.service.riskcontrol.RiskControlStrategySnapshotService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @description: 11点30执行，保留当天快照
 * @author: zhaozhengkang
 * @date: 2023/6/6 11:41
 */
@Slf4j
@Component
public class RiskControlStrategySnapshotJob {

    @Resource
    private RiskControlStrategySnapshotService riskControlStrategySnapshotService;
    @Resource
    private RiskControlStrategyService riskControlStrategyService;

    @XxlJob("riskControlStrategySnapshotJob")
    public void fullComputeMetricJobHandler() throws Exception {
        log.info("riskControlStrategySnapshotJob start");
        try {
            List<RiskControlStrategy> allCurrent = riskControlStrategyService.list();
            List<RiskControlStrategySnapshot> snapshots = new ArrayList<>();
            for (RiskControlStrategy current : allCurrent) {
                RiskControlStrategySnapshot snapshot = new RiskControlStrategySnapshot();
                snapshot.setMetricId(current.getId());
                snapshot.setDate(LocalDate.now());
                snapshot.setValueOne(current.getCurrentValueOne());
                snapshot.setValueTwo(current.getCurrentValueTwo());
                snapshot.setQuickContext(current.getQuickContext());
                snapshots.add(snapshot);
            }
            riskControlStrategySnapshotService.saveBatch(snapshots);
        } catch (Throwable t) {
            log.error("riskControlStrategySnapshotJob error", t);
        }
    }

}
