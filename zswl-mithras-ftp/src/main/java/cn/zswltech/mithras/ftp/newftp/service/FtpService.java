package cn.zswltech.mithras.ftp.newftp.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.foundation.constant.VersionTypeConstants;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.foundation.enums.common.RecordStatus;
import cn.zswltech.mithras.ftp.oldftp.enums.FtpBusinessVersion;
import cn.zswltech.mithras.projectprocess.enums.projpricing.RegionalClassify;
import cn.zswltech.mithras.customer.enums.client.CustomerEntityClassify;
import cn.zswltech.mithras.ftp.newftp.enums.*;
import cn.zswltech.mithras.projectprocess.enums.projpricing.FtpIndustryCategoryEnum;
import cn.zswltech.mithras.projectprocess.enums.projpricing.ProjectManageLevelEnum;
import cn.zswltech.mithras.projectprocess.enums.projreview.ProjectClassify;
import cn.zswltech.mithras.foundation.enums.common.RiskControlIndustryClassify;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.ftp.oldftp.bo.BillFtpBO;
import cn.zswltech.mithras.ftp.oldftp.bo.CashFtpInfluenceBO;
import cn.zswltech.mithras.ftp.oldftp.bo.FtpQuarterlyBasePricingBO;
import cn.zswltech.mithras.ftp.newftp.lib.impl.NewFtpQuarterlyBasePricingLibHandler;
import cn.zswltech.mithras.ftp.newftp.mapper.draft.NewFtpQuarterlyBasePricingDraftMapper;
import cn.zswltech.mithras.ftp.newftp.model.NewFtpBaseInfo;
import cn.zswltech.mithras.ftp.newftp.model.draft.NewFtpMonthlyGuidanceDraft;
import cn.zswltech.mithras.ftp.newftp.model.draft.NewFtpQuarterlyBasePricingDraft;
import cn.zswltech.mithras.ftp.newftp.model.draft.NewFtpQuarterlyBasePricingExtDraft;
import cn.zswltech.mithras.ftp.newftp.model.lib.NewFtpMonthlyGuidanceLib;
import cn.zswltech.mithras.ftp.newftp.model.lib.NewFtpQuarterlyBasePricingLib;
import cn.zswltech.mithras.ftp.newftp.service.draft.NewFtpQuarterlyBasePricingExtDraftService;
import cn.zswltech.mithras.ftp.newftp.service.lib.NewFtpMonthlyGuidanceLibService;
import cn.zswltech.mithras.ftp.newftp.service.port.FtpPricingContextPort;
import cn.zswltech.mithras.ftp.newftp.service.port.NewFtpCustomerFactPort;
import cn.zswltech.mithras.ftp.newftp.service.port.NewFtpFundDataPort;
import cn.zswltech.mithras.foundation.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2023/5/17
 * @description
 */
@Slf4j
@Service
public class FtpService {
    @Resource
    private NewFtpBaseInfoService newFtpBaseInfoService;
    @Resource
    private NewFtpMonthlyGuidanceLibService newFtpMonthlyGuidanceLibService;
    @Resource
    private NewFtpQuarterlyBasePricingDraftMapper newFtpQuarterlyBasePricingDraftMapper;
    @Resource
    private NewFtpQuarterlyBasePricingLibHandler newFtpQuarterlyBasePricingLibHandler;
    @Resource
    private FtpPricingContextPort ftpPricingContextPort;
    @Resource
    private FtpEffectiveGuidanceQueryService ftpEffectiveGuidanceQueryService;
    @Resource
    private BillFtpPricingService billFtpPricingService;
    @Resource
    private NewFtpFundDataPort newFtpFundDataPort;
    @Resource
    private NewFtpCustomerFactPort newFtpCustomerFactPort;
    @Resource
    private NewFtpQuarterlyBasePricingExtDraftService newFtpQuarterlyBasePricingExtDraftService;


    public BillFtpBO getBillFtp(LocalDate targetDate) {
        return billFtpPricingService.getBillFtp(targetDate);
    }

