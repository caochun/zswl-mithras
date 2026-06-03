package cn.zswltech.mithras.service.service.workbench;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.dto.workbench.WorkbenchBarMetricReq;
import cn.zswltech.mithras.dto.workbench.chart.LineBarChartValueVO;
import cn.zswltech.mithras.dto.workbench.chart.sub.ChartBaseDataVO;
import cn.zswltech.mithras.dto.workbench.chart.sub.ChartDataVO;
import cn.zswltech.mithras.service.enums.payment.PaymentWriteOffStatus;
import cn.zswltech.mithras.workbench.domain.enums.WorkbenchMetricDeptScope;
import cn.zswltech.mithras.workbench.domain.enums.WorkbenchMetricTimeScope;
import cn.zswltech.mithras.service.mapper.model.BaseModel;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.contract.mapper.model.contract.*;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentActualDetail;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projestablish.*;
import cn.zswltech.mithras.service.mapper.model.projreview.*;
import cn.zswltech.mithras.workbench.infrastructure.persistence.mapper.model.WorkbenchBarChartMetric;
import cn.zswltech.mithras.workbench.infrastructure.persistence.mapper.WorkbenchBarChartMetricMapper;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.contract.versioning.application.ContractAocPriceLibService;
import cn.zswltech.mithras.contract.versioning.application.ContractFactoringPriceLibService;
import cn.zswltech.mithras.contract.versioning.application.ContractLeasePriceLibService;
import cn.zswltech.mithras.service.service.lib.projestablish.ProjEstablishAocPriceLibService;
import cn.zswltech.mithras.service.service.lib.projestablish.ProjEstablishFactoringPriceLibService;
import cn.zswltech.mithras.service.service.lib.projestablish.ProjEstablishLeasePriceLibService;
import cn.zswltech.mithras.service.service.lib.projreview.ProjReviewAocPriceLibService;
import cn.zswltech.mithras.service.service.lib.projreview.ProjReviewFactoringPriceLibService;
import cn.zswltech.mithras.service.service.lib.projreview.ProjReviewLeasePriceLibService;
import cn.zswltech.mithras.service.service.payment.PaymentActualDetailService;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.service.service.projestablish.ProjEstablishBaseInfoService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.service.util.DateUtil;
import cn.zswltech.mithras.service.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhaozhengkang
 * @description 工作台-柱状图指标
 * @date 2023-05-09
 */
