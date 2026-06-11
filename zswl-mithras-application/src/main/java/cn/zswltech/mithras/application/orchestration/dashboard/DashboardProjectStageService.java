package cn.zswltech.mithras.application.orchestration.dashboard;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.gruul.common.util.AccountUtil;
import cn.zswltech.gruul.dao.dal.vo.AccountVO;
import cn.zswltech.mithras.dto.dashboard.*;
import cn.zswltech.mithras.application.orchestration.enums.BusinessModuleEnum;
import cn.zswltech.mithras.foundation.enums.common.ProcessStatus;
import cn.zswltech.mithras.foundation.enums.common.RecordStatus;
import cn.zswltech.mithras.contract.enums.contract.ContractProcessStatusEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.dashboard.application.DashboardAuthQueryHelper;
import cn.zswltech.mithras.dashboard.application.DashboardProjectService;
import cn.zswltech.mithras.dashboard.enums.DashboardCardGroupEnum;
import cn.zswltech.mithras.payment.enums.PaymentStatusEnum;
import cn.zswltech.mithras.projectprocess.enums.projreview.ProjReviewMaterialsEnum;
import cn.zswltech.mithras.dashboard.mapper.DashboardProjectStageMapper;
import cn.zswltech.mithras.foundation.persistence.model.BaseModel;
import cn.zswltech.mithras.document.mapper.model.MaterialsList;
import cn.zswltech.mithras.dashboard.mapper.model.*;
import cn.zswltech.mithras.foundation.util.Util;
import cn.zswltech.mithras.system.user.SysUserService;
import cn.zswltech.mithras.application.orchestration.document.materialsfile.MaterialsListService;
import cn.zswltech.mithras.foundation.state.ProjProcessState;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2024/6/16
 * @description
 */
@Service
public class DashboardProjectStageService extends DashboardProjectService implements cn.zswltech.mithras.dashboard.application.DashboardProjectStageApplicationService {
    @Resource
    private DashboardProjectStageMapper dashboardProjectStageMapper;
    @Resource
    private SysUserService sysUserService;

    public List<DashboardProjectStageStatisticsRSP> statisticsList(DashboardProjectStageStatisticsREQ req) throws Exception {
        List<DashboardProjectStageStatisticsRSP> result = new LinkedList<>();
        AccountVO accountVO = AccountUtil.getLoginInfo();
        // 立项阶段
        CompletableFuture<DashboardProjectStageStatisticsRSP> establish = CompletableFuture.supplyAsync(() -> establishStatistics(req, DashboardCardGroupEnum.PROJECT_VIEW_STAGE_ESTABLISH, accountVO));
        // 评审阶段
        CompletableFuture<DashboardProjectStageStatisticsRSP> review = CompletableFuture.supplyAsync(() -> reviewStatistics(req, DashboardCardGroupEnum.PROJECT_VIEW_STAGE_REVIEW, 1, accountVO));
        // 评审通过未创建合同
        CompletableFuture<DashboardProjectStageStatisticsRSP> reviewNoContract = CompletableFuture.supplyAsync(() -> reviewStatistics(req, DashboardCardGroupEnum.PROJECT_VIEW_STAGE_REVIEW_NO_CONTRACT, 2, accountVO));
        // 评审提交待出具法律合规意见书
        CompletableFuture<DashboardProjectStageStatisticsRSP> reviewNoLegalReport = CompletableFuture.supplyAsync(() -> reviewStatistics(req, DashboardCardGroupEnum.PROJECT_VIEW_STAGE_REVIEW_NO_LEGAL_REPORT, 3, accountVO));
        // 签约（合同）阶段
        CompletableFuture<DashboardProjectStageStatisticsRSP> contract = CompletableFuture.supplyAsync(() -> contractStatistics(req, DashboardCardGroupEnum.PROJECT_VIEW_STAGE_CONTRACT, accountVO));
        // 付款阶段
        CompletableFuture<DashboardProjectStageStatisticsRSP> preparePayment = CompletableFuture.supplyAsync(() -> paymentStatistics(req, DashboardCardGroupEnum.PROJECT_VIEW_STAGE_PREPARE_PAYMENT, 1, accountVO));
        // 投放阶段
        CompletableFuture<DashboardProjectStageStatisticsRSP> payment = CompletableFuture.supplyAsync(() -> paymentStatistics(req, DashboardCardGroupEnum.PROJECT_VIEW_STAGE_PAYMENT, 2, accountVO));
        // 还款阶段
        CompletableFuture<DashboardProjectStageStatisticsRSP> repayment = CompletableFuture.supplyAsync(() -> repaymentStatistics(req, DashboardCardGroupEnum.PROJECT_VIEW_STAGE_REPAYMENT, accountVO));
        // 放入返回参数中
        result.add(establish.get());
        result.add(review.get());
        result.add(reviewNoContract.get());
        result.add(contract.get());
        result.add(preparePayment.get());
        result.add(payment.get());
        result.add(repayment.get());
        //业务人员无需显示
        if (sysUserService.canViewDeptIds() == null) {
            result.add(reviewNoLegalReport.get());
        }
        return result;
    }

