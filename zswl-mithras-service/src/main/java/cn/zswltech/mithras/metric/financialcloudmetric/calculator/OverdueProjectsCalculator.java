package cn.zswltech.mithras.metric.financialcloudmetric.calculator;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.metric.enums.risk.index.RiskMetricStatus;
import cn.zswltech.mithras.metric.financialcloudmetric.model.FinancialCloudMetric;
import cn.zswltech.mithras.metric.financialcloudmetric.model.FinancialCloudMetricValue;
import cn.zswltech.mithras.metric.financialcloudmetric.service.FinancialCloudMetricService;
import cn.zswltech.mithras.metric.financialcloudmetric.service.FinancialCloudMetricValueService;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.service.enums.assetclassify.AssetClassifyResultEnum;
import cn.zswltech.mithras.service.enums.collection.CollectionWriteOffStatusEnum;
import cn.zswltech.mithras.service.enums.common.ProjectBizType;
import cn.zswltech.mithras.service.mapper.collection.CollectionBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.collection.CollectionRecordInfoMapper;
import cn.zswltech.mithras.service.mapper.lib.assetclassify.AssetClassifyClientAuxiliaryLibMapper;
import cn.zswltech.mithras.service.mapper.lib.contract.ContractBaseInfoLibMapper;
import cn.zswltech.mithras.service.mapper.lib.contract.ContractGuarantorLibMapper;
import cn.zswltech.mithras.service.mapper.lib.projreview.ProjReviewAocPriceLibMapper;
import cn.zswltech.mithras.service.mapper.lib.projreview.ProjReviewFactoringPriceLibMapper;
import cn.zswltech.mithras.service.mapper.lib.projreview.ProjReviewLeasePriceLibMapper;
import cn.zswltech.mithras.service.mapper.model.assetclassify.AssetClassifyClient;
import cn.zswltech.mithras.service.mapper.model.assetclassify.AssetClassifyClientAuxiliaryLib;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionBaseInfo;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionRecordInfo;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfoLib;
import cn.zswltech.mithras.service.mapper.model.contract.ContractGuarantorLib;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewAocPriceLib;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewFactoringPriceLib;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewLeasePriceLib;
import cn.zswltech.mithras.service.mapper.projreview.ProjReviewBaseInfoMapper;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.Id2NameService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.riskcontrol.RemainingPrincipalServiceImpl;
import cn.zswltech.mithras.service.service.riskcontrol.dto.ProjReviewPriceDto;
import cn.zswltech.mithras.service.util.LongUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.Data;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @description: 项目逾期信息计算类
 * @author: zhaozhengkang
 * @date: 2023/4/13 09:57
 */
@Component
public class OverdueProjectsCalculator {

    @Resource
    private CollectionBaseInfoMapper collectionBaseInfoMapper;
    @Resource
    private ContractBaseInfoLibMapper contractBaseInfoLibMapper;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private AssetClassifyClientAuxiliaryLibMapper assetClassifyClientAuxiliaryLibMapper;
    @Resource
    private ContractGuarantorLibMapper guarantorLibMapper;
    @Resource
    private ProjReviewLeasePriceLibMapper leasePriceLibMapper;
    @Resource
    private ProjReviewFactoringPriceLibMapper factoringPriceLibMapper;
    @Resource
    private ProjReviewAocPriceLibMapper aocPriceLibMapper;
    @Resource
    private ProjReviewBaseInfoMapper projReviewBaseInfoMapper;
    @Resource
    private RemainingPrincipalServiceImpl remainingPrincipalServiceImpl;
    @Resource
    private FinancialCloudMetricService financialCloudMetricService;
    @Resource
    private FinancialCloudMetricValueService financialCloudMetricValueService;