    public Integer getCashFtp(CashFtpInfluenceBO cashFtpInfluenceBO, Set<Long> contractIds) {
        NewFtpBaseInfo newFtpBaseInfo = ftpEffectiveGuidanceQueryService.getEffectiveMonthlyOrThrow(cashFtpInfluenceBO.getTargetDate());
        if (Objects.equals(newFtpBaseInfo.getFtpBusinessVersion(), FtpBusinessVersion.V2.name())) {
            // 使用老方法兼容老数据
            return this.getCashFtpDeprecated(newFtpBaseInfo, cashFtpInfluenceBO, contractIds);
        }
        // 期限范围分类
        TermRange termRange;
        int termYear = new BigDecimal(cashFtpInfluenceBO.getContractMonthCount()).divide(new BigDecimal(12), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).intValue();
        if (termYear <= 100) {
            termRange = TermRange.ONE_YEAR;
        } else if (termYear <= 300) {
            termRange = TermRange.ONE_TO_THREE_YEARS;
        } else {
            termRange = TermRange.MORE_THAN_THREE_YEARS;
        }
        // 评估主体的“是否关联方” = 是的话直接取协同类租赁业务
        if (Objects.nonNull(cashFtpInfluenceBO.getEvaluationSubjectId())) {
            if (newFtpCustomerFactPort.isRelatedClient(cashFtpInfluenceBO.getEvaluationSubjectId())) {
                NewFtpMonthlyGuidanceLib newFtpMonthlyGuidanceLib = newFtpMonthlyGuidanceLibService.getOne(
                        Wrappers.<NewFtpMonthlyGuidanceLib>lambdaQuery()
                                .eq(NewFtpMonthlyGuidanceDraft::getFtpId, newFtpBaseInfo.getId())
                                .eq(NewFtpMonthlyGuidanceDraft::getRiskIndustryClassify, RiskIndustryClassify.COLLABORATIVE_LEASING_BUSINESS.name())
                                .eq(NewFtpMonthlyGuidanceDraft::getTermRange, termRange)
                                .eq(NewFtpMonthlyGuidanceLib::getVersion, newFtpBaseInfo.getNewestVersion())
                                .last(StringUtil.mysqlLimitOne())
                );
                return Optional.ofNullable(newFtpMonthlyGuidanceLib).map(NewFtpMonthlyGuidanceDraft::getValue).orElse(0);
            }
        }
        FtpIndustryCategoryEnum ftpIndustryCategoryEnum = FtpIndustryCategoryEnum.getByName(cashFtpInfluenceBO.getFtpIndustryCategory());
        ProjectManageLevelEnum projectManageLevelEnum = ProjectManageLevelEnum.getByName(cashFtpInfluenceBO.getProjectManageLevel());
        // 风控行业分类
        RiskIndustryClassify riskIndustryClassify = RiskIndustryClassify.getByFtpIndustryCategory(ftpIndustryCategoryEnum);
        // 资产行业分类
        AssetIndustryClassify assetIndustryClassify = AssetIndustryClassify.getByProjectClassify(ProjectClassify.find(cashFtpInfluenceBO.getAssetIndustryClassify()));
        // 客户主体分类
        CustomerEntityClassify customerEntityClassify = CustomerEntityClassify.valueOf(newFtpCustomerFactPort.getCustomerEntityClassify(
                cashFtpInfluenceBO.getTenantId(), cashFtpInfluenceBO.getGuarantorIdList(), newFtpBaseInfo.getFtpBusinessVersion()));
        // 地区分类
        RegionalClassify regionalClassify;
        if (ftpIndustryCategoryEnum == FtpIndustryCategoryEnum.FTP_OTHER_INDUSTRY) {
            // 取项目定价的地区分类
            regionalClassify = RegionalClassify.getProjRegionalClassify(cashFtpInfluenceBO.getRegionClassify());
        } else {
            // 取项目定价的区域划分
            regionalClassify = RegionalClassify.getProjRegionDivision(cashFtpInfluenceBO.getRegionalDivision());
        }
        log.info("（新逻辑）查询FTP条件[ftpId: {}, riskIndustryClassify:{}, termRange:{}, customerEntityClassify:{}, assetIndustryClassify:{}, regionalClassify:{}]",
                newFtpBaseInfo.getId(),
                Optional.ofNullable(riskIndustryClassify).map(Enum::name).orElse("null"),
                termRange,
                Optional.ofNullable(customerEntityClassify).map(Enum::name).orElse("null"),
                Optional.ofNullable(assetIndustryClassify).map(Enum::name).orElse("null"),
                Optional.ofNullable(regionalClassify).map(Enum::name).orElse("null")
                );
        // 查询数据
        LambdaQueryWrapper<NewFtpMonthlyGuidanceLib> query = Wrappers.lambdaQuery();
        query.eq(NewFtpMonthlyGuidanceDraft::getFtpId, newFtpBaseInfo.getId());
        query.eq(NewFtpMonthlyGuidanceLib::getVersionType, VersionTypeConstants.NORMAL);
        query.eq(NewFtpMonthlyGuidanceDraft::getRiskIndustryClassify, Optional.ofNullable(riskIndustryClassify).map(Enum::name).orElse(""));
        if (Objects.nonNull(riskIndustryClassify) && Objects.equals(riskIndustryClassify.name(), RiskIndustryClassify.INDUSTRY.name())) {
            query.eq(NewFtpMonthlyGuidanceDraft::getAssetIndustryClassify, Optional.ofNullable(assetIndustryClassify).map(Enum::name).orElse(""));
        }
        query.eq(NewFtpMonthlyGuidanceDraft::getRegionalClassify, Optional.ofNullable(regionalClassify).map(Enum::name).orElse(""));
        query.eq(NewFtpMonthlyGuidanceDraft::getTermRange, termRange.name());
        if (ftpIndustryCategoryEnum == FtpIndustryCategoryEnum.FTP_OTHER_INDUSTRY) {
            query.eq(NewFtpMonthlyGuidanceDraft::getCustomerEntityClassify, Optional.ofNullable(customerEntityClassify).map(Enum::name).orElse(""));
        }
        query.orderByDesc(NewFtpMonthlyGuidanceDraft::getId);
        query.last(StringUtil.mysqlLimitOne());
        NewFtpMonthlyGuidanceLib targetOne = newFtpMonthlyGuidanceLibService.getOne(query);
        log.info("借据ID为{}的借据查询FTP结果:{}", cashFtpInfluenceBO.getReceiptId(), JSONUtil.toJsonStr(targetOne));
        if (Objects.isNull(targetOne) || Objects.isNull(targetOne.getValue())) {
            return null;
        }
        // ftp行业分类=公用事业类或民生消费类， 且项目管理层级=市级或是否AAA评级=是，则ftp基础价格在现有取值逻辑结果上扣减 0.2%
        // ftp行业分类=公用事业类或民生消费类， 且项目管理层级=镇级，则ftp基础价格在现有取值逻辑结果上增加 1%
        int basePrice = targetOne.getValue();
        if (StrUtil.equalsAny(cashFtpInfluenceBO.getFtpIndustryCategory(), FtpIndustryCategoryEnum.FTP_PUBLIC_UTILITIES.name(), FtpIndustryCategoryEnum.FTP_CIVIL_CONSUMPTION.name())
                && (projectManageLevelEnum == ProjectManageLevelEnum.CITY || YesOrNoNumberEnum.YES.getCode().equals(cashFtpInfluenceBO.getIsAAA()))) {
            basePrice = basePrice - 2000;
        }
        if (StrUtil.equalsAny(cashFtpInfluenceBO.getFtpIndustryCategory(), FtpIndustryCategoryEnum.FTP_PUBLIC_UTILITIES.name(), FtpIndustryCategoryEnum.FTP_CIVIL_CONSUMPTION.name())
                && projectManageLevelEnum == ProjectManageLevelEnum.TOWN) {
            basePrice = basePrice + 10000;
        }
        return basePrice;
    }

