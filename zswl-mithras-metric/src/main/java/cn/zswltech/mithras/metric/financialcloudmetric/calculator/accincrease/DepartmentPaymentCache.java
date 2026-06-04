package cn.zswltech.mithras.metric.financialcloudmetric.calculator.accincrease;

import cn.hutool.core.collection.CollUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.kpi.mapper.KpiProjectDistributionDeptLaunchWeightMapper;
import cn.zswltech.mithras.kpi.mapper.KpiProjectDistributionMapper;
import cn.zswltech.mithras.kpi.mapper.model.KpiProjectDistribution;
import cn.zswltech.mithras.kpi.mapper.model.KpiProjectDistributionDeptLaunchWeight;
import cn.zswltech.mithras.payment.domain.enums.WriteOffStatus;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.PaymentActualDetailMapper;
import cn.zswltech.mithras.payment.infrastructure.persistence.mapper.model.PaymentActualDetail;
import cn.zswltech.mithras.system.service.SysUserService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author bigbear
 * @date 2025/4/22 16:10
 * @description
 */
@Slf4j
@Component
public class DepartmentPaymentCache {

    private final ConcurrentHashMap<String, OrgDO> ORG_MAP = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<Long, Map<Long, Integer>> DEPT_PAYMENT_CACHE = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, List<PaymentActualDetail>> YEAR_PAYMENT_ACTUAL_CACHE = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, List<PaymentActualDetail>> LATER_PAYMENT_ACTUAL_CACHE = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, List<PaymentActualDetail>> MONTH_PAYMENT_ACTUAL_CACHE = new ConcurrentHashMap<>();

    @Resource
    private SysUserService sysUserService;
    @Resource
    private KpiProjectDistributionMapper kpiProjectDistributionMapper;
    @Resource
    private PaymentActualDetailMapper paymentActualDetailMapper;
    @Resource
    private KpiProjectDistributionDeptLaunchWeightMapper kpiProjectDistributionDeptLaunchWeightMapper;

    private void init(LocalDate dateTime) {
        if (!DEPT_PAYMENT_CACHE.isEmpty()) {
            return;
        }
        List<KpiProjectDistribution> distributionList = kpiProjectDistributionMapper.selectList(Wrappers.lambdaQuery());
        if (CollUtil.isEmpty(distributionList)) {
            log.warn("没有找到项目分配信息");
            return;
        }
        Map<Long, KpiProjectDistribution> distributionMap = distributionList.stream().collect(Collectors.toMap(KpiProjectDistribution::getId, Function.identity(), (a, b) -> a));

        // 改为取项目分配表投放占比
        Map<Long, List<KpiProjectDistributionDeptLaunchWeight>> listMap = kpiProjectDistributionDeptLaunchWeightMapper.selectList(Wrappers.<KpiProjectDistributionDeptLaunchWeight>lambdaQuery()
                        .in(KpiProjectDistributionDeptLaunchWeight::getProjectDistributionId, distributionList.stream().map(KpiProjectDistribution::getId).collect(Collectors.toList())))
                .stream().collect(Collectors.groupingBy(KpiProjectDistributionDeptLaunchWeight::getProjectDistributionId));
        for (Map.Entry<Long, List<KpiProjectDistributionDeptLaunchWeight>> entry : listMap.entrySet()) {
            Long distributionId = entry.getKey();
            List<KpiProjectDistributionDeptLaunchWeight> deptWeightList = entry.getValue();
            Map<Long, Integer> deptWeightMap = deptWeightList.stream().collect(Collectors.toMap(KpiProjectDistributionDeptLaunchWeight::getWeightTarget, KpiProjectDistributionDeptLaunchWeight::getWeightValue));

            KpiProjectDistribution distribution = distributionMap.get(distributionId);
            if (Objects.isNull(distribution)) {
                log.warn("项目分配信息不存在");
                continue;
            }
            DEPT_PAYMENT_CACHE.put(distribution.getContractId(), deptWeightMap);
        }

        Map<Long, List<PaymentActualDetail>> map = paymentActualDetailMapper.selectList(Wrappers.<PaymentActualDetail>lambdaQuery()
                        .ge(PaymentActualDetail::getPaidInDate, dateTime.with(TemporalAdjusters.firstDayOfYear()))
                        .le(PaymentActualDetail::getPaidInDate, dateTime.with(TemporalAdjusters.lastDayOfMonth()))
                        .eq(PaymentActualDetail::getWriteOffStatus, WriteOffStatus.WRITTEN_OFF.name()))
                .stream().collect(Collectors.groupingBy(PaymentActualDetail::getContractId));

        YEAR_PAYMENT_ACTUAL_CACHE.putAll(map);

        LATER_PAYMENT_ACTUAL_CACHE.putAll(paymentActualDetailMapper.selectList(Wrappers.<PaymentActualDetail>lambdaQuery()
                        .eq(PaymentActualDetail::getWriteOffStatus, WriteOffStatus.WRITTEN_OFF.name())
                        .le(PaymentActualDetail::getPaidInDate, dateTime.with(TemporalAdjusters.lastDayOfMonth())))
                .stream().collect(Collectors.groupingBy(PaymentActualDetail::getContractId)));

        ORG_MAP.putAll(sysUserService.listBizDept().stream().collect(Collectors.toMap(OrgDO::getCode, Function.identity())));

        MONTH_PAYMENT_ACTUAL_CACHE.putAll(paymentActualDetailMapper.selectList(Wrappers.<PaymentActualDetail>lambdaQuery()
                        .eq(PaymentActualDetail::getWriteOffStatus, WriteOffStatus.WRITTEN_OFF.name())
                        .ge(PaymentActualDetail::getPaidInDate, dateTime.with(TemporalAdjusters.firstDayOfMonth()))
                        .le(PaymentActualDetail::getPaidInDate, dateTime.with(TemporalAdjusters.lastDayOfMonth())))
                .stream().collect(Collectors.groupingBy(PaymentActualDetail::getContractId)));
    }

