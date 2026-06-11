package cn.zswltech.mithras.application.orchestration.document.gendoc.render;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.dto.client.client.ClientInfo;
import cn.zswltech.mithras.dto.projpricing.price.ProjPricingPriceDetailRSP;
import cn.zswltech.mithras.ftp.oldftp.bo.CashFtpInfluenceBO;
import cn.zswltech.mithras.ftp.oldftp.bo.FtpQuarterlyBasePricingBO;
import cn.zswltech.mithras.foundation.constant.GlobalConstants;
import cn.zswltech.mithras.customer.enums.client.EnterpriseNatureEnum;
import cn.zswltech.mithras.foundation.enums.common.ProjectBizType;
import cn.zswltech.mithras.contract.enums.contract.RepayRateEnum;
import cn.zswltech.mithras.ftp.newftp.enums.AssetIndustryClassify;
import cn.zswltech.mithras.customer.enums.client.CustomerEntityClassify;
import cn.zswltech.mithras.projectprocess.enums.projpricing.RegionalClassify;
import cn.zswltech.mithras.ftp.newftp.enums.TermRange;
import cn.zswltech.mithras.projectprocess.enums.projestablish.RateType;
import cn.zswltech.mithras.projectprocess.enums.projestablish.RepayCalcType;
import cn.zswltech.mithras.projectprocess.enums.projpricing.FtpIndustryCategoryEnum;
import cn.zswltech.mithras.projectprocess.enums.projpricing.ProjPricingMaterialsEnum;
import cn.zswltech.mithras.projectprocess.enums.projreview.ProjRegionalDivisionEnum;
import cn.zswltech.mithras.projectprocess.enums.projreview.ProjectClassify;
import cn.zswltech.mithras.riskcontrol.common.RiskControlIndustryClassify;
import cn.zswltech.mithras.contract.gendoc.AbstractBasicRender;
import cn.zswltech.mithras.customer.model.client.CorpCommerceInfo;
import cn.zswltech.mithras.payment.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projpricing.ProjPricingBaseInfo;
import cn.zswltech.mithras.projectprocess.mapper.model.projpricing.ProjPricingCashFlowPlan;
import cn.zswltech.mithras.projectprocess.mapper.model.projpricing.ProjPricingLeasePrice;
import cn.zswltech.mithras.projectprocess.mapper.projpricing.ProjPricingCashFlowPlanMapper;
import cn.zswltech.mithras.projectprocess.mapper.projpricing.ProjPricingLeasePriceMapper;
import cn.zswltech.mithras.projectprocess.application.bo.CashFlowBO;
import cn.zswltech.mithras.projectprocess.application.bo.DailyDiscountRateCalcResultBO;
import cn.zswltech.mithras.projectprocess.application.bo.ProjPricingRenderBO;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.bo.*;
import cn.zswltech.mithras.customer.application.client.CorpCommerceInfoService;
import cn.zswltech.mithras.ftp.newftp.service.FtpService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projpricing.ProjPricingPriceService;
import cn.zswltech.mithras.application.orchestration.util.FinancialUtil;
import cn.zswltech.mithras.foundation.util.LongUtil;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.deepoove.poi.XWPFTemplate;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2022/9/13
 * @description 项目收益率审查意见书
 * @deprecated 该类废弃，由cn.zswltech.mithras.application.orchestration.workflow.flow.dynamicform.projreview.SetPricingChooseApproveAuthHandler功能替代
 */
@Deprecated
@Slf4j
@Component
public class ProjPricingEarningsRateRender extends AbstractBasicRender<ProjPricingRenderBO> {
    @Resource
    private ProjPricingPriceService projPricingPriceService;
    @Resource
    private CorpCommerceInfoService corpCommerceInfoService;
    @Resource
    private FtpService ftpService;
    @Resource
    private ProjPricingCashFlowPlanMapper projPricingCashFlowPlanMapper;
    @Resource
    private ProjPricingLeasePriceMapper projPricingLeasePriceMapper;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;

