package cn.zswltech.mithras.riskcontrol.metric.subscriber;

import cn.zswltech.mithras.contract.mapper.lib.contract.ContractBaseInfoLibMapper;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfoLib;
import cn.zswltech.mithras.riskcontrol.strategy.RiskControlStrategy;
import cn.zswltech.mithras.riskcontrol.metric.AbstractMetricComputer;
import cn.zswltech.mithras.riskcontrol.metric.MetricComputeEvent;
import cn.zswltech.mithras.riskcontrol.metric.SubscribeSupporter;
import cn.zswltech.mithras.basedata.util.DateUtil;
import cn.zswltech.mithras.service.util.LongUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.eventbus.AllowConcurrentEvents;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 1.风控行业分类不为（公用事业类、民生消费类、集团协同业务）
 * R=授信金额（客户第一次生效的合同）
 *
 * @author zhaozhengkang
 */
@Component
@Slf4j
public class MetricComputer16A10000396_ZL044 extends AbstractMetricComputer implements SubscribeSupporter<MetricComputeEvent> {

    @Resource
    private ContractBaseInfoLibMapper contractBaseInfoLibMapper;


    @Override
    public String getMetricCode() {
        return "A10000396_ZL044";
    }

    @AllowConcurrentEvents
//    @Subscribe
    @Override
    public void onSubscribe(MetricComputeEvent metricComputeEvent) {
        log.info("MetricComputeA10000396_ZL044 onSubscribe");
        compute(metricComputeEvent);
    }

    @Override
    public void calculate(MetricComputeEvent event, RiskControlStrategy strategy) {
        // 1.风控行业分类不为（公用事业类、民生消费类、集团协同业务）
        List<ContractBaseInfoLib> contractNewestLibs = contractBaseInfoLibMapper
                .listNewestContractByPreviewIds(null);
        Map<Long, List<ContractBaseInfoLib>> collect = contractNewestLibs.stream()
                .filter(v -> v.getDataCreateTime().isBefore(DateUtil.endOfDay(event.getSnapshotDate())))
                .collect(Collectors.groupingBy(ContractBaseInfo::getClientId));
        Iterator<Map.Entry<Long, List<ContractBaseInfoLib>>> it = collect.entrySet().iterator();
        Long maxApplyCreditAmount = 0L;
        while (it.hasNext()) {
            Map.Entry<Long, List<ContractBaseInfoLib>> entry = it.next();
            if (entry.getValue().size() != 1) {
                it.remove();
                continue;
            }
            Long thisApplyCreditAmount = LongUtil.null2zero(entry.getValue().get(0).getApplyCreditAmount());
            maxApplyCreditAmount = Math.max(maxApplyCreditAmount, thisApplyCreditAmount);
        }
        MetricCompute_16Context calculateCtx = new MetricCompute_16Context();
        calculateCtx.setMaxApplyCreditAmount(maxApplyCreditAmount);
        strategy.setQuickContext(JSON.toJSONString(calculateCtx));
        strategy.setCurrentValueOneDecimal(new BigDecimal(maxApplyCreditAmount));

    }

    @Data
    static class MetricCompute_16Context {
        private Long maxApplyCreditAmount;
    }
}
