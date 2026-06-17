package cn.zswltech.mithras.application.orchestration.adapter.metric;

import cn.zswltech.mithras.metric.service.RiskStrategyCurrentValuePort;
import cn.zswltech.mithras.metric.service.RiskStrategyCurrentValueSnapshot;
import cn.zswltech.mithras.riskcontrol.strategy.RiskControlStrategy;
import cn.zswltech.mithras.riskcontrol.strategy.RiskControlStrategyMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RiskStrategyCurrentValuePortAdapter implements RiskStrategyCurrentValuePort {

    @Resource
    private RiskControlStrategyMapper riskControlStrategyMapper;

    @Override
    public List<RiskStrategyCurrentValueSnapshot> listCurrentValues() {
        return riskControlStrategyMapper.selectList(Wrappers.<RiskControlStrategy>lambdaQuery())
                .stream()
                .map(item -> new RiskStrategyCurrentValueSnapshot(
                        item.getMetricCode(),
                        item.getCurrentValueOne(),
                        item.getValueUnitOne()))
                .collect(Collectors.toList());
    }
}