    private DashboardProjectStageStatisticsRSP establishStatistics(DashboardProjectStageStatisticsREQ req, DashboardCardGroupEnum dashboardCardGroupEnum, AccountVO accountVO) {
        DashboardProjectStageEstablishQuery query = new DashboardProjectStageEstablishQuery();
        query.setAccountVO(accountVO);
        DashboardAuthQueryHelper.fillAuthQuery(query);
        if (ObjectUtil.isNotEmpty(req)) {
            query.setClientId(req.getClientId());
            query.setPermissionType(req.getPermissionType());
        }
        List<DashboardProjectStageEstablishResult> list = dashboardProjectStageMapper.listProjectOverviewEstablishStage(query);
        long totalAmount = 0L;
        int quantity = 0;
        int incrementThisMonth = 0;
        if (CollectionUtil.isNotEmpty(list)) {
            for (DashboardProjectStageEstablishResult detail : list) {
                quantity++;
                totalAmount += Optional.ofNullable(detail.getCreditAmount()).orElse(0L);
                if (Objects.nonNull(detail.getCreateTime()) && this.isThisMonth(detail.getCreateTime())) {
                    incrementThisMonth++;
                }
            }
        }
        return new DashboardProjectStageStatisticsRSP(dashboardCardGroupEnum.name(), dashboardCardGroupEnum.getDisplay(), quantity, new ValueUnitDTO(Util.toWanYuanWithoutSplit(totalAmount), "万元"), incrementThisMonth);
    }

    private DashboardProjectStageStatisticsRSP reviewStatistics(DashboardProjectStageStatisticsREQ req, DashboardCardGroupEnum dashboardCardGroupEnum, int viewType, AccountVO accountVO) {
        DashboardProjectStageReviewQuery query = new DashboardProjectStageReviewQuery();
        query.setViewType(viewType);
        query.setAccountVO(accountVO);
        DashboardAuthQueryHelper.fillAuthQuery(query);
        if (ObjectUtil.isNotEmpty(req)) {
            query.setClientId(req.getClientId());
            query.setPermissionType(req.getPermissionType());
        }
        List<DashboardProjectStageReviewResult> list = dashboardProjectStageMapper.listProjectOverviewReviewStage(query);
        long totalAmount = 0L;
        int quantity = 0;
        int incrementThisMonth = 0;
        if (CollectionUtil.isNotEmpty(list)) {
            for (DashboardProjectStageReviewResult detail : list) {
                quantity++;
                totalAmount += Optional.ofNullable(detail.getCreditAmount()).orElse(0L);
                if (viewType == 1) {
                    if (Objects.nonNull(detail.getCreateTime()) && this.isThisMonth(detail.getCreateTime())) {
                        incrementThisMonth++;
                    }
                }
                if (viewType == 2) {
                    if (Objects.nonNull(detail.getProcessInstanceEndTime()) && this.isThisMonth(detail.getProcessInstanceEndTime())) {
                        incrementThisMonth++;
                    }
                }
                if (viewType == 3) {
                    if (Objects.nonNull(detail.getProcessInstanceStartTime()) && this.isThisMonth(detail.getProcessInstanceStartTime())) {
                        incrementThisMonth++;
                    }
                }
            }
        }
        return new DashboardProjectStageStatisticsRSP(dashboardCardGroupEnum.name(), dashboardCardGroupEnum.getDisplay(), quantity, new ValueUnitDTO(Util.toWanYuanWithoutSplit(totalAmount), "万元"), incrementThisMonth);
    }

