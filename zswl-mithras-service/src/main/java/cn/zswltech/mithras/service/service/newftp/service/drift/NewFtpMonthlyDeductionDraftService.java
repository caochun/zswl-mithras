package cn.zswltech.mithras.service.service.newftp.service.drift;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.newftp.NewFtpMonthlyDeductionListREQ;
import cn.zswltech.mithras.dto.newftp.NewFtpMonthlyDeductionListRSP;
import cn.zswltech.mithras.dto.newftp.NewFtpMonthlyDeductionModifyREQ;
import cn.zswltech.mithras.service.constant.ResultMsg;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.service.enums.newftp.*;
import cn.zswltech.mithras.ftp.newftp.enums.*;
import cn.zswltech.mithras.service.mapper.model.basedata.BaseDataLpr;
import cn.zswltech.mithras.service.others.AuthCheckException;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.others.SpringContextHolder;
import cn.zswltech.mithras.service.service.basedata.BaseDataLprService;
import cn.zswltech.mithras.ftp.newftp.convert.NewFtpMonthlyDeductionDraftConverter;
import cn.zswltech.mithras.ftp.newftp.fms.DefaultNewFtpStateMachine;
import cn.zswltech.mithras.ftp.newftp.fms.NewFtpContext;
import cn.zswltech.mithras.ftp.newftp.fms.NewFtpEvent;
import cn.zswltech.mithras.ftp.newftp.mapper.draft.NewFtpMonthlyDeductionDraftMapper;
import cn.zswltech.mithras.ftp.newftp.enums.TermRange;
import cn.zswltech.mithras.ftp.newftp.model.NewFtpBaseInfo;
import cn.zswltech.mithras.ftp.newftp.model.config.NewFtpFinancingCostPricingConfig;
import cn.zswltech.mithras.ftp.newftp.model.config.NewFtpGuaranteeCostPricingConfig;
import cn.zswltech.mithras.ftp.newftp.model.config.NewFtpParameterSettingConfig;
import cn.zswltech.mithras.ftp.newftp.model.config.NewFtpTreasuryBondYieldPricingConfig;
import cn.zswltech.mithras.ftp.newftp.model.draft.*;
import cn.zswltech.mithras.ftp.newftp.service.drift.NewFtpDescriptionTextDraftService;
import cn.zswltech.mithras.ftp.newftp.service.drift.NewFtpParameterSettingDraftService;
import cn.zswltech.mithras.ftp.newftp.service.drift.NewFtpShiborInterestRatePricingDraftService;
import cn.zswltech.mithras.ftp.newftp.service.drift.NewFtpTreasuryBondYieldPricingDraftService;
import cn.zswltech.mithras.service.service.newftp.service.NewFtpBaseInfoService;
import cn.zswltech.mithras.service.service.newftp.service.config.NewFtpFinancingCostPricingConfigService;
import cn.zswltech.mithras.service.service.newftp.service.config.NewFtpGuaranteeCostPricingConfigService;
import cn.zswltech.mithras.ftp.newftp.service.config.NewFtpParameterSettingConfigService;
import cn.zswltech.mithras.ftp.newftp.service.config.NewFtpTreasuryBondYieldConfigService;
import cn.zswltech.mithras.ftp.newftp.utils.DateUtil;
import cn.zswltech.mithras.service.util.BigDecimalUtil;
import cn.zswltech.mithras.service.util.FlowUtil;
import cn.zswltech.mithras.service.util.LongUtil;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhaozhengkang
 * @description 月度计价指导
 * @date 2023-05-21
 */
@Service
public class NewFtpMonthlyDeductionDraftService extends ServiceImpl<NewFtpMonthlyDeductionDraftMapper, NewFtpMonthlyDeductionDraft> {

    @Resource
    private NewFtpParameterSettingConfigService parameterSettingConfigService;
    @Resource
    private NewFtpParameterSettingDraftService parameterSettingDraftService;
    @Resource
    private NewFtpFinancingCostPricingDraftService financingCostPricingDraftService;
    @Resource
    private NewFtpFinancingCostPricingConfigService financingCostPricingConfigService;
    @Resource
    private NewFtpGuaranteeCostPricingDraftService guaranteeCostPricingDraftService;
    @Resource
    private NewFtpGuaranteeCostPricingConfigService guaranteeCostPricingConfigService;
    @Resource
    private NewFtpShiborInterestRatePricingDraftService shiborInterestRatePricingDraftService;
    @Resource
    private NewFtpTreasuryBondYieldPricingDraftService treasuryBondYieldPricingDraftService;
    @Resource
    private NewFtpMonthlyDeductionDraftConverter baseConverter;
    @Resource
    private NewFtpBaseInfoService baseInfoService;
    @Resource
    private DefaultNewFtpStateMachine defaultNewFtpStateMachine;
    @Resource
    private BaseDataLprService baseDataLprService;
    @Resource
    private NewFtpLprPricingDraftService lprPricingDraftService;

