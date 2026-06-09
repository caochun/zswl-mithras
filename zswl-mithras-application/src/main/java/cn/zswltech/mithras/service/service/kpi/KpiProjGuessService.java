package cn.zswltech.mithras.service.service.kpi;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.kpi.mapper.model.KpiParameterConfig;
import cn.zswltech.mithras.kpi.service.KpiParameterConfigService;
import cn.zswltech.mithras.kpi.enums.KpiParameterConfigCodeEnum;
import cn.zswltech.mithras.finance.mapper.model.finance.FinanceProjectProfitDetail;
import cn.zswltech.mithras.kpi.mapper.model.*;
import cn.zswltech.mithras.kpi.service.KpiProjGuessDivideService;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.service.service.finance.FinanceProjectProfitDetailService;
import cn.zswltech.mithras.kpi.service.lib.KpiProjectDistributionBaseInfoLibService;
import cn.zswltech.mithras.kpi.service.lib.KpiProjectDistributionWeightLibService;
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
 * @date 2023/6/17
 * @description
 */
@Slf4j
@Service
public class KpiProjGuessService {
    @Resource
    private KpiProjectDistributionService kpiProjectDistributionService;
    @Resource
    private KpiProjectDistributionBaseInfoLibService kpiProjectDistributionBaseInfoLibService;
    @Resource
    private KpiProjectDistributionWeightLibService kpiProjectDistributionWeightLibService;
    @Resource
    private FinanceProjectProfitDetailService financeProjectProfitDetailService;
    @Resource
    private KpiProjGuessBaseInfoService kpiProjGuessBaseInfoService;
    @Resource
    private KpiProjGuessDivideService kpiProjGuessDivideService;
    @Resource
    private KpiParameterConfigService kpiParameterConfigService;

