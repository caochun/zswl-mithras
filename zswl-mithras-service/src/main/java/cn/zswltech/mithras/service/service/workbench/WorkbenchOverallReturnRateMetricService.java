package cn.zswltech.mithras.service.service.workbench;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.workbench.WorkbenchMetricReq;
import cn.zswltech.mithras.dto.workbench.chart.LineBarChartValueVO;
import cn.zswltech.mithras.dto.workbench.chart.sub.ChartBaseDataVO;
import cn.zswltech.mithras.dto.workbench.chart.sub.ChartDataVO;
import cn.zswltech.mithras.service.enums.payment.WriteOffStatus;
import cn.zswltech.mithras.riskcontrol.common.RiskControlIndustryClassify;
import cn.zswltech.mithras.service.enums.workbench.WorkbenchMetricDeptScope;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.ClientBaseModel;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentActualDetail;
import cn.zswltech.mithras.service.mapper.model.workbench.WorkbenchOverallReturnRateMetric;
import cn.zswltech.mithras.service.mapper.workbench.WorkbenchOverallReturnRateMetricMapper;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractPriceService;
import cn.zswltech.mithras.service.service.lib.client.CorpCommerceInfoLibService;
import cn.zswltech.mithras.service.service.payment.PaymentActualDetailService;
import cn.zswltech.mithras.customer.application.riskcontrol.dto.CorpCommerceInfoLibDto;
import cn.zswltech.mithras.service.util.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhaozhengkang
 * @description 工作台-项目整体收益率指标
 * @date 2023-05-09
 */