    private DashboardProjectStageStatisticsRSP contractStatistics(DashboardProjectStageStatisticsREQ req, DashboardCardGroupEnum dashboardCardGroupEnum, AccountVO accountVO) {
        DashboardProjectStageContractQuery query = new DashboardProjectStageContractQuery();
        query.setAccountVO(accountVO);
        DashboardAuthQueryHelper.fillAuthQuery(query);
        if (ObjectUtil.isNotEmpty(req)) {
            query.setClientId(req.getClientId());
            query.setPermissionType(req.getPermissionType());
        }
        List<DashboardProjectStageContractResult> list = dashboardProjectStageMapper.listProjectOverviewContractStage(query);
        long totalAmount = 0L;
        int quantity = 0;
        int incrementThisMonth = 0;
        if (CollectionUtil.isNotEmpty(list)) {
            for (DashboardProjectStageContractResult detail : list) {
                quantity++;
                totalAmount += Optional.ofNullable(detail.getContractAmount()).orElse(0L);
                if (Objects.nonNull(detail.getCreateTime()) && this.isThisMonth(detail.getCreateTime())) {
                    incrementThisMonth++;
                }
            }
        }
        return new DashboardProjectStageStatisticsRSP(dashboardCardGroupEnum.name(), dashboardCardGroupEnum.getDisplay(), quantity, new ValueUnitDTO(Util.toWanYuanWithoutSplit(totalAmount), "万元"), incrementThisMonth);
    }

    private DashboardProjectStageStatisticsRSP paymentStatistics(DashboardProjectStageStatisticsREQ req, DashboardCardGroupEnum dashboardCardGroupEnum, int viewType, AccountVO accountVO) {
        DashboardProjectStagePaymentQuery query = new DashboardProjectStagePaymentQuery();
        query.setViewType(viewType);
        query.setAccountVO(accountVO);
        DashboardAuthQueryHelper.fillAuthQuery(query);
        if (ObjectUtil.isNotEmpty(req)) {
            query.setClientId(req.getClientId());
            query.setPermissionType(req.getPermissionType());
        }
        List<DashboardProjectStagePaymentResult> list = dashboardProjectStageMapper.listProjectOverviewPaymentStage(query);
        long totalAmount = 0L;
        int quantity = 0;
        int incrementThisMonth = 0;
        if (CollectionUtil.isNotEmpty(list)) {
            for (DashboardProjectStagePaymentResult detail : list) {
                quantity++;
                totalAmount += Optional.ofNullable(detail.getApplyPayAmount()).orElse(0L);
                if (viewType == 1) {
                    if (Objects.nonNull(detail.getCreateTime()) && this.isThisMonth(detail.getCreateTime())) {
                        incrementThisMonth++;
                    }
                }
                if (viewType == 2) {
                    if (Objects.nonNull(detail.getProcessInstanceEndTime()) && this.isThisMonth(detail.getProcessInstanceEndTime())) {
                        incrementThisMonth++;
                    }
                }
            }
        }
        return new DashboardProjectStageStatisticsRSP(dashboardCardGroupEnum.name(), dashboardCardGroupEnum.getDisplay(), quantity, new ValueUnitDTO(Util.toWanYuanWithoutSplit(totalAmount), "万元"), incrementThisMonth);
    }

    private DashboardProjectStageStatisticsRSP repaymentStatistics(DashboardProjectStageStatisticsREQ req, DashboardCardGroupEnum dashboardCardGroupEnum, AccountVO accountVO) {
        DashboardProjectStageRepaymentQuery query = new DashboardProjectStageRepaymentQuery();
        query.setAccountVO(accountVO);
        DashboardAuthQueryHelper.fillAuthQuery(query);
        if (ObjectUtil.isNotEmpty(req)) {
            query.setClientId(req.getClientId());
            query.setPermissionType(req.getPermissionType());
        }
        List<DashboardProjectStageRepaymentResult> list = dashboardProjectStageMapper.listProjectOverviewRepaymentStage(query);
        long totalAmount = 0L;
        int quantity = 0;
        int incrementThisMonth = 0;
        if (CollectionUtil.isNotEmpty(list)) {
            for (DashboardProjectStageRepaymentResult detail : list) {
                quantity++;
                totalAmount += (Optional.ofNullable(detail.getTotalPlanRent()).orElse(0L) - Optional.ofNullable(detail.getTotalCollectionRent()).orElse(0L));
                if (Objects.nonNull(detail.getEarliestPayDate()) && this.isThisMonth(detail.getEarliestPayDate().atStartOfDay())) {
                    incrementThisMonth++;
                }
            }
        }
        return new DashboardProjectStageStatisticsRSP(dashboardCardGroupEnum.name(), dashboardCardGroupEnum.getDisplay(), quantity, new ValueUnitDTO(Util.toWanYuanWithoutSplit(totalAmount), "万元"), incrementThisMonth);
    }

