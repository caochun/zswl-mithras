package cn.zswltech.mithras.service.service.riskcontrol.eventbus.subscriber;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.riskcontrol.common.RiskControlIndustryClassify;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.lib.client.CorpCommerceInfoLibMapper;
import cn.zswltech.mithras.projectprocess.mapper.lib.projreview.ProjReviewAocPriceLibMapper;
import cn.zswltech.mithras.projectprocess.mapper.lib.projreview.ProjReviewBaseInfoLibMapper;
import cn.zswltech.mithras.projectprocess.mapper.lib.projreview.ProjReviewFactoringPriceLibMapper;
import cn.zswltech.mithras.projectprocess.mapper.lib.projreview.ProjReviewLeasePriceLibMapper;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.ClientBaseModel;
import cn.zswltech.mithras.projectprocess.mapper.model.projreview.*;
import cn.zswltech.mithras.riskcontrol.strategy.RiskControlStrategy;
import cn.zswltech.mithras.service.service.riskcontrol.AbstractMetricComputer;
import cn.zswltech.mithras.customer.application.riskcontrol.dto.CorpCommerceInfoLibDto;
import cn.zswltech.mithras.projectprocess.service.riskcontrol.dto.ProjReviewPriceDto;
import cn.zswltech.mithras.riskcontrol.metric.MetricComputeEvent;
import cn.zswltech.mithras.riskcontrol.metric.SubscribeSupporter;
import cn.zswltech.mithras.basedata.util.DateUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.eventbus.AllowConcurrentEvents;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 1.风控行业分类为（集团协同业务 / 船舶、光伏行业）
 * R=满足以上条件的合同内「报价方案-租赁期限（月）」/12
 *
 * @author zhaozhengkang
 */
@Component
@Slf4j
public class MetricComputer18A10000396_JC031 extends AbstractMetricComputer implements SubscribeSupporter<MetricComputeEvent> {

    @Resource
    private ProjReviewBaseInfoLibMapper projReviewBaseInfoLibMapper;
    @Resource
    private CorpCommerceInfoLibMapper corpCommerceInfoLibMapper;
    @Resource
    private ProjReviewLeasePriceLibMapper leasePriceLibMapper;
    @Resource
    private ProjReviewFactoringPriceLibMapper factoringPriceLibMapper;
    @Resource
    private ProjReviewAocPriceLibMapper aocPriceLibMapper;

    @Override
    public String getMetricCode() {
        return "A10000396_JC031";
    }

    @AllowConcurrentEvents
//    @Subscribe
    @Override
    public void onSubscribe(MetricComputeEvent metricComputeEvent) {
        log.info("MetricComputeA10000396_JC031 onSubscribe");
        compute(metricComputeEvent);
    }

    @Override
    public void calculate(MetricComputeEvent event, RiskControlStrategy strategy) {
        // 1.风控行业分类为（船舶、光伏行业 或 集团协同业务）
        CorpCommerceInfoLibDto commerceInfoLibDto = new CorpCommerceInfoLibDto();
        commerceInfoLibDto.setInRiskControlIndustryClassify(Arrays.asList(RiskControlIndustryClassify.NEW_MATERIALS.name(), RiskControlIndustryClassify.INTRA_GROUP_COLLABORATION.name()));
        Set<Long> targetClients = corpCommerceInfoLibMapper
                .listNewestCommerceInfo(commerceInfoLibDto).stream()
                .map(ClientBaseModel::getClientId).collect(Collectors.toSet());
        // 2.查询项目目标客户的项目审批
        Set<Long> projReviewIds = new HashSet<>();
        if (ObjectUtil.isNotEmpty(targetClients)) {
            projReviewIds = projReviewBaseInfoLibMapper
                    .listNewestPreviewByClientIds(targetClients, null)
                    .stream()
                    .filter(v -> v.getDataCreateTime().isBefore(DateUtil.endOfDay(event.getSnapshotDate())))
                    .map(ProjReviewBaseInfoLib::getOriginId).collect(Collectors.toSet());
        }
        //3.查询报价方案
        Map<Long, Integer> leaseCount = new HashMap<>();
        Map<Long, Integer> factoringCount = new HashMap<>();
        Map<Long, Integer> aocCount = new HashMap<>();
        if (ObjectUtil.isNotEmpty(projReviewIds)) {
            ProjReviewPriceDto priceDto = new ProjReviewPriceDto();
            priceDto.setProjReviewIds(projReviewIds);
            leaseCount = leasePriceLibMapper.listNewestPrice(priceDto)
                    .stream()
                    .filter(lib -> lib.getLeaseMonthCount() != null)
                    .collect(Collectors.toMap(ProjReviewLeasePriceLib::getProjectId, ProjReviewLeasePrice::getLeaseMonthCount));
            factoringCount = factoringPriceLibMapper.listNewestPrice(priceDto)
                    .stream()
                    .filter(lib -> lib.getFactoringCreditTerm() != null)
                    .collect(Collectors.toMap(ProjReviewFactoringPriceLib::getProjectId, ProjReviewFactoringPriceLib::getFactoringCreditTerm));
            aocCount = aocPriceLibMapper.listNewestPrice(priceDto)
                    .stream()
                    .filter(lib -> lib.getCreditAmountLoop() != null)
                    .collect(Collectors.toMap(ProjReviewAocPriceLib::getProjectId, ProjReviewAocPriceLib::getCreditAmountLoop));
        }
        //4.计算
        Integer maxMonthCount = 0;
        for (Map.Entry<Long, Integer> entry : leaseCount.entrySet()) {
            if (entry.getValue() > maxMonthCount) {
                maxMonthCount = entry.getValue();
            }
        }
        for (Map.Entry<Long, Integer> entry : factoringCount.entrySet()) {
            if (entry.getValue() > maxMonthCount) {
                maxMonthCount = entry.getValue();
            }
        }
        for (Map.Entry<Long, Integer> entry : aocCount.entrySet()) {
            if (entry.getValue() > maxMonthCount) {
                maxMonthCount = entry.getValue();
            }
        }
        //5.保存
        strategy.setCurrentValueOne(maxMonthCount / 12 * 10000L);
        MetricCompute_18Context context = new MetricCompute_18Context();
        context.setMaxMonthCount((long) maxMonthCount);
        strategy.setQuickContext(JSON.toJSONString(context));
    }

    @Data
    static class MetricCompute_18Context {
        private Long maxMonthCount;
    }

}