    @Transactional(rollbackFor = Throwable.class)
    public void add(LocalDate month, Long mainId) {
        List<BaseDataLpr> lprPricingList = baseDataLprService.list();

        //基础数据
        NewFtpBaseInfo newFtpBaseInfo = baseInfoService.getById(mainId);
        //因为叶芳说每次都要重新计算，所以直接用同一套配置
        List<NewFtpParameterSettingConfig> parameterSettingConfigs = parameterSettingConfigService.list();
        Map<String, List<NewFtpParameterSettingConfig>> paramMap = new HashMap<>(8);
        if (CollUtil.isNotEmpty(parameterSettingConfigs)) {
            paramMap = parameterSettingConfigs.stream().collect(Collectors.groupingBy(NewFtpParameterSettingConfig::getCategory));
        }

        NewFtpMonthlyDeductionDraft oneYear = new NewFtpMonthlyDeductionDraft();
        oneYear.setFtpId(mainId);
        oneYear.setTermRange(TermRange.ONE_YEAR.name());
        // 处理param的初始化

        // 1 权重 Key = FINANCIAL_MARKET_VOLATILITY
        Map<String, NewFtpParameterSettingConfig> weightMap = paramMap.get(ParamCategory.FINANCIAL_MARKET_VOLATILITY.name())
                .stream().collect(Collectors.toMap(NewFtpParameterSettingConfig::getParamName, Function.identity(), (a, b) -> a));
        oneYear.setTreasuryBondYieldWeight(weightMap.get("10年期国债收益率").getValue());
        oneYear.setShiborRateWeight(weightMap.get("1年期shibor利率").getValue());
        oneYear.setLprRateWeight(weightMap.get("同期LPR利率").getValue());
        oneYear.setFinancingCostTrendsWeight(weightMap.get("融资成本趋势").getValue());

        // 2 资产行业计价 key = INDUSTRY_ASSET_VALUATION
        Map<String, NewFtpParameterSettingConfig> assetPricing = paramMap.get(ParamCategory.INDUSTRY_ASSET_VALUATION.name())
                .stream().collect(Collectors.toMap(NewFtpParameterSettingConfig::getParamName, Function.identity(), (a, b) -> a));
        oneYear.setAssetEncourage(assetPricing.get("其他产业类-鼓励介入类").getValue());
        oneYear.setAssetModerate(assetPricing.get("其他产业类-适度支持类").getValue());
        oneYear.setAssetCautious(assetPricing.get("其他产业类-谨慎支持类").getValue());
        oneYear.setAssetPublic(assetPricing.get("公共事业类").getValue());
        oneYear.setAssetCivil(assetPricing.get("民生消费类").getValue());
        oneYear.setAssetStateOwned(assetPricing.get("国有产业类").getValue());

        // 3 产业类-地区分类计价 key = REGIONAL_CLASSIFICATION_VALUATION
        Map<String, NewFtpParameterSettingConfig> regional = paramMap.get(ParamCategory.REGIONAL_CLASSIFICATION_VALUATION.name())
                .stream().collect(Collectors.toMap(NewFtpParameterSettingConfig::getParamName, Function.identity(), (a, b) -> a));
        oneYear.setIndustryRegionZhejiang(regional.get("其他-浙江地区").getValue());
        oneYear.setIndustryRegionEncourage(regional.get("其他-鼓励支持类地区（除浙江）").getValue());
        oneYear.setIndustryRegionOther(regional.get("其他-其他地区").getValue());

        // 4 产业类-客户主体计价 key = INDUSTRY_CUSTOMER_VALUATION
        Map<String, NewFtpParameterSettingConfig> customer = paramMap.get(ParamCategory.INDUSTRY_CUSTOMER_VALUATION.name())
                .stream().collect(Collectors.toMap(NewFtpParameterSettingConfig::getParamName, Function.identity(), (a, b) -> a));
//        oneYear.setCustomerListed(customer.get("上市公司").getValue());
//        oneYear.setCustomerStateOwned(customer.get("国有企业").getValue());
        oneYear.setCustomerListedStateOwned(customer.get("上市公司/国有企业").getValue());
        oneYear.setCustomerOtherListed(customer.get("其他上市公司").getValue());
        oneYear.setCustomerOther(customer.get("其他").getValue());

        // 5 公共事业类-地区分类计价
        oneYear.setPublicUtilitiesRegionZhejiang(regional.get("公共事业类-浙江地区").getValue());
        oneYear.setPublicUtilitiesRegionEncourage(regional.get("公共事业类-鼓励支持类地区（除浙江）").getValue());
        oneYear.setPublicUtilitiesRegionOther(regional.get("公共事业类-其他地区").getValue());

        // 6 民生消费类-地区分类计价
        oneYear.setCivilConsumptionRegionZhejiang(regional.get("民生消费类-浙江地区").getValue());
        oneYear.setCivilConsumptionRegionEncourage(regional.get("民生消费类-鼓励支持类地区（除浙江）").getValue());
        oneYear.setCivilConsumptionRegionOther(regional.get("民生消费类-其他地区").getValue());

        // 7 国有产业类-地区分类计价
        oneYear.setStateOwnedIndustryRegionZhejiang(regional.get("国有产业类-浙江地区").getValue());
        oneYear.setStateOwnedIndustryRegionEncourage(regional.get("国有产业类-鼓励支持类地区（除浙江）").getValue());
        oneYear.setStateOwnedIndustryRegionOther(regional.get("国有产业类-其他地区").getValue());

        //查询编辑区
        Map<String, NewFtpFinancingCostPricingDraft> financingCostPricingDraftMap = financingCostPricingDraftService.list(Wrappers.<NewFtpFinancingCostPricingDraft>lambdaQuery()
                        .eq(NewFtpFinancingCostPricingDraft::getFtpId, mainId)
                        .eq(NewFtpFinancingCostPricingDraft::getMonth, newFtpBaseInfo.getMonth().minusMonths(1)))
                .stream().collect(Collectors.toMap(NewFtpFinancingCostPricingDraft::getTermRange, Function.identity(), (a, b) -> a));

        //融资成本：参考当年新增融资加权平均成本，本表数据参考<1>-<上月> 平均水平 （备注：如遇1月份，则<1>-<上月>需改为<去年全年>
        setFinancingCost(financingCostPricingDraftMap, oneYear, TermRange.ONE_YEAR, newFtpBaseInfo);

        //担保成本：从【维护基础数据】中获取上月末所对应的值
        NewFtpGuaranteeCostPricingDraft draftServiceOne = guaranteeCostPricingDraftService.getOne(Wrappers.<NewFtpGuaranteeCostPricingDraft>lambdaQuery()
                .eq(NewFtpGuaranteeCostPricingConfig::getMonth, month.minusMonths(1))
                .last(StringUtil.mysqlLimitOne()));
        if (ObjectUtil.isNull(draftServiceOne)) {
            NewFtpGuaranteeCostPricingConfig configServiceOne = guaranteeCostPricingConfigService.getOne(Wrappers.<NewFtpGuaranteeCostPricingConfig>lambdaQuery()
                    .eq(NewFtpGuaranteeCostPricingConfig::getMonth, month.minusMonths(1))
                    .last(StringUtil.mysqlLimitOne()));
            if (ObjectUtil.isNull(configServiceOne)) {
                throw new MithrasException(String.format("请前往基础数据维护%s月的担保成本数据！", month.minusMonths(1).getMonth().getValue()));
            }
            draftServiceOne = BeanUtil.copyProperties(configServiceOne, NewFtpGuaranteeCostPricingDraft.class);
        }
        oneYear.setGuaranteeCost(draftServiceOne.getCurrentAverage());
        //系统从【维护基础数据】中获取上月末所对应的值。（备注：原可取【融资管理】中的担保费率，但因为生产中数据有误，存在大量0.45%，实际业务中，担保费率固定为千五，所以本次上线，先固定0.5%）
        NewFtpGuaranteeCostPricingDraft guaranteeCostPricingDraftServiceOne = guaranteeCostPricingDraftService.getOne(Wrappers.<NewFtpGuaranteeCostPricingDraft>lambdaQuery()
                .eq(NewFtpGuaranteeCostPricingDraft::getFtpId, newFtpBaseInfo.getId())
                .eq(NewFtpGuaranteeCostPricingDraft::getMonth, newFtpBaseInfo.getMonth().minusMonths(1))
                .last(StringUtil.mysqlLimitOne()));
        if (ObjectUtil.isNull(guaranteeCostPricingDraftServiceOne)) {
            throw new MithrasException(String.format("%s的担保成本数据不存在，请前往基础数据维护", newFtpBaseInfo.getMonth().minusMonths(1)));
        }
        oneYear.setGuaranteeCost(guaranteeCostPricingDraftServiceOne.getCurrentAverage());

        //十年期国债收益率波动水平：上季度收益率平均值与上上季度收益率平均值的差值
        setTreasuryBondYield(newFtpBaseInfo, oneYear);

        //上季度 1 年期同业拆借利率平均值与上上季度 1 年期同业拆借利率平均值的差值
        setShiborRate(oneYear, newFtpBaseInfo);

        //上季度 LPR 利率平均值与上上季度 LPR 利率平均值的差值
        //上季度
        LocalDate monthNow = newFtpBaseInfo.getMonth();
        List<BaseDataLpr> lastQuarterList = lprPricingList.stream()
                .filter(a -> a.getLprDate().isAfter(DateUtil.getQuarterBegin(monthNow.minusMonths(3))))
                .filter(a -> a.getLprDate().isBefore(DateUtil.getQuarterEnd(monthNow.minusMonths(3))))
                .collect(Collectors.toList());
        //上上季度
        List<BaseDataLpr> lastLastQuarterList = lprPricingList.stream()
                .filter(a -> a.getLprDate().isAfter(DateUtil.getQuarterBegin(monthNow.minusMonths(6))))
                .filter(a -> a.getLprDate().isBefore(DateUtil.getQuarterEnd(monthNow.minusMonths(6))))
                .collect(Collectors.toList());

        if (ObjectUtil.isEmpty(lastQuarterList) || ObjectUtil.isEmpty(lastLastQuarterList)) {
            throw new MithrasException("请前往维护基础数据-LPR利率后再创建!");
        }
        AtomicReference<BigDecimal> last = new AtomicReference<>(BigDecimal.ZERO);
        AtomicReference<BigDecimal> lastLast = new AtomicReference<>(BigDecimal.ZERO);
        lastQuarterList.forEach(lastQuarter -> last.set(last.get().add(new BigDecimal(lastQuarter.getOneYear()))));
        lastLastQuarterList.forEach(lastQuarter -> lastLast.set(lastLast.get().add(new BigDecimal(lastQuarter.getOneYear()))));
        BigDecimal lprRate = last.get().divide(BigDecimal.valueOf(lastQuarterList.size()), 10, RoundingMode.HALF_UP)
                .subtract(lastLast.get().divide(BigDecimal.valueOf(lastLastQuarterList.size()), 10, RoundingMode.HALF_UP));

        Map<String, NewFtpParameterSettingConfig> settingConfigMap = parameterSettingConfigService.list(Wrappers.<NewFtpParameterSettingConfig>lambdaQuery()
                        .eq(NewFtpParameterSettingConfig::getCategory, ParamCategory.VALUATION_STANDARDS_FOR_FINANCIAL_MARKET_VOLATILITY.name()))
                .stream().collect(Collectors.toMap(NewFtpParameterSettingConfig::getParamName, Function.identity(), (a, b) -> a));

        NewFtpParameterSettingConfig settingConfig = settingConfigMap.get("LPR波动计价标准");
        if (ObjectUtil.isNull(settingConfig)) {
            throw new MithrasException("LPR波动计价标准不存在");
        }
        BigDecimal multiplicand = BigDecimal.valueOf(10000);
        oneYear.setLprRate(FluctuationValueEnum.DIRECT.name().equals(settingConfig.getParamOtherName()) ?
                lprRate.multiply(multiplicand).intValue() :
                SpringContextHolder.getBean(NewFtpTreasuryBondYieldConfigService.class)
                        .analyticalFormula(settingConfig, lprRate).multiply(multiplicand).intValue());

        //指公司上季度新增融资加权平均成本较当年同期限加权平均融资成本的波动趋势，当高于当年平均值时，则成本趋势波动水平定价 0.3%，反之则定价-0.3%
        NewFtpFinancingCostPricingDraft costPricingDraft = financingCostPricingDraftMap.get(TermRange.ONE_YEAR.name());
        oneYear.setFinancingCostTrends(setFinancingCostTrends(costPricingDraft, newFtpBaseInfo.getPricingFrequency()));

        List<NewFtpMonthlyDeductionDraft> deductionList = new ArrayList<>();
        deductionList.add(oneYear);

        // 创建1-3年的数据
        NewFtpMonthlyDeductionDraft oneToThreeYears = new NewFtpMonthlyDeductionDraft();
        BeanUtils.copyProperties(oneYear, oneToThreeYears);
        AtomicReference<BigDecimal> threeLast = new AtomicReference<>(BigDecimal.ZERO);
        AtomicReference<BigDecimal> threeLastLast = new AtomicReference<>(BigDecimal.ZERO);
        lastQuarterList.forEach(lastQuarter -> threeLast.set(threeLast.get().add(new BigDecimal(lastQuarter.getFiveYear()))));
        lastLastQuarterList.forEach(lastQuarter -> threeLastLast.set(threeLastLast.get().add(new BigDecimal(lastQuarter.getFiveYear()))));
        NewFtpParameterSettingConfig settingConfig1 = settingConfigMap.get("LPR波动计价标准");
        if (ObjectUtil.isNull(settingConfig)) {
            throw new MithrasException("LPR波动计价标准不存在");
        }
        BigDecimal lprRate1 = threeLast.get()
                .divide(BigDecimal.valueOf(lastQuarterList.size()), 10, RoundingMode.HALF_UP)
                .subtract(threeLastLast.get().divide(BigDecimal.valueOf(lastLastQuarterList.size()), 10, RoundingMode.HALF_UP));
        oneToThreeYears.setLprRate(FluctuationValueEnum.DIRECT.name().equals(settingConfig.getParamOtherName()) ?
                lprRate1.multiply(multiplicand).intValue() :
                SpringContextHolder.getBean(NewFtpTreasuryBondYieldConfigService.class)
                        .analyticalFormula(settingConfig1, lprRate1)
                        .multiply(multiplicand).intValue());
        setFinancingCost(financingCostPricingDraftMap, oneToThreeYears, TermRange.ONE_TO_THREE_YEARS, newFtpBaseInfo);
        NewFtpFinancingCostPricingDraft pricingDraft = financingCostPricingDraftMap.get(TermRange.ONE_TO_THREE_YEARS.name());
        oneToThreeYears.setFinancingCostTrends(setFinancingCostTrends(pricingDraft, newFtpBaseInfo.getPricingFrequency()));
        oneToThreeYears.setTermRange(TermRange.ONE_TO_THREE_YEARS.name());
        deductionList.add(oneToThreeYears);

        // 创建3年以上的数据
        NewFtpMonthlyDeductionDraft overThreeYears = new NewFtpMonthlyDeductionDraft();
        BeanUtils.copyProperties(oneToThreeYears, overThreeYears);
        NewFtpFinancingCostPricingDraft pricingDraft1 = financingCostPricingDraftMap.get(TermRange.MORE_THAN_THREE_YEARS.name());
        setFinancingCost(financingCostPricingDraftMap, overThreeYears, TermRange.MORE_THAN_THREE_YEARS, newFtpBaseInfo);
        overThreeYears.setFinancingCostTrends(setFinancingCostTrends(pricingDraft1, newFtpBaseInfo.getPricingFrequency()));
        overThreeYears.setTermRange(TermRange.MORE_THAN_THREE_YEARS.name());
        deductionList.add(overThreeYears);

        for (NewFtpMonthlyDeductionDraft deduction : deductionList) {
            // 计算小计
            deduction.subtotal();
//            Integer subtotalCost = LongUtil.null2zero(deduction.getGuaranteeCost()) + LongUtil.null2zero(deduction.getFinancingCost());
//            deduction.setSubtotalCost(subtotalCost);
//            // 金融市场波动计价-小计 判空
//            BigDecimal lpr = BigDecimalUtil.valueOf(deduction.getLprRateWeight()).multiply(BigDecimalUtil.valueOf(deduction.getLprRate()));
//            BigDecimal shibor = BigDecimalUtil.valueOf(deduction.getShiborRateWeight()).multiply(BigDecimalUtil.valueOf(deduction.getShiborRate()));
//            BigDecimal treasuryBond = BigDecimalUtil.valueOf(deduction.getTreasuryBondYieldWeight()).multiply(BigDecimalUtil.valueOf(deduction.getTreasuryBondYield()));
//            BigDecimal financeTrends = BigDecimalUtil.valueOf(deduction.getFinancingCostTrendsWeight()).multiply(BigDecimalUtil.valueOf(deduction.getFinancingCostTrends()));
//            BigDecimal subtotalRate = lpr.add(shibor).add(treasuryBond).add(financeTrends).divide(BigDecimalUtil.valueOf(1000000), 0, RoundingMode.HALF_UP);
//            deduction.setSubtotalRate(subtotalRate.intValue());
        }
        saveBatch(deductionList);
    }

