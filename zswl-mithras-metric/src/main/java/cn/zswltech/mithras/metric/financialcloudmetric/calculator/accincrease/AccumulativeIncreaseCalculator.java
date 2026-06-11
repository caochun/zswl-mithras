package cn.zswltech.mithras.metric.financialcloudmetric.calculator.accincrease;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.zswltech.gruul.dao.dal.dao.OrgDOMapper;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.dto.financialcloudmetric.ContractDetail;
import cn.zswltech.mithras.metric.financialcloudmetric.calculator.CalculateDetailCache;
import cn.zswltech.mithras.metric.financialcloudmetric.calculator.DepartmentPerCapitalCalculator;
import cn.zswltech.mithras.metric.financialcloudmetric.calculator.FinancialCloudMetricCalculator;
import cn.zswltech.mithras.metric.financialcloudmetric.calculator.enums.ConditionKey;
import cn.zswltech.mithras.contract.mapper.contract.ContractBaseInfoMapper;
import cn.zswltech.mithras.contract.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.customer.mapper.lib.client.CorpCommerceInfoLibMapper;
import cn.zswltech.mithras.customer.model.client.ClientBaseModel;
import cn.zswltech.mithras.customer.model.client.CorpCommerceInfoLib;
import cn.zswltech.mithras.payment.enums.WriteOffStatus;
import cn.zswltech.mithras.payment.mapper.PaymentActualDetailMapper;
import cn.zswltech.mithras.payment.mapper.model.PaymentActualDetail;
import cn.zswltech.mithras.projectprocess.mapper.projreview.ProjReviewBaseInfoMapper;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.customer.application.lib.client.dto.CorpCommerceInfoLibDto;
import cn.zswltech.mithras.foundation.util.LongUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
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
 * @description: 当年累计的投放额 计算父类 单位（万元）
 * 按部门，按行业，按地域
 * @author: zhaozhengkang
 * @date: 2023/4/13 10:43
 */
@Slf4j
public abstract class AccumulativeIncreaseCalculator implements FinancialCloudMetricCalculator {

    @Resource
    private PaymentActualDetailMapper paymentActualDetailMapper;
    @Resource
    private DepartmentPaymentCache departmentPaymentCache;
    @Resource
    private OrgDOMapper orgDOMapper;
    @Resource
    private CorpCommerceInfoLibMapper commerceInfoLibMapper;
    @Resource
    private ContractBaseInfoMapper contractBaseInfoMapper;
    @Resource
    private ProjReviewBaseInfoMapper projReviewBaseInfoMapper;

    private final Object lock = new Object();
    private static final Map<String, Long> DEPT_CODE_ID = new HashMap<>();
    private static final Map<String, Set<Long>> INDUSTRY_GROUP = new HashMap<>();

    public static void clear() {
        DEPT_CODE_ID.clear();
        INDUSTRY_GROUP.clear();
    }

    /**
     * 指标条件
     *
     * @return
     */
    public abstract Pair<ConditionKey, String> condition();

