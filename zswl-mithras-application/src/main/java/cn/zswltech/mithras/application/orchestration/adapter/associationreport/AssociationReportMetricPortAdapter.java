package cn.zswltech.mithras.application.orchestration.adapter.associationreport;

import cn.zswltech.mithras.associationreport.application.AssociationReportMetricPort;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.metric.enums.risk.index.RiskMetricFactorTable;
import cn.zswltech.mithras.metric.service.RiskMetricFactorMergeService;
import cn.zswltech.mithras.metric.service.RiskMetricFactorService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Component
public class AssociationReportMetricPortAdapter implements AssociationReportMetricPort {

    @Resource
    private RiskMetricFactorMergeService riskMetricFactorMergeService;
    @Resource
    private RiskMetricFactorService riskMetricFactorService;

    @Override
    public Map<String, Long> capitalBalance(int year, int month) {
        return mergedMetric(RiskMetricFactorTable.CAPITAL_BALANCE, year, month);
    }

    @Override
    public Map<String, Long> profit(int year, int month) {
        return mergedMetric(RiskMetricFactorTable.PROFIT, year, month);
    }

    @Override
    public Map<String, Long> guoZiKuaiBao(int year, int month) {
        return mergedMetric(RiskMetricFactorTable.GZKB, year, month);
    }

    @Override
    public Map<String, Long> subjectBalance(int year, int month) {
        return riskMetricFactorService.findMetricValueMap(RiskMetricFactorTable.SUBJECT_BALANCE.display, year, month);
    }

    @Override
    public BigDecimal subjectBalanceSum(int year, int month, List<String> factorNames) {
        return riskMetricFactorMergeService.subjectBalanceSum(year, month, factorNames);
    }

    private Map<String, Long> mergedMetric(RiskMetricFactorTable table, int year, int month) {
        return riskMetricFactorMergeService.findMetricValueMap(GlobalConstants.ZSZL_MERGE_ORG_CODE, table.display, year, month);
    }
}