    private void setShiborRate(NewFtpMonthlyDeductionDraft draft, NewFtpBaseInfo baseInfo) {
        //上季度
        List<NewFtpShiborInterestRatePricingDraft> lastQuarterList = shiborInterestRatePricingDraftService.list(Wrappers.<NewFtpShiborInterestRatePricingDraft>lambdaQuery()
                .between(NewFtpShiborInterestRatePricingDraft::getMonth,
                        DateUtil.getQuarterBegin(baseInfo.getMonth().minusMonths(3)),
                        DateUtil.getQuarterEnd(baseInfo.getMonth().minusMonths(3)))
                .eq(NewFtpShiborInterestRatePricingDraft::getFtpId, baseInfo.getId()));
        //上上季度
        List<NewFtpShiborInterestRatePricingDraft> lastLastQuarterList = shiborInterestRatePricingDraftService.list(Wrappers.<NewFtpShiborInterestRatePricingDraft>lambdaQuery()
                .between(NewFtpShiborInterestRatePricingDraft::getMonth,
                        DateUtil.getQuarterBegin(baseInfo.getMonth().minusMonths(6)),
                        DateUtil.getQuarterEnd(baseInfo.getMonth().minusMonths(6)))
                .eq(NewFtpShiborInterestRatePricingDraft::getFtpId, baseInfo.getId()));

        if (ObjectUtil.isEmpty(lastQuarterList) || ObjectUtil.isEmpty(lastLastQuarterList)) {
            throw new MithrasException("请前往维护基础数据-1年期shibor利率后再创建!");
        }

        BigDecimal last = BigDecimal.valueOf(lastQuarterList.stream().mapToLong(NewFtpShiborInterestRatePricingDraft::getAverage).sum())
                .divide(BigDecimal.valueOf(lastQuarterList.size()), 10, RoundingMode.HALF_UP);
        BigDecimal lastLast = BigDecimal.valueOf(lastLastQuarterList.stream().mapToLong(NewFtpShiborInterestRatePricingDraft::getAverage).sum())
                .divide(BigDecimal.valueOf(lastLastQuarterList.size()), 10, RoundingMode.HALF_UP);
        Map<String, NewFtpParameterSettingConfig> settingConfigMap = parameterSettingConfigService.list(Wrappers.<NewFtpParameterSettingConfig>lambdaQuery()
                        .eq(NewFtpParameterSettingConfig::getCategory, ParamCategory.VALUATION_STANDARDS_FOR_FINANCIAL_MARKET_VOLATILITY.name()))
                .stream().collect(Collectors.toMap(NewFtpParameterSettingConfig::getParamName, Function.identity(), (a, b) -> a));

        NewFtpParameterSettingConfig settingConfig = settingConfigMap.get("一年期shibor利率波动计价标准");
        if (ObjectUtil.isNull(settingConfig)) {
            throw new MithrasException("一年期shibor利率波动计价标准不存在");
        }
        BigDecimal shiborRate = last.subtract(lastLast);
        BigDecimal divisor = BigDecimal.valueOf(10000);
        draft.setShiborRate(FluctuationValueEnum.DIRECT.name().equals(settingConfig.getParamOtherName()) ? shiborRate.intValue() :
                SpringContextHolder.getBean(NewFtpTreasuryBondYieldConfigService.class)
                        .analyticalFormula(settingConfig, shiborRate.divide(divisor, 10, RoundingMode.HALF_UP))
                        .multiply(divisor).intValue());
    }

