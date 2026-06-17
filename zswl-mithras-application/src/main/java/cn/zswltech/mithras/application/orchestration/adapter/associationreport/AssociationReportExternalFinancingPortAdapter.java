package cn.zswltech.mithras.application.orchestration.adapter.associationreport;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.associationreport.application.AssociationReportExternalFinancingPort;
import cn.zswltech.mithras.associationreport.application.AssociationReportExternalFinancingSnapshot;
import cn.zswltech.mithras.fund.application.financing.FundFinancingCreditRefService;
import cn.zswltech.mithras.fund.application.organization.FundOrganizationService;
import cn.zswltech.mithras.fund.directfinancing.persistence.mapper.FundDirectFinancingBaseInfoMapper;
import cn.zswltech.mithras.fund.directfinancing.persistence.mapper.FundDirectFinancingProductDetailMapper;
import cn.zswltech.mithras.fund.directfinancing.persistence.mapper.FundDirectFinancingRepayActualMapper;
import cn.zswltech.mithras.fund.directfinancing.persistence.mapper.FundDirectFinancingRepayActualSplitMapper;
import cn.zswltech.mithras.fund.directfinancing.persistence.mapper.FundDirectFinancingRepayActualSplitRecordMapper;
import cn.zswltech.mithras.fund.directfinancing.persistence.model.FundDirectFinancingBaseInfo;
import cn.zswltech.mithras.fund.directfinancing.persistence.model.FundDirectFinancingProductDetail;
import cn.zswltech.mithras.fund.directfinancing.persistence.model.FundDirectFinancingRepayActual;
import cn.zswltech.mithras.fund.directfinancing.persistence.model.FundDirectFinancingRepayActualSplit;
import cn.zswltech.mithras.fund.directfinancing.persistence.model.FundDirectFinancingRepayActualSplitRecord;
import cn.zswltech.mithras.fund.enums.DirectFinancingType;
import cn.zswltech.mithras.fund.enums.financing.FinancingTypeEnum;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingBizTypeEnum;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingStatusEnum;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingTimeLimitTypeEnum;
import cn.zswltech.mithras.fund.persistence.mapper.financing.FundFinancingBaseInfoMapper;
import cn.zswltech.mithras.fund.persistence.mapper.financing.FundFinancingPlanMapper;
import cn.zswltech.mithras.fund.persistence.mapper.receiptrepay.FundReceiptFlowDetailMapper;
import cn.zswltech.mithras.fund.persistence.mapper.receiptrepay.FundReceiptRepayBaseInfoMapper;
import cn.zswltech.mithras.fund.persistence.model.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.fund.persistence.model.financing.FundFinancingCreditRef;
import cn.zswltech.mithras.fund.persistence.model.financing.FundFinancingPlan;
import cn.zswltech.mithras.fund.persistence.model.organization.FundOrganization;
import cn.zswltech.mithras.fund.persistence.model.receiptrepay.FundReceiptFlowDetail;
import cn.zswltech.mithras.fund.persistence.model.receiptrepay.FundReceiptRepayBaseInfo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AssociationReportExternalFinancingPortAdapter implements AssociationReportExternalFinancingPort {
    private static final String FINANCE_REPAY_CASH_FLOW_ITEM = "REPAY";
    private static final String PRINCIPAL_WRITE_OFF_ITEM = "PRINCIPAL";

    @Resource
    private FundFinancingBaseInfoMapper fundFinancingBaseInfoMapper;
    @Resource
    private FundFinancingPlanMapper fundFinancingPlanMapper;
    @Resource
    private FundReceiptRepayBaseInfoMapper fundReceiptRepayBaseInfoMapper;
    @Resource
    private FundReceiptFlowDetailMapper fundReceiptFlowDetailMapper;
    @Resource
    private FundFinancingCreditRefService fundFinancingCreditRefService;
    @Resource
    private FundOrganizationService fundOrganizationService;
    @Resource
    private FundDirectFinancingBaseInfoMapper fundDirectFinancingBaseInfoMapper;
    @Resource
    private FundDirectFinancingProductDetailMapper fundDirectFinancingProductDetailMapper;
    @Resource
    private FundDirectFinancingRepayActualMapper fundDirectFinancingRepayActualMapper;
    @Resource
    private FundDirectFinancingRepayActualSplitMapper fundDirectFinancingRepayActualSplitMapper;
    @Resource
    private FundDirectFinancingRepayActualSplitRecordMapper fundDirectFinancingRepayActualSplitRecordMapper;

    @Override
    public List<AssociationReportExternalFinancingSnapshot> listExternalFinancing(LocalDate targetDate) {
        List<AssociationReportExternalFinancingSnapshot> result = new LinkedList<>();
        result.addAll(indirect(targetDate));
        result.addAll(direct(targetDate));
        return result;
    }

    private List<AssociationReportExternalFinancingSnapshot> indirect(LocalDate targetDate) {
        LambdaQueryWrapper<FundFinancingBaseInfo> query = Wrappers.lambdaQuery();
        query.in(FundFinancingBaseInfo::getFinancingStatus, ListUtil.toList(FundFinancingStatusEnum.CARRY_INTEREST.name(), FundFinancingStatusEnum.SETTLE.name()));
        query.le(FundFinancingBaseInfo::getActualLoanDate, targetDate);
        List<FundFinancingBaseInfo> todoList = fundFinancingBaseInfoMapper.selectList(query);
        if (CollectionUtil.isEmpty(todoList)) {
            return Collections.emptyList();
        }
        List<AssociationReportExternalFinancingSnapshot> result = new LinkedList<>();
        for (FundFinancingBaseInfo fundFinancingBaseInfo : todoList) {
            try {
                AssociationReportExternalFinancingSnapshot snapshot = buildFromIndirect(fundFinancingBaseInfo, targetDate);
                if (Objects.nonNull(snapshot)) {
                    result.add(snapshot);
                }
            } catch (Exception e) {
                log.error("金融局报送【对外融资清单】间接融资自动取值发生异常[{}]", JSONUtil.toJsonStr(fundFinancingBaseInfo), e);
            }
        }
        return result;
    }

    private AssociationReportExternalFinancingSnapshot buildFromIndirect(FundFinancingBaseInfo fundFinancingBaseInfo, LocalDate targetDate) {
        FundFinancingPlan fundFinancingPlan = fundFinancingPlanMapper.selectOne(
                Wrappers.<FundFinancingPlan>lambdaQuery()
                        .eq(FundFinancingPlan::getFinancingId, fundFinancingBaseInfo.getId())
                        .last("limit 1")
        );
        FundReceiptRepayBaseInfo fundReceiptRepayBaseInfo = fundReceiptRepayBaseInfoMapper.selectOne(
                Wrappers.<FundReceiptRepayBaseInfo>lambdaQuery()
                        .eq(FundReceiptRepayBaseInfo::getFinancingId, fundFinancingBaseInfo.getId())
                        .isNull(FundReceiptRepayBaseInfo::getFinancingType)
                        .last("limit 1")
        );
        if (Objects.isNull(fundFinancingPlan) || Objects.isNull(fundReceiptRepayBaseInfo)) {
            return null;
        }
        List<FundReceiptFlowDetail> flowDetailList = fundReceiptFlowDetailMapper.selectList(
                Wrappers.<FundReceiptFlowDetail>lambdaQuery()
                        .eq(FundReceiptFlowDetail::getReceiptRepayId, fundReceiptRepayBaseInfo.getId())
        );
        long repayPrincipal = flowDetailList.stream()
                .filter(e -> StrUtil.equals(e.getCashFlowItem(), FINANCE_REPAY_CASH_FLOW_ITEM))
                .filter(e -> !e.getCashFlowDate().isAfter(targetDate))
                .filter(e -> Objects.nonNull(e.getPrincipalAmount()))
                .mapToLong(FundReceiptFlowDetail::getPrincipalAmount)
                .sum();
        long remainingPrincipal = fundFinancingPlan.getFinancingAmount() - repayPrincipal;
        if (remainingPrincipal <= 0) {
            return null;
        }

        String financingBusinessTypeName;
        if (StrUtil.equalsAny(fundFinancingBaseInfo.getBusinessType(), FundFinancingBizTypeEnum.BANK_ACCEPTANCE.name(), FundFinancingBizTypeEnum.COMMERCE_ACCEPTANCE.name(), FundFinancingBizTypeEnum.LETTER_OF_CREDIT.name())) {
            financingBusinessTypeName = "其他";
        } else if (StrUtil.equals(fundFinancingBaseInfo.getTimeLimitType(), FundFinancingTimeLimitTypeEnum.LONG_TERM_LOAN.name())) {
            financingBusinessTypeName = "长期银行贷款";
        } else {
            financingBusinessTypeName = "短期银行贷款";
        }

        String capitalProvider = null;
        List<FundFinancingCreditRef> relationList = fundFinancingCreditRefService.queryByFinancingId(fundFinancingBaseInfo.getId());
        if (CollectionUtil.isNotEmpty(relationList)) {
            List<FundOrganization> orgList = fundOrganizationService.listByIds(relationList.stream().map(FundFinancingCreditRef::getOrganizationId).collect(Collectors.toSet()));
            capitalProvider = Optional.ofNullable(orgList).map(e -> e.get(0).getAbbreviation()).orElse(null);
        }

        int interestRate = Optional.ofNullable(fundFinancingPlan.getLprRatePercent()).orElse(0) + Optional.ofNullable(fundFinancingPlan.getLprAddPercent()).orElse(0);
        return AssociationReportExternalFinancingSnapshot.builder()
                .loanBalance(remainingPrincipal)
                .financingBusinessTypeName(financingBusinessTypeName)
                .capitalProvider(capitalProvider)
                .financingInterestRate(BigDecimal.valueOf(interestRate).divide(BigDecimal.valueOf(1000000), 8, RoundingMode.HALF_UP))
                .financingLoanDate(fundFinancingBaseInfo.getActualLoanDate())
                .financingMaturityDate(fundFinancingBaseInfo.getActualExpireDate())
                .build();
    }

    private List<AssociationReportExternalFinancingSnapshot> direct(LocalDate targetDate) {
        LambdaQueryWrapper<FundDirectFinancingBaseInfo> query = Wrappers.lambdaQuery();
        query.in(FundDirectFinancingBaseInfo::getFinancingStatus, ListUtil.toList(FundFinancingStatusEnum.CARRY_INTEREST.name(), FundFinancingStatusEnum.SETTLE.name()));
        query.le(FundDirectFinancingBaseInfo::getCarryInterestTime, targetDate);
        List<FundDirectFinancingBaseInfo> todoList = fundDirectFinancingBaseInfoMapper.selectList(query);
        if (CollectionUtil.isEmpty(todoList)) {
            return Collections.emptyList();
        }
        List<AssociationReportExternalFinancingSnapshot> result = new LinkedList<>();
        for (FundDirectFinancingBaseInfo fundDirectFinancingBaseInfo : todoList) {
            List<FundDirectFinancingProductDetail> productDetailList = fundDirectFinancingProductDetailMapper.selectList(
                    Wrappers.<FundDirectFinancingProductDetail>lambdaQuery()
                            .eq(FundDirectFinancingProductDetail::getFinancingId, fundDirectFinancingBaseInfo.getId())
                            .orderByAsc(FundDirectFinancingProductDetail::getId)
            );
            if (CollectionUtil.isEmpty(productDetailList)) {
                continue;
            }
            for (FundDirectFinancingProductDetail productDetail : productDetailList) {
                try {
                    AssociationReportExternalFinancingSnapshot snapshot = buildFromDirect(fundDirectFinancingBaseInfo, productDetail, targetDate);
                    if (Objects.nonNull(snapshot)) {
                        result.add(snapshot);
                    }
                } catch (Exception e) {
                    log.error("金融局报送【对外融资清单】直接融资自动取值发生异常[{}]", JSONUtil.toJsonStr(productDetail), e);
                }
            }
        }
        return result;
    }

    private AssociationReportExternalFinancingSnapshot buildFromDirect(FundDirectFinancingBaseInfo fundDirectFinancingBaseInfo, FundDirectFinancingProductDetail productDetail, LocalDate targetDate) {
        AssociationReportExternalFinancingSnapshot.AssociationReportExternalFinancingSnapshotBuilder builder = AssociationReportExternalFinancingSnapshot.builder()
                .capitalProvider(fundDirectFinancingBaseInfo.getConsignee())
                .financingInterestRate(BigDecimal.valueOf(Optional.ofNullable(productDetail.getIssuanceRate()).orElse(0L)).divide(BigDecimal.valueOf(1000000), 8, RoundingMode.HALF_UP))
                .financingLoanDate(fundDirectFinancingBaseInfo.getCarryInterestTime());
        List<FundDirectFinancingRepayActual> repayActualList = fundDirectFinancingRepayActualMapper.selectList(
                Wrappers.<FundDirectFinancingRepayActual>lambdaQuery()
                        .eq(FundDirectFinancingRepayActual::getFinancingId, fundDirectFinancingBaseInfo.getId())
                        .orderByAsc(FundDirectFinancingRepayActual::getRepayDate)
                        .orderByAsc(FundDirectFinancingRepayActual::getPhase)
        );
        if (StrUtil.equalsAny(fundDirectFinancingBaseInfo.getDirectFinancingType(), DirectFinancingType.ABN.name(), DirectFinancingType.ABS.name())) {
            repayActualList.removeIf(e -> e.getRepayDate().isAfter(targetDate));
            Set<String> targetCashFlowCode = repayActualList.stream().map(FundDirectFinancingRepayActual::getCashFlowCode).collect(Collectors.toSet());
            long repayPrincipal = 0L;
            if (CollectionUtil.isNotEmpty(targetCashFlowCode)) {
                List<FundDirectFinancingRepayActualSplitRecord> repayActualSplitRecordList = fundDirectFinancingRepayActualSplitRecordMapper.selectList(
                        Wrappers.<FundDirectFinancingRepayActualSplitRecord>lambdaQuery()
                                .eq(FundDirectFinancingRepayActualSplitRecord::getProductDetailId, productDetail.getId())
                                .eq(FundDirectFinancingRepayActualSplitRecord::getCashFlowItem, PRINCIPAL_WRITE_OFF_ITEM)
                );
                if (CollectionUtil.isEmpty(repayActualSplitRecordList) && StrUtil.equals(fundDirectFinancingBaseInfo.getFinancingStatus(), FundFinancingStatusEnum.SETTLE.name())) {
                    repayPrincipal = Optional.ofNullable(productDetail.getIssuanceAmount()).orElse(0L) * 10000;
                } else {
                    repayPrincipal = repayActualSplitRecordList.stream()
                            .filter(e -> StrUtil.equals(e.getCashFlowItem(), PRINCIPAL_WRITE_OFF_ITEM))
                            .filter(e -> targetCashFlowCode.contains(e.getCashFlowCodeParent()))
                            .filter(e -> Objects.nonNull(e.getWriteOffAmount()))
                            .mapToLong(FundDirectFinancingRepayActualSplitRecord::getWriteOffAmount)
                            .sum();
                }
            }
            long remainingPrincipal = Optional.ofNullable(productDetail.getIssuanceAmount()).orElse(0L) * 10000 - repayPrincipal;
            if (remainingPrincipal <= 0) {
                return null;
            }
            List<FundDirectFinancingRepayActualSplit> repayActualSplitList = fundDirectFinancingRepayActualSplitMapper.selectList(
                    Wrappers.<FundDirectFinancingRepayActualSplit>lambdaQuery()
                            .eq(FundDirectFinancingRepayActualSplit::getProductDetailId, productDetail.getId())
            );
            repayActualSplitList.removeIf(e -> Objects.isNull(e.getRemainingPrincipalAmount()) || e.getRemainingPrincipalAmount() == 0);
            if (Objects.nonNull(productDetail.getExpectedExpirationDate())) {
                builder.financingMaturityDate(productDetail.getExpectedExpirationDate());
            }
            builder.loanBalance(remainingPrincipal)
                    .financingBusinessTypeName("资产证券化融资");
        } else {
            List<FundReceiptFlowDetail> detailList = getFinancingRepayDetail(fundDirectFinancingBaseInfo.getId(), FinancingTypeEnum.DIRECT.name());
            detailList.removeIf(e -> e.getCashFlowDate().isAfter(targetDate));
            long repayPrincipal = 0L;
            if (CollectionUtil.isNotEmpty(detailList)) {
                repayPrincipal = detailList.stream()
                        .filter(e -> StrUtil.equals(e.getCashFlowItem(), FINANCE_REPAY_CASH_FLOW_ITEM))
                        .filter(e -> Objects.nonNull(e.getPrincipalAmount()))
                        .mapToLong(FundReceiptFlowDetail::getPrincipalAmount)
                        .sum();
            }
            long remainingPrincipal = Optional.ofNullable(fundDirectFinancingBaseInfo.getFinancingAmount()).orElse(0L) * 10000 - repayPrincipal;
            if (remainingPrincipal <= 0) {
                return null;
            }
            if (CollectionUtil.isNotEmpty(repayActualList)) {
                repayActualList.sort(Comparator.comparing(FundDirectFinancingRepayActual::getRepayDate).reversed());
                builder.financingMaturityDate(repayActualList.get(0).getRepayDate());
            }
            builder.loanBalance(remainingPrincipal)
                    .financingBusinessTypeName("债券融资");
        }
        return builder.build();
    }

    private List<FundReceiptFlowDetail> getFinancingRepayDetail(Long financingId, String financingType) {
        FundReceiptRepayBaseInfo fundReceiptRepayBaseInfo = fundReceiptRepayBaseInfoMapper.selectOne(
                Wrappers.<FundReceiptRepayBaseInfo>lambdaQuery()
                        .eq(FundReceiptRepayBaseInfo::getFinancingId, financingId)
                        .eq(FinancingTypeEnum.DIRECT.name().equals(financingType), FundReceiptRepayBaseInfo::getFinancingType, financingType)
                        .isNull(!FinancingTypeEnum.DIRECT.name().equals(financingType), FundReceiptRepayBaseInfo::getFinancingType)
                        .last("limit 1")
        );
        if (ObjectUtil.isEmpty(fundReceiptRepayBaseInfo)) {
            return ListUtil.empty();
        }
        return fundReceiptFlowDetailMapper.selectList(
                Wrappers.<FundReceiptFlowDetail>lambdaQuery()
                        .eq(FundReceiptFlowDetail::getReceiptRepayId, fundReceiptRepayBaseInfo.getId())
                        .eq(FundReceiptFlowDetail::getCashFlowItem, FINANCE_REPAY_CASH_FLOW_ITEM)
        );
    }
}
