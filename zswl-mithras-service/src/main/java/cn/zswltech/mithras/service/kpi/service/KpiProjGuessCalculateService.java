package cn.zswltech.mithras.service.kpi.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.dto.kpi.parameterconfig.ProjectPaymentBonusRadioConfig;
import cn.zswltech.mithras.dto.kpi.parameterconfig.ProjectRadioConfig;
import cn.zswltech.mithras.dto.kpi.parameterconfig.ProjectScaleRadioConfig;
import cn.zswltech.mithras.dto.kpi.parameterconfig.ProjectTypeRadioConfig;
import cn.zswltech.mithras.kpi.bo.*;
import cn.zswltech.mithras.kpi.enums.KpiProjectClassifyEnum;
import cn.zswltech.mithras.kpi.enums.KpiProjectSourceDistributionEnum;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.kpi.mapper.model.KpiProjGuessBaseInfo;
import cn.zswltech.mithras.kpi.mapper.model.KpiProjGuessDivide;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.kpi.KpiProjGuessBaseInfoService;
import cn.zswltech.mithras.kpi.service.KpiProjGuessDivideService;
import cn.zswltech.mithras.service.service.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.service.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author: jackerhe
 * @date: 2024/9/26 09:07
 * 绩效测试表计算逻辑
 **/
@Slf4j
@Service
public class KpiProjGuessCalculateService {
    @Resource
    private KpiProjGuessCollectService kpiProjGuessCollectService;
    @Resource
    private KpiProjGuessBaseInfoService kpiProjGuessBaseInfoService;
    @Resource
    private KpiProjGuessDivideService kpiProjGuessDivideService;
    @Resource
    private PaymentBaseInfoService paymentBaseInfoService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;

    private final LocalDate PROJECT_SCALE_RADIO_DATE = LocalDate.of(2024, 1,1);