    // 方法逻辑过时，使用getCashFtp替代
    @Deprecated
    public Integer getCashFtpDeprecated(NewFtpBaseInfo effectOne, CashFtpInfluenceBO cashFtpInfluenceBO, Set<Long> contractIds) {
        //转换地区分类
        if (ObjectUtil.isNotEmpty(cashFtpInfluenceBO.getRegionClassify())) {
            cashFtpInfluenceBO.setRegionClassify(RegionalClassify.getProjRegionalClassify(cashFtpInfluenceBO.getRegionClassify()).name());
        }
//        EnterpriseNatureEnum enterpriseNatureEnum = EnterpriseNatureEnum.of(cashFtpInfluenceBO.getEnterpriseNature());
//        CustomerEntityClassify customerEntityClassify = null;
//        if (enterpriseNatureEnum != null) {
//            switch (enterpriseNatureEnum) {
//                case gyss:
//                case myss:
//                    customerEntityClassify = CustomerEntityClassify.LISTED_COMPANY;
//                    break;
//                case gyfss:
//                    customerEntityClassify = CustomerEntityClassify.STATE_OWNED_ENTERPRISE;
//                    break;
//                case myfss:
//                case other:
//                    customerEntityClassify = CustomerEntityClassify.OTHER;
//                    break;
//                default:
//                    break;
//            }
//        }

        CustomerEntityClassify customerEntityClassify = CustomerEntityClassify.valueOf(newFtpCustomerFactPort.getCustomerEntityClassify(
                cashFtpInfluenceBO.getTenantId(), cashFtpInfluenceBO.getGuarantorIdList(), effectOne.getFtpBusinessVersion()));
        RegionalClassify regionalClassify = null;
        if (cashFtpInfluenceBO.getZhejiang()) {
            regionalClassify = RegionalClassify.ZHEJIANG;
        } else {
            regionalClassify = RegionalClassify.of(cashFtpInfluenceBO.getRegionClassify());
        }
        AssetIndustryClassify assetIndustryClassify = null;
        ProjectClassify projectClassify = ProjectClassify.of(cashFtpInfluenceBO.getAssetIndustryClassify());
        if (projectClassify != null) {
            switch (projectClassify) {
                case ENCOURAGEMENT:
                    assetIndustryClassify = AssetIndustryClassify.ENCOURAGE_INTERVENTION;
                    break;
                case MODERATE_SUPPORT:
                    assetIndustryClassify = AssetIndustryClassify.MODERATE_SUPPORT;
                    break;
                case CAUTIOUS:
                    assetIndustryClassify = AssetIndustryClassify.CAUTIOUS_SUPPORT;
                    break;
                default:
                    break;
            }
        }

        RiskIndustryClassify riskIndustryClassify = null;
        RiskControlIndustryClassify riskControlIndustryClassify =
                RiskControlIndustryClassify.of(cashFtpInfluenceBO.getRiskControlIndustryClassify());
        if (riskControlIndustryClassify != null) {
            switch (riskControlIndustryClassify) {
                case INTRA_GROUP_COLLABORATION:
                    if ("BL".equals(cashFtpInfluenceBO.getBizType())) {
                        riskIndustryClassify = RiskIndustryClassify.COLLABORATIVE_FACTORING_BUSINESS;
                    } else {
                        riskIndustryClassify = RiskIndustryClassify.COLLABORATIVE_LEASING_BUSINESS;
                    }
                    break;
                case PUBLIC_UTILITIES:
                case CIVIL_CONSUMPTION:
                case TRAVEL:
                    riskIndustryClassify = RiskIndustryClassify.PUBLIC_UTILITY;
                    break;
//                case ENGINEERING_MACHINERY:
//                    riskIndustryClassify = RiskIndustryClassify.ENGINEERING_MACHINERY;
//                    break;
                default:
                    riskIndustryClassify = RiskIndustryClassify.INDUSTRY;
                    break;
            }
        } else {
            throw new MithrasException("风控行业分类为空");
        }
        TermRange termRange = null;
        int termYear = new BigDecimal(cashFtpInfluenceBO.getContractMonthCount()).divide(new BigDecimal(12), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).intValue();
        if (termYear <= 100) {
            termRange = TermRange.ONE_YEAR;
        } else if (termYear <= 300) {
            termRange = TermRange.ONE_TO_THREE_YEARS;
        } else {
            termRange = TermRange.MORE_THAN_THREE_YEARS;
        }
        log.info("（老逻辑）查询FTP条件[ftpId: {}, riskIndustryClassify:{}, termRange:{}, customerEntityClassify:{}, assetIndustryClassify:{}, regionalClassify:{}]", effectOne.getId(), riskIndustryClassify, termRange, customerEntityClassify, assetIndustryClassify, regionalClassify);
        NewFtpMonthlyGuidanceLib one = null;
        switch (riskIndustryClassify) {
            case INDUSTRY:
                one = newFtpMonthlyGuidanceLibService.getOne(Wrappers.<NewFtpMonthlyGuidanceLib>lambdaQuery()
                        .eq(NewFtpMonthlyGuidanceLib::getVersion, effectOne.getNewestVersion())
                        .eq(NewFtpMonthlyGuidanceDraft::getFtpId, effectOne.getId())
                        .eq(NewFtpMonthlyGuidanceDraft::getRiskIndustryClassify, riskIndustryClassify)
                        .eq(NewFtpMonthlyGuidanceDraft::getTermRange, termRange)
                        .eq(NewFtpMonthlyGuidanceDraft::getCustomerEntityClassify, customerEntityClassify)
                        .eq(NewFtpMonthlyGuidanceDraft::getAssetIndustryClassify, assetIndustryClassify)
                        .eq(NewFtpMonthlyGuidanceDraft::getRegionalClassify, regionalClassify).last("limit 1"));
                break;
            case PUBLIC_UTILITY:
                one = newFtpMonthlyGuidanceLibService.getOne(Wrappers.<NewFtpMonthlyGuidanceLib>lambdaQuery()
                        .eq(NewFtpMonthlyGuidanceLib::getVersion, effectOne.getNewestVersion())
                        .eq(NewFtpMonthlyGuidanceDraft::getFtpId, effectOne.getId())
                        .eq(NewFtpMonthlyGuidanceDraft::getRiskIndustryClassify, riskIndustryClassify)
                        .eq(NewFtpMonthlyGuidanceDraft::getTermRange, termRange)
                        .eq(NewFtpMonthlyGuidanceDraft::getRegionalClassify, regionalClassify).last("limit 1"));
                break;
//            case ENGINEERING_MACHINERY:
            case COLLABORATIVE_LEASING_BUSINESS:
                one = newFtpMonthlyGuidanceLibService.getOne(Wrappers.<NewFtpMonthlyGuidanceLib>lambdaQuery()
                        .eq(NewFtpMonthlyGuidanceLib::getVersion, effectOne.getNewestVersion())
                        .eq(NewFtpMonthlyGuidanceDraft::getFtpId, effectOne.getId())
                        .eq(NewFtpMonthlyGuidanceDraft::getRiskIndustryClassify, riskIndustryClassify)
                        .eq(NewFtpMonthlyGuidanceDraft::getTermRange, termRange)
                        .last("limit 1"));
                break;
            case COLLABORATIVE_FACTORING_BUSINESS:
                one = newFtpMonthlyGuidanceLibService.getOne(Wrappers.<NewFtpMonthlyGuidanceLib>lambdaQuery()
                        .eq(NewFtpMonthlyGuidanceLib::getVersion, effectOne.getNewestVersion())
                        .eq(NewFtpMonthlyGuidanceDraft::getFtpId, effectOne.getId())
                        .eq(NewFtpMonthlyGuidanceDraft::getRiskIndustryClassify, riskIndustryClassify)
                        .last("limit 1"));
                break;
            default:
                break;
        }
        Integer handledPledge = handlePledge(cashFtpInfluenceBO, contractIds);
        return one == null ? null : one.getValue() + handledPledge;
    }

