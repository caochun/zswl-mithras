package cn.zswltech.mithras.service.adapter.metric;

import cn.zswltech.mithras.metric.mapper.model.RiskMetricFactor;
import cn.zswltech.mithras.metric.service.RiskMetricFactorService;
import cn.zswltech.mithras.riskcontrol.application.RiskMetricFactorQueryService;
import cn.zswltech.mithras.riskcontrol.application.RiskMetricFactorValue;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;

@Component
public class RiskMetricFactorQueryServiceAdapter implements RiskMetricFactorQueryService {

    @Resource
    private RiskMetricFactorService factorService;

    @Override
    public RiskMetricFactorValue newestFactor(String factorName, String factorTable, LocalDate factorQueryDate) {
        RiskMetricFactor factor = factorService.getOne(Wrappers.<RiskMetricFactor>lambdaQuery()
                .eq(RiskMetricFactor::getFactorName, factorName)
                .eq(RiskMetricFactor::getFactorTable, factorTable)
                .ge(factorQueryDate != null, RiskMetricFactor::getFactorDate, factorQueryDate)
                .orderByDesc(RiskMetricFactor::getFactorDate)
                .last("limit 1"));
        if (factor == null) {
            return null;
        }
        return new RiskMetricFactorValue(factor.getFactorValue(), factor.getFactorDate());
    }
}
