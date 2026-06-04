package cn.zswltech.mithras.metric.financialcloudmetric.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.financialcloudmetric.FinancialCloudMetricValueListREQ;
import cn.zswltech.mithras.dto.financialcloudmetric.FinancialCloudMetricValueListRSP;
import cn.zswltech.mithras.dto.financialcloudmetric.FinancialCloudMetricValueModifyREQ;
import cn.zswltech.mithras.dto.financialcloudmetric.IndexDto;
import cn.zswltech.mithras.metric.enums.risk.index.RiskMetricDataSource;
import cn.zswltech.mithras.metric.enums.risk.index.RiskMetricFrequency;
import cn.zswltech.mithras.metric.enums.risk.index.RiskMetricStatus;
import cn.zswltech.mithras.metric.enums.risk.index.RiskMetricUnit;
import cn.zswltech.mithras.metric.financialcloudmetric.calculator.*;
import cn.zswltech.mithras.metric.financialcloudmetric.calculator.accincrease.AccumulativeIncreaseCalculator;
import cn.zswltech.mithras.metric.financialcloudmetric.calculator.accincrease.DepartmentPaymentCache;
import cn.zswltech.mithras.metric.financialcloudmetric.calculator.investbalance.InventoryInvestmentBalanceCalculator;
import cn.zswltech.mithras.metric.financialcloudmetric.calculator.returnrate.AverageReturnRateCalculator;
import cn.zswltech.mithras.metric.financialcloudmetric.converter.FinancialCloudMetricValueConverter;
import cn.zswltech.mithras.metric.financialcloudmetric.mapper.FinancialCloudMetricValueMapper;
import cn.zswltech.mithras.metric.financialcloudmetric.mapper.FinancialCloudMetricValueQuery;
import cn.zswltech.mithras.metric.financialcloudmetric.model.FinancialCloudMetric;
import cn.zswltech.mithras.metric.financialcloudmetric.model.FinancialCloudMetricValue;
import cn.zswltech.mithras.service.enums.ContentTypeEnum;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.util.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static cn.zswltech.mithras.metric.enums.risk.index.RiskMetricDataSource.MANUAL;
import static cn.zswltech.mithras.metric.enums.risk.index.RiskMetricStatus.PEND_REPORT;
import static cn.zswltech.mithras.service.util.StringUtil.mysqlLimit;

/**
 * @author zhaozhengkang
 * @description 金融云指标
 * @date 2023-04-12
 */