    public Integer handlePledge(CashFtpInfluenceBO cashFtpInfluenceBO, Set<Long> contractIds) {
        if (cashFtpInfluenceBO.isJudgePledge() && (RiskControlIndustryClassify.PUBLIC_UTILITIES.name()
                .equalsIgnoreCase(cashFtpInfluenceBO.getRiskControlIndustryClassify()) || RiskControlIndustryClassify.CIVIL_CONSUMPTION.name()
                .equalsIgnoreCase(cashFtpInfluenceBO.getRiskControlIndustryClassify()))) {
            boolean isPledge = isPledge(contractIds);
            boolean isDirectPledge = isDirectPledge(contractIds);
            boolean containsPledge = isPledge || isDirectPledge;
            if (!containsPledge) {
                return 10000;
            }
        }
        return 0;
    }

    public CashFtpInfluenceBO getCashFtpInfluence(Long contractId, LocalDate targetDate) {
        return ftpPricingContextPort.assembleCashFtpInfluence(contractId, targetDate);
    }

    //获取最新项目季度最低收益率
    public Integer getFtpQuarterlyBasePricing(FtpQuarterlyBasePricingBO ftpQuarterlyBasePricingBO) {
        //找到最新的生效的季度指导定价
        List<NewFtpQuarterlyBasePricingDraft> newFtpQuarterlyBasePricings = newFtpQuarterlyBasePricingDraftMapper.selectList(Wrappers.<NewFtpQuarterlyBasePricingDraft>lambdaQuery()
                .select(NewFtpQuarterlyBasePricingDraft::getFtpId)
                .groupBy(NewFtpQuarterlyBasePricingDraft::getFtpId));
        if (CollectionUtil.isEmpty(newFtpQuarterlyBasePricings)) {
            return 0;
        }
        NewFtpBaseInfo newFtpBaseInfo = newFtpBaseInfoService.getOne(Wrappers.<NewFtpBaseInfo>lambdaQuery()
                .eq(NewFtpBaseInfo::getFtpRecordStatus, RecordStatus.TAKE_EFFECT.name())
                .in(NewFtpBaseInfo::getId, newFtpQuarterlyBasePricings.stream().map(NewFtpQuarterlyBasePricingDraft::getFtpId).collect(Collectors.toList()))
                .orderByDesc(NewFtpBaseInfo::getId)
                .last(StringUtil.mysqlLimitOne()));
        if (ObjectUtil.isEmpty(newFtpBaseInfo)) {
            return 0;
        }
        if (ftpQuarterlyBasePricingBO.isRelated()) {
            NewFtpQuarterlyBasePricingExtDraft newFtpQuarterlyBasePricingExtDraft = newFtpQuarterlyBasePricingExtDraftService.getOne(
                    Wrappers.<NewFtpQuarterlyBasePricingExtDraft>lambdaQuery()
                            .eq(NewFtpQuarterlyBasePricingExtDraft::getFtpId, newFtpBaseInfo.getId())
                            .last(StringUtil.mysqlLimitOne())
            );
            if (Objects.nonNull(newFtpQuarterlyBasePricingExtDraft)) {
                switch (ftpQuarterlyBasePricingBO.getRelatedTermRange()) {
                    case THREE_YEAR: return Optional.ofNullable(newFtpQuarterlyBasePricingExtDraft.getThreeYear()).orElse(0);
                    case THREE_TO_FIVE_YEAR: return Optional.ofNullable(newFtpQuarterlyBasePricingExtDraft.getThreeToFiveYear()).orElse(0);
                    case MORE_THAN_FIVE_YEAR: return Optional.ofNullable(newFtpQuarterlyBasePricingExtDraft.getMoreThanFiveYear()).orElse(0);
                }
            }
        } else {
            NewFtpQuarterlyBasePricingDraft newFtpQuarterlyBasePricing = newFtpQuarterlyBasePricingDraftMapper.selectOne(Wrappers.<NewFtpQuarterlyBasePricingDraft>lambdaQuery()
                    .eq(NewFtpQuarterlyBasePricingDraft::getFtpId, newFtpBaseInfo.getId())
                    .eq(NewFtpQuarterlyBasePricingDraft::getAssetIndustryClassify, ftpQuarterlyBasePricingBO.getAssetIndustryClassify())
                    .eq(NewFtpQuarterlyBasePricingDraft::getRegionalClassify, ftpQuarterlyBasePricingBO.getRegionalClassify())
                    .eq(NewFtpQuarterlyBasePricingDraft::getTermRange, ftpQuarterlyBasePricingBO.getTermRange())
                    .eq(NewFtpQuarterlyBasePricingDraft::getCustomerEntityClassify, ftpQuarterlyBasePricingBO.getCustomerEntityClassify()));
            if (ObjectUtil.isNotEmpty(newFtpQuarterlyBasePricing)) {
                NewFtpQuarterlyBasePricingLib newFtpQuarterlyBasePricingLib = newFtpQuarterlyBasePricingLibHandler.queryLatestDataByOriginId(newFtpQuarterlyBasePricing.getId());
                return newFtpQuarterlyBasePricingLib == null ? 0 : Optional.ofNullable(newFtpQuarterlyBasePricingLib.getValue()).orElse(0);
            }
        }
        return 0;
    }