    public RePaymentDetailSumRSP repaymentList(DashboardProjectStageRepaymentDetailREQ req) {
        RePaymentDetailSumRSP sumRSP = new RePaymentDetailSumRSP();
        RePaymentDetailSumRSP.SumData sumData = new RePaymentDetailSumRSP.SumData();
        DashboardProjectStageRepaymentQuery dbQuery = this.buildQuery(req);
        List<DashboardProjectStageRepaymentResult> dbResultList = dashboardProjectStageMapper.listProjectOverviewRepaymentStage(dbQuery);
        if (CollectionUtil.isEmpty(dbResultList)) {
            List<DashboardProjectStageRepaymentDetailRSP> empty = new ArrayList<>();
            sumRSP.setList(empty);
            sumRSP.setSumData(sumData);
            return sumRSP;
        }
        List<DashboardProjectStageRepaymentDetailRSP> rspList = this.buildRspList(dbResultList, dbResult -> {
            DashboardProjectStageRepaymentDetailRSP rsp = new DashboardProjectStageRepaymentDetailRSP();
            rsp.setProjName(dbResult.getProjName());
            rsp.setContractCode(dbResult.getContractCode());
            rsp.setActualLeaseDate(dbResult.getActualLeaseDate());
            rsp.setActualPayDate(dbResult.getEarliestPayDate());
            rsp.setTotalCollectionRent(new ValueUnitDTO(Util.toYuanWithoutSplit(dbResult.getTotalCollectionRent()), "元"));
            rsp.setTotalRentBalance(new ValueUnitDTO(Util.toYuanWithoutSplit(dbResult.getTotalPlanRent() - dbResult.getTotalCollectionRent()), "元"));
            rsp.setStockRiskExposure(new ValueUnitDTO(Util.toYuanWithoutSplit(dbResult.getTotalPay() - dbResult.getTotalCollectionPrincipal() - dbResult.getCollectionFirstRent() - dbResult.getEarnestBalance()), "元"));
            rsp.setActualPayAmount(new ValueUnitDTO(Util.toYuanWithoutSplit(dbResult.getTotalPay()), "元"));
            rsp.setTotalPrincipalBalance(new ValueUnitDTO(Util.toYuanWithoutSplit(dbResult.getTotalPlanPrincipal() - dbResult.getTotalCollectionPrincipal()), "元"));
            rsp.setTotalInterestBalance(new ValueUnitDTO(Util.toYuanWithoutSplit(dbResult.getTotalPlanInterest() - dbResult.getTotalCollectionInterest()), "元"));
            if (Objects.nonNull(dbResult.getEstimateIrr())) {
                BigDecimal b = BigDecimal.valueOf(dbResult.getEstimateIrr()).divide(BigDecimal.valueOf(10000L), 2, RoundingMode.HALF_UP);
                rsp.setIrr(new ValueUnitDTO(b.toPlainString(), "%"));
            }
            if (Objects.nonNull(dbResult.getLeaseDuration())) {
                if (Objects.nonNull(dbResult.getActualLeaseDate())) {
                    long m = LocalDateTimeUtil.between(dbResult.getActualLeaseDate().atStartOfDay(), LocalDate.now().atStartOfDay(), ChronoUnit.MONTHS);
                    long r = dbResult.getLeaseDuration() - m;
                    rsp.setRemainingLeaseDuration(new ValueUnitDTO(String.valueOf(r), "月"));
                }
                rsp.setLeaseDuration(new ValueUnitDTO(dbResult.getLeaseDuration().toString(), "月"));
            }
            return rsp;
        });
        // 担保人处理
        this.fillGuarantorInfo(rspList);
        BigDecimal totalCollectionRent = rspList.stream().filter(c -> ObjectUtil.isNotEmpty(c.getTotalCollectionRent()))
                .map(e -> new BigDecimal(ObjectUtil.isNotEmpty(e.getTotalCollectionRent().getValue()) ? e.getTotalCollectionRent().getValue() : "0"))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalRentBalance = rspList.stream().filter(c -> ObjectUtil.isNotEmpty(c.getTotalRentBalance()))
                .map(e -> new BigDecimal(ObjectUtil.isNotEmpty(e.getTotalRentBalance().getValue()) ? e.getTotalRentBalance().getValue() : "0"))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal stockRiskExposure = rspList.stream().filter(c -> ObjectUtil.isNotEmpty(c.getStockRiskExposure()))
                .map(e -> new BigDecimal(ObjectUtil.isNotEmpty(e.getStockRiskExposure().getValue()) ? e.getStockRiskExposure().getValue() : "0"))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        sumData.setTotalCollectionRent(totalCollectionRent);
        sumData.setTotalRentBalance(totalRentBalance);
        sumData.setStockRiskExposure(stockRiskExposure);
        sumRSP.setList(rspList);
        sumRSP.setSumData(sumData);
        return sumRSP;
    }

