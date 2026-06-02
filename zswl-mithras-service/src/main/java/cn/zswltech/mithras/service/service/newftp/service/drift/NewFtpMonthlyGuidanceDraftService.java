package cn.zswltech.mithras.service.service.newftp.service.drift;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.mithras.dto.newftp.FtpValue;
import cn.zswltech.mithras.dto.newftp.NewFtpMonthlyGuidanceDetaiRsp;
import cn.zswltech.mithras.dto.newftp.NewFtpMonthlyGuidanceModifyREQ;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.convert.CommonConvert;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.common.RecordStatus;
import cn.zswltech.mithras.ftp.oldftp.enums.FtpBusinessVersion;
import cn.zswltech.mithras.service.enums.newftp.*;
import cn.zswltech.mithras.ftp.newftp.enums.*;
import cn.zswltech.mithras.service.others.AuthCheckException;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.ftp.oldftp.bo.NewFtpQuarterPricingBO;
import cn.zswltech.mithras.ftp.newftp.convert.NewFtpMonthlyGuidanceConfigConverter;
import cn.zswltech.mithras.service.service.newftp.fms.DefaultNewFtpStateMachine;
import cn.zswltech.mithras.service.service.newftp.fms.NewFtpContext;
import cn.zswltech.mithras.service.service.newftp.fms.NewFtpEvent;
import cn.zswltech.mithras.ftp.newftp.mapper.config.NewFtpMonthlyGuidanceTemplateConfigMapper;
import cn.zswltech.mithras.ftp.newftp.mapper.draft.NewFtpMonthlyGuidanceDraftMapper;
import cn.zswltech.mithras.ftp.newftp.model.NewFtpBaseInfo;
import cn.zswltech.mithras.ftp.newftp.model.config.NewFtpMonthlyGuidanceTemplateConfig;
import cn.zswltech.mithras.ftp.newftp.model.draft.NewFtpDescriptionTextDraft;
import cn.zswltech.mithras.ftp.newftp.model.draft.NewFtpMonthlyDeductionDraft;
import cn.zswltech.mithras.ftp.newftp.model.draft.NewFtpMonthlyGuidanceDraft;
import cn.zswltech.mithras.ftp.newftp.service.drift.NewFtpDescriptionTextDraftService;
import cn.zswltech.mithras.service.service.newftp.service.NewFtpBaseInfoService;
import cn.zswltech.mithras.service.util.FlowUtil;
import cn.zswltech.mithras.service.util.LongUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhaozhengkang
 * @description ftp报价表
 * @date 2023-05-21
 */
