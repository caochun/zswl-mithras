package cn.zswltech.mithras.riskcontrol.metric.subscriber;

import cn.zswltech.mithras.riskcontrol.metric.RiskMetricFactorQueryService;
import cn.zswltech.mithras.riskcontrol.metric.RiskMetricFactorValue;
import cn.zswltech.mithras.collection.mapper.CollectionBaseInfoMapper;
import cn.zswltech.mithras.riskcontrol.strategy.RiskControlStrategy;
import cn.zswltech.mithras.payment.mapper.PaymentActualDetailMapper;
import cn.zswltech.mithras.riskcontrol.metric.AbstractMetricComputer;
import cn.zswltech.mithras.riskcontrol.metric.MetricComputeEvent;
import cn.zswltech.mithras.riskcontrol.metric.SubscribeSupporter;
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
    private RiskMetricFactorQueryService factorService;

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
        RiskMetricFactorValue factor = factorService.newestFactor("资产总计@期末余额", "资产负债表", event.getFactorQueryDate());
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
