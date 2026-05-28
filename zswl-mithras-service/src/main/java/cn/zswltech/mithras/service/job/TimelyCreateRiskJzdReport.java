package cn.zswltech.mithras.service.job;

import cn.zswltech.mithras.service.service.riskcontrol.RiskControlJzdReportService;
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
    private RiskControlJzdReportService jzdReportService;

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
