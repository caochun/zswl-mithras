package cn.zswltech.mithras.metric.financialcloudmetric.calculator.investbalance;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.gruul.dao.dal.dao.OrgDOMapper;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.dto.financialcloudmetric.ContractDetail;
import cn.zswltech.mithras.metric.financialcloudmetric.calculator.CalculateDetailCache;
import cn.zswltech.mithras.metric.financialcloudmetric.calculator.DepartmentPerCapitalCalculator;
import cn.zswltech.mithras.metric.financialcloudmetric.calculator.FinancialCloudMetricCalculator;
import cn.zswltech.mithras.metric.financialcloudmetric.calculator.accincrease.DepartmentPaymentCache;
import cn.zswltech.mithras.metric.financialcloudmetric.calculator.enums.ConditionKey;
import cn.zswltech.mithras.service.enums.contract.ContractStatus;
import cn.zswltech.mithras.service.enums.contract.LesseeTypeEnum;
import cn.zswltech.mithras.service.mapper.lib.client.CorpCommerceInfoLibMapper;
import cn.zswltech.mithras.service.mapper.model.client.CorpCommerceInfoLib;
import cn.zswltech.mithras.service.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.service.mapper.model.contract.ContractTenantry;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentActualDetail;
import cn.zswltech.mithras.service.mapper.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.service.service.contract.ContractBaseInfoService;
import cn.zswltech.mithras.service.service.contract.ContractTenantryService;
import cn.zswltech.mithras.service.service.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.service.service.riskcontrol.RemainingPrincipalServiceImpl;
import cn.zswltech.mithras.service.service.riskcontrol.dto.CorpCommerceInfoLibDto;
import cn.zswltech.mithras.riskcontrol.exposure.RemainingPrincipalQueryDto;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import liquibase.pro.packaged.L;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @description: 存量投放余额计算父类  单位:万元
 * 按部门，按行业，按地域
 * @author: zhaozhengkang
 * @date: 2023/4/13 10:43
 */
@Slf4j
public abstract class InventoryInvestmentBalanceCalculator implements FinancialCloudMetricCalculator {

    @Resource
    private OrgDOMapper orgDOMapper;
    @Resource
    private DepartmentPaymentCache departmentPaymentCache;
    @Resource
    private CorpCommerceInfoLibMapper commerceInfoLibMapper;
    @Resource
    private RemainingPrincipalServiceImpl remainingPrincipalServiceImpl;
    @Resource
    private ContractBaseInfoService contractBaseInfoService;

    private final Object lock = new Object();
    private static final Map<String, Long> DEPT_CODE_ID = new HashMap<>();
    private static final Map<String, Set<Long>> INDUSTRY_GROUP = new HashMap<>();

    protected abstract Pair<ConditionKey, String> condition();


    public static void clear() {
        DEPT_CODE_ID.clear();
        INDUSTRY_GROUP.clear();
    }

    public BigDecimal calculateDeptCumulative(LocalDate dateTime) {
        dateTime = dateTime.with(TemporalAdjusters.lastDayOfMonth());
        ConcurrentHashMap<Long, List<PaymentActualDetail>> yearPaymentActual = departmentPaymentCache.getLaterPaymentActual(dateTime);
        if (yearPaymentActual.isEmpty()) {
            DepartmentPerCapitalCalculator.putStockInvestmentBalance(condition().getValue(), BigDecimal.ZERO);
            return BigDecimal.ZERO;
        }
        BigDecimal res = BigDecimal.ZERO;
        Set<Long> collect = contractBaseInfoService.listByIds(yearPaymentActual.keySet())
                .stream().filter(e -> CharSequenceUtil.equalsAny(e.getContractStatus(), ContractStatus.START_RENT.name(), ContractStatus.TAKE_EFFECT.name(), ContractStatus.NEW.name()))
                .map(ContractBaseInfo::getId).collect(Collectors.toSet());
        Map<Long, BigDecimal> remainingPrincipal = remainingPrincipalServiceImpl.remainingPrincipal(collect, dateTime.with(TemporalAdjusters.lastDayOfMonth()));
        for (Long contractId : collect) {
            // 拿到部门比重信息
            Map<Long, Integer> deptWeight = departmentPaymentCache.getDeptWeight(contractId, dateTime);
            if (Objects.isNull(deptWeight)) {
                // 查询合同所属部门
                ContractBaseInfo contractBaseInfo = contractBaseInfoService.getById(contractId);
                deptWeight = MapUtil.of(contractBaseInfo.getBizDeptId(), 1000000);
            }
            // 如果这里面包含下面设置的部门，则进行计算
            OrgDO org = departmentPaymentCache.getOrg(condition().getValue(), dateTime);
            if (Objects.isNull(org)) {
                continue;
            }
            if (deptWeight.containsKey(org.getId())) {
                res = res.add(remainingPrincipal.get(contractId)
                        .multiply(BigDecimal.valueOf(deptWeight.get(org.getId())))
                        .divide(BigDecimal.valueOf(1000000), 4, RoundingMode.HALF_UP)
                );
            }
        }
        DepartmentPerCapitalCalculator.putStockInvestmentBalance(condition().getValue(), res);
        return res;
    }

