package cn.zswltech.mithras.service.service.riskcontrol.eventbus.subscriber;

import cn.zswltech.mithras.metric.mapper.model.RiskMetricFactor;
import cn.zswltech.mithras.metric.service.RiskMetricFactorService;
import cn.zswltech.mithras.service.enums.riskcontrol.RiskControlIndustryClassify;
import cn.zswltech.mithras.service.mapper.model.riskcontrol.RiskControlStrategy;
import cn.zswltech.mithras.service.service.riskcontrol.eventbus.MetricComputeEvent;
import cn.zswltech.mithras.service.service.riskcontrol.eventbus.SubscribeSupporter;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;


/**
 * 1.风控行业分类为「集团内协同业务（授信主体为集团合并范围内企业）」
 * 2.R=满足以上条件的「未核销本金之和」
 * 风险策略调整，修改后的逻辑：
 * R=风控行业分类为「集团内协同业务（授信主体为集团合并范围内企业）」的「剩余本金之和 - 保证金余额」/资产负债表「所有者权益（或股东权益）合计@期末余额」
 *
 * @author zhaozhengkang
 */
@Component
@Slf4j
public class MetricComputer30A10000396_ZL007 extends RiskControlClassifyMetricComputer
        implements SubscribeSupporter<MetricComputeEvent> {
    public static final String FACTOR_NAME = "所有者权益（或股东权益）合计@期末余额";
    public static final String FACTOR_TABLE = "资产负债表";

    @Resource
    private RiskMetricFactorService factorService;

    @Override
    public String getMetricCode() {
        return "A10000396_ZL007";
    }

    @Override
    public List<String> getIndustryClassify() {
        return Collections.singletonList(RiskControlIndustryClassify.INTRA_GROUP_COLLABORATION.name());
    }

    @Override
    protected void calculate(MetricComputeEvent event, RiskControlStrategy strategy) {
        RiskMetricFactor factor = factorService.getOne(Wrappers.<RiskMetricFactor>lambdaQuery()
                .eq(RiskMetricFactor::getFactorName, FACTOR_NAME)
                .eq(RiskMetricFactor::getFactorTable, FACTOR_TABLE)
                .ge(RiskMetricFactor::getFactorDate, event.getFactorQueryDate())
                .orderByDesc(RiskMetricFactor::getFactorDate)
                .last("limit 1"));
        if (factor == null) {
            strategy.setNullReason("上月指标因子未导入");
            strategy.setCurrentValueOne(null);
            strategy.setCurrentValueTwo(null);
            return;
        }
        // 复用逻辑
        super.calculate(event, strategy);
        // 取出【剩余本金和 - 保证金余额】
        BigDecimal b = strategy.getCurrentValueOneDecimal();
        // 计算新值结果保留两位小数
        BigDecimal res = b.divide(new BigDecimal(factor.getFactorValue()), 4, RoundingMode.HALF_UP);
        strategy.setCurrentValueOneDecimal(res);
        MetricComputer30A10000396_ZL007.MetricComputer_30Context calculateCtx = new MetricComputer30A10000396_ZL007.MetricComputer_30Context();
        calculateCtx.setFactorValue(String.valueOf(factor.getFactorValue()));
        calculateCtx.setRiskExposure(b.toPlainString());
        strategy.setQuickContext(JSON.toJSONString(calculateCtx));
    }

    @AllowConcurrentEvents
    @Subscribe
    @Override
    public void onSubscribe(MetricComputeEvent metricComputeEvent) {
        log.info("MetricComputeA10000396_ZL007 onSubscribe");
        compute(metricComputeEvent);
    }

    @Data
    static class MetricComputer_30Context {
        private String totalRemainingPrincipal;
        private String factorValue;
        private String riskExposure;
    }
}
