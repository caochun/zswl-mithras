package cn.zswltech.mithras.application.orchestration.kpi;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.fuxi.common.dto.RatingManagementRSP;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.kpi.KpiExpectedLossDecisionQuery;
import cn.zswltech.mithras.dto.kpi.KpiProvisionBaseInfoDetailREQ;
import cn.zswltech.mithras.dto.kpi.KpiProvisionBaseInfoDetailRSP;
import cn.zswltech.mithras.dto.kpi.KpiProvisionBaseInfoModifyREQ;
import cn.zswltech.mithras.dto.rating.decision.DecisionExecuteEclResult;
import cn.zswltech.mithras.rating.feign.RatingManagementClient;
import cn.zswltech.mithras.rating.versioning.ratingclient.RatingClientLibService;
import cn.zswltech.mithras.rating.model.RatingClientLib;
import cn.zswltech.mithras.rating.service.DecisionService;
import cn.zswltech.mithras.kpi.mapper.model.KpiParameterConfig;
import cn.zswltech.mithras.kpi.application.config.KpiParameterConfigService;
import cn.zswltech.mithras.foundation.constant.ResultMsg;
import cn.zswltech.mithras.foundation.enums.CashFlowItemEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.assetclassify.enums.AssetClassifyResultEnum;
import cn.zswltech.mithras.foundation.enums.common.ProcessStatus;
import cn.zswltech.mithras.foundation.enums.common.ProjectBizType;
import cn.zswltech.mithras.kpi.enums.*;
import cn.zswltech.mithras.kpi.enums.config.EclConfigEnum;
import cn.zswltech.mithras.kpi.bo.*;
import cn.zswltech.mithras.leaseholdproperty.enums.LeaseItemManagerLeaseItemType;
import cn.zswltech.mithras.payment.enums.PaymentStatusEnum;
import cn.zswltech.mithras.payment.enums.PaymentWriteOffStatus;
import cn.zswltech.mithras.foundation.enums.LeaseType;
import cn.zswltech.mithras.collection.mapper.CollectionBaseInfoMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.customer.dto.ClientMaxLeaseMonthDTO;
import cn.zswltech.mithras.kpi.mapper.KpiProvisionBaseInfoMapper;
import cn.zswltech.mithras.kpi.mapper.KpiProvisionDetailMapper;
import cn.zswltech.mithras.assetclassify.mapper.lib.AssetClassifyClientAuxiliaryLibMapper;
import cn.zswltech.mithras.assetclassify.mapper.model.AssetClassify;
import cn.zswltech.mithras.assetclassify.mapper.model.AssetClassifyClientAuxiliaryLib;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.customer.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.customer.model.client.CorpCommerceInfoLib;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.model.contract.ContractReceipt;
import cn.zswltech.mithras.kpi.mapper.model.EclExecuteRecord;
import cn.zswltech.mithras.kpi.mapper.model.KpiProvisionBaseInfo;
import cn.zswltech.mithras.kpi.mapper.model.KpiProvisionDetail;
import cn.zswltech.mithras.kpi.application.ecl.EclBusinessConfigService;
import cn.zswltech.mithras.leaseholdproperty.mapper.model.LeaseItemInfo;
import cn.zswltech.mithras.payment.mapper.model.PaymentActualDetail;
import cn.zswltech.mithras.payment.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewBaseInfoLib;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.context.SpringContextHolder;
import cn.zswltech.mithras.foundation.util.Util;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.afterlease.application.RentCollectionDetailService;
import cn.zswltech.mithras.application.orchestration.assetclassify.AssetClassifyClientService;
import cn.zswltech.mithras.application.orchestration.assetclassify.AssetClassifyService;
import cn.zswltech.mithras.foundation.bo.*;
import cn.zswltech.mithras.budget.application.EclExecuteClientPromotionResultService;
import cn.zswltech.mithras.application.orchestration.client.ClientService;
import cn.zswltech.mithras.customer.application.client.CorpCommerceInfoService;
import cn.zswltech.mithras.collection.application.CollectionBaseInfoService;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.application.orchestration.contract.ContractIncomeSharingService;
import cn.zswltech.mithras.contract.core.ContractReceiptService;
import cn.zswltech.mithras.leaseholdproperty.application.LeaseItemInfoService;
import cn.zswltech.mithras.customer.application.lib.client.CorpCommerceInfoLibService;
import cn.zswltech.mithras.projectprocess.application.lib.projreview.handler.impl.ProjReviewBaseInfoLibHandler;
import cn.zswltech.mithras.margin.service.MarginBaseInfoService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentActualDetailService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.foundation.util.LongUtil;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author vico
 * @description 绩效-拨备表
 * @date 2023-06-19
 */
@Service
@Slf4j
public class KpiProvisionDetailService extends ServiceImpl<KpiProvisionDetailMapper, KpiProvisionDetail> {

    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private PaymentActualDetailService paymentActualDetailService;
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private ContractReceiptService contractReceiptService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ProjReviewBaseInfoService projReviewBaseInfoService;
    @Resource
    private ProjReviewBaseInfoLibHandler projReviewBaseInfoLibHandler;
    @Resource
    private MarginBaseInfoService marginBaseInfoService;
    @Resource
    private AssetClassifyClientAuxiliaryLibMapper assetClassifyClientAuxiliaryLibMapper;
    @Resource
    private AssetClassifyService assetClassifyService;
    @Resource
    private KpiProvisionBaseInfoMapper kpiProvisionBaseInfoMapper;
    @Resource
    private KpiParameterConfigService kpiParameterConfigService;
    @Resource
    private ClientService clientService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private CorpCommerceInfoLibService corpCommerceInfoLibService;
    @Resource
    private KpiProjectDistributionBaseInfoService kpiProjectDistributionBaseInfoService;
    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;
    @Resource
    private RentCollectionDetailService rentCollectionDetailService;
    @Resource
    private AssetClassifyClientService assetClassifyClientService;
    @Resource
    private CorpCommerceInfoService corpCommerceInfoService;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private KpiProvisionDetailService kpiProvisionDetailService;
    @Resource
    private ContractIncomeSharingService contractIncomeSharingService;
    @Resource
    private DecisionService decisionService;
    @Resource
    private LeaseItemInfoService leaseItemInfoService;
    @Resource
    private EclBusinessConfigService eclBusinessConfigService;
    @Resource
    private EclExecuteRecordService eclExecuteRecordService;
    @Resource
    private RatingManagementClient ratingManagementClient;

    //private final static List<String> DOWN_LEAVE = ListUtil.toList("B1", "B2", "B3", "Caa1", "Caa2", "Caa3");
    private final static String BASE_SCENE = "基准情景";
    private final static String OPT_SCENE = "乐观情景";
    private final static String GLO_SCENE = "悲观情景";

    public Integer getTargetDateByReceiptId(Long receiptId, LocalDate targetDate, boolean specific) {
        LocalDate endOfMonthDate = LocalDate.of(targetDate.getYear(), targetDate.getMonthValue(), targetDate.lengthOfMonth());
        // 找到最新生效的拨备计提数据
        LambdaQueryWrapper<KpiProvisionBaseInfo> query = Wrappers.lambdaQuery();
        query.eq(KpiProvisionBaseInfo::getProvisionStatus, KpiProvisionStatusEnum.EFFECT.name());
        if (specific) {
            // 指定日期查询需要精确匹配
            query.eq(KpiProvisionBaseInfo::getProvisionDate, endOfMonthDate);
        } else {
            // 非指定找最近的
            query.le(KpiProvisionBaseInfo::getProvisionDate, endOfMonthDate);
        }
        query.orderByDesc(KpiProvisionBaseInfo::getProvisionDate);
        query.last(StringUtil.mysqlLimitOne());
        KpiProvisionBaseInfo baseInfo = kpiProvisionBaseInfoMapper.selectOne(query);
        if (Objects.isNull(baseInfo)) {
            return null;
        }
        // 找到对应借据的数据
        LambdaQueryWrapper<KpiProvisionDetail> detailQuery = Wrappers.lambdaQuery();
        detailQuery.eq(KpiProvisionDetail::getProvisionId, baseInfo.getId());
        detailQuery.eq(KpiProvisionDetail::getReceiptId, receiptId);
        detailQuery.last(StringUtil.mysqlLimitOne());
        KpiProvisionDetail detail = this.getOne(detailQuery);
        if (Objects.isNull(detail)) {
            return null;
        }
        if (Objects.nonNull(detail.getWithdrawalRatioHand())) {
            return detail.getWithdrawalRatioHand().intValue();
        }
        if (Objects.nonNull(detail.getWithdrawalRatio())) {
            return detail.getWithdrawalRatio().intValue();
        }
        return null;
    }