    @Override
    public BigDecimal calculate(LocalDate dateTime) {
        // 元旦
        LocalDate start = dateTime.with(TemporalAdjusters.firstDayOfYear());
        //计算月的月底
        LocalDate end = dateTime.with(TemporalAdjusters.lastDayOfMonth());
        LambdaQueryWrapper<PaymentActualDetail> qw = Wrappers.<PaymentActualDetail>lambdaQuery()
                .eq(PaymentActualDetail::getWriteOffStatus, WriteOffStatus.WRITTEN_OFF.name())
                .ge(PaymentActualDetail::getPaidInDate, start)
                .le(PaymentActualDetail::getPaidInDate, end);
        Pair<ConditionKey, String> condition = condition();
        List<PaymentActualDetail> pay;
        if (condition != null) {
            if (condition.getKey() == ConditionKey.DEPARTMENT || condition.getKey() == ConditionKey.REGION) {
                Set<Long> targetContractIds = getTargetContractIds();
                if (targetContractIds.isEmpty()) {
                    return BigDecimal.ZERO;
                } else {
                    qw.in(PaymentActualDetail::getContractId, targetContractIds);
                }
            } else {
                // 这里指的是行业
                Set<Long> targetClientIds = getTargetClientIds();
                if (targetClientIds.isEmpty()) {
                    return BigDecimal.ZERO;
                } else {
                    qw.in(PaymentActualDetail::getClientId, targetClientIds);
                }
            }
        }
        pay = paymentActualDetailMapper.selectList(qw);

        // processCalculateDetail(pay);

        return pay.stream().map(PaymentActualDetail::getPaidInAmount)
                .map(LongUtil::null2zero)
                .map(BigDecimal::new)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calculateDeptCurrentYear(LocalDate dateTime) {
        ConcurrentHashMap<Long, List<PaymentActualDetail>> yearPaymentActual = departmentPaymentCache.getYearPaymentActual(dateTime);
        if (yearPaymentActual.isEmpty()) {
            DepartmentPerCapitalCalculator.putNewlyAddedInvestmentScale(condition().getValue(), BigDecimal.ZERO);
            return BigDecimal.ZERO;
        }
        BigDecimal res = BigDecimal.ZERO;
        for (Map.Entry<Long, List<PaymentActualDetail>> entry : yearPaymentActual.entrySet()) {
            Long contractId = entry.getKey();
            List<PaymentActualDetail> paymentActualDetails = entry.getValue();

            // 拿到部门比重信息
            Map<Long, Integer> deptWeight = departmentPaymentCache.getDeptWeight(contractId, dateTime);
            // 如果这里面包含下面设置的部门，则进行计算
            OrgDO org = departmentPaymentCache.getOrg(condition().getValue(), dateTime);
            if (Objects.isNull(org)) {
                continue;
            }
            if (Objects.isNull(deptWeight)) {
                // 查询合同所属部门
                ContractBaseInfo contractBaseInfo = contractBaseInfoMapper.selectById(contractId);
                deptWeight = MapUtil.of(contractBaseInfo.getBizDeptId(), 1000000);
            }
            if (deptWeight.containsKey(org.getId())) {
                long sum = paymentActualDetails.stream().mapToLong(PaymentActualDetail::getPaidInAmount).summaryStatistics().getSum();
                res = res.add(new BigDecimal(sum)
                        .multiply(BigDecimal.valueOf(deptWeight.get(org.getId())))
                        .divide(BigDecimal.valueOf(1000000), 2, RoundingMode.HALF_UP)
                );
            }
        }
        DepartmentPerCapitalCalculator.putNewlyAddedInvestmentScale(condition().getValue(), res);
        return res;
    }


    private void processCalculateDetail(List<PaymentActualDetail> paymentActualDetails) {
        Map<Long, List<PaymentActualDetail>> collect = paymentActualDetails.stream()
                .collect(Collectors.groupingBy(PaymentActualDetail::getContractId));
        List<ContractDetail> contractDetails = new ArrayList<>();
        collect.forEach((contractId, payments) -> {
            if (ObjectUtil.isEmpty(payments)) {
                return;
            }
            ContractDetail contractDetail = new ContractDetail();
            contractDetail.setContractId(contractId);
            contractDetail.setClientId(payments.get(0).getClientId());
            Long total = payments.stream().map(PaymentActualDetail::getPaidInAmount).map(BigDecimal::new)
                    .reduce(BigDecimal.ZERO, BigDecimal::add).divide(new BigDecimal(10000L), 0, RoundingMode.HALF_UP).longValue();
            contractDetail.setAmount(total);
            contractDetails.add(contractDetail);

        });
        CalculateDetailCache.put(metricCode(), JSON.toJSONString(contractDetails));
    }


    private Set<Long> getTargetContractIds() {
        Pair<ConditionKey, String> condition = this.condition();
        List<Long> contractIds;
        switch (condition.getKey()) {
            case DEPARTMENT:
                if (DEPT_CODE_ID.isEmpty()) {
                    synchronized (lock) {
                        if (DEPT_CODE_ID.isEmpty()) {
                            DEPT_CODE_ID.putAll(orgDOMapper.queryAll().stream()
                                    .collect(Collectors.toMap(OrgDO::getCode, OrgDO::getId)));
                        }
                    }
                }
                contractIds = contractBaseInfoMapper.selectList(Wrappers.<ContractBaseInfo>lambdaQuery()
                        .eq(ContractBaseInfo::getBizDeptId, DEPT_CODE_ID.get(condition.getValue())))
                        .stream()
                        .map(ContractBaseInfo::getId).collect(Collectors.toList());

                break;
            case REGION:
                //查询符合的项目
                List<Long> projReviewIds = projReviewBaseInfoMapper.selectList(Wrappers.<ProjReviewBaseInfo>lambdaQuery()
                        .eq(ProjReviewBaseInfo::getProvince, condition.getValue()))
                        .stream()
                        .map(ProjReviewBaseInfo::getId).collect(Collectors.toList());
                if (CollectionUtil.isEmpty(projReviewIds)) {
                    contractIds = Collections.emptyList();
                } else {
                    //按照合同所属业务部门查询符合条件的数据
                    contractIds = contractBaseInfoMapper.selectList(Wrappers.<ContractBaseInfo>lambdaQuery()
                            .in(ContractBaseInfo::getProjReviewId, projReviewIds))
                            .stream()
                            .map(ContractBaseInfo::getId).collect(Collectors.toList());
                }
                break;
            default:
                contractIds = new ArrayList<>();
        }
        return new HashSet<>(contractIds);
    }

    private Set<Long> getTargetClientIds() {
        Pair<ConditionKey, String> condition = this.condition();
        Set<Long> res = new HashSet<>();
        if (Objects.requireNonNull(condition.getKey()) == ConditionKey.INDUSTRY) {
            if (INDUSTRY_GROUP.isEmpty()) {
                synchronized (lock) {
                    if (INDUSTRY_GROUP.isEmpty()) {
                        // 这里的客户信息要合同主承租人的信息
                        Map<String, List<CorpCommerceInfoLib>> industryGroup = commerceInfoLibMapper.listNewestCommerceInfo(new CorpCommerceInfoLibDto()).stream().collect(Collectors.groupingBy(corpCommerceInfoLib -> {
                            String industryType = corpCommerceInfoLib.getIndustryType();
                            if (ObjectUtil.isEmpty(industryType)) {
                                return "NONE";
                            }
                            char c = industryType.charAt(0);
                            if (c >= 'A' && c <= 'T') {
                                return String.valueOf(c);
                            }
                            return "NONE";
                        }));

                        industryGroup.forEach((s, corpCommerceInfoLibs) -> {
                            Set<Long> clientIds = corpCommerceInfoLibs.stream().map(ClientBaseModel::getClientId)
                                    .collect(Collectors.toSet());
                            INDUSTRY_GROUP.put(s, clientIds);
                        });
                    }
                }
            }
            if (!INDUSTRY_GROUP.containsKey(condition.getValue())) {
                return res;
            }
            res.addAll(INDUSTRY_GROUP.get(condition.getValue()));
            //这里不再计算地区的，c
           /* case REGION:
                Set<Long> regionInClientIds = clientService.list(Wrappers.<Client>lambdaQuery()
                                .eq(Client::getProvinceOfAffiliation, this.condition().getValue())
                                .eq(Client::getClientStatus, ClientStatus.TAKE_EFFECT.name()))
                        .stream().map(Client::getId).collect(Collectors.toSet());
                res.addAll(regionInClientIds);
                break;*/
        }
        return res;
    }
}