    public void clear() {
        DEPT_PAYMENT_CACHE.clear();
        ORG_MAP.clear();
        LATER_PAYMENT_ACTUAL_CACHE.clear();
        YEAR_PAYMENT_ACTUAL_CACHE.clear();
        MONTH_PAYMENT_ACTUAL_CACHE.clear();
    }

    public Map<Long, Integer> getDeptWeight(Long contractId, LocalDate dateTime) {
        if (DEPT_PAYMENT_CACHE.isEmpty()) {
            synchronized (DepartmentPaymentCache.class) {
                if (DEPT_PAYMENT_CACHE.isEmpty()) {
                    synchronized (DEPT_PAYMENT_CACHE) {
                        clear();
                        init(dateTime);
                    }
                }
            }
        }
        return DEPT_PAYMENT_CACHE.get(contractId);
    }

    public ConcurrentHashMap<Long, List<PaymentActualDetail>> getYearPaymentActual(LocalDate dateTime) {
        if (YEAR_PAYMENT_ACTUAL_CACHE.isEmpty()) {
            synchronized (DepartmentPaymentCache.class) {
                if (YEAR_PAYMENT_ACTUAL_CACHE.isEmpty()) {
                    synchronized (YEAR_PAYMENT_ACTUAL_CACHE) {
                        clear();
                        init(dateTime);
                    }
                }
            }
        }
        return YEAR_PAYMENT_ACTUAL_CACHE;
    }

    public ConcurrentHashMap<Long, List<PaymentActualDetail>> getLaterPaymentActual(LocalDate dateTime) {
        if (LATER_PAYMENT_ACTUAL_CACHE.isEmpty()) {
            synchronized (DepartmentPaymentCache.class) {
                if (LATER_PAYMENT_ACTUAL_CACHE.isEmpty()) {
                    synchronized (LATER_PAYMENT_ACTUAL_CACHE) {
                        clear();
                        init(dateTime);
                    }
                }
            }
        }
        return LATER_PAYMENT_ACTUAL_CACHE;
    }

    public OrgDO getOrg(String orgCode, LocalDate dateTime) {
        if (ORG_MAP.isEmpty()) {
            synchronized (DepartmentPaymentCache.class) {
                if (ORG_MAP.isEmpty()) {
                    synchronized (ORG_MAP) {
                        clear();
                        init(dateTime);
                    }
                }
            }
        }
        return ORG_MAP.get(orgCode);
    }

    public ConcurrentHashMap<Long, List<PaymentActualDetail>> getMonthPaymentActual(LocalDate dateTime) {
        if (MONTH_PAYMENT_ACTUAL_CACHE.isEmpty()) {
            synchronized (DepartmentPaymentCache.class) {
                if (MONTH_PAYMENT_ACTUAL_CACHE.isEmpty()) {
                    synchronized (MONTH_PAYMENT_ACTUAL_CACHE) {
                        clear();
                        init(dateTime);
                    }
                }
            }
        }
        return MONTH_PAYMENT_ACTUAL_CACHE;
    }
}
