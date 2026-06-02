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
 * 1.风控行业分类为「新材料、新科技、智能制造等高端装备制造行业」
 * R=满足以上条件的「未核销本金之和」
 *
 * @author zhaozhengkang
 * @deprecated 何栋要求删除（逻辑代码先保留）
 */
@Deprecated
//@Component
@Slf4j
public class MetricComputer28J10000396_JC47588 extends RiskControlClassifyMetricComputer
        implements SubscribeSupporter<MetricComputeEvent> {


    @Override
    public String getMetricCode() {
        return "J10000396_JC47588";
    }

    @Override
    public List<String> getIndustryClassify() {
        return Collections.singletonList(RiskControlIndustryClassify.NEW_MATERIALS.name());
    }

    @AllowConcurrentEvents
    @Subscribe
    @Override
    public void onSubscribe(MetricComputeEvent metricComputeEvent) {
        log.info("MetricComputeJ10000396_JC47588 onSubscribe");

        compute(metricComputeEvent);
    }


}
