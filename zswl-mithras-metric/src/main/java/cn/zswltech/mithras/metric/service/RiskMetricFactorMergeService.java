package cn.zswltech.mithras.metric.service;



import cn.hutool.core.collection.CollectionUtil;

import cn.hutool.core.util.StrUtil;

import cn.zswltech.mithras.metric.enums.risk.index.RiskMetricCurrency;

import cn.zswltech.mithras.metric.enums.risk.index.RiskMetricFactorTable;

import cn.zswltech.mithras.metric.mapper.RiskMetricFactorMergeMapper;

import cn.zswltech.mithras.metric.mapper.model.RiskMetricFactor;

import cn.zswltech.mithras.metric.mapper.model.RiskMetricFactorMerge;

import cn.zswltech.mithras.basedata.persistence.model.BaseDataExchangeRate;

import cn.zswltech.mithras.foundation.exception.MithrasException;

import cn.zswltech.mithras.basedata.service.BaseDataExchangeRateService;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;



import javax.annotation.Resource;

import java.math.BigDecimal;

import java.time.LocalDate;

import java.util.*;

import java.util.stream.Collectors;



/**

 * @author dingqi

 * @date 2025/9/21

 * @description

 */

@Slf4j

@Service

public class RiskMetricFactorMergeService extends ServiceImpl<RiskMetricFactorMergeMapper, RiskMetricFactorMerge> {

    @Resource

    private BaseDataExchangeRateService baseDataExchangeRateService;



    public Map<String, Long> findMetricValueMap(String orgCode, String factorTable, int year, int month) {

        LocalDate date = LocalDate.of(year, month, 1);

        LambdaQueryWrapper<RiskMetricFactorMerge> query = Wrappers.lambdaQuery();

        query.eq(RiskMetricFactorMerge::getOrgCode, orgCode);

        query.eq(RiskMetricFactorMerge::getFactorDate, LocalDate.of(year, month, date.lengthOfMonth()));

        query.eq(RiskMetricFactorMerge::getFactorTable, factorTable);

        return this.list(query).stream().collect(Collectors.toMap(RiskMetricFactor::getFactorName, RiskMetricFactor::getFactorValue));

    }



    /**

     * 科目余额表合计值

     * @param year 年份

     * @param month 月份

     * @param factorName 名称

     * @return 合计值（返回单位为毫厘）

     */

    public BigDecimal subjectBalanceSum(int year, int month, List<String> factorName) {

        LocalDate beginOfMonth = LocalDate.of(year, month, 1);

        LocalDate endOfMonth = LocalDate.of(beginOfMonth.getYear(), beginOfMonth.getMonthValue(), beginOfMonth.lengthOfMonth());

        LambdaQueryWrapper<RiskMetricFactorMerge> query = Wrappers.lambdaQuery();

        query.eq(RiskMetricFactor::getFactorDate, endOfMonth);

        query.eq(RiskMetricFactor::getFactorTable, RiskMetricFactorTable.SUBJECT_BALANCE.display);

        query.in(RiskMetricFactor::getFactorName, factorName);

        List<RiskMetricFactorMerge> list = this.list(query);

        if (CollectionUtil.isEmpty(list)) {

            return BigDecimal.ZERO;

        }

        BigDecimal sumBD = BigDecimal.ZERO;

        for (RiskMetricFactorMerge metricFactorMerge : list) {

            BigDecimal b = BigDecimal.valueOf(Optional.ofNullable(metricFactorMerge.getFactorValue()).orElse(0L));

            if (!StrUtil.equals(RiskMetricCurrency.CNY.name(), metricFactorMerge.getFactorCurrency())) {

                // 外币需要转人民币，查询对应汇率

                BaseDataExchangeRate exchangeRate = baseDataExchangeRateService.getEffectByYearMonthCurrency(year, month, metricFactorMerge.getFactorCurrency());

                if (Objects.isNull(exchangeRate)) {

                    log.error("没有找到{}年{}月币种为{}的汇率信息", year, month, metricFactorMerge.getFactorCurrency());

                    throw new MithrasException("没有找到对应年月的汇率信息");

                }

                b = b.multiply(exchangeRate.getExchangeRate());

            }

            sumBD = sumBD.add(b);

        }

        return sumBD;

    }

}
