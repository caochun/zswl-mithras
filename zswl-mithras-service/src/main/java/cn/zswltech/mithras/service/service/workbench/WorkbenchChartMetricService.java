package cn.zswltech.mithras.service.service.workbench;

import cn.hutool.core.util.ObjectUtil;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

/**
 * @description:
 * @author: zhaozhengkang
 * @date: 2023/5/12 12:19
 */
@Service
@Slf4j
public class WorkbenchChartMetricService {

    @Resource
    private WorkbenchBarChartMetricService barChartMetricService;
    @Resource
    private WorkbenchRadarChartMetricService radarChartMetricService;
    @Resource
    private WorkbenchOverallReturnRateMetricService overallReturnRateMetricService;
    @Resource
    private WorkbenchCardMetricService cardMetricService;

    @XxlJob("workbenchMetricJobHandler")
    public void workbenchMetricJobHandler() {
        log.info("workbenchMetricJobHandler 执行");
        String jobParam = XxlJobHelper.getJobParam();
        if (ObjectUtil.isNotEmpty(jobParam)) {
            LocalDate dateTime = LocalDate.parse(jobParam, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    .with(TemporalAdjusters.firstDayOfMonth());
            overallReturnRateMetricService.calculate(dateTime);
        } else {
            barChartMetricService.calculate();
            overallReturnRateMetricService.calculate(LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()));
            radarChartMetricService.calculate();
            cardMetricService.calculate();
        }

    }
}
