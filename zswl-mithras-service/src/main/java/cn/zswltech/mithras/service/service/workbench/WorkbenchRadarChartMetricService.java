package cn.zswltech.mithras.service.service.workbench;

import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.mithras.dto.workbench.WorkbenchMetricReq;
import cn.zswltech.mithras.dto.workbench.chart.RadarChartValueVO;
import cn.zswltech.mithras.dto.workbench.chart.sub.RadarDataVO;
import cn.zswltech.mithras.service.enums.CashFlowItemEnum;
import cn.zswltech.mithras.workbench.domain.enums.WorkbenchMetricDeptScope;
import cn.zswltech.mithras.workbench.domain.enums.WorkbenchMetricRole;
import cn.zswltech.mithras.workbench.domain.enums.WorkbenchMetricUnit;
import cn.zswltech.mithras.customer.infrastructure.persistence.mapper.model.client.Client;
import cn.zswltech.mithras.service.mapper.model.collection.CollectionBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.*;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentActualDetail;
import cn.zswltech.mithras.workbench.infrastructure.persistence.mapper.model.WorkbenchRadarChartMetric;
import cn.zswltech.mithras.workbench.infrastructure.persistence.mapper.WorkbenchRadarChartMetricMapper;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.service.client.ClientService;
import cn.zswltech.mithras.service.service.collection.CollectionBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractPriceService;
import cn.zswltech.mithras.contract.versioning.application.ContractAocPriceLibService;
import cn.zswltech.mithras.contract.versioning.application.ContractFactoringPriceLibService;
import cn.zswltech.mithras.contract.versioning.application.ContractLeasePriceLibService;
import cn.zswltech.mithras.service.service.payment.PaymentActualDetailService;
import cn.zswltech.mithras.service.util.BigDecimalUtil;
import cn.zswltech.mithras.service.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhaozhengkang
 * @description 工作台-龙虎雷达图
 * @date 2023-05-10
 */
