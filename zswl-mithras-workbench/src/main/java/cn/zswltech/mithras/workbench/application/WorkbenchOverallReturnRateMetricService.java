package cn.zswltech.mithras.workbench.application;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.workbench.WorkbenchMetricReq;
import cn.zswltech.mithras.dto.workbench.chart.LineBarChartValueVO;
import cn.zswltech.mithras.dto.workbench.chart.sub.ChartBaseDataVO;
import cn.zswltech.mithras.dto.workbench.chart.sub.ChartDataVO;
import cn.zswltech.mithras.riskcontrol.common.RiskControlIndustryClassify;
import cn.zswltech.mithras.workbench.application.job.WorkbenchOverallReturnRateMetricCalculator;
import cn.zswltech.mithras.workbench.domain.enums.WorkbenchMetricDeptScope;
import cn.zswltech.mithras.workbench.infrastructure.persistence.mapper.WorkbenchOverallReturnRateMetricMapper;
import cn.zswltech.mithras.workbench.infrastructure.persistence.mapper.model.WorkbenchOverallReturnRateMetric;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author zhaozhengkang
 * @description 工作台-项目整体收益率指标
 * @date 2023-05-09
 */
@Service
public class WorkbenchOverallReturnRateMetricService
        extends ServiceImpl<WorkbenchOverallReturnRateMetricMapper, WorkbenchOverallReturnRateMetric>
        implements WorkbenchOverallReturnRateMetricCalculator {
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");
    private static final Map<String, List<String>> INDUSTRY_MAP = new HashMap<>();

    static {
        INDUSTRY_MAP.put("PUBLIC_UTILITIES", Collections.singletonList("PUBLIC_UTILITIES"));
        INDUSTRY_MAP.put("CIVIL_CONSUMPTION", Arrays.asList("CIVIL_CONSUMPTION", "TRAVEL"));
        INDUSTRY_MAP.put("OTHER", new ArrayList<>());
    }

    @Resource
    private WorkbenchOverallReturnRatePort overallReturnRatePort;

    public LineBarChartValueVO deptReturnRateChart(WorkbenchMetricReq req) {
        LocalDate thisMonth = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        LocalDate start = thisMonth.minusMonths(11);
        Map<String, List<WorkbenchOverallReturnRateMetric>> deptGroup = baseMapper.selectList(
                        Wrappers.<WorkbenchOverallReturnRateMetric>lambdaQuery()
                                .ge(WorkbenchOverallReturnRateMetric::getMonth, start)
                                .isNotNull(WorkbenchOverallReturnRateMetric::getDeptSocp)).stream()
                .collect(Collectors.groupingBy(WorkbenchOverallReturnRateMetric::getDeptSocp));

        List<ChartDataVO> data = new ArrayList<>();
        deptGroup.forEach((dept, metrics) -> {
            List<ChartBaseDataVO> baseDataVos = metrics.stream().map(metric -> new ChartBaseDataVO(
                            month(metric.getMonth()), metric.getValue(), null, "%"))
                    .collect(Collectors.toList());
            String dataType;
            if ("ZSZL".equals(dept)) {
                dataType = "公司整体";
            } else {
                dataType = WorkbenchMetricDeptScope.valueOf(dept).display();
            }
            baseDataVos.sort(Comparator.comparing(ChartBaseDataVO::getName));
            data.add(new ChartDataVO(dataType, "line", baseDataVos));

        });
        return new LineBarChartValueVO("部门项目收益率曲线图", data);
    }

    public LineBarChartValueVO projectTypeReturnRateChart(WorkbenchMetricReq req) {
        LocalDate thisMonth = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        LocalDate start = thisMonth.minusMonths(11);
        Map<String, List<WorkbenchOverallReturnRateMetric>> projectTypeGroup = baseMapper.selectList(
                        Wrappers.<WorkbenchOverallReturnRateMetric>lambdaQuery()
                                .ge(WorkbenchOverallReturnRateMetric::getMonth, start)
                                .isNotNull(WorkbenchOverallReturnRateMetric::getProjectType)).stream()
                .collect(Collectors.groupingBy(WorkbenchOverallReturnRateMetric::getProjectType));

        List<ChartDataVO> data = new ArrayList<>();
        projectTypeGroup.forEach((projectType, metrics) -> {
            List<ChartBaseDataVO> baseDataVos = metrics.stream().map(metric -> new ChartBaseDataVO(
                            month(metric.getMonth()), metric.getValue(), null, "%"))
                    .collect(Collectors.toList());
            String dataType;
            if ("ZSZL".equals(projectType)) {
                dataType = "公司整体";
            } else if ("OTHER".equals(projectType)) {
                dataType = "其他";
            } else {
                dataType = RiskControlIndustryClassify.valueOf(projectType).display();
            }
            baseDataVos.sort(Comparator.comparing(ChartBaseDataVO::getName));
            data.add(new ChartDataVO(dataType, "line", baseDataVos));

        });
        return new LineBarChartValueVO("项目类型项目收益率曲线图", data);
    }

    public void calculate(LocalDate dateTime) {
        List<WorkbenchOverallReturnRateMetric> metrics = baseMapper.selectList(
                Wrappers.<WorkbenchOverallReturnRateMetric>lambdaQuery()
                        .eq(WorkbenchOverallReturnRateMetric::getMonth, dateTime));
        if (ObjectUtil.isEmpty(metrics)) {
            List<WorkbenchOverallReturnRateMetric> nextMonthMetrics = baseMapper.selectList(
                    Wrappers.<WorkbenchOverallReturnRateMetric>lambdaQuery()
                            .eq(WorkbenchOverallReturnRateMetric::getMonth,
                                    LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).minusMonths(1)));
            List<WorkbenchOverallReturnRateMetric> thisMonthMetrics = nextMonthMetrics.stream()
                    .peek(metric -> {
                        metric.setId(null);
                        metric.setMonth(dateTime);
                        metric.setValue("0.00");
                    }).collect(Collectors.toList());
            saveBatch(thisMonthMetrics);
            metrics = thisMonthMetrics;
        }
        Map<String, WorkbenchOverallReturnRateMetric> typeMap = new HashMap<>();
        metrics.forEach(metric -> {
            if ("ZSZL".equals(metric.getDeptSocp())) {
                typeMap.put("ZSZL", metric);
            }
            if (metric.getDeptSocp() != null && !"ZSZL".equals(metric.getDeptSocp())) {
                typeMap.put("dept-" + metric.getDeptSocp(), metric);
            }
            if (metric.getProjectType() != null && !"ZSZL".equals(metric.getProjectType())) {
                typeMap.put("industry-" + metric.getProjectType(), metric);
            }
        });

        for (WorkbenchMetricDeptScope deptScope : WorkbenchMetricDeptScope.values()) {
            if (deptScope.equals(WorkbenchMetricDeptScope.ZSZL)) {
                BigDecimal all = overallReturnRatePort.calculateAverageIrr(null, dateTime);
                typeMap.get("ZSZL").setValue(all.toString());
            } else {
                Set<Long> deptClients = overallReturnRatePort.listActiveClientIdsByDeptCode(deptScope.name());
                BigDecimal deptRes = overallReturnRatePort.calculateAverageIrr(deptClients, dateTime);
                typeMap.get("dept-" + deptScope.name()).setValue(deptRes.toString());
            }
        }

        Set<Long> targetClientIds;
        for (String industry : INDUSTRY_MAP.keySet()) {
            if ("OTHER".equals(industry)) {
                Set<String> notIn = INDUSTRY_MAP.values().stream().flatMap(Collection::stream)
                        .collect(Collectors.toSet());
                targetClientIds = overallReturnRatePort.listClientIdsExcludingIndustries(notIn);
            } else {
                targetClientIds = INDUSTRY_MAP.get(industry).stream()
                        .map(overallReturnRatePort::listClientIdsByIndustry)
                        .flatMap(Set::stream)
                        .collect(Collectors.toSet());
            }
            BigDecimal industryRes = overallReturnRatePort.calculateAverageIrr(targetClientIds, dateTime);
            typeMap.get("industry-" + industry).setValue(industryRes.toString());
        }
        updateBatchById(typeMap.values());
    }

    private String month(LocalDate date) {
        return MONTH_FORMATTER.format(date);
    }
}