    public List<KpiProvisionDetail> listByProvisionId(Long provisionId) {
        LambdaQueryWrapper<KpiProvisionDetail> query = Wrappers.lambdaQuery();
        query.eq(KpiProvisionDetail::getProvisionId, provisionId);
        return this.list(query);
    }

    //借据维度，一个借据一条数据。查询合同下借据信息
    @Transactional(rollbackFor = Throwable.class)
    public void add(LocalDate provisionDate, Long provisionId) {
        //查询已有数据
        List<KpiProvisionDetail> kpiProvisionDetails = baseMapper.selectList(Wrappers.<KpiProvisionDetail>lambdaQuery()
                .eq(KpiProvisionDetail::getProvisionId, provisionId));
        Map<Long, KpiProvisionDetail> updateDetailMap = new HashMap<>();
        if (ObjectUtil.isNotEmpty(kpiProvisionDetails)) {
            updateDetailMap = kpiProvisionDetails.stream().collect(Collectors.toMap(KpiProvisionDetail::getReceiptId, e -> e, (a,
                                                                                                                               b) -> a));
        }
        //查询已经付款合同ID
        List<PaymentBaseInfo> paymentBaseInfos = paymentBaseInfoService.list(Wrappers.<PaymentBaseInfo>lambdaQuery()
                .in(PaymentBaseInfo::getPaymentStatus, Arrays.asList(PaymentStatusEnum.TAKE_EFFECT.name(), PaymentStatusEnum.FINISHED.name()))
                .le(PaymentBaseInfo::getApplyPaymentDate, provisionDate));
        if (ObjectUtil.isEmpty(paymentBaseInfos)) {
            return;
        }
        //借据，付款信息
        Map<Long, List<PaymentBaseInfo>> receiptPaymentMap = paymentBaseInfos.stream().filter(base -> ObjectUtil.isNotEmpty(base.getReceiptIdFinal())).collect(Collectors.groupingBy(PaymentBaseInfo::getReceiptIdFinal));
        List<Long> paymentIds = paymentBaseInfos.stream().filter(base -> ObjectUtil.isNotEmpty(base.getReceiptIdFinal())).map(PaymentBaseInfo::getId).collect(Collectors.toList());
        //每个付款对应实际付款
        Map<Long, Long> paymentIdAmountMap = paymentActualDetailService.list(Wrappers.<PaymentActualDetail>lambdaQuery()
                .le(PaymentActualDetail::getPaidInDate, provisionDate)
                .in(ObjectUtil.isNotEmpty(paymentIds), PaymentActualDetail::getPaymentId, paymentIds)
                .le(PaymentActualDetail::getPaidInDate, provisionDate)
                .in(PaymentActualDetail::getWriteOffStatus, ListUtil.toList(PaymentWriteOffStatus.WRITTEN_OFF.name(),
                        PaymentWriteOffStatus.PART_WRITTEN_OFF.name()))).stream().filter(base -> LongUtil.null2zero(base.getPaidInAmount()) != 0).collect(Collectors.toMap(PaymentActualDetail::getPaymentId,
                PaymentActualDetail::getPaidInAmount, (a, b) -> LongUtil.null2zero(a) + LongUtil.null2zero(b)));
        //修改查询借据对应最近一期租金表剩余本金
        List<CollectionBaseInfo> lastRentByContractIdsAndTime = collectionBaseInfoMapper.getLastRentByContractIdsAndTime(receiptPaymentMap.keySet(),
                provisionDate);
        Map<Long, Long> lastRentByContractIdsAndTimeMap = new HashMap<>();
        if (ObjectUtil.isNotEmpty(lastRentByContractIdsAndTime)) {
            lastRentByContractIdsAndTimeMap = lastRentByContractIdsAndTime.stream().collect(Collectors.toMap(CollectionBaseInfo::getReceiptId,
                    base -> LongUtil.null2zero(base.getReceiptRemainingPrincipal()), (a, b) -> a));
        }
        //查询借据收款信息<借据id, 已收本金> 收款时间在provisionDate之前
        Map<Long, Long> receiptIdCollectionPrincipalMap = collectionBaseInfoService.sumRemainingPrincipalByReceipt(receiptPaymentMap.keySet(),
                provisionDate, CashFlowItemEnum.RENT.name());
        //查询已核销首期租金
        Map<Long, Long> paymentIdFistRentMap = collectionBaseInfoService.sumRemainingPrincipalByReceipt(receiptPaymentMap.keySet(),
                provisionDate, CashFlowItemEnum.FIRST_RENT.name());
        //借据对应剩余本金大于0
        Map<Long, Long> receiptPrincipalMap = new HashMap<>();
        //借据对应付款-首期租金
        Map<Long, Long> receiptFistRentMap = new HashMap<>();
        long sumAmount = 0;
        List<PaymentBaseInfo> paymentList;
        for (Long receiptId : receiptPaymentMap.keySet()) {
            sumAmount = 0;
            paymentList = receiptPaymentMap.get(receiptId);
            if (ObjectUtil.isNotEmpty(paymentList)) {
                sumAmount += paymentList.stream().map(PaymentBaseInfo::getId).map(paymentIdAmountMap::get).mapToLong(LongUtil::null2zero).sum();
                sumAmount += paymentList.stream().map(PaymentBaseInfo::getRetentionMoney).mapToLong(LongUtil::null2zero).sum();
            }
            //减去首期租金
            sumAmount -= paymentIdFistRentMap.getOrDefault(receiptId, 0L);
            receiptFistRentMap.put(receiptId, sumAmount);
            sumAmount -= LongUtil.null2zero(receiptIdCollectionPrincipalMap.get(receiptId));
            //有付款都计算
            receiptPrincipalMap.put(receiptId, sumAmount);
        }
        if (receiptPrincipalMap.size() <= 0) {
            return;
        }
        //剩余本金大于0 合同id
        List<ContractReceipt> contractReceipts = contractReceiptService.listByIds(receiptPrincipalMap.keySet());
        Map<Long, Long> receiptId2ContractIdMap = contractReceipts.stream().collect(Collectors.toMap(ContractReceipt::getId,
                ContractReceipt::getContractId, (a, b) -> a));
        List<Long> contractIds = contractReceipts.stream().map(ContractReceipt::getContractId).collect(Collectors.toList());
        //查询合同基本信息
        List<ContractBaseInfo> contractBaseInfos = contractBaseInfoService.listByIds(contractIds);
        Map<Long, List<ContractBaseInfo>> clientIdContractMap = contractBaseInfos.stream().collect(Collectors.groupingBy(ContractBaseInfo::getClientId));
        Map<Long, ContractBaseInfo> contractId2beanMap = contractBaseInfos.stream().collect(Collectors.toMap(ContractBaseInfo::getId, e -> e, (a, b) -> a));
        Set<Long> clientSet = contractBaseInfos.stream().map(ContractBaseInfo::getClientId).collect(Collectors.toSet());


        //查询借据到期日
        Map<Long, LocalDate> endTimeByReceiptMap = collectionBaseInfoService.getEndTimeByReceipt(receiptPrincipalMap.keySet());
       /* List<CollectionBaseInfo> earnestMoneyList = collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.EARNEST_MONEY.name())
                .in(CollectionBaseInfo::getContractId, contractIds));
        if (ObjectUtil.isEmpty(earnestMoneyList)) {
            earnestMoneyList = new ArrayList<>();
        }*/
        /*//付款下总保证金 <PaymentId, EarnestMoney>
        Map<Long, Long> paymentIdEarnestMoneyMap = earnestMoneyList.stream().collect(Collectors.toMap(CollectionBaseInfo::getPaymentId,
                e -> LongUtil.null2zero(e.getCollectionAmount()), (a, b) -> LongUtil.null2zero(a) + LongUtil.null2zero(b)));
        //合同下总收保证金 <PaymentId, EarnestMoney>
        Map<Long, Long> contractIdEarnestMoneyAllMap = earnestMoneyList.stream().collect(Collectors.toMap(CollectionBaseInfo::getContractId,
                e -> LongUtil.null2zero(e.getCollectionAmount()), (a, b) -> LongUtil.null2zero(a) + LongUtil.null2zero(b)));
        //查询<合同id, 保证金余额>
        Map<Long, Long> contractIdMarginMap = marginBaseInfoService.list(Wrappers.<MarginBaseInfo>lambdaQuery()
                .in(MarginBaseInfo::getContractId, contractIds)).stream().collect(Collectors.toMap(MarginBaseInfo::getContractId,
                e -> LongUtil.null2zero(e.getCollectionAmount()), (a, b) -> a));*/
        //查询<合同id, 保证金余额>
        Map<Long, Long> receiptIdMarginMap = marginBaseInfoService.getAmountByReceiptIds(receiptPrincipalMap.keySet(), provisionDate);
        //获取客户五级分类 3月，6月，9月，12月：关联租后的五级分类，获取对应客户的五级分类；获取不到，则报错提示：未能从风控模块获取到本季度的五级分类，是否取上月份拨备计提表中获取？  是  否
        //2）其他月份：关联上一月份的拨备计提表"
        Map<Long, AssetClassifyClientAuxiliaryLib> clientIdAssetMap = new HashMap<>();
        //上月分类结果
        Map<Long, KpiProvisionDetail> clientIdAssetLastMonthMap = new HashMap<>();
        Map<Long, KpiProvisionDetail> receiptIdAssetLastMonthMap = new HashMap<>();
        //每月的“五级分类”“计提比例”，都从【资产五级分类】模块获取上季度的生效数据，取不到再从【拨备计提】获取最近1个月份的生效数据
        int year;
        int month;
        year = provisionDate.getYear();
        month = provisionDate.getMonthValue();
        if (provisionDate.getMonthValue() <= 3) {
            --year;
            month = 4;
        } else {
            month = month % 3 == 0 ? month / 3 - 1 : (month - month % 3) / 3;
        }
        AssetClassify assetClassify = assetClassifyService.getOne(Wrappers.<AssetClassify>lambdaQuery()
                .eq(AssetClassify::getYear, year)
                .eq(AssetClassify::getFinish, YesOrNoNumberEnum.YES.getCode())
                .eq(AssetClassify::getQuarter, month)
                .last(StringUtil.mysqlLimitOne()));
        if (ObjectUtil.isNotEmpty(assetClassify)) {
            clientIdAssetMap = assetClassifyClientAuxiliaryLibMapper.listNewestClassifyLibByClientIdAndAssetClassifyId(clientSet, assetClassify.getId()).stream().collect(Collectors.toMap(AssetClassifyClientAuxiliaryLib::getClientId, e -> e, (a, b) -> a));
        }
        //查询上月客户分类结果
        KpiProvisionBaseInfo kpiProvisionBaseInfo = kpiProvisionBaseInfoMapper.selectOne(Wrappers.<KpiProvisionBaseInfo>lambdaQuery()
                .eq(KpiProvisionBaseInfo::getProvisionDate, provisionDate.minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()))
                .eq(KpiProvisionBaseInfo::getProvisionStatus, KpiProvisionStatusEnum.EFFECT.name())
                .orderByDesc(KpiProvisionBaseInfo::getProvisionDate)
                .last(StringUtil.mysqlLimitOne()));
        List<KpiProvisionDetail> lastKpiProvisionDetails = null;
        if (ObjectUtil.isNotEmpty(kpiProvisionBaseInfo)) {
            lastKpiProvisionDetails = baseMapper.selectList(Wrappers.<KpiProvisionDetail>lambdaQuery()
                    .eq(KpiProvisionDetail::getProvisionId, kpiProvisionBaseInfo.getId()));
        }
        if (lastKpiProvisionDetails != null) {
            clientIdAssetLastMonthMap = lastKpiProvisionDetails.stream().collect(Collectors.toMap(KpiProvisionDetail::getClientId, e -> e, (a, b) -> a));
            receiptIdAssetLastMonthMap = lastKpiProvisionDetails.stream().collect(Collectors.toMap(KpiProvisionDetail::getReceiptId, e -> e, (a, b) -> a));
        }
        //计提比例参数
        KpiParameterConfig oneByConfigCode = kpiParameterConfigService.getOneByConfigCode(KpiParameterConfigCodeEnum.PROVISION_RADIO);
        // List<ProvisionRadioConfig.Data> radioList = JSONUtil.toList(oneByConfigCode.getConfigValue(), ProvisionRadioConfig.Data.class);
        //计算项目类型
        List<Client> clientList = clientService.listByIds(clientSet);
        //查询客户信息
        Map<Long, CorpCommerceInfo> clientId2CorpCommerceInfoMap = corpCommerceInfoService.list(clientSet).stream().collect(Collectors.toMap(CorpCommerceInfo::getClientId, e -> e, (a, b) -> a));
        //查询客户下最长租期
        Map<Long, Long> clientId2MaxMonthMap = contractBaseInfoMapper.clientMaxLeaseMonth(clientSet).stream().collect(Collectors.toMap(ClientMaxLeaseMonthDTO::getClientId,
                ClientMaxLeaseMonthDTO::getLeaseMonthCount, (a, b) -> a));
        Map<Long, String> clientVersionMap = clientList.stream().collect(Collectors.toMap(Client::getId, Client::getNewestVersion));
        Map<Long, CorpCommerceInfoLib> corpCommerceInfoLibMap = corpCommerceInfoLibService.getSpecificVersionMap(clientVersionMap);
        // 应付利息
        Map<Long, Long> accruedInterestMap = contractIncomeSharingService.getAccruedInterest(receiptPaymentMap.keySet(), provisionDate);
         //下期租金
        Map<Long, Long> receiptNextRentMap = SpringContextHolder.getBean(CollectionBaseInfoService.class).getReceiptNextRent(receiptPaymentMap.keySet(), provisionDate);
        //获取合同类型
        Map<Long, String> contractId2BeanMap = contractBaseInfos.stream().filter(e -> ObjectUtil.isNotEmpty(e.getLeaseType())).collect(Collectors.toMap(ContractBaseInfo::getId, ContractBaseInfo::getLeaseType, (a, b) -> b));