    private void setTreasuryBondYield(NewFtpBaseInfo newFtpBaseInfo, NewFtpMonthlyDeductionDraft oneYear) {
        //上季度
        List<NewFtpTreasuryBondYieldPricingDraft> lastQuarterList = treasuryBondYieldPricingDraftService.list(Wrappers.<NewFtpTreasuryBondYieldPricingDraft>lambdaQuery()
                .between(NewFtpTreasuryBondYieldPricingDraft::getMonth,
                        DateUtil.getQuarterBegin(newFtpBaseInfo.getMonth().minusMonths(3)),
                        DateUtil.getQuarterEnd(newFtpBaseInfo.getMonth().minusMonths(3)))
                .eq(NewFtpTreasuryBondYieldPricingDraft::getFtpId, newFtpBaseInfo.getId()));
        //上上季度
        List<NewFtpTreasuryBondYieldPricingDraft> lastLastQuarterList = treasuryBondYieldPricingDraftService.list(Wrappers.<NewFtpTreasuryBondYieldPricingDraft>lambdaQuery()
                .between(NewFtpTreasuryBondYieldPricingDraft::getMonth,
                        DateUtil.getQuarterBegin(newFtpBaseInfo.getMonth().minusMonths(6)),
                        DateUtil.getQuarterEnd(newFtpBaseInfo.getMonth().minusMonths(6)))
                .eq(NewFtpTreasuryBondYieldPricingDraft::getFtpId, newFtpBaseInfo.getId()));

        if (ObjectUtil.isEmpty(lastQuarterList) || ObjectUtil.isEmpty(lastLastQuarterList)) {
            throw new MithrasException("请前往维护基础数据-10年期国债收益率后再创建!");
        }
        BigDecimal last = BigDecimal.valueOf(lastQuarterList.stream().mapToLong(NewFtpTreasuryBondYieldPricingConfig::getAverage).sum())
                .divide(BigDecimal.valueOf(lastQuarterList.size()), 10, RoundingMode.HALF_UP);
        BigDecimal lastLast = BigDecimal.valueOf(lastLastQuarterList.stream().mapToLong(NewFtpTreasuryBondYieldPricingConfig::getAverage).sum())
                .divide(BigDecimal.valueOf(lastLastQuarterList.size()), 10, RoundingMode.HALF_UP);
        Map<String, NewFtpParameterSettingConfig> settingConfigMap = parameterSettingConfigService.list(Wrappers.<NewFtpParameterSettingConfig>lambdaQuery()
                        .eq(NewFtpParameterSettingConfig::getCategory, ParamCategory.VALUATION_STANDARDS_FOR_FINANCIAL_MARKET_VOLATILITY.name()))
                .stream().collect(Collectors.toMap(NewFtpParameterSettingConfig::getParamName, Function.identity(), (a, b) -> a));

        NewFtpParameterSettingConfig settingConfig = settingConfigMap.get("十年期国债收益率波动计价标准");
        if (ObjectUtil.isNull(settingConfig)) {
            throw new MithrasException("十年期国债收益率波动计价标准不存在");
        }
        BigDecimal treasuryBondYield = last.subtract(lastLast);
        BigDecimal divisor = BigDecimal.valueOf(10000);
        oneYear.setTreasuryBondYield(FluctuationValueEnum.DIRECT.name().equals(settingConfig.getParamOtherName()) ?
                treasuryBondYield.intValue() :
                SpringContextHolder.getBean(NewFtpTreasuryBondYieldConfigService.class)
                        .analyticalFormula(settingConfig, treasuryBondYield.divide(divisor, 10, RoundingMode.HALF_UP))
                        .multiply(divisor).intValue());
    }

