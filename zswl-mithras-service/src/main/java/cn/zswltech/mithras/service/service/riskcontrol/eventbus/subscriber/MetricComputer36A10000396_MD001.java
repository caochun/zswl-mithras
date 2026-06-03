package cn.zswltech.mithras.service.service.riskcontrol.eventbus.subscriber;

import cn.zswltech.mithras.metric.mapper.model.RiskMetricFactor;
import cn.zswltech.mithras.metric.service.RiskMetricFactorService;
import cn.zswltech.mithras.collection.mapper.CollectionBaseInfoMapper;
import cn.zswltech.mithras.riskcontrol.strategy.RiskControlStrategy;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.PaymentActualDetailMapper;
import cn.zswltech.mithras.service.service.riskcontrol.AbstractMetricComputer;
import cn.zswltech.mithras.riskcontrol.metric.MetricComputeEvent;
import cn.zswltech.mithras.riskcontrol.metric.SubscribeSupporter;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * R=存量租赁资产剩余本金之和/资产负债表（总资产科目余额）
 * @author dingqi
 * @date 2024/11/26
 * @description
 */
@Slf4j
@Component
public class MetricComputer36A10000396_MD001 extends AbstractMetricComputer implements SubscribeSupporter<MetricComputeEvent> {
    @Resource
    private PaymentActualDetailMapper paymentActualDetailMapper;
    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;
    @Resource
    private RiskMetricFactorService factorService;

    @Override
    protected String getMetricCode() {
        return "A10000396_MD001";
    }

    @Override
    protected void calculate(MetricComputeEvent event, RiskControlStrategy strategy) {
        // 分子
        long totalPay = paymentActualDetailMapper.totalPay();
        long totalFirstRent = collectionBaseInfoMapper.totalFirstRentCollection();
        long totalPrincipal = collectionBaseInfoMapper.totalRentPrincipalCollection();
        long remainingPrincipal = totalPay - totalFirstRent - totalPrincipal;
        // 分母
        RiskMetricFactor factor = factorService.getOne(Wrappers.<RiskMetricFactor>lambdaQuery()
                .eq(RiskMetricFactor::getFactorName, "资产总计@期末余额")
                .eq(RiskMetricFactor::getFactorTable, "资产负债表")
                .ge(RiskMetricFactor::getFactorDate, event.getFactorQueryDate())
                .orderByDesc(RiskMetricFactor::getFactorDate)
                .last("limit 1"));
        if (factor == null) {
            strategy.setNullReason("上月指标因子未导入");
            strategy.setCurrentValueOne(null);
            strategy.setCurrentValueTwo(null);
            return;
        }
        strategy.setCurrentValueOneDecimal(BigDecimal.valueOf(remainingPrincipal).divide(new BigDecimal(factor.getFactorValue()), 4, RoundingMode.HALF_UP));
    }

    @AllowConcurrentEvents
    @Subscribe
    @Override
    public void onSubscribe(MetricComputeEvent event) {
        log.info("MetricComputeA10000396_MD001 onSubscribe");
        compute(event);
    }
}