    private DashboardProjectStageRepaymentQuery buildQuery(DashboardProjectStageRepaymentDetailREQ req) {
        DashboardProjectStageRepaymentQuery query = new DashboardProjectStageRepaymentQuery();
        query.setAccountVO(AccountUtil.getLoginInfo());
        query.setContractCode(req.getContractCode());
        query.setProjName(req.getProjName());
        query.setRentBalanceFrom(req.getTotalRentBalanceFrom());
        query.setRentBalanceTo(req.getTotalRentBalanceTo());
        query.setPermissionType(req.getPermissionType());
        DashboardAuthQueryHelper.fillAuthQuery(query);
        return query;
    }

    public PaymentDetailSumRSP paymentList(DashboardProjectStagePaymentDetailREQ req, Integer viewType) {
        PaymentDetailSumRSP sumRsp = new PaymentDetailSumRSP();
        PaymentDetailSumRSP.SumData sumData = new PaymentDetailSumRSP.SumData();
        DashboardProjectStagePaymentQuery dbQuery = this.buildQuery(req);
        dbQuery.setViewType(viewType);
        List<DashboardProjectStagePaymentResult> dbResultList = dashboardProjectStageMapper.listProjectOverviewPaymentStage(dbQuery);
        if (CollectionUtil.isEmpty(dbResultList)) {
            List<DashboardProjectStagePaymentDetailRSP> empty = new ArrayList<>();
            sumRsp.setList(empty);
            sumRsp.setSumData(sumData);
            return sumRsp;
        }
        List<DashboardProjectStagePaymentDetailRSP> rspList = this.buildRspList(dbResultList, dbResult -> {
            DashboardProjectStagePaymentDetailRSP rsp = new DashboardProjectStagePaymentDetailRSP();
            rsp.setProjName(dbResult.getProjName());
            rsp.setCreditAmount(new ValueUnitDTO(Optional.ofNullable(dbResult.getCreditAmount()).map(e -> Util.toYuanWithoutSplit(e)).orElse(""), "元"));
            rsp.setContractAmount(new ValueUnitDTO(Optional.ofNullable(dbResult.getContractAmount()).map(e -> Util.toYuanWithoutSplit(e)).orElse(""), "元"));
            rsp.setContractCode(dbResult.getContractCode());
            rsp.setPaymentCode(dbResult.getPaymentCode());
            rsp.setPaymentStatusCode(dbResult.getPaymentStatus());
            rsp.setPaymentStatusDisplay(Optional.ofNullable(PaymentStatusEnum.find(dbResult.getPaymentStatus())).map(PaymentStatusEnum::display).orElse(""));
            rsp.setPaymentProcessStatusCode(dbResult.getPaymentProcessStatus());
            rsp.setPaymentProcessStatusDisplay(Optional.ofNullable(ProcessStatus.of(dbResult.getPaymentProcessStatus())).map(e -> e.display).orElse(""));
            rsp.setApplyPayDate(dbResult.getApplyPayDate());
            if (Objects.nonNull(dbResult.getApplyPayAmount())) {
                rsp.setApplyPayAmount(new ValueUnitDTO(Util.toYuanWithoutSplit(dbResult.getApplyPayAmount()), "元"));
            }
            if (Objects.nonNull(dbResult.getLprPercent()) && Objects.nonNull(dbResult.getLprAddPercent())) {
                BigDecimal b = BigDecimal.valueOf(dbResult.getLprPercent() + dbResult.getLprAddPercent()).divide(BigDecimal.valueOf(10000L), 2, RoundingMode.HALF_UP);
                rsp.setInterestRate(new ValueUnitDTO(b.toPlainString(), "%"));
            }
            if (Objects.nonNull(dbResult.getLeaseDuration())) {
                rsp.setLeaseDuration(new ValueUnitDTO(dbResult.getLeaseDuration().toString(), "月"));
            }
            return rsp;
        });
        // 担保人处理
        this.fillGuarantorInfo(rspList);
        BigDecimal contractAmount = rspList.stream().filter(c -> ObjectUtil.isNotEmpty(c.getContractAmount()))
                .map(e -> new BigDecimal(ObjectUtil.isNotEmpty(e.getContractAmount().getValue()) ? e.getContractAmount().getValue() : "0"))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal applyPayAmount = rspList.stream().filter(c -> ObjectUtil.isNotEmpty(c.getApplyPayAmount()))
                .map(e -> new BigDecimal(ObjectUtil.isNotEmpty(e.getApplyPayAmount().getValue()) ? e.getApplyPayAmount().getValue() : "0"))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        sumData.setContractAmount(contractAmount);
        sumData.setApplyPayAmount(applyPayAmount);
        sumRsp.setList(rspList);
        sumRsp.setSumData(sumData);
        return sumRsp;
    }