    private static void setFinancingCost(Map<String, NewFtpFinancingCostPricingDraft> financingCostPricingMap, NewFtpMonthlyDeductionDraft monthlyDeductionDraft, TermRange termRange, NewFtpBaseInfo baseInfo) {
        //季度和月份需要分开算
        NewFtpFinancingCostPricingDraft draft = financingCostPricingMap.get(termRange.name());
        monthlyDeductionDraft.setFinancingCost(draft.getAnnualAverage());
    }

    private Integer setFinancingCostTrends(NewFtpFinancingCostPricingDraft financingCostPricing, String pricingFrequency) {
        Map<String, NewFtpParameterSettingConfig> settingConfigMap = parameterSettingConfigService.list(Wrappers.<NewFtpParameterSettingConfig>lambdaQuery()
                        .eq(NewFtpParameterSettingConfig::getCategory, ParamCategory.VALUATION_STANDARDS_FOR_FINANCIAL_MARKET_VOLATILITY.name()))
                .stream().collect(Collectors.toMap(NewFtpParameterSettingConfig::getParamName, Function.identity(), (a, b) -> a));

        NewFtpParameterSettingConfig settingConfig = settingConfigMap.get("成本趋势波动计价标准");
        if (ObjectUtil.isNull(settingConfig)) {
            throw new MithrasException("成本趋势波动计价标准不存在");
        }

        BigDecimal divisor = BigDecimal.valueOf(10000);
        if (PricingFrequencyEnum.MONTHLY.name().equals(pricingFrequency)) {
            BigDecimal i = BigDecimal.valueOf((long) Optional.ofNullable(financingCostPricing.getCurrentAverage()).orElse(0)
                    - Optional.ofNullable(financingCostPricing.getAnnualAverage()).orElse(0));
            return FluctuationValueEnum.DIRECT.name().equals(settingConfig.getParamOtherName()) ? i.intValue()
                    : SpringContextHolder.getBean(NewFtpTreasuryBondYieldConfigService.class)
                    .analyticalFormula(settingConfig, i.divide(divisor))
                    .multiply(divisor).intValue();
        }
        if (PricingFrequencyEnum.QUARTER.name().equals(pricingFrequency)) {
            BigDecimal i = BigDecimal.valueOf((long) Optional.ofNullable(financingCostPricing.getCurrentQuarterAverage()).orElse(0)
                    - Optional.ofNullable(financingCostPricing.getAnnualAverage()).orElse(0));
            return FluctuationValueEnum.DIRECT.name().equals(settingConfig.getParamOtherName()) ? i.intValue() :
                    SpringContextHolder.getBean(NewFtpTreasuryBondYieldConfigService.class)
                            .analyticalFormula(settingConfig, i.divide(divisor))
                            .multiply(divisor).intValue();
        }
        throw new MithrasException("定价频率有误，请检查！");
    }

