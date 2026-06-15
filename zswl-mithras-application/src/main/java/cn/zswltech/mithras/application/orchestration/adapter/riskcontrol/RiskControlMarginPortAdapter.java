package cn.zswltech.mithras.application.orchestration.adapter.riskcontrol;

import cn.hutool.core.collection.CollectionUtil;
import cn.zswltech.mithras.foundation.util.LongUtil;
import cn.zswltech.mithras.margin.service.MarginBaseInfoService;
import cn.zswltech.mithras.riskcontrol.application.port.RiskControlMarginPort;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RiskControlMarginPortAdapter implements RiskControlMarginPort {

    @Resource
    private MarginBaseInfoService marginBaseInfoService;

    @Override
    public Map<Long, Long> getClientMarginBalances(Collection<Long> clientIds) {
        if (CollectionUtil.isEmpty(clientIds)) {
            return Collections.emptyMap();
        }
        return marginBaseInfoService.getClientMarginBalances(clientIds);
    }

    @Override
    public BigDecimal totalDepositByClientIds(Set<Long> clientIds) {
        return getClientMarginBalances(clientIds).values().stream()
                .map(LongUtil::null2zero)
                .map(BigDecimal::new)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public Map<Long, BigDecimal> totalDepositGroupByClientIds(Set<Long> clientIds) {
        return getClientMarginBalances(clientIds).entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> BigDecimal.valueOf(LongUtil.null2zero(e.getValue()))));
    }

    @Override
    public Map<Long, Long> depositGroupByClientId(Set<Long> clientIds) {
        return getClientMarginBalances(clientIds);
    }

    @Override
    public Long sumReportMarginAmount(Collection<Long> contractIds) {
        return marginBaseInfoService.sumReportMarginAmount(contractIds);
    }
}