    private DashboardProjectStagePaymentQuery buildQuery(DashboardProjectStagePaymentDetailREQ req) {
        DashboardProjectStagePaymentQuery query = new DashboardProjectStagePaymentQuery();
        query.setAccountVO(AccountUtil.getLoginInfo());
        DashboardAuthQueryHelper.fillAuthQuery(query);
        query.setClientId(req.getClientId());
        query.setContractCode(req.getContractCode());
        query.setPaymentStatus(req.getPaymentStatusCode());
        query.setPaymentProcessStatus(req.getPaymentProcessStatusCode());
        query.setApplyPayDateFrom(req.getApplyPayDateFrom());
        query.setApplyPayDateTo(req.getApplyPayDateTo());
        query.setBizDeptId(req.getBizDeptId());
        query.setProjSponsorUserId(req.getProjSponsorUserId());
        query.setPermissionType(req.getPermissionType());
        return query;
    }

    public ContractDetailSumRSP contractList(DashboardProjectStageContractDetailREQ req) {
        ContractDetailSumRSP contractDetailRsp = new ContractDetailSumRSP();
        ContractDetailSumRSP.SumData sumData = new ContractDetailSumRSP.SumData();
        DashboardProjectStageContractQuery dbQuery = this.buildQuery(req);
        List<DashboardProjectStageContractResult> dbResultList = dashboardProjectStageMapper.listProjectOverviewContractStage(dbQuery);
        if (CollectionUtil.isEmpty(dbResultList)) {
            List<DashboardProjectStageContractDetailRSP> empty = new ArrayList<>();
            contractDetailRsp.setList(empty);
            contractDetailRsp.setSumData(sumData);
            return contractDetailRsp;
        }
        List<DashboardProjectStageContractDetailRSP> rspList = this.buildRspList(dbResultList, dbResult -> {
            DashboardProjectStageContractDetailRSP rsp = new DashboardProjectStageContractDetailRSP();
            rsp.setProjName(dbResult.getProjName());
            rsp.setCreditAmount(new ValueUnitDTO(Optional.ofNullable(dbResult.getCreditAmount()).map(e -> Util.toYuanWithoutSplit(e)).orElse(""), "元"));
            rsp.setContractAmount(new ValueUnitDTO(Optional.ofNullable(dbResult.getContractAmount()).map(e -> Util.toYuanWithoutSplit(e)).orElse(""), "元"));
            rsp.setContractCode(dbResult.getContractCode());
            rsp.setContractStatusCode(dbResult.getContractStatus());
            rsp.setContractStatusDisplay(Optional.ofNullable(ContractStatus.find(dbResult.getContractStatus())).map(ContractStatus::display).orElse(""));
            rsp.setContractProcessStatusCode(dbResult.getContractProcessStatus());
            rsp.setContractProcessStatusDisplay(Optional.ofNullable(ContractProcessStatusEnum.of(dbResult.getContractProcessStatus())).map(ContractProcessStatusEnum::display).orElse(""));
            if (Objects.nonNull(dbResult.getLprPercent()) && Objects.nonNull(dbResult.getLprAddPercent())) {
               BigDecimal b = BigDecimal.valueOf(dbResult.getLprPercent() + dbResult.getLprAddPercent()).divide(BigDecimal.valueOf(10000L), 2, RoundingMode.HALF_UP);
               rsp.setInterestRate(new ValueUnitDTO(b.toPlainString(), "%"));
            }
            if (Objects.nonNull(dbResult.getLeaseDuration())) {
                rsp.setLeaseDuration(new ValueUnitDTO(dbResult.getLeaseDuration().toString(), "月"));
            }
            return rsp;
        });
        // 担保人处理
        this.fillGuarantorInfo(rspList);
        //金额/数量合计
        BigDecimal contractAmount = rspList.stream().filter(c -> ObjectUtil.isNotEmpty(c.getContractAmount()))
                .map(e -> new BigDecimal(ObjectUtil.isNotEmpty(e.getContractAmount().getValue()) ? e.getContractAmount().getValue() : "0"))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        sumData.setContractAmount(contractAmount);
        contractDetailRsp.setList(rspList);
        contractDetailRsp.setSumData(sumData);
        return contractDetailRsp;
    }