    @Transactional(rollbackFor = Throwable.class)
    public void calculate(List<Long> contractIds, LocalDate date, boolean flash) {
        log.info("开始执行项目利润测算逻辑 KpiProjGuessCalculateService [contractId:{}, date:{}, flash:{}]", contractIds, date, flash);
        //获取计算所需要的参数
        if(ObjectUtil.isEmpty(date)) {
            return;
        }
        int year = date.getYear();
        int month = date.getMonthValue();
        // 查询本月和上月是否已有数据
        List<KpiProjGuessBaseInfo> existCurrentMonthInfos = kpiProjGuessBaseInfoService.getSpecificDateList(contractIds, year, month);
        List<KpiProjGuessBaseInfo> existLastMonthInfos;
        Map<Long, KpiProjGuessBaseInfo> existLastMonthInfoMap = new HashMap<>();
        if (month == 1) {
            existLastMonthInfos = kpiProjGuessBaseInfoService.getSpecificDateList(contractIds, year - 1, 12);
        } else {
            existLastMonthInfos = kpiProjGuessBaseInfoService.getSpecificDateList(contractIds, year, month - 1);
        }
        if(ObjectUtil.isNotEmpty(existLastMonthInfos)) {
            existLastMonthInfoMap = existLastMonthInfos.stream().collect(Collectors.toMap(KpiProjGuessBaseInfo::getContractId, e -> e, (a, b) -> a));
        }
        Map<Long, KpiProjGuessBaseInfo> existCurrentMonthInfoMap = existCurrentMonthInfos.stream().collect(Collectors.toMap(KpiProjGuessBaseInfo::getContractId, e -> e, (a, b) -> a));
        // 确定项目利润
        // 查询是否已有利润分配 -> 借据维度
        List<KpiFinanceProjectProfitRecordBo> projectProfitRecords = kpiProjGuessCollectService.getProjectProfitRecords(contractIds, year, month, flash);
        if(ObjectUtil.isEmpty(projectProfitRecords)) {
            return;
        }
        Map<Long, Long> contractId2Proj = contractBaseInfoService.listByIds(projectProfitRecords.stream().map(KpiFinanceProjectProfitRecordBo::getContractId).filter(ObjectUtil::isNotEmpty).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(ContractBaseInfo::getId, ContractBaseInfo::getProjReviewId, (a, b) -> a));
        Map<Long, Long> projReview2Payment = paymentBaseInfoService.getProjReviewPaymentProjReviewId(new HashSet<>(contractId2Proj.values()), null, date.with(TemporalAdjusters.lastDayOfMonth()));
        Map<Long, LocalDate> contractFirstPaymentDate = paymentBaseInfoService.getContractFirstPaymentDate(contractId2Proj.keySet());
        // 查询项目分配基本信息
        // 确定分润比
        Map<Long, KpiProjectDistributionRecordBo> projectDistributionWeightMap = kpiProjGuessCollectService.getProjectDistributionWeight(contractIds, date, flash)
                .stream().collect(Collectors.toMap(KpiProjectDistributionRecordBo::getContractId, e -> e, (a, b) -> a));
        // 确定提奖比例
        KpiParameterConfigBo parameterConfig = kpiProjGuessCollectService.getParameterConfig(date, flash);
        // 查询本月投放数据
        Map<Long, KpiPaymentAmountBo> contractPaymentAmountMap = kpiProjGuessCollectService.getContractPaymentAmount(contractIds, date, flash)
                .stream().collect(Collectors.toMap(KpiPaymentAmountBo::getContractId, e -> e, (a, b) -> b));

        //查询已经存在数据
        // 绩效测算子表数据
        List<KpiProjGuessDivide> projGuessDivideThisMonthList = kpiProjGuessDivideService.listByContractIdsYearMonth(contractIds, year, month);
        int preYear;
        int preMonth;
        if (month == 1) {
            preYear = year - 1;
            preMonth = 12;
        } else {
            preYear = year;
            preMonth = month - 1;
        }
        List<KpiProjGuessDivide> projGuessDividePreMonthList = kpiProjGuessDivideService.listByContractIdsYearMonth(contractIds, preYear, preMonth);
        Set<Long> toDeleteIds = new HashSet<>();
        List<KpiProjGuessBaseInfo> kpiProjGuessBaseInfos = new ArrayList<>();
        List<KpiProjGuessDivide> kpiProjGuessDivides = new ArrayList<>();

        for(KpiFinanceProjectProfitRecordBo projectProfitRecordBo : projectProfitRecords) {
            //分配详情
            List<KpiProjGuessDivide> kpiProjGuessDividesList = this.buildGuessDivide(projGuessDivideThisMonthList, projGuessDividePreMonthList,
                    projectDistributionWeightMap.get(projectProfitRecordBo.getContractId()), projectProfitRecordBo, parameterConfig,
                    year, month, contractPaymentAmountMap.get(projectProfitRecordBo.getContractId()), toDeleteIds, projReview2Payment.get(contractId2Proj.get(projectProfitRecordBo.getContractId())));
            if(ObjectUtil.isNotEmpty(kpiProjGuessDividesList)) {
                kpiProjGuessDivides.addAll(kpiProjGuessDividesList);
                //分配基础
                kpiProjGuessBaseInfos.add(this.buildGuessBaseInfo(existCurrentMonthInfoMap.get(projectProfitRecordBo.getContractId()), projectDistributionWeightMap.get(projectProfitRecordBo.getContractId()), year, month,
                        projectProfitRecordBo, existLastMonthInfoMap.get(projectProfitRecordBo.getContractId()), kpiProjGuessDividesList,
                        contractPaymentAmountMap.get(projectProfitRecordBo.getContractId()), projReview2Payment.get(contractId2Proj.get(projectProfitRecordBo.getContractId())),
                        contractFirstPaymentDate.get(projectProfitRecordBo.getContractId())));
            }
        }
        kpiProjGuessDivideService.removeByIds(toDeleteIds);
        //保存
        if(ObjectUtil.isNotEmpty(kpiProjGuessBaseInfos)){
            List<KpiProjGuessBaseInfo> allBase = new ArrayList<>();
            List<KpiProjGuessBaseInfo> saveBases = kpiProjGuessBaseInfos.stream().filter(e -> ObjectUtil.isEmpty(e.getId())).collect(Collectors.toList());
            List<KpiProjGuessBaseInfo> updateBases = kpiProjGuessBaseInfos.stream().filter(e -> ObjectUtil.isNotEmpty(e.getId())).collect(Collectors.toList());
            if(ObjectUtil.isNotEmpty(saveBases)) {
                kpiProjGuessBaseInfoService.saveBatch(saveBases);
                allBase.addAll(saveBases);
            }
            if(ObjectUtil.isNotEmpty(updateBases)) {
                kpiProjGuessBaseInfoService.updateBatchById(updateBases);
                allBase.addAll(updateBases);
            }
            Map<Long, Long> contractId2GuessId = allBase.stream().collect(Collectors.toMap(KpiProjGuessBaseInfo::getContractId, KpiProjGuessBaseInfo::getId, (a, b) -> b));
            //保存详情
            kpiProjGuessDivides.forEach(e -> {
                e.setKpiProjGuessId(contractId2GuessId.get(e.getContractId()));
            });
            //补充调整值
            this.calculateAdjust2(kpiProjGuessDivides, kpiProjGuessBaseInfos, year, month);
            kpiProjGuessDivideService.saveOrUpdateBatch(kpiProjGuessDivides);
        }

    }

