package cn.zswltech.mithras.riskcontrol.concentration;

import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.riskcontrol.*;
import cn.zswltech.mithras.riskcontrol.exposure.RemainingPrincipalService;
import cn.zswltech.mithras.riskcontrol.metric.RiskMetricFactorQueryService;
import cn.zswltech.mithras.riskcontrol.metric.RiskMetricFactorValue;
import cn.zswltech.mithras.riskcontrol.concentration.RiskControlConcentrationClientConverter;
import cn.zswltech.mithras.riskcontrol.common.RiskControlIndustryClassify;
import cn.zswltech.mithras.assetclassify.mapper.lib.AssetClassifyClientAuxiliaryLibMapper;
import cn.zswltech.mithras.customer.mapper.client.ClientMapper;
import cn.zswltech.mithras.customer.mapper.lib.client.CorpAddressInfoLibMapper;
import cn.zswltech.mithras.customer.mapper.lib.client.CorpCommerceInfoLibMapper;
import cn.zswltech.mithras.assetclassify.model.AssetClassifyClient;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.customer.model.client.ClientBaseModel;
import cn.zswltech.mithras.customer.model.client.CorpAddressInfo;
import cn.zswltech.mithras.customer.model.client.CorpCommerceInfoLib;
import cn.zswltech.mithras.riskcontrol.strategy.RiskControlStrategy;
import cn.zswltech.mithras.riskcontrol.strategy.RiskControlMetricStrategyService;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.margin.service.MarginBaseInfoService;
import cn.zswltech.mithras.customer.versioning.dto.CorpCommerceInfoLibDto;
import cn.zswltech.mithras.riskcontrol.exposure.RemainingPrincipalQueryDto;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.collection.CollUtil.isNotEmpty;
import static java.math.RoundingMode.HALF_UP;

/**
 * @author zhaozhengkang
 * @description 客户集中度
 * @date 2023-02-27
 */