    private DashboardProjectStageContractQuery buildQuery(DashboardProjectStageContractDetailREQ req) {
        DashboardProjectStageContractQuery query = new DashboardProjectStageContractQuery();
        query.setAccountVO(AccountUtil.getLoginInfo());
        DashboardAuthQueryHelper.fillAuthQuery(query);
        query.setClientId(req.getClientId());
        query.setProjName(req.getProjName());
        query.setContractCode(req.getContractCode());
        query.setContractProcessStatus(req.getContractProcessStatusCode());
        query.setBizDeptId(req.getBizDeptId());
        query.setProjSponsorUserId(req.getProjSponsorUserId());
        query.setPermissionType(req.getPermissionType());
        return query;
    }

    public ReviewDetailSumRSP reviewList(DashboardProjectStageReviewDetailREQ req, Integer viewType) {
        ReviewDetailSumRSP reviewDetailRsp = new ReviewDetailSumRSP();
        ReviewDetailSumRSP.SumData sumData = new ReviewDetailSumRSP.SumData();
        DashboardProjectStageReviewQuery dbQuery = this.buildQuery(req);
        dbQuery.setViewType(viewType);
        List<DashboardProjectStageReviewResult> dbResultList = dashboardProjectStageMapper.listProjectOverviewReviewStage(dbQuery);
        if (CollectionUtil.isEmpty(dbResultList)) {
            List<DashboardProjectStageReviewDetailRSP> empty = new ArrayList<>();
            reviewDetailRsp.setList(empty);
            reviewDetailRsp.setSumData(sumData);
            return reviewDetailRsp;
        }
        List<DashboardProjectStageReviewDetailRSP> rspList = this.buildRspList(dbResultList, dbResult -> {
            DashboardProjectStageReviewDetailRSP rsp = new DashboardProjectStageReviewDetailRSP();
            rsp.setCreditAmount(new ValueUnitDTO(Optional.ofNullable(dbResult.getCreditAmount()).map(Util::toYuanWithoutSplit).orElse(""), "元"));
            rsp.setProjReviewStatusCode(dbResult.getProjReviewStatus());
            rsp.setProjReviewStatusDisplay(Optional.ofNullable(RecordStatus.of(dbResult.getProjReviewStatus())).map(RecordStatus::display).orElse(""));
            rsp.setProjReviewProcessStatusCode(dbResult.getProjReviewProcessStatus());
            rsp.setProjReviewProcessStatusDisplay(Optional.ofNullable(ProjProcessState.of(dbResult.getProjReviewProcessStatus())).map(e -> e.display).orElse(""));
            return rsp;
        });
        // 如果viewType = 3需要补全一下尽调报告上传时间
        if (viewType == 3) {
            List<Long> projReviewIds = rspList.stream().map(DashboardProjectBasicRSP::getMainId).collect(Collectors.toList());
            List<MaterialsList> list = SpringUtil.getBean(MaterialsListService.class).list(BusinessModuleEnum.PROJ_REVIEW.name(), Collections.singletonList(ProjReviewMaterialsEnum.DUE_DILIGENCE_REPORT.name()), projReviewIds);
            Map<Long, List<MaterialsList>> map = list.stream().collect(Collectors.groupingBy(MaterialsList::getBelongId));
            rspList.forEach(e -> {
                List<MaterialsList> files = map.get(e.getMainId());
                if (CollectionUtil.isNotEmpty(files)) {
                    files.sort(Comparator.comparing(BaseModel::getCreateTime));
                    e.setDueDiligenceReportUploadTime(files.get(0).getCreateTime());
                }
            });
        }
        //金额/数量合计
        BigDecimal creditAmount = rspList.stream().filter(c -> ObjectUtil.isNotEmpty(c.getCreditAmount()))
                .map(e -> new BigDecimal(ObjectUtil.isNotEmpty(e.getCreditAmount().getValue()) ? e.getCreditAmount().getValue() : "0"))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        sumData.setCreditAmount(creditAmount);
        reviewDetailRsp.setList(rspList);
        reviewDetailRsp.setSumData(sumData);
        return reviewDetailRsp;
    }

