package cn.zswltech.mithras.workbench.application;

import cn.hutool.core.collection.ListUtil;
import cn.zswltech.mithras.dto.workbench.WorkbenchBarMetricReq;
import cn.zswltech.mithras.dto.workbench.chart.LineBarChartValueVO;
import cn.zswltech.mithras.dto.workbench.chart.sub.ChartBaseDataVO;
import cn.zswltech.mithras.dto.workbench.chart.sub.ChartDataVO;
import cn.zswltech.mithras.workbench.application.job.WorkbenchBarChartMetricCalculator;
import cn.zswltech.mithras.workbench.domain.enums.WorkbenchMetricDeptScope;
import cn.zswltech.mithras.workbench.domain.enums.WorkbenchMetricTimeScope;
import cn.zswltech.mithras.workbench.infrastructure.persistence.mapper.WorkbenchBarChartMetricMapper;
import cn.zswltech.mithras.workbench.infrastructure.persistence.mapper.model.WorkbenchBarChartMetric;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author zhaozhengkang
 * @description 工作台-柱状图指标
 * @date 2023-05-09
 */
@Service
public class WorkbenchBarChartMetricService
        extends ServiceImpl<WorkbenchBarChartMetricMapper, WorkbenchBarChartMetric>
        implements WorkbenchBarChartMetricCalculator {

    public static final String PROJ_ESTABLISH_COUNT_METRIC_NAME = "新增立项数";
    public static final String PROJ_ESTABLISH_AMOUNT_METRIC_NAME = "新增立项金额";
    public static final String PROJ_REVIEW_COUNT_METRIC_NAME = "新增评审数";
    public static final String PROJ_REVIEW_AMOUNT_METRIC_NAME = "新增评审金额";
    public static final String CONTRACT_COUNT_METRIC_NAME = "新增合同数";
    public static final String CONTRACT_AMOUNT_METRIC_NAME = "新增合同金额";
    public static final String PAYMENT_COUNT_METRIC_NAME = "新增投放数";
    public static final String PAYMENT_AMOUNT_METRIC_NAME = "新增投放金额";

    @Resource
    private WorkbenchBarChartMetricPort barChartMetricPort;

    public LineBarChartValueVO barChart(WorkbenchBarMetricReq req) {
        LineBarChartValueVO rsp = new LineBarChartValueVO();
        rsp.setTitle("柱状图");
        List<ChartDataVO> data = new ArrayList<>();
        Map<String, List<WorkbenchBarChartMetric>> metricGroup = baseMapper.selectList(
                        Wrappers.<WorkbenchBarChartMetric>lambdaQuery()
                                .eq(WorkbenchBarChartMetric::getScop, req.getWorkbenchMetricTimeScope())
                                .isNotNull(WorkbenchBarChartMetric::getDeptCode))
                .stream().collect(Collectors.groupingBy(WorkbenchBarChartMetric::getMetricName));

        data.add(getMetricData(PROJ_ESTABLISH_COUNT_METRIC_NAME, PROJ_ESTABLISH_AMOUNT_METRIC_NAME, metricGroup));
        data.add(getMetricData(PROJ_REVIEW_COUNT_METRIC_NAME, PROJ_REVIEW_AMOUNT_METRIC_NAME, metricGroup));
        data.add(getMetricData(CONTRACT_COUNT_METRIC_NAME, CONTRACT_AMOUNT_METRIC_NAME, metricGroup));
        data.add(getMetricData(PAYMENT_COUNT_METRIC_NAME, PAYMENT_AMOUNT_METRIC_NAME, metricGroup));
        rsp.setData(data);
        return rsp;
    }

    private ChartDataVO getMetricData(String countKey, String amountKey,
                                      Map<String, List<WorkbenchBarChartMetric>> metricGroup) {
        Map<String, String> dept2Count = Optional.ofNullable(metricGroup.get(countKey)).orElse(ListUtil.empty()).stream()
                .collect(Collectors.toMap(WorkbenchBarChartMetric::getDeptCode,
                        metric -> {
                            if (metric.getValue() == null) {
                                return "0";
                            }
                            return metric.getValue();
                        }));
        Map<String, String> dept2Amount = Optional.ofNullable(metricGroup.get(amountKey)).orElse(ListUtil.empty()).stream()
                .collect(Collectors.toMap(WorkbenchBarChartMetric::getDeptCode,
                        metric -> {
                            if (metric.getValue() == null) {
                                return "0.00";
                            }
                            return metric.getValue();
                        }));
        ChartDataVO chartDataVO = new ChartDataVO();
        chartDataVO.setDataType(countKey.substring(2, 4));
        chartDataVO.setChartType("bar");
        List<ChartBaseDataVO> chartBaseDataVOS = new ArrayList<>();
        dept2Count.keySet().forEach(scope -> {
            ChartBaseDataVO chartBaseDataVO = new ChartBaseDataVO();
            chartBaseDataVO.setName(WorkbenchMetricDeptScope.valueOf(scope).display());
            chartBaseDataVO.setValue(dept2Count.get(scope));
            chartBaseDataVO.setHoverValue(dept2Amount.getOrDefault(scope, "0.00") + "万元");
            chartBaseDataVOS.add(chartBaseDataVO);
        });
        chartDataVO.setList(chartBaseDataVOS);
        return chartDataVO;
    }

    /**
     * 获取新增立项数据
     * 获取新增评审数据
     * 获取新增合同数据
     * 获取新增投放数据
     */
    @Override
    public void calculate() {
        Map<String, WorkbenchBarChartMetric> metricMap = baseMapper.selectList(
                        Wrappers.<WorkbenchBarChartMetric>lambdaQuery()
                                .isNotNull(WorkbenchBarChartMetric::getDeptCode)
                                .isNotNull(WorkbenchBarChartMetric::getScop))
                .stream()
                .collect(Collectors.toMap(WorkbenchBarChartMetric::identity, item -> item, (k1, k2) -> k1));

        for (WorkbenchMetricDeptScope deptScope : WorkbenchMetricDeptScope.values()) {
            if (WorkbenchMetricDeptScope.ZSZL.equals(deptScope)) {
                continue;
            }
            for (WorkbenchMetricTimeScope timeScope : WorkbenchMetricTimeScope.values()) {
                doCalculator(deptScope, timeScope, metricMap);
            }
        }
        updateBatchById(metricMap.values());
    }

    private void doCalculator(WorkbenchMetricDeptScope deptScope, WorkbenchMetricTimeScope timeScope,
                              Map<String, WorkbenchBarChartMetric> metricMap) {
        LocalDateTime startTime = startTime(timeScope);
        if (startTime == null) {
            return;
        }

        WorkbenchBarChartMetric establishCount = metricMap.get(
                String.join("-", deptScope.name(), timeScope.name(), PROJ_ESTABLISH_COUNT_METRIC_NAME));
        WorkbenchBarChartMetric establishAmount = metricMap.get(
                String.join("-", deptScope.name(), timeScope.name(), PROJ_ESTABLISH_AMOUNT_METRIC_NAME));
        establishCount.setValue(String.valueOf(barChartMetricPort.countProjectEstablish(deptScope.name(), startTime)));
        establishAmount.setValue(formatAmount(barChartMetricPort.calculateProjectEstablishAmount(deptScope.name(), startTime)));

        WorkbenchBarChartMetric reviewCount = metricMap.get(
                String.join("-", deptScope.name(), timeScope.name(), PROJ_REVIEW_COUNT_METRIC_NAME));
        WorkbenchBarChartMetric reviewAmount = metricMap.get(
                String.join("-", deptScope.name(), timeScope.name(), PROJ_REVIEW_AMOUNT_METRIC_NAME));
        reviewCount.setValue(String.valueOf(barChartMetricPort.countProjectReview(deptScope.name(), startTime)));
        reviewAmount.setValue(formatAmount(barChartMetricPort.calculateProjectReviewAmount(deptScope.name(), startTime)));

        WorkbenchBarChartMetric contractCount = metricMap.get(
                String.join("-", deptScope.name(), timeScope.name(), CONTRACT_COUNT_METRIC_NAME));
        WorkbenchBarChartMetric contractAmount = metricMap.get(
                String.join("-", deptScope.name(), timeScope.name(), CONTRACT_AMOUNT_METRIC_NAME));
        contractCount.setValue(String.valueOf(barChartMetricPort.countContract(deptScope.name(), startTime)));
        contractAmount.setValue(formatAmount(barChartMetricPort.calculateContractAmount(deptScope.name(), startTime)));

        WorkbenchBarChartMetric paymentCount = metricMap.get(
                String.join("-", deptScope.name(), timeScope.name(), PAYMENT_COUNT_METRIC_NAME));
        WorkbenchBarChartMetric paymentAmount = metricMap.get(
                String.join("-", deptScope.name(), timeScope.name(), PAYMENT_AMOUNT_METRIC_NAME));
        paymentCount.setValue(String.valueOf(barChartMetricPort.countPayment(deptScope.name(), startTime)));
        paymentAmount.setValue(formatAmount(barChartMetricPort.calculatePaymentAmount(
                deptScope.name(), startTime.toLocalDate())));
    }

    private LocalDateTime startTime(WorkbenchMetricTimeScope timeScope) {
        LocalDateTime startTime = LocalDate.now().atStartOfDay();
        switch (timeScope) {
            case WEEKLY:
                return startTime.with(DayOfWeek.MONDAY);
            case MONTHLY:
                return startTime.with(TemporalAdjusters.firstDayOfMonth());
            case QUARTERLY:
                int currentMonth = LocalDate.now().getMonthValue();
                int quarterStartMonth = ((currentMonth - 1) / 3) * 3 + 1;
                return LocalDate.now().withMonth(quarterStartMonth).withDayOfMonth(1).atStartOfDay();
            case YEARLY:
                return startTime.with(TemporalAdjusters.firstDayOfYear());
            default:
                return startTime;
        }
    }

    private String formatAmount(BigDecimal amount) {
        if (amount == null) {
            return "0.00";
        }
        return amount.toString();
    }
}