    //计算调整值
    public void calculateAdjust(List<KpiProjGuessDivide> kpiProjGuessDivides, int year, int month){
        if(ObjectUtil.isEmpty(kpiProjGuessDivides)) {
            return;
        }
        //获取本年month月前的数据
        List<KpiProjGuessDivide> list = kpiProjGuessDivideService.list(Wrappers.<KpiProjGuessDivide>lambdaQuery()
                .eq(KpiProjGuessDivide::getDivideYear, year)
                .le(KpiProjGuessDivide::getDivideMonth, month));
        if(ObjectUtil.isEmpty(list)) {
            return;
        }
        BigDecimal maxBigDecimal = new BigDecimal("1.6");
        Map<Long, KpiProjGuessBaseInfo> baseId2Bean = kpiProjGuessBaseInfoService.listByIds(list.stream().map(KpiProjGuessDivide::getKpiProjGuessId).collect(Collectors.toSet())).stream().collect(Collectors.toMap(KpiProjGuessBaseInfo::getId, e -> e, (a, b) -> a));
        Map<String, List<KpiProjGuessDivide>> oldDivide = list.stream().collect(Collectors.groupingBy(this::getUnique));
        //开始计算
        kpiProjGuessDivides.forEach(e -> {
            List<KpiProjGuessDivide> oldProjGuessDivides = oldDivide.get(this.getUnique(e));
                    //sum 的范围为从本年1月份开始计算到当月"
            if(ObjectUtil.isNotEmpty(oldProjGuessDivides)) {
                BigDecimal profitAdjust = new BigDecimal(LongUtil.null2zero(e.getProjectProfit()).toString()).multiply(new BigDecimal(e.getProjectRadioConfig() == null ? "1" : e.getProjectRadioConfig()))
                        .multiply(new BigDecimal(e.getTypeRadioConfig() == null ? "1" : e.getTypeRadioConfig()));
                //
                BigDecimal profitCurrent = new BigDecimal(LongUtil.null2zero(e.getProfitCurrent()));
                BigDecimal bonusAdjust = new BigDecimal(LongUtil.null2zero(e.getProjectProfit()).toString()).multiply(new BigDecimal(e.getProjectRadioConfig() == null ? "1" : e.getProjectRadioConfig()))
                        .multiply(new BigDecimal(e.getTypeRadioConfig() == null ? "1" : e.getTypeRadioConfig())).multiply(LongUtil.tenThousand2Dollar(LongUtil.null2zero(e.getDivideWeight()).toString()).divide(new BigDecimal(100), 6, RoundingMode.HALF_UP));
                BigDecimal bonusCurrent = new BigDecimal(LongUtil.null2zero(e.getBonusCurrent()));
                for (KpiProjGuessDivide old : oldProjGuessDivides) {
                    KpiProjGuessBaseInfo kpiProjGuessBaseInfo = baseId2Bean.get(old.getKpiProjGuessId());
                    //历史的不再计算
                    if (kpiProjGuessBaseInfo == null || old.getProjectProfit() == null) {
                        continue;
                    }
                    ProjectRadioConfig config = JSONUtil.toBean(kpiProjGuessBaseInfo.getAwardRatioConfig(), ProjectRadioConfig.class);
                    BigDecimal weightValue = LongUtil.tenThousand2Dollar(LongUtil.null2zero(old.getDivideWeight()).toString()).divide(new BigDecimal(100), 6, RoundingMode.HALF_UP);
                    //利润提奖-调整值
                    //sum（项目利润-当期值✖️项目类型系数✖️基础提奖比例）✖️ 本月的 项目规模系数 - sum（利润提奖-当期值）
                    profitAdjust = profitAdjust.add(new BigDecimal(LongUtil.null2zero(old.getProjectProfit()).toString()).multiply(new BigDecimal(old.getProjectRadioConfig() == null ? config.getConfig(kpiProjGuessBaseInfo.getProjClassify(), kpiProjGuessBaseInfo.getReceiptFirstPaymentDate(), LocalDate.of(year, month, 1)) : old.getProjectRadioConfig()))
                            .multiply(maxBigDecimal.min(new BigDecimal(old.getTypeRadioConfig() == null ? "1" : old.getTypeRadioConfig()).multiply(new BigDecimal(e.getScaleRadioConfig())))));
                    profitCurrent = profitCurrent.add(new BigDecimal(LongUtil.null2zero(old.getProfitCurrent())));
                    //利润奖金-调整值
                    //计算公式=  sum（项目利润-当期值✖️基础提奖比例✖️项目类型系数✖️占比）✖️ 本月的 项目规模系数 - sum（利润奖金-当期值）
                    bonusAdjust = bonusAdjust.add(new BigDecimal(LongUtil.null2zero(old.getProjectProfit()).toString()).multiply(new BigDecimal(old.getProjectRadioConfig() == null ? config.getConfig(kpiProjGuessBaseInfo.getProjClassify(), kpiProjGuessBaseInfo.getReceiptFirstPaymentDate(), LocalDate.of(year, month, 1)) : old.getProjectRadioConfig()))
                            .multiply(maxBigDecimal.min(new BigDecimal(old.getTypeRadioConfig() == null ? "1" : old.getTypeRadioConfig()).multiply(new BigDecimal(e.getScaleRadioConfig())))).multiply(weightValue));
                    bonusCurrent = bonusCurrent.add(new BigDecimal(LongUtil.null2zero(old.getBonusCurrent())));
                }
                profitAdjust = profitAdjust.subtract(profitCurrent);
                if (profitAdjust.longValue() != 0) {
                    e.setProfitAdjust(profitAdjust.longValue());
                }
                //奖金
                bonusAdjust = bonusAdjust.subtract(bonusCurrent);
                if (bonusAdjust.longValue() != 0) {
                    e.setBonusAdjust(bonusAdjust.longValue());
                }
            }
        });
    }

