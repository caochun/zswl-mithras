package cn.zswltech.mithras.service.service.riskcontrol.eventbus.subscriber;

import cn.zswltech.mithras.metric.mapper.model.RiskMetricFactor;
import cn.zswltech.mithras.metric.service.RiskMetricFactorService;
import cn.zswltech.mithras.service.enums.riskcontrol.RiskControlIndustryClassify;
import cn.zswltech.mithras.service.mapper.lib.client.CorpCommerceInfoLibMapper;
import cn.zswltech.mithras.service.mapper.model.client.CorpCommerceInfoLib;
import cn.zswltech.mithras.service.mapper.model.riskcontrol.RiskControlStrategy;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.riskcontrol.AbstractMetricComputer;
import cn.zswltech.mithras.service.service.riskcontrol.RemainingPrincipalServiceImpl;
import cn.zswltech.mithras.service.service.riskcontrol.dto.CorpCommerceInfoLibDto;
import cn.zswltech.mithras.service.service.riskcontrol.dto.RemainingPrincipalQueryDto;
import cn.zswltech.mithras.riskcontrol.eventbus.MetricComputeEvent;
import cn.zswltech.mithras.riskcontrol.eventbus.SubscribeSupporter;
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
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * R=按单个客户维度（客户注册地址不为「浙江省」和风控行业分类不为「集团协同业务」）的剩余本金余额/资产负债表「所有者权益（或股东权益）合计@期末余额」
 * 风险策略调整，修改计算逻辑：
 * R=按单个客户维度（客户注册地址不为「浙江省」和风控行业分类不为「集团协同业务」）的剩余本金余额
 *
 * @author zhaozhengkang
 */
@Component
@Slf4j
public class MetricComputer8A10000396_ZL004 extends AbstractMetricComputer implements SubscribeSupporter<MetricComputeEvent> {
    public static final String FACTOR_NAME = "所有者权益（或股东权益）合计@期末余额";
    public static final String FACTOR_TABLE = "资产负债表";

    @Resource
    private CorpCommerceInfoLibMapper corpCommerceInfoLibMapper;
    @Resource
    private RemainingPrincipalServiceImpl remainingPrincipalServiceImpl;
    @Resource
    private RiskMetricFactorService factorService;
    @Resource
    private ClientService clientService;

    @Override
    public String getMetricCode() {
        return "A10000396_ZL004";
    }

    @AllowConcurrentEvents
    @Subscribe
    @Override
    public void onSubscribe(MetricComputeEvent metricComputeEvent) {
        log.info("MetricComputeA10000396_ZL004 onSubscribe");
        compute(metricComputeEvent);
    }

    @Override
    public void calculate(MetricComputeEvent event, RiskControlStrategy strategy) {
        Long maxRemainingPrincipal = 0L;
//        RiskMetricFactor factor = factorService.getOne(Wrappers.<RiskMetricFactor>lambdaQuery()
//                .eq(RiskMetricFactor::getFactorName, FACTOR_NAME)
//                .eq(RiskMetricFactor::getFactorTable, FACTOR_TABLE)
//                .ge(RiskMetricFactor::getFactorDate, event.getFactorQueryDate())
//                .orderByDesc(RiskMetricFactor::getFactorDate)
//                .last("limit 1"));
//        if (factor == null) {
//            strategy.setNullReason("上月指标因子未导入");
//            strategy.setCurrentValueOne(null);
//            strategy.setCurrentValueTwo(null);
//            return;
//        }
        // 从企业地址表查询最新版本的非浙江省的客户id
        Set<Long> clientsInZhejiang = clientService.getSpecifyProvinceClientIds(Collections.singletonList("330000"));
        CorpCommerceInfoLibDto dto = new CorpCommerceInfoLibDto();
        dto.setNotInRiskControlIndustryClassify(Collections.singletonList(RiskControlIndustryClassify.INTRA_GROUP_COLLABORATION.name()));
        dto.setNotInClientIds(clientsInZhejiang);
        Set<Long> targetClientIds = corpCommerceInfoLibMapper.listNewestCommerceInfo(dto).stream().map(CorpCommerceInfoLib::getClientId).collect(Collectors.toSet());
        RemainingPrincipalQueryDto queryDto = new RemainingPrincipalQueryDto();
        queryDto.setClientIds(targetClientIds);
        queryDto.setEndDate(event.getSnapshotDate());
        Map<Long, Long> clientIdToRemainingPrincipal =
                remainingPrincipalServiceImpl.remainingPrincipalGroupByClientId(queryDto);
        for (Long remainingPrincipal : clientIdToRemainingPrincipal.values()) {
            maxRemainingPrincipal = Math.max(maxRemainingPrincipal, remainingPrincipal);
        }
//        // 计算新值结果保留两位小数
//        BigDecimal res = new BigDecimal(maxRemainingPrincipal)
//                .divide(new BigDecimal(factor.getFactorValue()), 4, RoundingMode.HALF_UP)
//                .multiply(new BigDecimal(10000));
//        strategy.setCurrentValueOne(res.longValue());
        strategy.setCurrentValueTwoDecimal(new BigDecimal(maxRemainingPrincipal));
        MetricCompute_8Context calculateCtx = new MetricCompute_8Context();
//        calculateCtx.setFactorValue(factor.getFactorValue());
        calculateCtx.setClientIdToRemainingPrincipal(clientIdToRemainingPrincipal);
        calculateCtx.setMaxRemainingPrincipal(maxRemainingPrincipal);
        strategy.setQuickContext(JSON.toJSONString(calculateCtx));
    }

    @Data
    static class MetricCompute_8Context {
        private Map<Long, Long> clientIdToRemainingPrincipal;
        private Long maxRemainingPrincipal;
        private Long factorValue;
    }
}
