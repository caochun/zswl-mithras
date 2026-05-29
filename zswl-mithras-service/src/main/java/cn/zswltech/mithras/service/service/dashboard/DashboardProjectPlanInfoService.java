package cn.zswltech.mithras.service.service.dashboard;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.dto.dashboard.*;
import cn.zswltech.mithras.common.constant.OrgConstants;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.common.enums.ProjectBizType;
import cn.zswltech.mithras.service.enums.kpi.BelongTypeEnum;
import cn.zswltech.mithras.service.enums.kpi.BusinessTypeEnum;
import cn.zswltech.mithras.service.enums.kpi.KpiProjectClassifyEnum;
import cn.zswltech.mithras.service.mapper.dashboard.ContractOrgPlanMapper;
import cn.zswltech.mithras.service.mapper.dashboard.DashboardProjectInfoMapper;
import cn.zswltech.mithras.service.mapper.kpi.PerformanceBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.model.dashboard.ContractOrgPlan;
import cn.zswltech.mithras.service.mapper.model.dashboard.DashboardProjectPayInfoResult;
import cn.zswltech.mithras.service.mapper.model.dashboard.DashboardProjectPlanInfoQuery;
import cn.zswltech.mithras.service.mapper.model.dashboard.DashboardProjectPlanInfoResult;
import cn.zswltech.mithras.service.mapper.model.kpi.PerformanceBaseInfo;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentBaseInfo;
import cn.zswltech.mithras.service.mapper.model.payment.PaymentCollectionInfo;
import cn.zswltech.mithras.service.mapper.payment.PaymentBaseInfoMapper;
import cn.zswltech.mithras.service.mapper.payment.PaymentCollectionInfoMapper;
import cn.zswltech.mithras.service.others.Util;
import cn.zswltech.mithras.service.service.SysUserService;
import cn.zswltech.mithras.service.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


@Slf4j
@Service
public class DashboardProjectPlanInfoService extends DashboardProjectService {

    @Resource
    private DashboardProjectInfoMapper dashboardProjectInfoMapper;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private ContractOrgPlanMapper contractOrgPlanMapper;
    @Resource
    private PerformanceBaseInfoMapper performanceBaseInfoMapper;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Resource
    private PaymentCollectionInfoMapper paymentCollectionInfoMapper;

