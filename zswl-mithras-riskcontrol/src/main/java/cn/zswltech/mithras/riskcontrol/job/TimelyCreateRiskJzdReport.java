package cn.zswltech.mithras.riskcontrol.job;

import cn.zswltech.mithras.riskcontrol.report.jzd.RiskControlJzdReportTimedCreateService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;

/**
 * @author yibin
 */
@Slf4j
@Component
public class TimelyCreateRiskJzdReport {
    @Resource
    private RiskControlJzdReportTimedCreateService jzdReportService;

    @XxlJob("timelyCreateRiskJzdReport")
    public void create() {
        log.info(">>>>>>>>>>>>>>timelyCreateRiskJzdReport start");
        try {
            jzdReportService.generateQuarterlyData(LocalDate.now().minusMonths(1));
        } catch (Exception e) {
            log.error("timelyCreateRiskJzdReport Error.", e);
        }
        log.info(">>>>>>>>>>>>>>timelyCreateRiskJzdReport end");
    }
}