    @Deprecated
    @Transactional(rollbackFor = Throwable.class)
    public void calculate(Long contractId, int year, int month) {
        log.info("开始执行项目利润测算逻辑[contractId:{}, year:{}, month:{}]", contractId, year, month);

        // 查询本月和上月是否已有数据
        KpiProjGuessBaseInfo existCurrentMonthInfo = kpiProjGuessBaseInfoService.getSpecificDateOne(contractId, year, month);
        KpiProjGuessBaseInfo existLastMonthInfo;
        if (month == 1) {
            existLastMonthInfo = kpiProjGuessBaseInfoService.getSpecificDateOne(contractId, year - 1, 12);
        } else {
            existLastMonthInfo = kpiProjGuessBaseInfoService.getSpecificDateOne(contractId, year, month - 1);
        }

        // 确定项目利润
        BigDecimal projectProfit = this.getProjectProfit(contractId, year, month);
        log.info("确定项目利润结果:{}", Optional.ofNullable(projectProfit).map(BigDecimal::toPlainString).orElse(null));

        // 查询项目分配基本信息
        KpiProjectDistributionBaseInfoLib distributionBaseInfoLib = this.getDistributionBaseInfoLib(contractId, year, month);

        // 确定分润比
        List<KpiProjectDistributionWeightLib> weightLibList;
        if (Objects.nonNull(distributionBaseInfoLib)) {
            weightLibList = kpiProjectDistributionWeightLibService.listByMainIdAndVersion(distributionBaseInfoLib.getProjectDistributionId(), distributionBaseInfoLib.getVersion());
        } else {
            weightLibList = Collections.emptyList();
        }
        log.info("确定分润比结果:{}", JSONUtil.toJsonStr(weightLibList));

        // 确定提奖比例
        String awardRadioConfig = null;
        if (Objects.nonNull(existCurrentMonthInfo) && StrUtil.isNotBlank(existCurrentMonthInfo.getAwardRatioConfig())) {
            // 取快照
            awardRadioConfig = existCurrentMonthInfo.getAwardRatioConfig();
        } else {
            // 取当前最新的
            KpiParameterConfig kpiParameterConfig = kpiParameterConfigService.getOneByConfigCode(KpiParameterConfigCodeEnum.PROJECT_RADIO);
            if (Objects.nonNull(kpiParameterConfig)) {
                awardRadioConfig = kpiParameterConfig.getConfigValue();
            }
        }
        BigDecimal awardRadio = null;
        if (Objects.nonNull(distributionBaseInfoLib)) {
            awardRadio = kpiParameterConfigService.ensureProjectRadio(awardRadioConfig, distributionBaseInfoLib.getProjClassify(), distributionBaseInfoLib.getContractStartDate(), LocalDate.of(year, month, 1));
        }
        log.info("确定项目提奖比例结果:{}", Optional.ofNullable(awardRadio).map(BigDecimal::toPlainString).orElse(null));

        if (Objects.isNull(projectProfit) || CollectionUtil.isEmpty(weightLibList) || Objects.isNull(awardRadio)) {
            log.info("参与计算绩效奖金的必要条件存在缺失，放弃计算");
            return;
        }

        // 绩效测算主表数据
        KpiProjGuessBaseInfo toSaveBaseInfo = new KpiProjGuessBaseInfo();
        if (Objects.nonNull(existCurrentMonthInfo)) {
            toSaveBaseInfo.setId(existCurrentMonthInfo.getId());
        }
        toSaveBaseInfo.setKpiProjectDistributionId(distributionBaseInfoLib.getProjectDistributionId());
        toSaveBaseInfo.setKpiProjectDistributionVersion(distributionBaseInfoLib.getVersion());
        toSaveBaseInfo.setContractId(contractId);
        toSaveBaseInfo.setCalculateDateYear(year);
        toSaveBaseInfo.setCalculateDateMonth(month);
        toSaveBaseInfo.setAwardRatio(awardRadio.multiply(BigDecimal.valueOf(1000000)).longValue());
        toSaveBaseInfo.setAwardRatioConfig(awardRadioConfig);
        toSaveBaseInfo.setProfitCurrent(projectProfit.longValue());
        if (Objects.nonNull(existLastMonthInfo)) {
            toSaveBaseInfo.setProfitTotal(toSaveBaseInfo.getProfitCurrent() + existLastMonthInfo.getProfitTotal());
        } else {
            toSaveBaseInfo.setProfitTotal(toSaveBaseInfo.getProfitCurrent());
        }

        // 绩效测算子表数据
        List<KpiProjGuessDivide> projGuessDivideThisMonthList = kpiProjGuessDivideService.listByContractYearMonth(contractId, year, month);
        int preYear;
        int preMonth;
        if (month == 1) {
            preYear = year - 1;
            preMonth = 12;
        } else {
            preYear = year;
            preMonth = month - 1;
        }
        List<KpiProjGuessDivide> projGuessDividePreMonthList = kpiProjGuessDivideService.listByContractYearMonth(contractId, preYear, preMonth);
        Map<String, KpiProjGuessDivide> projGuessDivideThisMonthMap = projGuessDivideThisMonthList.stream().collect(Collectors.toMap(e -> e.getDivideType() + "-" + e.getDivideTarget(), e -> e));
        Map<String, KpiProjGuessDivide> projGuessDividePreMonthMap = projGuessDividePreMonthList.stream().collect(Collectors.toMap(e -> e.getDivideType() + "-" + e.getDivideTarget(), e -> e));
        // 遍历分润比来处理
        List<KpiProjGuessDivide> kpiProjGuessDivideList = new LinkedList<>();
        for (KpiProjectDistributionWeightLib weightLib : weightLibList) {
            if (StrUtil.isBlank(weightLib.getWeightTarget()) || Objects.isNull(weightLib.getWeightValue())) {
                continue;
            }
            if (weightLib.getWeightValue() == 0) {
                continue;
            }
            // 项目奖金 = 项目利润 * 分润比 * 项目提奖比例
            BigDecimal myProfit = projectProfit.multiply(BigDecimal.valueOf(weightLib.getWeightValue()).divide(BigDecimal.valueOf(1000000), 10, RoundingMode.HALF_UP));
            BigDecimal bonus = myProfit.multiply(awardRadio);
            KpiProjGuessDivide divide = new KpiProjGuessDivide();
            divide.setContractId(contractId);
            divide.setDivideYear(year);
            divide.setDivideMonth(month);
            divide.setDivideType(weightLib.getWeightType());
            divide.setDivideTarget(Long.valueOf(weightLib.getWeightTarget()));
            divide.setProfitCurrent(Util.mithrasLongDecimalTwo(myProfit.longValue()));
            divide.setBonusCurrent(Util.mithrasLongDecimalTwo(bonus.longValue()));
            divide.setProfitTotal(divide.getProfitCurrent());
            divide.setBonusTotal(divide.getBonusCurrent());
            String mapKey = weightLib.getWeightType() + "-" + weightLib.getWeightTarget();
            KpiProjGuessDivide exist = projGuessDivideThisMonthMap.get(mapKey);
            if (Objects.nonNull(exist)) {
                // 填入id更新用
                divide.setId(exist.getId());
            }
            KpiProjGuessDivide preExist = projGuessDividePreMonthMap.get(mapKey);
            if (Objects.nonNull(preExist) && month != 1) {
                // 利用上一期的数据更新累计值
                divide.setProfitTotal(divide.getProfitCurrent() + Optional.ofNullable(preExist.getProfitTotal()).orElse(0L));
                divide.setBonusTotal(divide.getBonusCurrent() + Optional.ofNullable(preExist.getBonusTotal()).orElse(0L));
            }
            kpiProjGuessDivideList.add(divide);
            // 从map中移除，后续会判断剩余在map中的是本次需要删除的
            projGuessDivideThisMonthMap.remove(mapKey);
        }
        if (CollectionUtil.isNotEmpty(projGuessDivideThisMonthMap)) {
            Set<Long> toDeleteIds = projGuessDivideThisMonthMap.values().stream().map(KpiProjGuessDivide::getId).collect(Collectors.toSet());
            kpiProjGuessDivideService.removeByIds(toDeleteIds);
        }
        // 统计当月奖金
        long bonusCurrent = kpiProjGuessDivideList.stream().mapToLong(KpiProjGuessDivide::getBonusCurrent).sum();
        // 回填到主表中
        toSaveBaseInfo.setBonusCurrent(bonusCurrent);
        if (Objects.nonNull(existLastMonthInfo)) {
            toSaveBaseInfo.setBonusTotal(toSaveBaseInfo.getBonusCurrent() + existLastMonthInfo.getBonusTotal());
        } else {
            toSaveBaseInfo.setBonusTotal(toSaveBaseInfo.getBonusCurrent());
        }
        // 保存
        kpiProjGuessBaseInfoService.saveOrUpdate(toSaveBaseInfo);
        if (CollectionUtil.isNotEmpty(kpiProjGuessDivideList)) {
            // 主表id回填
            for (KpiProjGuessDivide kpiProjGuessDivide : kpiProjGuessDivideList) {
                if (Objects.isNull(kpiProjGuessDivide.getKpiProjGuessId())) {
                    kpiProjGuessDivide.setKpiProjGuessId(toSaveBaseInfo.getId());
                }
            }
            kpiProjGuessDivideService.saveOrUpdateBatch(kpiProjGuessDivideList);
        }
    }

