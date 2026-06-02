package cn.zswltech.mithras.service.service.riskcontrol.eventbus.subscriber;

import cn.zswltech.mithras.service.enums.riskcontrol.RiskControlIndustryClassify;
import cn.zswltech.mithras.riskcontrol.eventbus.MetricComputeEvent;
import cn.zswltech.mithras.riskcontrol.eventbus.SubscribeSupporter;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;


/**
 * 1.风控行业分类为「5G、IDC、通信服务等」
 * R=满足以上条件的「未核销本金之和」
 * 风险策略调整，修改后的逻辑：
 * R=风控行业分类为「5G、IDC、通信服务等」的「剩余本金之和 - 保证金余额」
 *
 * @author zhaozhengkang
 */
@Component
@Slf4j
public class MetricComputer27J10000396_JC47578 extends RiskControlClassifyMetricComputer
        implements SubscribeSupporter<MetricComputeEvent> {

    @Override
    public String getMetricCode() {
        return "J10000396_JC47578";
    }

    @Override
    public List<String> getIndustryClassify() {
        return Collections.singletonList(RiskControlIndustryClassify.INFORMATION_INDUSTRY.name());
    }

    @AllowConcurrentEvents
    @Subscribe
    @Override
    public void onSubscribe(MetricComputeEvent metricComputeEvent) {
        log.info("MetricComputeJ10000396_JC47578 onSubscribe");
        
        compute(metricComputeEvent);
    }
}