    public List<DashboardProjectPlanListRSP> planList(DashboardProjectPlanListREQ req) {
        DashboardProjectPlanInfoQuery query = buildQuery(req);
        query.setProjName(req.getProjName());
        query.setBizDeptId(req.getBizDeptId());
        query.setContractCode(req.getContractCode());
        List<DashboardProjectPlanInfoResult> resultList = dashboardProjectInfoMapper.listPlanInfo(query);
        List<DashboardProjectPlanListRSP> rspList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(resultList)){
            rspList = resultList.stream().map(m -> {
                DashboardProjectPlanListRSP rsp = BeanUtil.copyProperties(m, DashboardProjectPlanListRSP.class);
                rsp.setActualPayAmount(new ValueUnitDTO(Util.toWanYuanWithoutSplit(m.getActualPayAmountLong()), "万元"));
                rsp.setContractAmount(new ValueUnitDTO(Util.toWanYuanWithoutSplit(m.getContractAmountLong()), "万元"));
                rsp.setIrr(new ValueUnitDTO(Util.toYuanWithoutSplit(m.getIrrInt().longValue()), "%"));
                rsp.setCommission(new ValueUnitDTO(Util.toWanYuanWithoutSplit(m.getCommissionLong()), "万元"));
                rsp.setEarnestMoney(new ValueUnitDTO(Util.toWanYuanWithoutSplit(m.getEarnestMoneyLong()), "万元"));
                KpiProjectClassifyEnum kpiProjectClassifyEnum = KpiProjectClassifyEnum.find(m.getProjClassify());
                rsp.setProjectClassifyDisplay(kpiProjectClassifyEnum != null ? kpiProjectClassifyEnum.getDisplay() : null);
                rsp.setBizTypeDisplay(Optional.ofNullable(ProjectBizType.of(m.getBizType())).map(ProjectBizType::display).orElse(""));
                if(m.getContractLimit() != null) {
                    BigDecimal contractLimitYear = BigDecimal.valueOf(m.getContractLimit()).divide(new BigDecimal("12"), 2, RoundingMode.HALF_UP);
                    rsp.setContractLimitYear(new ValueUnitDTO(contractLimitYear.toString(),"年"));
                }
                return rsp;
            }).collect(Collectors.toList());
        }
        return rspList;
    }

    public DashboardProjectPlanStatisticsRSP statistics(DashboardProjectPlanStatisticsREQ req) {
        DashboardProjectPlanInfoQuery query = buildQuery(req);
        List<DashboardProjectPlanInfoResult> resultList = dashboardProjectInfoMapper.listPlanInfo(query);
        if (CollUtil.isEmpty(resultList)) {
            return null;
        }
        DashboardProjectPlanStatisticsRSP rsp = new DashboardProjectPlanStatisticsByDeptRSP();
        if(CollectionUtils.isNotEmpty(resultList)) {
            Map<Long, List<DashboardProjectPlanInfoResult>> map = resultList.stream().collect(Collectors.groupingBy(DashboardProjectPlanInfoResult::getContractId));
            if (!map.isEmpty()) {
                for (Map.Entry<Long, List<DashboardProjectPlanInfoResult>> entry : map.entrySet()) {
                    List<DashboardProjectPlanInfoResult> list = entry.getValue();
                    Collections.sort(list, new Comparator<DashboardProjectPlanInfoResult>() {
                        @Override
                        public int compare(DashboardProjectPlanInfoResult o1, DashboardProjectPlanInfoResult o2) {
                            return o2.getActualPayDate().compareTo(o1.getActualPayDate());
                        }
                    });
                }
            }
            List<DashboardProjectPlanInfoResult> res = map.values().stream().map(obj -> {
                DashboardProjectPlanInfoResult result = new DashboardProjectPlanInfoResult();
                // 只需要将投放的钱加起来就好了
                obj.sort(Comparator.comparing(DashboardProjectPlanInfoResult::getActualPayDate));
                BeanUtil.copyProperties(obj.get(0), result);
                long totalPayInAmount = obj.stream().mapToLong(e -> LongUtil.null2zero(e.getActualPayAmountLong())).summaryStatistics().getSum();
                result.setActualPayAmountLong(totalPayInAmount);
                return result;
            }).collect(Collectors.toList());
            //res = res.stream().filter(e -> Objects.nonNull(e.getIrr())).collect(Collectors.toList());
            rsp.setPayContractQuantity(res.size());
            long payAmount = res.stream().mapToLong(DashboardProjectPlanInfoResult::getActualPayAmountLong).sum();
            Map<Long, Long> planAmountMap = getPlanAmount(req, query);
            long planPayAmount = planAmountMap.values().stream().mapToLong(Long::valueOf).sum();
            if(planPayAmount != 0 && !Objects.equals(planPayAmount,0L)){
                BigDecimal finishRate = BigDecimal.valueOf(payAmount).divide(BigDecimal.valueOf(planPayAmount), 2, RoundingMode.HALF_UP);
                rsp.setPlanPayAmount(new ValueUnitDTO(Util.toWanYuanWithoutSplit(planPayAmount), "万元"));
                rsp.setFinishRate(new ValueUnitDTO(String.valueOf(finishRate.multiply(new BigDecimal("100")).intValue()), "%"));
            }
            rsp.setPayAmount(new ValueUnitDTO(Util.toWanYuanWithoutSplit(payAmount), "万元"));
        }
        return rsp;
    }

    public List<DashboardProjectPlanStatisticsByDeptRSP> statisticsGroupByDept(DashboardProjectPlanStatisticsREQ req) {
        DashboardProjectPlanInfoQuery query = buildQuery(req);
        List<DashboardProjectPlanInfoResult> dbList = dashboardProjectInfoMapper.listPlanInfo(query);
        List<DashboardProjectPlanInfoResult> resultList = listPlanInfo(dbList);
        Map<Long, Long> planAmountMap = getPlanAmount(req, query);
        List<DashboardProjectPlanStatisticsByDeptRSP> rspList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(resultList)){
            Map<Long, List<DashboardProjectPlanInfoResult>> planMap = resultList.stream().collect(Collectors.groupingBy(DashboardProjectPlanInfoResult::getBizDeptId));
            List<OrgDO> orgList = sysUserService.listBizDept();
//            orgList.removeIf(e -> OrgConstants.DISCARD_ORG.contains(e.getCode()));
            orgList.removeIf(e -> Objects.equals(e.getState(), YesOrNoNumberEnum.NO.getCode()));
            rspList = orgList.stream().map(orgDO -> {
                DashboardProjectPlanStatisticsByDeptRSP rsp = DashboardProjectPlanStatisticsByDeptRSP.createNoData(orgDO.getId(), orgDO.getName());
                List<DashboardProjectPlanInfoResult> res = planMap.get(orgDO.getId());
                List<DashboardProjectPlanInfoResult> planList = finishPut(res);
                Long planPayAmount = planAmountMap.get(orgDO.getId());
                if(planPayAmount != null){
                    rsp.setPlanPayAmount(new ValueUnitDTO(Util.toWanYuanWithoutSplit(planPayAmount), "万元"));
                }
                //planList = planList.stream().filter(e -> Objects.nonNull(e.getIrr())).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(planList)) {
                    rsp.setPayContractQuantity(planList.size());
                    long payAmount = planList.stream().mapToLong(e -> LongUtil.null2zero(e.getActualPayAmountLong())).summaryStatistics().getSum();
                    if(planPayAmount != null && !Objects.equals(planPayAmount, 0L)){
                        BigDecimal finishRate = BigDecimal.valueOf(payAmount).divide(BigDecimal.valueOf(planPayAmount),2, RoundingMode.HALF_UP);
                        rsp.setSortedField(finishRate);
                        rsp.setFinishRate(new ValueUnitDTO(String.valueOf(finishRate.multiply(new BigDecimal("100")).intValue()),"%"));
                    }
                    rsp.setPayAmount(new ValueUnitDTO(Util.toWanYuanWithoutSplit(payAmount), "万元"));
                }
                return rsp;
            }).collect(Collectors.toList());
            AtomicInteger rank = new AtomicInteger();
            rspList = rspList.stream().sorted(Comparator.comparing(DashboardProjectPlanStatisticsByDeptRSP::getSortedField).reversed())
                    .peek(m -> m.setFinishRateRank(rank.incrementAndGet())).collect(Collectors.toList());
        }
        return rspList;
    }

    private List<DashboardProjectPlanInfoResult> listPlanInfo(List<DashboardProjectPlanInfoResult> dbList) {
        Map<Long, List<DashboardProjectPlanInfoResult>> map = dbList.stream().collect(Collectors.groupingBy(DashboardProjectPlanInfoResult::getContractId));
        if (!map.isEmpty()) {
            for (Map.Entry<Long, List<DashboardProjectPlanInfoResult>> entry : map.entrySet()) {
                List<DashboardProjectPlanInfoResult> list = entry.getValue();
                Collections.sort(list, new Comparator<DashboardProjectPlanInfoResult>() {
                    @Override
                    public int compare(DashboardProjectPlanInfoResult o1, DashboardProjectPlanInfoResult o2) {
                        return o2.getActualPayDate().compareTo(o1.getActualPayDate());
                    }
                });
            }
        }
        return map.values().stream().map(obj -> {
            DashboardProjectPlanInfoResult result = new DashboardProjectPlanInfoResult();
            // 只需要将投放的钱加起来就好了
            obj.sort(Comparator.comparing(DashboardProjectPlanInfoResult::getActualPayDate));
            BeanUtil.copyProperties(obj.get(0), result);
            long totalPayInAmount = obj.stream().mapToLong(e -> LongUtil.null2zero(e.getActualPayAmountLong())).summaryStatistics().getSum();
            result.setActualPayAmountLong(totalPayInAmount);
            return result;
        }).collect(Collectors.toList());
    }

    private DashboardProjectPlanInfoQuery buildQuery(DashboardProjectPlanStatisticsREQ req) {
        DashboardProjectPlanInfoQuery query = new DashboardProjectPlanInfoQuery();
        LocalDate[] queryDateArray = this.transformQueryDate(req);
        query.setQueryDateFrom(queryDateArray[0]);
        query.setQueryDateTo(queryDateArray[1]);
        query.fillAuthQuery();
        return query;
    }

    private LocalDate[] transformQueryDate(DashboardProjectPlanStatisticsREQ req) {
        LocalDate now = LocalDate.now();
        LocalDate queryFrom;
        LocalDate queryTo;
        if (Objects.equals(req.getQueryType(), DashboardProjectPlanStatisticsREQ.QUERY_TYPE_YEAR)) {
            queryFrom = now.with(TemporalAdjusters.firstDayOfYear());
            queryTo = now.with(TemporalAdjusters.lastDayOfYear());
        } else {
            queryFrom = now.with(TemporalAdjusters.firstDayOfMonth());
            queryTo = now.with(TemporalAdjusters.lastDayOfMonth());
        }
        return new LocalDate[] {queryFrom, queryTo};
    }


    private Map<Long,Long> getPlanAmount(DashboardProjectPlanStatisticsREQ req, DashboardProjectPlanInfoQuery query) {
        // 年度计划取 performance_base_info表 ，月度计划取 contract_org_plan
        LocalDate queryDateFrom = query.getQueryDateFrom();
        Map<Long,Long> planAmountMap = null;
        if(DashboardProjectPlanStatisticsREQ.QUERY_TYPE_MONTH.equals(req.getQueryType())) {
            List<ContractOrgPlan> contractOrgPlans = contractOrgPlanMapper.selectList(Wrappers.<ContractOrgPlan>lambdaQuery()
                    .eq(ContractOrgPlan::getYear,queryDateFrom.getYear())
                    .eq(ContractOrgPlan::getMonth,queryDateFrom.getMonth()));
            if(CollectionUtils.isNotEmpty(contractOrgPlans)){
                planAmountMap = contractOrgPlans.stream().collect(Collectors.toMap(ContractOrgPlan::getDeptId, ContractOrgPlan::getPlanAmount));
            }
        }else{
            List<PerformanceBaseInfo> performanceBaseInfos = performanceBaseInfoMapper.selectList(Wrappers.<PerformanceBaseInfo>lambdaQuery()
                    .eq(PerformanceBaseInfo::getYear, queryDateFrom.getYear())
                    .eq(PerformanceBaseInfo::getBusinessType, BusinessTypeEnum.DEPT_TOTAL.name())
                    .eq(PerformanceBaseInfo::getBelongType, BelongTypeEnum.DEPARTMENT.name()));
            if(CollectionUtils.isNotEmpty(performanceBaseInfos)){
                planAmountMap = performanceBaseInfos.stream().collect(Collectors.toMap(PerformanceBaseInfo::getBelongDeptId, PerformanceBaseInfo::getAdvertisingAmount,(m1,m2) -> m1));
            }
        }
        return Optional.ofNullable(planAmountMap).orElse(new HashMap<>());
    }

    private List<DashboardProjectPlanInfoResult> finishPut(List<DashboardProjectPlanInfoResult> list) {
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }
        List<DashboardProjectPlanInfoResult> res = new ArrayList<>();
        Set<Long> paymentIds = new HashSet<>();
        for (DashboardProjectPlanInfoResult result : list) {
            paymentIds.add(result.getPaymentId());
        }
        List<PaymentBaseInfo> baseInfoList = paymentBaseInfoMapper.selectList(Wrappers.<PaymentBaseInfo>lambdaQuery()
                .in(CollectionUtil.isNotEmpty(paymentIds), PaymentBaseInfo::getId, paymentIds));
        List<PaymentCollectionInfo> collectionInfoList = paymentCollectionInfoMapper.selectList(Wrappers.<PaymentCollectionInfo>lambdaQuery()
                .in(CollectionUtil.isNotEmpty(paymentIds), PaymentCollectionInfo::getPaymentId, paymentIds));
        Map<Long, PaymentBaseInfo> paymentBaseInfoMap = new HashMap<>();
        Map<Long, List<PaymentCollectionInfo>> collectionInfoMap = new HashMap<>();
        for (PaymentBaseInfo baseInfo : baseInfoList) {
            paymentBaseInfoMap.putIfAbsent(baseInfo.getId(), baseInfo);
        }
        for (PaymentCollectionInfo paymentCollectionInfo : collectionInfoList) {
            collectionInfoMap.putIfAbsent(paymentCollectionInfo.getPaymentId(), new ArrayList<>());
            collectionInfoMap.get(paymentCollectionInfo.getPaymentId()).add(paymentCollectionInfo);
        }
        for (DashboardProjectPlanInfoResult result : list) {
            PaymentBaseInfo baseInfo = paymentBaseInfoMap.get(result.getPaymentId());
            if (baseInfo != null && baseInfo.getIsFinishPut() != null && baseInfo.getIsFinishPut()) {
                List<PaymentCollectionInfo> byPaymentIdList = collectionInfoMap.get(result.getPaymentId());
                if (byPaymentIdList != null && !byPaymentIdList.isEmpty()) {
                    result.setActualPayAmountLong(LongUtil.null2zero(result.getActualPayAmountLong()) - LongUtil.null2zero(byPaymentIdList.get(0).getDownPayment()));
                }
            }
            res.add(result);
        }
        return res;
    }
}