@Service
public class RiskControlConcentrationClientService
        extends ServiceImpl<RiskControlConcentrationClientMapper, RiskControlConcentrationClient> {
    public static final String FACTOR_NAME = "所有者权益（或股东权益）合计@期末余额";
    public static final String FACTOR_TABLE = "资产负债表";

    private static final Long CLIENT_SPONSOR_NULL_PLACEHOLDER = -1L;

    @Resource
    private CorpCommerceInfoLibMapper corpCommerceInfoLibMapper;
    @Resource
    private CorpAddressInfoLibMapper corpAddressInfoLibMapper;
    @Resource
    private AssetClassifyClientAuxiliaryLibMapper assetClassifyClientAuxiliaryLibMapper;
    @Resource
    private RemainingPrincipalService remainingPrincipalService;
    @Resource
    private RiskControlConcentrationClientConverter baseConverter;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private RiskMetricFactorQueryService factorService;
    @Resource
    private RiskControlConcentrationGroupService concentrationGroupService;
    @Resource
    private RiskControlMetricStrategyService strategyService;
    @Resource
    private RiskControlConcentrationUserScopeService userScopeService;
    @Resource
    private ClientMapper clientMapper;
    @Resource
    private MarginBaseInfoService marginBaseInfoService;

    @XxlJob("riskControlConcentrationClientJobHandler")
    public void riskControlConcentrationClientJobHandler() {
        try {
            CorpCommerceInfoLibDto commerceInfoLibDto = new CorpCommerceInfoLibDto();
            // 查询所有法人客户的最新的工商信息
            List<CorpCommerceInfoLib> corpCommerceInfoLibs =
                    corpCommerceInfoLibMapper.listNewestCommerceInfo(commerceInfoLibDto);
            // 查询客户名称
            List<Long> clientIds = new ArrayList<>();
            corpCommerceInfoLibs.stream().forEach(corpCommerceInfoLib -> {
                clientIds.add(corpCommerceInfoLib.getClientId());
                if (corpCommerceInfoLib.getBelongGroupClientId() != null && corpCommerceInfoLib.getBelongGroupClientId() > 0) {
                    clientIds.add(corpCommerceInfoLib.getBelongGroupClientId());
                }
            });
            Map<Long, String> clientId2Name = id2NameService.clientId2Name(clientIds);

            Map<Long, CorpCommerceInfoLib> clientsMap = corpCommerceInfoLibs.stream()
                    .collect(Collectors.toMap(ClientBaseModel::getClientId, item -> item, (k1, k2) -> k2));
            RemainingPrincipalQueryDto remainingPrincipalQueryDto = new RemainingPrincipalQueryDto();
            // 查询所有法人客户的最新注册地址
            Map<Long, String> registryAddress =
                    corpAddressInfoLibMapper.listNewestAddressByClientId(clientsMap.keySet()).stream()
                            .collect(Collectors.toMap(ClientBaseModel::getClientId, CorpAddressInfo::getProvince, (k1, k2) -> k2));
            // 查询指定客户的资产分类等级
            Map<Long, String> assertClassifyResult =
                    assetClassifyClientAuxiliaryLibMapper.listNewestClassifyLibByClientId(clientsMap.keySet()).stream()
                            .collect(Collectors.toMap(AssetClassifyClient::getClientId,
                                    AssetClassifyClient::getClassifyResult, (k1, k2) -> k2));

            remainingPrincipalQueryDto.setClientIds(clientsMap.keySet());
            // 计算所有客户的剩余本金
            Map<Long, Long> clientIdToRemaining =
                    remainingPrincipalService.remainingPrincipalGroupByClientId(remainingPrincipalQueryDto);
            // 计算所有存在逾期的客户的逾期金额
            Map<Long, Long> clientIdToOverdueAmount = remainingPrincipalService.overdueAmountGroupByClient();
            // 计算所有客户的保证金余额
            Map<Long, Long> clientIdToMargin = marginBaseInfoService.getClientMarginBalances(clientsMap.keySet());
            //获取客户信息
            Map<Long, Long> clientSponsorMap = clientMapper.selectBatchIds(clientsMap.keySet()).stream().filter(e -> Objects.nonNull(e.getBelongSponsorId())).collect(Collectors.toMap(Client::getId, Client::getBelongSponsorId));
            // 查询当月数据
            LocalDate fistDayOfMonth = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
            Map<Long, RiskControlConcentrationClient> clientConcentrations = baseMapper.selectList(Wrappers.<RiskControlConcentrationClient>lambdaQuery()
                    .eq(RiskControlConcentrationClient::getDateTimePoint, fistDayOfMonth)).stream().collect(Collectors.toMap(RiskControlConcentrationClient::getClientId, item -> item, (k1, k2) -> k2));
            // 查询风控因子
            RiskMetricFactorValue factor = factorService.newestFactor(FACTOR_NAME, FACTOR_TABLE, null);
            //查询风控策略
            Map<Long, RiskControlStrategy> concentrationStrategy = strategyService.list(
                            Wrappers.<RiskControlStrategy>lambdaQuery()
                                    .in(RiskControlStrategy::getId, Arrays.asList(6L, 7L, 8L, 9L)))
                    .stream().collect(Collectors.toMap(RiskControlStrategy::getId, item -> item, (k1, k2) -> k2));

            List<RiskControlConcentrationClient> updateList = new ArrayList<>();
            for (Map.Entry<Long, CorpCommerceInfoLib> entry : clientsMap.entrySet()) {
                Long clientId = entry.getKey();
                CorpCommerceInfoLib commerceInfoLib = entry.getValue();
                RiskControlConcentrationClient concentrationClient =
                        clientConcentrations.getOrDefault(clientId, new RiskControlConcentrationClient());
                concentrationClient.setClientId(clientId);
                concentrationClient.setClientName(clientId2Name.get(clientId));
                concentrationClient.setGroupId(commerceInfoLib.getBelongGroupClientId());
                if (commerceInfoLib.getBelongGroupClientId() != null && commerceInfoLib.getBelongGroupClientId() > 0) {
                    concentrationClient.setGroupName(clientId2Name.get(commerceInfoLib.getBelongGroupClientId()));
                }
                concentrationClient.setIsRelated(commerceInfoLib.getIsRelated());
                concentrationClient.setRiskControlIndustryClassify(commerceInfoLib.getRiskControlIndustryClassify());
                concentrationClient.setProvince(registryAddress.getOrDefault(clientId, ""));
                concentrationClient.setAssertClassifyResult(assertClassifyResult.getOrDefault(clientId, ""));
                concentrationClient.setDateTimePoint(fistDayOfMonth);
                concentrationClient.setRemainingPrincipal(clientIdToRemaining.getOrDefault(clientId, 0L));
                concentrationClient.setRemainingMargin(clientIdToMargin.getOrDefault(clientId, 0L));
                concentrationClient.setBadBalance(clientIdToOverdueAmount.getOrDefault(clientId, 0L));
                Long clientSponsorUserId = clientSponsorMap.get(clientId);
                concentrationClient.setClientSponsorId(Objects.isNull(clientSponsorUserId) ? CLIENT_SPONSOR_NULL_PLACEHOLDER : clientSponsorUserId);
                if (concentrationClient.getRemainingPrincipal() == 0L || factor == null || factor.getFactorValue() == null
                        || factor.getFactorValue() == 0L) {
                    concentrationClient.setConcentrationRatio(0L);
                } else {
                    concentrationClient.setConcentrationRatio(new BigDecimal(concentrationClient.getRemainingPrincipal())
                            .divide(new BigDecimal(factor.getFactorValue()), 4, HALF_UP)
                            .multiply(new BigDecimal(10000L)).longValue());
                }
                RiskControlStrategy strategy = null;
                if ("330000".equals(concentrationClient.getProvince())
                        || RiskControlIndustryClassify.INTRA_GROUP_COLLABORATION.name()
                        .equals(concentrationClient.getRiskControlIndustryClassify())) {
                    concentrationClient.setZhejiangInnerGroup(1);
                    strategy = concentrationStrategy.get(6L);
                } else {
                    concentrationClient.setZhejiangInnerGroup(0);
                    strategy = concentrationStrategy.get(8L);
                }
                if (ObjectUtil.isNotEmpty(strategy)) {
                    concentrationClient.setState(strategy.currentAlertState(concentrationClient.getConcentrationRatio(),
                            concentrationClient.getRemainingPrincipal()).name());
                }
                updateList.add(concentrationClient);
            }
            saveOrUpdateBatch(updateList);

            // 更新集团集中度
            Map<Long, List<RiskControlConcentrationClient>> collect = updateList.stream()
                    .filter(concentrationClient -> concentrationClient.getGroupId() != null && concentrationClient.getGroupId() > 0)
                    .collect(Collectors.groupingBy(RiskControlConcentrationClient::getGroupId));

            Map<Long, RiskControlConcentrationGroup> groupMap = concentrationGroupService.list(Wrappers.<RiskControlConcentrationGroup>lambdaQuery()
                            .eq(RiskControlConcentrationGroup::getDateTimePoint, fistDayOfMonth)).stream()
                    .collect(Collectors.toMap(RiskControlConcentrationGroup::getGroupId, item -> item, (k1, k2) -> k2));

            List<RiskControlConcentrationGroup> updateGroupList = new ArrayList<>();
            collect.forEach((groupId, concentrationClients) -> {
                // 集团删除后，某些关联子公司还存储了该集团的外键，产生脏数据
                if (clientsMap.get(groupId) == null) {
                    return;
                }
                RiskControlConcentrationGroup concentrationGroup = groupMap.getOrDefault(groupId, new RiskControlConcentrationGroup());
                Long remainingPrincipal = 0L, badBalance = 0L, remainingMargin = 0L;
                for (RiskControlConcentrationClient concentrationClient : concentrationClients) {
                    remainingPrincipal += concentrationClient.getRemainingPrincipal();
                    badBalance += concentrationClient.getBadBalance();
                    remainingMargin += concentrationClient.getRemainingMargin();
                }
                concentrationGroup.setGroupId(groupId);
                concentrationGroup.setGroupName(clientId2Name.get(groupId));
                concentrationGroup.setDateTimePoint(fistDayOfMonth);
                concentrationGroup.setRemainingPrincipal(remainingPrincipal);
                concentrationGroup.setBadBalance(badBalance);
                concentrationGroup.setRemainingMargin(remainingMargin);
                concentrationGroup.setProvince(registryAddress.getOrDefault(groupId, ""));
                if (clientsMap.get(groupId) != null) {
                    concentrationGroup.setRiskControlIndustryClassify(clientsMap.get(groupId).getRiskControlIndustryClassify());
                }
                RiskControlStrategy strategy;
                if ("330000".equals(concentrationGroup.getProvince())
                        || RiskControlIndustryClassify.INTRA_GROUP_COLLABORATION.name()
                        .equals(concentrationGroup.getRiskControlIndustryClassify())) {
                    concentrationGroup.setZhejiangInnerGroup(1);
                    strategy = concentrationStrategy.get(7L);
                } else {
                    concentrationGroup.setZhejiangInnerGroup(0);
                    strategy = concentrationStrategy.get(9L);
                }
                if (concentrationGroup.getRemainingPrincipal() == 0L || factor == null || factor.getFactorValue() == null
                        || factor.getFactorValue() == 0L) {
                    concentrationGroup.setConcentrationRatio(0L);
                } else {
                    concentrationGroup.setConcentrationRatio(new BigDecimal(concentrationGroup.getRemainingPrincipal())
                            .divide(new BigDecimal(factor.getFactorValue()), 4, HALF_UP)
                            .multiply(new BigDecimal(10000L)).longValue());
                }
                if (ObjectUtil.isNotEmpty(strategy)) {
                    concentrationGroup.setState(strategy.currentAlertState(concentrationGroup.getConcentrationRatio(),
                            concentrationGroup.getRemainingPrincipal()).name());
                }
                updateGroupList.add(concentrationGroup);
            });
            concentrationGroupService.saveOrUpdateBatch(updateGroupList);
        } catch (Exception e) {
            log.error("用户集中度更新失败", e);
        }
    }


    public PageR<RiskControlConcentrationClientListRSP> listClient(RiskControlConcentrationClientListREQ req) {
        QueryWrapper<RiskControlConcentrationClient> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("date_time_point", req.getDataTimePoint());
        queryWrapper.like(ObjectUtil.isNotEmpty(req.getClientName()), "client_name", req.getClientName());
        queryWrapper.like(ObjectUtil.isNotEmpty(req.getGroupName()), "group_name", req.getGroupName());
        queryWrapper.eq(ObjectUtil.isNotEmpty(req.getState()), "state", req.getState());
        queryWrapper.eq(ObjectUtil.isNotEmpty(req.getZhejiangInnerGroup()), "zhejiang_inner_group",
                req.getZhejiangInnerGroup());
        Set<Long> bizUserIds = userScopeService.bizUserIds();
        queryWrapper.in(isNotEmpty(bizUserIds), "client_sponsor_id", bizUserIds);
        queryWrapper.orderByDesc("remaining_principal");
        Page<RiskControlConcentrationClient> page = baseMapper.selectPage(
                new Page<>(req.getPage(), req.getPageSize()), queryWrapper);
        BigDecimal totalRemaining = getTotalRemaining();
        List<RiskControlConcentrationClientListRSP> rsps = new ArrayList<>();
        for (RiskControlConcentrationClient record : page.getRecords()) {
            RiskControlConcentrationClientListRSP rsp = baseConverter.entity2ListRsp(record);
            if (rsp.getBadBalance() != null && rsp.getBadBalance() != 0L) {
                rsp.setBadBalanceRatio(new BigDecimal(rsp.getBadBalance()).divide(totalRemaining, 4, HALF_UP)
                        .multiply(new BigDecimal(10000L)).longValue());
            }
            rsps.add(rsp);
        }
        return PageR.of(page, rsps);
    }

    public BigDecimal getTotalRemaining() {
        return baseMapper.selectList(Wrappers.<RiskControlConcentrationClient>lambdaQuery().gt(RiskControlConcentrationClient::getRemainingPrincipal, 0L)).stream()
                .map(RiskControlConcentrationClient::getRemainingPrincipal)
                .map(BigDecimal::new)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public PageR<RiskControlConcentrationClientListRSP> listRelate(RiskControlConcentrationRelateListREQ req) {
        QueryWrapper<RiskControlConcentrationClient> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("date_time_point", req.getDataTimePoint());
        queryWrapper.like(ObjectUtil.isNotEmpty(req.getClientName()), "client_name", req.getClientName());
        queryWrapper.eq(ObjectUtil.isNotEmpty(req.getState()), "state", req.getState());
        queryWrapper.eq("is_related", 1);
        queryWrapper.orderByDesc("remaining_principal");
        Page<RiskControlConcentrationClient> page = baseMapper.selectPage(
                new Page<>(req.getPage(), req.getPageSize()), queryWrapper);
        BigDecimal totalRemaining = getTotalRemaining();
        List<RiskControlConcentrationClientListRSP> rsps = new ArrayList<>();
        for (RiskControlConcentrationClient record : page.getRecords()) {
            RiskControlConcentrationClientListRSP rsp = baseConverter.entity2ListRsp(record);
            if (rsp.getBadBalance() != null && rsp.getBadBalance() != 0L) {
                rsp.setBadBalanceRatio(new BigDecimal(rsp.getBadBalance()).divide(totalRemaining, 4, HALF_UP)
                        .multiply(new BigDecimal(10000L)).longValue());
            }
            rsps.add(rsp);
        }
        return PageR.of(page, rsps);
    }

    public RiskControlConcentrationAllRelateRSP listAllRelate(RiskControlConcentrationRelateListREQ req) {
        List<RiskControlConcentrationClient> list = baseMapper.selectList(
                        Wrappers.<RiskControlConcentrationClient>lambdaQuery()
                                .eq(RiskControlConcentrationClient::getDateTimePoint,
                                        req.getDataTimePoint())
                                .eq(RiskControlConcentrationClient::getIsRelated, 1)).stream()
                .filter(client -> client.getRemainingPrincipal() != null && client.getRemainingPrincipal() > 0).collect(Collectors.toList());
        Long remainingPrincipal = 0L, badBalance = 0L;

        for (RiskControlConcentrationClient client : list) {
            remainingPrincipal += client.getRemainingPrincipal();
            badBalance += client.getBadBalance();
        }
        // 查询风控因子
        RiskMetricFactorValue factor = factorService.newestFactor(FACTOR_NAME, FACTOR_TABLE, null);

        RiskControlConcentrationAllRelateRSP rsp = new RiskControlConcentrationAllRelateRSP();
        rsp.setRemainingPrincipal(remainingPrincipal);
        rsp.setBadBalance(badBalance);
        if (badBalance == 0L || remainingPrincipal == 0L) {
            rsp.setBadBalanceRatio(0L);
        } else {
            BigDecimal totalRemaining = getTotalRemaining();
            rsp.setBadBalanceRatio(new BigDecimal(badBalance)
                    .divide(totalRemaining, 4, HALF_UP)
                    .multiply(new BigDecimal(10000L)).longValue());
        }
        if (remainingPrincipal == 0L || factor == null || factor.getFactorValue() == null
                || factor.getFactorValue() == 0L) {
            rsp.setConcentrationRatio(0L);
        } else {
            rsp.setConcentrationRatio(new BigDecimal(remainingPrincipal)
                    .divide(new BigDecimal(factor.getFactorValue()), 4, HALF_UP)
                    .multiply(new BigDecimal(10000L)).longValue());
        }
        return rsp;
    }

    public FRIRsp fri() {
        RiskMetricFactorValue factor = factorService.newestFactor(FACTOR_NAME, FACTOR_TABLE, null);
        FRIRsp friRsp = new FRIRsp();
        if (factor != null) {
            friRsp.setFriDate(factor.getFactorDate());
        }
        return friRsp;
    }

}