    public KpiProjectDistributionBaseInfoLib getDistributionBaseInfoLib(Long contractId, int year, int month) {
        // 查询项目分配信息
        KpiProjectDistribution kpiProjectDistribution = kpiProjectDistributionService.getOneByContractId(contractId);
        if (Objects.isNull(kpiProjectDistribution)) {
            log.info("没有找到合同对应的项目分配信息");
            return null;
        }
        // 查询项目分配对应日期的生效数据
        KpiProjectDistributionBaseInfoLib baseInfoLib = kpiProjectDistributionBaseInfoLibService.getNearSpecificDate(kpiProjectDistribution.getId(), year, month);
        if (Objects.isNull(baseInfoLib)) {
            log.info("没有找到小于等于给定年月的生效项目分配基本信息");
            return null;
        }
        return baseInfoLib;
    }

    public BigDecimal getProjectProfit(Long contractId, int year, int month) {
        FinanceProjectProfitDetail financeProjectProfitDetail = financeProjectProfitDetailService.getSpecificOne(contractId, year, month);
        if (Objects.isNull(financeProjectProfitDetail)) {
            return null;
        }
        if (Objects.isNull(financeProjectProfitDetail.getProfitThisMonth())) {
            return null;
        }
        return new BigDecimal(financeProjectProfitDetail.getProfitThisMonth());
    }
}
