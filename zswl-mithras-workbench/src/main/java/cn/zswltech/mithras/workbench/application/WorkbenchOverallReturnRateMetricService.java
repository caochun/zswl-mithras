package cn.zswltech.mithras.workbench.application;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.workbench.WorkbenchMetricReq;
import cn.zswltech.mithras.dto.workbench.chart.LineBarChartValueVO;
import cn.zswltech.mithras.dto.workbench.chart.sub.ChartBaseDataVO;
import cn.zswltech.mithras.dto.workbench.chart.sub.ChartDataVO;
import cn.zswltech.mithras.workbench.application.job.WorkbenchOverallReturnRateMetricCalculator;
import cn.zswltech.mithras.workbench.application.port.WorkbenchOverallReturnRatePort;
import cn.zswltech.mithras.workbench.enums.WorkbenchMetricDeptScope;
import cn.zswltech.mithras.workbench.mapper.WorkbenchOverallReturnRateMetricMapper;
import cn.zswltech.mithras.workbench.mapper.model.WorkbenchOverallReturnRateMetric;
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
    private static final Map<String, String> INDUSTRY_DISPLAY_MAP = new HashMap<>();

    static {
        INDUSTRY_MAP.put("PUBLIC_UTILITIES", Collections.singletonList("PUBLIC_UTILITIES"));
        INDUSTRY_MAP.put("CIVIL_CONSUMPTION", Arrays.asList("CIVIL_CONSUMPTION", "TRAVEL"));
        INDUSTRY_MAP.put("OTHER", new ArrayList<>());

        INDUSTRY_DISPLAY_MAP.put("PUBLIC_UTILITIES", "公用事业类");
        INDUSTRY_DISPLAY_MAP.put("CIVIL_CONSUMPTION", "民生消费类（含供水供热供电供气、污水处理等）");
        INDUSTRY_DISPLAY_MAP.put("TRAVEL", "旅游行业");
        INDUSTRY_DISPLAY_MAP.put("STEEL", "钢铁、不锈钢及有色金属冶炼行业");
        INDUSTRY_DISPLAY_MAP.put("TRANSPORTATION_LOGISTICS", "交通运输物流行业（含冷链仓储物流、汽车经销商、普通物流、公共交通等）");
        INDUSTRY_DISPLAY_MAP.put("WATER_TRANSPORTATION", "水上运输业");
        INDUSTRY_DISPLAY_MAP.put("PAPER_MAKING", "造纸、精细化工、汽车零部件等传统制造行业");
        INDUSTRY_DISPLAY_MAP.put("CONSTRUCTION", "建筑工程行业（含建筑材料）");
        INDUSTRY_DISPLAY_MAP.put("INFORMATION_INDUSTRY", "信息产业（5G、IDC、通信服务等新基建行业）");
        INDUSTRY_DISPLAY_MAP.put("NEW_MATERIALS", "新能源、新材料、新科技等智能制造、先进装备制造行业");
        INDUSTRY_DISPLAY_MAP.put("INNOVATION_BUSINESS", "创新业务（取国标行业分类第二级）");
        INDUSTRY_DISPLAY_MAP.put("INTRA_GROUP_COLLABORATION", "集团内协同业务");
        INDUSTRY_DISPLAY_MAP.put("NON_GOVERNMENT_FUNDED_EDUCATION", "民办教育行业");
        INDUSTRY_DISPLAY_MAP.put("OTHER", "其他行业");
        INDUSTRY_DISPLAY_MAP.put("PUBLIC_HOLDING_COMPANY_INDUSTRY", "国有控股产业");
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
                dataType = INDUSTRY_DISPLAY_MAP.getOrDefault(projectType, projectType);
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