    private DashboardProjectStageReviewQuery buildQuery(DashboardProjectStageReviewDetailREQ req) {
        DashboardProjectStageReviewQuery query = new DashboardProjectStageReviewQuery();
        query.setAccountVO(AccountUtil.getLoginInfo());
        DashboardAuthQueryHelper.fillAuthQuery(query);
        query.setClientId(req.getClientId());
        query.setProjName(req.getProjName());
        query.setProjectReviewStatus(req.getProjReviewStatusCode());
        query.setProjectReviewProcessStatus(req.getProjReviewProcessStatusCode());
        query.setBizDeptId(req.getBizDeptId());
        query.setProjSponsorUserId(req.getProjSponsorUserId());
        query.setPermissionType(req.getPermissionType());
        return query;
    }

    public EstablishDetailSumRSP establishList(DashboardProjectStageEstablishDetailREQ req) {
        EstablishDetailSumRSP sumRSP = new EstablishDetailSumRSP();
        EstablishDetailSumRSP.SumData sumData = new EstablishDetailSumRSP.SumData();
        DashboardProjectStageEstablishQuery dbQuery = this.buildQuery(req);
        List<DashboardProjectStageEstablishResult> dbResultList = dashboardProjectStageMapper.listProjectOverviewEstablishStage(dbQuery);
        if (CollectionUtil.isEmpty(dbResultList)) {
            List<DashboardProjectStageEstablishDetailRSP> empty = new ArrayList<>();
            sumRSP.setList(empty);
            sumRSP.setSumData(sumData);
            return sumRSP;
        }
        List<DashboardProjectStageEstablishDetailRSP> rspList = this.buildRspList(dbResultList, dbResult -> {
            DashboardProjectStageEstablishDetailRSP rsp = new DashboardProjectStageEstablishDetailRSP();
            rsp.setProjEstablishType(dbResult.getProjectEstablishType());
            rsp.setCreditAmount(new ValueUnitDTO(Optional.ofNullable(dbResult.getCreditAmount()).map(Util::toYuanWithoutSplit).orElse(""), "元"));
            rsp.setProjEstablishStatusCode(dbResult.getProjectEstablishStatus());
            rsp.setProjEstablishStatusDisplay(Optional.ofNullable(RecordStatus.of(dbResult.getProjectEstablishStatus())).map(RecordStatus::display).orElse(""));
            rsp.setProjEstablishProcessStatusCode(dbResult.getProjectEstablishProcessStatus());
            rsp.setProjEstablishProcessStatusDisplay(Optional.ofNullable(ProjProcessState.of(dbResult.getProjectEstablishProcessStatus())).map(e -> e.display).orElse(""));
            return rsp;
        });
        //金额/数量合计
        BigDecimal creditAmount = rspList.stream().filter(c -> ObjectUtil.isNotEmpty(c.getCreditAmount()))
                .map(e -> new BigDecimal(ObjectUtil.isNotEmpty(e.getCreditAmount().getValue()) ? e.getCreditAmount().getValue() : "0"))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        sumData.setCreditAmount(creditAmount);
        sumRSP.setList(rspList);
        sumRSP.setSumData(sumData);
        return sumRSP;
    }

    private DashboardProjectStageEstablishQuery buildQuery(DashboardProjectStageEstablishDetailREQ req) {
        DashboardProjectStageEstablishQuery query = new DashboardProjectStageEstablishQuery();
        query.setAccountVO(AccountUtil.getLoginInfo());
        DashboardAuthQueryHelper.fillAuthQuery(query);
        query.setClientId(req.getClientId());
        query.setProjName(req.getProjName());
        query.setProjectEstablishStatus(req.getProjEstablishStatusCode());
        query.setProjectEstablishProcessStatus(req.getProjEstablishProcessStatusCode());
        query.setBizDeptId(req.getBizDeptId());
        query.setProjSponsorUserId(req.getProjSponsorUserId());
        query.setPermissionType(req.getPermissionType());
        return query;
    }

    private boolean isThisMonth(LocalDateTime targetDateTime) {
        LocalDateTime now = LocalDateTime.now();
        return (targetDateTime.getYear() == now.getYear()) && (targetDateTime.getMonthValue() == now.getMonthValue());
    }
}