    public void calculate(LocalDate dateTime) {
        // 保证接口幂等，先删除本月已计算过的风险项目数据
        financialCloudMetricValueService.remove(Wrappers.<FinancialCloudMetricValue>lambdaQuery()
                .eq(FinancialCloudMetricValue::getOneLevelType, "按项目")
                .eq(FinancialCloudMetricValue::getDataTime, dateTime));


        LocalDate overdueDate = dateTime.with(TemporalAdjusters.lastDayOfMonth());
        if (dateTime.getMonthValue() == LocalDate.now().getMonthValue()) {
            overdueDate = LocalDate.now();
        }
        Map<Long, List<CollectionBaseInfo>> collect = collectionBaseInfoMapper.selectList(Wrappers.<CollectionBaseInfo>lambdaQuery()
                        .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                        .gt(CollectionBaseInfo::getPhase, 0)
                        .isNotNull(CollectionBaseInfo::getCode)
                        .lt(CollectionBaseInfo::getPlanCollectionDate, overdueDate))
                .stream().collect(Collectors.groupingBy(CollectionBaseInfo::getContractId));

        // 合同维度计算剩余本金
        Map<Long, Triple<BigDecimal, BigDecimal, Long>> remainingAmount = new HashMap<>();

        // 遍历每个分片并执行查询或其他操作
        List<CollectionBaseInfo> overdueRecord = new ArrayList<>();
        for (Map.Entry<Long, List<CollectionBaseInfo>> entry : collect.entrySet()) {
            // 查询实际的收款
            Long contractId = entry.getKey();
            Triple<BigDecimal, BigDecimal, Long> principalInterestTriple;
            List<CollectionBaseInfo> collectionBaseInfos = collect.get(contractId);
            List<Long> collectionBaseInfoIds = collectionBaseInfos.stream().map(CollectionBaseInfo::getId).collect(Collectors.toList());
            List<CollectionRecordInfo> collectionRecordInfos = SpringUtil.getBean(CollectionRecordInfoMapper.class).selectList(Wrappers.<CollectionRecordInfo>lambdaQuery()
                    .in(CollectionRecordInfo::getCollectionId, collectionBaseInfoIds)
                    .le(CollectionRecordInfo::getCollectionDate, overdueDate));
            // 检查实际收款和预计收款找出逾期记录
            if (CollUtil.isEmpty(collectionRecordInfos)) {
                // 未收款，逾期，将本金和利息添加到逾期记录中
                BigDecimal principal = collectionBaseInfos.stream().map(CollectionBaseInfo::getPrincipal)
                        .map(LongUtil::null2zero)
                        .map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal interest = collectionBaseInfos.stream().map(CollectionBaseInfo::getInterest)
                        .map(LongUtil::null2zero)
                        .map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add);
                collectionBaseInfos.sort(Comparator.comparing(CollectionBaseInfo::getPlanCollectionDate));
                principalInterestTriple = Triple.of(principal, interest, LocalDateTimeUtil.between(collectionBaseInfos.get(0).getPlanCollectionDate().atStartOfDay(), overdueDate.atStartOfDay(), ChronoUnit.DAYS));
                remainingAmount.put(contractId, principalInterestTriple);
                overdueRecord.addAll(collectionBaseInfos);
                continue;
            }

            // 不为空，需要找到每个期项的实际收款
            Map<Long, List<CollectionRecordInfo>> recordListMap = collectionRecordInfos.stream().collect(Collectors.groupingBy(CollectionRecordInfo::getCollectionId));
            List<CollectionBaseInfo> overdueRecordInContract = collectionBaseInfos.stream().filter(collectionBaseInfo -> {
                List<CollectionRecordInfo> infos = recordListMap.get(collectionBaseInfo.getId());
                if (CollUtil.isEmpty(infos)) {
                    // 未收款，逾期
                    return true;
                }
                // 计算实际收款 本金 + 利息
                long actualReceipt = infos.stream().mapToLong(o -> LongUtil.null2zero(o.getPrincipal()) + LongUtil.null2zero(o.getInterest())).sum();
                // 未收款，逾期
                return actualReceipt < LongUtil.null2zero(collectionBaseInfo.getPlanCollectionAmount());
            }).collect(Collectors.toList());
            if (overdueRecordInContract.isEmpty()) {
                continue;
            }
            // 计算逾期记录
            BigDecimal principal = overdueRecordInContract.stream().map(e -> LongUtil.null2zero(e.getPrincipal()) - LongUtil.null2zero(e.getCollectionPrincipal()))
                    .map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal interest = overdueRecordInContract.stream().map(e -> LongUtil.null2zero(e.getInterest()) - LongUtil.null2zero(e.getCollectionInterest()))
                    .map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add);
            overdueRecordInContract.sort(Comparator.comparing(CollectionBaseInfo::getPlanCollectionDate));
            // 需要过滤掉逾期金额是0的
            CollectionBaseInfo collectionBaseInfo = null;
            Optional<CollectionBaseInfo> collectionBaseInfoOptional = overdueRecordInContract.stream()
                    .filter(e -> LongUtil.null2zero(e.getPlanCollectionAmount()) - LongUtil.null2zero(e.getCollectionAmount()) > 0)
                    .min(Comparator.comparing(CollectionBaseInfo::getPlanCollectionDate));
            if(collectionBaseInfoOptional!= null && collectionBaseInfoOptional.isPresent()){
                collectionBaseInfo = collectionBaseInfoOptional.get();
            }
//            CollectionBaseInfo collectionBaseInfo = overdueRecordInContract.stream()
//                    .filter(e -> LongUtil.null2zero(e.getPlanCollectionAmount()) - LongUtil.null2zero(e.getCollectionAmount()) > 0)
//                    .min(Comparator.comparing(CollectionBaseInfo::getPlanCollectionDate)).get();
            if(collectionBaseInfo != null) {
                long overdueDays = LocalDateTimeUtil.between(collectionBaseInfo.getPlanCollectionDate().atStartOfDay(), overdueDate.atStartOfDay(), ChronoUnit.DAYS);
                principalInterestTriple = Triple.of(principal, interest, overdueDays);
                remainingAmount.put(contractId, principalInterestTriple);
                overdueRecord.addAll(overdueRecordInContract);
            }
        }

        if (overdueRecord.isEmpty()) {
            return;
        }
        // 将逾期记录按照合同id分组
        Map<Long, List<CollectionBaseInfo>> contractId2OverdueRecord = overdueRecord.stream()
                .collect(Collectors.groupingBy(CollectionBaseInfo::getContractId));

        // 获取最新的合同基本信息
        List<ContractBaseInfoLib> contractBaseInfoLibs = contractBaseInfoLibMapper.listNewestContractByContractIds(contractId2OverdueRecord.keySet());
        Map<Long, ContractBaseInfoLib> baseInfoLibMap = contractBaseInfoLibs.stream().collect(Collectors.toMap(ContractBaseInfoLib::getOriginId, Function.identity(), (v1, v2) -> v1));

        // 获取客户id
        Set<Long> lessees = contractBaseInfoLibs.stream().map(ContractBaseInfo::getClientId).collect(Collectors.toSet());
        Map<Long, String> lesseeName = id2NameService.clientId2Name(lessees);
        // 查询客户的五级分类
        Map<Long, AssetClassifyClientAuxiliaryLib> classifyResult = new HashMap<>();
        List<AssetClassifyClientAuxiliaryLib> assetClassifyClientAuxiliaryLibs = assetClassifyClientAuxiliaryLibMapper.selectList(Wrappers.<AssetClassifyClientAuxiliaryLib>lambdaQuery()
                        .select(AssetClassifyClientAuxiliaryLib::getClientId,
                                AssetClassifyClientAuxiliaryLib::getClassifyResult,
                                AssetClassifyClientAuxiliaryLib::getVersionType,
                                AssetClassifyClient::getAssetClassifyId,
                                AssetClassifyClientAuxiliaryLib::getVersion)
                .in(AssetClassifyClientAuxiliaryLib::getClientId, lessees));
        assetClassifyClientAuxiliaryLibs.stream().collect(Collectors.groupingBy(AssetClassifyClientAuxiliaryLib::getClientId))
                .forEach((k, v) -> {
                    List<AssetClassifyClientAuxiliaryLib> list = v.stream().filter(e -> Objects.equals(VersionTypeConstants.NORMAL, e.getVersionType()))
                            .sorted(Comparator.comparing(AssetClassifyClientAuxiliaryLib::getAssetClassifyId)
                                    .thenComparing(AssetClassifyClientAuxiliaryLib::getVersion).reversed()).collect(Collectors.toList());
                    classifyResult.put(k, list.get(0));
                });


        Map<Long, Long> riskExposureByContracts = contractBaseInfoService.getStockRiskExposureByContracts(contractId2OverdueRecord.keySet());
        Map<Long, Pair<BigDecimal, BigDecimal>> prePrincipalInterest = remainingPrincipalServiceImpl.prePrincipalInterest(dateTime);
        Map<Long, OverdueProjectInfo> tmpMap = new HashMap<>();
        for (Map.Entry<Long, List<CollectionBaseInfo>> entry : contractId2OverdueRecord.entrySet()) {
            ContractBaseInfoLib contract = baseInfoLibMap.get(entry.getKey());
            OverdueProjectInfo projectInfo;
            if (!tmpMap.containsKey(contract.getProjReviewId())) {
                projectInfo = new OverdueProjectInfo();
                projectInfo.setProjectId(contract.getProjReviewId());
                projectInfo.setProjectName(contract.getProjName());
                projectInfo.setLesseeName(lesseeName.get(contract.getClientId()));
                if (classifyResult.containsKey(contract.getClientId())) {
                    projectInfo.setFiveLevelClassify(AssetClassifyResultEnum
                            .valueOf(classifyResult.get(contract.getClientId()).getClassifyResult()).display());
                }
            } else {
                projectInfo = tmpMap.get(contract.getProjReviewId());
            }

            // 风险敞口放在这里累计
            projectInfo.setRiskExposure(LongUtil.null2zero(projectInfo.getRiskExposure()) + LongUtil.null2zero(riskExposureByContracts.get(contract.getOriginId())));

            // 计算逾期本金和逾期利息 同时累计到剩余本金和剩余利息
            Triple<BigDecimal, BigDecimal, Long> bigDecimalBigDecimalLongTriple = remainingAmount.get(contract.getOriginId());
            projectInfo.setOverduePrincipal(projectInfo.getOverduePrincipal().add(bigDecimalBigDecimalLongTriple.getLeft()));
            projectInfo.setRemainingPrincipal(projectInfo.getRemainingPrincipal().add(bigDecimalBigDecimalLongTriple.getLeft()));
            projectInfo.setOverdueInterest(projectInfo.getOverdueInterest().add(bigDecimalBigDecimalLongTriple.getMiddle()));
            projectInfo.setRemainingInterest(projectInfo.getRemainingInterest().add(bigDecimalBigDecimalLongTriple.getMiddle()));
            projectInfo.setOverdueAmount(projectInfo.getOverdueAmount().add(bigDecimalBigDecimalLongTriple.getLeft()).add(bigDecimalBigDecimalLongTriple.getMiddle()));
            projectInfo.setOverdueDays(Math.max(projectInfo.getOverdueDays(), bigDecimalBigDecimalLongTriple.getRight()));

            // 获取项目评审的报价
            if (projectInfo.getCreditAmount() == null && contract.getProjReviewId() != null) {
                projectInfo.setCreditAmount(getCreditAmount(contract.getProjReviewId()));
            }

            // 获取担保方
            List<ContractGuarantorLib> guarantors = guarantorLibMapper.selectList(
                    Wrappers.<ContractGuarantorLib>lambdaQuery()
                            .eq(ContractGuarantorLib::getContractId, contract.getOriginId())
                            .eq(ContractGuarantorLib::getVersion, contract.getVersion()));
            guarantors.stream().map((Function<ContractGuarantorLib, List<Long>>) contractGuarantorLib -> {
                if (ObjectUtil.isEmpty(contractGuarantorLib.getGuarantorIds())) {
                    return new ArrayList<>();
                }
                return JSON.parseArray(contractGuarantorLib.getGuarantorIds(), Long.class);
            }).forEach(longs -> projectInfo.getGuaranteeNames().addAll(id2NameService.clientId2Name(longs).values().stream().distinct().collect(Collectors.toList())));
            // 更新剩余本金和利息
            if (remainingAmount.containsKey(contract.getOriginId())) {
                Pair<BigDecimal, BigDecimal> pair = prePrincipalInterest.get(contract.getOriginId());
                if (ObjectUtil.isNotNull(pair)) {
                    BigDecimal newRemainingPrincipal = projectInfo.getRemainingPrincipal().add(pair.getKey());
                    BigDecimal newRemainingInterest = projectInfo.getRemainingInterest().add(pair.getValue());
                    projectInfo.setRemainingPrincipal(newRemainingPrincipal);
                    projectInfo.setRemainingInterest(newRemainingInterest);
                }
            }
            tmpMap.put(contract.getProjReviewId(), projectInfo);
        }


        // 动态插入 按项目 的指标
        List<FinancialCloudMetric> metrics =
                financialCloudMetricService.list(Wrappers.<FinancialCloudMetric>lambdaQuery()
                        .eq(FinancialCloudMetric::getOneLevelType, "按项目"));
        List<FinancialCloudMetricValue> metricValues = new ArrayList<>();
        tmpMap.forEach((aLong, overdueProjectInfo) -> {
            if (overdueProjectInfo.getOverdueAmount().compareTo(BigDecimal.ZERO) == 0) {
                return;
            }
            OverdueProjectFormatedInfo format = overdueProjectInfo.format();
            metrics.forEach(metric -> {
                Object obj = ReflectUtil.getFieldValue(format, metric.getMetricCode());
                if (obj == null) {
                    return;
                }
                String fieldValue = obj.toString();
                FinancialCloudMetricValue metricValue =
                        financialCloudMetricValueService.buildMetricValue(metric, dateTime);
                metricValue.setTwoLevelType(format.getFCM_137());
                if (metric.getUnit() == null) {
                    metricValue.setMetricValueAdjusted(fieldValue);
                } else {
                    metricValue.setMetricValue(fieldValue);
                }
                metricValue.setStatus(RiskMetricStatus.PEND_REPORT.name());
                metricValues.add(metricValue);
            });
        });
        if (CollectionUtil.isNotEmpty(metricValues)) {
            financialCloudMetricValueService.saveOrUpdateBatch(metricValues);
        }
    }

    private Long getCreditAmount(Long projReviewId) {
        Long amount = 0L;
        ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoMapper.selectById(projReviewId);
        if (projReviewBaseInfo == null) {
            return amount;
        }
        ProjectBizType projectBizType = ProjectBizType.of(projReviewBaseInfo.getBizType());
        if (projectBizType == null) {
            return amount;
        }
        ProjReviewPriceDto projReviewPriceDto = new ProjReviewPriceDto();
        projReviewPriceDto.setProjReviewIds(new HashSet<>(Collections.singletonList(projReviewId)));

        switch (projectBizType) {
            case ZL:
            case ZZ:
                List<ProjReviewLeasePriceLib> leasePriceLibs = leasePriceLibMapper
                        .listNewestPrice(projReviewPriceDto);
                if (ObjectUtil.isNotEmpty(leasePriceLibs)) {
                    // 取项目批复金额
                    amount = leasePriceLibs.get(0).getProjectApprovalAmount();
                }
                break;
            case ZR:
                List<ProjReviewFactoringPriceLib> factoringPriceLibs = factoringPriceLibMapper.listNewestPrice(projReviewPriceDto);
                if (ObjectUtil.isNotEmpty(factoringPriceLibs)) {
                    // 取项目批复金额
                    amount = factoringPriceLibs.get(0).getProjectApprovalAmount();
                }
                break;
            case BL:
                List<ProjReviewAocPriceLib> aocPriceLibs = aocPriceLibMapper.listNewestPrice(projReviewPriceDto);
                if (ObjectUtil.isNotEmpty(aocPriceLibs)) {
                    // 取项目批复金额
                    amount = aocPriceLibs.get(0).getProjectApprovalAmount();
                }
                break;
            default:
                throw new MithrasException("不支持的业务类型");
        }
        return null == amount ? 0L : amount;
    }
}