@Service
public class WorkbenchRadarChartMetricService
        extends ServiceImpl<WorkbenchRadarChartMetricMapper, WorkbenchRadarChartMetric> {

    @Resource
    private SysUserService sysUserService;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;
    @Resource
    private PaymentActualDetailService paymentActualDetailService;
    @Resource
    private ContractLeasePriceLibService contractLeasePriceLibService;
    @Resource
    private ContractFactoringPriceLibService contractFactoringPriceLibService;
    @Resource
    private ContractAocPriceLibService contractAocPriceLibService;
    @Resource
    private ContractPriceService contractPriceService;
    @Resource
    private CollectionBaseInfoService collectionBaseInfoService;
    @Resource
    private ClientService clientService;


    public static final Map<String, String> METRIC_NAME_RSP_MAP = new HashMap<>();

    static {
        METRIC_NAME_RSP_MAP.put("投放", "本年人均新增投放");
        METRIC_NAME_RSP_MAP.put("咨询费", "本年人均新增咨询费");
        METRIC_NAME_RSP_MAP.put("合同数量", "本年人均新增合同数量");
        METRIC_NAME_RSP_MAP.put("利润", "利润");
        METRIC_NAME_RSP_MAP.put("回款及时率", "回款及时率");
    }


    public RadarChartValueVO radarChart(WorkbenchMetricReq req) {
        WorkbenchMetricRole role = WorkbenchMetricRole.valueOf(req.getCurrentRoleCode());
        switch (role) {
            case COMPREHENSIVE_MANAGEMENT:
                return comprehensiveManagement();
            case XMJL:
                return businessPersonnel();
            case JSSYB_BDS:
            case HGJCYWB_BDS:
            case JXHJGYWB_BDS:
            case JCSSYWB_BDS:
            case LLWLTD_BDS:
            case XJZZHXJJTD_BDS:
            case JTYSYWB_BDS:
            case GGSY_BDS:
                return bds(req.getCurrentRoleCode());
            default:
                return null;
        }
    }

    /**
     * 综合管理人员的雷达图
     */
    private RadarChartValueVO comprehensiveManagement() {
        List<String> scopes = Arrays.stream(WorkbenchMetricDeptScope.values()).map(Enum::name)
                .filter(v -> !"ZSZL".equals(v)).collect(Collectors.toList());
        scopes.add("DEPT_MAX");

        Map<String, List<WorkbenchRadarChartMetric>> group = baseMapper.selectList(
                        Wrappers.<WorkbenchRadarChartMetric>lambdaQuery()
                                .in(WorkbenchRadarChartMetric::getDeptScop, scopes))
                .stream().collect(Collectors.groupingBy(WorkbenchRadarChartMetric::getDeptScop));
        Map<String, List<RadarDataVO>> data = new HashMap<>();
        group.forEach((scope, metrics) -> {
            List<RadarDataVO> radarDataVos = metrics.stream()
                    .map(metric -> new RadarDataVO(METRIC_NAME_RSP_MAP.get(metric.getMetricName()), metric.getValue(),
                            WorkbenchMetricUnit.valueOf(metric.getUnit()).display()))
                    .collect(Collectors.toList());
            if ("DEPT_MAX".equals(scope)) {
                data.put("MAX", radarDataVos);
            } else {
                data.put(WorkbenchMetricDeptScope.valueOf(scope).display(), radarDataVos);
            }
        });
        return new RadarChartValueVO("龙虎雷达图", data, "部门单项最大值");
    }

    /**
     * 业务部门主管的雷达图
     *
     * @param role
     * @return
     */
    private RadarChartValueVO bds(String role) {
        List<String> scopes = new ArrayList<>();
        scopes.add("DEPT_MAX");
        scopes.add(role.split("_")[0]);
        scopes.add("ZSZL");
        Map<String, List<WorkbenchRadarChartMetric>> group = baseMapper.selectList(
                        Wrappers.<WorkbenchRadarChartMetric>lambdaQuery()
                                .in(WorkbenchRadarChartMetric::getDeptScop, scopes))
                .stream().collect(Collectors.groupingBy(WorkbenchRadarChartMetric::getDeptScop));
        Map<String, List<RadarDataVO>> data = new HashMap<>();
        group.forEach((scope, metrics) -> {
            List<RadarDataVO> radarDataVos = metrics.stream()
                    .map(metric -> new RadarDataVO(METRIC_NAME_RSP_MAP.get(metric.getMetricName()), metric.getValue(),
                            WorkbenchMetricUnit.valueOf(metric.getUnit()).display()))
                    .collect(Collectors.toList());
            if ("DEPT_MAX".equals(scope)) {
                data.put("MAX", radarDataVos);
            } else if ("ZSZL".equals(scope)) {
                data.put("公司平均值", radarDataVos);
            } else {
                data.put("本部门均值", radarDataVos);
            }
        });
        return new RadarChartValueVO("龙虎雷达图", data, "部门单项最大值");
    }

    /**
     * 业务人员的雷达图
     *
     * @return
     */
    private RadarChartValueVO businessPersonnel() {
        List<String> scopes = new ArrayList<>();
        scopes.add("PERSON_MAX");
        scopes.add("ZSZL");
        Map<String, List<WorkbenchRadarChartMetric>> group = baseMapper.selectList(
                        Wrappers.<WorkbenchRadarChartMetric>lambdaQuery()
                                .in(WorkbenchRadarChartMetric::getDeptScop, scopes))
                .stream().collect(Collectors.groupingBy(WorkbenchRadarChartMetric::getDeptScop));
        Map<String, List<RadarDataVO>> data = new HashMap<>();
        group.forEach((scope, metrics) -> {
            List<RadarDataVO> radarDataVos = metrics.stream()
                    .map(metric -> new RadarDataVO(metric.getMetricName(), metric.getValue(),
                            WorkbenchMetricUnit.valueOf(metric.getUnit()).display()))
                    .collect(Collectors.toList());
            if ("PERSON_MAX".equals(scope)) {
                data.put("MAX", radarDataVos);
            } else {
                data.put("公司平均值", radarDataVos);
            }
        });

        Long userId = AccountUtil.getLoginInfo().getId();
//        if (userId == 3L) {
//            userId = 68L;
//        }
        List<ContractBaseInfo> contractBaseInfos = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                .eq(ContractBaseInfo::getProjSponsorUserId, userId)
                .ge(ContractBaseInfo::getCreateTime, LocalDate.now().atStartOfDay().with(TemporalAdjusters.firstDayOfYear()))
                .in(ContractBaseInfo::getContractStatus, "TAKE_EFFECT", "START_RENT", "SETTLE"));
        Set<Long> contractIds = contractBaseInfos.stream().map(ContractBaseInfo::getId).collect(Collectors.toSet());
        // 本年新增合同数量
        List<RadarDataVO> personalValue = new ArrayList<>();
        BigDecimal totalLaunch = calculateLaunchAmount(userId);
        personalValue.add(new RadarDataVO("投放", totalLaunch.toString(), "万元"));
        BigDecimal profit = calculateProfit(contractIds);
        personalValue.add(new RadarDataVO("利润", profit.toString(), "%"));
        BigDecimal collectionRate = calculateCollectionRate(userId);
        personalValue.add(new RadarDataVO("回款及时率", collectionRate.toString(), "%"));
        BigDecimal totalFee = calculateConsultingFees(contractIds, 1);
        personalValue.add(new RadarDataVO("咨询费", totalFee.toString(), "万元"));
        BigDecimal contractNum = new BigDecimal(contractBaseInfos.size());
        personalValue.add(new RadarDataVO("合同数量", contractNum.toString(), "个"));
        data.put("个人龙虎值", personalValue);

        return new RadarChartValueVO("龙虎雷达图", data, "个人单项最大值");
    }

    public void calculate() {
        Map<String, WorkbenchRadarChartMetric> scopeMap = baseMapper.selectList(
                        Wrappers.<WorkbenchRadarChartMetric>lambdaQuery())
                .stream()
                .collect(Collectors.toMap(metric -> String.join("_", metric.getDeptScop(), metric.getMetricName()), v -> v));
        WorkbenchRadarChartMetric deptMaxContractNum = scopeMap.get(String.join("_", "DEPT_MAX", "合同数量"));
        deptMaxContractNum.setValue("0");
        WorkbenchRadarChartMetric deptMaxLaunchAmount = scopeMap.get(String.join("_", "DEPT_MAX", "投放"));
        deptMaxLaunchAmount.setValue("0");
        WorkbenchRadarChartMetric deptMaxFee = scopeMap.get(String.join("_", "DEPT_MAX", "咨询费"));
        deptMaxFee.setValue("0");
        WorkbenchRadarChartMetric deptMaxCollectionRate = scopeMap.get(String.join("_", "DEPT_MAX", "回款及时率"));
        deptMaxCollectionRate.setValue("0");
        WorkbenchRadarChartMetric deptMaxProfit = scopeMap.get(String.join("_", "DEPT_MAX", "利润"));
        deptMaxProfit.setValue("0");

        for (WorkbenchMetricDeptScope deptScope : WorkbenchMetricDeptScope.values()) {
            //计算部门人数
            int personCount;
            if (deptScope == WorkbenchMetricDeptScope.ZSZL) {
                personCount = sysUserService.countUserByRoleAndOrg(null);
            } else {
                personCount = sysUserService.countUserByRoleAndOrg(deptScope.name());
            }

            List<ContractBaseInfo> contractBaseInfos = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                    .eq(!"ZSZL".equals(deptScope.name()), ContractBaseInfo::getBizDeptId, sysUserService.getOrgIdByCode(deptScope.name()))
                    .ge(ContractBaseInfo::getCreateTime, LocalDate.now().atStartOfDay().with(TemporalAdjusters.firstDayOfYear()))
                    .in(ContractBaseInfo::getContractStatus, "TAKE_EFFECT", "START_RENT", "SETTLE"));
            Set<Long> contractIds = contractBaseInfos.stream().map(ContractBaseInfo::getId).collect(Collectors.toSet());
            if (personCount == 0) {
                continue;
            }
            // 本年人均新增合同数量
            BigDecimal averageContractNum = new BigDecimal(contractBaseInfos.size()).divide(new BigDecimal(personCount), 0, RoundingMode.HALF_UP);
            scopeMap.get(String.join("_", deptScope.name(), "合同数量")).setValue(averageContractNum.toString());
            // 本年人均新增投放
            BigDecimal totalLaunch = calculateLaunchAmount(deptScope);
            WorkbenchRadarChartMetric averageLaunch = scopeMap.get(String.join("_", deptScope.name(), "投放"));
            averageLaunch.setValue(BigDecimalUtil.divide(totalLaunch, new BigDecimal(personCount), 2).toString());
            // 本年人均新增咨询费累计额
            BigDecimal totalFee = calculateConsultingFees(contractIds, personCount);
            scopeMap.get(String.join("_", deptScope.name(), "咨询费")).setValue(totalFee.toString());
            // 本年新增回款及时率
            BigDecimal collectionRate = calculateCollectionRate(deptScope);
            scopeMap.get(String.join("_", deptScope.name(), "回款及时率")).setValue(collectionRate.toString());
            // 利润
            BigDecimal profit = calculateProfit(contractIds);
            scopeMap.get(String.join("_", deptScope.name(), "利润")).setValue(profit.toString());

            if (deptScope != WorkbenchMetricDeptScope.ZSZL) {
                if (averageContractNum.compareTo(stringToBigDecimal(deptMaxContractNum.getValue())) > 0) {
                    deptMaxContractNum.setValue(averageContractNum.toString());
                }

                if (new BigDecimal(averageLaunch.getValue()).compareTo(stringToBigDecimal(deptMaxLaunchAmount.getValue())) > 0) {
                    deptMaxLaunchAmount.setValue(averageLaunch.getValue());
                }
                if (totalFee.compareTo(stringToBigDecimal(deptMaxFee.getValue())) > 0) {
                    deptMaxFee.setValue(totalFee.toString());
                }
                if (collectionRate.compareTo(stringToBigDecimal(deptMaxCollectionRate.getValue())) > 0) {
                    deptMaxCollectionRate.setValue(collectionRate.toString());
                }
                if (profit.compareTo(stringToBigDecimal(deptMaxProfit.getValue())) > 0) {
                    deptMaxProfit.setValue(profit.toString());
                }
            }
        }
        //计算最大个人龙虎值
        WorkbenchRadarChartMetric personMaxContractNum = scopeMap.get(String.join("_", "PERSON_MAX", "合同数量"));
        personMaxContractNum.setValue("0");
        WorkbenchRadarChartMetric personMaxLaunchAmount = scopeMap.get(String.join("_", "PERSON_MAX", "投放"));
        personMaxLaunchAmount.setValue("0");
        WorkbenchRadarChartMetric personMaxFee = scopeMap.get(String.join("_", "PERSON_MAX", "咨询费"));
        personMaxFee.setValue("0");
        WorkbenchRadarChartMetric personMaxCollectionRate = scopeMap.get(String.join("_", "PERSON_MAX", "回款及时率"));
        personMaxCollectionRate.setValue("0");
        WorkbenchRadarChartMetric personMaxProfit = scopeMap.get(String.join("_", "PERSON_MAX", "利润"));
        personMaxProfit.setValue("0");

        Set<Long> businessUseIds = sysUserService.getUserIdsByRole(WorkbenchMetricRole.XMJL.name());
        for (Long userId : businessUseIds) {
            List<ContractBaseInfo> contractBaseInfos = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                    .eq(ContractBaseInfo::getProjSponsorUserId, userId)
                    .ge(ContractBaseInfo::getCreateTime, LocalDate.now().atStartOfDay().with(TemporalAdjusters.firstDayOfYear()))
                    .in(ContractBaseInfo::getContractStatus, "TAKE_EFFECT", "START_RENT", "SETTLE"));
            Set<Long> contractIds = contractBaseInfos.stream().map(ContractBaseInfo::getId).collect(Collectors.toSet());

            BigDecimal contractNum = new BigDecimal(contractBaseInfos.size());
            BigDecimal totalLaunch = calculateLaunchAmount(userId);
            BigDecimal totalFee = calculateConsultingFees(contractIds, 1);
            BigDecimal collectionRate = calculateCollectionRate(userId);
            BigDecimal profit = calculateProfit(contractIds);

            if (contractNum.compareTo(stringToBigDecimal(personMaxContractNum.getValue())) > 0) {
                personMaxContractNum.setValue(contractNum.toString());
            }
            if (totalLaunch.compareTo(stringToBigDecimal(personMaxLaunchAmount.getValue())) > 0) {
                personMaxLaunchAmount.setValue(totalLaunch.toString());
            }
            if (totalFee.compareTo(stringToBigDecimal(personMaxFee.getValue())) > 0) {
                personMaxFee.setValue(totalFee.toString());
            }
            if (collectionRate.compareTo(stringToBigDecimal(personMaxCollectionRate.getValue())) > 0) {
                personMaxCollectionRate.setValue(collectionRate.toString());
            }
            if (profit.compareTo(stringToBigDecimal(personMaxProfit.getValue())) > 0) {
                personMaxProfit.setValue(profit.toString());
            }
        }
        updateBatchById(scopeMap.values());
    }

    /**
     * 付款核销完毕金额本年累计额，万元 小数点后两位
     *
     * @return
     */
    private BigDecimal calculateLaunchAmount(WorkbenchMetricDeptScope deptScope) {
        Set<Long> targetClientIds;
        if ("ZSZL".equals(deptScope.name())) {
            targetClientIds = null;
        } else {
            targetClientIds = clientService.list(Wrappers.<Client>lambdaQuery()
                            .eq(Client::getBelongDeptId, sysUserService.getOrgIdByCode(deptScope.name()))
                            .eq(Client::getClientStatus, "TAKE_EFFECT")).stream().map(Client::getId)
                    .collect(Collectors.toSet());
        }
        return paymentActualDetailService.writtenOffDetailsByClientIds(targetClientIds,
                        LocalDate.now().with(TemporalAdjusters.firstDayOfYear()), null).stream()
                .map(PaymentActualDetail::getPaidInAmount)
                .map(LongUtil::null2zero)
                .map(BigDecimal::new)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(100000000L), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateLaunchAmount(Long userId) {
        Set<Long> targetClientIds = clientService.list(Wrappers.<Client>lambdaQuery()
                .eq(Client::getBelongSponsorId, userId)
                .eq(Client::getClientStatus, "TAKE_EFFECT")).stream().map(Client::getId).collect(Collectors.toSet());
        if (ObjectUtil.isEmpty(targetClientIds)) {
            return BigDecimal.ZERO;
        }

        List<PaymentActualDetail> actualDetails = paymentActualDetailService.writtenOffDetailsByClientIds(
                targetClientIds, LocalDate.now().with(TemporalAdjusters.firstDayOfYear()), null);

        BigDecimal targetAmount = actualDetails.stream().map(PaymentActualDetail::getPaidInAmount)
                .map(LongUtil::null2zero).map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add);

        return targetAmount.divide(BigDecimal.valueOf(100000000L), 2, RoundingMode.HALF_UP);
    }

    /**
     * 咨询费本年累计额，万元 小数点后两位
     *
     * @return
     */
    private BigDecimal calculateConsultingFees(Set<Long> contractIds, int personCount) {
        if (ObjectUtil.isEmpty(contractIds)) {
            return BigDecimal.ZERO;
        }
        List<ContractLeasePriceLib> contractLeasePriceLibs = contractLeasePriceLibService.queryNewestLib(contractIds);
        List<ContractFactoringPriceLib> contractFactoringPriceLibs = contractFactoringPriceLibService.queryNewestLib(contractIds);
        List<ContractAocPriceLib> contractAocPriceLibs = contractAocPriceLibService.queryNewestLib(contractIds);
        BigDecimal totalFee = contractLeasePriceLibs.stream().map(c -> {
                    // 增加手续费
                    Long consultingFee = Objects.isNull(c.getConsultingFee()) ? 0L : c.getConsultingFee();
                    Long commission = Objects.isNull(c.getCommission()) ? 0L : c.getCommission();
                    return consultingFee + commission ;
                })
                .map(LongUtil::null2zero).map(BigDecimal::new)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        totalFee = contractFactoringPriceLibs.stream().map(ContractFactoringPriceLib::getConsultingFee)
                .map(LongUtil::null2zero).map(BigDecimal::new)
                .reduce(totalFee, BigDecimal::add);
        totalFee = contractAocPriceLibs.stream().map(ContractAocPriceLib::getConsultingFee)
                .map(LongUtil::null2zero).map(BigDecimal::new)
                .reduce(totalFee, BigDecimal::add);


        return totalFee.divide(BigDecimal.valueOf(personCount * 100000000L), 2, RoundingMode.HALF_UP);
    }

    /**
     * 100%-(本年累计额逾期本金/本年累计应还本金)
     *
     * @return
     */
    private BigDecimal calculateCollectionRate(WorkbenchMetricDeptScope deptScope) {
        LocalDate start = LocalDate.now().with(TemporalAdjusters.firstDayOfYear());
        LocalDate end = LocalDate.now().with(TemporalAdjusters.lastDayOfYear());
        Set<Long> targetContractIds = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                .eq(!"ZSZL".equals(deptScope.name()), ContractBaseInfo::getBizDeptId, sysUserService.getOrgIdByCode(deptScope.name()))
                .in(ContractBaseInfo::getContractStatus, "START_RENT")).stream().map(ContractBaseInfo::getId).collect(Collectors.toSet());
        if (ObjectUtil.isEmpty(targetContractIds)) {
            return BigDecimal.valueOf(100);
        }
        List<CollectionBaseInfo> collections = collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                .in(CollectionBaseInfo::getContractId, targetContractIds)
                .ge(CollectionBaseInfo::getPlanCollectionDate, start)
                .le(CollectionBaseInfo::getPlanCollectionDate, end));
        BigDecimal totalPrincipal = collections.stream().map(CollectionBaseInfo::getPrincipal)
                .map(LongUtil::null2zero)
                .map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalOverduePrincipal = collections.stream()
                .filter(CollectionBaseInfo::overdued)
                .map(CollectionBaseInfo::getPrincipal).map(LongUtil::null2zero)
                .map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal tmp = totalPrincipal.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : totalOverduePrincipal.divide(totalPrincipal, 6, RoundingMode.HALF_UP);
        return new BigDecimal(1).subtract(tmp).multiply(new BigDecimal(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateCollectionRate(Long userId) {
        LocalDate start = LocalDate.now().with(TemporalAdjusters.firstDayOfYear());
        LocalDate end = LocalDate.now().with(TemporalAdjusters.lastDayOfYear());

        Set<Long> targetContractIds = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                .eq(ContractBaseInfo::getProjSponsorUserId, userId)
                .in(ContractBaseInfo::getContractStatus, "START_RENT")).stream().map(ContractBaseInfo::getId).collect(Collectors.toSet());
        if (ObjectUtil.isEmpty(targetContractIds)) {
            return BigDecimal.valueOf(100);
        }
        List<CollectionBaseInfo> collections = collectionBaseInfoService.list(Wrappers.<CollectionBaseInfo>lambdaQuery()
                .eq(CollectionBaseInfo::getCashFlowItem, CashFlowItemEnum.RENT.name())
                .in(CollectionBaseInfo::getContractId, targetContractIds)
                .ge(CollectionBaseInfo::getPlanCollectionDate, start)
                .le(CollectionBaseInfo::getPlanCollectionDate, end));
        BigDecimal totalPrincipal = collections.stream().map(CollectionBaseInfo::getPrincipal)
                .map(LongUtil::null2zero)
                .map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalOverduePrincipal = collections.stream()
                .filter(CollectionBaseInfo::overdued)
                .map(CollectionBaseInfo::getPrincipal).map(LongUtil::null2zero)
                .map(BigDecimal::new).reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalPrincipal.compareTo(BigDecimal.ZERO) == 0) {
            // 防止除以0
            totalPrincipal = BigDecimal.ONE;
        }
        BigDecimal tmp = totalOverduePrincipal.divide(totalPrincipal, 6, RoundingMode.HALF_UP);
        return new BigDecimal(1).subtract(tmp).multiply(new BigDecimal(100))
                .setScale(2, RoundingMode.HALF_UP);
    }


    /**
     * 本年新增合同加权平均IRR，%
     *
     * @return
     */
    private BigDecimal calculateProfit(Set<Long> contractIds) {
        // 获取这些合同的最新版本的金额(key) 和 利率(value)
        Map<Long, Pair<Long, Integer>> rateMap = contractPriceService.queryNewestIrrRate(contractIds);
        if (rateMap.isEmpty()) {
            return BigDecimal.ZERO;
        }
        // 计算合同总金额
        BigDecimal totalAmount = rateMap.values().stream().map(Pair::getKey)
                .filter(Objects::nonNull).map(BigDecimal::valueOf).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal average = BigDecimal.ZERO;
        // 计算加权总额
        for (Map.Entry<Long, Pair<Long, Integer>> entry : rateMap.entrySet()) {
            Long contractAmount = entry.getValue().getKey();
            Integer contractRate = entry.getValue().getValue();
            average = totalAmount.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : BigDecimal.valueOf(contractAmount).divide(totalAmount, 10, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(contractRate)).add(average);
        }
        return average.divide(new BigDecimal(10000), 2, RoundingMode.HALF_UP);
    }


    public BigDecimal stringToBigDecimal(String str) {
        if (ObjectUtil.isEmpty(str)) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(str);
    }
}