    @Transactional(rollbackFor = Throwable.class)
    public void modify(NewFtpMonthlyDeductionModifyREQ req) {
        NewFtpMonthlyDeductionDraft originalInfo = baseMapper.selectById(req.getId());
        if (originalInfo == null) {
            throw new MithrasException(ResultMsg.RECORD_NOT_EXIST);
        }
        if (req.getLprRateWeight() + req.getTreasuryBondYieldWeight() + req.getShiborRateWeight() + req.getFinancingCostTrendsWeight() != 1000000) {
            throw new MithrasException("权重相加不为100%，请调整后再保存");
        }
        ProcessResp relatedProcess = baseInfoService.findRelatedProcess(originalInfo.getFtpId());
        if (ObjectUtil.isNotEmpty(relatedProcess)) {
            boolean isStartUserNode = FlowUtil.isStartUserNode(relatedProcess);
            if (!isStartUserNode) {
                throw new AuthCheckException("该数据处于流程中，且流程不在发起人节点，不允许修改数据");
            }
        }

        NewFtpMonthlyDeductionDraft update = baseConverter.modifyReq2Entity(req);
        update.subtotal();
        updateById(update);

        NewFtpBaseInfo baseInfo = baseInfoService.getById(originalInfo.getFtpId());
        defaultNewFtpStateMachine.execute(NewFtpContext.of(baseInfo, NewFtpEvent.MODIFY_SAVE, baseInfo.getProcessStatus()));
    }

