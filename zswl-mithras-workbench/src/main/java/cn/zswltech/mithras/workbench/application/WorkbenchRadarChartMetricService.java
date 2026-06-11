package cn.zswltech.mithras.workbench.application;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.dto.workbench.WorkbenchMetricReq;
import cn.zswltech.mithras.dto.workbench.chart.RadarChartValueVO;
import cn.zswltech.mithras.dto.workbench.chart.sub.RadarDataVO;
import cn.zswltech.mithras.foundation.util.BigDecimalUtil;
import cn.zswltech.mithras.workbench.application.job.WorkbenchRadarChartMetricCalculator;
import cn.zswltech.mithras.workbench.enums.WorkbenchMetricDeptScope;
import cn.zswltech.mithras.workbench.enums.WorkbenchMetricRole;
import cn.zswltech.mithras.workbench.enums.WorkbenchMetricUnit;
import cn.zswltech.mithras.workbench.mapper.WorkbenchRadarChartMetricMapper;
import cn.zswltech.mithras.workbench.mapper.model.WorkbenchRadarChartMetric;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author zhaozhengkang
 * @description 工作台-龙虎雷达图
 * @date 2023-05-10
 */
@Service
public class WorkbenchRadarChartMetricService
        extends ServiceImpl<WorkbenchRadarChartMetricMapper, WorkbenchRadarChartMetric>
        implements WorkbenchRadarChartMetricCalculator {

    private static final Collection<String> EFFECTIVE_CONTRACT_STATUSES =
            Arrays.asList("TAKE_EFFECT", "START_RENT", "SETTLE");
    private static final Map<String, String> METRIC_NAME_RSP_MAP = new HashMap<>();

    static {
        METRIC_NAME_RSP_MAP.put("投放", "本年人均新增投放");
        METRIC_NAME_RSP_MAP.put("咨询费", "本年人均新增咨询费");
        METRIC_NAME_RSP_MAP.put("合同数量", "本年人均新增合同数量");
        METRIC_NAME_RSP_MAP.put("利润", "利润");
        METRIC_NAME_RSP_MAP.put("回款及时率", "回款及时率");
    }

    @Resource
    private WorkbenchRadarChartMetricPort radarChartMetricPort;

    public RadarChartValueVO radarChart(WorkbenchMetricReq req) {
        WorkbenchMetricRole role = WorkbenchMetricRole.valueOf(req.getCurrentRoleCode());
        switch (role) {
            case COMPREHENSIVE_MANAGEMENT:
                return comprehensiveManagement();
            case XMJL:
                return businessPersonnel();
            case JSSYB_BDS:
            case HGJCYWB_BDS:
            case JXHJGYWB_BDS:
            case JCSSYWB_BDS:
            case LLWLTD_BDS:
            case XJZZHXJJTD_BDS:
            case JTYSYWB_BDS:
            case GGSY_BDS:
                return bds(req.getCurrentRoleCode());
            default:
                return null;
        }
    }

    private RadarChartValueVO comprehensiveManagement() {
        List<String> scopes = Arrays.stream(WorkbenchMetricDeptScope.values()).map(Enum::name)
                .filter(v -> !"ZSZL".equals(v)).collect(Collectors.toList());
        scopes.add("DEPT_MAX");

        Map<String, List<WorkbenchRadarChartMetric>> group = baseMapper.selectList(
                        Wrappers.<WorkbenchRadarChartMetric>lambdaQuery()
                                .in(WorkbenchRadarChartMetric::getDeptScop, scopes))
                .stream().collect(Collectors.groupingBy(WorkbenchRadarChartMetric::getDeptScop));
        Map<String, List<RadarDataVO>> data = new HashMap<>();
        group.forEach((scope, metrics) -> {
            List<RadarDataVO> radarDataVos = metrics.stream()
                    .map(metric -> new RadarDataVO(METRIC_NAME_RSP_MAP.get(metric.getMetricName()), metric.getValue(),
                            WorkbenchMetricUnit.valueOf(metric.getUnit()).display()))
                    .collect(Collectors.toList());
            if ("DEPT_MAX".equals(scope)) {
                data.put("MAX", radarDataVos);
            } else {
                data.put(WorkbenchMetricDeptScope.valueOf(scope).display(), radarDataVos);
            }
        });
        return new RadarChartValueVO("龙虎雷达图", data, "部门单项最大值");
    }

    private RadarChartValueVO bds(String role) {
        List<String> scopes = new ArrayList<>();
        scopes.add("DEPT_MAX");
        scopes.add(role.split("_")[0]);
        scopes.add("ZSZL");
        Map<String, List<WorkbenchRadarChartMetric>> group = baseMapper.selectList(
                        Wrappers.<WorkbenchRadarChartMetric>lambdaQuery()
                                .in(WorkbenchRadarChartMetric::getDeptScop, scopes))
                .stream().collect(Collectors.groupingBy(WorkbenchRadarChartMetric::getDeptScop));
        Map<String, List<RadarDataVO>> data = new HashMap<>();
        group.forEach((scope, metrics) -> {
            List<RadarDataVO> radarDataVos = metrics.stream()
                    .map(metric -> new RadarDataVO(METRIC_NAME_RSP_MAP.get(metric.getMetricName()), metric.getValue(),
                            WorkbenchMetricUnit.valueOf(metric.getUnit()).display()))
                    .collect(Collectors.toList());
            if ("DEPT_MAX".equals(scope)) {
                data.put("MAX", radarDataVos);
            } else if ("ZSZL".equals(scope)) {
                data.put("公司平均值", radarDataVos);
            } else {
                data.put("本部门均值", radarDataVos);
            }
        });
        return new RadarChartValueVO("龙虎雷达图", data, "部门单项最大值");
    }

    private RadarChartValueVO businessPersonnel() {
        List<String> scopes = new ArrayList<>();
        scopes.add("PERSON_MAX");
        scopes.add("ZSZL");
        Map<String, List<WorkbenchRadarChartMetric>> group = baseMapper.selectList(
                        Wrappers.<WorkbenchRadarChartMetric>lambdaQuery()
                                .in(WorkbenchRadarChartMetric::getDeptScop, scopes))
                .stream().collect(Collectors.groupingBy(WorkbenchRadarChartMetric::getDeptScop));
        Map<String, List<RadarDataVO>> data = new HashMap<>();
        group.forEach((scope, metrics) -> {
            List<RadarDataVO> radarDataVos = metrics.stream()
                    .map(metric -> new RadarDataVO(metric.getMetricName(), metric.getValue(),
                            WorkbenchMetricUnit.valueOf(metric.getUnit()).display()))
                    .collect(Collectors.toList());
            if ("PERSON_MAX".equals(scope)) {
                data.put("MAX", radarDataVos);
            } else {
                data.put("公司平均值", radarDataVos);
            }
        });

        Long userId = AccountUtil.getLoginInfo().getId();
        LocalDate start = LocalDate.now().with(TemporalAdjusters.firstDayOfYear());
        Set<Long> contractIds = radarChartMetricPort.listCurrentYearContractIdsBySponsor(
                userId, start.atStartOfDay(), EFFECTIVE_CONTRACT_STATUSES);

        List<RadarDataVO> personalValue = new ArrayList<>();
        BigDecimal totalLaunch = radarChartMetricPort.calculateLaunchAmountBySponsor(userId, start);
        personalValue.add(new RadarDataVO("投放", totalLaunch.toString(), "万元"));
        BigDecimal profit = radarChartMetricPort.calculateProfit(contractIds);
        personalValue.add(new RadarDataVO("利润", profit.toString(), "%"));
        BigDecimal collectionRate = radarChartMetricPort.calculateCollectionRateBySponsor(
                userId, start, LocalDate.now().with(TemporalAdjusters.lastDayOfYear()));
        personalValue.add(new RadarDataVO("回款及时率", collectionRate.toString(), "%"));
        BigDecimal totalFee = radarChartMetricPort.calculateConsultingFees(contractIds, 1);
        personalValue.add(new RadarDataVO("咨询费", totalFee.toString(), "万元"));
        personalValue.add(new RadarDataVO("合同数量", new BigDecimal(contractIds.size()).toString(), "个"));
        data.put("个人龙虎值", personalValue);

        return new RadarChartValueVO("龙虎雷达图", data, "个人单项最大值");
    }

    @Override
    public void calculate() {
        Map<String, WorkbenchRadarChartMetric> scopeMap = baseMapper.selectList(
                        Wrappers.<WorkbenchRadarChartMetric>lambdaQuery())
                .stream()
                .collect(Collectors.toMap(metric -> String.join("_", metric.getDeptScop(), metric.getMetricName()), v -> v));
        WorkbenchRadarChartMetric deptMaxContractNum = scopeMap.get(String.join("_", "DEPT_MAX", "合同数量"));
        deptMaxContractNum.setValue("0");
        WorkbenchRadarChartMetric deptMaxLaunchAmount = scopeMap.get(String.join("_", "DEPT_MAX", "投放"));
        deptMaxLaunchAmount.setValue("0");
        WorkbenchRadarChartMetric deptMaxFee = scopeMap.get(String.join("_", "DEPT_MAX", "咨询费"));
        deptMaxFee.setValue("0");
        WorkbenchRadarChartMetric deptMaxCollectionRate = scopeMap.get(String.join("_", "DEPT_MAX", "回款及时率"));
        deptMaxCollectionRate.setValue("0");
        WorkbenchRadarChartMetric deptMaxProfit = scopeMap.get(String.join("_", "DEPT_MAX", "利润"));
        deptMaxProfit.setValue("0");

        LocalDate start = LocalDate.now().with(TemporalAdjusters.firstDayOfYear());
        LocalDate end = LocalDate.now().with(TemporalAdjusters.lastDayOfYear());
        for (WorkbenchMetricDeptScope deptScope : WorkbenchMetricDeptScope.values()) {
            int personCount = radarChartMetricPort.countUsersByDeptScope(deptScope.name());
            Set<Long> contractIds = radarChartMetricPort.listCurrentYearContractIdsByDeptScope(
                    deptScope.name(), start.atStartOfDay(), EFFECTIVE_CONTRACT_STATUSES);
            if (personCount == 0) {
                continue;
            }

            BigDecimal averageContractNum = new BigDecimal(contractIds.size())
                    .divide(new BigDecimal(personCount), 0, RoundingMode.HALF_UP);
            scopeMap.get(String.join("_", deptScope.name(), "合同数量")).setValue(averageContractNum.toString());
            BigDecimal totalLaunch = radarChartMetricPort.calculateLaunchAmountByDeptScope(deptScope.name(), start);
            WorkbenchRadarChartMetric averageLaunch = scopeMap.get(String.join("_", deptScope.name(), "投放"));
            averageLaunch.setValue(BigDecimalUtil.divide(totalLaunch, new BigDecimal(personCount), 2).toString());
            BigDecimal totalFee = radarChartMetricPort.calculateConsultingFees(contractIds, personCount);
            scopeMap.get(String.join("_", deptScope.name(), "咨询费")).setValue(totalFee.toString());
            BigDecimal collectionRate = radarChartMetricPort.calculateCollectionRateByDeptScope(deptScope.name(), start, end);
            scopeMap.get(String.join("_", deptScope.name(), "回款及时率")).setValue(collectionRate.toString());
            BigDecimal profit = radarChartMetricPort.calculateProfit(contractIds);
            scopeMap.get(String.join("_", deptScope.name(), "利润")).setValue(profit.toString());

            if (deptScope != WorkbenchMetricDeptScope.ZSZL) {
                if (averageContractNum.compareTo(stringToBigDecimal(deptMaxContractNum.getValue())) > 0) {
                    deptMaxContractNum.setValue(averageContractNum.toString());
                }

                if (new BigDecimal(averageLaunch.getValue()).compareTo(stringToBigDecimal(deptMaxLaunchAmount.getValue())) > 0) {
                    deptMaxLaunchAmount.setValue(averageLaunch.getValue());
                }
                if (totalFee.compareTo(stringToBigDecimal(deptMaxFee.getValue())) > 0) {
                    deptMaxFee.setValue(totalFee.toString());
                }
                if (collectionRate.compareTo(stringToBigDecimal(deptMaxCollectionRate.getValue())) > 0) {
                    deptMaxCollectionRate.setValue(collectionRate.toString());
                }
                if (profit.compareTo(stringToBigDecimal(deptMaxProfit.getValue())) > 0) {
                    deptMaxProfit.setValue(profit.toString());
                }
            }
        }

        WorkbenchRadarChartMetric personMaxContractNum = scopeMap.get(String.join("_", "PERSON_MAX", "合同数量"));
        personMaxContractNum.setValue("0");
        WorkbenchRadarChartMetric personMaxLaunchAmount = scopeMap.get(String.join("_", "PERSON_MAX", "投放"));
        personMaxLaunchAmount.setValue("0");
        WorkbenchRadarChartMetric personMaxFee = scopeMap.get(String.join("_", "PERSON_MAX", "咨询费"));
        personMaxFee.setValue("0");
        WorkbenchRadarChartMetric personMaxCollectionRate = scopeMap.get(String.join("_", "PERSON_MAX", "回款及时率"));
        personMaxCollectionRate.setValue("0");
        WorkbenchRadarChartMetric personMaxProfit = scopeMap.get(String.join("_", "PERSON_MAX", "利润"));
        personMaxProfit.setValue("0");

        Set<Long> businessUseIds = radarChartMetricPort.listBusinessUserIds(WorkbenchMetricRole.XMJL.name());
        for (Long userId : businessUseIds) {
            Set<Long> contractIds = radarChartMetricPort.listCurrentYearContractIdsBySponsor(
                    userId, start.atStartOfDay(), EFFECTIVE_CONTRACT_STATUSES);

            BigDecimal contractNum = new BigDecimal(contractIds.size());
            BigDecimal totalLaunch = radarChartMetricPort.calculateLaunchAmountBySponsor(userId, start);
            BigDecimal totalFee = radarChartMetricPort.calculateConsultingFees(contractIds, 1);
            BigDecimal collectionRate = radarChartMetricPort.calculateCollectionRateBySponsor(userId, start, end);
            BigDecimal profit = radarChartMetricPort.calculateProfit(contractIds);

            if (contractNum.compareTo(stringToBigDecimal(personMaxContractNum.getValue())) > 0) {
                personMaxContractNum.setValue(contractNum.toString());
            }
            if (totalLaunch.compareTo(stringToBigDecimal(personMaxLaunchAmount.getValue())) > 0) {
                personMaxLaunchAmount.setValue(totalLaunch.toString());
            }
            if (totalFee.compareTo(stringToBigDecimal(personMaxFee.getValue())) > 0) {
                personMaxFee.setValue(totalFee.toString());
            }
            if (collectionRate.compareTo(stringToBigDecimal(personMaxCollectionRate.getValue())) > 0) {
                personMaxCollectionRate.setValue(collectionRate.toString());
            }
            if (profit.compareTo(stringToBigDecimal(personMaxProfit.getValue())) > 0) {
                personMaxProfit.setValue(profit.toString());
            }
        }
        updateBatchById(scopeMap.values());
    }

    public BigDecimal stringToBigDecimal(String str) {
        if (ObjectUtil.isEmpty(str)) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(str);
    }
}
