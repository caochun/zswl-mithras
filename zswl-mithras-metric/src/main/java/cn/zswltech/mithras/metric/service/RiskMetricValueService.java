package cn.zswltech.mithras.metric.service;

import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.dto.metric.value.RiskMetricValueListReq;
import cn.zswltech.mithras.dto.metric.value.RiskMetricValueModifyReq;
import cn.zswltech.mithras.metric.aggregator.MetricCalculator;
import cn.zswltech.mithras.metric.emit.MetricEmitter;
import cn.zswltech.mithras.metric.emit.model.req.risk.index.RiskIndexReqBody;
import cn.zswltech.mithras.metric.emit.model.req.risk.index.RiskIndexReqSingleBody;
import cn.zswltech.mithras.metric.enums.risk.index.RiskMetricFrequency;
import cn.zswltech.mithras.metric.enums.risk.index.RiskMetricStatus;
import cn.zswltech.mithras.metric.enums.risk.index.RiskMetricUnit;
import cn.zswltech.mithras.metric.mapper.RiskMetricValueMapper;
import cn.zswltech.mithras.metric.mapper.model.RiskMetricValue;
import cn.zswltech.mithras.metric.mapper.model.condition.RiskMetricValueListConditions;
import cn.zswltech.mithras.riskcontrol.strategy.RiskControlStrategy;
import cn.zswltech.mithras.riskcontrol.strategy.RiskControlStrategyMapper;
import cn.zswltech.mithras.foundation.cache.RedisDistLock;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.hutool.core.util.ObjectUtil.isNotNull;
import static cn.hutool.core.util.ObjectUtil.isNull;
import static cn.zswltech.mithras.metric.enums.risk.index.RiskMetricFrequency.valueOf;
import static cn.zswltech.mithras.metric.enums.risk.index.RiskMetricStatus.PEND_REPORT;
import static cn.zswltech.mithras.foundation.exception.MithrasException.err;
import static cn.zswltech.mithras.foundation.util.StringUtil.mysqlLimit;
import static java.math.RoundingMode.HALF_UP;

/**
 * @author yibin
 */
@Slf4j
@Service
public class RiskMetricValueService extends ServiceImpl<RiskMetricValueMapper, RiskMetricValue> {

    @Autowired
    private List<MetricCalculator> calculatorList = new ArrayList<>();

    @Resource
    private MetricEmitter metricEmitter;
    @Resource
    private RedisDistLock lock;
    @Resource
    private RiskControlStrategyMapper riskControlStrategyMapper;

    @Transactional(rollbackFor = Exception.class)
    public void modify(List<RiskMetricValueModifyReq> req) {
        for (RiskMetricValueModifyReq modifyReq : req) {
            RiskMetricValue value = new RiskMetricValue();
            value.setId(modifyReq.getId());
            value.setMetricValueAdjusted(modifyReq.getMetricValueAdjusted());
            value.setStatus(PEND_REPORT.name());
            this.updateById(value);
        }
    }