    //计算调整值
    public void calculateAdjust2(List<KpiProjGuessDivide> kpiProjGuessDivides, List<KpiProjGuessBaseInfo> kpiProjGuessBaseInfos, int year, int month){
        if (ObjectUtil.isEmpty(kpiProjGuessDivides)) {
            return;
        }
        //获取本年month月前的数据
        List<KpiProjGuessDivide> list = kpiProjGuessDivideService.list(Wrappers.<KpiProjGuessDivide>lambdaQuery()
                .eq(KpiProjGuessDivide::getDivideYear, year)
                .le(KpiProjGuessDivide::getDivideMonth, month)
                .orderByDesc(KpiProjGuessDivide::getDivideMonth));
        if (ObjectUtil.isEmpty(list)) {
            return;
        }
        BigDecimal maxBigDecimal = new BigDecimal("1.6");
        Map<Long, KpiProjGuessBaseInfo> baseId2Bean = kpiProjGuessBaseInfoService.listByIds(list.stream().map(KpiProjGuessDivide::getKpiProjGuessId).collect(Collectors.toSet())).stream().collect(Collectors.toMap(KpiProjGuessBaseInfo::getId, e -> e, (a, b) -> a));
        Map<String, KpiProjGuessDivide> oldDivide = list.stream().collect(Collectors.toMap(this::getUnique, e -> e, (a, b) -> a.getDivideMonth() >= b.getDivideMonth() ? a : b));
        Map<Long, List<KpiProjGuessDivide>> baseId2Divides = kpiProjGuessDivides.stream().collect(Collectors.groupingBy(KpiProjGuessDivide::getKpiProjGuessId));
        //开始计算
        kpiProjGuessBaseInfos.forEach(base -> {
            //利润提奖-调整值 计算公式=  项目利润（累计值））✖️基础提奖比例✖️项目类型系数✖️ 项目规模系数－上一期利润提奖（累计值）-利润提奖（当期值） 注意：项目规模系数需取当月值
            //1.先计算当前总的利润提奖 项目利润（累计值））
            List<KpiProjGuessDivide> guessDivides = baseId2Divides.get(base.getId());
            ProjectRadioConfig config = JSONUtil.toBean(base.getAwardRatioConfig(), ProjectRadioConfig.class);
            if (ObjectUtil.isNotEmpty(guessDivides)) {
                //开始计算
                guessDivides.forEach(e -> {
                    KpiProjGuessDivide oldProjGuessDivides = oldDivide.get(this.getUnique(e));

                    //sum 的范围为从本年1月份开始计算到当月"
                    if (ObjectUtil.isNotEmpty(oldProjGuessDivides)) {
                        BigDecimal weightValue = LongUtil.tenThousand2Dollar(LongUtil.null2zero(e.getDivideWeight()).toString()).divide(new BigDecimal(100), 6, RoundingMode.HALF_UP);
                        //利润提奖-当期值
                        BigDecimal profitCurrent = new BigDecimal(LongUtil.null2zero(e.getProfitCurrent()));

                        //利润提奖-调整值
                        //项目利润（累计值））✖️基础提奖比例✖️项目类型系数✖️ 项目规模系数  X分配比例*分配比
                        BigDecimal profitAdjust = new BigDecimal(LongUtil.null2zero(base.getProfitTotal()).toString())
                                .multiply(new BigDecimal(e.getProjectRadioConfig() == null ? config.getConfig(base.getProjClassify(), base.getReceiptFirstPaymentDate(), LocalDate.of(year, month, 1)) : e.getProjectRadioConfig()))
                                .multiply(maxBigDecimal.min(new BigDecimal(e.getTypeRadioConfig() == null ? "1" : e.getTypeRadioConfig()).multiply(new BigDecimal(e.getScaleRadioConfig()))));
                        //－上一期利润提奖（累计值）-利润提奖（当期值）
                        profitAdjust = profitAdjust.subtract(new BigDecimal(LongUtil.null2zero(oldProjGuessDivides.getProfitTotal())));
                        profitAdjust = profitAdjust.subtract(profitCurrent);
                        if (profitAdjust.longValue() != 0) {
                            base.setProfitAdjust(profitAdjust.longValue());
                            e.setProfitAdjust(profitAdjust.longValue());
                            e.setProfitTotal(e.getProfitTotal() + e.getProfitAdjust());
                            //利润奖金-调整值
                            //计算公式=利润提奖（调整值）✖️占比
                            BigDecimal bonusAdjust = profitAdjust.multiply(weightValue);
                            e.setBonusAdjust(bonusAdjust.longValue());
                            e.setBonusTotal(e.getBonusTotal() + e.getBonusAdjust());
                        }
                    }
                });
            }
        });

    }