    public List<NewFtpMonthlyDeductionListRSP> list(NewFtpMonthlyDeductionListREQ req) {
        List<NewFtpMonthlyDeductionDraft> list = list(Wrappers.<NewFtpMonthlyDeductionDraft>lambdaQuery()
                .eq(NewFtpMonthlyDeductionDraft::getFtpId, req.getMainId()));
        return baseConverter.entity2ListRsp(list);
    }


    @Transactional(rollbackFor = Throwable.class)
    public void refresh(Long ftpId) {
        NewFtpBaseInfo ftpBaseInfo = baseInfoService.getById(ftpId);
        if (ftpBaseInfo == null) {
            throw new MithrasException("ftp不存在");
        }
        remove(Wrappers.<NewFtpMonthlyDeductionDraft>lambdaQuery().eq(NewFtpMonthlyDeductionDraft::getFtpId, ftpId));
        SpringContextHolder.getBean(NewFtpMonthlyDeductionDraftService.class).add(ftpBaseInfo.getMonth(), ftpId);
    }

    @Transactional(rollbackFor = Throwable.class)
    public R<Void> addMonthlyDeduction(Long mainId) {
        NewFtpBaseInfo baseInfo = baseInfoService.getById(mainId);
        if (ObjectUtil.isNull(baseInfo)) {
            throw new MithrasException("ftp不存在！");
        }
        //插入月度计价表
        SpringContextHolder.getBean(NewFtpMonthlyDeductionDraftService.class).add(baseInfo.getMonth(), baseInfo.getId());
        //插入文本
        NewFtpDescriptionTextDraft text = new NewFtpDescriptionTextDraft();
        text.setFtpId(mainId);
        text.setDescType(DescriptionType.MONTHLY_DEDUCTION.name());
        text.setDescContent(String.format(DescriptionType.MONTHLY_DEDUCTION.getDesc(), getDynamicDesc(baseInfo.getMonth())));
        SpringContextHolder.getBean(NewFtpDescriptionTextDraftService.class).save(text);
        if (!YesOrNoNumberEnum.YES.getCode().equals(baseInfo.getCalculateDeductionFlag())) {
            NewFtpBaseInfo newFtpBaseInfo = new NewFtpBaseInfo();
            newFtpBaseInfo.setId(baseInfo.getId());
            newFtpBaseInfo.setCalculateDeductionFlag(YesOrNoNumberEnum.YES.getCode());
            baseInfoService.updateById(newFtpBaseInfo);
        }
        return R.ok();
    }

