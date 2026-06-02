package cn.zswltech.mithras.service.service.riskcontrol.eventbus.subscriber;

import cn.zswltech.mithras.metric.mapper.model.RiskMetricFactor;
import cn.zswltech.mithras.metric.service.RiskMetricFactorService;
import cn.zswltech.mithras.service.enums.riskcontrol.RiskControlIndustryClassify;
import cn.zswltech.mithras.service.mapper.lib.client.CorpAddressInfoLibMapper;
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
import java.util.*;


/**
 * R=按集团维度（客户注册地址不为「浙江省」和风控行业分类不为「集团协同业务」）的剩余本金余额（/资产负债表「所有者权益（或股东权益）合计@期末余额」
 * 风险策略调整，修改计算逻辑：
 * R=按集团维度（客户注册地址不为「浙江省」和风控行业分类不为「集团协同业务」）的剩余本金之和
 *
 * @author zhaozhengkang
 */
@Component
@Slf4j
public class MetricComputer9A10000396_ZL005 extends AbstractMetricComputer implements SubscribeSupporter<MetricComputeEvent> {
    public static final String FACTOR_NAME = "所有者权益（或股东权益）合计@期末余额";
    public static final String FACTOR_TABLE = "资产负债表";
    @Resource
    private CorpAddressInfoLibMapper corpAddressInfoLibMapper;
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
        return "A10000396_ZL005";
    }

    @AllowConcurrentEvents
    @Subscribe
    @Override
    public void onSubscribe(MetricComputeEvent metricComputeEvent) {
        log.info("MetricComputeA10000396_ZL005 onSubscribe");
        compute(metricComputeEvent);
    }

    @Override
    public void calculate(MetricComputeEvent event, RiskControlStrategy strategy) {
        long maxRemainingPrincipal = 0L;
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
        // 从企业地址表查询最新版本的浙江省的客户id
        Set<Long> clientsInZhejiang = clientService.getSpecifyProvinceClientIds(Collections.singletonList("330000"));
        // 从企业商务信息表查询最新版本的非浙江省的客户id
        CorpCommerceInfoLibDto dto = new CorpCommerceInfoLibDto();
        dto.setNotInRiskControlIndustryClassify(Collections.singletonList(RiskControlIndustryClassify.INTRA_GROUP_COLLABORATION.name()));
        dto.setNotInClientIds(clientsInZhejiang);
        List<CorpCommerceInfoLib> corpCommerceInfoLibs = corpCommerceInfoLibMapper.listNewestCommerceInfo(dto);
        Map<Long, Long> client2Group = new HashMap<>();
        for (CorpCommerceInfoLib lib : corpCommerceInfoLibs) {
            if (lib.getBelongGroupClientId() != null && lib.getBelongGroupClientId() != -1L) {
                client2Group.put(lib.getClientId(), lib.getBelongGroupClientId());
            }
        }
        RemainingPrincipalQueryDto queryDto = new RemainingPrincipalQueryDto();
        queryDto.setClientIds(client2Group.keySet());
        queryDto.setEndDate(event.getSnapshotDate());
        Map<Long, Long> clientIdToRemainingPrincipal =
                remainingPrincipalServiceImpl.remainingPrincipalGroupByClientId(queryDto);
        Map<Long, Long> groupIdToRemainingPrincipal = new HashMap<>();
        for (Map.Entry<Long, Long> entry : clientIdToRemainingPrincipal.entrySet()) {
            Long groupId = client2Group.get(entry.getKey());
            groupIdToRemainingPrincipal.put(groupId,
                    groupIdToRemainingPrincipal.getOrDefault(groupId, 0L) + entry.getValue());
            maxRemainingPrincipal = Math.max(maxRemainingPrincipal, groupIdToRemainingPrincipal.get(groupId));
        }
//        // 计算新值结果保留两位小数
//        BigDecimal res = new BigDecimal(maxRemainingPrincipal)
//                .divide(new BigDecimal(factor.getFactorValue()), 4, RoundingMode.HALF_UP)
//                .multiply(new BigDecimal(10000));
//        strategy.setCurrentValueOne(res.longValue());
        strategy.setCurrentValueTwoDecimal(new BigDecimal(maxRemainingPrincipal));
        MetricCompute_9Context calculateCtx = new MetricCompute_9Context();
        calculateCtx.setGroupIdToRemainingPrincipal(groupIdToRemainingPrincipal);
        calculateCtx.setMaxRemainingPrincipal(maxRemainingPrincipal);
//        calculateCtx.setFactorValue(factor.getFactorValue());
        strategy.setQuickContext(JSON.toJSONString(calculateCtx));
    }

    @Data
    static class MetricCompute_9Context {
        private Long factorValue;
        private Long maxRemainingPrincipal;
        private Map<Long, Long> groupIdToRemainingPrincipal;
    }
}
