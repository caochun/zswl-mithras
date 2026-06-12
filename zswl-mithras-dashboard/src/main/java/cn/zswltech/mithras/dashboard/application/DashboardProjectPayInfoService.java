package cn.zswltech.mithras.dashboard.application;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.zswltech.gruul.dao.dal.entity.OrgDO;
import cn.zswltech.mithras.dto.dashboard.*;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.dashboard.enums.PayInfoQueryDimensionEnum;
import cn.zswltech.mithras.projectprocess.enums.projreview.ProjRegionalClassify;
import cn.zswltech.mithras.riskcontrol.common.RiskControlIndustryClassify;
import cn.zswltech.mithras.dashboard.mapper.DashboardProjectInfoMapper;
import cn.zswltech.mithras.customer.mobile.persistence.model.VisitRecord;
import cn.zswltech.mithras.dashboard.model.DashboardProjectPayInfoQuery;
import cn.zswltech.mithras.dashboard.model.DashboardProjectPayInfoResult;
import cn.zswltech.mithras.payment.model.PaymentBaseInfo;
import cn.zswltech.mithras.payment.model.PaymentCollectionInfo;
import cn.zswltech.mithras.payment.mapper.PaymentBaseInfoMapper;
import cn.zswltech.mithras.payment.mapper.PaymentCollectionInfoMapper;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.dashboard.application.util.DashboardAmountUtil;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.foundation.util.LongUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2024/6/24
 * @description
 */
@Slf4j
@Service
public class DashboardProjectPayInfoService extends DashboardProjectService implements DashboardProjectPayApplicationService {
    @Resource
    private SysUserService sysUserService;
    @Resource
    private DashboardProjectInfoMapper dashboardProjectInfoMapper;
    @Resource
    private PaymentBaseInfoMapper paymentBaseInfoMapper;
    @Resource
    private PaymentCollectionInfoMapper paymentCollectionInfoMapper;