@Slf4j
@Service
public class NewFtpMonthlyGuidanceDraftService
        extends ServiceImpl<NewFtpMonthlyGuidanceDraftMapper, NewFtpMonthlyGuidanceDraft> {
    @Resource
    private NewFtpMonthlyGuidanceTemplateConfigMapper monthlyGuidanceTemplateConfigMapper;
    @Resource
    private NewFtpMonthlyGuidanceConfigConverter baseConverter;
    @Resource
    private NewFtpMonthlyDeductionDraftService deductionService;
    @Resource
    private DefaultNewFtpStateMachine defaultNewFtpStateMachine;
    @Resource
    private NewFtpBaseInfoService baseInfoService;

    public Map<String, String> getRowKeysMap(Long ftpId) {
        NewFtpBaseInfo newFtpBaseInfo = baseInfoService.getById(ftpId);
        FtpBusinessVersion ftpBusinessVersion = FtpBusinessVersion.getByName(newFtpBaseInfo.getFtpBusinessVersion());
        if (Objects.isNull(ftpBusinessVersion)) {
            log.error("FTP业务定价版本为空[{}]", ftpId);
            throw new MithrasException("FTP业务定价版本为空");
        }
        Map<String, String> rowKeysMap = new HashMap<>(32);
        // 前十一行一致，统一处理
        rowKeysMap.put("row1", CommonConvert.appendFtpGuidanceKey(RiskIndustryClassify.INDUSTRY, AssetIndustryClassify.ENCOURAGE_INTERVENTION, RegionalClassify.ZHEJIANG));
        rowKeysMap.put("row2", CommonConvert.appendFtpGuidanceKey(RiskIndustryClassify.INDUSTRY, AssetIndustryClassify.ENCOURAGE_INTERVENTION, RegionalClassify.ENCOURAGE));
        rowKeysMap.put("row3", CommonConvert.appendFtpGuidanceKey(RiskIndustryClassify.INDUSTRY, AssetIndustryClassify.ENCOURAGE_INTERVENTION, RegionalClassify.OTHER));
        rowKeysMap.put("row4", CommonConvert.appendFtpGuidanceKey(RiskIndustryClassify.INDUSTRY, AssetIndustryClassify.MODERATE_SUPPORT, RegionalClassify.ZHEJIANG));
        rowKeysMap.put("row5", CommonConvert.appendFtpGuidanceKey(RiskIndustryClassify.INDUSTRY, AssetIndustryClassify.MODERATE_SUPPORT, RegionalClassify.ENCOURAGE));
        rowKeysMap.put("row6", CommonConvert.appendFtpGuidanceKey(RiskIndustryClassify.INDUSTRY, AssetIndustryClassify.MODERATE_SUPPORT, RegionalClassify.OTHER));
        rowKeysMap.put("row7", CommonConvert.appendFtpGuidanceKey(RiskIndustryClassify.INDUSTRY, AssetIndustryClassify.CAUTIOUS_SUPPORT, RegionalClassify.ZHEJIANG));
        rowKeysMap.put("row8", CommonConvert.appendFtpGuidanceKey(RiskIndustryClassify.INDUSTRY, AssetIndustryClassify.CAUTIOUS_SUPPORT, RegionalClassify.ENCOURAGE));
        rowKeysMap.put("row9", CommonConvert.appendFtpGuidanceKey(RiskIndustryClassify.INDUSTRY, AssetIndustryClassify.CAUTIOUS_SUPPORT, RegionalClassify.OTHER));
        switch (ftpBusinessVersion) {
            case V2: {
                rowKeysMap.put("row10", CommonConvert.appendFtpGuidanceKey(RiskIndustryClassify.PUBLIC_UTILITY, null, RegionalClassify.ZHEJIANG));
                rowKeysMap.put("row11", CommonConvert.appendFtpGuidanceKey(RiskIndustryClassify.PUBLIC_UTILITY, null, RegionalClassify.ENCOURAGE));
                rowKeysMap.put("row12", CommonConvert.appendFtpGuidanceKey(RiskIndustryClassify.PUBLIC_UTILITY, null, RegionalClassify.OTHER));
                // 重要！！！第十三行（工程机械类（厂商担保模式））拿掉了
                rowKeysMap.put("row14", CommonConvert.appendFtpGuidanceKey(RiskIndustryClassify.COLLABORATIVE_LEASING_BUSINESS, null, null));
                rowKeysMap.put("row15", CommonConvert.appendFtpGuidanceKey(RiskIndustryClassify.COLLABORATIVE_FACTORING_BUSINESS, null, null));
                return rowKeysMap;
            }
            case V3: {
                // 重要！！！第十行（工程机械类（厂商担保模式））拿掉了
                rowKeysMap.put("row11", CommonConvert.appendFtpGuidanceKey(RiskIndustryClassify.COLLABORATIVE_LEASING_BUSINESS, null, null));
                rowKeysMap.put("row12", CommonConvert.appendFtpGuidanceKey(RiskIndustryClassify.COLLABORATIVE_FACTORING_BUSINESS, null, null));
                rowKeysMap.put("row13", CommonConvert.appendFtpGuidanceKey(RiskIndustryClassify.PUBLIC_UTILITY, null, RegionalClassify.ZHEJIANG));
                rowKeysMap.put("row14", CommonConvert.appendFtpGuidanceKey(RiskIndustryClassify.PUBLIC_UTILITY, null, RegionalClassify.ENCOURAGE));
                rowKeysMap.put("row15", CommonConvert.appendFtpGuidanceKey(RiskIndustryClassify.PUBLIC_UTILITY, null, RegionalClassify.OTHER));
                rowKeysMap.put("row16", CommonConvert.appendFtpGuidanceKey(RiskIndustryClassify.CIVIL_CONSUMPTION, null, RegionalClassify.ZHEJIANG));
                rowKeysMap.put("row17", CommonConvert.appendFtpGuidanceKey(RiskIndustryClassify.CIVIL_CONSUMPTION, null, RegionalClassify.ENCOURAGE));
                rowKeysMap.put("row18", CommonConvert.appendFtpGuidanceKey(RiskIndustryClassify.CIVIL_CONSUMPTION, null, RegionalClassify.OTHER));
                rowKeysMap.put("row19", CommonConvert.appendFtpGuidanceKey(RiskIndustryClassify.STATE_OWNED_INDUSTRY, null, RegionalClassify.ZHEJIANG));
                rowKeysMap.put("row20", CommonConvert.appendFtpGuidanceKey(RiskIndustryClassify.STATE_OWNED_INDUSTRY, null, RegionalClassify.ENCOURAGE));
                rowKeysMap.put("row21", CommonConvert.appendFtpGuidanceKey(RiskIndustryClassify.STATE_OWNED_INDUSTRY, null, RegionalClassify.OTHER));
                return rowKeysMap;
            }
            default: {
                throw new MithrasException("未定义的FTP业务定价版本");
            }
        }
    }

    public void add(NewFtpBaseInfo baseInfo) {
        List<NewFtpMonthlyGuidanceDraft> toBeInsert = new ArrayList<>();
        monthlyGuidanceTemplateConfigMapper
                .selectList(Wrappers.<NewFtpMonthlyGuidanceTemplateConfig>lambdaQuery().eq(NewFtpMonthlyGuidanceTemplateConfig::getFtpBusinessVersion, baseInfo.getFtpBusinessVersion()))
                .forEach(template -> {
            NewFtpMonthlyGuidanceDraft info = baseConverter.template2Entity(template);
            info.setFtpId(baseInfo.getId());
            info.setCreateTime(null);
            info.setUpdateTime(null);
            info.setCreateBy(null);
            info.setUpdateBy(null);
            toBeInsert.add(info);
        });
        SpringContextHolder.getBean(NewFtpMonthlyGuidanceDraftService.class).saveBatch(toBeInsert);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(NewFtpMonthlyGuidanceModifyREQ req) {
        NewFtpMonthlyGuidanceDraft originalInfo = baseMapper.selectById(req.getId());
        if (ObjectUtil.isNull(originalInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }

        ProcessResp relatedProcess = baseInfoService.findRelatedProcess(originalInfo.getFtpId());
        if (ObjectUtil.isNotEmpty(relatedProcess)) {
            boolean isStartUserNode = FlowUtil.isStartUserNode(relatedProcess);
            if (!isStartUserNode) {
                throw new AuthCheckException("该数据处于流程中，且流程不在发起人节点，不允许修改数据");
            }
        }
        NewFtpMonthlyGuidanceDraft info = new NewFtpMonthlyGuidanceDraft();
        info.setId(req.getId());
        info.setValue(req.getValue());
        baseMapper.updateById(info);

        NewFtpBaseInfo baseInfo = baseInfoService.getById(originalInfo.getFtpId());
        defaultNewFtpStateMachine.execute(NewFtpContext.of(baseInfo, NewFtpEvent.MODIFY_SAVE, baseInfo.getProcessStatus()));
    }

    public NewFtpMonthlyGuidanceDetaiRsp detail(Long mainId) {
        List<NewFtpMonthlyGuidanceDraft> all = getByMainId(mainId);
        Map<String, List<NewFtpMonthlyGuidanceDraft>> rows = all.stream()
                .collect(Collectors.groupingBy(guidance -> String.join(":",
                        guidance.getRiskIndustryClassify(),
                        StringUtil.null2Space(guidance.getAssetIndustryClassify()),
                        StringUtil.null2Space(guidance.getRegionalClassify()))));
        NewFtpMonthlyGuidanceDetaiRsp rsp = new NewFtpMonthlyGuidanceDetaiRsp();
        this.getRowKeysMap(mainId).forEach((field, key) -> {
            List<FtpValue> rowData = rows.get(key).stream()
                    .sorted(Comparator.comparing(NewFtpMonthlyGuidanceDraft::getTemplateId))
                    .map(mg -> new FtpValue().setId(mg.getId()).setValue(mg.getValue()))
                    .collect(Collectors.toList());
            ReflectUtil.setFieldValue(rsp, field, rowData);
        });
        return rsp;
    }

    public List<NewFtpMonthlyGuidanceDraft> getByMainId(Long mainId) {
        return baseMapper.selectList(
                Wrappers.<NewFtpMonthlyGuidanceDraft>lambdaQuery()
                        .eq(NewFtpMonthlyGuidanceDraft::getFtpId, mainId));
    }

    public void calculate(Long mainId) {
        NewFtpBaseInfo baseInfo = baseInfoService.getById(mainId);
        if (Objects.isNull(baseInfo)) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        FtpBusinessVersion ftpBusinessVersion = FtpBusinessVersion.getByName(baseInfo.getFtpBusinessVersion());
        if (Objects.isNull(ftpBusinessVersion)) {
            throw new MithrasException("无法确认当前FTP业务定价的业务版本");
        }
        switch (ftpBusinessVersion) {
            case V2: {
                // 老逻辑先抽方法保留吧，理论上新逻辑上线后不应该有场景会走进这个计算方法
                this.doCalculateV2(mainId);
                break;
            }
            case V3: {
                this.doCalculateV3(mainId);
                break;
            }
            default: {
                throw new MithrasException("未定义的FTP业务定价版本");
            }
        }
    }

    private void doCalculateV3(Long mainId) {
        Map<String, NewFtpMonthlyDeductionDraft> deductionMap = deductionService.list(
                        Wrappers.<NewFtpMonthlyDeductionDraft>lambdaQuery()
                                .eq(NewFtpMonthlyDeductionDraft::getFtpId, mainId)).stream()
                .collect(Collectors.toMap(NewFtpMonthlyDeductionDraft::getTermRange, Function.identity(), (k1, k2) -> k2));
        // 取模板
        List<String> riskIndustryClassifyList = ListUtil.of(
                RiskIndustryClassify.INDUSTRY.name(),
                RiskIndustryClassify.PUBLIC_UTILITY.name(),
                RiskIndustryClassify.CIVIL_CONSUMPTION.name(),
                RiskIndustryClassify.STATE_OWNED_INDUSTRY.name()
        );
        List<NewFtpMonthlyGuidanceDraft> toBeInsert = SpringContextHolder.getBean(NewFtpMonthlyGuidanceDraftService.class).list(
                Wrappers.<NewFtpMonthlyGuidanceDraft>lambdaQuery()
                        .eq(NewFtpMonthlyGuidanceDraft::getFtpId, mainId)
                        .in(NewFtpMonthlyGuidanceDraft::getRiskIndustryClassify, riskIndustryClassifyList));
        // 其他产业类
        toBeInsert.stream().filter(mg -> RiskIndustryClassify.INDUSTRY.name().equals(mg.getRiskIndustryClassify()))
                .forEach(mg -> {
                    NewFtpMonthlyDeductionDraft deductionDraft = deductionMap.get(mg.getTermRange());
                    Integer integerValue = getIntegerValue(deductionDraft, mg);
                    int baseValue = LongUtil.null2zero(deductionDraft.getSubtotalRate()) + LongUtil.null2zero(deductionDraft.getSubtotalCost());
                    mg.setValue(integerValue + baseValue);
                });
        // 公共事业类、民生消费类、国有产业类
        MonthlyDeductionDataExtractor extractor = new MonthlyDeductionDataExtractor(deductionMap);
        toBeInsert.stream().filter(mg -> StrUtil.equalsAny(mg.getRiskIndustryClassify(), RiskIndustryClassify.PUBLIC_UTILITY.name(), RiskIndustryClassify.CIVIL_CONSUMPTION.name(), RiskIndustryClassify.STATE_OWNED_INDUSTRY.name()))
                .forEach(mg -> {
                    NewFtpMonthlyDeductionDraft deductionDraft = deductionMap.get(mg.getTermRange());
                    Integer integerValue;
                    if (StrUtil.equals(mg.getRiskIndustryClassify(), RiskIndustryClassify.PUBLIC_UTILITY.name())) {
                        integerValue = deductionDraft.getAssetPublic();
                    } else if (StrUtil.equals(mg.getRiskIndustryClassify(), RiskIndustryClassify.CIVIL_CONSUMPTION.name())) {
                        integerValue = deductionDraft.getAssetCivil();
                    } else if (StrUtil.equals(mg.getRiskIndustryClassify(), RiskIndustryClassify.STATE_OWNED_INDUSTRY.name())) {
                        integerValue = deductionDraft.getAssetStateOwned();
                    } else {
                        throw new MithrasException("未定义的行业分类");
                    }
                    Integer subtotalCost = extractor.getSubtotalCost(mg.getTermRange());
                    Integer subtotalRate = extractor.getSubtotalRate(mg.getTermRange());
                    Integer regionalClassifyPricing = extractor.getRegionalClassifyPricing(mg.getTermRange(),
                            mg.getRiskIndustryClassify(), mg.getRegionalClassify());
                    mg.setValue(subtotalCost + subtotalRate + regionalClassifyPricing);
                    if (Objects.nonNull(integerValue)) {
                        mg.setValue(mg.getValue() + integerValue);
                    }
                });
        SpringContextHolder.getBean(NewFtpMonthlyGuidanceDraftService.class).saveOrUpdateBatch(toBeInsert);
        // 协同类租赁业务取最优
        List<NewFtpMonthlyGuidanceDraft> leaseBusinessPricingList = list(
                Wrappers.<NewFtpMonthlyGuidanceDraft>lambdaQuery()
                        .eq(NewFtpMonthlyGuidanceDraft::getFtpId, mainId)
                        .eq(NewFtpMonthlyGuidanceDraft::getRiskIndustryClassify, RiskIndustryClassify.COLLABORATIVE_LEASING_BUSINESS.name()));

        Map<String, NewFtpMonthlyGuidanceDraft> source = list(Wrappers.<NewFtpMonthlyGuidanceDraft>lambdaQuery()
                .eq(NewFtpMonthlyGuidanceDraft::getFtpId, mainId)
                .eq(NewFtpMonthlyGuidanceDraft::getRiskIndustryClassify, RiskIndustryClassify.INDUSTRY.name())
                .eq(NewFtpMonthlyGuidanceDraft::getAssetIndustryClassify, AssetIndustryClassify.ENCOURAGE_INTERVENTION.name())
                .eq(NewFtpMonthlyGuidanceDraft::getCustomerEntityClassify, CustomerEntityClassify.CUSTOMER_LISTED_STATE_OWNED.name())
                .eq(NewFtpMonthlyGuidanceDraft::getRegionalClassify, RegionalClassify.ZHEJIANG.name()))
                .stream().collect(Collectors.toMap(NewFtpMonthlyGuidanceDraft::getTermRange, v -> v, (k1, k2) -> k2));
        // 在对应年限的最低ftp的基础上减少0.2%
        leaseBusinessPricingList.forEach(guidance -> guidance.setValue(source.get(guidance.getTermRange()).getValue() - 2000));
        SpringContextHolder.getBean(NewFtpMonthlyGuidanceDraftService.class).updateBatchById(leaseBusinessPricingList);
    }

    private void doCalculateV2(Long mainId) {
        Map<String, NewFtpMonthlyDeductionDraft> deductionMap = deductionService.list(
                        Wrappers.<NewFtpMonthlyDeductionDraft>lambdaQuery()
                                .eq(NewFtpMonthlyDeductionDraft::getFtpId, mainId)).stream()
                .collect(Collectors.toMap(NewFtpMonthlyDeductionDraft::getTermRange, Function.identity(), (k1, k2) -> k2));
        //拿到模版
        List<NewFtpMonthlyGuidanceDraft> toBeInsert = SpringContextHolder.getBean(NewFtpMonthlyGuidanceDraftService.class).list(
                Wrappers.<NewFtpMonthlyGuidanceDraft>lambdaQuery()
                        .eq(NewFtpMonthlyGuidanceDraft::getFtpId, mainId)
                        .in(NewFtpMonthlyGuidanceDraft::getRiskIndustryClassify, RiskIndustryClassify.INDUSTRY.name(), RiskIndustryClassify.PUBLIC_UTILITY.name()));
        toBeInsert.stream().filter(mg -> RiskIndustryClassify.INDUSTRY.name().equals(mg.getRiskIndustryClassify()))
                .forEach(mg -> {
                    NewFtpMonthlyDeductionDraft deductionDraft = deductionMap.get(mg.getTermRange());
                    Integer integerValue = getIntegerValue(deductionDraft, mg);
                    int baseValue = LongUtil.null2zero(deductionDraft.getSubtotalRate()) + LongUtil.null2zero(deductionDraft.getSubtotalCost());
                    mg.setValue(integerValue + baseValue);
                });
        Map<String, Map<String, Map<String, Map<String, Integer>>>> firstMap = new HashMap<>();
        toBeInsert.stream().filter(mg -> RegionalClassify.ZHEJIANG.name().equals(mg.getRegionalClassify()))
                .filter(mg -> RiskIndustryClassify.INDUSTRY.name().equals(mg.getRiskIndustryClassify()))
                .collect(Collectors.groupingBy(NewFtpMonthlyGuidanceDraft::getAssetIndustryClassify))
                .forEach((k, v) -> {
                    Map<String, Map<String, Map<String, Integer>>> secondMap = new HashMap<>();
                    v.stream().collect(Collectors.groupingBy(NewFtpMonthlyGuidanceDraft::getRegionalClassify))
                            .forEach((k1, v1) -> {
                                Map<String, Map<String, Integer>> thirdMap = new HashMap<>();
                                v1.stream().collect(Collectors.groupingBy(NewFtpMonthlyGuidanceDraft::getTermRange))
                                        .forEach((k2, v2) -> {
                                            Map<String, Integer> fourthMap = v2.stream().collect(Collectors.toMap(NewFtpMonthlyGuidanceDraft::getCustomerEntityClassify, NewFtpMonthlyGuidanceDraft::getValue, (k4, k5) -> k5));
                                            thirdMap.put(k2, fourthMap);
                                        });
                                secondMap.put(k1, thirdMap);
                            });
                    firstMap.put(k, secondMap);
                });
        // 因为公共事业分类没有资产行业分类，所以公共事业类需要单独处理
        MonthlyDeductionDataExtractor extractor = new MonthlyDeductionDataExtractor(deductionMap);
        toBeInsert.stream().filter(mg -> RiskIndustryClassify.PUBLIC_UTILITY.name().equals(mg.getRiskIndustryClassify()))
                .forEach(mg -> {
                    Integer subtotalCost = extractor.getSubtotalCost(mg.getTermRange());
                    Integer subtotalRate = extractor.getSubtotalRate(mg.getTermRange());
                    Integer regionalClassifyPricing = extractor.getRegionalClassifyPricing(mg.getTermRange(),
                            mg.getRiskIndustryClassify(), mg.getRegionalClassify());
                    mg.setValue(subtotalCost + subtotalRate + regionalClassifyPricing);
                });
        // 浙江地区国有企业FTP成本与上市公司同价
        for (NewFtpMonthlyGuidanceDraft mg : toBeInsert) {
            if (!RegionalClassify.ZHEJIANG.name().equals(mg.getRegionalClassify()) ||
                    !CustomerEntityClassify.STATE_OWNED_ENTERPRISE.name().equals(mg.getCustomerEntityClassify())) {
                continue;
            }
            Integer integer = firstMap.get(mg.getAssetIndustryClassify())
                    .get(mg.getRegionalClassify())
                    .get(mg.getTermRange())
                    .get(CustomerEntityClassify.LISTED_COMPANY.name());

            mg.setValue(integer);
        }
        SpringContextHolder.getBean(NewFtpMonthlyGuidanceDraftService.class).saveOrUpdateBatch(toBeInsert);
        NewFtpMonthlyGuidanceDraftService draftService = SpringContextHolder.getBean(NewFtpMonthlyGuidanceDraftService.class);

        //协同类租赁业务
        Map<String, NewFtpMonthlyGuidanceDraft> leaseBusinessPricing = list(
                Wrappers.<NewFtpMonthlyGuidanceDraft>lambdaQuery()
                        .eq(NewFtpMonthlyGuidanceDraft::getFtpId, mainId)
                        .eq(NewFtpMonthlyGuidanceDraft::getRiskIndustryClassify, RiskIndustryClassify.COLLABORATIVE_LEASING_BUSINESS.name())).stream()
                .collect(Collectors.toMap(NewFtpMonthlyGuidanceDraft::getTermRange, Function.identity(), (k1, k2) -> k2));

        Map<String, NewFtpMonthlyGuidanceDraft> source = list(Wrappers.<NewFtpMonthlyGuidanceDraft>lambdaQuery()
                .eq(NewFtpMonthlyGuidanceDraft::getFtpId, mainId)
                .eq(NewFtpMonthlyGuidanceDraft::getRiskIndustryClassify, RiskIndustryClassify.INDUSTRY.name())
                .eq(NewFtpMonthlyGuidanceDraft::getAssetIndustryClassify, AssetIndustryClassify.ENCOURAGE_INTERVENTION.name())
                .eq(NewFtpMonthlyGuidanceDraft::getCustomerEntityClassify, CustomerEntityClassify.LISTED_COMPANY.name())
                .eq(NewFtpMonthlyGuidanceDraft::getRegionalClassify, RegionalClassify.ZHEJIANG.name()))
                .stream().collect(Collectors.toMap(NewFtpMonthlyGuidanceDraft::getTermRange, v -> v, (k1, k2) -> k2));

        leaseBusinessPricing.forEach((termRange, guidance) -> guidance.setValue(source.get(termRange).getValue()));
        draftService.updateBatchById(leaseBusinessPricing.values());
    }

    private Integer getIntegerValue(NewFtpMonthlyDeductionDraft ductionDraft, NewFtpMonthlyGuidanceDraft dto) {
        AssetIndustryClassify assetIndustryClassify = AssetIndustryClassify.valueOf(dto.getAssetIndustryClassify());
        int assetIndustry = 0;
        switch (assetIndustryClassify) {
            case ENCOURAGE_INTERVENTION:
                assetIndustry = ductionDraft.getAssetEncourage();
                break;
            case CAUTIOUS_SUPPORT:
                assetIndustry = ductionDraft.getAssetCautious();
                break;
            case MODERATE_SUPPORT:
                assetIndustry = ductionDraft.getAssetModerate();
                break;
            default:
                break;
        }

        RegionalClassify regionalClassify = RegionalClassify.of(dto.getRegionalClassify());
        int regional = 0;
        switch (regionalClassify) {
            case ZHEJIANG:
                regional = ductionDraft.getIndustryRegionZhejiang();
                break;
            case OTHER:
                regional = ductionDraft.getIndustryRegionOther();
                break;
            case ENCOURAGE:
                regional = ductionDraft.getIndustryRegionEncourage();
                break;
            default:
                break;
        }

        EnterpriseTypeEnum enterpriseTypeEnum = EnterpriseTypeEnum.valueOf(dto.getCustomerEntityClassify());
        int customer = 0;
        switch (enterpriseTypeEnum) {
            case STATE_OWNED_ENTERPRISE:
                customer = ductionDraft.getCustomerStateOwned();
                break;
            case CUSTOMER_LISTED_STATE_OWNED:
                customer = ductionDraft.getCustomerListedStateOwned();
                break;
            case CUSTOMER_OTHER_LISTED:
                customer = ductionDraft.getCustomerOtherListed();
                break;
            case OTHER:
                customer = ductionDraft.getCustomerOther();
                break;
            case LISTED_COMPANY:
                customer = ductionDraft.getCustomerListed();
                break;
            default:
                break;
        }
        return regional + assetIndustry + customer;
    }

    /**
     * 获取产业类一个季度的定价，用于计算季度最低收益率
     *
     * @param months 月份
     * @return 用于计算的数据
     */
    public List<NewFtpQuarterPricingBO> quarterPricings(List<LocalDate> months) {
        // 改造，如果某个月的FTP没有则取当月最近的一个月
        LambdaQueryWrapper<NewFtpBaseInfo> query = Wrappers.lambdaQuery();
        query.eq(NewFtpBaseInfo::getFtpRecordStatus, RecordStatus.TAKE_EFFECT.name());
        List<NewFtpBaseInfo> ftpBaseInfoList = baseInfoService.list(query);
        if (CollectionUtil.isEmpty(ftpBaseInfoList)) {
            return Collections.emptyList();
        }
        ftpBaseInfoList.sort(Comparator.comparing(NewFtpBaseInfo::getMonth));
        Map<String, Long> date2IdMap = new HashMap<>();
        for (LocalDate targetDate : months) {
            NewFtpBaseInfo newFtpBaseInfo = this.findTheBestChoice(targetDate, ftpBaseInfoList);
            if (Objects.nonNull(newFtpBaseInfo)) {
                log.info("{}年{}月的FTP使用{}年{}月的数据", targetDate.getYear(), targetDate.getMonthValue(), newFtpBaseInfo.getMonth().getYear(), newFtpBaseInfo.getMonth().getMonthValue());
                date2IdMap.put(LocalDateTimeUtil.format(targetDate, DatePattern.NORM_DATE_PATTERN), newFtpBaseInfo.getId());
            } else {
                log.info("{}年{}月的FTP没有找到可使用的数据", targetDate.getYear(), targetDate.getMonthValue());
            }
        }
        if (CollectionUtil.isEmpty(date2IdMap)) {
            return Collections.emptyList();
        }
        // 查询月度指导
        LambdaQueryWrapper<NewFtpMonthlyGuidanceDraft> guidanceQuery = Wrappers.lambdaQuery();
        guidanceQuery.in(NewFtpMonthlyGuidanceDraft::getFtpId, date2IdMap.values());
        guidanceQuery.eq(NewFtpMonthlyGuidanceDraft::getRiskIndustryClassify, "INDUSTRY");
        List<NewFtpMonthlyGuidanceDraft> ftpMonthlyGuidanceList = this.list(guidanceQuery);
        if (CollectionUtil.isEmpty(ftpMonthlyGuidanceList)) {
            return Collections.emptyList();
        }
        Map<Long, List<NewFtpMonthlyGuidanceDraft>> map = ftpMonthlyGuidanceList.stream().collect(Collectors.groupingBy(NewFtpMonthlyGuidanceDraft::getFtpId));
        List<NewFtpQuarterPricingBO> result = new LinkedList<>();
        for (Map.Entry<String, Long> entry : date2IdMap.entrySet()) {
            NewFtpQuarterPricingBO bo = new NewFtpQuarterPricingBO();
            bo.setTargetDate(LocalDateTimeUtil.parseDate(entry.getKey(), DatePattern.NORM_DATE_PATTERN));
            bo.setFtpId(entry.getValue());
            bo.setFtpMonthlyGuidanceList(map.get(entry.getValue()));
            result.add(bo);
        }
        return result;
    }

    private NewFtpBaseInfo findTheBestChoice(LocalDate targetDate, List<NewFtpBaseInfo> candidateList) {
        // 从后往前找到第一个小于等于给定日期的FTP
        for (int i = candidateList.size() - 1; i >= 0; i--) {
            NewFtpBaseInfo item = candidateList.get(i);
            if (targetDate.isEqual(item.getMonth()) || targetDate.isAfter(item.getMonth())) {
                return item;
            }
        }
        return null;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void monthlyGuidanceAdd(Long mainId) {
        NewFtpBaseInfo baseInfo = baseInfoService.getById(mainId);
        if (ObjectUtil.isNull(baseInfo)) {
            throw new MithrasException("ftp不存在");
        }
        // 新增月度指导数据
        LocalDate month = baseInfo.getMonth();
        Long ftpId = baseInfo.getId();
        SpringContextHolder.getBean(NewFtpMonthlyGuidanceDraftService.class).add(baseInfo);

        //新增月度指导数据下半部分
        SpringContextHolder.getBean(NewFtpMonthlyGuidanceExtDraftService.class).add(month, ftpId);
        //新增ftp报价文本
        List<NewFtpDescriptionTextDraft> list = new LinkedList<>();

        NewFtpDescriptionTextDraft text = new NewFtpDescriptionTextDraft();
        text.setFtpId(ftpId);
        text.setDescType(DescriptionType.MONTHLY_GUIDANCE.name());
        text.setDescContent(DescriptionType.MONTHLY_GUIDANCE.getDesc());
        list.add(text);

        NewFtpDescriptionTextDraft text1 = new NewFtpDescriptionTextDraft();
        text1.setFtpId(mainId);
        text1.setDescType(DescriptionType.MONTHLY_SUPPLEMENT.name());
        text1.setDescContent("");
        list.add(text1);

        // 新增季度最低收益率
        if (PricingFrequencyEnum.QUARTER.name().equals(baseInfo.getPricingFrequency())) {
            //插入季度最低收益率表格
            SpringContextHolder.getBean(NewFtpQuarterlyBasePricingDraftService.class).add(ftpId);
            //插入季度最低收益率文本
            NewFtpDescriptionTextDraft text4 = new NewFtpDescriptionTextDraft();
            text4.setFtpId(mainId);
            text4.setDescType(DescriptionType.QUARTERLY_SUPPLEMENT.name());
            text4.setDescContent("");
            list.add(text4);

            NewFtpDescriptionTextDraft text5 = new NewFtpDescriptionTextDraft();
            text5.setFtpId(mainId);
            text5.setDescType(DescriptionType.QUARTERLY_PRICING.name());
            text5.setDescContent(DescriptionType.QUARTERLY_PRICING.getDesc());
            list.add(text5);
            // 插入集团控股公司季度最低收益率
            SpringUtil.getBean(NewFtpQuarterlyBasePricingExtDraftService.class).add(ftpId);
            // 插入集团控股公司季度最低收益率文本
            NewFtpDescriptionTextDraft text6 = new NewFtpDescriptionTextDraft();
            text6.setFtpId(mainId);
            text6.setDescType(DescriptionType.QUARTERLY_PRICING_EXT.name());
            text6.setDescContent(DescriptionType.QUARTERLY_PRICING_EXT.getDesc());
            list.add(text6);
        }
        SpringContextHolder.getBean(NewFtpDescriptionTextDraftService.class).saveBatch(list);
        if (!YesOrNoNumberEnum.YES.getCode().equals(baseInfo.getCalculateGuidanceFlag())) {
            NewFtpBaseInfo newFtpBaseInfo = new NewFtpBaseInfo();
            newFtpBaseInfo.setId(baseInfo.getId());
            newFtpBaseInfo.setCalculateGuidanceFlag(YesOrNoNumberEnum.YES.getCode());
            baseInfoService.updateById(newFtpBaseInfo);
        }
        SpringContextHolder.getBean(NewFtpMonthlyGuidanceDraftService.class).calculate(ftpId);
    }
}
