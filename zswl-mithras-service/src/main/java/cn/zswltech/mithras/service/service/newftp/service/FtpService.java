package cn.zswltech.mithras.service.service.newftp.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailREQ;
import cn.zswltech.mithras.dto.contract.price.ContractPriceDetailRSP;
import cn.zswltech.mithras.service.constant.VersionTypeConstants;
import cn.zswltech.mithras.service.enums.CorpAddressType;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.client.ClientType;
import cn.zswltech.mithras.service.enums.client.EnterpriseNatureEnum;
import cn.zswltech.mithras.service.enums.client.OwnershipTypeEnum;
import cn.zswltech.mithras.service.enums.common.RecordStatus;
import cn.zswltech.mithras.service.enums.contract.LesseeTypeEnum;
import cn.zswltech.mithras.ftp.oldftp.enums.FtpBusinessVersion;
import cn.zswltech.mithras.service.enums.newftp.*;
import cn.zswltech.mithras.ftp.newftp.enums.*;
import cn.zswltech.mithras.service.enums.projpricing.FtpIndustryCategoryEnum;
import cn.zswltech.mithras.service.enums.projpricing.ProjectManageLevelEnum;
import cn.zswltech.mithras.service.enums.projreview.ProjectClassify;
import cn.zswltech.mithras.service.enums.riskcontrol.RiskControlIndustryClassify;
import cn.zswltech.mithras.service.fund.direct.entity.FundDirectFinancingPledgeInfo;
import cn.zswltech.mithras.service.fund.direct.service.FundDirectFinancingPledgeInfoService;
import cn.zswltech.mithras.service.mapper.lib.client.CorpCommerceInfoLibMapper;
import cn.zswltech.mithras.service.mapper.model.client.ClientBaseModel;
import cn.zswltech.mithras.service.mapper.model.client.CorpAddressInfo;
import cn.zswltech.mithras.service.mapper.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.service.mapper.model.client.CorpCommerceInfoLib;
import cn.zswltech.mithras.service.mapper.model.contract.*;
import cn.zswltech.mithras.service.mapper.model.fund.financing.FundFinancingPledgeInfo;
import cn.zswltech.mithras.service.mapper.model.payment.FtpAssessmentInfo;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projpricing.ProjPricingBaseInfo;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.ftp.oldftp.bo.BillFtpBO;
import cn.zswltech.mithras.ftp.oldftp.bo.CashFtpInfluenceBO;
import cn.zswltech.mithras.ftp.oldftp.bo.FtpQuarterlyBasePricingBO;
import cn.zswltech.mithras.service.service.client.CorpAddressInfoService;
import cn.zswltech.mithras.service.service.client.CorpCommerceInfoService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractPriceService;
import cn.zswltech.mithras.service.service.fund.financing.FundFinancingPledgeInfoService;
import cn.zswltech.mithras.service.service.lib.contract.ContractGuarantorLibService;
import cn.zswltech.mithras.service.service.lib.contract.ContractTenantryLibService;
import cn.zswltech.mithras.service.service.newftp.lib.impl.NewFtpQuarterlyBasePricingLibHandler;
import cn.zswltech.mithras.ftp.newftp.mapper.draft.NewFtpQuarterlyBasePricingDraftMapper;
import cn.zswltech.mithras.ftp.newftp.model.NewFtpBaseInfo;
import cn.zswltech.mithras.ftp.newftp.model.draft.NewFtpMonthlyGuidanceDraft;
import cn.zswltech.mithras.ftp.newftp.model.draft.NewFtpMonthlyGuidanceExtDraft;
import cn.zswltech.mithras.ftp.newftp.model.draft.NewFtpQuarterlyBasePricingDraft;
import cn.zswltech.mithras.ftp.newftp.model.draft.NewFtpQuarterlyBasePricingExtDraft;
import cn.zswltech.mithras.ftp.newftp.model.lib.NewFtpMonthlyGuidanceExtLib;
import cn.zswltech.mithras.ftp.newftp.model.lib.NewFtpMonthlyGuidanceLib;
import cn.zswltech.mithras.ftp.newftp.model.lib.NewFtpQuarterlyBasePricingLib;
import cn.zswltech.mithras.service.service.newftp.service.drift.NewFtpQuarterlyBasePricingExtDraftService;
import cn.zswltech.mithras.ftp.newftp.service.lib.NewFtpMonthlyGuidanceExtLibService;
import cn.zswltech.mithras.service.service.newftp.service.lib.NewFtpMonthlyGuidanceLibService;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.service.service.projpricing.ProjPricingBaseInfoService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private ContractPriceService contractPriceService;
    @Resource
    private CorpCommerceInfoService corpCommerceInfoService;
    @Resource
    private CorpAddressInfoService corpAddressInfoService;
    @Resource
    private ProjReviewBaseInfoService projReviewBaseInfoService;
    @Resource
    private NewFtpBaseInfoService newFtpBaseInfoService;
    @Resource
    private NewFtpMonthlyGuidanceLibService newFtpMonthlyGuidanceLibService;
    @Resource
    private NewFtpMonthlyGuidanceExtLibService newFtpMonthlyGuidanceExtLibService;
    @Resource
    private NewFtpQuarterlyBasePricingDraftMapper newFtpQuarterlyBasePricingDraftMapper;
    @Resource
    private NewFtpQuarterlyBasePricingLibHandler newFtpQuarterlyBasePricingLibHandler;
    @Resource
    private FundFinancingPledgeInfoService fundFinancingPledgeInfoService;
    @Resource
    private FundDirectFinancingPledgeInfoService fundDirectFinancingPledgeInfoService;
    @Resource
    private CorpCommerceInfoLibMapper corpCommerceInfoLibMapper;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;


    public BillFtpBO getBillFtp(LocalDate targetDate) {
        NewFtpBaseInfo effectOne = newFtpBaseInfoService.getOne(Wrappers.<NewFtpBaseInfo>lambdaQuery()
                .eq(NewFtpBaseInfo::getFtpRecordStatus, "TAKE_EFFECT")
                .le(NewFtpBaseInfo::getMonth, targetDate)
                .orderByDesc(NewFtpBaseInfo::getMonth).last("limit 1"));
        if (effectOne == null) {
            throw new MithrasException("未找到生效的FTP");
        }
        NewFtpMonthlyGuidanceExtLib ext = newFtpMonthlyGuidanceExtLibService.getOne(Wrappers.<NewFtpMonthlyGuidanceExtLib>lambdaQuery()
                .eq(NewFtpMonthlyGuidanceExtDraft::getFtpId, effectOne.getId())
                .eq(NewFtpMonthlyGuidanceExtLib::getVersion, effectOne.getNewestVersion())
                .last("limit 1"));
        return new BillFtpBO(ext.getSellingPrice(), ext.getBuyingPrice());
    }

    public Integer getCashFtp(CashFtpInfluenceBO cashFtpInfluenceBO, Set<Long> contractIds) {
        // 取最新生效的FTP
        NewFtpBaseInfo newFtpBaseInfo = newFtpBaseInfoService.getOne(Wrappers.<NewFtpBaseInfo>lambdaQuery()
                .eq(NewFtpBaseInfo::getFtpRecordStatus, RecordStatus.TAKE_EFFECT.name())
                .le(NewFtpBaseInfo::getMonth, cashFtpInfluenceBO.getTargetDate())
                .orderByDesc(NewFtpBaseInfo::getMonth)
                .last(StringUtil.mysqlLimitOne()));
        if (newFtpBaseInfo == null) {
            throw new MithrasException("未找到生效的FTP");
        }
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
            List<CorpCommerceInfo> corpCommerceInfoList = SpringUtil.getBean(CorpCommerceInfoService.class).findByClientId(cashFtpInfluenceBO.getEvaluationSubjectId());
            if (CollectionUtil.isNotEmpty(corpCommerceInfoList)) {
                CorpCommerceInfo corpCommerceInfo = corpCommerceInfoList.get(0);
                if (Objects.equals(corpCommerceInfo.getIsRelated(), YesOrNoNumberEnum.YES.getCode())) {
                    NewFtpMonthlyGuidanceLib newFtpMonthlyGuidanceLib = SpringUtil.getBean(NewFtpMonthlyGuidanceLibService.class).getOne(
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
        }
        FtpIndustryCategoryEnum ftpIndustryCategoryEnum = FtpIndustryCategoryEnum.getByName(cashFtpInfluenceBO.getFtpIndustryCategory());
        ProjectManageLevelEnum projectManageLevelEnum = ProjectManageLevelEnum.getByName(cashFtpInfluenceBO.getProjectManageLevel());
        // 风控行业分类
        RiskIndustryClassify riskIndustryClassify = RiskIndustryClassify.getByFtpIndustryCategory(ftpIndustryCategoryEnum);
        // 资产行业分类
        AssetIndustryClassify assetIndustryClassify = AssetIndustryClassify.getByProjectClassify(ProjectClassify.find(cashFtpInfluenceBO.getAssetIndustryClassify()));
        // 客户主体分类
        CustomerEntityClassify customerEntityClassify = this.getCEntityClassifyByMainTenantryId(cashFtpInfluenceBO.getTenantId(), cashFtpInfluenceBO.getGuarantorIdList(), newFtpBaseInfo);
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

        CustomerEntityClassify customerEntityClassify = getCEntityClassifyByMainTenantryId(cashFtpInfluenceBO.getTenantId(), cashFtpInfluenceBO.getGuarantorIdList(), effectOne);
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

    /**
     * 20240822版本-判断客户是否为上市公司时，需按上述流程图中的逻辑进行判断
     * /* 1、判断承租人的企业性质是否是上市公司
     * /*     A. 是：属于上市公司
     * /*     B. 否：判断“上市公司控股类型”
     * /*          1）直接控股：属于上市公司
     * /*          2）非控股：不属于上市公司
     * /*          3）间接控股：查看担保人是否为上市公司
     * /*              a. 是：属于上市公司
     * /*              b. 否：不属于上市公司
     **/
    public CustomerEntityClassify getCEntityClassifyByMainTenantryId(Long mainTenantryId, List<Long> guarantorIdList, NewFtpBaseInfo newFtpBaseInfo) {
        CorpCommerceInfoLib commerceInfoLib = corpCommerceInfoLibMapper.selectOne(Wrappers.<CorpCommerceInfoLib>lambdaQuery()
                .eq(ClientBaseModel::getClientId, mainTenantryId)
                .eq(CorpCommerceInfoLib::getVersionType, VersionTypeConstants.NORMAL)
                .orderByDesc(CorpCommerceInfoLib::getVersion)
                .last(StringUtil.mysqlLimitOne()));
        if (Objects.isNull(commerceInfoLib)) {
            throw MithrasException.newException("客户工商信息不存在");
        }
        //根据上述规则找到客户真正的企业性质
        if (CharSequenceUtil.isBlank(commerceInfoLib.getEnterpriseNature())) {
            throw MithrasException.newException("企业性质信息为空");
        }
        EnterpriseNatureEnum enterpriseNatureEnum = EnterpriseNatureEnum.of(commerceInfoLib.getEnterpriseNature());
        if (Objects.isNull(enterpriseNatureEnum)) {
            throw MithrasException.newException("企业性质信息错误");
        }
        FtpBusinessVersion ftpBusinessVersion = FtpBusinessVersion.getByName(newFtpBaseInfo.getFtpBusinessVersion());
        switch (enterpriseNatureEnum) {
            case myqtss:
                return CustomerEntityClassify.CUSTOMER_OTHER_LISTED;
            case myss:
            case gyss:
                if (ftpBusinessVersion == FtpBusinessVersion.V3) {
                    return CustomerEntityClassify.CUSTOMER_LISTED_STATE_OWNED;
                } else {
                    return CustomerEntityClassify.LISTED_COMPANY;
                }
            case gyfss:
                if (ftpBusinessVersion == FtpBusinessVersion.V3) {
                    return CustomerEntityClassify.CUSTOMER_LISTED_STATE_OWNED;
                }
            case myfss:
            case other:
                // 找到控股类型
                if (CharSequenceUtil.isBlank(commerceInfoLib.getOwnershipType())) {
                    throw new MithrasException("客户的控股类型为空");
                }
                OwnershipTypeEnum ownershipTypeEnum = OwnershipTypeEnum.ofName(commerceInfoLib.getOwnershipType());
                if (Objects.isNull(ownershipTypeEnum)) {
                    throw MithrasException.newException("客户的控股类型错误");
                }
                switch (ownershipTypeEnum) {
                    case DIRECT:
                        return CustomerEntityClassify.LISTED_COMPANY;
                    case NON:
                        if (enterpriseNatureEnum.equals(EnterpriseNatureEnum.gyfss)) {
                            return CustomerEntityClassify.STATE_OWNED_ENTERPRISE;
                        }
                        return CustomerEntityClassify.OTHER;
                    case INDIRECT:
                        // 间接控股，找到担保人的企业性质
                        return getEntityClassify(guarantorIdList, enterpriseNatureEnum);
                }
                break;
            default:
                break;
        }
        throw MithrasException.newException("客户类型错误");
    }

    private CustomerEntityClassify getEntityClassify(List<Long> guarantorIdList, EnterpriseNatureEnum enterpriseNatureEnum) {
        if (CollectionUtils.isEmpty(guarantorIdList)) {
            switch (enterpriseNatureEnum) {
                case gyss:
                    return CustomerEntityClassify.LISTED_COMPANY;
                case gyfss:
                    return CustomerEntityClassify.STATE_OWNED_ENTERPRISE;
                case myfss:
                    return CustomerEntityClassify.OTHER;
                default:
                    return CustomerEntityClassify.OTHER;
            }
        }
        for (Long guarantorId : guarantorIdList) {
            CorpCommerceInfoLib corpCommerceInfoLib = corpCommerceInfoLibMapper.selectOne(Wrappers.<CorpCommerceInfoLib>lambdaQuery()
                    .eq(ClientBaseModel::getClientId, guarantorId)
                    .eq(CorpCommerceInfoLib::getVersionType, VersionTypeConstants.NORMAL)
                    .orderByDesc(CorpCommerceInfoLib::getVersion)
                    .last(StringUtil.mysqlLimitOne()));
            if (Objects.isNull(corpCommerceInfoLib)) {
                throw MithrasException.newException("客户工商信息不存在");
            }
            if (CharSequenceUtil.isBlank(corpCommerceInfoLib.getEnterpriseNature())) {
                throw MithrasException.newException("企业性质信息为空");
            }
            EnterpriseNatureEnum enterpriseNature = EnterpriseNatureEnum.of(corpCommerceInfoLib.getEnterpriseNature());
            if (Objects.isNull(enterpriseNature)) {
                throw MithrasException.newException("担保人企业性质信息错误");
            }
            switch (enterpriseNature) {
                case myss:
                case gyss:
                    return CustomerEntityClassify.LISTED_COMPANY;
                case gyfss:
                case myfss:
                case other:
                    if (enterpriseNatureEnum.equals(EnterpriseNatureEnum.gyfss)) {
                        return CustomerEntityClassify.STATE_OWNED_ENTERPRISE;
                    }
                    return CustomerEntityClassify.OTHER;
                default:
                    break;
            }
        }
        throw MithrasException.newException("担保人的企业性质信息错误");
    }

    public CashFtpInfluenceBO getCashFtpInfluence(Long contractId, LocalDate targetDate) {
        ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractId);
        Assert.notNull(contractBaseInfo, () -> MithrasException.newException("合同信息不存在"));
        CashFtpInfluenceBO cashFtpInfluenceBO = new CashFtpInfluenceBO();
        cashFtpInfluenceBO.setBizType(contractBaseInfo.getBizType());
        // 查询合同报价方案
        ContractPriceDetailREQ req = new ContractPriceDetailREQ();
        req.setContractId(contractId);
        ContractPriceDetailRSP contractPriceDetailRSP = contractPriceService.detail(req);
        if (Objects.nonNull(contractPriceDetailRSP)) {
            cashFtpInfluenceBO.setContractMonthCount(contractPriceDetailRSP.getMonthCount());
        }
        // 查询客户工商信息
//        CorpCommerceInfo corpCommerceInfo = corpCommerceInfoService.detail(contractBaseInfo.getClientId(), null);
        List<CorpCommerceInfo> corpCommerceInfoList = corpCommerceInfoService.findByClientId(contractBaseInfo.getClientId());
        if (CollectionUtil.isNotEmpty(corpCommerceInfoList)) {
            CorpCommerceInfo corpCommerceInfo = corpCommerceInfoList.get(0);
            cashFtpInfluenceBO.setRiskControlIndustryClassify(corpCommerceInfo.getRiskControlIndustryClassify());
            cashFtpInfluenceBO.setEnterpriseNature(corpCommerceInfo.getEnterpriseNature());
        }
        // 确定客户地址是否在浙江
        cashFtpInfluenceBO.setZhejiang(this.isZhejiang(contractBaseInfo.getClientId()));
        // 查询项目评审信息
        ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoService.getById(contractBaseInfo.getProjReviewId());
        if (Objects.nonNull(projReviewBaseInfo)) {
//            cashFtpInfluenceBO.setAssetIndustryClassify(projReviewBaseInfo.getProjectClassify());
//            cashFtpInfluenceBO.setRegionClassify(projReviewBaseInfo.getRegionalProjectClassify());
            // 查定价
            ProjPricingBaseInfo projPricingBaseInfo = SpringUtil.getBean(ProjPricingBaseInfoService.class).getPricingByReviewId(projReviewBaseInfo.getId());
            if (Objects.nonNull(projPricingBaseInfo)) {
                cashFtpInfluenceBO.setFtpIndustryCategory(projPricingBaseInfo.getFtpIndustryCategory());
                cashFtpInfluenceBO.setAssetIndustryClassify(projPricingBaseInfo.getProjectClassify());
                cashFtpInfluenceBO.setRegionClassify(projPricingBaseInfo.getRegionalProjectClassify());
                cashFtpInfluenceBO.setRegionalDivision(projPricingBaseInfo.getRegionalDivision());
                cashFtpInfluenceBO.setProjectManageLevel(projPricingBaseInfo.getProjectManageLevel());
                cashFtpInfluenceBO.setIsAAA(projPricingBaseInfo.getIsAAA());
                cashFtpInfluenceBO.setEvaluationSubjectId(projPricingBaseInfo.getEvaluationSubjectId());
            }
        }
        cashFtpInfluenceBO.setTargetDate(targetDate);

        // 尝试填充担保人人信息和主承租人信息，这个只在合同阶段使用
        ContractTenantryLib tenantryLib = SpringUtil.getBean(ContractTenantryLibService.class).getOne(Wrappers.<ContractTenantryLib>lambdaQuery()
                .eq(ContractTenantryLib::getContractId, contractId)
                .eq(ContractTenantry::getLesseeType, LesseeTypeEnum.MAIN_LESSSEE.name())
                .eq(ContractTenantryLib::getVersionType, VersionTypeConstants.NORMAL)
                .orderByDesc(ContractTenantryLib::getVersion)
                .last(StringUtil.mysqlLimitOne()));
        if (Objects.nonNull(tenantryLib)) {
            cashFtpInfluenceBO.setTenantId(tenantryLib.getLesseeId());
        }

        ContractGuarantorLib contractGuarantorLib = SpringUtil.getBean(ContractGuarantorLibService.class).getOne(Wrappers.<ContractGuarantorLib>lambdaQuery()
                .eq(ContractGuarantorLib::getContractId, contractId)
                .eq(ContractGuarantorLib::getVersionType, VersionTypeConstants.NORMAL)
                .eq(ContractGuarantor::getGuarantorType, ClientType.CORPORATION.name())
                .orderByDesc(ContractGuarantorLib::getVersion)
                .last(StringUtil.mysqlLimitOne())
        );
        if (Objects.nonNull(contractGuarantorLib) && StrUtil.isNotBlank(contractGuarantorLib.getGuarantorIds())) {
            List<Long> ids = JSONUtil.toList(contractGuarantorLib.getGuarantorIds(), Long.class);
            if (CollectionUtil.isNotEmpty(ids)) {
                cashFtpInfluenceBO.setGuarantorIdList(ids);
            }
        }
        return cashFtpInfluenceBO;
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
            NewFtpQuarterlyBasePricingExtDraft newFtpQuarterlyBasePricingExtDraft = SpringUtil.getBean(NewFtpQuarterlyBasePricingExtDraftService.class).getOne(
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

    private boolean isZhejiang(Long clientId) {
        // 查询客户地址
        List<CorpAddressInfo> corpAddressInfoList = corpAddressInfoService.listCorpAddressInfo(clientId);
        if (CollectionUtil.isNotEmpty(corpAddressInfoList)) {
            Map<String, List<CorpAddressInfo>> map = corpAddressInfoList.stream().collect(Collectors.groupingBy(CorpAddressInfo::getAddressType));
            List<CorpAddressInfo> workAddressList = map.get(CorpAddressType.WORK_ADDRESS.name());
            List<CorpAddressInfo> registryAddressList = map.get(CorpAddressType.REGISTRY_ADDRESS.name());
            if (CollectionUtil.isNotEmpty(workAddressList)) {
                for (CorpAddressInfo corpAddressInfo : workAddressList) {
                    if (Objects.equals(corpAddressInfo.getProvince(), "330000")) {
                        return true;
                    }
                }
                for (CorpAddressInfo corpAddressInfo : registryAddressList) {
                    if (Objects.equals(corpAddressInfo.getProvince(), "330000")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isPledge(Set<Long> contractIds) {
        if (CollectionUtil.isEmpty(contractIds)) {
            return false;
        }
        boolean isPledge = false;
        List<FundFinancingPledgeInfo> fundFinancingPledgeInfoList = fundFinancingPledgeInfoService.list(Wrappers.<FundFinancingPledgeInfo>lambdaQuery().in(FundFinancingPledgeInfo::getContractId, contractIds));
        if (CollectionUtil.isEmpty(fundFinancingPledgeInfoList)) {
            return isPledge;
        }
        for (FundFinancingPledgeInfo fundFinancingPledgeInfo : fundFinancingPledgeInfoList) {
            if (fundFinancingPledgeInfo.getIsPledge().equals(true)) {
                isPledge = true;
                break;
            }
        }
        return isPledge;
    }

    public boolean isDirectPledge(Set<Long> contractIds) {
        if (CollectionUtil.isEmpty(contractIds)) {
            return false;
        }
        boolean isDirectPledge = false;
        List<FundDirectFinancingPledgeInfo> directFinancingPledgeInfoList = fundDirectFinancingPledgeInfoService.list(Wrappers.<FundDirectFinancingPledgeInfo>lambdaQuery().in(FundDirectFinancingPledgeInfo::getContractId, contractIds));
        if (CollectionUtil.isEmpty(directFinancingPledgeInfoList)) {
            return isDirectPledge;
        }
        for (FundDirectFinancingPledgeInfo fundDirectFinancingPledgeInfo : directFinancingPledgeInfoList) {
            if (fundDirectFinancingPledgeInfo.getIsPledge().equals(true)) {
                isDirectPledge = true;
                break;
            }
        }
        return isDirectPledge;
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
