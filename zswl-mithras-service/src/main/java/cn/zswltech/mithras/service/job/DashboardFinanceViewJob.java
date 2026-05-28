package cn.zswltech.mithras.service.job;

import cn.zswltech.mithras.finance.view.service.DashboardFvCardSnapshotService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

/**
 * @author bigbear
 * @version 1.0
 * @description 每个月初生成上个月最后一天的数据
 * @since 2025/9/3 17:00
 **/
@Slf4j
@Component
public class DashboardFinanceViewJob {

    @Resource
    private DashboardFvCardSnapshotService dashboardFvCardSnapshotService;

    @XxlJob("DashboardFinanceViewJob")
    public void dashboardFinanceViewJob() {
        log.info("DashboardFinanceViewJob start..............");
        try {
//            dashboardFvCardSnapshotService.generate(LocalDate.now().plusMonths(1).with(TemporalAdjusters.lastDayOfMonth()));
            dashboardFvCardSnapshotService.generate(LocalDate.now());
            log.info("DashboardFinanceViewJob end..............");
        }catch (Exception e) {
            log.error("DashboardFinanceViewJob error: {}", e.getMessage(), e);
        }
    }
}