@Service
public class WorkbenchBarChartMetricService
        extends ServiceImpl<WorkbenchBarChartMetricMapper, WorkbenchBarChartMetric> {
    @Resource
    private SysUserService sysUserService;
    @Resource
    private ProjEstablishBaseInfoService establishBaseInfoService;
    @Resource
    private ProjEstablishAocPriceLibService aocPriceLibService;
    @Resource
    private ProjEstablishFactoringPriceLibService factoringPriceLibService;
    @Resource
    private ProjEstablishLeasePriceLibService leasePriceLibService;
    @Resource
    private ProjReviewBaseInfoService reviewBaseInfoService;
    @Resource
    private ProjReviewAocPriceLibService reviewAocPriceLibService;
    @Resource
    private ProjReviewFactoringPriceLibService reviewFactoringPriceLibService;
    @Resource
    private ProjReviewLeasePriceLibService reviewLeasePriceLibService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractAocPriceLibService contractAocPriceLibService;
    @Resource
    private ContractFactoringPriceLibService contractFactoringPriceLibService;
    @Resource
    private ContractLeasePriceLibService contractLeasePriceLibService;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private PaymentActualDetailService actualDetailService;
    @Resource
    private ClientService clientService;

    public static final String PROJ_ESTABLISH_COUNT_METRIC_NAME = "新增立项数";
    public static final String PROJ_ESTABLISH_AMOUNT_METRIC_NAME = "新增立项金额";
    public static final String PROJ_REVIEW_COUNT_METRIC_NAME = "新增评审数";
    public static final String PROJ_REVIEW_AMOUNT_METRIC_NAME = "新增评审金额";
    public static final String CONTRACT_COUNT_METRIC_NAME = "新增合同数";
    public static final String CONTRACT_AMOUNT_METRIC_NAME = "新增合同金额";
    public static final String PAYMENT_COUNT_METRIC_NAME = "新增投放数";
    public static final String PAYMENT_AMOUNT_METRIC_NAME = "新增投放金额";

    public LineBarChartValueVO barChart(WorkbenchBarMetricReq req) {
        LineBarChartValueVO rsp = new LineBarChartValueVO();
        rsp.setTitle("柱状图");
        List<ChartDataVO> data = new ArrayList<>();
        Map<String, List<WorkbenchBarChartMetric>> metricGroup = baseMapper.selectList(
                        Wrappers.<WorkbenchBarChartMetric>lambdaQuery()
                                .eq(WorkbenchBarChartMetric::getScop, req.getWorkbenchMetricTimeScope())
                                .isNotNull(WorkbenchBarChartMetric::getDeptCode))
                .stream().collect(Collectors.groupingBy(WorkbenchBarChartMetric::getMetricName));
        // 获取新增立项数据
        ChartDataVO projEstablishData = getMetricData(PROJ_ESTABLISH_COUNT_METRIC_NAME,
                PROJ_ESTABLISH_AMOUNT_METRIC_NAME, metricGroup);
        data.add(projEstablishData);
        // 获取新增评审数据
        ChartDataVO projReviewData = getMetricData(PROJ_REVIEW_COUNT_METRIC_NAME,
                PROJ_REVIEW_AMOUNT_METRIC_NAME, metricGroup);
        data.add(projReviewData);
        // 获取新增合同数据
        ChartDataVO contractData = getMetricData(CONTRACT_COUNT_METRIC_NAME,
                CONTRACT_AMOUNT_METRIC_NAME, metricGroup);
        data.add(contractData);
        // 获取新增投放数据
        ChartDataVO paymentData = getMetricData(PAYMENT_COUNT_METRIC_NAME,
                PAYMENT_AMOUNT_METRIC_NAME, metricGroup);
        data.add(paymentData);
        rsp.setData(data);
        return rsp;
    }

    private ChartDataVO getMetricData(String countKey, String amountKey, Map<String, List<WorkbenchBarChartMetric>> metricGroup) {
        Map<String, String> dept2Count = Optional.ofNullable(metricGroup.get(countKey)).orElse(ListUtil.empty()).stream()
                .collect(Collectors.toMap(WorkbenchBarChartMetric::getDeptCode,
                        metric -> {
                            if (metric.getValue() == null) {
                                return "0";
                            }
                            return metric.getValue();
                        }));
        Map<String, String> dept2Amount = Optional.ofNullable(metricGroup.get(countKey)).orElse(ListUtil.empty()).stream()
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
        dept2Count.keySet().forEach(s -> {
            ChartBaseDataVO chartBaseDataVO = new ChartBaseDataVO();
            chartBaseDataVO.setName(WorkbenchMetricDeptScope.valueOf(s).display());
            chartBaseDataVO.setValue(dept2Count.get(s));
            chartBaseDataVO.setHoverValue(dept2Amount.get(s) + "万元");
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
    public void calculate() {
        Map<String, WorkbenchBarChartMetric> metricMap = baseMapper.selectList(Wrappers.<WorkbenchBarChartMetric>lambdaQuery()
                        .isNotNull(WorkbenchBarChartMetric::getDeptCode)
                        .isNotNull(WorkbenchBarChartMetric::getScop)).stream()
                .collect(Collectors.toMap(WorkbenchBarChartMetric::identity, item -> item, (k1, k2) -> k1));

        for (WorkbenchMetricDeptScope deptScope : WorkbenchMetricDeptScope.values()) {
            if (WorkbenchMetricDeptScope.ZSZL.equals(deptScope)) {
                continue;
            }
            for (WorkbenchMetricTimeScope timeScope : WorkbenchMetricTimeScope.values()) {
                doCalculator(deptScope, timeScope, metricMap);
            }
        }
        // 更新数据
        updateBatchById(metricMap.values());
    }

    private void doCalculator(WorkbenchMetricDeptScope deptScope, WorkbenchMetricTimeScope timeScope, Map<String, WorkbenchBarChartMetric> metricMap) {
        LocalDateTime startTime = LocalDate.now().atStartOfDay();
        switch (timeScope) {
            case WEEKLY:
                startTime = startTime.with(DayOfWeek.MONDAY);
                break;
            case MONTHLY:
                startTime = startTime.with(TemporalAdjusters.firstDayOfMonth());
                break;
            case QUARTERLY:
                startTime = DateUtil.quarterStart(0);
                break;
            case YEARLY:
                startTime = startTime.with(TemporalAdjusters.firstDayOfYear());
                break;
            default:
                break;
        }
        if (startTime == null) {
            return;
        }
        // 计算立项数据
        List<ProjEstablishBaseInfo> establishBaseInfos = establishBaseInfoService.list(Wrappers.<ProjEstablishBaseInfo>lambdaQuery()
                .eq(ProjEstablishBaseInfo::getProjEstablishStatus, "TAKE_EFFECT")
                .eq(ProjEstablishBaseInfo::getBizDeptId, sysUserService.getOrgIdByCode(deptScope.name()))
                .ge(BaseModel::getCreateTime, startTime));
        Set<Long> projEstablishIds = establishBaseInfos.stream().map(ProjEstablishBaseInfo::getId)
                .collect(Collectors.toSet());
        WorkbenchBarChartMetric metric_1 = metricMap.get(
                String.join("-", deptScope.name(), timeScope.name(), PROJ_ESTABLISH_COUNT_METRIC_NAME));
        WorkbenchBarChartMetric metric_2 = metricMap.get(
                String.join("-", deptScope.name(), timeScope.name(), PROJ_ESTABLISH_AMOUNT_METRIC_NAME));
        if (ObjectUtil.isNotEmpty(projEstablishIds)) {
            List<ProjEstablishLeasePriceLib> leasePriceLibs = leasePriceLibService
                    .listNewestByProjEstablishIds(projEstablishIds);
            List<ProjEstablishAocPriceLib> aocPriceLibs = aocPriceLibService
                    .listNewestByProjEstablishIds(projEstablishIds);
            List<ProjEstablishFactoringPriceLib> factoringPriceLibs = factoringPriceLibService
                    .listNewestByProjEstablishIds(projEstablishIds);
            // 新增立项个数
            metric_1.setValue(String.valueOf(establishBaseInfos.size()));
            // 新增立项金额
            BigDecimal establishAmount = BigDecimal.ZERO;
            establishAmount = leasePriceLibs.stream().map(ProjEstablishLeasePrice::getApplyCreditAmount)
                    .map(LongUtil::null2zero).map(BigDecimal::new).reduce(establishAmount, BigDecimal::add);
            establishAmount = aocPriceLibs.stream().map(ProjEstablishAocPrice::getApplyCreditAmount)
                    .map(LongUtil::null2zero).map(BigDecimal::new).reduce(establishAmount, BigDecimal::add);
            establishAmount = factoringPriceLibs.stream().map(ProjEstablishFactoringPrice::getApplyCreditAmount)
                    .map(LongUtil::null2zero).map(BigDecimal::new).reduce(establishAmount, BigDecimal::add);
            metric_2.setValue(establishAmount
                    .divide(BigDecimal.valueOf(100000000L), 2, RoundingMode.HALF_UP).toString());
        } else {
            metric_1.setValue("0");
            metric_2.setValue("0.00");
        }
        // 计算评审数据
        List<ProjReviewBaseInfo> reviewBaseInfos = reviewBaseInfoService.list(Wrappers.<ProjReviewBaseInfo>lambdaQuery()
                .eq(ProjReviewBaseInfo::getProjReviewStatus, "TAKE_EFFECT")
                .eq(ProjReviewBaseInfo::getBizDeptId, sysUserService.getOrgIdByCode(deptScope.name()))
                .ge(BaseModel::getCreateTime, startTime));
        Set<Long> projReviewIds = reviewBaseInfos.stream().map(ProjReviewBaseInfo::getId).collect(Collectors.toSet());
        WorkbenchBarChartMetric metric_3 = metricMap.get(
                String.join("-", deptScope.name(), timeScope.name(), PROJ_REVIEW_COUNT_METRIC_NAME));
        WorkbenchBarChartMetric metric_4 = metricMap.get(
                String.join("-", deptScope.name(), timeScope.name(), PROJ_REVIEW_AMOUNT_METRIC_NAME));
        if (ObjectUtil.isNotEmpty(projReviewIds)) {
            List<ProjReviewLeasePriceLib> leasePriceLibs = reviewLeasePriceLibService
                    .listNewestByProjReviewIds(projReviewIds);
            List<ProjReviewAocPriceLib> aocPriceLibs = reviewAocPriceLibService.listNewestByProjReviewIds(projReviewIds);
            List<ProjReviewFactoringPriceLib> factoringPriceLibs = reviewFactoringPriceLibService
                    .listNewestByProjReviewIds(projReviewIds);
            // 新增立项个数
            metric_3.setValue(String.valueOf(reviewBaseInfos.size()));
            // 新增立项金额
            BigDecimal reviewAmount = BigDecimal.ZERO;
            reviewAmount = leasePriceLibs.stream().map(ProjReviewLeasePrice::getApplyCreditAmount)
                    .map(LongUtil::null2zero).map(BigDecimal::new).reduce(reviewAmount, BigDecimal::add);
            reviewAmount = aocPriceLibs.stream().map(ProjReviewAocPrice::getApplyCreditAmount)
                    .map(LongUtil::null2zero).map(BigDecimal::new).reduce(reviewAmount, BigDecimal::add);
            reviewAmount = factoringPriceLibs.stream().map(ProjReviewFactoringPrice::getApplyCreditAmount)
                    .map(LongUtil::null2zero).map(BigDecimal::new).reduce(reviewAmount, BigDecimal::add);
            metric_4.setValue(reviewAmount.divide(BigDecimal.valueOf(100000000L), 2, RoundingMode.HALF_UP).toString());
        } else {
            metric_3.setValue("0");
            metric_4.setValue("0.00");
        }
        // 计算合同数据
        List<ContractBaseInfo> contractBaseInfos = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                .eq(ContractBaseInfo::getBizDeptId, sysUserService.getOrgIdByCode(deptScope.name()))
                .in(ContractBaseInfo::getContractStatus, "TAKE_EFFECT", "SETTLE", "START_RENT")
                .ge(BaseModel::getCreateTime, startTime));
        Set<Long> contractIds = contractBaseInfos.stream().map(ContractBaseInfo::getId).collect(Collectors.toSet());
        WorkbenchBarChartMetric metric_5 = metricMap.get(
                String.join("-", deptScope.name(), timeScope.name(), CONTRACT_COUNT_METRIC_NAME));
        WorkbenchBarChartMetric metric_6 = metricMap.get(
                String.join("-", deptScope.name(), timeScope.name(), CONTRACT_AMOUNT_METRIC_NAME));
        if (ObjectUtil.isNotEmpty(contractIds)) {
            List<ContractLeasePriceLib> leasePriceLibs = contractLeasePriceLibService.queryNewestLib(contractIds);
            List<ContractAocPriceLib> aocPriceLibs = contractAocPriceLibService.queryNewestLib(contractIds);
            List<ContractFactoringPriceLib> factoringPriceLibs = contractFactoringPriceLibService.queryNewestLib(contractIds);
            // 新增立项个数
            metric_5.setValue(String.valueOf(contractBaseInfos.size()));
            // 新增立项金额
            BigDecimal contractAmount = BigDecimal.ZERO;
            contractAmount = leasePriceLibs.stream().map(ContractLeasePrice::getApplyCreditAmount)
                    .map(LongUtil::null2zero).map(BigDecimal::new).reduce(contractAmount, BigDecimal::add);
            contractAmount = aocPriceLibs.stream().map(ContractAocPriceLib::getContractAmount)
                    .map(LongUtil::null2zero).map(BigDecimal::new).reduce(contractAmount, BigDecimal::add);
            contractAmount = factoringPriceLibs.stream().map(ContractFactoringPriceLib::getContractAmount)
                    .map(LongUtil::null2zero).map(BigDecimal::new).reduce(contractAmount, BigDecimal::add);
            metric_6.setValue(contractAmount
                    .divide(BigDecimal.valueOf(100000000L), 2, RoundingMode.HALF_UP).toString());
        } else {
            metric_5.setValue("0");
            metric_6.setValue("0.00");
        }
        // 计算投放数据
        Set<Long> targetContractIds = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                .eq(ContractBaseInfo::getBizDeptId, sysUserService.getOrgIdByCode(deptScope.name()))
                .in(ContractBaseInfo::getContractStatus, "TAKE_EFFECT", "START_RENT")).stream().map(ContractBaseInfo::getId).collect(Collectors.toSet());
        WorkbenchBarChartMetric metric_7 = metricMap.get(
                String.join("-", deptScope.name(), timeScope.name(), PAYMENT_COUNT_METRIC_NAME));
        WorkbenchBarChartMetric metric_8 = metricMap.get(
                String.join("-", deptScope.name(), timeScope.name(), PAYMENT_AMOUNT_METRIC_NAME));
        // 新增投放个数
        if (ObjectUtil.isNotEmpty(targetContractIds)) {
            Set<Long> paymentIds = paymentBaseInfoService.list(Wrappers.<PaymentBaseInfo>lambdaQuery()
                    .in(PaymentBaseInfo::getContractId, targetContractIds)
                    .in(PaymentBaseInfo::getWriteOffStatus, PaymentWriteOffStatus.WRITTEN_OFF.name(),
                            PaymentWriteOffStatus.PART_WRITTEN_OFF.name())
                    .ge(BaseModel::getCreateTime, startTime)).stream().map(PaymentBaseInfo::getId).collect(Collectors.toSet());
            metric_7.setValue(String.valueOf(paymentIds.size()));
        } else {
            metric_7.setValue("0");
        }
        // 新增投放金额
        Set<Long> targetClientIds;
        if ("ZSZL".equals(deptScope.name())) {
            targetClientIds = null;
        } else {
            targetClientIds = clientService.list(Wrappers.<Client>lambdaQuery()
                            .eq(Client::getClientStatus, "TAKE_EFFECT")
                            .eq(Client::getBelongDeptId, sysUserService.getOrgIdByCode(deptScope.name())))
                    .stream().map(Client::getId).collect(Collectors.toSet());
        }
        String totalLaunch = actualDetailService
                .writtenOffDetailsByClientIds(targetClientIds, startTime.toLocalDate(), null).stream()
                .map(PaymentActualDetail::getPaidInAmount)
                .map(LongUtil::null2zero)
                .map(BigDecimal::new)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(100000000L), 2, RoundingMode.HALF_UP).toString();
        metric_8.setValue(totalLaunch);
    }
}