    public boolean isPledge(Set<Long> contractIds) {
        if (CollectionUtil.isEmpty(contractIds)) {
            return false;
        }
        return newFtpFundDataPort.existsPledge(contractIds);
    }

    public boolean isDirectPledge(Set<Long> contractIds) {
        if (CollectionUtil.isEmpty(contractIds)) {
            return false;
        }
        return newFtpFundDataPort.existsDirectPledge(contractIds);
    }

//    @Transactional(rollbackFor = Throwable.class)
//    public List<FtpAssessmentInfo> getFtpAssessmentInfo(CashFtpInfluenceBO cashFtpInfluence, List<PaymentBaseInfo> paymentBaseInfos) {
//        if (CollUtil.isEmpty(paymentBaseInfos)) {
//            return Collections.emptyList();
//        }
//        List<PaymentBaseInfo> needUpdateList = new ArrayList<>();
//        List<FtpAssessmentInfo> ftpAssessmentInfoList = new ArrayList<>(paymentBaseInfos.size());
//        BillFtpBO billFtp = this.getBillFtp(cashFtpInfluence.getTargetDate());
//        Long cacheGuidePrice = null;
//        Long cachePledgePrice = null;
//        for (PaymentBaseInfo paymentBaseInfo : paymentBaseInfos) {
//            FtpAssessmentInfo ftpAssessmentInfo = FtpAssessmentInfo.builder()
//                    .mountainAdjustment(0L).gradeAdjustment(0L).handAdjustment(0L)
//                    .paymentId(paymentBaseInfo.getId()).build();
//            Integer ticketPrice = Optional.ofNullable(billFtp).map(BillFtpBO::getBillFtpSell).orElse(0);
//            ftpAssessmentInfo.setTicketPrice(Long.parseLong(String.valueOf(ticketPrice)));
//            Set<Long> contractIds = new HashSet<>(Collections.singletonList(paymentBaseInfo.getContractId()));
//            if (Objects.isNull(cachePledgePrice)) {
//                // 只走一次
//                cachePledgePrice = Long.valueOf(handlePledge(cashFtpInfluence, contractIds));
//            }
//            if (Objects.isNull(cacheGuidePrice)) {
//                // 只走一次
//                cacheGuidePrice = Long.valueOf(getCashFtp(cashFtpInfluence, contractIds));
//            }
//            // 一开始他们两个应该是一样的
//            ftpAssessmentInfo.setGuidePrice(cacheGuidePrice - cachePledgePrice);
//            ftpAssessmentInfo.setBasePrice(cacheGuidePrice - cachePledgePrice);
//            ftpAssessmentInfo.setPledgePrice(cachePledgePrice);
//            // 这里存在执行顺序，后面两个set方法必须后执行
//            ftpAssessmentInfo.setGuidePrice();
//            ftpAssessmentInfo.setAssessmentPrice();
//            // 后续的某些信息需要自己获取
//            ftpAssessmentInfoList.add(ftpAssessmentInfo);
//
//            PaymentBaseInfo baseInfo = new PaymentBaseInfo();
//            baseInfo.setId(paymentBaseInfo.getId());
//            baseInfo.setCashFtp(Math.toIntExact(ftpAssessmentInfo.getAssessmentPrice()));
//            baseInfo.setBillFtp(Math.toIntExact(ftpAssessmentInfo.getTicketPrice()));
//            needUpdateList.add(baseInfo);
//        }
//
//        if (CollUtil.isNotEmpty(needUpdateList)) {
//            log.error("需要填充付款申请的FTP价格: {}", JSONUtil.toJsonStr(needUpdateList));
//            // 存在数据库死锁问题，待排查后修改，先注释
////            paymentBaseInfoService.updateBatchById(needUpdateList);
//        }
//        return ftpAssessmentInfoList;
//    }
}