    public List<DashboardProjectPayListRSP> list(DashboardProjectPayListREQ req) {
        DashboardProjectPayInfoQuery query = this.buildQuery(req);
        query.setClientId(req.getClientId());
        query.setContractCode(req.getContractCode());
        query.setActualPayDateTo(req.getActualPayDateTo());
        query.setActualPayDateFrom(req.getActualPayDateFrom());
        DashboardAuthQueryHelper.fillAuthQuery(query);
        List<DashboardProjectPayInfoResult> dbList = this.listPayInfo(query, req.getQueryDimension());
        if (CollectionUtil.isEmpty(dbList)) {
            return Collections.emptyList();
        }
        return this.buildRspList(dbList, dbResult -> {
            DashboardProjectPayListRSP rsp = new DashboardProjectPayListRSP();
            rsp.setActualPayDate(dbResult.getActualPayDate());
            rsp.setContractId(dbResult.getContractId());
            rsp.setContractCode(dbResult.getContractCode());
            if (CharSequenceUtil.equalsAny(dbResult.getRiskControlIndustryClassifyCode(), RiskControlIndustryClassify.PUBLIC_UTILITIES.name(), RiskControlIndustryClassify.CIVIL_CONSUMPTION.name(), RiskControlIndustryClassify.TRAVEL.name())) {
                rsp.setRiskStrategy("公用事业类");
            } else {
                rsp.setRiskStrategy("产业类");
            }
            if (Objects.nonNull(dbResult.getActualPayAmount())) {
                rsp.setActualPayAmount(new ValueUnitDTO(DashboardAmountUtil.toYuanWithoutSplit(dbResult.getActualPayAmount()), "元"));
            }
            if (Objects.nonNull(dbResult.getDuration())) {
                rsp.setDuration(new ValueUnitDTO(dbResult.getDuration().toString(), "月"));
            }
            if (Objects.nonNull(dbResult.getIrr())) {
                BigDecimal b = BigDecimal.valueOf(dbResult.getIrr()).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP);
                rsp.setActualIrr(new ValueUnitDTO(b.toPlainString(), "%"));
            }
            if (Objects.nonNull(dbResult.getLpr())) {
                BigDecimal b = BigDecimal.valueOf(dbResult.getLpr()).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP);
                rsp.setInterestRate(new ValueUnitDTO(b.toPlainString(), "%"));
            }
            if (Objects.nonNull(dbResult.getContractAmount())) {
                if (Objects.nonNull(dbResult.getConsultingFee())) {
                    BigDecimal b = BigDecimal.valueOf(dbResult.getConsultingFee()).divide(BigDecimal.valueOf(dbResult.getContractAmount()), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
                    BigDecimal b1 = BigDecimal.valueOf(dbResult.getPaymentConsultingFee()).divide(BigDecimal.valueOf(dbResult.getContractAmount()), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
                    rsp.setConsultingFeeRate(req.getQueryDimension().equals(PayInfoQueryDimensionEnum.CONTRACT.name()) ? new ValueUnitDTO(b.toPlainString(), "%") : new ValueUnitDTO(b1.toPlainString(), "%"));
                }
                if (Objects.nonNull(dbResult.getCommission())) {
                    BigDecimal b = BigDecimal.valueOf(dbResult.getCommission()).divide(BigDecimal.valueOf(dbResult.getContractAmount()), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
                    BigDecimal b1 = BigDecimal.valueOf(LongUtil.null2zero(dbResult.getPaymentCommission())).divide(BigDecimal.valueOf(dbResult.getContractAmount()), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
                    rsp.setCommissionRate(req.getQueryDimension().equals(PayInfoQueryDimensionEnum.CONTRACT.name()) ? new ValueUnitDTO(b.toPlainString(), "%") : new ValueUnitDTO(b1.toPlainString(), "%"));
                }
            }
            if (Objects.nonNull(dbResult.getEarnestMoney())) {
                rsp.setEarnest(req.getQueryDimension().equals(PayInfoQueryDimensionEnum.CONTRACT.name()) ?
                        new ValueUnitDTO(DashboardAmountUtil.toYuanWithoutSplit(dbResult.getEarnestMoney()), "元") :
                        new ValueUnitDTO(DashboardAmountUtil.toYuanWithoutSplit(dbResult.getPaymentEarnestMoney()), "元"));
            }
            rsp.setRegionalProjectClassifyCode(dbResult.getRegionalProjectClassify());
            rsp.setRegionalProjectClassifyDisplay(Optional.ofNullable(ProjRegionalClassify.of(dbResult.getRegionalProjectClassify())).map(ProjRegionalClassify::display).orElse(""));
            rsp.setPaymentId(dbResult.getPaymentId());
            rsp.setPaymentCode(dbResult.getPaymentCode());
            rsp.setReceiptId(dbResult.getReceiptId());
            rsp.setReceiptCode(dbResult.getReceiptCode());
            return rsp;
        });
    }

    public DashboardProjectPayStatisticsRSP statistics(DashboardProjectPayStatisticsREQ req) {
        DashboardProjectPayInfoQuery query = this.buildQuery(req);
        List<DashboardProjectPayInfoResult> contractList = this.listPayInfo(query, PayInfoQueryDimensionEnum.CONTRACT.name());
        List<DashboardProjectPayInfoResult> receiptList = this.listPayInfo(query, PayInfoQueryDimensionEnum.RECEIPT.name());
        try {
            DashboardProjectPayStatisticsRSP contract = this.convert(contractList, DashboardProjectPayStatisticsRSP.class);
            DashboardProjectPayStatisticsRSP receipt = this.convert(receiptList, DashboardProjectPayStatisticsRSP.class);
            if (Objects.nonNull(receipt) && Objects.nonNull(contract)) {
                contract.setAverageIrr(receipt.getAverageIrr());
                contract.setAverageCommissionRate(receipt.getAverageCommissionRate());
                contract.setAverageContractInterestRate(receipt.getAverageContractInterestRate());
            }
            return contract;
        } catch (Exception e) {
            log.error("【业务工作台-项目视图-投放情况-按公司统计】参数转换发生异常", e);
            throw new MithrasException("系统繁忙，请稍后再试");
        }
    }

    public List<DashboardProjectPayStatisticsByDeptRSP> statisticsListByDept(DashboardProjectPayStatisticsREQ req) {
        DashboardProjectPayInfoQuery query = this.buildQuery(req);
        List<DashboardProjectPayInfoResult> dbList = this.listPayInfo(query, PayInfoQueryDimensionEnum.CONTRACT.name());
        // 按照部门分组
        Map<Long, List<DashboardProjectPayInfoResult>> map;
        if (CollectionUtil.isEmpty(dbList)) {
            map = Collections.emptyMap();
        } else {
            map = dbList.stream().collect(Collectors.groupingBy(DashboardProjectPayInfoResult::getBizDeptId));
        }
        // 获取所有业务部门
        List<OrgDO> orgList = sysUserService.listBizDept();
//        orgList.removeIf(e -> OrgConstants.DISCARD_ORG.contains(e.getCode()));
        orgList.removeIf(e -> Objects.equals(e.getState(), YesOrNoNumberEnum.NO.getCode()));
        return orgList.stream().map(org -> {
            List<DashboardProjectPayInfoResult> list = map.get(org.getId());
            if (CollectionUtil.isEmpty(list)) {
                return DashboardProjectPayStatisticsByDeptRSP.createNoData(org.getId(), org.getName());
            }
            List<DashboardProjectPayInfoResult> res = finishPut(list);
            DashboardProjectPayStatisticsByDeptRSP rsp;
            try {
                rsp = convert(res, DashboardProjectPayStatisticsByDeptRSP.class);
            } catch (Exception e) {
                log.error("【业务工作台-项目视图-投放情况-按部门统计】参数转换发生异常", e);
                throw new MithrasException("系统繁忙，请稍后再试");
            }
            rsp.setBizDeptId(org.getId());
            rsp.setBizDeptName(org.getName());
            return rsp;
        }).sorted(Comparator.comparing(DashboardProjectPayStatisticsRSP::getPayContractQuantity).reversed()).collect(Collectors.toList());
    }

    private List<DashboardProjectPayInfoResult> finishPut(List<DashboardProjectPayInfoResult> list) {
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }
        List<DashboardProjectPayInfoResult> res = new ArrayList<>();
        Set<Long> paymentIds = new HashSet<>();
        for (DashboardProjectPayInfoResult result : list) {
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
        for (DashboardProjectPayInfoResult result : list) {
            PaymentBaseInfo baseInfo = paymentBaseInfoMap.get(result.getPaymentId());
            if (baseInfo != null && baseInfo.getIsFinishPut() != null && baseInfo.getIsFinishPut()) {
                List<PaymentCollectionInfo> byPaymentIdList = collectionInfoMap.get(result.getPaymentId());
                if (byPaymentIdList != null && !byPaymentIdList.isEmpty()) {
                    result.setActualPayAmount(LongUtil.null2zero(result.getActualPayAmount()) - LongUtil.null2zero(byPaymentIdList.get(0).getDownPayment()));
                }
            }
            res.add(result);
        }
        return res;
    }

    /**
     * 这是通用的数据查询方法，所有之前直接调用Mapper的地方都应该使用这个方法
     *
     * @param queryDimension 查询维度
     */
    private List<DashboardProjectPayInfoResult> listPayInfo(DashboardProjectPayInfoQuery query, String queryDimension) {
        // 1. 查询出所有借据维度符合条件的数据
        List<DashboardProjectPayInfoResult> dbList = dashboardProjectInfoMapper.listPayInfo(query);
        // 2、分别封装按月和按年统计的数据列表
        if (CollUtil.isEmpty(dbList)) {
            return dbList;
        }

        if (PayInfoQueryDimensionEnum.CONTRACT.name().equals(queryDimension)) {
            // 按照合同为维度
            Map<Long, List<DashboardProjectPayInfoResult>> map = dbList.stream().collect(Collectors.groupingBy(DashboardProjectPayInfoResult::getContractId));
            if (!map.isEmpty()) {
                for (Map.Entry<Long, List<DashboardProjectPayInfoResult>> entry : map.entrySet()) {
                    List<DashboardProjectPayInfoResult> list = entry.getValue();
                    Collections.sort(list, new Comparator<DashboardProjectPayInfoResult>() {
                        @Override
                        public int compare(DashboardProjectPayInfoResult o1, DashboardProjectPayInfoResult o2) {
                            return o2.getActualPayDate().compareTo(o1.getActualPayDate());
                        }
                    });
                }
            }
            return map.values().stream().map(obj -> {
                DashboardProjectPayInfoResult result = new DashboardProjectPayInfoResult();
                // 只需要将投放的钱加起来就好了
                obj.sort(Comparator.comparing(DashboardProjectPayInfoResult::getActualPayDate));
                BeanUtil.copyProperties(obj.get(0), result);
                long totalPayInAmount = obj.stream().mapToLong(e -> LongUtil.null2zero(e.getActualPayAmount())).summaryStatistics().getSum();
                result.setActualPayAmount(totalPayInAmount);
                return result;
            }).collect(Collectors.toList());
        } else {
            // 按照借据为维度
            Map<Long, List<DashboardProjectPayInfoResult>> map = dbList.stream()
                    .filter(e -> CharSequenceUtil.isNotBlank(e.getReceiptCode()))
                    .collect(Collectors.groupingBy(DashboardProjectPayInfoResult::getReceiptId));
            if (CollUtil.isEmpty(map)) {
                return Collections.emptyList();
            }
            List<DashboardProjectPayInfoResult> resultList = new ArrayList<>(map.size());
            map.forEach((receiptId, list) -> {
                // 按照实际投放日期按照月份维度分组
                LocalDate now = LocalDate.now();
                for (int i = 1; i < 13; i++) {
                    LocalDate beginDate = now.with(TemporalAdjusters.firstDayOfYear()).plusMonths(i - 1);
                    LocalDate endDate = beginDate.with(TemporalAdjusters.lastDayOfMonth());
                    List<DashboardProjectPayInfoResult> projectPayInfoResults = list.stream()
                            .filter(item -> !item.getActualPayDate().isBefore(beginDate) && !item.getActualPayDate().isAfter(endDate))
                            .collect(Collectors.toList());
                    if (CollUtil.isEmpty(projectPayInfoResults)) {
                        continue;
                    }
                    DashboardProjectPayInfoResult result = new DashboardProjectPayInfoResult();
                    projectPayInfoResults.sort(Comparator.comparing(DashboardProjectPayInfoResult::getActualPayDate));
                    BeanUtil.copyProperties(projectPayInfoResults.get(0), result);
                    result.setActualPayAmount(projectPayInfoResults.stream().mapToLong(e -> LongUtil.null2zero(e.getActualPayAmount())).summaryStatistics().getSum());
                    resultList.add(result);
                }
            });
            return resultList;
        }
    }

    private <T extends DashboardProjectPayStatisticsRSP> T convert(List<DashboardProjectPayInfoResult> dbList, Class<T> clz) throws InstantiationException, IllegalAccessException {
        if (CollectionUtil.isEmpty(dbList)) {
            return null;
        }
        //dbList = dbList.stream().filter(e -> Objects.nonNull(e.getIrr())).collect(Collectors.toList());
        BigDecimal tempIrr = BigDecimal.valueOf(0);
        BigDecimal tempCommissionRate = BigDecimal.valueOf(0);
        BigDecimal tempContractRate = BigDecimal.valueOf(0);
        BigDecimal totalPayAmount = BigDecimal.valueOf(dbList.stream().mapToLong(e -> LongUtil.null2zero(e.getActualPayAmount())).summaryStatistics().getSum());
        for (DashboardProjectPayInfoResult dbResult : dbList) {
            tempIrr = tempIrr.add(BigDecimal.valueOf(LongUtil.null2zero(dbResult.getIrr()))
                    .multiply(BigDecimal.valueOf(LongUtil.null2zero(dbResult.getActualPayAmount())))
                    .divide(totalPayAmount, 20, RoundingMode.HALF_UP)
                    .divide(BigDecimal.valueOf(10000), 20, RoundingMode.HALF_UP));
            tempCommissionRate = tempCommissionRate.add(BigDecimal.valueOf(LongUtil.null2zero(dbResult.getPaymentCommission()))
                    .divide(totalPayAmount, 20, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100)));
            tempContractRate = tempContractRate.add(BigDecimal.valueOf(LongUtil.null2zero(dbResult.getLpr()))
                    .divide(BigDecimal.valueOf(10000), 20, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(LongUtil.null2zero(dbResult.getActualPayAmount())))
                    .divide(totalPayAmount, 20, RoundingMode.HALF_UP));
        }
        // 平均
        T t = clz.newInstance();
        t.setPayContractQuantity(dbList.size());
        t.setPayAmount(new ValueUnitDTO(totalPayAmount.divide(BigDecimal.valueOf(100000000), 2, RoundingMode.HALF_UP).toPlainString(), "万元"));
        t.setAverageIrr(new ValueUnitDTO(tempIrr.setScale(2, RoundingMode.HALF_UP).toPlainString(), "%"));
        t.setAverageCommissionRate(new ValueUnitDTO(tempCommissionRate.setScale(2, RoundingMode.HALF_UP).toPlainString(), "%"));
        t.setAverageContractInterestRate(new ValueUnitDTO(tempContractRate.setScale(2, RoundingMode.HALF_UP).toPlainString(), "%"));
        return t;
    }

    private DashboardProjectPayInfoQuery buildQuery(DashboardProjectPayStatisticsREQ req) {
        DashboardProjectPayInfoQuery query = new DashboardProjectPayInfoQuery();
        LocalDate[] queryDateArray = this.transformQueryDate(req);
        query.setQueryDateFrom(queryDateArray[0]);
        query.setQueryDateTo(queryDateArray[1]);
        DashboardAuthQueryHelper.fillAuthQuery(query);
        return query;
    }

    private LocalDate[] transformQueryDate(DashboardProjectPayStatisticsREQ req) {
        LocalDate queryFrom;
        LocalDate queryTo;
        if (Objects.equals(req.getQueryType(), DashboardProjectPayStatisticsREQ.QUERY_TYPE_YEAR)) {
            queryFrom = LocalDateTime.now().with(TemporalAdjusters.firstDayOfYear()).toLocalDate();
            queryTo = LocalDateTime.now().with(TemporalAdjusters.lastDayOfYear()).toLocalDate();
        } else {
            queryFrom = LocalDateTime.now().with(TemporalAdjusters.firstDayOfMonth()).toLocalDate();
            queryTo = LocalDateTime.now().with(TemporalAdjusters.lastDayOfMonth()).toLocalDate();
        }
        return new LocalDate[]{queryFrom, queryTo};
    }
}