        //构建数据
        List<KpiProvisionDetail> addList = new ArrayList<>();
        List<Long> deleteList = new LinkedList<>();
        KpiProvisionDetail kpiProvisionDetail;
        ContractBaseInfo contractBaseInfo;
        String riskLevel = null;
        Long awardRatio = null;
        KpiProvisionDetail kpiProvisionDetail1;
        for (Long receiptId : receiptPrincipalMap.keySet()) {
            contractBaseInfo = contractId2beanMap.get(receiptId2ContractIdMap.get(receiptId));
            if (ObjectUtil.isEmpty(contractBaseInfo)) {
                continue;
            }
            kpiProvisionDetail = new KpiProvisionDetail();
            kpiProvisionDetail.setProvisionId(provisionId);
            kpiProvisionDetail.setReceiptId(receiptId);
            kpiProvisionDetail.setBizType(contractBaseInfo.getBizType());
            kpiProvisionDetail.setLeaseType(contractBaseInfo.getLeaseType());
            kpiProvisionDetail.setProjClassify(kpiProjectDistributionBaseInfoService.ensureProjClassify(corpCommerceInfoLibMap.getOrDefault(contractBaseInfo.getClientId(), new CorpCommerceInfoLib()).getRiskControlIndustryClassify()).name());
            kpiProvisionDetail.setProfitBelongDeptId(contractBaseInfo.getBizDeptId());
            kpiProvisionDetail.setSponsorUserId(contractBaseInfo.getProjSponsorUserId());
            kpiProvisionDetail.setClientId(contractBaseInfo.getClientId());
            kpiProvisionDetail.setContractId(contractBaseInfo.getId());
            kpiProvisionDetail.setContractCode(contractBaseInfo.getContractCode());
            kpiProvisionDetail.setEndDate(endTimeByReceiptMap.get(receiptId));
            //月末日往前追溯的第一个期项所对应的“剩余本金”，没有则取核销的付款金额-核销的首期租金金额
            //kpiProvisionDetail.setRemainingPrincipal(lastRentByContractIdsAndTimeMap.get(receiptId) == null ? receiptFistRentMap.getOrDefault(receiptId, 0L) : lastRentByContractIdsAndTimeMap.get(receiptId));
            //修改为实际的
            kpiProvisionDetail.setRemainingPrincipal(receiptPrincipalMap.get(receiptId));
            //填充借据保证金
            kpiProvisionDetail.setEarnestBalance(receiptIdMarginMap.getOrDefault(receiptId, 0L));
            //敞口
            kpiProvisionDetail.setExposure(LongUtil.null2zero(kpiProvisionDetail.getRemainingPrincipal()) - LongUtil.null2zero(kpiProvisionDetail.getEarnestBalance()));
            //如果敞口小于0，则设置为0
            if (kpiProvisionDetail.getExposure() < 0) {
                kpiProvisionDetail.setExposure(0L);
            }
            AssetClassifyClientAuxiliaryLib riskLevel1 = clientIdAssetMap.get(contractBaseInfo.getClientId());
            if (ObjectUtil.isNotEmpty(riskLevel1)) {
                riskLevel = riskLevel1.getClassifyResult();
                awardRatio = riskLevel1.getAwardRatio();
            }
            if (ObjectUtil.isNull(riskLevel)) {
                riskLevel = ObjectUtil.isNull(clientIdAssetLastMonthMap.get(contractBaseInfo.getClientId())) ?
                        AssetClassifyResultEnum.NORMAL.name() : clientIdAssetLastMonthMap.get(contractBaseInfo.getClientId()).getRiskLevel();
            }
            if (ObjectUtil.isEmpty(awardRatio)) {
                awardRatio = ObjectUtil.isNull(clientIdAssetLastMonthMap.get(contractBaseInfo.getClientId())) ?
                        null : clientIdAssetLastMonthMap.get(contractBaseInfo.getClientId()).getWithdrawalRatio();
            }
            //最终默认值
            if (ObjectUtil.isEmpty(awardRatio)) {
                int days = 0;
                List<ContractBaseInfo> contractBaseInfoList = clientIdContractMap.get(contractBaseInfo.getClientId());
                if (ObjectUtil.isNotEmpty(contractBaseInfoList)) {
                    days = rentCollectionDetailService.findLongestDayOverdueRent(contractBaseInfoList.stream().map(ContractBaseInfo::getId).collect(Collectors.toList()));
                }
                CorpCommerceInfo corpCommerceInfo = clientId2CorpCommerceInfoMap.getOrDefault(contractBaseInfo.getClientId(), new CorpCommerceInfo());
                riskLevel = assetClassifyClientService.ensureResult(days, corpCommerceInfo);
                awardRatio = assetClassifyClientService.getInitAwardRatio(corpCommerceInfo.getRiskControlIndustryClassify(),
                        riskLevel,
                        clientId2MaxMonthMap.getOrDefault(contractBaseInfo.getClientId(), 0L));
            }
            kpiProvisionDetail1 = updateDetailMap.get(receiptId);
            if (ObjectUtil.isNotEmpty(kpiProvisionDetail1)) {
                kpiProvisionDetail.setId(kpiProvisionDetail1.getId());
                if (ObjectUtil.isNotEmpty(kpiProvisionDetail1.getWithdrawalRatioHand())) {
                    kpiProvisionDetail.setWithdrawalRatio(kpiProvisionDetail1.getWithdrawalRatioHand());
                    kpiProvisionDetail.setWithdrawalRatioHand(kpiProvisionDetail1.getWithdrawalRatioHand());
                } else {
                    kpiProvisionDetail.setWithdrawalRatio(awardRatio);
                }
            } else {
                kpiProvisionDetail.setWithdrawalRatio(awardRatio);
            }
            kpiProvisionDetail.setRiskLevel(riskLevel);
            kpiProvisionDetail.setWithdrawalRatioConfig(oneByConfigCode.getConfigValue());
//            kpiProvisionDetail.setProfitCurrent(LongUtil.other2Long(LongUtil.tenThousand2Dollar(LongUtil.null2zero(kpiProvisionDetail.getExposure()).toString()).multiply(LongUtil.tenThousand2Dollar(LongUtil.null2zero(kpiProvisionDetail.getWithdrawalRatio()).toString()).divide(new BigDecimal(100), 10, RoundingMode.HALF_UP)).toString()));
            kpiProvisionDetail.setProfitTotal(ObjectUtil.isEmpty(receiptIdAssetLastMonthMap.get(receiptId)) ? null :
                    receiptIdAssetLastMonthMap.get(receiptId).getProfitCurrent());
            kpiProvisionDetail.setProvisionDate(provisionDate);
            if(ObjectUtil.isEmpty(kpiProvisionDetail.getRemainingPrincipal()) || kpiProvisionDetail.getRemainingPrincipal() <= 0) {
                kpiProvisionDetail.setAccruedInterest(0L);
            } else {
                kpiProvisionDetail.setAccruedInterest(accruedInterestMap.get(kpiProvisionDetail.getReceiptId()));
            }
            //应计利息（只有业务类型不等于经营性租赁才有值）、下期租金（只有业务类型等于经营性租赁才有值）
            if (LeaseType.jyx_zu.name().equals(contractId2BeanMap.get(kpiProvisionDetail.getContractId()))) {
                kpiProvisionDetail.setNextRent(receiptNextRentMap.get(kpiProvisionDetail.getReceiptId()));
            }
            if(LongUtil.null2zero(kpiProvisionDetail.getRemainingPrincipal()) > 0 || LongUtil.null2zero(kpiProvisionDetail.getBonusCurrent()) != 0) {
                addList.add(kpiProvisionDetail);
            }
            // 如果剩余本金小于等于0则需要删除
            long principalBalance = Optional.ofNullable(receiptPrincipalMap.get(receiptId)).orElse(0L);
            if (principalBalance <= 0 && Objects.nonNull(kpiProvisionDetail.getId())) {
                log.info("拨备计提-ID为{}的借据剩余本金小于等于0，从本次拨备计提数据中剔除", receiptId);
                deleteList.add(kpiProvisionDetail.getId());
            }
            awardRatio = null;
            riskLevel = null;
        }
        Map<Long, KpiProvisionDetail> finalUpdateDetailMap = updateDetailMap;
        Set<Long> keys = finalUpdateDetailMap.keySet();
        keys.removeAll(receiptPrincipalMap.keySet());
        if( ObjectUtil.isNotEmpty(keys)){
            Set<Long> collect = keys.stream().map(finalUpdateDetailMap::get).map(KpiProvisionDetail::getId).collect(Collectors.toSet());
            if(CollectionUtil.isNotEmpty(collect)){
                kpiProvisionDetailService.removeByIds(collect);
            }
        }
        kpiProvisionDetailService.saveOrUpdateBatch(addList);
        if (CollectionUtil.isNotEmpty(deleteList)) {
            kpiProvisionDetailService.removeByIds(deleteList);
        }
        //更新计算本月风险金
        List<Long> provisionDetailIds = addList.stream().map(KpiProvisionDetail::getId).collect(Collectors.toList());
        SpringContextHolder.getBean(KpiProvisionDetailService.class).buildProfitCurrent2(provisionDetailIds.subList(0, Math.min(provisionDetailIds.size(), 20)), provisionDate);
        if (provisionDetailIds.size() > 20) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    //异步执行
                    CompletableFuture.supplyAsync(() -> {
                        try {
                            SpringContextHolder.getBean(KpiProvisionDetailService.class).buildProfitCurrent2(provisionDetailIds.subList(20, provisionDetailIds.size()), provisionDate);
                        } catch (Exception e) {
                            log.error("计算本月风险金异常 {}", provisionDetailIds, e);
                        }
                        return null;
                    });
                }
            });
        }
    }

    public KpiProvisionBaseInfoDetailRSP detail(KpiProvisionBaseInfoDetailREQ req) {
        KpiProvisionBaseInfo kpiProvisionBaseInfo = kpiProvisionBaseInfoMapper.selectById(req.getId());
        if (ObjectUtil.isEmpty(kpiProvisionBaseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        Page<KpiProvisionDetail> kpiProvisionDetailPage = baseMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()), Wrappers.<KpiProvisionDetail>lambdaQuery()
                .eq(KpiProvisionDetail::getProvisionId, kpiProvisionBaseInfo.getId())
                .like(ObjectUtil.isNotEmpty(req.getContractCode()), KpiProvisionDetail::getContractCode, req.getContractCode())
                .eq(ObjectUtil.isNotEmpty(req.getProfitBelongDeptId()), KpiProvisionDetail::getProfitBelongDeptId, req.getProfitBelongDeptId())
                .eq(ObjectUtil.isNotEmpty(req.getClientId()), KpiProvisionDetail::getClientId, req.getClientId()));
        KpiProvisionBaseInfoDetailRSP kpiProvisionBaseInfoDetailRSP = new KpiProvisionBaseInfoDetailRSP();
        kpiProvisionBaseInfoDetailRSP.setProvisionDate(kpiProvisionBaseInfo.getProvisionDate());
        List<KpiProvisionBaseInfoDetailRSP.KpiProvisionBaseInfoBody> kpiProvisionBaseInfoBody = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(kpiProvisionDetailPage.getRecords())) {
            kpiProvisionBaseInfoBody = BeanUtil.copyToList(kpiProvisionDetailPage.getRecords(), KpiProvisionBaseInfoDetailRSP.KpiProvisionBaseInfoBody.class);
        }
        List<Long> deptIds = new ArrayList<>();
        List<Long> clientIds = new ArrayList<>();
        kpiProvisionBaseInfoBody.forEach(base -> {
            deptIds.add(base.getProfitBelongDeptId());
            clientIds.add(base.getClientId());
        });
        Map<Long, String> receiptId2Name = id2NameService.receiptId2Name(kpiProvisionBaseInfoBody.stream().map(KpiProvisionBaseInfoDetailRSP.KpiProvisionBaseInfoBody::getReceiptId).collect(Collectors.toList()));
        Map<Long, String> deptId2Name = id2NameService.deptId2Name(deptIds);
        Map<Long, String> clientId2Name = id2NameService.clientId2Name(clientIds);
        kpiProvisionBaseInfoBody.forEach(base -> {
            base.setProfitBelongDeptName(deptId2Name.get(base.getProfitBelongDeptId()));
            base.setClientName(clientId2Name.get(base.getClientId()));
            base.setReceiptCode(receiptId2Name.get(base.getReceiptId()));
            ProjectBizType of = ProjectBizType.of(base.getBizType());
            if (of != null) {
                base.setBizTypeName(of.display);
                if (ProjectBizType.ZL.name().equals(base.getBizType())) {
                    LeaseType of1 = LeaseType.of(base.getLeaseType());
                    if (of1 != null) {
                        base.setBizTypeName(of1.display);
                    }
                }
            }
            KpiProjectClassifyEnum kpiProjectClassifyEnum = KpiProjectClassifyEnum.find(base.getProjClassify());
            base.setProjClassifyName(kpiProjectClassifyEnum != null ? kpiProjectClassifyEnum.display() : null);
            if (base.getEndDate() != null) {
                base.setResidualMaturity(new BigDecimal(Math.max(ChronoUnit.DAYS.between(kpiProvisionBaseInfo.getProvisionDate(), base.getEndDate()), 0)).divide(new BigDecimal("365"), 2, RoundingMode.HALF_UP).toString());
            }
            if (ObjectUtil.isNotEmpty(base.getWithdrawalRatioHand())) {
                base.setWithdrawalRatio(base.getWithdrawalRatioHand());
            }
        });

        kpiProvisionBaseInfoDetailRSP.setProvisionBaseInfoList(PageR.of(kpiProvisionBaseInfoBody, kpiProvisionDetailPage.getTotal(),
                kpiProvisionDetailPage.getCurrent(), kpiProvisionDetailPage.getSize()));
        return kpiProvisionBaseInfoDetailRSP;
    }

    public void modify(KpiProvisionBaseInfoModifyREQ req) {
        KpiProvisionDetail kpiProvisionDetail = baseMapper.selectById(req.getId());
        if (ObjectUtil.isEmpty(kpiProvisionDetail)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        kpiProvisionDetail.setRemainingPrincipal(req.getRemainingPrincipal());
        kpiProvisionDetail.setEarnestBalance(req.getEarnestBalance());
//        kpiProvisionDetail.setWithdrawalRatioHand(req.getWithdrawalRatio());
        kpiProvisionDetail.setExposure(LongUtil.null2zero(kpiProvisionDetail.getRemainingPrincipal()) - LongUtil.null2zero(kpiProvisionDetail.getEarnestBalance()));
//        kpiProvisionDetail.setProfitCurrent(LongUtil.other2Long(LongUtil.tenThousand2Dollar(LongUtil.null2zero(kpiProvisionDetail.getExposure()).toString()).multiply(LongUtil.tenThousand2Dollar(LongUtil.null2zero(req.getWithdrawalRatio()).toString()).divide(new BigDecimal(100), 10, RoundingMode.HALF_UP)).toString()));
        kpiProvisionDetail.setBonusCurrent(LongUtil.null2zero(kpiProvisionDetail.getProfitCurrent()) - LongUtil.null2zero(kpiProvisionDetail.getProfitTotal()));
        KpiProvisionBaseInfo kpiProvisionBaseInfo1 = kpiProvisionBaseInfoMapper.selectById(kpiProvisionDetail.getProvisionId());
        if (ObjectUtil.isNotEmpty(kpiProvisionBaseInfo1)) {
            kpiProvisionBaseInfo1.setProvisionStatus(KpiProvisionStatusEnum.UN_EFFECT.name());
            kpiProvisionBaseInfoMapper.updateById(kpiProvisionBaseInfo1);
        }
        buildProfitCurrentSingle(kpiProvisionDetail, kpiProvisionDetail.getProvisionDate());
        kpiProvisionDetail = baseMapper.selectById(req.getId());
        //修改下个月
        LocalDate localDate = kpiProvisionDetail.getProvisionDate().plusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
        KpiProvisionBaseInfo kpiProvisionBaseInfo = kpiProvisionBaseInfoMapper.selectOne(Wrappers.<KpiProvisionBaseInfo>lambdaQuery()
                .eq(KpiProvisionBaseInfo::getProvisionDate, localDate)
                .last(StringUtil.mysqlLimitOne()));
        if (ObjectUtil.isNotEmpty(kpiProvisionBaseInfo)) {
            KpiProvisionDetail kpiProvisionDetail1 = baseMapper.selectOne(Wrappers.<KpiProvisionDetail>lambdaQuery()
                    .eq(KpiProvisionDetail::getProvisionId, kpiProvisionBaseInfo.getId())
                    .eq(KpiProvisionDetail::getReceiptId, kpiProvisionDetail.getReceiptId()));
            if (ObjectUtil.isNotEmpty(kpiProvisionDetail1)) {
                buildProfitCurrentSingle(kpiProvisionDetail1, localDate);

                kpiProvisionDetail1.setProfitTotal(kpiProvisionDetail.getProfitCurrent());
                kpiProvisionDetail1.setBonusCurrent(LongUtil.null2zero(kpiProvisionDetail1.getProfitCurrent()) - LongUtil.null2zero(kpiProvisionDetail1.getProfitTotal()));
                baseMapper.updateById(kpiProvisionDetail1);
            }
        }
        baseMapper.updateById(kpiProvisionDetail);
    }

    public void buildProfitCurrentSingle(KpiProvisionDetail provisionDetail, LocalDate provisionDate){
        if(provisionDetail == null){
            return;
        }
        buildProfitCurrent2(Collections.singletonList(provisionDetail.getId()), provisionDate);
    }

    //计算本月风险金余额
    @Deprecated
    public void buildProfitCurrent(List<KpiProvisionDetail> provisionDetailList, LocalDate provisionDate) {
        if(CollectionUtil.isEmpty(provisionDetailList)){
            return;
        }
        // 应付利息
        Map<Long, Long> accruedInterestMap = contractIncomeSharingService.getAccruedInterest(provisionDetailList.stream().map(KpiProvisionDetail::getReceiptId).collect(Collectors.toList()), provisionDate);

        LocalDate now = LocalDate.now();
        Set<Long> contractIdList = provisionDetailList.stream().map(KpiProvisionDetail::getContractId).collect(Collectors.toSet());
        Set<Long> clientIdList = provisionDetailList.stream().map(KpiProvisionDetail::getClientId).collect(Collectors.toSet());
        List<LeaseItemInfo> leaseItemInfoList = leaseItemInfoService.list();
        Map<Long, List<String>> leaseItemTypeMap = contractIdList.stream().collect(Collectors.toMap(Function.identity(), contractId -> {
            return Optional.ofNullable(leaseItemInfoList.stream().filter(item -> {
                List<Long> list = Optional.ofNullable(item.getContractIds()).map(m -> JSON.parseArray(m, Long.class)).orElse(Collections.emptyList());
                return list.contains(contractId);
            }).map(m -> JSON.parseArray(m.getLeaseItemTypes(),String.class)).flatMap(Collection::stream).collect(Collectors.toList())).orElse(Collections.emptyList());
        }, (m1, m2) -> m1));
        // 逾期天数
        Map<Long, Long> overdueMaxDate = new HashMap<>();
        Map<Long, LocalDate> overdueMaxDateTime = new HashMap<>();

        List<CollectionBaseInfo> collectionBaseInfoList = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .in(CollectionBaseInfo::getContractId, contractIdList)
                .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                .gt(CollectionBaseInfo::getPhase, 0)
                .lt(CollectionBaseInfo::getPlanCollectionDate, provisionDate));
        Map<Long, List<CollectionBaseInfo>> collectionMap = collectionBaseInfoList.stream().collect(Collectors.groupingBy(CollectionBaseInfo::getContractId));
        if (CollectionUtil.isNotEmpty(collectionBaseInfoList)) {
            // 逾期合同最大天数 为0代表未逾期
            overdueMaxDate = collectionMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                List<CollectionBaseInfo> collectionBaseInfos = collectionMap.get(entry.getKey());
                LocalDate maxOverdueDate = collectionBaseInfos.stream().filter(f -> f.getPlanCollectionDate().isBefore(now)).filter(e -> {
                    long planRent = Optional.ofNullable(e.getPlanCollectionAmount()).orElse(0L);
                    long actualRent = Optional.ofNullable(e.getCollectionAmount()).orElse(0L);
                    return planRent > actualRent;
                }).map(CollectionBaseInfo::getPlanCollectionDate).min(Comparator.comparing(Function.identity())).orElse(now);
                return ChronoUnit.DAYS.between(maxOverdueDate ,now);
            }));
            overdueMaxDateTime = collectionMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                List<CollectionBaseInfo> collectionBaseInfos = collectionMap.get(entry.getKey());
                LocalDate maxOverdueDate = collectionBaseInfos.stream().filter(f -> f.getPlanCollectionDate().isBefore(now)).filter(e -> {
                    long planRent = Optional.ofNullable(e.getPlanCollectionAmount()).orElse(0L);
                    long actualRent = Optional.ofNullable(e.getCollectionAmount()).orElse(0L);
                    return planRent > actualRent;
                }).map(CollectionBaseInfo::getPlanCollectionDate).min(Comparator.comparing(Function.identity())).orElse(now);
                return maxOverdueDate;
            }));
        }
        Map<Long, String> clientId2Name = id2NameService.clientId2Name(clientIdList);

        // 评级信息
        Map<Long, List<RatingClientLib>> ratingClientLibMap = Optional.ofNullable(SpringContextHolder.getBean(RatingClientLibService.class).list(Wrappers.<RatingClientLib>lambdaQuery()
                .in(RatingClientLib::getClientId, provisionDetailList.stream().map(KpiProvisionDetail::getClientId).collect(Collectors.toList()))
                .eq(RatingClientLib::getProcessStatus, ProcessStatus.APPROVAL_PASS.name()))).map(m -> m.stream().collect(Collectors.groupingBy(RatingClientLib::getClientId))).orElse(Collections.emptyMap());

        for (KpiProvisionDetail kpiProvisionDetail : provisionDetailList) {
            Long accruedInterest = accruedInterestMap.get(kpiProvisionDetail.getReceiptId());
            KpiExpectedLossDecisionQuery query = new KpiExpectedLossDecisionQuery();
            query.setOrder_num(kpiProvisionDetail.getContractCode());
            query.setClient_name(clientId2Name.get(kpiProvisionDetail.getClientId()));
            query.setClassify(Optional.ofNullable(AssetClassifyResultEnum.valueOf(kpiProvisionDetail.getRiskLevel())).map(AssetClassifyResultEnum::getDisplay).orElse(null));
            query.setExpire_day(kpiProvisionDetail.getEndDate());
            query.setAccrued_interest(Util.mithrasLong2BigDecimal(accruedInterest));
            query.setRemain_principal(Util.mithrasLong2BigDecimal(kpiProvisionDetail.getRemainingPrincipal()));
            query.setDeposit(Util.mithrasLong2BigDecimal(kpiProvisionDetail.getEarnestBalance()));
            List<String> leaseTypeList = leaseItemTypeMap.get(kpiProvisionDetail.getContractId());
            query.setLease_type(leaseTypeList.contains(LeaseItemManagerLeaseItemType.VESSEL.name()) ? KpiLeaseItemTypeEnum.INCLUDE_SHIP.display() : KpiLeaseItemTypeEnum.EXCLUDE_SHIP.display());

            List<RatingClientLib> ratingClientLibs = ratingClientLibMap.get(kpiProvisionDetail.getClientId());
            if(CollectionUtil.isNotEmpty(ratingClientLibs)) {
                RatingClientLib lastRatingClient = ratingClientLibs.stream().max(Comparator.comparing(RatingClientLib::getCreateTime)).orElse(new RatingClientLib());
                RatingClientLib firstRatingClient = ratingClientLibs.stream().filter(f -> Objects.equals(f.getModelCode(), lastRatingClient.getModelCode())).min(Comparator.comparing(RatingClientLib::getCreateTime)).orElse(new RatingClientLib());
                query.setInner_level(lastRatingClient.getFinalScore());
                query.setInner_pd(Optional.ofNullable(lastRatingClient.getPd()).map(BigDecimal::new).orElse(null));
                query.setGroup(Optional.ofNullable(KpiRatingModelGroupEnum.valueOf(lastRatingClient.getModelCode())).map(KpiRatingModelGroupEnum::getDisplay).orElse(null));
                query.setInner_first_level(firstRatingClient.getFinalScore());
            }
            query.setLate_day(overdueMaxDate.get(kpiProvisionDetail.getContractId()));
            query.setLateDate(overdueMaxDateTime.get(kpiProvisionDetail.getContractId()));
            DecisionExecuteEclResult result = decisionService.eclExecute(query);
            if(result == null){
                log.error("本月风险金余额计算失败,id=" + kpiProvisionDetail.getId());
            }
            kpiProvisionDetail.setAccruedInterest(accruedInterest);
            kpiProvisionDetail.setProfitCurrent(Util.toMithrasUnit(result.getEcl()));
            kpiProvisionDetail.setBonusCurrent(LongUtil.null2zero(kpiProvisionDetail.getProfitCurrent()) - LongUtil.null2zero(kpiProvisionDetail.getProfitTotal()));
        }
    }

    /**
     * 计算某个日期之前的风险金余额
     **/
    @Transactional(rollbackFor = Throwable.class)
    public void buildProfitCurrent2(List<Long> provisionDetailIds, LocalDate provisionDate) {
        if(CollectionUtil.isEmpty(provisionDetailIds)){
            return;
        }
        List<KpiProvisionDetail> provisionDetailList = kpiProvisionDetailService.listByIds(provisionDetailIds);
        if(CollectionUtil.isEmpty(provisionDetailList)){
            return;
        }
        StopWatch stopWatch = new StopWatch("计算某个日期之前的风险金余额");
        stopWatch.start("构建参数3");
        //财务存在预测拨备的情况，此时按照当前时间来计算逾期
        LocalDate now = provisionDate;
        LocalDate now1 = LocalDate.now();
        Set<Long> contractIdList = provisionDetailList.stream().map(KpiProvisionDetail::getContractId).collect(Collectors.toSet());
        Set<Long> clientIdList = provisionDetailList.stream().map(KpiProvisionDetail::getClientId).collect(Collectors.toSet());
        List<LeaseItemInfo> leaseItemInfoList = leaseItemInfoService.list();
        stopWatch.stop();
        stopWatch.start("构建参数4");
        Map<Long, List<String>> leaseItemTypeMap = contractIdList.stream().collect(Collectors.toMap(Function.identity(), contractId -> {
            return Optional.ofNullable(leaseItemInfoList.stream().filter(item -> {
                List<Long> list = Optional.ofNullable(item.getContractIds()).map(m -> JSON.parseArray(m, Long.class)).orElse(Collections.emptyList());
                return list.contains(contractId);
            }).map(m -> JSON.parseArray(m.getLeaseItemTypes(),String.class)).flatMap(Collection::stream).collect(Collectors.toList())).orElse(Collections.emptyList());
        }, (m1, m2) -> m1));
        stopWatch.stop();
        stopWatch.start("构建参数5");

        // 逾期天数
        Map<Long, Long> overdueMaxDate = new HashMap<>();
        Map<Long, LocalDate> overdueMaxDateTime = new HashMap<>();

        List<CollectionBaseInfo> collectionBaseInfoList = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .in(CollectionBaseInfo::getContractId, contractIdList)
                .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                .gt(CollectionBaseInfo::getPhase, 0)
                .lt(CollectionBaseInfo::getPlanCollectionDate, now));
        Map<Long, List<CollectionBaseInfo>> collectionMap = collectionBaseInfoList.stream().collect(Collectors.groupingBy(CollectionBaseInfo::getContractId));
        if (CollectionUtil.isNotEmpty(collectionBaseInfoList)) {
            // 逾期合同最大天数 为0代表未逾期
            overdueMaxDate = collectionMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                List<CollectionBaseInfo> collectionBaseInfos = collectionMap.get(entry.getKey());
                LocalDate maxOverdueDate = collectionBaseInfos.stream().filter(f -> f.getPlanCollectionDate().isBefore(now1)).filter(e -> {
                    long planRent = Optional.ofNullable(e.getPlanCollectionAmount()).orElse(0L);
                    long actualRent = Optional.ofNullable(e.getCollectionAmount()).orElse(0L);
                    return planRent > actualRent;
                }).map(CollectionBaseInfo::getPlanCollectionDate).min(Comparator.comparing(Function.identity())).orElse(now);
                return ChronoUnit.DAYS.between(maxOverdueDate ,now);
            }));
            overdueMaxDateTime = collectionMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                List<CollectionBaseInfo> collectionBaseInfos = collectionMap.get(entry.getKey());
                return collectionBaseInfos.stream().filter(f -> f.getPlanCollectionDate().isBefore(now1)).filter(e -> {
                    long planRent = Optional.ofNullable(e.getPlanCollectionAmount()).orElse(0L);
                    long actualRent = Optional.ofNullable(e.getCollectionAmount()).orElse(0L);
                    return planRent > actualRent;
                }).map(CollectionBaseInfo::getPlanCollectionDate).min(Comparator.comparing(Function.identity())).orElse(now);
            }));
        }
        //查询项目信息
        List<ContractBaseInfo> contractBaseInfos = contractBaseInfoService.listByIds(contractIdList);
        Map<Long, List<String>> contractId2LeaseItemTypes = contractBaseInfos.stream().collect(Collectors.toMap(ContractBaseInfo::getId, c -> {
            return Optional.ofNullable(c.getLeaseItemTypes()).map(e ->JSONUtil.toList(e, String.class)).orElse(new ArrayList<>());}, (a, b) -> a));
        Map<Long, Long> contractId2ReviewId = contractBaseInfos.stream().collect(Collectors.toMap(ContractBaseInfo::getId, ContractBaseInfo::getProjReviewId));
        Map<Long, Long> reviewId2EvaluationSubjectId = new HashMap<>();
        List<Long> projReviewIds = contractBaseInfos.stream().map(ContractBaseInfo::getProjReviewId).collect(Collectors.toList());
        if (ObjectUtil.isNotEmpty(projReviewIds)) {
            reviewId2EvaluationSubjectId.putAll(projReviewBaseInfoLibHandler.listLatestByOriginIds(projReviewIds)
                    .stream().filter(e -> ObjectUtil.isNotEmpty(e.getEvaluationSubjectId())).collect(Collectors.toMap(ProjReviewBaseInfoLib::getOriginId, ProjReviewBaseInfoLib::getEvaluationSubjectId, (a, b) -> b)));
        }
        Map<Long, Long> contractId2EvaluationSubjectId = new HashMap<>();
        contractId2ReviewId.forEach((k, v) -> {
            contractId2EvaluationSubjectId.put(k, reviewId2EvaluationSubjectId.get(v));
        });
        clientIdList.addAll(contractId2EvaluationSubjectId.values());
        Map<Long, String> clientId2Name = id2NameService.clientId2Name(clientIdList);

        List<RatingClientLib> ratingClientLibList = SpringContextHolder.getBean(RatingClientLibService.class).list(Wrappers.<RatingClientLib>lambdaQuery()
                .in(RatingClientLib::getClientId, clientIdList)
                .eq(RatingClientLib::getProcessStatus, ProcessStatus.APPROVAL_PASS.name()));
        // 评级信息
        Map<Long, List<RatingClientLib>> ratingClientLibMap = Optional.ofNullable(ratingClientLibList).map(m -> m.stream().collect(Collectors.groupingBy(RatingClientLib::getClientId))).orElse(Collections.emptyMap());

        //查询pd
        Map<String, String> ratingLeave2PdMap = eclBusinessConfigService.innerLeave2PD();
        Map<String, String> innerLeave2OutLeaveMap = eclBusinessConfigService.innerLeave2OutLeave();
        EclBreachMappingBO eclBreachMappingBO = JSONUtil.toBean(eclBusinessConfigService.getByCode(EclConfigEnum.BREACH_MAPPING.name()).getConfigValue(), EclBreachMappingBO.class);
        //md评级对应pd
        List<EclBreachMappingBO.BreachMappingData> mdLeave2Pd = eclBreachMappingBO.getData();
        //查询调整因子Z
        Map<String, EclForwardZBO.EclForwardZBOData> group2ForwardZ = JSONUtil.toBean(eclBusinessConfigService.getByCode(EclConfigEnum.FORWARD_Z.name()).getConfigValue(), EclForwardZBO.class).getData().stream().collect(Collectors.toMap(EclForwardZBO.EclForwardZBOData::getGroup, e -> e, (a, b) -> a));

        //获取lgd
        Map<String, String> leaseType2Lgd = JSONUtil.toBean(eclBusinessConfigService.getByCode(EclConfigEnum.LOSS_LGD.name()).getConfigValue(), EclLossLgdBO.class).getData().stream().collect(Collectors.toMap(EclLossLgdBO.EclLossLgdData::getLeaseType, EclLossLgdBO.EclLossLgdData::getLgd, (a, b) -> a));

        //情景权重
        Map<String, BigDecimal> eclWeightMap = JSONUtil.toBean(eclBusinessConfigService.getByCode(EclConfigEnum.SCENARIO_WEIGHT.name()).getConfigValue(), EclScenarioWeightBO.class).getData().stream().collect(Collectors.toMap(EclScenarioWeightBO.EclScenarioWeightData::getScene, EclScenarioWeightBO.EclScenarioWeightData::getSceneWeight, (a, b) -> a));

        //获取内部评级
        Map<String, String> innerLeave2Pd = JSONUtil.toBean(eclBusinessConfigService.getByCode(EclConfigEnum.INNER_BREACH_MAPPING.name()).getConfigValue(), EclInnerBreachMappingBO.class).getData().stream().collect(Collectors.toMap(EclInnerBreachMappingBO.BreachMappingData::getInnerLevel,
                EclInnerBreachMappingBO.BreachMappingData::getInnerPd, (a, b) -> a));

        //获取外部评级
        RatingManagementRSP domesticBaseLeaveR = null;
        try {
            domesticBaseLeaveR = ratingManagementClient.getDomesticBaseLeave(new ArrayList<>(clientId2Name.values())).getData();
        } catch (Exception e) {
            log.warn("获取外部评级异常", e);
        }
        Map<String, String> clientName2Rating = new HashMap<>();
        if (ObjectUtil.isNotEmpty(domesticBaseLeaveR)) {
            clientName2Rating.putAll(domesticBaseLeaveR.getClientName2Rating());
        }
        List<EclResultBO> eclResultBOS = new ArrayList<>();
        //查询是否有导入数据
        Map<Long, EclExecuteRecord> importProvisionDetailId2EclExecuteRecord = new HashMap<>();
        importProvisionDetailId2EclExecuteRecord.putAll(eclExecuteRecordService.list(Wrappers.<EclExecuteRecord>lambdaQuery()
        .in(EclExecuteRecord::getKpiProvisionDetailId, provisionDetailIds)
        .eq(EclExecuteRecord::getSourceType, YesOrNoNumberEnum.YES.getCode())).stream().collect(Collectors.toMap(EclExecuteRecord::getKpiProvisionDetailId, e -> e, (a, b) -> b)));
        //获取上迁结果
        Map<Long, Integer> promotionResult = SpringContextHolder.getBean(EclExecuteClientPromotionResultService.class).getPromotionResult(null, 6);

        stopWatch.stop();
        stopWatch.start("请求模型");
        Long clientId;
        //这里可以改成多线程调用
        for (KpiProvisionDetail kpiProvisionDetail : provisionDetailList) {
            EclExecuteRecord importRecord = importProvisionDetailId2EclExecuteRecord.get(kpiProvisionDetail.getId());
            KpiExpectedLossDecisionQuery query = new KpiExpectedLossDecisionQuery();
            //先取评估主体
            clientId = contractId2EvaluationSubjectId.get(kpiProvisionDetail.getContractId()) == null ? kpiProvisionDetail.getClientId() : contractId2EvaluationSubjectId.get(kpiProvisionDetail.getContractId());
            query.setOrder_num(kpiProvisionDetail.getContractCode());
            query.setClientId(clientId);
            query.setClient_name(clientId2Name.get(clientId));
            query.setActualClientId(kpiProvisionDetail.getClientId());
            query.setActualClientName(clientId2Name.get(kpiProvisionDetail.getClientId()));
            query.setClassify(Optional.of(AssetClassifyResultEnum.valueOf(kpiProvisionDetail.getRiskLevel())).map(AssetClassifyResultEnum::getDisplay).orElse(null));
            query.setExpire_day(kpiProvisionDetail.getEndDate());
            if (ObjectUtil.isNotEmpty(query.getExpire_day())) {
                query.setDate_difference(Math.max(ChronoUnit.DAYS.between(now, query.getExpire_day()), 0));
            }
            query.setAccrued_interest(Util.mithrasLong2BigDecimal(LongUtil.null2zero(kpiProvisionDetail.getAccruedInterest())));
            query.setRemain_principal(Util.mithrasLong2BigDecimal(LongUtil.null2zero(kpiProvisionDetail.getRemainingPrincipal())));
            query.setDeposit(Util.mithrasLong2BigDecimal(LongUtil.null2zero(kpiProvisionDetail.getEarnestBalance())));
           // List<String> leaseTypeList = leaseItemTypeMap.get(kpiProvisionDetail.getContractId());

            List<String> leaseTypeList = contractId2LeaseItemTypes.get(kpiProvisionDetail.getContractId());
            if (ObjectUtil.isNotEmpty(leaseTypeList)) {
                query.setLease_type(leaseTypeList.contains(LeaseItemManagerLeaseItemType.VESSEL.name()) ? KpiLeaseItemTypeEnum.INCLUDE_SHIP.display() : KpiLeaseItemTypeEnum.EXCLUDE_SHIP.display());
            } else {
                query.setLease_type(KpiLeaseItemTypeEnum.EXCLUDE_SHIP.display());
            }
            query.setLgd(leaseType2Lgd.get(query.getLease_type()));

            query.setBase_weight(eclWeightMap.get(BASE_SCENE));
            query.setOpt_weight(eclWeightMap.get(OPT_SCENE));
            query.setGlo_weight(eclWeightMap.get(GLO_SCENE));
            query.setPromotionResult(promotionResult.getOrDefault(kpiProvisionDetail.getContractId(), 0));

            query.setEcl_breach_mapping_map(JSONUtil.toJsonStr(mdLeave2Pd));
            if (ObjectUtil.isNotEmpty(importRecord)) {
                if (ObjectUtil.isEmpty(query.getClient_name())) {
                    query.setClient_name(importRecord.getClientName());
                }
                if (ObjectUtil.isEmpty(query.getActualClientName())) {
                    query.setActualClientName(importRecord.getClientName());
                }
                if (ObjectUtil.isNotEmpty(importRecord)) {
                    query.setInner_level(importRecord.getInnerMdLevel());
                    query.setGroup(importRecord.getGroup());
                }
                if (ObjectUtil.isNotEmpty(query.getLease_type())) {
                    query.setLease_type(importRecord.getLeaseType());
                }
            }

            List<RatingClientLib> ratingClientLibs = ratingClientLibMap.get(clientId);

            if(CollectionUtil.isNotEmpty(ratingClientLibs)) {
                RatingClientLib lastRatingClient = ratingClientLibs.stream().max(Comparator.comparing(RatingClientLib::getCreateTime)).orElse(new RatingClientLib());
                RatingClientLib firstRatingClient = ratingClientLibs.stream().filter(f -> Objects.equals(f.getModelCode(), lastRatingClient.getModelCode())).min(Comparator.comparing(RatingClientLib::getCreateTime)).orElse(new RatingClientLib());
                if (ObjectUtil.isEmpty(query.getInner_level())) {
                    query.setInner_level(lastRatingClient.getFinalScore());
                }
                String pd = innerLeave2Pd.get(query.getInner_level());
                query.setInner_pd(new BigDecimal(pd == null ? "0" : pd));
                //query.setInner_pd(new BigDecimal(lastRatingClient.getPd() == null ? "0" : lastRatingClient.getPd()));
                if (ObjectUtil.isEmpty(query.getGroup())) {
                    query.setGroup(Optional.ofNullable(KpiRatingModelGroupEnum.find(lastRatingClient.getModelCode())).map(KpiRatingModelGroupEnum::getDisplay).orElse(null));
                }
                EclForwardZBO.EclForwardZBOData eclForwardZBO = group2ForwardZ.get(KpiRatingModelGroupEnum.client_hymx.display().equals(query.getGroup()) ? KpiRatingModelGroupEnum.client_fzzy_service.display() : query.getGroup());
                if (ObjectUtil.isNotEmpty(eclForwardZBO)) {
                    query.setRzy_ecl_base_z(eclForwardZBO.getFactorBaseZ());
                    query.setRzy_ecl_glo_z(eclForwardZBO.getFactorGloZ());
                    query.setRzy_ecl_opt_z(eclForwardZBO.getFactorOptZ());
                }
                query.setInner_first_level(firstRatingClient.getFinalScore());
                //计算下迁等级
                EclOuterLevelEnum nowDisplay = EclOuterLevelEnum.getByDisplay(innerLeave2OutLeaveMap.get(query.getInner_level()));
                EclOuterLevelEnum firstDisplay = EclOuterLevelEnum.getByDisplay(innerLeave2OutLeaveMap.get(query.getInner_first_level()));

                if(ObjectUtil.isNotEmpty(nowDisplay) && ObjectUtil.isNotEmpty(firstDisplay)) {
                    query.setRzy_ecl_down_level(Math.max(nowDisplay.getOrder() - firstDisplay.getOrder(), 0));
                } else {
                    query.setRzy_ecl_down_level(0);
                }
            } else if (ObjectUtil.isNotEmpty(query.getInner_level())) {
                String pd = ratingLeave2PdMap.get(query.getInner_level());
                query.setInner_pd(new BigDecimal(pd == null ? "0" : pd));
                EclForwardZBO.EclForwardZBOData eclForwardZBO = group2ForwardZ.get(KpiRatingModelGroupEnum.client_hymx.display().equals(query.getGroup()) ? KpiRatingModelGroupEnum.client_fzzy_service.display() : query.getGroup());
                if (ObjectUtil.isNotEmpty(eclForwardZBO)) {
                    query.setRzy_ecl_base_z(eclForwardZBO.getFactorBaseZ());
                    query.setRzy_ecl_glo_z(eclForwardZBO.getFactorGloZ());
                    query.setRzy_ecl_opt_z(eclForwardZBO.getFactorOptZ());
                }
                query.setRzy_ecl_down_level(0);
            }

            query.setLate_day(overdueMaxDate.get(kpiProvisionDetail.getContractId()));
            query.setLateDate(overdueMaxDateTime.get(kpiProvisionDetail.getContractId()));
            //补充外部信息
            query.setOuter_level(innerLeave2OutLeaveMap.get(clientName2Rating.get(query.getClient_name())));
            query.setEcl_out_pd(ratingLeave2PdMap.get(clientName2Rating.get(query.getClient_name())));
            DecisionExecuteEclResult result = decisionService.eclExecute(query);
            if(result == null){
                log.error("本月风险金余额计算失败,id=" + kpiProvisionDetail.getId());
            }

            kpiProvisionDetail.setProfitCurrent(Util.toMithrasUnit(result.getEcl()));
            kpiProvisionDetail.setBonusCurrent(LongUtil.null2zero(kpiProvisionDetail.getProfitCurrent()) - LongUtil.null2zero(kpiProvisionDetail.getProfitTotal()));
            EclResultBO eclResultBO = new EclResultBO();
            eclResultBO.setKpiProvisionDetail(kpiProvisionDetail);
            eclResultBO.setQuery(query);
            eclResultBO.setResult(result);
            eclResultBOS.add(eclResultBO);
        }
        stopWatch.stop();
        stopWatch.start("保存结果");
        //批量保存ecl记录
        List<EclExecuteRecord> records = eclExecuteRecordService.modifyRecordByEclResultBO(eclResultBOS);
        if (CollectionUtil.isNotEmpty(records)) {
            Map<Long, String> kpiProvisionDetailId2RemarkMap = records.stream().filter(e -> ObjectUtil.isNotEmpty(e.getKpiProvisionDetailId()) && ObjectUtil.isNotEmpty(e.getRemark())).collect(Collectors.toMap(EclExecuteRecord::getKpiProvisionDetailId, EclExecuteRecord::getRemark, (a, b) -> b));
            provisionDetailList.forEach(e -> {
                e.setRemark(kpiProvisionDetailId2RemarkMap.get(e.getId()));
            });
        }
        SpringContextHolder.getBean(KpiProvisionDetailService.class).updateBatchById(provisionDetailList);
        stopWatch.stop();
        log.info("KpiProvisionDetailService buildProfitCurrent2 {}", stopWatch.prettyPrint(TimeUnit.SECONDS));
    }

}