    @Override
    public BigDecimal calculate(LocalDate dateTime) {
        Pair<ConditionKey, String> condition = condition();
        if (condition == null) {
            return calculateFullContract(dateTime);
        }
        RemainingPrincipalQueryDto dto = new RemainingPrincipalQueryDto();
        dto.setEndDate(dateTime.with(TemporalAdjusters.lastDayOfMonth()));
        Set<Long> targetContractIds = getTargetContractIds();
        if (targetContractIds == null) {
            calculateFullContract(dateTime);
        }
        if (targetContractIds != null && targetContractIds.isEmpty()) {
            return BigDecimal.ZERO;
        }
        dto.setContractIds(targetContractIds);

        Map<Long, BigDecimal> remainingPrincipal = remainingPrincipalServiceImpl.remainingPrincipal(dto.getContractIds(), dto.getEndDate());
        BigDecimal res = BigDecimal.ZERO;
        for (Long contractId : dto.getContractIds()) {
            res = res.add(Optional.ofNullable(remainingPrincipal.get(contractId)).orElse(BigDecimal.ZERO));
        }
        return res;
    }


    private BigDecimal calculateFullContract(LocalDate dateTime) {
        RemainingPrincipalQueryDto dto = new RemainingPrincipalQueryDto();
        dto.setEndDate(dateTime.with(TemporalAdjusters.lastDayOfMonth()));
        Map<Long, BigDecimal> remainingPrincipals = remainingPrincipalServiceImpl.remainingPrincipalGroupByContractId(dto);
        processDetails(remainingPrincipals);
        return remainingPrincipals.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void processDetails(Map<Long, BigDecimal> remainingPrincipals) {
        // 处理计算明细信息
        List<ContractDetail> contractDetails = new ArrayList<>();
        for (Map.Entry<Long, BigDecimal> entry : remainingPrincipals.entrySet()) {
            if (entry.getValue() == null || entry.getValue().equals(BigDecimal.ZERO)) {
                continue;
            }
            ContractDetail contractDetail = new ContractDetail();
            contractDetail.setContractId(entry.getKey());
            contractDetail.setAmount(entry.getValue().divide(new BigDecimal(10000L), 0, RoundingMode.HALF_UP).longValue());
            contractDetails.add(contractDetail);
        }
        CalculateDetailCache.put(metricCode(), JSON.toJSONString(contractDetails));
    }

    private Set<Long> getTargetContractIds() {
        Pair<ConditionKey, String> condition = this.condition();
        if (condition == null) {
            return null;
        }
        Set<Long> res = new HashSet<>();
        switch (condition.getKey()) {
            case INDUSTRY:
                if (INDUSTRY_GROUP.isEmpty()) {
                    synchronized (lock) {
                        if (INDUSTRY_GROUP.isEmpty()) {
                            // ====》 20250716:由于数据库中的客户id不一致问题，这个地方从全量合同出发
                            List<ContractBaseInfo> contractBaseInfos = contractBaseInfoService.list(Wrappers.<ContractBaseInfo>lambdaQuery()
                                    .notIn(ContractBaseInfo::getContractStatus, ContractStatus.SETTLE.name(), ContractStatus.INVALID.name(), ContractStatus.CLOSED.name()));
                            List<ContractTenantry> contractTenantryList = SpringUtil.getBean(ContractTenantryService.class)
                                    .list(Wrappers.<ContractTenantry>lambdaQuery()
                                            .in(ContractTenantry::getContractId, contractBaseInfos.stream().map(ContractBaseInfo::getId).collect(Collectors.toSet()))
                                            .eq(ContractTenantry::getLesseeType, LesseeTypeEnum.MAIN_LESSSEE.name()));
                            Map<String, List<ContractTenantry>> collect = contractTenantryList.stream().filter(e -> Objects.nonNull(e.getRentConcatAccountId())).collect(Collectors.groupingBy(ContractTenantry::getRentConcatAccountId));
                            Set<Long> tempClientIds = contractTenantryList.stream().map(ContractTenantry::getRentConcatAccountId)
                                    .filter(Objects::nonNull).map(Long::valueOf).collect(Collectors.toSet());
                            // 找到里面的的国标行业分类进行筛选
                            CorpCommerceInfoLibDto dto = new CorpCommerceInfoLibDto();
                            dto.setInClientIds(tempClientIds);
                            commerceInfoLibMapper.listNewestCommerceInfo(dto)
                                    .stream().collect(Collectors.groupingBy(corpCommerceInfoLib -> {
                                        String industryType = corpCommerceInfoLib.getIndustryType();
                                        if (ObjectUtil.isEmpty(industryType)) {
                                            return "NONE";
                                        }
                                        char c = industryType.charAt(0);
                                        if (c >= 'A' && c <= 'T') {
                                            return String.valueOf(c);
                                        }
                                        return "NONE";
                                    })).forEach((s, corpCommerceInfoLibs) -> {
                                        Set<Long> tmp = new HashSet<>(32);
                                        for (CorpCommerceInfoLib lib : corpCommerceInfoLibs) {
                                            List<ContractTenantry> list = collect.get(lib.getClientId().toString());
                                            if (CollUtil.isNotEmpty(list)) {
                                                tmp.addAll(list.stream().map(ContractTenantry::getContractId).collect(Collectors.toList()));
                                            }
                                        }
                                        INDUSTRY_GROUP.put(s, tmp);
                                    });
                        }
                    }
                }
                if (!INDUSTRY_GROUP.containsKey(condition.getValue())) {
                    return res;
                }
                res.addAll(INDUSTRY_GROUP.get(condition.getValue()));
                break;
            case REGION:
                // 找合同
                List<Long> projReviewIds = SpringUtil.getBean(ProjReviewBaseInfoService.class).list(Wrappers.<ProjReviewBaseInfo>lambdaQuery()
                                .eq(ProjReviewBaseInfo::getProvince, condition.getValue()))
                        .stream().map(ProjReviewBaseInfo::getId).collect(Collectors.toList());
                if (!CollUtil.isEmpty(projReviewIds)) {
                    //按照合同所属业务部门查询符合条件的数据
                    List<Long> collected = SpringUtil.getBean(ContractBaseInfoService.class).list(Wrappers.<ContractBaseInfo>lambdaQuery()
                                    .in(ContractBaseInfo::getProjReviewId, projReviewIds)
                                    .notIn(ContractBaseInfo::getContractStatus, ContractStatus.SETTLE.name(), ContractStatus.INVALID.name(), ContractStatus.CLOSED.name()))
                            .stream().map(ContractBaseInfo::getId).collect(Collectors.toList());
                    res.addAll(collected);
                }
                break;
            case DEPARTMENT:
                if (DEPT_CODE_ID.isEmpty()) {
                    synchronized (lock) {
                        if (DEPT_CODE_ID.isEmpty()) {
                            DEPT_CODE_ID.putAll(orgDOMapper.queryAll().stream()
                                    .collect(Collectors.toMap(OrgDO::getCode, OrgDO::getId)));
                        }
                    }
                }
                List<ContractBaseInfo> infoList = SpringUtil.getBean(ContractBaseInfoService.class).list(Wrappers.<ContractBaseInfo>lambdaQuery()
                        .notIn(ContractBaseInfo::getContractStatus, ContractStatus.SETTLE.name(), ContractStatus.INVALID.name(), ContractStatus.CLOSED.name())
                        .eq(ContractBaseInfo::getBizDeptId, DEPT_CODE_ID.get(condition().getValue())));
                res.addAll(infoList.stream().map(ContractBaseInfo::getId).collect(Collectors.toSet()));
        }
        return res;
    }
}
