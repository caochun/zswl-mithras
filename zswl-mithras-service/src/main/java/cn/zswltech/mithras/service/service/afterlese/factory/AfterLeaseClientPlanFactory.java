package cn.zswltech.mithras.service.service.afterlese.factory;

import cn.zswltech.mithras.dto.afterlease.AfterLeaseClientPlanRSP;
import cn.zswltech.mithras.service.enums.afterlease.AfterLeaseCheckTermEnum;
import cn.zswltech.mithras.service.enums.afterlease.AfterLeaseCheckWayEnum;
import cn.zswltech.mithras.service.enums.riskcontrol.RiskControlIndustryClassify;
import cn.zswltech.mithras.service.util.BigDecimalUtil;
import org.testng.collections.Lists;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class AfterLeaseClientPlanFactory {

    // 定义风险敞口阈值常量
    private static final long THRESHOLD_5000_WAN = 500000000000L;  // 5000万元
    private static final long THRESHOLD_3000_WAN = 300000000000L;  // 3000万元
    private static final long THRESHOLD_1000_WAN = 100000000000L;  // 1000万元

    private static final Map<String, Function<Object, List<AfterLeaseClientPlanRSP>>> industryClassify2AfterLeaseClientPlanMap = new HashMap<>();

    public AfterLeaseClientPlanFactory() {
        industryClassify2AfterLeaseClientPlanMap.put(RiskControlIndustryClassify.PUBLIC_UTILITIES.name(), this::getPublicUtilityAfterLeaseClientPlan);
        industryClassify2AfterLeaseClientPlanMap.put(RiskControlIndustryClassify.CIVIL_CONSUMPTION.name(), this::getCivilConsumptionAfterLeaseClientPlan);
        industryClassify2AfterLeaseClientPlanMap.put(RiskControlIndustryClassify.WATER_TRANSPORTATION.name(), this::getWaterTransportationAfterLeaseClientPlan);
        industryClassify2AfterLeaseClientPlanMap.put(RiskControlIndustryClassify.STEEL.name(), this::getSteelAfterLeaseClientPlan);
        industryClassify2AfterLeaseClientPlanMap.put(RiskControlIndustryClassify.NON_GOVERNMENT_FUNDED_EDUCATION.name(), this::getNonGovernmentFundedEducationClientPlan);
    }

    public static AfterLeaseClientPlanFactory getInstance(){
        return new AfterLeaseClientPlanFactory();
    }

    /**
     * 获取租后检查计划 - 通用公共对外暴露方法
     *
     * @param industryClassify 风控行业分类
     * @param customizer       检查计划补充
     * @param acceptParam      检查计划生成所需参数
     * @return 租后检查计划列表
     */
    public List<AfterLeaseClientPlanRSP> retrieveAfterLeaseClientPlans(String industryClassify,
                                                                              Consumer<AfterLeaseClientPlanRSP> customizer,
                                                                              Object acceptParam) {
        if (industryClassify == null || !industryClassify2AfterLeaseClientPlanMap.containsKey(industryClassify)) {
            return Lists.newArrayList();
        }

        Function<Object, List<AfterLeaseClientPlanRSP>> function = industryClassify2AfterLeaseClientPlanMap.get(industryClassify);
        List<AfterLeaseClientPlanRSP> result = function.apply(acceptParam);

        // 应用自定义配置器
        if (customizer != null && result != null) {
            result.forEach(customizer);
        }
        return result;
    }

    /**
     * Long 类型安全检查
     */
    private <T> List<AfterLeaseClientPlanRSP> processWithLongValue(T t, Function<Long, List<AfterLeaseClientPlanRSP>> processor) {
        if (t instanceof Long) {
            Long value = (Long) t;
            return processor.apply(value);
        } else {
            // 添加日志记录类型不匹配的情况
            System.out.println("Type mismatch: expected Long, got " + (t != null ? t.getClass().getName() : "null"));
            return Lists.newArrayList();
        }
    }

    /**
     * 公用事业类，下次租后检查计划获取
     */
    private <T> List<AfterLeaseClientPlanRSP> getPublicUtilityAfterLeaseClientPlan(T t) {
        return processWithLongValue(t, stockRiskExposure ->
                Arrays.asList(
                        AfterLeaseClientPlanRSP.builder()
                                .checkWay(AfterLeaseCheckWayEnum.OFFSITE.name())
                                .term(AfterLeaseCheckTermEnum.HALF_A_YEAR.term)
                                .stockRiskExposure(stockRiskExposure)
                                .build(),
                        AfterLeaseClientPlanRSP.builder()
                                .checkWay(AfterLeaseCheckWayEnum.SITE.name())
                                .term(AfterLeaseCheckTermEnum.HALF_A_YEAR.term)
                                .stockRiskExposure(stockRiskExposure)
                                .build()
                )
        );
    }

    /**
     * 民生消费类，下次租后检查计划获取
     */
    private <T> List<AfterLeaseClientPlanRSP> getCivilConsumptionAfterLeaseClientPlan(T t) {
        return processWithLongValue(t, stockRiskExposure ->
                Arrays.asList(
                        AfterLeaseClientPlanRSP.builder()
                                .checkWay(AfterLeaseCheckWayEnum.OFFSITE.name())
                                .term(AfterLeaseCheckTermEnum.HALF_A_YEAR.term)
                                .stockRiskExposure(stockRiskExposure)
                                .build(),
                        AfterLeaseClientPlanRSP.builder()
                                .checkWay(AfterLeaseCheckWayEnum.SITE.name())
                                .term(AfterLeaseCheckTermEnum.HALF_A_YEAR.term)
                                .stockRiskExposure(stockRiskExposure)
                                .build()
                )
        );
    }

    /**
     * 水上运输业，即航运行业，下次租后检查计划获取
     */
    private <T> List<AfterLeaseClientPlanRSP> getWaterTransportationAfterLeaseClientPlan(T t) {
        return processWithLongValue(t, stockRiskExposure -> {
            List<AfterLeaseClientPlanRSP> afterLeaseClientPlanRSPList = Lists.newArrayList();

            // XMX-161 默认风险缓释度
            BigDecimal riskMitigationDegree = BigDecimalUtil.stringToBigDecimal("0.9");

            if (stockRiskExposure >= THRESHOLD_5000_WAN) {
                // 风险敞口余额5000万元（含）以上
                if (riskMitigationDegree.compareTo(BigDecimalUtil.stringToBigDecimal("0.9")) >= 0) {
                    // 风险缓释度≥90%的项目
                    afterLeaseClientPlanRSPList.add(AfterLeaseClientPlanRSP.builder()
                            .checkWay(AfterLeaseCheckWayEnum.SITE.name())
                            .term(AfterLeaseCheckTermEnum.HALF_A_YEAR.term)
                            .stockRiskExposure(stockRiskExposure)
                            .build());
                } else {
                    // 风险缓释度<90%的项目
                    afterLeaseClientPlanRSPList.add(AfterLeaseClientPlanRSP.builder()
                            .checkWay(AfterLeaseCheckWayEnum.SITE.name())
                            .term(AfterLeaseCheckTermEnum.QUARTER.term)
                            .stockRiskExposure(stockRiskExposure)
                            .build());
                }
                return afterLeaseClientPlanRSPList;
            }

            if (stockRiskExposure >= THRESHOLD_1000_WAN) {
                // 风险敞口在1000万元（含）以上至5000万以下
                afterLeaseClientPlanRSPList.add(AfterLeaseClientPlanRSP.builder()
                        .checkWay(AfterLeaseCheckWayEnum.OFFSITE.name())
                        .term(AfterLeaseCheckTermEnum.HALF_A_YEAR.term)
                        .stockRiskExposure(stockRiskExposure)
                        .build());
                afterLeaseClientPlanRSPList.add(AfterLeaseClientPlanRSP.builder()
                        .checkWay(AfterLeaseCheckWayEnum.SITE.name())
                        .term(AfterLeaseCheckTermEnum.YEAR.term)
                        .stockRiskExposure(stockRiskExposure)
                        .build());
                return afterLeaseClientPlanRSPList;
            }

            // 风险敞口余额1000万元（含新增及存量敞口下降项目）以下
            afterLeaseClientPlanRSPList.add(AfterLeaseClientPlanRSP.builder()
                    .checkWay(AfterLeaseCheckWayEnum.OFFSITE.name())
                    .term(AfterLeaseCheckTermEnum.HALF_A_YEAR.term)
                    .stockRiskExposure(stockRiskExposure)
                    .build());
            return afterLeaseClientPlanRSPList;
        });
    }

    /**
     * 钢铁、不锈钢及有色金属冶炼行业, 下次租后检查计划获取
     */
    private <T> List<AfterLeaseClientPlanRSP> getSteelAfterLeaseClientPlan(T t) {
        return processWithLongValue(t, stockRiskExposure -> {
            List<AfterLeaseClientPlanRSP> afterLeaseClientPlanRSPList = Lists.newArrayList();

            // 风险敞口余额3000万元（含）以上
            if (stockRiskExposure >= THRESHOLD_3000_WAN) {
                afterLeaseClientPlanRSPList.add(AfterLeaseClientPlanRSP.builder()
                        .checkWay(AfterLeaseCheckWayEnum.SITE.name())
                        .term(AfterLeaseCheckTermEnum.HALF_A_YEAR.term)
                        .stockRiskExposure(stockRiskExposure)
                        .build());
                return afterLeaseClientPlanRSPList;
            }

            if (stockRiskExposure >= THRESHOLD_1000_WAN) {
                // 风险敞口在1000万元（含）以上至3000万以下
                afterLeaseClientPlanRSPList.add(AfterLeaseClientPlanRSP.builder()
                        .checkWay(AfterLeaseCheckWayEnum.OFFSITE.name())
                        .term(AfterLeaseCheckTermEnum.HALF_A_YEAR.term)
                        .stockRiskExposure(stockRiskExposure)
                        .build());
                afterLeaseClientPlanRSPList.add(AfterLeaseClientPlanRSP.builder()
                        .checkWay(AfterLeaseCheckWayEnum.SITE.name())
                        .term(AfterLeaseCheckTermEnum.YEAR.term)
                        .stockRiskExposure(stockRiskExposure)
                        .build());
                return afterLeaseClientPlanRSPList;
            }

            // 风险敞口余额1000万元以下
            afterLeaseClientPlanRSPList.add(AfterLeaseClientPlanRSP.builder()
                    .checkWay(AfterLeaseCheckWayEnum.OFFSITE.name())
                    .term(AfterLeaseCheckTermEnum.HALF_A_YEAR.term)
                    .stockRiskExposure(stockRiskExposure)
                    .build());
            return afterLeaseClientPlanRSPList;
        });
    }

    /**
     * 民办教育行业, 下次租后检查计划获取
     */
    private <T> List<AfterLeaseClientPlanRSP> getNonGovernmentFundedEducationClientPlan(T t) {
        return processWithLongValue(t, stockRiskExposure -> {
            List<AfterLeaseClientPlanRSP> afterLeaseClientPlanRSPList = Lists.newArrayList();

            if (stockRiskExposure >= THRESHOLD_3000_WAN) {
                // 风险敞口余额3000万元（含）以上
                afterLeaseClientPlanRSPList.add(AfterLeaseClientPlanRSP.builder()
                        .checkWay(AfterLeaseCheckWayEnum.SITE.name())
                        .term(AfterLeaseCheckTermEnum.HALF_A_YEAR.term)
                        .stockRiskExposure(stockRiskExposure)
                        .build());
                return afterLeaseClientPlanRSPList;
            }

            if (stockRiskExposure >= THRESHOLD_1000_WAN) {
                // 风险敞口在1000万元（含）以上至3000万以下
                afterLeaseClientPlanRSPList.add(AfterLeaseClientPlanRSP.builder()
                        .checkWay(AfterLeaseCheckWayEnum.OFFSITE.name())
                        .term(AfterLeaseCheckTermEnum.HALF_A_YEAR.term)
                        .stockRiskExposure(stockRiskExposure)
                        .build());
                afterLeaseClientPlanRSPList.add(AfterLeaseClientPlanRSP.builder()
                        .checkWay(AfterLeaseCheckWayEnum.SITE.name())
                        .term(AfterLeaseCheckTermEnum.YEAR.term)
                        .stockRiskExposure(stockRiskExposure)
                        .build());
                return afterLeaseClientPlanRSPList;
            }

            // 风险敞口余额1000万元以下
            afterLeaseClientPlanRSPList.add(AfterLeaseClientPlanRSP.builder()
                    .checkWay(AfterLeaseCheckWayEnum.OFFSITE.name())
                    .term(AfterLeaseCheckTermEnum.HALF_A_YEAR.term)
                    .stockRiskExposure(stockRiskExposure)
                    .build());
            return afterLeaseClientPlanRSPList;
        });
    }
}