    @Override
    public String render(OutputStream outputStream, ProjPricingRenderBO projPricingRenderBO) throws Exception {
        ProjPricingBaseInfo projPricingBaseInfo = projPricingRenderBO.getProjPricingBaseInfo();
        Map<Long, OrgDO> orgMap = businessDataRepository.getOrgMap(Collections.singletonList(projPricingBaseInfo.getBizDeptId()));
        OrgDO orgDO = orgMap.get(projPricingBaseInfo.getBizDeptId());
        ProjPricingPriceDetailRSP priceDetail = projPricingPriceService.detail(projPricingBaseInfo.getId());
        if (Objects.isNull(priceDetail.getLeasePriceDetailRSP()) && Objects.isNull(priceDetail.getFactoringPriceDetailRSP()) && Objects.isNull(priceDetail.getAocPriceDetailRSP())) {
            throw new MithrasException("报价方案不存在");
        }
        //查询客户信息
        CorpCommerceInfo corpCommerceInfo = corpCommerceInfoService.detail(projPricingBaseInfo.getClientId(), null);
        if (Objects.isNull(corpCommerceInfo)) {
            throw new MithrasException("客户信息不存在");
        }
        String templatePath = "/doc/项目评审_项目收益率审查意见书.docx";
        Map<String, Object> renderMap = new HashMap<>(32);
        FtpQuarterlyBasePricingBO ftpQuarterlyBasePricingBO = new FtpQuarterlyBasePricingBO();
        renderMap.put(RenderParameterKeyHolder.REPORT_DATE, LocalDateTimeUtil.format(LocalDateTime.now(), DatePattern.CHINESE_DATE_PATTERN));
        renderMap.put(RenderParameterKeyHolder.PROJ_NAME, projPricingBaseInfo.getProjName());
        renderMap.put(RenderParameterKeyHolder.BIZ_DEPT, Optional.ofNullable(orgDO).map(OrgDO::getName).orElse(""));
        renderMap.put(RenderParameterKeyHolder.PROJECT_CLASSIF, Optional.ofNullable(ProjectClassify.find(projPricingBaseInfo.getProjectClassify())).map(ProjectClassify::display).orElse("   "));
        TermRange term = getTerm(priceDetail.getMonthCount());
        renderMap.put(RenderParameterKeyHolder.TERM, term.getProjReviewEarningRateDisplay());
        ftpQuarterlyBasePricingBO.setTermRange(term.name());
//        if (StrUtil.equalsAny(corpCommerceInfo.getRiskControlIndustryClassify(), RiskControlIndustryClassify.PUBLIC_UTILITIES.name(), RiskControlIndustryClassify.CIVIL_CONSUMPTION.name(), RiskControlIndustryClassify.TRAVEL.name())) {
//            //当风控行业分类=公用事业类、民生消费类、旅游行业时按区域划分进行区分，①若区域划分为「浙江地区」则展示为“浙江地区”②若地区分类为「一类地区」则展示为“鼓励支持类地区”；若地区分类为「二类地区」则展示为“其他地区”；
//            renderMap.put(RenderParameterKeyHolder.REGIONAL_PROJECT_CLASSIFY, ProjRegionalDivisionEnum.getProjReviewEarningsRegionalClassify(ProjRegionalDivisionEnum.of(projPricingBaseInfo.getRegionalDivision())));
//        } else {
//            //当风控行业分类≠公用事业类、民生消费类、旅游行业时按地区分类进行区分，①若地区分类为「浙江地区」则展示为“浙江地区”；②若地区分类为「鼓励支持类」则展示为“鼓励支持类地区”；③若地区分类为「适度支持类」或「谨慎支持类」则展示为“其他地区”。
//            renderMap.put(RenderParameterKeyHolder.REGIONAL_PROJECT_CLASSIFY, RegionalClassify.getProjRegionalClassify(projPricingBaseInfo.getRegionalProjectClassify()).display());
//        }
        RegionalClassify regionalClassify;
        if (StrUtil.equalsAny(projPricingBaseInfo.getFtpIndustryCategory(), FtpIndustryCategoryEnum.FTP_STATE_OWNED_INDUSTRY.name(), FtpIndustryCategoryEnum.FTP_PUBLIC_UTILITIES.name(), FtpIndustryCategoryEnum.FTP_CIVIL_CONSUMPTION.name())) {
            regionalClassify = ProjRegionalDivisionEnum.getProjReviewEarningsRegionalClassify(ProjRegionalDivisionEnum.of(projPricingBaseInfo.getRegionalDivision()));
        } else {
            regionalClassify = RegionalClassify.getProjRegionalClassify(projPricingBaseInfo.getRegionalProjectClassify());
        }
        if (Objects.isNull(regionalClassify)) {
            log.error("无法确定FTP地区分类，使用其他地区进行兜底[{}]", projPricingBaseInfo.getProjCode());
            regionalClassify = RegionalClassify.OTHER;
        }
        renderMap.put(RenderParameterKeyHolder.REGIONAL_PROJECT_CLASSIFY, regionalClassify.display());
//        if ("浙江地区".equals(renderMap.get(RenderParameterKeyHolder.REGIONAL_PROJECT_CLASSIFY))) {
//            ftpQuarterlyBasePricingBO.setRegionalClassify("ZHEJIANG");
//        } else if ("鼓励支持类地区".equals(renderMap.get(RenderParameterKeyHolder.REGIONAL_PROJECT_CLASSIFY))) {
//            ftpQuarterlyBasePricingBO.setRegionalClassify("ENCOURAGE");
//        } else {
//            ftpQuarterlyBasePricingBO.setRegionalClassify("OTHER");
//        }
        ftpQuarterlyBasePricingBO.setRegionalClassify(regionalClassify.name());

        //获取项目承租人
        List<Long> projPricingClientIds = getProjPricingClientIds(projPricingBaseInfo);
        Set<String> enterpriseNatureSet = corpCommerceInfoService.list(projPricingClientIds).stream().map(CorpCommerceInfo::getEnterpriseNature).collect(Collectors.toSet());
        //企业性质
        CustomerEntityClassify customerEntityClassify = EnterpriseNatureEnum.changeProjReviewDisplay(enterpriseNatureSet);
        if (Objects.isNull(customerEntityClassify)) {
            log.error("无法确定FTP客户主体分类，使用其他进行兜底[{}]", projPricingBaseInfo.getProjCode());
            customerEntityClassify = CustomerEntityClassify.OTHER;
        }
        renderMap.put(RenderParameterKeyHolder.ENTERPRISE_NATURE, customerEntityClassify.display());
        ftpQuarterlyBasePricingBO.setCustomerEntityClassify(customerEntityClassify.name());
        //资产行业分类
        ProjectClassify projectClassify = ProjectClassify.find(projPricingBaseInfo.getProjectClassify());
        renderMap.put(RenderParameterKeyHolder.PROJECT_CLASSIFY, Optional.ofNullable(projectClassify).map(ProjectClassify::display).orElse(""));
        ftpQuarterlyBasePricingBO.setAssetIndustryClassify(Optional.ofNullable(AssetIndustryClassify.getByProjectClassify(projectClassify)).map(AssetIndustryClassify::name).orElse(""));
        //季度项目最低收益率
        //这里王振说协同取最优
        if (ProjectClassify.INTRA_GROUP_COLLABORATION.name().equals(projPricingBaseInfo.getProjectClassify())) {
            ftpQuarterlyBasePricingBO.setAssetIndustryClassify(AssetIndustryClassify.ENCOURAGE_INTERVENTION.name());
            ftpQuarterlyBasePricingBO.setRegionalClassify("ZHEJIANG");
            ftpQuarterlyBasePricingBO.setCustomerEntityClassify(CustomerEntityClassify.LISTED_COMPANY.name());
        } else if (ProjectClassify.CONSTRUCTION_MACHINERY.name().equals(projPricingBaseInfo.getProjectClassify())) {
            ftpQuarterlyBasePricingBO.setAssetIndustryClassify(AssetIndustryClassify.ENCOURAGE_INTERVENTION.name());
        }
        renderMap.put(RenderParameterKeyHolder.QUARTERLY_MINIMUM_YIELD, LongUtil.tenThousand2Dollar(String.valueOf(ftpService.getFtpQuarterlyBasePricing(ftpQuarterlyBasePricingBO))).setScale(2, RoundingMode.HALF_UP).toPlainString());
        BigDecimal ratePercent = LongUtil.tenThousand2Dollar(String.valueOf(priceDetail.getRatePercent())).setScale(2, RoundingMode.HALF_UP);
        renderMap.put(RenderParameterKeyHolder.RATE_PERCENT, ratePercent.toPlainString());
        //折现率
        BigDecimal discountRate = getCashFlowBOS(projPricingRenderBO);
        renderMap.put(RenderParameterKeyHolder.DISCOUNT_RATE, discountRate);
        //构建参数
        BigDecimal cashFtp = null;
        //todo 代码报错，先捕捉一下
        List<PaymentBaseInfo> paymentBaseInfoList = paymentBaseInfoService.listEffectPaymentByContractId(projPricingBaseInfo.getClientId());
        Set<Long> paymentIdList = null;
        if (!CollectionUtil.isEmpty(paymentBaseInfoList)) {
            paymentIdList = paymentBaseInfoList.stream().map(PaymentBaseInfo::getContractId).collect(Collectors.toSet());
        }
        try {
            cashFtp = LongUtil.tenThousand2Dollar(String.valueOf(ftpService.getCashFtp(getCashFtpInfluenceBO(projPricingBaseInfo, priceDetail, corpCommerceInfo, ftpQuarterlyBasePricingBO.getRegionalClassify()), paymentIdList))).setScale(2, RoundingMode.HALF_UP);
            renderMap.put(RenderParameterKeyHolder.CASH_FTP, cashFtp.toPlainString());
            renderMap.put(RenderParameterKeyHolder.RATE_PERCENT_COMPARE_COMPARE_CASH_FTP, discountRate.compareTo(cashFtp) > 0 ? "高" : ratePercent.compareTo(cashFtp) < 0 ? "低" : "等");
        } catch (Exception e) {
            log.error("生成项目收益率审查意见书中的FTP数据异常", e);
        }
        ProjectBizTypeBO projectBizTypeBO = new ProjectBizTypeBO();
        projectBizTypeBO.setProjectBizType(projPricingBaseInfo.getBizType());
        if (StrUtil.isNotBlank(projPricingBaseInfo.getLeaseTypes())) {
            projectBizTypeBO.setLeaseTypeList(JSONUtil.toList(projPricingBaseInfo.getLeaseTypes(), String.class));
        } else {
            if (ProjectBizType.ZL.name().equals(projPricingBaseInfo.getBizType()) || ProjectBizType.ZZ.name().equals(projPricingBaseInfo.getBizType())) {
                throw new MithrasException("请先补全租赁类型");
            }
        }
        renderMap.put(RenderParameterKeyHolder.PROJECT_BIZ_TYPE, projectBizTypeBO.text());
        if (Objects.nonNull(priceDetail.getApplyCreditAmount())) {
            renderMap.put(RenderParameterKeyHolder.APPLY_CREDIT_AMOUNT_WAN, this.toWan(priceDetail.getApplyCreditAmount()));
        }
        renderMap.put(RenderParameterKeyHolder.MONTH_COUNT, priceDetail.getMonthCount());
        if (Objects.nonNull(priceDetail.getIrr())) {
            renderMap.put(RenderParameterKeyHolder.IRR, NumberUtil.div(priceDetail.getIrr().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP).toPlainString());
        }
        if (Objects.nonNull(priceDetail.getEarnestMoney())) {
            renderMap.put(RenderParameterKeyHolder.EARNEST_WAN, this.toWan(priceDetail.getEarnestMoney()));
        }
        if (Objects.nonNull(priceDetail.getRatePercent())) {
            renderMap.put(RenderParameterKeyHolder.INTEREST_RATE, NumberUtil.div(priceDetail.getRatePercent().toString(), GlobalConstants.MONEY_MULTIPLE).setScale(2, RoundingMode.HALF_UP).toPlainString());
        }
        renderMap.put(RenderParameterKeyHolder.RATE_TYPE, Optional.ofNullable(RateType.of(priceDetail.getRateType())).map(RateType::display).orElse(""));
        if (Objects.nonNull(priceDetail.getConsultingFee())) {
            renderMap.put(RenderParameterKeyHolder.CONSULTING_FEE_WAN, this.toWan(priceDetail.getConsultingFee()));
        }
        if (Objects.nonNull(priceDetail.getNominalPrice())) {
            renderMap.put(RenderParameterKeyHolder.NOMINAL_PRICE_WAN, this.toYuan(priceDetail.getNominalPrice()));
        }
        //手续费/首期利息（万元）（方法体内已做非空操作，不可能为空，就不加if语句）
        renderMap.put(RenderParameterKeyHolder.INITIAL_COST_WAN, this.toWan(priceDetail.getCommission() + priceDetail.getFirstInstallmentInterest()));
        //风控行业分类
//        if (StringUtils.isNotEmpty(corpCommerceInfo.getRiskControlIndustryClassify())) {
//            renderMap.put(RenderParameterKeyHolder.RISK_CONTROL_INDUSTRY_CLASSIFY, Objects.requireNonNull(RiskControlIndustryClassify.findByName(corpCommerceInfo.getRiskControlIndustryClassify())).display());
//        }

        renderMap.put(RenderParameterKeyHolder.PROJECT_CLASSIF, Optional.ofNullable(projectClassify).map(ProjectClassify::display).orElse("        "));
        if (Objects.nonNull(priceDetail.getApplyCreditAmount()) && Objects.nonNull(priceDetail.getEarnestMoney())) {
            BigDecimal b1 = BigDecimal.valueOf(priceDetail.getApplyCreditAmount());
            BigDecimal b2 = BigDecimal.valueOf(priceDetail.getEarnestMoney());
            BigDecimal r = b2.divide(b1, 10, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
            renderMap.put(RenderParameterKeyHolder.EARNEST_RATE, r.toPlainString());
        }
        renderMap.put(RenderParameterKeyHolder.RENT_CALCULATOR_TYPE, Optional.ofNullable(RepayCalcType.find(priceDetail.getCalculateType())).map(RepayCalcType::display).orElse(""));
        RepayRateEnum repayRateEnum = RepayRateEnum.of(priceDetail.getRepayRate());
        if (repayRateEnum == RepayRateEnum.LRREGULAR || repayRateEnum == RepayRateEnum.NON_STAGES) {
            renderMap.put(RenderParameterKeyHolder.REPAY_RATE, repayRateEnum.display);
        } else {
            if (ProjectBizType.ZL.name().equals(projPricingBaseInfo.getBizType()) || ProjectBizType.ZZ.name().equals(projPricingBaseInfo.getBizType())) {
                Integer n = this.calculateN(priceDetail.getRepayRate(), priceDetail.getMonthCount(), priceDetail.getLeasePriceDetailRSP().getRepayTimesTotal());
                renderMap.put(RenderParameterKeyHolder.REPAY_RATE, Optional.ofNullable(n).map(i -> "T+" + i).orElse("T+_月"));
            }
        }

        XWPFTemplate template = XWPFTemplate.compile(ProjPricingEarningsRateRender.class.getResourceAsStream(templatePath)).render(renderMap);
        template.writeAndClose(outputStream);

        return projPricingBaseInfo.getProjName() + "-" + ProjPricingMaterialsEnum.YIELD_REVIEW_REPORT.getDisplay() + GlobalConstants.OFFICE_WORD_SUFFIX;
    }

    @NotNull
    private BigDecimal getCashFlowBOS(ProjPricingRenderBO projPricingRenderBO) {
        //获取现金流计划
        LambdaQueryWrapper<ProjPricingCashFlowPlan> PlanQueryWrapper = Wrappers.<ProjPricingCashFlowPlan>lambdaQuery()
                .eq(Objects.nonNull(projPricingRenderBO.getProjPricingBaseInfo().getId()), ProjPricingCashFlowPlan::getProjectId, projPricingRenderBO.getProjPricingBaseInfo().getId());
        List<ProjPricingCashFlowPlan> projPricingBaseInfoList = projPricingCashFlowPlanMapper.selectList(PlanQueryWrapper);
        //获取现金流计划
        Wrapper<ProjPricingLeasePrice> priceQueryWrapper = Wrappers.<ProjPricingLeasePrice>lambdaQuery()
                .eq(Objects.nonNull(projPricingRenderBO.getProjPricingBaseInfo().getId()), ProjPricingLeasePrice::getProjectId, projPricingRenderBO.getProjPricingBaseInfo().getId());
        ProjPricingLeasePrice projPricingLeasePrice = projPricingLeasePriceMapper.selectOne(priceQueryWrapper);

        List<CashFlowBO> cashFlowBOList = projPricingBaseInfoList.stream().map(item -> {
            CashFlowBO cashFlowBO = new CashFlowBO()
                    .setCashFlowDate(item.getCashFlowDate())//日期
                    .setCashFlowPhase(item.getCashFlowPhase())//期项
                    .setRemainingPrincipal(item.getRemainingPrincipal())//剩余本金
                    .setRent(item.getRent())//租金
                    .setPrincipal(item.getPrincipal())//本金
                    .setInterest(item.getInterest());//利息

            // 最后一个期限现金流金额为：租金-保证金
            if (Objects.nonNull(projPricingLeasePrice.getRepayTimesTotal())
                    && Objects.nonNull(item.getCashFlowPhase())
                    && projPricingLeasePrice.getRepayTimesTotal().equals(item.getCashFlowPhase())) {
                cashFlowBO.setCashFlowAmount(item.getRent() - projPricingLeasePrice.getEarnestMoney());
            } else {
                cashFlowBO.setCashFlowAmount(item.getRent());
            }
            return cashFlowBO;
        }).collect(Collectors.toList());

        //0期的现金流：-1 * 授信金额 + 首期租金 + 保证金 + 手续费 + 首期利息
        Long cashFlowAmount = -1 * projPricingLeasePrice.getApplyCreditAmount()
                + Optional.ofNullable(projPricingLeasePrice.getDownPayment()).orElse(0L)
                + Optional.ofNullable(projPricingLeasePrice.getEarnestMoney()).orElse(0L)
                + Optional.ofNullable(projPricingLeasePrice.getCommission()).orElse(0L)
                + Optional.ofNullable(projPricingLeasePrice.getFirstInstallmentInterest()).orElse(0L);
        //方法需要0期限的数据
        CashFlowBO cashFlowBO = new CashFlowBO()
                .setCashFlowAmount(cashFlowAmount)
                .setCashFlowDate(projPricingLeasePrice.getPlannedStartingDate())
                .setCashFlowPhase(0);
        cashFlowBOList.add(cashFlowBO);
        //日折现率
        DailyDiscountRateCalcResultBO dailyDiscountRateCalcResultBO = FinancialUtil.calculateDailyDiscountRate(cashFlowBOList);
        //转为年折现率
        return NumberUtil.mul(dailyDiscountRateCalcResultBO.getDailyDiscountRate(), BigDecimal.valueOf(100), BigDecimal.valueOf(365)).setScale(2, RoundingMode.HALF_UP);
    }

    private CashFtpInfluenceBO getCashFtpInfluenceBO(ProjPricingBaseInfo projPricingBaseInfo, ProjPricingPriceDetailRSP priceDetail, CorpCommerceInfo corpCommerceInfo, String regionalClassify) {
        CashFtpInfluenceBO cashFtpInfluenceBO = new CashFtpInfluenceBO();
        cashFtpInfluenceBO.setJudgePledge(false);
        cashFtpInfluenceBO.setBizType(projPricingBaseInfo.getBizType());
        cashFtpInfluenceBO.setContractMonthCount(priceDetail.getMonthCount());
        cashFtpInfluenceBO.setRiskControlIndustryClassify(corpCommerceInfo.getRiskControlIndustryClassify());
        cashFtpInfluenceBO.setEnterpriseNature(corpCommerceInfo.getEnterpriseNature());
        cashFtpInfluenceBO.setAssetIndustryClassify(projPricingBaseInfo.getProjectClassify());
        cashFtpInfluenceBO.setRegionClassify(projPricingBaseInfo.getRegionalProjectClassify());
        cashFtpInfluenceBO.setTargetDate(LocalDate.now());
        cashFtpInfluenceBO.setZhejiang("ZHEJIANG".equals(regionalClassify));
        // 如果项目评审-行业分类是特定的两种类型则特殊处理
        if (Objects.equals(projPricingBaseInfo.getProjectClassify(), ProjectClassify.CONSTRUCTION_MACHINERY.name())) {
//            cashFtpInfluenceBO.setRiskControlIndustryClassify(RiskControlIndustryClassify.ENGINEERING_MACHINERY.name());
            cashFtpInfluenceBO.setAssetIndustryClassify(null);
        } else if (Objects.equals(projPricingBaseInfo.getProjectClassify(), ProjectClassify.INTRA_GROUP_COLLABORATION.name())) {
            cashFtpInfluenceBO.setRiskControlIndustryClassify(RiskControlIndustryClassify.INTRA_GROUP_COLLABORATION.name());
            cashFtpInfluenceBO.setAssetIndustryClassify(null);
        }
        return cashFtpInfluenceBO;
    }

    private List<Long> getProjPricingClientIds(ProjPricingBaseInfo projPricingBaseInfo) {
        ProjectBizType projectBizType = ProjectBizType.of(projPricingBaseInfo.getBizType());
        List<Long> clientIds = new ArrayList<>();
        switch (projectBizType) {
            case ZZ:
            case ZL:
                clientIds = JSONArray.parseArray(projPricingBaseInfo.getLesseeInfo()).toJavaList(ClientInfo.class).stream().map(ClientInfo::getClientId).collect(Collectors.toList());
                break;
            case BL:
            case ZR:
                clientIds = JSONArray.parseArray(projPricingBaseInfo.getCreditorInfo()).toJavaList(ClientInfo.class).stream().map(ClientInfo::getClientId).collect(Collectors.toList());
                break;
        }
        if (ObjectUtil.isNotEmpty(projPricingBaseInfo.getGuaranteeInfo())) {
            clientIds.addAll(JSONArray.parseArray(projPricingBaseInfo.getGuaranteeInfo()).toJavaList(ClientInfo.class).stream().map(ClientInfo::getClientId).collect(Collectors.toList()));
        }
        return clientIds;
    }

    private TermRange getTerm(Integer month) {
        if (month <= 12) {
            return TermRange.ONE_YEAR;
        } else if (month > 36) {
            return TermRange.MORE_THAN_THREE_YEARS;
        } else {
            return TermRange.ONE_TO_THREE_YEARS;
        }
    }

    private static class RenderParameterKeyHolder {
        // 日期
        private static final String REPORT_DATE = "reportDate";
        // 项目名称
        private static final String PROJ_NAME = "projName";
        // 业务部门
        private static final String BIZ_DEPT = "bizDept";
        // 项目分类
        public static final String PROJECT_CLASSIF = "projectClassif";
        // 企业性质
        public static final String ENTERPRISE_NATURE = "enterpriseNature";
        // 业务类型
        public static final String PROJECT_BIZ_TYPE = "projectBizType";
        // 申请授信金额，万元
        private static final String APPLY_CREDIT_AMOUNT_WAN = "applyCreditAmountW";
        // 融资期限
        private static final String MONTH_COUNT = "monthCount";
        // 收益率
        private static final String IRR = "irr";
        // 保证金，万元
        private static final String EARNEST_WAN = "earnestW";
        // 合同利率
        private static final String INTEREST_RATE = "interestRate";
        // 利率类型
        public static final String RATE_TYPE = "rateType";
        // 服务费，万元
        private static final String CONSULTING_FEE_WAN = "consultingFeeW";
        // 名义价款，元
        private static final String NOMINAL_PRICE_WAN = "nominalPriceW";
        // 保证金率，保证金/申请授信 * 100%，保留2位小数
        private static final String EARNEST_RATE = "earnestRate";
        // 支付频率
        public static final String REPAY_RATE = "repayRate";
        // 租金支付方式
        public static final String RENT_CALCULATOR_TYPE = "rentCalculatorType";
        //期限 1年、1-3年、3年以上
        private static final String TERM = "term";
        //地区分类
        private static final String REGIONAL_PROJECT_CLASSIFY = "regionalProjectClassify";
        //资产行业分类
        private static final String PROJECT_CLASSIFY = "projectClassify";
        //季度项目最低收益率
        private static final String QUARTERLY_MINIMUM_YIELD = "quarterlyMinimumYield";
        //租金利率
        private static final String RATE_PERCENT = "ratePercent";
        //租金利率与月度 FTP 指导定价前后两者进行比较，前者大则为高，以此类推
        private static final String RATE_PERCENT_COMPARE_COMPARE_CASH_FTP = "ratePercentCompareCashFtp";
        //月度 FTP 指导定价
        private static final String CASH_FTP = "cashFtp";
        //手续费/首期利息（万元）
        public static final String INITIAL_COST_WAN = "InitialCost";
        //折现率
        public static final String DISCOUNT_RATE = "discountRate";
//        //风控行业分类
//        public static final String RISK_CONTROL_INDUSTRY_CLASSIFY = "riskControlIndustryClassify";

    }
}