    public Page<RiskMetricValue> list(RiskMetricValueListReq req) {
        if (req.getDataTime() != null) {
            req.setDataTime(req.getDataTime().with(TemporalAdjusters.lastDayOfMonth()));
        }
        return this.getBaseMapper().list(new Page<>(req.getPage(), req.getPageSize()),
                BeanUtil.copyProperties(req, RiskMetricValueListConditions.class));
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean report(LocalDate dataTime) {
        RiskMetricValueListReq param = new RiskMetricValueListReq();
        param.setDataTime(dataTime);
        param.setNeedReport(true);
        Page<RiskMetricValue> pageList = this.list(param);
        if (pageList.getRecords().stream().anyMatch(e -> isNull(e.getMetricValue()) && isNull(e.getMetricValueAdjusted()))) {
            err("必填指标未填写");
        }
        //
        try {
            param.setNeedReport(null);
            param.setPageSize(5000);
            List<RiskMetricValue> valueList = this.list(param).getRecords();
            Map<String, List<RiskMetricValue>> freqMap = valueList.stream().filter(e -> isNull(e.getMetricValue()) || isNull(e.getMetricValueAdjusted()))
                    .collect(Collectors.groupingBy(RiskMetricValue::getFrequency));
            for (String freq : freqMap.keySet()) {
                RiskMetricFrequency f = valueOf(freq);
                int monthDelta = 0;
                switch (f) {
                    case MONTH: {
                        monthDelta = 0;
                        break;
                    }
                    case SEASON: {
                        monthDelta = 3;
                        break;
                    }
                    case HALF_YEAR: {
                        monthDelta = 6;
                        break;
                    }
                    case YEAR: {
                        monthDelta = 12;
                        break;
                    }
                    default:
                        err("无效的报送频次." + freq);
                        break;
                }

                RiskIndexReqBody body = new RiskIndexReqBody();
                body.setFrequency(freq);
                LocalDate date = freqMap.get(freq).get(0).getDataTime();
                body.setDataTimeEnd(LocalDateTime.of(date, LocalTime.MAX).with(TemporalAdjusters.lastDayOfMonth()));
                body.setDataTimeBegin(LocalDateTime.of(date.minusMonths(monthDelta).with(TemporalAdjusters.firstDayOfMonth()), LocalTime.MIN));
                List<RiskIndexReqSingleBody> list = freqMap.get(freq).stream()
                        .filter(e -> isNotNull(e.getMetricValueAdjusted()) || isNotNull(e.getMetricValue()))
                        .map(e -> {
                            RiskIndexReqSingleBody single = new RiskIndexReqSingleBody();
                            Long v = e.getMetricValueAdjusted() != null ? e.getMetricValueAdjusted() : e.getMetricValue();
                            single.setValue(v.toString());
                            if (RiskMetricUnit.WAN.name().equals(e.getUnit())
                                    || RiskMetricUnit.YUAN.name().equals(e.getUnit())
                                    || RiskMetricUnit.PERCENT.name().equals(e.getUnit())
                            ) {
                                single.setValue(mithrasLong2BigDecimal(v).setScale(2, HALF_UP).toString());
                            }
                            single.setCode(e.getMetricCode());
                            single.setName(e.getMetricName());
                            return single;
                        }).collect(Collectors.toList());
                body.setIndexList(list);
                metricEmitter.emitRiskIndex(body);
                //
                RiskMetricValue toBe = new RiskMetricValue();
                toBe.setStatus(RiskMetricStatus.REPORTED.name());
                this.update(toBe, Wrappers.<RiskMetricValue>lambdaQuery()
                        .and(w -> w
                                .or().isNotNull(RiskMetricValue::getMetricValue)
                                .or().isNotNull(RiskMetricValue::getMetricValueAdjusted)
                        )
                        .eq(RiskMetricValue::getDataTime, dataTime));
            }
        } catch (Exception e) {
            log.error("报送失败", e);
            RiskMetricValue toBe = new RiskMetricValue();
            toBe.setStatus(RiskMetricStatus.REPORT_FAIL.name());
            this.update(toBe, Wrappers.<RiskMetricValue>lambdaQuery()
                    .and(w -> w
                            .or().isNotNull(RiskMetricValue::getMetricValue)
                            .or().isNotNull(RiskMetricValue::getMetricValueAdjusted)
                    )
                    .eq(RiskMetricValue::getDataTime, dataTime));
            return false;
        }
        return true;
    }

    public LocalDateTime lastReportTime() {
        List<RiskMetricValue> list = this.list(Wrappers.<RiskMetricValue>lambdaQuery()
                .eq(RiskMetricValue::getStatus, RiskMetricStatus.REPORTED.name())
                .orderByDesc(RiskMetricValue::getUpdateTime)
                .last(mysqlLimit(0, 1))
        );
        if (!list.isEmpty()) {
            return list.get(0).getUpdateTime();
        }
        return null;
    }

    //    @Transactional(rollbackFor = Exception.class)
    public void calc(LocalDate dataTime) {
        String key = "mithras:metric:calc:onlyOne";
        if (lock.tryLock(key, 1, 60 * 1000)) {
            try {
                Map<String, RiskControlStrategy> strategyMap =
                        riskControlStrategyMapper.selectList(Wrappers.<RiskControlStrategy>lambdaQuery())
                                .stream().collect(Collectors.toMap(RiskControlStrategy::getMetricCode, item -> item, (k1, k2) -> k1));
                getCalculatorList().parallelStream().forEach(calculator -> {
                    try {
                        //风控策略有值
                        if (strategyMap.containsKey(calculator.metricCode())) {
                            RiskControlStrategy riskControlStrategy = strategyMap.get(calculator.metricCode());
                            BigDecimal value = new BigDecimal(riskControlStrategy.getCurrentValueOne());
                            if ("亿元".equals(riskControlStrategy.getValueUnitOne())) {
                                value = value.multiply(new BigDecimal(100000000));
                            }
                            RiskMetricValue toBe = new RiskMetricValue();
                            toBe.setStatus(PEND_REPORT.name());
                            toBe.setMetricValue(value.longValue());
                            this.update(toBe, Wrappers.<RiskMetricValue>lambdaUpdate()
                                    .eq(RiskMetricValue::getMetricCode, calculator.metricCode())
                                    .eq(RiskMetricValue::getDataTime, dataTime)
                            );
                            return;
                        }
                        BigDecimal value = calculator.calc(dataTime);
                        String metricCode = calculator.metricCode();
                        RiskMetricValue toBe = new RiskMetricValue();
                        toBe.setStatus(PEND_REPORT.name());
                        toBe.setMetricValue(value.longValue());
                        this.update(toBe, Wrappers.<RiskMetricValue>lambdaUpdate()
                                .eq(RiskMetricValue::getMetricCode, metricCode)
                                .eq(RiskMetricValue::getDataTime, dataTime)
                        );
                    } catch (Exception e) {
                        try {
                            log.error("指标：{}计算失败，表达式：{}, 变量：{}",
                                    calculator.metricCode(), calculator.calcExpression(), calculator.varMap(dataTime), e);
                        } catch (Exception ee) {
                            log.error("", ee);
                        }
                    }
                });
            } finally {
                lock.unlock(key);
            }
        } else {
            err("已在计算中，稍后再尝试");
        }
    }

    public List<MetricCalculator> getCalculatorList() {
        return calculatorList;
    }

    private BigDecimal mithrasLong2BigDecimal(Long value) {
        return BigDecimal.valueOf(value).divide(BigDecimal.valueOf(10000L));
    }
}

