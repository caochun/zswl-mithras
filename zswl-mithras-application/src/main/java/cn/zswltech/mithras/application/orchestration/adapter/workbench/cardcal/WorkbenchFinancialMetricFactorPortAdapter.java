package cn.zswltech.mithras.application.orchestration.adapter.workbench.cardcal;

import cn.zswltech.mithras.metric.financialcloudmetric.calculator.MissingFactorException;
import cn.zswltech.mithras.metric.mapper.model.RiskMetricFactor;
import cn.zswltech.mithras.metric.service.RiskMetricFactorService;
import cn.zswltech.mithras.workbench.application.cardcal.WorkbenchFinancialMetricFactorMissingException;
import cn.zswltech.mithras.workbench.application.cardcal.WorkbenchFinancialMetricFactorPort;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;

@Component
public class WorkbenchFinancialMetricFactorPortAdapter implements WorkbenchFinancialMetricFactorPort {
    @Resource
    private RiskMetricFactorService riskMetricFactorService;

    @Override
    public Long getFactorValue(String name, String table, LocalDate dateTime) {
        try {
            RiskMetricFactor factor = riskMetricFactorService.getFactor(name, table, dateTime);
            return factor == null ? null : factor.getFactorValue();
        } catch (MissingFactorException e) {
            throw new WorkbenchFinancialMetricFactorMissingException(e.getMessage(), e);
        }
    }
}
