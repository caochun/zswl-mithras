package cn.zswltech.mithras.metric.job;
import cn.zswltech.mithras.customer.enums.CorpAddressType;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapUtil;
import cn.zswltech.mithras.collection.mapper.CollectionBaseInfoMapper;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.projectprocess.mapper.projreview.ProjReviewBaseInfoMapper;
import cn.zswltech.mithras.metric.service.RiskMetricDictService;
import cn.zswltech.mithras.metric.service.RiskMetricTimedService;
import cn.zswltech.mithras.metric.mapper.model.RiskMetricDict;
import cn.zswltech.mithras.metric.mapper.model.RiskMetricTimed;
import cn.zswltech.mithras.foundation.enums.CashFlowItemEnum;
import cn.zswltech.mithras.foundation.enums.common.RecordStatus;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.mapper.corp.CorpCommerceInfoMapper;
import cn.zswltech.mithras.customer.mapper.corp.IndustryTypeMapper;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.customer.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.customer.model.client.IndustryType;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.foundation.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.hutool.core.util.ObjectUtil.isNotNull;
import static cn.hutool.core.util.ObjectUtil.isNull;
import static cn.zswltech.mithras.metric.enums.risk.index.RiskMetricLevel5.NORMAL;
import static cn.zswltech.mithras.metric.enums.risk.index.RiskMetricLevel5.SUSPICIOUS;

/**
 * @author yibin
 * 配置在 MonthlyTaskGenerator之后
 */
@Slf4j
@Component
public class MonthlyMetricTimedGenerator {
    @Resource
    RiskMetricTimedService metricTimedService;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private ProjReviewBaseInfoMapper reviewBaseInfoMapper;
    @Resource
    private ClientMapper clientMapper;
    @Resource
    private CorpCommerceInfoMapper commerceInfoMapper;
    @Resource
    private IndustryTypeMapper industryTypeMapper;
    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;
    @Resource
    private RiskMetricDictService metricDictService;

    @XxlJob("monthlyMetricTimedGenerate")
    @Transactional(rollbackFor = Exception.class)
    public void generate() {
        log.info("每月自动存数-定时器开始执行");
        try {
            LocalDate date = LocalDate.now().minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
            List<ContractBaseInfo> rentingContractList = contractBaseInfoMapper.selectList(Wrappers.<ContractBaseInfo>lambdaQuery()
                    .eq(ContractBaseInfo::getContractStatus, ContractStatus.START_RENT.name())
            );
            Set<String> projCodeSet = rentingContractList.stream().map(ContractBaseInfo::getProjCode).collect(Collectors.toSet());
            Map<String, ProjReviewBaseInfo> reviewMap = reviewBaseInfoMapper.selectList(Wrappers.<ProjReviewBaseInfo>lambdaQuery()
                    .in(ProjReviewBaseInfo::getProjCode, projCodeSet).notIn(ProjReviewBaseInfo::getProjReviewStatus, ListUtil.toList(RecordStatus.CLOSED.name(), RecordStatus.EXPIRE.name()))
            ).stream().collect(Collectors.toMap(ProjReviewBaseInfo::getProjCode, e -> e));
            Map<Long, Client> clientMap = clientMapper.selectList(Wrappers.<Client>lambdaQuery()
                    .in(Client::getId, reviewMap.values().stream().map(ProjReviewBaseInfo::getClientId).collect(Collectors.toSet()))
            ).stream().collect(Collectors.toMap(Client::getId, e -> e));
            Map<Long, CorpCommerceInfo> commerceInfoMap = commerceInfoMapper.selectList(Wrappers.<CorpCommerceInfo>lambdaQuery()
                    .in(CorpCommerceInfo::getClientId, clientMap.values().stream().map(Client::getId).collect(Collectors.toSet()))
            ).stream().collect(Collectors.toMap(CorpCommerceInfo::getClientId, e -> e));
            Map<Long, Client> belongGroupMap = clientMapper.selectList(Wrappers.<Client>lambdaQuery()
                    .in(Client::getId, commerceInfoMap.values().stream().map(CorpCommerceInfo::getBelongGroupClientId).collect(Collectors.toSet()))
            ).stream().collect(Collectors.toMap(Client::getId, e -> e));
        /*Map<Long, CorpAddressInfo> addressInfoMap = addressInfoMapper.selectList(Wrappers.<CorpAddressInfo>lambdaQuery()
                .in(CorpAddressInfo::getClientId, clientMap.values().stream().map(Client::getId).collect(Collectors.toSet()))
                .eq(CorpAddressInfo::getAddressType, CorpAddressType.REGISTRY_ADDRESS.name())
        ).stream().collect(Collectors.toMap(CorpAddressInfo::getClientId, e -> e));*/
            Map<Long/*proj review id*/, Long> leftCapitalMap = listProRemainPrinci(reviewMap.values().stream().map(ProjReviewBaseInfo::getId).collect(Collectors.toList()));
            Map<Long/*proj review id*/, Long> overdueAmountMap = listProOverdueAmount(reviewMap.values().stream().map(ProjReviewBaseInfo::getId).collect(Collectors.toList()));

            //
            Set<String> relatedSet = metricDictService.list(Wrappers.<RiskMetricDict>lambdaQuery().eq(RiskMetricDict::getDictType, "RELATED_CORP"))
                    .stream().map(e -> e.getDictKey().trim()).collect(Collectors.toSet());


            List<RiskMetricTimed> list = new ArrayList<>(projCodeSet.size());
            for (String projCode : projCodeSet) {
                RiskMetricTimed timed = new RiskMetricTimed();
                ProjReviewBaseInfo review = reviewMap.get(projCode);
                if (isNull(review)) {
                    continue;
                }
                Client client = clientMap.get(review.getClientId());
                if (isNull(client)) {
                    continue;
                }
                CorpCommerceInfo commerceInfo = commerceInfoMap.get(client.getId());
                IndustryType industryType = industryTypeMapper.selectOne(Wrappers.<IndustryType>lambdaQuery().eq(IndustryType::getCode, commerceInfo.getIndustryType()));

                timed.setRentingProjCode(projCode);
                timed.setProjName(review.getProjName());
                timed.setProjType(review.getProjectType());
                if (isNotNull(industryType)) {
                    timed.setIndustryType(industryType.getCode());
                    timed.setIndustryTypeName(industryType.getDisplay());
                }
                //除了河南骏化是可以，其他都是正常。暂时写死，todo
                timed.setLevel5Type(client.getUscCode().equals("9141000075227790XK") ? SUSPICIOUS.name() : NORMAL.name());
                timed.setClientId(client.getId());
                timed.setClientName(client.getClientName());
                if (null != commerceInfo.getBelongGroupClientId()) {
                    timed.setBelongGroupId(commerceInfo.getBelongGroupClientId());
                    Client groupClient = belongGroupMap.get(commerceInfo.getBelongGroupClientId());
                    if (null != groupClient) {
                        timed.setBelongGroupName(groupClient.getClientName());
                    }
                }
                timed.setRelated(relatedSet.contains(client.getUscCode()));
                timed.setLeftCapital(leftCapitalMap.get(review.getId()));
                timed.setOverdueTotal(overdueAmountMap.get(review.getId()));
                timed.setDataTime(date);
                //
                if (timed.getLeftCapital() == null && timed.getOverdueTotal() == null) {
                    log.warn("在租项目{}的剩余本金和逾期罚息均为空，跳过.", review.getProjName());
                }
                list.add(timed);
            }
            metricTimedService.remove(Wrappers.<RiskMetricTimed>lambdaQuery().eq(RiskMetricTimed::getDataTime, date));
            metricTimedService.saveBatch(list);
        } catch (Exception e) {
            log.error("定期存数失败", e);
        }
        log.info("每月自动存数-定时器执行完成");
    }