    private String getDynamicDesc(LocalDate month) {
        int monthValue = month.getMonthValue();
        if (monthValue == 1) {
            return "去年全年";
        }
        return "<1月>-<" + (monthValue - 1) + "月>";
    }
}

/**
 * 提取器
 */
@NoArgsConstructor
class MonthlyDeductionDataExtractor {
    private Map<String, NewFtpMonthlyDeductionDraft> map;

    public MonthlyDeductionDataExtractor(Map<String, NewFtpMonthlyDeductionDraft> map) {
        this.map = map;
    }

    /**
     * 获取成本费用小计
     *
     * @return
     */
    public Integer getSubtotalCost(String termRange) {
        return map.get(termRange).getSubtotalCost();
    }

    /**
     * 获取金融市场波动计价-小计
     *
     * @param termRange
     * @return
     */
    public Integer getSubtotalRate(String termRange) {
        return map.get(termRange).getSubtotalRate();
    }

    /**
     * 获取资产分类
     *
     * @param termRange
     * @param assetClassify
     * @return
     */
    public Integer getAssetClassifyPricing(String termRange, String assetClassify) {
        if (ObjectUtil.isEmpty(assetClassify)) {
            return 0;
        }
        NewFtpMonthlyDeductionDraft deduction = map.get(termRange);
        AssetIndustryClassify assetIndustryClassify = AssetIndustryClassify.valueOf(assetClassify);
        switch (assetIndustryClassify) {
            case ENCOURAGE_INTERVENTION:
                return deduction.getAssetEncourage();
            case MODERATE_SUPPORT:
                return deduction.getAssetModerate();
            case CAUTIOUS_SUPPORT:
                return deduction.getAssetCautious();
            default:
                break;
        }
        return 0;
    }

    /**
     * 地区分类
     *
     * @param termRange
     * @param industryClassify
     * @param regionalClassify
     * @return
     */
    public Integer getRegionalClassifyPricing(String termRange, String industryClassify, String regionalClassify) {
        if (ObjectUtil.isEmpty(industryClassify) || ObjectUtil.isEmpty(regionalClassify)) {
            return 0;
        }
        NewFtpMonthlyDeductionDraft deduction = map.get(termRange);
        RegionalClassify regionalClassifyEnum;
        regionalClassifyEnum = RegionalClassify.valueOf(regionalClassify);
        if (RiskIndustryClassify.INDUSTRY.name().equals(industryClassify)) {
            switch (regionalClassifyEnum) {
                case ZHEJIANG:
                    return deduction.getIndustryRegionZhejiang();
                case ENCOURAGE:
                    return deduction.getIndustryRegionEncourage();
                case OTHER:
                    return deduction.getIndustryRegionOther();
                default:
                    break;
            }
        } else if (RiskIndustryClassify.PUBLIC_UTILITY.name().equals(industryClassify)) {
            switch (regionalClassifyEnum) {
                case ZHEJIANG:
                    return deduction.getPublicUtilitiesRegionZhejiang();
                case ENCOURAGE:
                    return deduction.getPublicUtilitiesRegionEncourage();
                case OTHER:
                    return deduction.getPublicUtilitiesRegionOther();
                default:
                    break;
            }
        } else if (RiskIndustryClassify.CIVIL_CONSUMPTION.name().equals(industryClassify)) {
            switch (regionalClassifyEnum) {
                case ZHEJIANG:
                    return deduction.getCivilConsumptionRegionZhejiang();
                case ENCOURAGE:
                    return deduction.getCivilConsumptionRegionEncourage();
                case OTHER:
                    return deduction.getCivilConsumptionRegionOther();
                default:
                    break;
            }
        } else if (RiskIndustryClassify.STATE_OWNED_INDUSTRY.name().equals(industryClassify)) {
            switch (regionalClassifyEnum) {
                case ZHEJIANG:
                    return deduction.getStateOwnedIndustryRegionZhejiang();
                case ENCOURAGE:
                    return deduction.getStateOwnedIndustryRegionEncourage();
                case OTHER:
                    return deduction.getStateOwnedIndustryRegionOther();
                default:
                    break;
            }
        } else {
            throw new MithrasException("请选择正确的行业分类");
        }
        return 0;
    }

    /**
     * 客户分类
     *
     * @param termRange
     * @param customerClassify
     * @return
     */
    public Integer getCustomerClassifyPricing(String termRange, String customerClassify) {
        if (ObjectUtil.isEmpty(customerClassify)) {
            return 0;
        }
        NewFtpMonthlyDeductionDraft deduction = map.get(termRange);
        CustomerEntityClassify customerClassifyEnum = CustomerEntityClassify.valueOf(customerClassify);
        switch (customerClassifyEnum) {
            case LISTED_COMPANY:
                return deduction.getCustomerListed();
            case STATE_OWNED_ENTERPRISE:
                return deduction.getCustomerStateOwned();
            case OTHER:
                return deduction.getCustomerOther();
            default:
                break;
        }
        return 0;
    }
}