@Service
@Slf4j
public class FinancialCloudMetricValueService extends
        ServiceImpl<FinancialCloudMetricValueMapper, FinancialCloudMetricValue> {
    @Resource
    private FinancialCloudMetricService metricService;
    @Resource
    private FinancialCloudMetricValueConverter baseConverter;
    @Resource
    private OverdueProjectsCalculator overdueProjectsCalculator;
    @Resource
    private DepartmentPaymentCache departmentPaymentCache;
    @Resource
    private ContractRemainingPrincipalReader remainingPrincipalReader;

    @Autowired
    private List<FinancialCloudMetricCalculator> calculatorList = new ArrayList<>();
    @Autowired
    private final List<DepartmentPerCapitalCalculator> departmentPerCapitalCalculators = new ArrayList<>();
    @Autowired
    private List<DepartmentBaseCalculator> departmentBaseCalculators = new ArrayList<>();
    @Autowired
    private final List<Secondary> secondary = new ArrayList<>();

    private final List<Long> yearMetricIds = new ArrayList<>();

    @Value("${financial.cloud.upload_url}")
    private String uploadUrl;


    @Transactional(rollbackFor = Throwable.class)
    public void modify(List<FinancialCloudMetricValueModifyREQ> req) {
        List<FinancialCloudMetricValue> toUpdateList = new LinkedList<>();
        for (FinancialCloudMetricValueModifyREQ modifyReq : req) {
            FinancialCloudMetricValue value = new FinancialCloudMetricValue();
            value.setId(modifyReq.getId());
            value.setMetricValueAdjusted(modifyReq.getMetricValueAdjusted());
            value.setStatus(PEND_REPORT.name());
            toUpdateList.add(value);
//            this.updateById(value);
        }
        if (CollectionUtil.isNotEmpty(toUpdateList)) {
            this.updateBatchById(toUpdateList);
        }
    }

    public PageR<FinancialCloudMetricValueListRSP> list(FinancialCloudMetricValueListREQ req) {
        List<FinancialCloudMetric> metrics = metricService.list();
        Map<Long, FinancialCloudMetric> cloudMetricMap = metrics.stream().collect(Collectors.toMap(FinancialCloudMetric::getId, item -> item));

        Map<String, List<FinancialCloudMetric>> frequencyMap = metrics.stream().collect(Collectors.groupingBy(FinancialCloudMetric::getFrequency));
        List<Long> yearMetricIds = frequencyMap.get(RiskMetricFrequency.YEAR.name()).stream().map(FinancialCloudMetric::getId).collect(Collectors.toList());
        this.yearMetricIds.addAll(yearMetricIds);
        FinancialCloudMetricValueQuery query = baseConverter.listReq2Query(req);
        query.setMetricIds(yearMetricIds);
        query.setFirstMonth(LocalDate.of(req.getDataTime().getYear(), 1, 1));
        Page<FinancialCloudMetricValue> metricValuePage =
                baseMapper.advanceList(new Page<>(req.getPage(), req.getPageSize()), query);
        if (metricValuePage.getRecords().isEmpty()) {
            return PageR.empty(req.getPage(), req.getPageSize());
        }
        List<FinancialCloudMetricValueListRSP> listRSPS = new ArrayList<>();
        for (FinancialCloudMetricValue metricValue : metricValuePage.getRecords()) {
            FinancialCloudMetricValueListRSP rsp = baseConverter.entity2ListRsp(metricValue);
            FinancialCloudMetric metric = cloudMetricMap.get(rsp.getMetricId());
            if (ObjectUtil.isNotEmpty(metric)) {
                rsp.setUnit(metric.getUnit());
                rsp.setFrequency(metric.getFrequency());
            }
            if (metricValue.getValue() == null) {
                rsp.setIsRed(true);
            }
            listRSPS.add(rsp);
        }
        return PageR.of(metricValuePage, listRSPS);
    }

    public LocalDateTime getLastReportTime() {
        List<FinancialCloudMetricValue> list = this.list(Wrappers.<FinancialCloudMetricValue>lambdaQuery()
                .eq(FinancialCloudMetricValue::getStatus, RiskMetricStatus.REPORTED.name())
                .orderByDesc(FinancialCloudMetricValue::getUpdateTime)
                .last(mysqlLimit(0, 1))
        );
        if (!list.isEmpty()) {
            return list.get(0).getUpdateTime();
        }
        return null;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void report(LocalDate dataTime) {
        FinancialCloudMetricValueQuery query = new FinancialCloudMetricValueQuery();
        query.setMetricIds(yearMetricIds);
        query.setFirstMonth(LocalDate.of(dataTime.getYear(), 1, 1));
        query.setDataTime(dataTime);

        // 过滤掉"按行业", "按地域", "按部门"切值为0的指标 todo 先全量传输
        List<FinancialCloudMetricValue> metricValues = baseMapper.reportList(query);
        if (ObjectUtil.isEmpty(metricValues)) {
            throw new MithrasException("没有找到可报送的指标数据！");
        }
        Map<Long, FinancialCloudMetric> cloudMetricMap = metricService.list().stream().collect(Collectors.toMap(FinancialCloudMetric::getId, item -> item));
        List<IndexDto> dtos = new ArrayList<>();
        for (FinancialCloudMetricValue metricValue : metricValues) {
            IndexDto dto = new IndexDto();
            FinancialCloudMetric metric = cloudMetricMap.get(metricValue.getMetricId());
            dto.setMetrics(metric.getMetricName());
            dto.setMetricsFirstType(metric.getOneLevelType());
            dto.setMetricsSecondType(metricValue.getTwoLevelType());
            dto.setOneLevelType(metric.getMetricFirstType());
            dto.setTwoLevelType(metric.getMetricSecondType());
            dto.setFrequency(metric.getFrequency());
            dto.setMonth(metricValue.getDataTime().getYear()
                    + String.format("%02d", metricValue.getDataTime().getMonthValue()));
            String value = metricValue.getValue();
            if (value == null) {
                throw new MithrasException("指标'" + metric.getMetricName() + "'值为空,请补充");
            }
            if (ObjectUtil.isNotEmpty(metric.getUnit())) {
                RiskMetricUnit unitEnum = RiskMetricUnit.valueOf(metric.getUnit());
                dto.setUnit(unitEnum.display);
                if (unitEnum == RiskMetricUnit.HU || unitEnum == RiskMetricUnit.TIAN) {
                    dto.setValue(value);
                } else if (unitEnum == RiskMetricUnit.PERCENT) {
                    BigDecimal valueDecimal = new BigDecimal(value).divide(new BigDecimal(1000000), 4, RoundingMode.HALF_UP);
                    dto.setValue(valueDecimal.toString());
                } else {
                    BigDecimal valueDecimal = new BigDecimal(value).divide(new BigDecimal(10000), 2, RoundingMode.HALF_UP);
                    dto.setValue(valueDecimal.toString());
                }
            } else {
                dto.setValue(value);
            }
            dtos.add(dto);
        }
        try {
            String response = HttpUtil.post(uploadUrl, JSONUtil.toJsonStr(dtos), ContentTypeEnum.json);
            if (ObjectUtil.isEmpty(response)) {
                throw new MithrasException("报送失败！");
            }
            log.info("报送结果：{}", response);
            JSONObject responseObject = JSON.parseObject(response);
            Integer code = responseObject.getInteger("code");
            if (code == 200) {
                //报送成功更新状态
                List<FinancialCloudMetricValue> reportedList = metricValues.stream().peek(financialCloudMetricValue ->
                                financialCloudMetricValue.setStatus(RiskMetricStatus.REPORTED.name()))
                        .collect(Collectors.toList());
                this.updateBatchById(reportedList);
            }
        } catch (IOException e) {
            log.error("报送失败！", e);
            throw new MithrasException("报送失败！");
        }

    }

    public void calc(LocalDate dataTime) {
        Map<String, FinancialCloudMetric> code2Metric = metricService.list()
                .stream().collect(Collectors.toMap(FinancialCloudMetric::getMetricCode, item -> item));

        // 部门业绩目标
        departmentBaseCalculators.parallelStream().forEach(calculator -> {
            doCalculate(calculator, code2Metric.get(calculator.metricCode()), dataTime);
        });

        //先计算无依赖的指标
        calculatorList.parallelStream().forEach(calculator -> {
            doCalculate(calculator, code2Metric.get(calculator.metricCode()), dataTime);
        });

        //人均指标
        departmentPerCapitalCalculators.parallelStream().forEach(calculator -> {
            doCalculate(calculator, code2Metric.get(calculator.metricCode()), dataTime);
        });
        //依赖前数据的指标
        secondary.parallelStream().forEach(calculator -> {
            FinancialCloudMetricCalculator financialCloudMetricCalculator = (FinancialCloudMetricCalculator) calculator;
            doCalculate(financialCloudMetricCalculator, code2Metric.get(financialCloudMetricCalculator.metricCode()), dataTime);
        });
        overdueProjectsCalculator.calculate(dataTime);

        // 清空缓存
        AccumulativeIncreaseCalculator.clear();
        AverageFinancingCostCalculator.clear();
        AverageReturnRateCalculator.clear();
        DepartmentPerCapitalCalculator.clear();
        InventoryInvestmentBalanceCalculator.clear();
        DepartmentBaseCalculator.clear();
        departmentPaymentCache.clear();
        remainingPrincipalReader.clear();
    }

    @PostConstruct
    private void initCalculatorList() {
        Map<String, FinancialCloudMetricCalculator> calculatorMap =
                calculatorList.stream().collect(Collectors.toMap(v -> v.getClass().getSimpleName(), v -> v));
        Map<String, DepartmentPerCapitalCalculator> departmentPerCapitalCalculatorMap =
                departmentPerCapitalCalculators.stream().collect(Collectors.toMap(v -> v.getClass().getSimpleName(), v -> v));
        Map<String, Secondary> secondaryMap = secondary.stream()
                .collect(Collectors.toMap(v -> v.getClass().getSimpleName(), v -> v));
        calculatorMap.entrySet().removeIf(next -> departmentPerCapitalCalculatorMap.containsKey(next.getKey()) || secondaryMap.containsKey(next.getKey()));
        Map<String, DepartmentBaseCalculator> departmentBaseCalculatorMap = departmentBaseCalculators.stream().collect(Collectors.toMap(v -> v.getClass().getSimpleName(), v -> v));
        departmentBaseCalculators = new ArrayList<>(departmentBaseCalculatorMap.values());
        calculatorList = new ArrayList<>(calculatorMap.values());
    }

    private void doCalculate(FinancialCloudMetricCalculator calculator, FinancialCloudMetric metric, LocalDate dataTime) {
        FinancialCloudMetricValue metricValue = new FinancialCloudMetricValue();
        try {
            BigDecimal res = calculator.calculate(dataTime);
            if (!(calculator instanceof DepartmentPerCapitalCalculator)) {
                RiskMetricUnit unit = RiskMetricUnit.valueOf(metric.getUnit());
                switch (unit) {
                    case WAN:
                        res = res.divide(new BigDecimal(10000), 0, RoundingMode.HALF_UP);
                        break;
                    case YI:
                        res = res.divide(new BigDecimal(100000000), 0, RoundingMode.HALF_UP);
                        break;
                    default:
                        break;
                }
            }
            metricValue.setStatus(RiskMetricStatus.PEND_REPORT.name());
            metricValue.setMetricValue(String.valueOf(res.longValue()));
            // 处理计算明细信息
            String details = CalculateDetailCache.get(metric.getMetricCode());
            if (ObjectUtil.isNotEmpty(details)) {
                metricValue.setContractDetail(details);
            }
        } catch (MissingFactorException me) {
            log.info(me.getMessage());
            metricValue.setMetricValue(me.getMessage());
        } catch (Exception e) {
            log.error("指标：{}计算失败", calculator.metricCode(), e);
        }
        if (ObjectUtil.isNotEmpty(metricValue.getMetricValue())) {
            baseMapper.update(metricValue, Wrappers.<FinancialCloudMetricValue>lambdaQuery()
                    .eq(FinancialCloudMetricValue::getMetricCode, calculator.metricCode())
                    .eq(FinancialCloudMetricValue::getDataTime, dataTime));
        }

    }

    public FinancialCloudMetricValue buildMetricValue(FinancialCloudMetric metric, LocalDate dataTime) {
        FinancialCloudMetricValue value = baseConverter.metric2Value(metric);
        value.setId(null);
        value.setMetricId(metric.getId());
        value.setDataTime(dataTime);
        if ("YEAR".equals(metric.getFrequency())) {
            value.setDataSource(MANUAL.name());
        } else {
            value.setDataSource(RiskMetricDataSource.AUTO.name());
        }
        value.setStatus(RiskMetricStatus.PEND_FILL.name());
        return value;
    }


    public FinancialCloudMetricValue getMetricValue(String metricCode, LocalDate dataTime) {
        return baseMapper.selectOne(Wrappers.<FinancialCloudMetricValue>lambdaQuery()
                .eq(FinancialCloudMetricValue::getMetricCode, metricCode)
                .eq(FinancialCloudMetricValue::getDataTime, dataTime));
    }

    public void testSpecificMetric(String metricCode) {
        FinancialCloudMetricCalculator calculator = findCalculator(metricCode);
        FinancialCloudMetric metric = metricService.getOne(Wrappers.<FinancialCloudMetric>lambdaQuery().eq(FinancialCloudMetric::getMetricCode, calculator.metricCode()).last("limit 1"));
        doCalculate(calculator, metric, LocalDate.of(2023, 9, 1));
    }

    private FinancialCloudMetricCalculator findCalculator(String metricCode) {
        for (FinancialCloudMetricCalculator calculator : calculatorList) {
            if (Objects.equals(calculator.metricCode(), metricCode)) {
                return calculator;
            }
        }
        for (DepartmentBaseCalculator calculator : departmentBaseCalculators) {
            if (Objects.equals(calculator.metricCode(), metricCode)) {
                return calculator;
            }
        }
        for (DepartmentPerCapitalCalculator calculator : departmentPerCapitalCalculators) {
            if (Objects.equals(calculator.metricCode(), metricCode)) {
                return calculator;
            }
        }
        for (Secondary item : secondary) {
            FinancialCloudMetricCalculator calculator = (FinancialCloudMetricCalculator) item;
            if (Objects.equals(calculator.metricCode(), metricCode)) {
                return calculator;
            }
        }
        throw new MithrasException("指标计算器不存在");
    }
}