    private Map<Long, Long> listProRemainPrinci(List<Long> projIds) {
        return calculation(projIds, Boolean.FALSE);
    }

    private Map<Long, Long> listProOverdueAmount(List<Long> projReviewIds) {
        return calculation(projReviewIds, Boolean.TRUE);
    }

    private Map<Long, Long> calculation(List<Long> projReviewIds, Boolean isExpired) {
        if (isNull(projReviewIds) || projReviewIds.isEmpty()) {
            return MapUtil.empty();
        }
        List<ContractBaseInfo> contractBaseInfos = contractBaseInfoMapper.selectList(Wrappers.<ContractBaseInfo>lambdaQuery()
                .in(ContractBaseInfo::getProjReviewId, projReviewIds));
        if (contractBaseInfos.isEmpty()) {
            return MapUtil.empty();
        }
        List<Long> contractIds = contractBaseInfos.stream().map(ContractBaseInfo::getId).collect(Collectors.toList());
        List<CollectionBaseInfo> collectionBaseInfos = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                .in(CollectionBaseInfo::getContractId, contractIds));
        if (collectionBaseInfos.isEmpty()) {
            return MapUtil.empty();
        }
        Map<Long, Long> rsp = new HashMap<>(projReviewIds.size());
        Map<Long, Long> contractId2projId = contractBaseInfos.stream().collect(Collectors.toMap(ContractBaseInfo::getId, ContractBaseInfo::getProjReviewId));
        for (CollectionBaseInfo baseInfo : collectionBaseInfos) {
            if (isExpired && LocalDate.now().isBefore(baseInfo.getPlanCollectionDate())) {
                continue;
            }
            Long projReviewId = contractId2projId.get(baseInfo.getContractId());
            if (isNull(rsp.get(projReviewId))) {
                rsp.put(projReviewId, LongUtil.null2zero(baseInfo.getPrincipal()) - LongUtil.null2zero(baseInfo.getCollectionPrincipal()));
            } else {
                rsp.put(projReviewId,
                        rsp.get(projReviewId) +
                                LongUtil.null2zero(baseInfo.getPrincipal()) - LongUtil.null2zero(baseInfo.getCollectionPrincipal()));
            }
            if (isExpired) {
                rsp.put(projReviewId,
                        rsp.get(projReviewId) +
                                LongUtil.null2zero(baseInfo.getInterest()) - LongUtil.null2zero(baseInfo.getCollectionInterest()));
            }
        }
        return rsp;
    }
}