    private String getUnique(KpiProjGuessDivide e ){
        return String.join("-", String.valueOf(e.getContractId()), e.getDivideType(), String.valueOf(e.getDivideTarget()));
    }

    private KpiProjGuessBaseInfo buildGuessBaseInfo(KpiProjGuessBaseInfo existCurrentMonthInfo, KpiProjectDistributionRecordBo distributionBaseInfo, int year, int month,
                                                    KpiFinanceProjectProfitRecordBo projectProfit, KpiProjGuessBaseInfo existLastMonthInfo, List<KpiProjGuessDivide> kpiProjGuessDivideList, KpiPaymentAmountBo paymentAmountBo, Long projYearPaymentAmount,
                                                    LocalDate contractFistDate) {
        // 绩效测算主表数据
        KpiProjGuessBaseInfo toSaveBaseInfo = new KpiProjGuessBaseInfo();
        if (Objects.nonNull(existCurrentMonthInfo)) {
            toSaveBaseInfo.setId(existCurrentMonthInfo.getId());
        }

        toSaveBaseInfo.setKpiProjectDistributionId(distributionBaseInfo.getProjectDistributionId());
        toSaveBaseInfo.setKpiProjectDistributionVersion(distributionBaseInfo.getKpiProjectDistributionVersion());
        toSaveBaseInfo.setContractId(projectProfit.getContractId());
        toSaveBaseInfo.setProjClassify(distributionBaseInfo.getProjClassify());
        //修改
        if(ObjectUtil.isNotEmpty(contractFistDate) && contractFistDate.isAfter(LocalDate.of(LocalDate.now().getYear(), 12, 31))) {
            toSaveBaseInfo.setProjSource(KpiProjectSourceDistributionEnum.NEW.name());
        } else {
            toSaveBaseInfo.setProjSource(KpiProjectSourceDistributionEnum.HISTORY.name());
        }
        toSaveBaseInfo.setCalculateDateYear(year);
        toSaveBaseInfo.setCalculateDateMonth(month);
        if(ObjectUtil.isNotEmpty(projectProfit)) {
            toSaveBaseInfo.setReceiptId(projectProfit.getReceiptId());
            toSaveBaseInfo.setReceiptFirstPaymentDate(projectProfit.getReceiptFirstPaymentDate());
            toSaveBaseInfo.setProfitCurrent(projectProfit.getProfitThisMonth());
        }
        // 回填到主表中
        toSaveBaseInfo.setBonusCurrent(kpiProjGuessDivideList.stream().mapToLong(KpiProjGuessDivide::getBonusCurrent).sum());
        toSaveBaseInfo.setPaymentCurrent(kpiProjGuessDivideList.stream().filter(e -> ObjectUtil.isNotEmpty(e.getPaymentCurrent())).mapToLong(KpiProjGuessDivide::getPaymentCurrent).sum());
        toSaveBaseInfo.setProjPaymentYearAmount(projYearPaymentAmount);
        if (ObjectUtil.isNotEmpty(paymentAmountBo)) {
            toSaveBaseInfo.setContractPaymentMonthAmount(paymentAmountBo.getPaymentAmount());
        }
        if (Objects.nonNull(existLastMonthInfo)) {
            //toSaveBaseInfo.setBonusTotal(toSaveBaseInfo.getBonusCurrent() + existLastMonthInfo.getBonusTotal());
            toSaveBaseInfo.setPaymentTotal(toSaveBaseInfo.getPaymentCurrent() + existLastMonthInfo.getPaymentTotal());
        } else {
            //toSaveBaseInfo.setBonusTotal(toSaveBaseInfo.getBonusTotal());
            toSaveBaseInfo.setPaymentTotal(toSaveBaseInfo.getPaymentTotal());
        }
        //奖金-l累计值 = 明细提交值
        toSaveBaseInfo.setBonusTotal(kpiProjGuessDivideList.get(0).getProfitTotal());
        //利润-累计值 取自【财务管理-项目利润】-累计值（累计扣费后利润）
        toSaveBaseInfo.setProfitTotal(projectProfit.getTotalProfitThisYear());
        return toSaveBaseInfo;
    }