@Service
public class WorkbenchOverallReturnRateMetricService
        extends ServiceImpl<WorkbenchOverallReturnRateMetricMapper, WorkbenchOverallReturnRateMetric> {
    @Resource
    private SysUserService sysUserService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private PaymentActualDetailService paymentActualDetailService;
    @Resource
    private ContractPriceService contractPriceService;
    @Resource
    private ClientService clientService;
    @Resource
    private CorpCommerceInfoLibService corpCommerceInfoLibService;

    private static final Map<String, List<String>> INDUSTRY_MAP = new HashMap<>();

    static {
        INDUSTRY_MAP.put("PUBLIC_UTILITIES", Collections.singletonList("PUBLIC_UTILITIES"));
        INDUSTRY_MAP.put("CIVIL_CONSUMPTION", Arrays.asList("CIVIL_CONSUMPTION", "TRAVEL"));
        INDUSTRY_MAP.put("OTHER", new ArrayList<>());
    }

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
                            DateUtil.getMonthStr(metric.getMonth()), metric.getValue(), null, "%"))
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
                            DateUtil.getMonthStr(metric.getMonth()), metric.getValue(), null, "%"))
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
        // 查询db中当月的指标
        List<WorkbenchOverallReturnRateMetric> metrics = baseMapper.selectList(
                Wrappers.<WorkbenchOverallReturnRateMetric>lambdaQuery()
                        .eq(WorkbenchOverallReturnRateMetric::getMonth, dateTime));
        // 若当月没有指标，则查询上月的指标，并清空id，设置月份为当月，设置值为0.00，插入当月的指标
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
        // 将当月指标分成三组
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
            // 先计算公司整体指标
            if (deptScope.equals(WorkbenchMetricDeptScope.ZSZL)) {
                BigDecimal all = doCalculate(null, dateTime);
                typeMap.get("ZSZL").setValue(all.toString());
            } else {
                Set<Long> deptClients = clientService.list(Wrappers.<Client>lambdaQuery()
                                .eq(Client::getBelongDeptId, sysUserService.getOrgIdByCode(deptScope.name()))
                                .eq(Client::getClientStatus, "TAKE_EFFECT"))
                        .stream()
                        .map(Client::getId)
                        .collect(Collectors.toSet());
                BigDecimal deptRes = doCalculate(deptClients, dateTime);
                typeMap.get("dept-" + deptScope.name()).setValue(deptRes.toString());
            }
        }

        Set<Long> targetClientIds;
        for (String industry : INDUSTRY_MAP.keySet()) {
            if ("OTHER".equals(industry)) {
                List<String> notIn = INDUSTRY_MAP.values().stream().flatMap(Collection::stream)
                        .collect(Collectors.toList());
                targetClientIds = corpCommerceInfoLibService.listNewestCommerceInfo(new CorpCommerceInfoLibDto()
                                .setNotInRiskControlIndustryClassify(notIn)).stream()
                        .map(ClientBaseModel::getClientId).collect(Collectors.toSet());
            } else {
                targetClientIds = corpCommerceInfoLibService.listNewestCommerceInfo(new CorpCommerceInfoLibDto()
                                .setInRiskControlIndustryClassify(INDUSTRY_MAP.get(industry))).stream()
                        .map(ClientBaseModel::getClientId).collect(Collectors.toSet());
            }
            BigDecimal industryRes = doCalculate(targetClientIds, dateTime);
            typeMap.get("industry-" + industry).setValue(industryRes.toString());
        }
        updateBatchById(typeMap.values());
    }

    /**
     * 计算指定客户的合同的平均收益率
     *
     * @param clientIds 为null表示不指定客户计算全部， 为empty表示没有符合条件的客户
     * @param endTime
     * @return
     */
    private BigDecimal doCalculate(Set<Long> clientIds, @Nullable LocalDate endTime) {
        List<ContractBaseInfo> contracts;
        //获取期末在租项目相关的所有的投放合同
        if (clientIds == null) {
            // 查所有
            contracts = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                    .eq(ContractBaseInfo::getContractStatus, "START_RENT")
                    .le(ContractBaseInfo::getCreateTime, endTime));
        } else {
            if (clientIds.isEmpty()) {
                return BigDecimal.ZERO;
            } else {
                contracts = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                        .in(ContractBaseInfo::getClientId, clientIds)
                        .eq(ContractBaseInfo::getContractStatus, "START_RENT")
                        .le(ContractBaseInfo::getCreateTime, endTime));
            }
        }
        if (contracts.isEmpty()) {
            return BigDecimal.ZERO;
        }

        Set<Long> contractIds = contracts.stream().map(ContractBaseInfo::getId).collect(Collectors.toSet());
        // 获取所有已核销的投放记录
        List<PaymentActualDetail> actualDetails =
                paymentActualDetailService.list(Wrappers.<PaymentActualDetail>lambdaQuery()
                        .in(PaymentActualDetail::getContractId, contractIds)
                        .eq(PaymentActualDetail::getWriteOffStatus, WriteOffStatus.WRITTEN_OFF.name())
                        .le(PaymentActualDetail::getPaidInDate, endTime));

        if (actualDetails.isEmpty()) {
            return BigDecimal.ZERO;
        }
        //计算总投放额
        BigDecimal totalAmount = actualDetails.stream().map(PaymentActualDetail::getPaidInAmount).map(BigDecimal::new)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<Long, List<PaymentActualDetail>> groupByContract = actualDetails.stream().collect(Collectors.groupingBy(PaymentActualDetail::getContractId));

        //获取投放相关合同的IRR
        Map<Long, Integer> irrs = contractPriceService.queryNewestIrr(contractIds);

        // 计算加权平均IRR
        BigDecimal averageIrr = BigDecimal.ZERO;
        for (Map.Entry<Long, List<PaymentActualDetail>> entry : groupByContract.entrySet()) {
            Integer irr = irrs.get(entry.getKey());
            if (ObjectUtil.isEmpty(irr)) {
                continue;
            }
            BigDecimal oneContractTotal = entry.getValue().stream().map(PaymentActualDetail::getPaidInAmount).map(BigDecimal::new)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            averageIrr = oneContractTotal.divide(totalAmount, 10, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(irr)).add(averageIrr);
        }
        return averageIrr.divide(new BigDecimal(10000), 2, RoundingMode.HALF_UP);
    }
}