package cn.zswltech.mithras.metric.job;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.metric.service.RiskMetricService;
import cn.zswltech.mithras.metric.emit.MetricEmitter;
import cn.zswltech.mithras.metric.enums.risk.index.RiskMetricDataSource;
import cn.zswltech.mithras.metric.enums.risk.index.RiskMetricStatus;
import cn.zswltech.mithras.metric.mapper.model.RiskMetric;
import cn.zswltech.mithras.metric.mapper.model.RiskMetricValue;
import cn.zswltech.mithras.metric.service.RiskMetricValueService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.hutool.core.text.CharSequenceUtil.isNotBlank;
import static cn.zswltech.mithras.metric.enums.risk.index.RiskMetricFrequency.*;

/**
 * @author yibin
 */
@Slf4j
@Component
public class MonthlyTaskGenerator {
    @Resource
    RiskMetricService metricService;
    @Resource
    private RiskMetricValueService metricValueService;
    @Resource
    private MetricEmitter metricEmitter;

    @XxlJob("monthlyMetricTaskGenerate")
    @Transactional(rollbackFor = Exception.class)
    public void generate() {
        log.info("每月生成指标任务记录-定时器开始执行");
        try {
            List<RiskMetric> allRiskMetric = metricService.list();
            LocalDate month = LocalDate.now().minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());

            //指定month
            String param = XxlJobHelper.getJobParam();
            if (isNotBlank(param)) {
                try {
                    month = LocalDateTimeUtil.parse(param, "yyyy-MM").toLocalDate();
                } catch (Exception e) {
                    log.error("指定月份生成任务失败，传入的月份格式错误。param str:{}", param);
                    return;
                }
            }
            List<RiskMetricValue> reportedList = metricValueService.list(Wrappers.<RiskMetricValue>lambdaQuery()
                    .eq(RiskMetricValue::getDataTime, month)
                    .eq(RiskMetricValue::getStatus, RiskMetricStatus.REPORTED.name())
            );
            if (!reportedList.isEmpty()) {
                log.error("{}指标填报记录已经报送成功，不允许重新生成", month.format(DateTimeFormatter.ofPattern("yyyy-MM")));
                return;
            }
            //
            LocalDate finalMonth = month;
            int nowMonth = LocalDate.now().getMonthValue();
            Set<String> requiredMetrics = metricEmitter.requiredMetrics(month);
            List<RiskMetricValue> list = allRiskMetric.parallelStream().map(e -> {
                String frequency = e.getFrequency();
                if (SEASON.name().equals(frequency) && nowMonth % 3 != 0) {
                    return null;
                }
                if (HALF_YEAR.name().equals(frequency) && nowMonth % 6 != 0) {
                    return null;
                }
                if (YEAR.name().equals(frequency) && nowMonth % 12 != 0) {
                    return null;
                }
                RiskMetricValue v = new RiskMetricValue();
                v.setRiskMetricId(e.getId());
                v.setMetricCode(e.getMetricCode());
                v.setDataTime(finalMonth);
                v.setDataSource(RiskMetricDataSource.AUTO.name());
                v.setStatus(RiskMetricStatus.PEND_FILL.name());
                if (requiredMetrics.contains(v.getMetricCode())) {
                    v.setNeedReport(true);
                }
                return v;
            }).filter(ObjectUtil::isNotNull).collect(Collectors.toList());

            metricValueService.remove(Wrappers.<RiskMetricValue>lambdaQuery().eq(RiskMetricValue::getDataTime, month));
            metricValueService.saveBatch(list);
        } catch (Exception e) {
            log.error("生成指标任务失败", e);
        }
        log.info("每月生成指标任务记录-定时器执行结束");
    }
}