@Data
class OverdueProjectInfo {
    private Long projectId;
    private String projectName;
    private String lesseeName;
    private Long riskExposure;
    private String fiveLevelClassify;

    private List<String> guaranteeNames = new ArrayList<>();

    private long overdueDays = 0;
    private BigDecimal overduePrincipal = BigDecimal.ZERO;
    private BigDecimal overdueInterest = BigDecimal.ZERO;
    private BigDecimal overdueAmount = BigDecimal.ZERO;

    private Long creditAmount;

    private BigDecimal remainingPrincipal = BigDecimal.ZERO;
    private BigDecimal remainingInterest = BigDecimal.ZERO;

    public OverdueProjectFormatedInfo format() {
        OverdueProjectFormatedInfo formatInfo = new OverdueProjectFormatedInfo();
        formatInfo.setFCM_137(projectName);
        formatInfo.setFCM_138(overdueAmount.divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP).toString());
        formatInfo.setFCM_139(lesseeName);
        formatInfo.setFCM_140(String.join(",", guaranteeNames));
        formatInfo.setFCM_141(new BigDecimal(LongUtil.null2zero(creditAmount)).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP).toString());
        formatInfo.setFCM_142(remainingPrincipal.divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP).toString());
        formatInfo.setFCM_143(BigDecimal.valueOf(LongUtil.null2zero(riskExposure)).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP).toString());
        formatInfo.setFCM_144(remainingInterest.divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP).toString());
        formatInfo.setFCM_145(overduePrincipal.divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP).toString());
        formatInfo.setFCM_146(overdueInterest.divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP).toString());
        formatInfo.setFCM_147(String.valueOf(overdueDays));
        formatInfo.setFCM_148(fiveLevelClassify == null ? "" : fiveLevelClassify);
        formatInfo.setFCM_149("");
        return formatInfo;
    }
}

/**
 * FCM_137  项目名称
 * FCM_138  逾期金额
 * FCM_139  承租人
 * FCM_140  担保人
 * FCM_141  授信金额
 * FCM_142  剩余本金
 * FCM_143  风险敞口
 * FCM_144  剩余利息
 * FCM_145  逾期本金
 * FCM_146  逾期利息
 * FCM_147  逾期天数
 * FCM_148  五级分类
 * FCM_149  逾期催收情况
 */
@Data
class OverdueProjectFormatedInfo {
    private String FCM_137;
    private String FCM_138;
    private String FCM_139;
    private String FCM_140;
    private String FCM_141;
    private String FCM_142;
    private String FCM_143;
    private String FCM_144;
    private String FCM_145;
    private String FCM_146;
    private String FCM_147;
    private String FCM_148;
    private String FCM_149;
}