    private List<KpiProjGuessDivide> buildGuessDivide(List<KpiProjGuessDivide> projGuessDivideThisMonthList, List<KpiProjGuessDivide> projGuessDividePreMonthList,
                                                      KpiProjectDistributionRecordBo distributionRecordBo, KpiFinanceProjectProfitRecordBo projectProfitBo, KpiParameterConfigBo parameterConfig,
                                                      int year, int month, KpiPaymentAmountBo paymentAmountBo, Set<Long> toDeleteIds, Long paymentAmount) {
        if (ObjectUtil.isEmpty(distributionRecordBo) || ObjectUtil.isEmpty(parameterConfig) || ObjectUtil.isEmpty(projectProfitBo)) {
            return null;
        }
        Map<String, KpiProjGuessDivide> projGuessDivideThisMonthMap = projGuessDivideThisMonthList.stream().collect(Collectors.toMap(e -> e.getContractId() + "-" + e.getDivideType() + "-" + e.getDivideTarget(), e -> e, (a, b) -> b));
        Map<String, KpiProjGuessDivide> projGuessDividePreMonthMap = projGuessDividePreMonthList.stream().collect(Collectors.toMap(e -> e.getContractId() + "-" + e.getDivideType() + "-" + e.getDivideTarget(), e -> e, (a, b) -> b));
        List<KpiProjectDistributionWeightInfoRecordBo> weightLibList = distributionRecordBo.getWeightInfoList();
        if(ObjectUtil.isEmpty(weightLibList)) {
            return null;
        }
        // 遍历分润比来处理
        List<KpiProjGuessDivide> kpiProjGuessDivideList = new LinkedList<>();
        //项目利润
        BigDecimal projectProfit = projectProfitBo == null ? null : new BigDecimal(projectProfitBo.getProfitThisMonth());
        if (projectProfit == null) {
            return null;
        }
        //计算提奖比例
        //基础提奖比例
        BigDecimal projectRadioConfig = BigDecimal.ZERO;
        //项目规模系数
        String scaleRadioConfig = "1";
        //项目类型系数
        String typeRadioConfig = "1";
        //投放奖金系数
        BigDecimal paymentBonusRadioConfig = BigDecimal.ZERO;
        ProjectRadioConfig projectRadioConfigBo = parameterConfig.getProjectRadioConfig();
        if (ObjectUtil.isNotEmpty(projectRadioConfigBo)) {
            projectRadioConfig = new BigDecimal(Optional.ofNullable(projectRadioConfigBo.getConfig(distributionRecordBo.getProjClassify(), projectProfitBo.getReceiptFirstPaymentDate(), LocalDate.of(year, month, 1))).orElse("0")).divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP);
        }
        ProjectScaleRadioConfig projectScaleRadioConfigBo = parameterConfig.getProjectScaleRadioConfig();
        //借据起租日期在2024年1月1日后
        if (ObjectUtil.isNotEmpty(projectScaleRadioConfigBo) && ObjectUtil.isNotEmpty(projectProfitBo.getReceiptFirstPaymentDate()) && !projectProfitBo.getReceiptFirstPaymentDate().isBefore(PROJECT_SCALE_RADIO_DATE)) {
            scaleRadioConfig = projectScaleRadioConfigBo.getConfig(LongUtil.tenThousand2Dollar(LongUtil.null2zero(paymentAmount).toString()));
        }
        ProjectTypeRadioConfig projectTypeRadioConfigBo = parameterConfig.getProjectTypeRadioConfig();
        if (ObjectUtil.isNotEmpty(projectTypeRadioConfigBo)) {
            typeRadioConfig = projectTypeRadioConfigBo.getConfig(projectProfitBo.getBizType(), projectProfitBo.getLeaseType());
        }
        ProjectPaymentBonusRadioConfig paymentBonusRadioConfigBo = parameterConfig.getProjectPaymentBonusRadioConfig();
        if (ObjectUtil.isNotEmpty(paymentBonusRadioConfigBo)) {
            paymentBonusRadioConfig = new BigDecimal(paymentBonusRadioConfigBo.getConfig(Optional.ofNullable(KpiProjectClassifyEnum.find(distributionRecordBo.getProjClassify())).map(KpiProjectClassifyEnum::display).orElse(KpiProjectClassifyEnum.PUBLIC.display())));
            paymentBonusRadioConfig = paymentBonusRadioConfig.divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP);
        }
        //基础提奖比例 * (min(项目规模系数 * 项目类型系数, 1.6))
        BigDecimal rate = projectRadioConfig.multiply(new BigDecimal("1.6").min(new BigDecimal(scaleRadioConfig).multiply(new BigDecimal(typeRadioConfig))));
        for (KpiProjectDistributionWeightInfoRecordBo weightLib : weightLibList) {
            if (ObjectUtil.isEmpty(weightLib.getWeightTarget()) || Objects.isNull(weightLib.getWeightValue())) {
                continue;
            }
            if (weightLib.getWeightValue() == 0) {
                continue;
            }
            //占比
            BigDecimal weightValue = LongUtil.tenThousand2Dollar(weightLib.getWeightValue().toString()).divide(new BigDecimal(100), 6, RoundingMode.HALF_UP);
            KpiProjGuessDivide divide = new KpiProjGuessDivide();
            divide.setContractId(distributionRecordBo.getContractId());
            divide.setDivideYear(year);
            divide.setDivideMonth(month);
            divide.setDivideWeight(weightLib.getWeightValue());
            divide.setDivideType(weightLib.getWeightType());
            divide.setDivideTarget(LongUtil.null2zero(weightLib.getWeightTarget()));
            //填充参数
            divide.setProjectRadioConfig(projectRadioConfig.toPlainString());
            divide.setScaleRadioConfig(scaleRadioConfig);
            divide.setTypeRadioConfig(typeRadioConfig);
            divide.setPaymentBonusRadioConfig(paymentBonusRadioConfig.toPlainString());
            divide.setProjectProfit(projectProfitBo.getProfitThisMonth());

            //项目利润-当期值✖️提奖比例
            divide.setProfitCurrent(projectProfit.multiply(rate).longValue());
            //项目利润-当期值✖️提奖比例✖️占比
            divide.setBonusCurrent(projectProfit.multiply(rate).multiply(weightValue).longValue());

            if (ObjectUtil.isNotEmpty(paymentAmountBo)) {
                //投放提奖 = 投放额*分配权重 -》 投放提奖改为投放金额*投放奖金系数
                divide.setPaymentAwardCurrent(new BigDecimal(LongUtil.null2zero(paymentAmountBo.getPaymentAmount()).toString()).multiply(paymentBonusRadioConfig).longValue());
                //投放奖金 = 投放额*分配权重*0.0005
                divide.setPaymentCurrent(new BigDecimal(divide.getPaymentAwardCurrent()).multiply(weightValue).longValue());
            }
            divide.setProfitTotal(divide.getProfitCurrent());
            divide.setBonusTotal(divide.getBonusCurrent());
            divide.setPaymentTotal(divide.getPaymentCurrent());
            divide.setPaymentAwardTotal(divide.getPaymentAwardCurrent());
            String mapKey = distributionRecordBo.getContractId() + "-" + weightLib.getWeightType() + "-" + weightLib.getWeightTarget();
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
                divide.setPaymentTotal(LongUtil.null2zero(divide.getPaymentCurrent()) + Optional.ofNullable(preExist.getPaymentTotal()).orElse(0L));
                divide.setPaymentAwardTotal(LongUtil.null2zero(divide.getPaymentAwardCurrent()) + Optional.ofNullable(preExist.getPaymentAwardTotal()).orElse(0L));
            }
            divide.setDeptId(projectProfitBo.getAssessDeptId());
            kpiProjGuessDivideList.add(divide);
            // 从map中移除，后续会判断剩余在map中的是本次需要删除的
            projGuessDivideThisMonthMap.remove(mapKey);
        }
        if (CollectionUtil.isNotEmpty(projGuessDivideThisMonthMap)) {
            toDeleteIds.addAll(projGuessDivideThisMonthMap.values().stream().map(KpiProjGuessDivide::getId).collect(Collectors.toSet()));
        }
        return kpiProjGuessDivideList;
    }

}
