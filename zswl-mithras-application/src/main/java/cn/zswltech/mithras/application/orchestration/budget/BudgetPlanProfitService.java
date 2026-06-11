package cn.zswltech.mithras.application.orchestration.budget;

import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.contract.core.ContractLeasePriceService;
import cn.zswltech.mithras.contract.core.ContractReceiptService;

import cn.zswltech.mithras.budget.application.BudgetParameterConfigService;
import cn.zswltech.mithras.budget.application.BudgetPlanCostDetailFundService;
import cn.zswltech.mithras.budget.application.BudgetPlanCostDetailProjectService;
import cn.zswltech.mithras.budget.application.BudgetPlanPayDetailCashFlowService;
import cn.zswltech.mithras.budget.application.BudgetPlanPayDetailExpenseService;
import cn.zswltech.mithras.budget.application.BudgetPlanPayDetailPriceService;
import cn.zswltech.mithras.budget.application.BudgetPlanPayProcessInfoService;
import cn.zswltech.mithras.budget.application.BudgetPlanProfitApplicationService;
import cn.zswltech.mithras.budget.bo.BudgetEclRiskReserveBO;
import cn.zswltech.mithras.budget.bo.BudgetPlanStatisticsBO;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.budget.*;
import cn.zswltech.mithras.dto.projreview.price.ProjReviewPriceDetailRSP;
import cn.zswltech.mithras.foundation.enums.CashFlowItemEnum;
import cn.zswltech.mithras.foundation.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.assetclassify.enums.AssetClassifyResultEnum;
import cn.zswltech.mithras.budget.enums.BudgetPlanDataCategoryEnum;
import cn.zswltech.mithras.budget.enums.BudgetPlanCalculateStatusEnum;
import cn.zswltech.mithras.budget.enums.BudgetPlanTypeEnum;
import cn.zswltech.mithras.budget.enums.BudgetStatusEnum;
import cn.zswltech.mithras.foundation.enums.common.ProjectBizType;
import cn.zswltech.mithras.foundation.enums.common.RecordStatus;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.contract.enums.contract.IncomeConfirmTypeEnum;
import cn.zswltech.mithras.ftp.newftp.enums.RelatedTermRange;
import cn.zswltech.mithras.foundation.enums.LeaseType;
import cn.zswltech.mithras.projectprocess.enums.projpricing.FtpIndustryCategoryEnum;
import cn.zswltech.mithras.budget.mapper.*;
import cn.zswltech.mithras.collection.mapper.CollectionBaseInfoMapper;
import cn.zswltech.mithras.contract.mapper.contract.ContractIncomeSharingMapper;
import cn.zswltech.mithras.ftp.oldftp.mapper.FtpInterestDetailRecordMapper;
import cn.zswltech.mithras.foundation.persistence.model.CommonVersion;
import cn.zswltech.mithras.budget.mapper.model.*;
import cn.zswltech.mithras.customer.model.client.Client;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractLeasePrice;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractReceipt;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractRentActual;
import cn.zswltech.mithras.ftp.oldftp.model.FtpInterestBaseInfo;
import cn.zswltech.mithras.ftp.oldftp.model.FtpInterestDetailRecord;
import cn.zswltech.mithras.kpi.mapper.model.KpiProjectDistribution;
import cn.zswltech.mithras.kpi.mapper.model.KpiProjectDistributionDeptWeight;
import cn.zswltech.mithras.kpi.mapper.model.KpiProjectDistributionDeptWeightLib;
import cn.zswltech.mithras.payment.mapper.model.FtpAssessmentInfo;
import cn.zswltech.mithras.payment.mapper.model.PaymentActualDetail;
import cn.zswltech.mithras.payment.mapper.model.PaymentBaseInfo;
import cn.zswltech.mithras.payment.mapper.model.PaymentCollectionInfo;
import cn.zswltech.mithras.projectprocess.model.projpricing.ProjPricingBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewBaseInfo;
import cn.zswltech.mithras.projectprocess.model.projreview.ProjReviewLeasePrice;
import cn.zswltech.mithras.payment.mapper.PaymentCollectionInfoMapper;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.util.Util;
import cn.zswltech.mithras.system.user.Id2NameService;
import cn.zswltech.mithras.application.orchestration.assetclassify.AssetClassifyClientService;
import cn.zswltech.mithras.budget.bo.BudgetEclRiskReserveBO;
import cn.zswltech.mithras.application.orchestration.client.ClientService;
import cn.zswltech.mithras.collection.application.CollectionBaseInfoService;
import cn.zswltech.mithras.application.orchestration.contract.*;
import cn.zswltech.mithras.application.orchestration.ftp.FtpInterestBaseInfoService;
import cn.zswltech.mithras.application.orchestration.ftp.FtpInterestDetailRecordService;
import cn.zswltech.mithras.kpi.application.distribution.KpiProjectDistributionDeptWeightLibService;
import cn.zswltech.mithras.application.orchestration.kpi.KpiProjectDistributionService;
import cn.zswltech.mithras.application.orchestration.kpi.KpiProvisionDetailService;
import cn.zswltech.mithras.kpi.application.distribution.lib.KpiProjectDistributionLibVersionService;
import cn.zswltech.mithras.margin.service.MarginRecordService;
import cn.zswltech.mithras.application.orchestration.payment.FtpAssessmentInfoService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentActualDetailService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentBaseInfoService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projpricing.ProjPricingBaseInfoService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projreview.ProjReviewBaseInfoService;
import cn.zswltech.mithras.projectprocess.application.projreview.ProjReviewLeasePriceService;
import cn.zswltech.mithras.application.orchestration.projectprocess.projreview.ProjReviewPriceService;
import cn.zswltech.mithras.application.orchestration.util.FinancialUtil;
import cn.zswltech.mithras.foundation.util.StringUtil;
import cn.zswltech.mithras.foundation.async.ThreadPoolUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import cn.zswltech.mithras.contract.core.ContractRentActualService;

/**
* @description 预算管理-预算计划-利润预算
* @author vico
* @date 2025-04-11
*/
@Slf4j
@Service
public class BudgetPlanProfitService extends ServiceImpl<BudgetPlanProfitMapper, BudgetPlanProfit> implements BudgetPlanProfitApplicationService {
    @Resource
    private Id2NameService id2NameService;
    @Resource
    private BudgetPlanService budgetPlanService;
    @Resource
    private BudgetPlanCostService budgetPlanCostService;
    @Resource
    private BudgetPlanPayService budgetPlanPayService;
    @Resource
    private BudgetPlanPayFlowService budgetPlanPayFlowService;
    @Resource
    private BudgetPlanProfitDetailService budgetPlanProfitDetailService;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Autowired
    private BudgetPlanPayDetailService budgetPlanPayDetailService;

    public void modifyCalculateStatus(Long budgetPlanProfitId, BudgetPlanCalculateStatusEnum calculateStatus) {
        BudgetPlanProfit update = new BudgetPlanProfit();
        update.setId(budgetPlanProfitId);
        update.setCalculateStatus(calculateStatus.name());
        this.updateById(update);
    }

    public Long createAndInit(BudgetPlanProfitCreateREQ req) {
        Long budgetPlanProfitId = this.create(req);
        ThreadPoolUtil.getCommonPool().execute(() -> {
            try {
                this.initProfitDetail(budgetPlanProfitId);
            } catch (Exception e) {
                log.error("预算管理-异步计算利润明细发生异常[利润预算id: {}]", budgetPlanProfitId, e);
                SpringUtil.getBean(BudgetPlanProfitService.class).modifyCalculateStatus(budgetPlanProfitId, BudgetPlanCalculateStatusEnum.FAILURE);
            }
        });
        ThreadPoolUtil.getCommonPool().execute(() -> {
            Long budgetPlanCostId = null;
            try {
                BudgetPlanProfit budgetPlanProfit = this.getById(budgetPlanProfitId);
                BudgetPlanCost budgetPlanCost = budgetPlanCostService.findByBudgetPlanId(budgetPlanProfit.getBudgetPlanId());
                budgetPlanCostId = budgetPlanCost.getId();
                SpringUtil.getBean(BudgetPlanCostService.class).initCostDetail(budgetPlanCost.getId());
                SpringUtil.getBean(BudgetPlanCostService.class).modifyCalculateStatus(budgetPlanCostId, BudgetPlanCalculateStatusEnum.SUCCESS);
            } catch (Exception e) {
                log.error("预算管理-异步计算成本预算明细发生异常[利润预算id: {}, 成本预算id: {}]", budgetPlanProfitId, budgetPlanCostId, e);
                SpringUtil.getBean(BudgetPlanCostService.class).modifyCalculateStatus(budgetPlanCostId, BudgetPlanCalculateStatusEnum.FAILURE);
            }
        });
        return budgetPlanProfitId;
    }

    public BudgetPlanProfitRSP info(Long id) {
        BudgetPlanProfit budgetPlanProfit = this.getById(id);
        BudgetPlanProfitRSP rsp = BeanUtil.copyProperties(budgetPlanProfit, BudgetPlanProfitRSP.class);
        BudgetPlanPay budgetPlanPay = budgetPlanPayService.getByBudgetPlanId(budgetPlanProfit.getBudgetPlanId());
        rsp.setBudgetPlanPayId(budgetPlanPay.getId());
        return rsp;
    }

    public void delete(BudgetPlanProfitRemoveREQ req) {
        this.delete(req.getId());
    }

    public BudgetPlanProfit getOneByBudgetPlanId(Long budgetPlanId) {
        LambdaQueryWrapper<BudgetPlanProfit> query = Wrappers.lambdaQuery();
        query.eq(BudgetPlanProfit::getBudgetPlanId, budgetPlanId);
        return this.getOne(query);
    }

    public void initProfitDetail(Long budgetPlanProfitId) {
        BudgetPlanProfit budgetPlanProfit = this.getById(budgetPlanProfitId);
        if (Objects.isNull(budgetPlanProfit)) {
            throw new MithrasException("利润预算数据不存在");
        }
        BudgetPlan budgetPlan = budgetPlanService.getById(budgetPlanProfit.getBudgetPlanId());
        if (Objects.isNull(budgetPlan)) {
            throw new MithrasException("预算主数据不存在");
        }
        List<BudgetPlanProfitDetail> detailList = new LinkedList<>();
        // 找存量借据（起租和结清的合同）
        List<ContractBaseInfo> startRentContractList = SpringUtil.getBean(ContractBaseInfoService.class).list(
                Wrappers.<ContractBaseInfo>lambdaQuery()
                        .in(ContractBaseInfo::getContractStatus, ListUtil.of(ContractStatus.START_RENT.name(), ContractStatus.SETTLE.name()))
        );
        if (CollectionUtil.isNotEmpty(startRentContractList)) {
            // 查询客户信息
            for (ContractBaseInfo contractBaseInfo : startRentContractList) {
                try {
                    // 如果是结清状态，则判断一下实际结束日期需要在预算区间开始日期之后
                    if (StrUtil.equals(contractBaseInfo.getContractStatus(), ContractStatus.SETTLE.name()) && Objects.nonNull(contractBaseInfo.getActualFinishDate()) && !contractBaseInfo.getActualFinishDate().isAfter(budgetPlan.getBudgetDateFrom())) {
                        continue;
                    }
                    detailList.addAll(this.calculateByContract(budgetPlanProfit, contractBaseInfo));
                } catch (Exception e) {
                    log.error("预算管理-利润预算-计算单个合同发生异常[contract:{}]", JSONUtil.toJsonStr(contractBaseInfo), e);
                    throw e;
                }
            }
        }
        // 找投放计划明细
        List<BudgetPlanPayDetail> budgetPlanPayDetailList = SpringUtil.getBean(BudgetPlanPayDetailService.class).listByBudgetPlanId(budgetPlan.getId());
        if (CollectionUtil.isNotEmpty(budgetPlanPayDetailList)) {
            for (BudgetPlanPayDetail budgetPlanPayDetail : budgetPlanPayDetailList) {
                try {
                    detailList.addAll(this.calculateByBudgetPlanPayDetail(budgetPlanProfit, budgetPlanPayDetail));
                } catch (Exception e) {
                    log.error("预算管理-利润预算-计算单个投放计划明细发生异常[budgetPlanPayDetail:{}]", JSONUtil.toJsonStr(budgetPlanPayDetail), e);
                    throw e;
                }
            }
        }
        // 数据库操作
        Boolean success = transactionTemplate.execute(transactionStatus -> {
            try {
                SpringUtil.getBean(BudgetPlanProfitDetailService.class).deleteByBudgetPlanProfitId(budgetPlanProfitId);
                SpringUtil.getBean(BudgetPlanProfitDetailService.class).saveBatch(detailList);
                return Boolean.TRUE;
            } catch (Exception e) {
                log.error("预算管理-利润预算-保存数据发生异常[budgetPlanProfitId:{}, detailList:{}]", budgetPlanProfitId, JSONUtil.toJsonStr(detailList), e);
                transactionStatus.setRollbackOnly();
                return Boolean.FALSE;
            }
        });
        if (Objects.nonNull(success) && success) {
            // 创建拨备预测相关数据
            EclExecutePredictBaseInfoAddREQ req = new EclExecutePredictBaseInfoAddREQ();
            req.setBudgetPlanId(budgetPlan.getId());
            req.setBudgetPlanName(budgetPlan.getPlanName());
            req.setPredictDataFrom(budgetPlan.getBudgetDateFrom());
            req.setPredictDataTo(budgetPlan.getBudgetDateTo());
            req.setSource(YesOrNoNumberEnum.NO.getCode());
            SpringUtil.getBean(EclExecutePredictBaseInfoService.class).create(req);
        } else {
            SpringUtil.getBean(BudgetPlanProfitService.class).modifyCalculateStatus(budgetPlanProfitId, BudgetPlanCalculateStatusEnum.FAILURE);
        }
    }

    public void refreshRiskFundBalanceByEcl(Long budgetPlanId, List<BudgetEclRiskReserveBO> list, boolean isSuccess) {
        Long budgetPlanProfitId = null;
        try {
            if (CollectionUtil.isEmpty(list)) {
                return;
            }
            // 查询预算信息
            BudgetPlan budgetPlan = budgetPlanService.getById(budgetPlanId);
            if (Objects.isNull(budgetPlan)) {
                log.error("预算数据不存在，不执行Ecl更新利润预算明细[{}]", budgetPlanId);
                throw new MithrasException("预算数据不存在");
            }
            BudgetPlanProfit budgetPlanProfit = this.getOneByBudgetPlanId(budgetPlanId);
            if (Objects.isNull(budgetPlanProfit)) {
                log.error("利润预算数据不存在，不执行Ecl更新利润预算明细[{}]", budgetPlanId);
                throw new MithrasException("利润预算数据不存在");
            }
            budgetPlanProfitId = budgetPlanProfit.getId();
            // 判断Ecl执行结果
            if (!isSuccess) {
                log.error("Ecl计算风险准备金失败，利润预算测算失败");
                SpringUtil.getBean(BudgetPlanProfitService.class).modifyCalculateStatus(budgetPlanProfitId, BudgetPlanCalculateStatusEnum.FAILURE);
                return;
            }
            // 执行处理逻辑
            this.doRefreshByEcl(budgetPlanProfit, list);
            // 更新预算状态
            SpringUtil.getBean(BudgetPlanProfitService.class).modifyCalculateStatus(budgetPlanProfitId, BudgetPlanCalculateStatusEnum.SUCCESS);
        } catch (Exception e) {
            log.error("处理Ecl回调发生未知异常[budgetPlanId:{}, dataList:{}]", budgetPlanId, JSONUtil.toJsonStr(list));
            SpringUtil.getBean(BudgetPlanProfitService.class).modifyCalculateStatus(budgetPlanProfitId, BudgetPlanCalculateStatusEnum.FAILURE);
        }
    }

    private void doRefreshByEcl(BudgetPlanProfit budgetPlanProfit, List<BudgetEclRiskReserveBO> list) {
        Optional<BudgetEclRiskReserveBO> beginEclOpt = list.stream().filter(e -> e.getCalculationDate().isEqual(budgetPlanProfit.getBudgetDateFrom().minusDays(1))).findFirst();
        Optional<BudgetEclRiskReserveBO> endEclOpt = list.stream().filter(e -> e.getCalculationDate().isEqual(budgetPlanProfit.getBudgetDateTo())).findFirst();
        List<BudgetPlanProfitDetail> updateList = new LinkedList<>();
        // 查询利润预算详情
        List<BudgetPlanProfitDetail> detailList = SpringUtil.getBean(BudgetPlanProfitDetailService.class).listHistoryByBudgetPlanId(budgetPlanProfit.getBudgetPlanId());
        Map<Long, List<BudgetPlanProfitDetail>> detailMap = detailList.stream().filter(e -> Objects.nonNull(e.getReceiptId()) && e.getReceiptId() > 0).collect(Collectors.groupingBy(BudgetPlanProfitDetail::getReceiptId));
        for (Map.Entry<Long, List<BudgetPlanProfitDetail>> entry : detailMap.entrySet()) {
            List<BudgetPlanProfitDetail> receiptDetailList = entry.getValue();
            for (BudgetPlanProfitDetail receiptDetail : receiptDetailList) {
                BigDecimal factor = this.ensureDeptPercent(receiptDetail.getContractId(), receiptDetail.getBelongDeptId());
                log.info("Ecl刷新利润预算数据，合同{}在部门{}的分润占比为{}}", receiptDetail.getContractId(), receiptDetail.getBelongDeptId(), factor.toPlainString());
                boolean updateFlag = false;
                if (beginEclOpt.isPresent()) {
                    Long riskFundBalance = beginEclOpt.get().getReceiptId2RiskReserve().get(entry.getKey());
                    Long riskFundBalanceOverdue = beginEclOpt.get().getReceiptId2RiskReserveOverdue().get(entry.getKey());
                    if (Objects.nonNull(riskFundBalance)) {
                        BigDecimal b = BigDecimal.valueOf(riskFundBalance).multiply(factor);
                        receiptDetail.setEndOfLastPeriodRiskFundIdeal(b.longValue());
                        updateFlag = true;
                    }
                    if (Objects.nonNull(riskFundBalanceOverdue)) {
                        BigDecimal b = BigDecimal.valueOf(riskFundBalanceOverdue).multiply(factor);
                        receiptDetail.setEndOfLastPeriodRiskFund(b.longValue());
                        updateFlag = true;
                    }
                }
                if (endEclOpt.isPresent()) {
                    Long riskFundBalance = endEclOpt.get().getReceiptId2RiskReserve().get(entry.getKey());
                    Long riskFundBalanceOverdue = endEclOpt.get().getReceiptId2RiskReserveOverdue().get(entry.getKey());
                    if (Objects.nonNull(riskFundBalance)) {
                        BigDecimal b = BigDecimal.valueOf(riskFundBalance).multiply(factor);
                        receiptDetail.setEndOfThisPeriodRiskFundIdeal(b.longValue());
                        updateFlag = true;
                    }
                    if (Objects.nonNull(riskFundBalanceOverdue)) {
                        BigDecimal b = BigDecimal.valueOf(riskFundBalanceOverdue).multiply(factor);
                        receiptDetail.setEndOfThisPeriodRiskFund(b.longValue());
                        updateFlag = true;
                    }
                }
                if (updateFlag) {
                    Integer expenseRate = SpringUtil.getBean(BudgetParameterConfigService.class).getExpenseRatioByDeptId(receiptDetail.getBelongDeptId());
                    receiptDetail.calculate(expenseRate);
                    updateList.add(receiptDetail);
                }
            }
        }
        // 更新数据
        if (CollectionUtil.isNotEmpty(updateList)) {
            transactionTemplate.executeWithoutResult(transactionStatus -> {
                try {
                    budgetPlanProfitDetailService.updateBatchById(updateList);
                } catch (Exception e) {
                    transactionStatus.setRollbackOnly();
                    log.error("Ecl批量更新利润预算明细数据发生异常", e);
                }
            });
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void recalculateByBudgetPlanPayDetail(BudgetPlanPayDetail budgetPlanPayDetail) {
        BudgetPlanProfit budgetPlanProfit = this.getOneByBudgetPlanId(budgetPlanPayDetail.getBudgetPlanId());
        List<BudgetPlanProfitDetail> todoList = this.calculateByBudgetPlanPayDetail(budgetPlanProfit, budgetPlanPayDetail);
        if (CollectionUtil.isEmpty(todoList)) {
            return;
        }
        // 找一下投放计划是否有对应的利润预算数据
        List<BudgetPlanProfitDetail> existProfitDetailList = SpringUtil.getBean(BudgetPlanProfitDetailService.class).list(Wrappers.<BudgetPlanProfitDetail>lambdaQuery().eq(BudgetPlanProfitDetail::getBudgetPlanPayDetailId, budgetPlanPayDetail.getId()));
        if (CollectionUtil.isEmpty(existProfitDetailList)) {
            // 直接保存即可
            SpringUtil.getBean(BudgetPlanProfitDetailService.class).saveBatch(todoList);
        } else {
            // 需要做变更，同时如果是年度/半年度的需要拷贝出利润调整项和备注填入到新的（按照业务设计应该只有一条，其他类型的忽略，因为其他类型没有开放利润调整项的修改入口）
            if (existProfitDetailList.size() == 1) {
                BudgetPlanProfitDetail exist = existProfitDetailList.get(0);
                todoList.forEach(e -> {
                    e.setId(exist.getId());
                    e.setProfitAdjust(exist.getProfitAdjust());
                    e.setRemark(exist.getRemark());
                });
                SpringUtil.getBean(BudgetPlanProfitDetailService.class).updateBatchById(todoList);
            } else {
                // 移除后新增
                Set<Long> removeIds = existProfitDetailList.stream().map(BudgetPlanProfitDetail::getId).collect(Collectors.toSet());
                SpringUtil.getBean(BudgetPlanProfitDetailService.class).removeByIds(removeIds);
                SpringUtil.getBean(BudgetPlanProfitDetailService.class).saveBatch(todoList);
            }
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void notifyCreatePlanPay(Long budgetPlanProfitId) {
        BudgetPlanProfit budgetPlanProfit = this.getById(budgetPlanProfitId);
        if (Objects.equals(budgetPlanProfit.getIsCollectTaskNotify(), YesOrNoNumberEnum.YES.getCode())) {
            throw new MithrasException("已发送待办，不允许再次发送");
        }
        BudgetPlanPay budgetPlanPay = budgetPlanPayService.getOne(Wrappers.<BudgetPlanPay>lambdaQuery().eq(BudgetPlanPay::getBudgetPlanId, budgetPlanProfit.getBudgetPlanId()));
        // 需要收集的话起流程
        Map<Long, String> processMap = null;
        if (Objects.equals(budgetPlanProfit.getNeedCollect(), YesOrNoNumberEnum.YES.getCode())) {
            if (StrUtil.equalsAny(budgetPlanProfit.getBudgetType(), BudgetPlanTypeEnum.MONTH.name(), BudgetPlanTypeEnum.MONTH_ADJUST.name())) {
                processMap = budgetPlanPayFlowService.createMonthFlow(budgetPlanPay.getId());
            } else {
                processMap = budgetPlanPayFlowService.createYearFlow(budgetPlanPay.getId());
            }
        } else {
            throw new MithrasException("当前预算<无需收集投放计划>，不允许该操作");
        }
        // 保存流程信息
        if (CollectionUtil.isNotEmpty(processMap)) {
            List<BudgetPlanPayProcessInfo> budgetPlanPayProcessInfoList = processMap.entrySet().stream().map(e -> {
                BudgetPlanPayProcessInfo budgetPlanPayProcessInfo = new BudgetPlanPayProcessInfo();
                budgetPlanPayProcessInfo.setBudgetPlanPayId(budgetPlanPay.getId());
                budgetPlanPayProcessInfo.setBudgetPlanId(budgetPlanPay.getBudgetPlanId());
                budgetPlanPayProcessInfo.setBelongDeptId(e.getKey());
                budgetPlanPayProcessInfo.setProcessInstanceId(e.getValue());
                budgetPlanPayProcessInfo.setIsCollectFinish(YesOrNoNumberEnum.NO.getCode());
                return budgetPlanPayProcessInfo;
            }).collect(Collectors.toList());
            SpringUtil.getBean(BudgetPlanPayProcessInfoService.class).saveBatch(budgetPlanPayProcessInfoList);
        }
        // 变更待办发送状态字段
        BudgetPlanProfit update = new BudgetPlanProfit();
        update.setId(budgetPlanProfitId);
        update.setIsCollectTaskNotify(YesOrNoNumberEnum.YES.getCode());
        this.updateById(update);
    }

    public List<BudgetPlanProfitProcessRSP> process(BudgetPlanProfitProcessREQ req) {
        BudgetPlanProfit budgetPlanProfit = this.getById(req.getBudgetPlanProfitId());
        if (Objects.isNull(budgetPlanProfit)) {
            throw new MithrasException("利润预算主数据不存在");
        }
        LambdaQueryWrapper<BudgetPlanProfitDetail> query = budgetPlanProfitDetailService.initDefaultQuery(req.getBudgetPlanProfitId());
        query.ge(BudgetPlanProfitDetail::getPayDate, budgetPlanProfit.getBudgetDateFrom());
        query.le(BudgetPlanProfitDetail::getPayDate, budgetPlanProfit.getBudgetDateTo());
        if (Objects.nonNull(req.getBelongDeptId())) {
            query.eq(BudgetPlanProfitDetail::getBelongDeptId, req.getBelongDeptId());
        }
        if (Objects.nonNull(req.getQueryDateFrom())) {
            query.ge(BudgetPlanProfitDetail::getPayDate, req.getQueryDateFrom());
        }
        if (Objects.nonNull(req.getQueryDateTo())) {
            query.le(BudgetPlanProfitDetail::getPayDate, req.getQueryDateTo());
        }
        query.last(StringUtil.mysqlLimit(0, 2000));
        List<BudgetPlanProfitDetail> detailList = budgetPlanProfitDetailService.list(query);
        if (CollectionUtil.isEmpty(detailList)) {
            return Collections.emptyList();
        }
        // 取出所有有数据的部门
        List<Long> deptIds = detailList.stream().map(BudgetPlanProfitDetail::getBelongDeptId).distinct().sorted(Comparator.comparing(e -> e)).collect(Collectors.toList());
        Map<Long, String> deptNameMap = id2NameService.deptId2Name(deptIds);
        // 按照日期分组
        Map<String, List<BudgetPlanProfitDetail>> map = detailList.stream().collect(Collectors.groupingBy(e -> LocalDateTimeUtil.format(e.getPayDate(), DatePattern.NORM_MONTH_PATTERN)));
        // 处理返回参数
        List<BudgetPlanProfitProcessRSP> rspList = new LinkedList<>();
        // 合计行的数据提前定义
        Map<Long, BudgetPlanProfitProcessRSP.DataGroupByDept> deptSumMap = new HashMap<>();
        for (Long deptId : deptIds) {
            BudgetPlanProfitProcessRSP.DataGroupByDept sumDeptData = new BudgetPlanProfitProcessRSP.DataGroupByDept();
            sumDeptData.setDeptId(deptId);
            sumDeptData.setDeptName(deptNameMap.get(deptId));
            deptSumMap.put(deptId, sumDeptData);
        }
        // FIXME 循环嵌套可优化为sql进行group by
        for (Map.Entry<String, List<BudgetPlanProfitDetail>> entry : map.entrySet()) {
            BudgetPlanProfitProcessRSP rsp = new BudgetPlanProfitProcessRSP();
            String yearMonth = entry.getKey();
            rsp.setDate(yearMonth);
            // 按照部门分组
            Map<Long, List<BudgetPlanProfitDetail>> deptMap = entry.getValue().stream().collect(Collectors.groupingBy(BudgetPlanProfitDetail::getBelongDeptId));
            List<BudgetPlanProfitProcessRSP.DataGroupByDept> dataGroupByDeptList = new LinkedList<>();
            for (Long deptId : deptIds) {
                BudgetPlanProfitProcessRSP.DataGroupByDept dataGroupByDept = new BudgetPlanProfitProcessRSP.DataGroupByDept();
                dataGroupByDept.setDeptId(deptId);
                dataGroupByDept.setDeptName(deptNameMap.get(deptId));
                List<BudgetPlanProfitDetail> list = deptMap.get(deptId);
                if (CollectionUtil.isNotEmpty(list)) {
                    for (BudgetPlanProfitDetail detail : list) {
                        if (Objects.equals(detail.getFtpIndustryCategory(), FtpIndustryCategoryEnum.FTP_PUBLIC_UTILITIES.name())) {
                            dataGroupByDept.publicUtilitiesAdd(Optional.ofNullable(detail.getActualPay()).orElse(0L));
                            deptSumMap.get(deptId).publicUtilitiesAdd(Optional.ofNullable(detail.getActualPay()).orElse(0L));
                        }
                        if (Objects.equals(detail.getFtpIndustryCategory(), FtpIndustryCategoryEnum.FTP_CIVIL_CONSUMPTION.name())) {
                            dataGroupByDept.civilConsumptionAdd(Optional.ofNullable(detail.getActualPay()).orElse(0L));
                            deptSumMap.get(deptId).civilConsumptionAdd(Optional.ofNullable(detail.getActualPay()).orElse(0L));
                        }
                        if (Objects.equals(detail.getFtpIndustryCategory(), FtpIndustryCategoryEnum.FTP_STATE_OWNED_INDUSTRY.name())) {
                            dataGroupByDept.stateOwnedIndustryAdd(Optional.ofNullable(detail.getActualPay()).orElse(0L));
                            deptSumMap.get(deptId).stateOwnedIndustryAdd(Optional.ofNullable(detail.getActualPay()).orElse(0L));
                        }
                        if (Objects.equals(detail.getFtpIndustryCategory(), FtpIndustryCategoryEnum.FTP_OTHER_INDUSTRY.name())) {
                            dataGroupByDept.otherIndustryAdd(Optional.ofNullable(detail.getActualPay()).orElse(0L));
                            deptSumMap.get(deptId).otherIndustryAdd(Optional.ofNullable(detail.getActualPay()).orElse(0L));
                        }
                    }
                }
                dataGroupByDeptList.add(dataGroupByDept);
            }
            rsp.setDeptDataList(dataGroupByDeptList);
            rspList.add(rsp);
        }
        // 根据日期排序
        rspList.sort(Comparator.comparing(BudgetPlanProfitProcessRSP::getDate));
        // 添加合计行
        BudgetPlanProfitProcessRSP sumRSP = new BudgetPlanProfitProcessRSP();
        sumRSP.setDate("合计");
        List<BudgetPlanProfitProcessRSP.DataGroupByDept> sumDeptList = new LinkedList<>(deptSumMap.values());
        sumDeptList.sort(Comparator.comparing(BudgetPlanProfitProcessRSP.DataGroupByDept::getDeptId));
        sumRSP.setDeptDataList(sumDeptList);
        return rspList;
    }

    public BudgetPlanProfitSummaryRSP summary(BudgetPlanProfitSummaryREQ req) {
        BudgetPlanProfit budgetPlanProfit = this.getById(req.getBudgetPlanProfitId());
        if (Objects.isNull(budgetPlanProfit)) {
            throw new MithrasException("利润预算数据不存在");
        }
        // 查询明细数据
        LambdaQueryWrapper<BudgetPlanProfitDetail> query = budgetPlanProfitDetailService.initDefaultQuery(req.getBudgetPlanProfitId());
        if (Objects.nonNull(req.getBelongDeptId())) {
            query.eq(BudgetPlanProfitDetail::getBelongDeptId, req.getBelongDeptId());
        }
        if (StrUtil.isNotBlank(req.getFtpIndustryCategory())) {
            query.eq(BudgetPlanProfitDetail::getFtpIndustryCategory, req.getFtpIndustryCategory());
        }
        query.last(StringUtil.mysqlLimit(0, 2000));
        List<BudgetPlanProfitDetail> detailList = budgetPlanProfitDetailService.list(query);
        if (CollectionUtil.isEmpty(detailList)) {
            BudgetPlanProfitSummaryRSP result = new BudgetPlanProfitSummaryRSP();
            result.setDeptDataList(Collections.emptyList());
            result.setSumData(new BudgetPlanProfitSummaryRSP.Data());
            return result;
        }
        // 按照部门id进行分组
        Map<Long, List<BudgetPlanProfitDetail>> deptMap = detailList.stream().collect(Collectors.groupingBy(BudgetPlanProfitDetail::getBelongDeptId));
        Map<Long, String> deptNameMap = id2NameService.deptId2Name(deptMap.keySet());
        // 处理返回参数
        List<BudgetPlanProfitSummaryRSP.BudgetPlanProfitDeptSummary> deptResult = new LinkedList<>();
        for (Map.Entry<Long, List<BudgetPlanProfitDetail>> entry : deptMap.entrySet()) {
            BudgetPlanProfitSummaryRSP.BudgetPlanProfitDeptSummary rsp = new BudgetPlanProfitSummaryRSP.BudgetPlanProfitDeptSummary();
            rsp.setBelongDeptId(entry.getKey());
            rsp.setBelongDeptName(deptNameMap.get(entry.getKey()));
            rsp.setDataList(this.summaryDeptDataList(budgetPlanProfit, entry.getValue()));
            // 添加部门小计行
            rsp.getDataList().add(this.buildSum("部门小计", Collections.singletonList(rsp)));
            deptResult.add(rsp);
        }
        // 添加合计行
        BudgetPlanProfitSummaryRSP.Data sumData = this.buildSum("", deptResult);
        // 返回参数
        BudgetPlanProfitSummaryRSP rsp = new BudgetPlanProfitSummaryRSP();
        rsp.setDeptDataList(deptResult);
        rsp.setSumData(sumData);
        return rsp;
    }

    public List<BudgetPlanProfitSummaryOtherRSP> summaryOther(BudgetPlanProfitSummaryOtherREQ req) {
        // 查询利润预算的预算区间
        BudgetPlanProfit budgetPlanProfit = this.getById(req.getBudgetPlanProfitId());
        if (Objects.isNull(budgetPlanProfit)) {
            throw new MithrasException("利润预算数据不存在");
        }
        // 查询所有明细数据
        LambdaQueryWrapper<BudgetPlanProfitDetail> query = budgetPlanProfitDetailService.initDefaultQuery(req.getBudgetPlanProfitId());
        if (Objects.nonNull(req.getBelongDeptId())) {
            query.eq(BudgetPlanProfitDetail::getBelongDeptId, req.getBelongDeptId());
        }
        List<BudgetPlanProfitDetail> detailList = budgetPlanProfitDetailService.list(query);
        if (CollectionUtil.isEmpty(detailList)) {
            return Collections.emptyList();
        }
        // 取出所有有数据的部门
        List<Long> deptIds = detailList.stream().map(BudgetPlanProfitDetail::getBelongDeptId).distinct().collect(Collectors.toList());
        Map<Long, String> deptNameMap = id2NameService.deptId2Name(deptIds);
        // 取预算区间的年份作为分组
        List<BudgetPlanProfitSummaryOtherRSP> result = new LinkedList<>();
        int beginYear = budgetPlanProfit.getBudgetDateFrom().getYear();
        int endYear = budgetPlanProfit.getBudgetDateTo().getYear();
        int targetYear = beginYear;
        while (targetYear <= endYear) {
            // 过滤出对应年份的数据
            final int finalTargetYear = targetYear;
            Map<Long, List<BudgetPlanProfitDetail>> detpDetailMap = detailList.stream().filter(e -> Objects.equals(String.valueOf(finalTargetYear), e.getPeriodValue())).collect(Collectors.groupingBy(BudgetPlanProfitDetail::getBelongDeptId));
            // 添加部门指标数据
            result.add(budgetPlanProfitDetailService.summaryOtherCalculate(targetYear, deptNameMap, detpDetailMap, "投放额", BudgetPlanProfitDetail::getActualPay));
            result.add(budgetPlanProfitDetailService.summaryOtherCalculate(targetYear, deptNameMap, detpDetailMap, "营业收入", BudgetPlanProfitDetail::getIncome));
            result.add(budgetPlanProfitDetailService.summaryOtherCalculate(targetYear, deptNameMap, detpDetailMap, "利润", BudgetPlanProfitDetail::getGrossProfit));
            result.add(budgetPlanProfitDetailService.summaryOtherCalculate(targetYear, deptNameMap, detpDetailMap, "费用", BudgetPlanProfitDetail::getExpenseIdeal));
            result.add(budgetPlanProfitDetailService.summaryOtherCalculate(targetYear, deptNameMap, detpDetailMap, "目标利润", BudgetPlanProfitDetail::getAssessmentProfitIdeal));
            result.add(budgetPlanProfitDetailService.summaryOtherCalculate(targetYear, deptNameMap, detpDetailMap, "目标利润（拨备前）", detail -> detail.getGrossProfit() - detail.getExpenseIdeal() + detail.getRiskFundDiffIdeal()));
            result.add(budgetPlanProfitDetailService.summaryOtherCalculate(targetYear, deptNameMap, detpDetailMap, "年初资产总额", BudgetPlanProfitDetail::getEndOfLastPeriodBalance));
            result.add(budgetPlanProfitDetailService.summaryOtherCalculate(targetYear, deptNameMap, detpDetailMap, "年末资产总额", BudgetPlanProfitDetail::getEndOfThisPeriodBalance));
            // IRR需要加权平均，不是单独的加总，需要特殊处理
            result.add(budgetPlanProfitDetailService.summaryOtherIrrCalculate(targetYear, deptNameMap, detpDetailMap, "收益率水平（IRR）"));
            targetYear++;
        }
        return result;
    }

    public PageR<BudgetPlanProfitListRSP> pageList(BudgetPlanProfitListREQ req) {
        Page<BudgetPlanProfit> pageQuery = new Page<>(req.getPage(), req.getPageSize());
        LambdaQueryWrapper<BudgetPlanProfit> conditionQuery = Wrappers.lambdaQuery();
        conditionQuery.orderByDesc(BudgetPlanProfit::getId);
        Page<BudgetPlanProfit> data = this.page(pageQuery, conditionQuery);
        List<BudgetPlanProfitListRSP> list = BeanUtil.copyToList(data.getRecords(), BudgetPlanProfitListRSP.class);
        // 查询预算主表补充一点数据
        if (CollectionUtil.isNotEmpty(list)) {
            List<BudgetPlan> budgetPlanList = SpringUtil.getBean(BudgetPlanService.class).listByIds(list.stream().map(BudgetPlanProfitListRSP::getBudgetPlanId).collect(Collectors.toSet()));
            Map<Long, BudgetPlan> budgetPlanMap = budgetPlanList.stream().collect(Collectors.toMap(BudgetPlan::getId, e -> e));
            for (BudgetPlanProfitListRSP rsp : list) {
                rsp.setIsAdjust(Optional.ofNullable(budgetPlanMap.get(rsp.getBudgetPlanId())).map(BudgetPlan::getIsAdjust).orElse(0));
            }
        }
        return PageR.of(list, data.getTotal(), data.getPages(), data.getCurrent(), data.getSize());
    }

    @Transactional(rollbackFor = Throwable.class)
    public Long create(BudgetPlanProfitCreateREQ req) {
        BudgetPlanTypeEnum budgetPlanTypeEnum = BudgetPlanTypeEnum.findByName(req.getBudgetType());
        if (Objects.isNull(budgetPlanTypeEnum)) {
            throw new MithrasException("未定义的预算计划类型");
        }
        BudgetPlan budgetPlan = BeanUtil.copyProperties(req, BudgetPlan.class);
        budgetPlan.setPlanYear(budgetPlan.getBudgetDateFrom().getYear());
        if (StrUtil.equalsAny(budgetPlan.getBudgetType(), BudgetPlanTypeEnum.MONTH.name(), BudgetPlanTypeEnum.MONTH_ADJUST.name())) {
            budgetPlan.setPlanMonth(budgetPlan.getBudgetDateFrom().getMonthValue());
        }
        if (StrUtil.equalsAny(budgetPlan.getBudgetType(), BudgetPlanTypeEnum.MONTH.name(), BudgetPlanTypeEnum.MONTH_ADJUST.name())) {
            budgetPlan.setPlanName(budgetPlan.getPlanYear() + "年" + budgetPlan.getPlanMonth() + "月" + budgetPlanTypeEnum.display());
        } else {
            budgetPlan.setPlanName(budgetPlan.getPlanYear() + "年" + budgetPlanTypeEnum.display());
        }
        if (Objects.equals(budgetPlan.getNeedCollect(), YesOrNoNumberEnum.YES.getCode())) {
            budgetPlan.setBudgetStatus(BudgetStatusEnum.COLLECTING.name());
        } else {
            budgetPlan.setBudgetStatus(BudgetStatusEnum.COLLECT_FINISH.name());
        }
        BudgetPlanProfit budgetPlanProfit = BeanUtil.copyProperties(budgetPlan, BudgetPlanProfit.class);
        BudgetPlanPay budgetPlanPay = BeanUtil.copyProperties(budgetPlan, BudgetPlanPay.class);
        BudgetPlanCost budgetPlanCost = BeanUtil.copyProperties(budgetPlan, BudgetPlanCost.class);
        // 写预算计划主表
        budgetPlanService.save(budgetPlan);
        // 写利润预算主表
        budgetPlanProfit.setBudgetPlanId(budgetPlan.getId());
        budgetPlanProfit.setBudgetPlanName(budgetPlan.getPlanName());
        budgetPlanProfit.setCalculateStatus(BudgetPlanCalculateStatusEnum.DOING.name());
        this.getBaseMapper().insert(budgetPlanProfit);
        // 写投放计划主表
        budgetPlanPay.setBudgetPlanId(budgetPlan.getId());
        budgetPlanPay.setBudgetPlanName(budgetPlan.getPlanName());
        budgetPlanPayService.save(budgetPlanPay);
        // 如果是月度调整计划，需要回填id并从对应的周报中拷贝一份投放计划的数据
        if (Objects.equals(budgetPlan.getBudgetType(), BudgetPlanTypeEnum.MONTH_ADJUST.name())) {
            if (Objects.isNull(req.getOriginBudgetPlanId())) {
                throw new MithrasException("<月度调整计划>的来源计划id不能为空");
            }
            BudgetPlan originBudgetPlan = new BudgetPlan();
            originBudgetPlan.setId(req.getOriginBudgetPlanId());
            originBudgetPlan.setIsAdjust(YesOrNoNumberEnum.YES.getCode());
            originBudgetPlan.setAdjustPlanId(budgetPlan.getId());
            budgetPlanService.updateById(originBudgetPlan);
//            // 找到周报
//            List<BudgetPlanPayWeeklyReportDetail> weeklyReportDetailList = SpringUtil.getBean(BudgetPlanPayWeeklyReportDetailService.class).findLatestByBudgetPlanId(req.getOriginBudgetPlanId());
//            if (CollectionUtil.isNotEmpty(weeklyReportDetailList)) {
//                // 找到对应的项目评审信息
//                Set<Long> projReviewIds = weeklyReportDetailList.stream().map(BudgetPlanPayWeeklyReportDetail::getProjReviewId).collect(Collectors.toSet());
//                List<ProjReviewBaseInfo> projReviewBaseInfoList = SpringUtil.getBean(ProjReviewBaseInfoService.class).listByIds(projReviewIds);
//                Map<Long, ProjReviewBaseInfo> projReviewBaseInfoMap = projReviewBaseInfoList.stream().collect(Collectors.toMap(ProjReviewBaseInfo::getId, e -> e));
//                List<BudgetPlanPayDetail> copyResult = weeklyReportDetailList.stream().map(e -> {
//                    BudgetPlanPayDetail copy = BeanUtil.copyProperties(e, BudgetPlanPayDetail.class);
//                    copy.reset();
//                    copy.setBudgetPlanId(budgetPlan.getId());
//                    copy.setBudgetPlanPayId(budgetPlanPay.getId());
//                    copy.setIsBusinessheadConfirm(YesOrNoNumberEnum.NO.getCode());
//                    copy.setIsLeaderinchargeConfirm(YesOrNoNumberEnum.NO.getCode());
//                    // 更新主办和部门
//                    ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoMap.get(e.getProjReviewId());
//                    if (Objects.nonNull(projReviewBaseInfo)) {
//                        copy.setBelongDeptId(projReviewBaseInfo.getBizDeptId());
//                        copy.setSponsorUserId(projReviewBaseInfo.getProjSponsorUserId());
//                    }
//                    return copy;
//                }).collect(Collectors.toList());
//                budgetPlanPayDetailService.saveBatch(copyResult);
//            }
            // 目前周报还没使用，从对应月度计划拷贝数据
            List<BudgetPlanPayDetail> detailList = new LinkedList<>();
            Set<Long> projReviewIds = new HashSet<>();
            List<BudgetPlanPayDetail> originPlanPayDetailList = budgetPlanPayDetailService.listByBudgetPlanId(req.getOriginBudgetPlanId());
            // 去掉投放日在填报区间外的项目
            originPlanPayDetailList.removeIf(e -> Objects.nonNull(e.getPlanPayDate()) && (e.getPlanPayDate().isBefore(budgetPlan.getWriteDateFrom()) || e.getPlanPayDate().isAfter(budgetPlan.getBudgetDateTo())));
            if (CollectionUtil.isNotEmpty(originPlanPayDetailList)) {
                // 找到对应的项目评审信息
                projReviewIds.addAll(originPlanPayDetailList.stream().map(BudgetPlanPayDetail::getProjReviewId).collect(Collectors.toSet()));
                List<ProjReviewBaseInfo> projReviewBaseInfoList = SpringUtil.getBean(ProjReviewBaseInfoService.class).listByIds(projReviewIds);
                Map<Long, ProjReviewBaseInfo> projReviewBaseInfoMap = projReviewBaseInfoList.stream().collect(Collectors.toMap(ProjReviewBaseInfo::getId, e -> e));
                // 查询项目投放
                Map<Long, Long> projReviewId2TotalPayMap = SpringUtil.getBean(PaymentActualDetailService.class).calculatePayAmountByProjReviewId(projReviewIds);
                List<BudgetPlanPayDetail> copyResult = originPlanPayDetailList.stream().map(e -> {
                    BudgetPlanPayDetail copy = BeanUtil.copyProperties(e, BudgetPlanPayDetail.class);
                    copy.reset();
                    copy.setBudgetPlanId(budgetPlan.getId());
                    copy.setBudgetPlanPayId(budgetPlanPay.getId());
                    copy.setIsBusinessheadConfirm(YesOrNoNumberEnum.NO.getCode());
                    copy.setIsLeaderinchargeConfirm(YesOrNoNumberEnum.NO.getCode());
                    // 更新主办和部门
                    ProjReviewBaseInfo projReviewBaseInfo = projReviewBaseInfoMap.get(e.getProjReviewId());
                    if (Objects.nonNull(projReviewBaseInfo)) {
                        copy.setBelongDeptId(projReviewBaseInfo.getBizDeptId());
                        copy.setSponsorUserId(projReviewBaseInfo.getProjSponsorUserId());
                    }
                    // 更新已投放金额
                    copy.setPaidAmount(Optional.ofNullable(projReviewId2TotalPayMap.get(e.getProjReviewId())).orElse(0L));
                    return copy;
                }).collect(Collectors.toList());
                detailList.addAll(copyResult);
            }
            // 补充不在月度投放计划内但在填报区间内有产生实际投放的项目
            this.suppleExtraProject(detailList, budgetPlan, budgetPlanPay, projReviewIds);
            if (CollectionUtil.isNotEmpty(detailList)) {
                budgetPlanPayDetailService.saveBatch(detailList);
            }
        }
        // 写成本预算主表
        budgetPlanCost.setBudgetPlanId(budgetPlan.getId());
        budgetPlanCost.setBudgetPlanName(budgetPlan.getPlanName());
        budgetPlanCost.setCalculateStatus(BudgetPlanCalculateStatusEnum.DOING.name());
        budgetPlanCostService.save(budgetPlanCost);
        return budgetPlanProfit.getId();
    }

    public void confirm(Long budgetPlanProfitId) {
        BudgetPlanProfit budgetPlanProfit = this.getById(budgetPlanProfitId);
        if (Objects.isNull(budgetPlanProfit)) {
            throw new MithrasException("利润预算数据不存在");
        }
        Long budgetPlanId = budgetPlanProfit.getBudgetPlanId();
        budgetPlanService.confirm(budgetPlanId);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void delete(Long budgetPlanProfitId) {
        BudgetPlanProfit budgetPlanProfit = this.getById(budgetPlanProfitId);
        if (Objects.isNull(budgetPlanProfit)) {
            throw new MithrasException("利润预算数据不存在");
        }
        Long budgetPlanId = budgetPlanProfit.getBudgetPlanId();
        budgetPlanService.delete(budgetPlanId);
        if (StrUtil.equals(budgetPlanProfit.getBudgetType(), BudgetPlanTypeEnum.MONTH_ADJUST.name())) {
            // 如果是月度调整计划删除，需重置对应月度计划的标识位
            BudgetPlan originBudgetPlan = budgetPlanService.findByAdjustPlanId(budgetPlanId);
            BudgetPlan update = new BudgetPlan();
            update.setId(originBudgetPlan.getId());
            update.setIsAdjust(YesOrNoNumberEnum.NO.getCode());
            update.setAdjustPlanId(0L);
            budgetPlanService.updateById(update);
        }
        // 删除对应的拨备计提
        SpringUtil.getBean(EclExecutePredictBaseInfoService.class).removeByBudgetPlan(budgetPlanId);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void deleteByBudgetPlanId(Long budgetPlanId) {
        LambdaQueryWrapper<BudgetPlanProfit> query = Wrappers.lambdaQuery();
        query.eq(BudgetPlanProfit::getBudgetPlanId, budgetPlanId);
        SpringUtil.getBean(BudgetPlanProfitService.class).remove(query);
        // 删除明细
        SpringUtil.getBean(BudgetPlanProfitDetailService.class).deleteByBudgetPlanId(budgetPlanId);
    }

    public void confirmByBudgetPlanId(Long budgetPlanId) {
        LambdaUpdateWrapper<BudgetPlanProfit> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.set(BudgetPlanProfit::getBudgetStatus, BudgetStatusEnum.CONFIRM.name());
        updateWrapper.eq(BudgetPlanProfit::getBudgetPlanId, budgetPlanId);
        this.update(updateWrapper);
    }

    private List<BudgetPlanProfitSummaryRSP.Data> summaryDeptDataList(BudgetPlanProfit budgetPlanProfit, List<BudgetPlanProfitDetail> detailList) {
        List<BudgetPlanProfitSummaryRSP.Data> dataList = new LinkedList<>();
        // 按照FTP行业分类进行分组
        if (Objects.isNull(detailList)) {
            detailList = Collections.emptyList();
        }
        Map<String, List<BudgetPlanProfitDetail>> map = detailList.stream().collect(Collectors.groupingBy(BudgetPlanProfitDetail::getFtpIndustryCategory));
        // 所有FTP行业分类都需要进行统计
        for (FtpIndustryCategoryEnum ftpIndustryCategoryEnum : FtpIndustryCategoryEnum.values()) {
            List<BudgetPlanProfitDetail> filterList = map.get(ftpIndustryCategoryEnum.name());
            dataList.add(this.buildData(budgetPlanProfit, ftpIndustryCategoryEnum, filterList));
        }
        return dataList;
    }

    private BudgetPlanProfitSummaryRSP.Data buildData(BudgetPlanProfit budgetPlanProfit, FtpIndustryCategoryEnum ftpIndustryCategoryEnum, List<BudgetPlanProfitDetail> filterList) {
        BudgetPlanProfitSummaryRSP.Data data = new BudgetPlanProfitSummaryRSP.Data();
        data.setSumRow(false);
        data.setFtpIndustryCategory(ftpIndustryCategoryEnum.name());
        data.setFtpIndustryCategoryDisplay(ftpIndustryCategoryEnum.getDisplay());
        if (CollectionUtil.isEmpty(filterList)) {
            return data;
        }
        // 新增项目需要额外计算以下四个
        BigDecimal irrFeature = BigDecimal.ZERO;
        BigDecimal consultingFeeRateYearFeature = BigDecimal.ZERO;
        BigDecimal consultingFeeRateFeature = BigDecimal.ZERO;
        BigDecimal ftpFeature = BigDecimal.ZERO;
        long expenseTotal = 0L;
        long riskFundDiff = 0L;
        long endOfLastPeriodBalance = 0L;
        long endOfThisPeriodBalance = 0L;
        for (BudgetPlanProfitDetail detail : filterList) {
            // 存量和新增的需拆开
            if (Objects.equals(detail.getDataCategory(), BudgetPlanDataCategoryEnum.HISTORY.name())) {
                // 不含税收入
                data.setIncomeHistory(data.getIncomeHistory() + Optional.ofNullable(detail.getIncomeWithoutTax()).orElse(0L));
                // 不含税成本
                data.setCostHistory(data.getCostHistory() + Optional.ofNullable(detail.getCostWithoutTax()).orElse(0L));
                // 差价 = 收入 - 成本
                data.setDiffHistory(data.getDiffHistory() + (detail.getIncomeWithoutTax() - detail.getCost()));
                // 税费+拨备 = 印花税 + 附加税 + 拨备
                data.setTaxRiskHistory(data.getTaxRiskHistory() + (Optional.ofNullable(detail.getStampTax()).orElse(0L) + Optional.ofNullable(detail.getAdditionalTax()).orElse(0L) + Optional.ofNullable(detail.getRiskFundDiffIdeal()).orElse(0L)));
                // 利润 = 差价 - （税费 + 拨备）
                data.setProfitHistory(data.getProfitHistory() + Optional.ofNullable(detail.getAssessmentProfitIdeal()).orElse(0L));
            }
            if (Objects.equals(detail.getDataCategory(), BudgetPlanDataCategoryEnum.FUTURE.name())) {
                data.setPayAmountFeature(data.getPayAmountFeature() + Optional.ofNullable(detail.getActualPay()).orElse(0L));
                // 不含税收入
                data.setIncomeFeature(data.getIncomeFeature() + Optional.ofNullable(detail.getIncomeWithoutTax()).orElse(0L));
                // 不含税成本
                data.setCostFeature(data.getCostFeature() + Optional.ofNullable(detail.getCostWithoutTax()).orElse(0L));
                // 差价 = 收入 - 成本
                data.setDiffFeature(data.getDiffFeature() + (detail.getIncomeWithoutTax() - detail.getCost()));
                // 税费 = 印花税 + 附加税
                data.setTaxFeature(data.getTaxFeature() + (Optional.ofNullable(detail.getStampTax()).orElse(0L) + Optional.ofNullable(detail.getAdditionalTax()).orElse(0L)));
                // 拨备
                data.setRiskFeature(data.getRiskFeature() + Optional.ofNullable(detail.getRiskFundDiffIdeal()).orElse(0L));
                // 利润 = 差价 - （税费 + 拨备）
                data.setProfitFeature(data.getProfitFeature() + Optional.ofNullable(detail.getAssessmentProfitIdeal()).orElse(0L));
                // 计算需要加权平均的
                if (Objects.nonNull(detail.getIrr())) {
                    irrFeature = irrFeature.add(BigDecimal.valueOf(detail.getIrr()).multiply(BigDecimal.valueOf(detail.getActualPay())));
                }
                if (Objects.nonNull(detail.getConsultingFeeRate())) {
                    consultingFeeRateFeature = consultingFeeRateFeature.add(BigDecimal.valueOf(detail.getConsultingFeeRate()).multiply(BigDecimal.valueOf(detail.getActualPay())));
                }
                if (Objects.nonNull(detail.getConsultingFeeRateYear())) {
                    consultingFeeRateYearFeature = consultingFeeRateYearFeature.add(BigDecimal.valueOf(detail.getConsultingFeeRateYear()).multiply(BigDecimal.valueOf(detail.getActualPay())));
                }
                if (Objects.nonNull(detail.getFtp())) {
                    ftpFeature = ftpFeature.add(BigDecimal.valueOf(detail.getFtp()).multiply(BigDecimal.valueOf(detail.getActualPay())));
                }
            }
            expenseTotal += Optional.ofNullable(detail.getExpenseIdeal()).orElse(0L);
            riskFundDiff += Optional.ofNullable(detail.getRiskFundDiffIdeal()).orElse(0L);
            endOfLastPeriodBalance += Optional.ofNullable(detail.getEndOfLastPeriodBalance()).orElse(0L);
            endOfThisPeriodBalance += Optional.ofNullable(detail.getEndOfThisPeriodBalance()).orElse(0L);
        }
        // 合计值
        data.setIncomeTotal(data.getIncomeHistory() + data.getIncomeFeature());
        data.setProfitTotal(data.getProfitHistory() + data.getProfitFeature());
        data.setExpenseTotal(expenseTotal);
        // 目标利润 = 合计利润 - 合计费用
        data.setProfitGoalTotal(data.getProfitTotal() - data.getExpenseTotal());
        // 目标利润（拨备前） = 目标利润 + （新增拨备 + 存量拨备）
        data.setProfitGoalWithoutRiskFundTotal(data.getProfitGoalTotal() + riskFundDiff);
        data.setBeginOfThisPeriodBalance(data.getBeginOfThisPeriodBalance() + endOfLastPeriodBalance);
        data.setEndOfThisPeriodBalance(data.getEndOfThisPeriodBalance() + endOfThisPeriodBalance);
        if (data.getPayAmountFeature() != 0) {
            data.setIrrFeature(Util.mithrasIntegerDecimalTwo(irrFeature.divide(BigDecimal.valueOf(data.getPayAmountFeature()), 2, RoundingMode.HALF_UP).intValue()));
            data.setConsultingFeeRateFeature(Util.mithrasIntegerDecimalTwo(consultingFeeRateFeature.divide(BigDecimal.valueOf(data.getPayAmountFeature()), 2, RoundingMode.HALF_UP).intValue()));
            data.setConsultingFeeRateYearFeature(Util.mithrasIntegerDecimalTwo(consultingFeeRateYearFeature.divide(BigDecimal.valueOf(data.getPayAmountFeature()), 2, RoundingMode.HALF_UP).intValue()));
            data.setFtpFeature(Util.mithrasIntegerDecimalTwo(ftpFeature.divide(BigDecimal.valueOf(data.getPayAmountFeature()), 2, RoundingMode.HALF_UP).intValue()));
        }
        return data;
    }

    private BudgetPlanProfitSummaryRSP.Data buildSum(String display, List<BudgetPlanProfitSummaryRSP.BudgetPlanProfitDeptSummary> rspList) {
        BudgetPlanProfitSummaryRSP.Data sumData = new BudgetPlanProfitSummaryRSP.Data();
        sumData.setSumRow(true);
        sumData.setFtpIndustryCategoryDisplay(display);
        long totalPay = 0L;
        BigDecimal irrFeature = BigDecimal.ZERO;
        BigDecimal consultingFeeRateYearFeature = BigDecimal.ZERO;
        BigDecimal consultingFeeRateFeature = BigDecimal.ZERO;
        BigDecimal ftpFeature = BigDecimal.ZERO;
        for (BudgetPlanProfitSummaryRSP.BudgetPlanProfitDeptSummary rsp : rspList) {
            for (BudgetPlanProfitSummaryRSP.Data data : rsp.getDataList()) {
                if (data.isSumRow()) {
                    // 跳过统计行，防止重复统计数据
                    continue;
                }
                sumData.setIncomeHistory(sumData.getIncomeHistory() + data.getIncomeHistory());
                sumData.setCostHistory(sumData.getCostHistory() + data.getCostHistory());
                sumData.setDiffHistory(sumData.getDiffHistory() + data.getDiffHistory());
                sumData.setTaxRiskHistory(sumData.getTaxRiskHistory() + data.getTaxRiskHistory());
                sumData.setProfitHistory(sumData.getProfitHistory() + data.getProfitHistory());
                sumData.setIncomeFeature(sumData.getIncomeFeature() + data.getIncomeFeature());
                sumData.setCostFeature(sumData.getCostFeature() + data.getCostFeature());
                sumData.setDiffFeature(sumData.getDiffFeature() + data.getDiffFeature());
                sumData.setTaxFeature(sumData.getTaxFeature() + data.getTaxFeature());
                sumData.setRiskFeature(sumData.getRiskFeature() + data.getRiskFeature());
                sumData.setProfitFeature(sumData.getProfitFeature() + data.getProfitFeature());
                sumData.setIncomeTotal(sumData.getIncomeTotal() + data.getIncomeTotal());
                sumData.setExpenseTotal(sumData.getExpenseTotal() + data.getExpenseTotal());
                sumData.setProfitTotal(sumData.getProfitTotal() + data.getProfitTotal());
                sumData.setProfitGoalTotal(sumData.getProfitGoalTotal() + data.getProfitGoalTotal());
                sumData.setProfitGoalWithoutRiskFundTotal(sumData.getProfitGoalWithoutRiskFundTotal() + data.getProfitGoalWithoutRiskFundTotal());
                sumData.setBeginOfThisPeriodBalance(sumData.getBeginOfThisPeriodBalance() + data.getBeginOfThisPeriodBalance());
                sumData.setEndOfThisPeriodBalance(sumData.getEndOfThisPeriodBalance() + data.getEndOfThisPeriodBalance());
                if (Objects.nonNull(data.getPayAmountFeature()) && data.getPayAmountFeature() > 0) {
                    totalPay += data.getPayAmountFeature();
                    if (Objects.nonNull(data.getIrrFeature())) {
                        irrFeature = irrFeature.add(BigDecimal.valueOf(data.getIrrFeature()).multiply(BigDecimal.valueOf(data.getPayAmountFeature())));
                    }
                    if (Objects.nonNull(data.getConsultingFeeRateFeature())) {
                        consultingFeeRateFeature = consultingFeeRateFeature.add(BigDecimal.valueOf(data.getConsultingFeeRateFeature()).multiply(BigDecimal.valueOf(data.getPayAmountFeature())));
                    }
                    if (Objects.nonNull(data.getConsultingFeeRateYearFeature())) {
                        consultingFeeRateYearFeature = consultingFeeRateYearFeature.add(BigDecimal.valueOf(data.getConsultingFeeRateYearFeature()).multiply(BigDecimal.valueOf(data.getPayAmountFeature())));
                    }
                    if (Objects.nonNull(data.getFtpFeature())) {
                        ftpFeature = ftpFeature.add(BigDecimal.valueOf(data.getFtpFeature()).multiply(BigDecimal.valueOf(data.getPayAmountFeature())));
                    }
                }
            }
        }
        sumData.setPayAmountFeature(totalPay);
        if (totalPay != 0) {
            sumData.setIrrFeature(Util.mithrasIntegerDecimalTwo(irrFeature.divide(BigDecimal.valueOf(totalPay), 2, RoundingMode.HALF_UP).intValue()));
            sumData.setConsultingFeeRateFeature(Util.mithrasIntegerDecimalTwo(consultingFeeRateFeature.divide(BigDecimal.valueOf(totalPay), 2, RoundingMode.HALF_UP).intValue()));
            sumData.setConsultingFeeRateYearFeature(Util.mithrasIntegerDecimalTwo(consultingFeeRateYearFeature.divide(BigDecimal.valueOf(totalPay), 2, RoundingMode.HALF_UP).intValue()));
            sumData.setFtpFeature(Util.mithrasIntegerDecimalTwo(ftpFeature.divide(BigDecimal.valueOf(totalPay), 2, RoundingMode.HALF_UP).intValue()));
        }
        return sumData;
    }

    public List<BudgetPlanProfitDetail> calculateByContract(BudgetPlanProfit budgetPlanProfit, ContractBaseInfo contractBaseInfo) {
//        // 实际起租日在填报区间内的不要
//        if (Objects.isNull(contractBaseInfo.getActualLeaseDate()) || (!budgetPlanProfit.getWriteDateFrom().isBefore(contractBaseInfo.getActualLeaseDate()) && !budgetPlanProfit.getWriteDateTo().isAfter(contractBaseInfo.getActualLeaseDate()))) {
//            log.info("{}的实际起租日在预算填报区间内，无需计算该合同", contractBaseInfo.getContractCode());
//            return Collections.emptyList();
//        }
        // 只取租赁合同
        if (!StrUtil.equalsAny(contractBaseInfo.getBizType(), ProjectBizType.ZL.name(), ProjectBizType.ZZ.name())) {
            log.info("{}的业务类型非目标类型，无需计算该合同", contractBaseInfo.getContractCode());
            return Collections.emptyList();
        }
        // 查询一些关联数据
        ContractLeasePrice contractLeasePrice = SpringUtil.getBean(ContractLeasePriceService.class).getByContractId(contractBaseInfo.getId());
        ProjReviewBaseInfo projReviewBaseInfo = SpringUtil.getBean(ProjReviewBaseInfoService.class).getById(contractBaseInfo.getProjReviewId());
        if (Objects.isNull(projReviewBaseInfo)) {
            log.warn("{}没有找到对应的项目评审，跳过不处理", contractBaseInfo.getContractCode());
            return Collections.emptyList();
        }
        ProjPricingBaseInfo projPricingBaseInfo = SpringUtil.getBean(ProjPricingBaseInfoService.class).getPricingByReviewIdExcludeProjName(projReviewBaseInfo.getId());
        // 准备计算相关数据
        List<BudgetPlanProfitDetail> result = new LinkedList<>();
        // 查询借据
        List<ContractReceipt> contractReceiptList = SpringUtil.getBean(ContractReceiptService.class).listByContractId(contractBaseInfo.getId());
        for (ContractReceipt contractReceipt : contractReceiptList) {
            if (Objects.isNull(contractReceipt.getReceiptStartDate())) {
                continue;
            }
            if (!contractReceipt.getReceiptStartDate().isBefore(budgetPlanProfit.getWriteDateFrom()) && !contractReceipt.getReceiptStartDate().isAfter(budgetPlanProfit.getWriteDateTo())) {
                log.warn("借据{}的起租日在预算填报区间内，无需计算该借据", contractReceipt.getReceiptCode());
                continue;
            }
            if (StrUtil.equals(budgetPlanProfit.getBudgetType(), BudgetPlanTypeEnum.OTHER.name())) {
                // 其他类型的预算计划，计算利润预算详情的时候需特殊处理（按照预算区间年份分录）
                List<BudgetPlanProfitDetail> budgetPlanProfitDetailList = this.buildOtherByContract(budgetPlanProfit, contractBaseInfo, contractReceipt, projReviewBaseInfo, projPricingBaseInfo, contractLeasePrice);
                if (CollectionUtil.isNotEmpty(budgetPlanProfitDetailList)) {
                    result.addAll(budgetPlanProfitDetailList);
                }
            } else if (StrUtil.equalsAny(budgetPlanProfit.getBudgetType(), BudgetPlanTypeEnum.MONTH.name(), BudgetPlanTypeEnum.MONTH_ADJUST.name())) {
                List<BudgetPlanProfitDetail> toInsertList = this.buildByContract(budgetPlanProfit, contractBaseInfo, contractReceipt, projReviewBaseInfo, projPricingBaseInfo, contractLeasePrice, budgetPlanProfit.getBudgetDateFrom(), budgetPlanProfit.getBudgetDateTo());
                result.addAll(toInsertList);
            } else {
                List<BudgetPlanProfitDetail> toInsertList = this.buildByContract(budgetPlanProfit, contractBaseInfo, contractReceipt, projReviewBaseInfo, projPricingBaseInfo, contractLeasePrice, budgetPlanProfit.getBudgetDateFrom(), budgetPlanProfit.getBudgetDateTo());
                result.addAll(toInsertList);
            }
        }
        return result;
    }

    private List<BudgetPlanProfitDetail> calculateByBudgetPlanPayDetail(BudgetPlanProfit budgetPlanProfit, BudgetPlanPayDetail budgetPlanPayDetail) {
        List<BudgetPlanProfitDetail> result = new LinkedList<>();
        if (Objects.isNull(budgetPlanPayDetail.getPlanPayDate())) {
            log.error("投放计划无投放日，无法计算利润数据[{}]", JSONUtil.toJsonStr(budgetPlanPayDetail));
            return result;
        }
        if (StrUtil.equals(budgetPlanProfit.getBudgetType(), BudgetPlanTypeEnum.OTHER.name())) {
            // 其他类型的预算计划，计算利润预算详情的时候需特殊处理（按照预算区间年份分录）
            List<BudgetPlanProfitDetail> budgetPlanProfitDetailList = this.calculateFromOther(budgetPlanProfit, budgetPlanPayDetail);
            if (CollectionUtil.isNotEmpty(budgetPlanProfitDetailList)) {
                result.addAll(budgetPlanProfitDetailList);
            }
        } else if (StrUtil.equalsAny(budgetPlanProfit.getBudgetType(), BudgetPlanTypeEnum.MONTH.name(), BudgetPlanTypeEnum.MONTH_ADJUST.name())) {
            BudgetPlanProfitDetail budgetPlanProfitDetail = this.calculateFromMonth(budgetPlanProfit, budgetPlanPayDetail);
            result.add(budgetPlanProfitDetail);
        } else {
            BudgetPlanProfitDetail budgetPlanProfitDetail = this.calculateFromNotMonth(budgetPlanProfit, budgetPlanPayDetail, budgetPlanProfit.getBudgetDateFrom(), budgetPlanProfit.getBudgetDateTo());
            result.add(budgetPlanProfitDetail);
        }
        return result;
    }

    private List<BudgetPlanProfitDetail> buildOtherByContract(BudgetPlanProfit budgetPlanProfit, ContractBaseInfo contractBaseInfo, ContractReceipt contractReceipt, ProjReviewBaseInfo projReviewBaseInfo, ProjPricingBaseInfo projPricingBaseInfo, ContractLeasePrice contractLeasePrice) {
        List<BudgetPlanProfitDetail> result = new LinkedList<>();
        // 复用年度/半年度的逻辑，特殊之处在于需要根据年份进行拆解
        int year = budgetPlanProfit.getBudgetDateFrom().getYear();
        while (year <= budgetPlanProfit.getBudgetDateTo().getYear()) {
            LocalDate dataStartDate;
            LocalDate dataEndDate;
            if (year == budgetPlanProfit.getBudgetDateFrom().getYear()) {
                dataStartDate = budgetPlanProfit.getBudgetDateFrom();
            } else {
                dataStartDate = LocalDate.of(year, 1, 1);
            }
            if (year == budgetPlanProfit.getBudgetDateTo().getYear()) {
                dataEndDate = budgetPlanProfit.getBudgetDateTo();
            } else {
                dataEndDate = LocalDate.of(year, 12, 31);
            }
            result.addAll(this.buildByContract(budgetPlanProfit, contractBaseInfo, contractReceipt, projReviewBaseInfo, projPricingBaseInfo, contractLeasePrice, dataStartDate, dataEndDate));
            year++;
        }
        return result;
    }

    private List<BudgetPlanProfitDetail> buildByContract(BudgetPlanProfit budgetPlanProfit, ContractBaseInfo contractBaseInfo, ContractReceipt contractReceipt, ProjReviewBaseInfo projReviewBaseInfo, ProjPricingBaseInfo projPricingBaseInfo, ContractLeasePrice contractLeasePrice, LocalDate dataStartDate, LocalDate dataEndDate) {
        if (Objects.isNull(dataStartDate) || Objects.isNull(dataEndDate)) {
            throw new MithrasException("非法的预算区间");
        }
        // 根据实际起租日是否在预算区间内来决定该合同的数据是新增还是存量
//        BudgetPlanDataCategoryEnum budgetPlanDataCategoryEnum = this.ensureDataCategory(contractBaseInfo.getActualLeaseDate(), budgetPlanProfit.getBudgetDateFrom(), budgetPlanProfit.getBudgetDateTo());
        BudgetPlanDataCategoryEnum budgetPlanDataCategoryEnum = this.ensureDataCategory(contractReceipt.getReceiptStartDate(), budgetPlanProfit.getBudgetDateFrom(), budgetPlanProfit.getBudgetDateTo());
        // 查询五级分类
        String assetClassifyResult = SpringUtil.getBean(AssetClassifyClientService.class).findLatestClassifyByClientId(contractBaseInfo.getClientId());
        if (StrUtil.isBlank(assetClassifyResult)) {
            assetClassifyResult = AssetClassifyResultEnum.NORMAL.name();
        }
        LocalDate earliestOverdueDate = null;
        // 五级分类是否正常
        boolean assetClassifyIsNormal = StrUtil.equals(assetClassifyResult, AssetClassifyResultEnum.NORMAL.name());
        // 五级分类是否后三类
        boolean assetClassifyIsLastThreeClassify = StrUtil.equalsAny(assetClassifyResult, AssetClassifyResultEnum.SECONDARY.name(), AssetClassifyResultEnum.SUSPICIOUS.name(), AssetClassifyResultEnum.LOSS.name());
        if (!assetClassifyIsNormal || assetClassifyIsLastThreeClassify) {
            // 需要找到最早逾期日期
            earliestOverdueDate = SpringUtil.getBean(CollectionBaseInfoService.class).findEarliestOverdueDate(contractReceipt.getId());
            log.info("{}在最近一次五级分类中处于后三类，最早逾期日期为:{}", contractBaseInfo.getContractCode(), LocalDateTimeUtil.format(earliestOverdueDate, DatePattern.NORM_DATE_PATTERN));
        }
        // 查询FTP价格
        FtpAssessmentInfo ftpAssessmentInfo = SpringUtil.getBean(FtpAssessmentInfoService.class).findLatestEffect(budgetPlanProfit.getBudgetDateFrom(), contractReceipt.getId());
        if (Objects.isNull(ftpAssessmentInfo)) {
            ftpAssessmentInfo = new FtpAssessmentInfo();
        }
        // 查询借据关联的付款
        List<PaymentBaseInfo> paymentBaseInfoList = SpringUtil.getBean(PaymentBaseInfoService.class).listByReceiptId(contractReceipt.getId());
        // 投放金额
        long actualPay = 0L;
        // 保证金
        long deposit = 0L;
        // 咨询服务费
        long consultingFee = 0L;
        if (CollectionUtil.isNotEmpty(paymentBaseInfoList)) {
            Set<Long> paymentIds = paymentBaseInfoList.stream().map(PaymentBaseInfo::getId).collect(Collectors.toSet());
            List<PaymentActualDetail> paymentActualDetailList = SpringUtil.getBean(PaymentActualDetailService.class).listByPaymentIds(paymentIds);
            // 取预算区间内的投放金额
            actualPay = paymentActualDetailList.stream().filter(e -> !e.getPaidInDate().isBefore(dataStartDate) && !e.getPaidInDate().isAfter(dataEndDate)).mapToLong(PaymentActualDetail::getPaidInAmount).sum();
            List<PaymentCollectionInfo> paymentCollectionInfoList = SpringUtil.getBean(PaymentCollectionInfoMapper.class).selectList(Wrappers.<PaymentCollectionInfo>lambdaQuery().in(PaymentCollectionInfo::getPaymentId, paymentIds));
            deposit = paymentCollectionInfoList.stream().mapToLong(PaymentCollectionInfo::getEarnestMoney).sum();
            consultingFee = paymentCollectionInfoList.stream().mapToLong(PaymentCollectionInfo::getConsultingFee).sum();
        }
        long endOfLastPeriodBalance;
        long endOfThisPeriodBalance;
        if (!assetClassifyIsNormal && Objects.nonNull(earliestOverdueDate)) {
            // 如果是非“正常”类且找到了最早逾期日期，则余额只计算到最早逾期日
            if (earliestOverdueDate.isBefore(dataStartDate)) {
                // 期初余额 = 期末余额 = 最早逾期日余额
                endOfLastPeriodBalance = this.calculatePrincipalBalance(contractReceipt, earliestOverdueDate, true, true);
                endOfThisPeriodBalance = endOfLastPeriodBalance;
            } else if (earliestOverdueDate.isAfter(dataEndDate)) {
                // 期初余额 = 区间开始日余额，期末余额 = 区间结束日余额
                endOfLastPeriodBalance = this.calculatePrincipalBalance(contractReceipt, dataStartDate, false, false);
                endOfThisPeriodBalance = this.calculatePrincipalBalance(contractReceipt, dataEndDate, true, false);
            } else {
                // 期初余额 = 区间开始日余额，期末余额 = 最早逾期日余额
                endOfLastPeriodBalance = this.calculatePrincipalBalance(contractReceipt, dataStartDate, false, false);
                endOfThisPeriodBalance = this.calculatePrincipalBalance(contractReceipt, earliestOverdueDate, true, true);
            }
        } else {
            endOfLastPeriodBalance = this.calculatePrincipalBalance(contractReceipt, dataStartDate, false, false);
            endOfThisPeriodBalance = this.calculatePrincipalBalance(contractReceipt, dataEndDate, true, false);
        }
        // 营业收入
        long interestIncome = 0L;
        if (Objects.equals(contractBaseInfo.getIncomeConfirmType(), IncomeConfirmTypeEnum.AIR.name())) {
            // 实际利率法
            if (assetClassifyIsLastThreeClassify && Objects.nonNull(earliestOverdueDate)) {
                // 收入只算到最早逾期日之前
                interestIncome = SpringUtil.getBean(ContractIncomeSharingMapper.class).sumIncome(contractReceipt.getId(), dataStartDate, earliestOverdueDate.minusDays(1));
            } else {
                interestIncome = SpringUtil.getBean(ContractIncomeSharingMapper.class).sumIncome(contractReceipt.getId(), dataStartDate, dataEndDate);
            }
        } else {
            // 剩余本金法 = 预算区间内利息收入之和+预算区间期末的剩余本金法计提的收入-预算区间期初最后一个月的剩余本金法计提的收入
            LocalDate calculateEndDate = dataEndDate;
            if (assetClassifyIsLastThreeClassify && Objects.nonNull(earliestOverdueDate)) {
                calculateEndDate = earliestOverdueDate;
            }
            if (calculateEndDate.isAfter(dataStartDate)) {
                long interestSum = SpringUtil.getBean(CollectionBaseInfoMapper.class).calculatePlanInterest(contractReceipt.getId(), dataStartDate, calculateEndDate);
                long incomeRP = SpringUtil.getBean(ContractIncomeSharingService.class).calculateIncomeRP(contractReceipt.getId(), dataStartDate, calculateEndDate);
                BigDecimal b = BigDecimal.valueOf(interestSum).add(BigDecimal.valueOf(incomeRP));
                interestIncome = Util.mithrasLongDecimalTwo(b.longValue());
            } else {
                log.info("借据{}的最早逾期日期比预算区间开始日期还要早，利息收入为0", contractReceipt.getReceiptCode());
            }
        }
        // 预算区间内核销的其他款项也要算收入
        long consultingFeeIncome = 0L;
        // 罚息
        long penaltyInterestIncome = 0L;
        // 提前终止补偿金
        long earlyStopCompensationIncome = 0L;
        // 租前息
        long beforeInterestIncome = 0L;
        List<CollectionBaseInfo> incomeCollectionBaseInfoList = SpringUtil.getBean(CollectionBaseInfoService.class).listByContractIds(Collections.singletonList(contractBaseInfo.getId()));
        if (CollectionUtil.isNotEmpty(incomeCollectionBaseInfoList)) {
            Set<Long> paymentIds = paymentBaseInfoList.stream().map(PaymentBaseInfo::getId).collect(Collectors.toSet());
            consultingFeeIncome = incomeCollectionBaseInfoList.stream()
                    .filter(e -> Objects.equals(e.getReceiptId(), contractReceipt.getId()) || paymentIds.contains(e.getPaymentId()))
                    .filter(e -> StrUtil.equals(e.getCashFlowItem(), CashFlowItemEnum.OTHERAMOUNT.name()))
                    .filter(e -> Objects.nonNull(e.getCollectionAmount()))
                    .filter(e -> Objects.nonNull(e.getCollectionDate()))
                    .filter(e -> !e.getCollectionDate().isBefore(dataStartDate) && !e.getCollectionDate().isAfter(dataEndDate)).mapToLong(CollectionBaseInfo::getCollectionAmount).sum();
            penaltyInterestIncome = incomeCollectionBaseInfoList.stream()
                    .filter(e -> Objects.equals(e.getReceiptId(), contractReceipt.getId()) || paymentIds.contains(e.getPaymentId()))
                    .filter(e -> StrUtil.equals(e.getCashFlowItem(), CashFlowItemEnum.RENT.name()))
                    .filter(e -> Objects.nonNull(e.getCollectionPenaltyInterest()))
                    .filter(e -> Objects.nonNull(e.getCollectionDate()))
                    .filter(e -> !e.getCollectionDate().isBefore(dataStartDate) && !e.getCollectionDate().isAfter(dataEndDate)).mapToLong(CollectionBaseInfo::getCollectionPenaltyInterest).sum();
            // 提前终止补偿金放在合同的最后一期租金所在的借据
            ContractRentActual lastRent = this.findLastRent(contractBaseInfo);
            if (Objects.nonNull(lastRent) && Objects.equals(contractReceipt.getId(), lastRent.getReceiptId())) {
                earlyStopCompensationIncome = incomeCollectionBaseInfoList.stream()
                        .filter(e -> StrUtil.equals(e.getCashFlowItem(), CashFlowItemEnum.EARLY_STOP_COMPENSATION.name()))
                        .filter(e -> Objects.nonNull(e.getCollectionAmount()))
                        .filter(e -> Objects.nonNull(e.getCollectionDate()))
                        .filter(e -> !e.getCollectionDate().isBefore(dataStartDate) && !e.getCollectionDate().isAfter(dataEndDate)).mapToLong(CollectionBaseInfo::getCollectionAmount).sum();
            }
            // 租前息没有单列现金流类型，取合同起租日前的利息即可
            if (assetClassifyIsLastThreeClassify && Objects.nonNull(earliestOverdueDate)) {
                // 租前息收入只算到最早逾期日之前
                final LocalDate targetDate = earliestOverdueDate;
                beforeInterestIncome = incomeCollectionBaseInfoList.stream()
                        .filter(e -> StrUtil.equals(e.getCashFlowItem(), CashFlowItemEnum.RENT.name()))
                        .filter(e -> e.getPlanCollectionDate().isBefore(contractBaseInfo.getActualLeaseDate()) && e.getPlanCollectionDate().isBefore(targetDate))
                        .filter(e -> Objects.nonNull(e.getPlanCollectionAmount())).mapToLong(CollectionBaseInfo::getPlanCollectionAmount).sum();
            } else {
                beforeInterestIncome = incomeCollectionBaseInfoList.stream()
                        .filter(e -> StrUtil.equals(e.getCashFlowItem(), CashFlowItemEnum.RENT.name()))
                        .filter(e -> e.getPlanCollectionDate().isBefore(contractBaseInfo.getActualLeaseDate()))
                        .filter(e -> !e.getPlanCollectionDate().isBefore(dataStartDate) && !e.getPlanCollectionDate().isAfter(dataEndDate))
                        .filter(e -> Objects.nonNull(e.getInterest())).mapToLong(CollectionBaseInfo::getInterest).sum();
            }
        }
        // 预算区间内将要结清的名义价款也要算上
        long nominalPriceIncome = 0L;
        if (Objects.nonNull(contractLeasePrice.getNominalPrice()) && Objects.isNull(earliestOverdueDate)) {
            ContractRentActual lastRent = this.findLastRent(contractBaseInfo);
            // 如果最后一期是当前借据且计划还款日期处于预算区间内则计算名义价款
            if (Objects.nonNull(lastRent) && Objects.equals(lastRent.getReceiptId(), contractReceipt.getId()) && !lastRent.getCashFlowDate().isBefore(dataStartDate) && !lastRent.getCashFlowDate().isAfter(dataEndDate)) {
                nominalPriceIncome = contractLeasePrice.getNominalPrice();
            }
        }
        // 营业收入 = 利息收入 + 咨询服务费收入 + 名义价款收入 + 罚息收入 + 提前终止补偿金收入 + 租前息收入
        BigDecimal incomeBD = BigDecimal.valueOf(consultingFeeIncome + interestIncome + nominalPriceIncome + penaltyInterestIncome + earlyStopCompensationIncome + beforeInterestIncome);
        // 不含税收入
        BigDecimal consultingFeeTaxRate = FinancialUtil.ensureConsultingTaxRate();
        BigDecimal valueAddedTaxRate = FinancialUtil.ensureValueAddedTaxRate(contractBaseInfo.getLeaseType());
        BigDecimal interestIncomeWithoutTaxBD = FinancialUtil.calculateAmountWithoutTax(interestIncome, valueAddedTaxRate);
        BigDecimal consultingFeeIncomeWithoutBD = FinancialUtil.calculateAmountWithoutTax(consultingFeeIncome, consultingFeeTaxRate);
        BigDecimal nominalPriceIncomeWithoutTaxBD = FinancialUtil.calculateAmountWithoutTax(nominalPriceIncome, valueAddedTaxRate);
        BigDecimal penaltyInterestIncomeWithoutBD = FinancialUtil.calculateAmountWithoutTax(penaltyInterestIncome, valueAddedTaxRate);
        BigDecimal earlyStopCompensationIncomeWithoutBD = FinancialUtil.calculateAmountWithoutTax(earlyStopCompensationIncome, valueAddedTaxRate);
        BigDecimal beforeInterestIncomeWithoutBD = FinancialUtil.calculateAmountWithoutTax(beforeInterestIncome, valueAddedTaxRate);
        BigDecimal incomeWithoutTaxBD = interestIncomeWithoutTaxBD.add(consultingFeeIncomeWithoutBD).add(nominalPriceIncomeWithoutTaxBD).add(penaltyInterestIncomeWithoutBD).add(earlyStopCompensationIncomeWithoutBD).add(beforeInterestIncomeWithoutBD);
        // 营业成本
        long cost = 0L;
        int costCashFtp = Optional.ofNullable(ftpAssessmentInfo.getAssessmentPrice()).map(Long::intValue).orElse(0);
        int costBillFtp = Optional.ofNullable(ftpAssessmentInfo.getTicketPrice()).map(Long::intValue).orElse(0);
        long currentCashOccupy = 0L;
        long currentBillOccupy = 0L;
        long totalCashOccupy = 0L;
        long totalBillOccupy = 0L;
        LocalDate budgetFtpInterestStartDate = null;
        // 取FTP计息数据计算
        FtpInterestBaseInfo ftpInterestBaseInfo = SpringUtil.getBean(FtpInterestBaseInfoService.class).getOneByReceiptId(contractReceipt.getId());
        // 营业成本包含两部分：已有的FTP计息数据 + FTP计息最近更新日期后一天到预算区间结束日期的计算数据
        // 查询租金现金流
        List<CollectionBaseInfo> collectionBaseInfoList = SpringUtil.getBean(CollectionBaseInfoService.class).listRentByReceiptId(contractReceipt.getId());
        if (Objects.nonNull(ftpInterestBaseInfo)) {
            if (Objects.nonNull(ftpInterestBaseInfo.getLastUpdateDate())) {
                // 存在FTP计息详情
                if (ftpInterestBaseInfo.getLastUpdateDate().isBefore(dataStartDate)) {
                    // 资金占用需初始化为预算区间初的资金占用
                    FtpInterestDetailRecord ftpInterestRecordLatest = SpringUtil.getBean(FtpInterestDetailRecordService.class).getLatestRecord(ftpInterestBaseInfo.getId(), dataStartDate.minusDays(1));
                    if (Objects.nonNull(ftpInterestRecordLatest)) {
                        budgetFtpInterestStartDate = dataStartDate;
                        currentCashOccupy = Optional.ofNullable(ftpInterestRecordLatest.getCashOccupy()).orElse(0L);
                        currentBillOccupy = Optional.ofNullable(ftpInterestRecordLatest.getBillOccupy()).orElse(0L);
                        List<CollectionBaseInfo> list = collectionBaseInfoList.stream().filter(e -> e.getPlanCollectionDate().isBefore(dataStartDate) && e.getPlanCollectionDate().isAfter(ftpInterestRecordLatest.getInterestDate())).collect(Collectors.toList());
                        // 上一次FTP计息日期到预算区间开始日期之间如果有租金收入则需要扣除
                        currentCashOccupy = currentCashOccupy - list.stream().filter(e -> Objects.nonNull(e.getPlanCollectionAmount())).mapToLong(CollectionBaseInfo::getPlanCollectionAmount).sum();
                    }
                } else if (ftpInterestBaseInfo.getLastUpdateDate().isAfter(dataEndDate)) {
                    // 说明预算区间中的数据都使用FTP计息数据
                    cost = SpringUtil.getBean(FtpInterestDetailRecordMapper.class).sumTotalInterestBetweenTargetRange(ftpInterestBaseInfo.getId(), dataStartDate, dataEndDate);
                    budgetFtpInterestStartDate = ftpInterestBaseInfo.getLastUpdateDate().plusDays(1);
                    FtpInterestDetailRecord ftpInterestRecordLatest = SpringUtil.getBean(FtpInterestDetailRecordService.class).getLatestRecord(ftpInterestBaseInfo.getId(), dataEndDate);
                    currentCashOccupy = Optional.ofNullable(ftpInterestRecordLatest).map(FtpInterestDetailRecord::getCashOccupy).orElse(0L);
                    currentBillOccupy = Optional.ofNullable(ftpInterestRecordLatest).map(FtpInterestDetailRecord::getBillOccupy).orElse(0L);
                    totalCashOccupy = SpringUtil.getBean(FtpInterestDetailRecordMapper.class).sumTotalCashOccupyBetweenTargetRange(ftpInterestBaseInfo.getId(), dataStartDate, dataEndDate);
                    totalBillOccupy = SpringUtil.getBean(FtpInterestDetailRecordMapper.class).sumTotalBillOccupyBetweenTargetRange(ftpInterestBaseInfo.getId(), dataStartDate, dataEndDate);
                    if (Objects.nonNull(ftpInterestRecordLatest) && Objects.nonNull(ftpInterestRecordLatest.getCashFtp())) {
                        costCashFtp = ftpInterestRecordLatest.getCashFtp() + Optional.ofNullable(ftpInterestRecordLatest.getFtpOverdueAdjust()).orElse(0);
                    }
                    if (Objects.nonNull(ftpInterestRecordLatest) && Objects.nonNull(ftpInterestRecordLatest.getBillFtp())) {
                        costBillFtp = ftpInterestRecordLatest.getBillFtp();
                    }
                } else {
                    // 说明一部分用FTP计息数据，一部分用新算的数据
                    cost = SpringUtil.getBean(FtpInterestDetailRecordMapper.class).sumTotalInterestBetweenTargetRange(ftpInterestBaseInfo.getId(), dataStartDate, ftpInterestBaseInfo.getLastUpdateDate());
                    budgetFtpInterestStartDate = ftpInterestBaseInfo.getLastUpdateDate().plusDays(1);
                    FtpInterestDetailRecord ftpInterestRecordLatest = SpringUtil.getBean(FtpInterestDetailRecordService.class).getLatestRecord(ftpInterestBaseInfo.getId(), ftpInterestBaseInfo.getLastUpdateDate());
                    currentCashOccupy = Optional.ofNullable(ftpInterestRecordLatest).map(FtpInterestDetailRecord::getCashOccupy).orElse(0L);
                    currentBillOccupy = Optional.ofNullable(ftpInterestRecordLatest).map(FtpInterestDetailRecord::getBillOccupy).orElse(0L);
                    totalCashOccupy = SpringUtil.getBean(FtpInterestDetailRecordMapper.class).sumTotalCashOccupyBetweenTargetRange(ftpInterestBaseInfo.getId(), dataStartDate, ftpInterestBaseInfo.getLastUpdateDate());
                    totalBillOccupy = SpringUtil.getBean(FtpInterestDetailRecordMapper.class).sumTotalBillOccupyBetweenTargetRange(ftpInterestBaseInfo.getId(), dataStartDate, ftpInterestBaseInfo.getLastUpdateDate());
                    if (Objects.nonNull(ftpInterestRecordLatest) && Objects.nonNull(ftpInterestRecordLatest.getCashFtp())) {
                        costCashFtp = ftpInterestRecordLatest.getCashFtp() + Optional.ofNullable(ftpInterestRecordLatest.getFtpOverdueAdjust()).orElse(0);
                    }
                    if (Objects.nonNull(ftpInterestRecordLatest) && Objects.nonNull(ftpInterestRecordLatest.getBillFtp())) {
                        costBillFtp = ftpInterestRecordLatest.getBillFtp() + Optional.ofNullable(ftpInterestRecordLatest.getFtpOverdueAdjust()).orElse(0);
                    }
                }
            }
        }
        if (Objects.isNull(budgetFtpInterestStartDate)) {
            // 兜底逻辑
            budgetFtpInterestStartDate = dataStartDate;
            List<CollectionBaseInfo> list = collectionBaseInfoList.stream().filter(e -> !e.getPlanCollectionDate().isBefore(dataStartDate)).collect(Collectors.toList());
            currentCashOccupy = list.stream().filter(e -> Objects.nonNull(e.getPrincipal())).mapToLong(CollectionBaseInfo::getPrincipal).sum();
        }
        // 查询预算区间内的应还租金
        Map<LocalDate, List<CollectionBaseInfo>> collectionBaseInfoMap = collectionBaseInfoList.stream().collect(Collectors.groupingBy(CollectionBaseInfo::getPlanCollectionDate));
        LocalDate interestDate = budgetFtpInterestStartDate;
        List<FtpInterestHelper> tempList = new LinkedList<>();
        while (!interestDate.isAfter(dataEndDate)) {
            List<CollectionBaseInfo> list = collectionBaseInfoMap.get(interestDate);
            if (CollectionUtil.isNotEmpty(list)) {
                // 取未核销的金额（防止提前核销导致重复减少资金占用）
                long planCollectionAmount = list.stream().filter(e -> Objects.nonNull(e.getPlanCollectionAmount())).mapToLong(CollectionBaseInfo::getPlanCollectionAmount).sum();
                long actualCollectionAmount = list.stream().filter(e -> Objects.nonNull(e.getCollectionAmount())).mapToLong(CollectionBaseInfo::getCollectionAmount).sum();
                long adjustCashOccupy = Math.max((planCollectionAmount - actualCollectionAmount), 0L);
                // 调整资金占用
                if (!assetClassifyIsLastThreeClassify) {
                    currentCashOccupy = currentCashOccupy - adjustCashOccupy;
                }
            }
            BigDecimal cashInterestBD = BigDecimal.valueOf(currentCashOccupy).multiply(BigDecimal.valueOf(costCashFtp).divide(BigDecimal.valueOf(360 * 1000000), 20, RoundingMode.HALF_UP));
            long currentCashInterest = Util.mithrasLongDecimalTwo(cashInterestBD.longValue());
            BigDecimal billInterestBD = BigDecimal.valueOf(currentBillOccupy).multiply(BigDecimal.valueOf(costBillFtp).divide(BigDecimal.valueOf(360 * 1000000), 20, RoundingMode.HALF_UP));
            long currentBillInterest = Util.mithrasLongDecimalTwo(billInterestBD.longValue());
            long fundInterest = currentCashInterest + currentBillInterest;
            if (fundInterest < 0) {
                fundInterest = 0;
                currentCashInterest = 0;
                currentBillInterest = 0;
            }
            tempList.add(new FtpInterestHelper(LocalDateTimeUtil.format(interestDate, DatePattern.NORM_DATE_PATTERN), currentCashInterest, costCashFtp, currentCashOccupy, currentBillInterest, costBillFtp, currentBillOccupy));
            cost = cost + fundInterest;
            totalCashOccupy += currentCashOccupy;
            totalBillOccupy += currentBillOccupy;
            interestDate = interestDate.plusDays(1);
        }
        log.info("{}计算未来的FTP计息结果:{}", contractReceipt.getReceiptCode(), JSONUtil.toJsonStr(tempList));
        tempList.clear();
        BigDecimal costBD = BigDecimal.valueOf(cost);
        // 增值税 = （不含税收入 - 营业成本） * 税率
        BigDecimal valueAddedTaxBD = incomeWithoutTaxBD.subtract(costBD).multiply(valueAddedTaxRate);
        if (valueAddedTaxBD.longValue() < 0) {
            valueAddedTaxBD = BigDecimal.ZERO;
        }
        // 附加税 = 增值税 * 12%
        BigDecimal additionalTaxBD = valueAddedTaxBD.multiply(BigDecimal.valueOf(0.12));
        // 印花税
        BigDecimal stampTaxBD;
        if (budgetPlanDataCategoryEnum == BudgetPlanDataCategoryEnum.HISTORY) {
            // 存量项目不存在投放，无印花税
            stampTaxBD = BigDecimal.ZERO;
        } else {
            // 印花税 = 借据信息中的印花税
            stampTaxBD = BigDecimal.valueOf(Optional.ofNullable(contractReceipt.getStampDuty()).orElse(0L));
        }
        // 毛利 = 营业收入（不含税） - 营业成本（含税） - 附加税 - 印花税
        BigDecimal grassProfitBD = incomeWithoutTaxBD.subtract(costBD).subtract(additionalTaxBD).subtract(stampTaxBD);
        // 拨备 = 预算区间末（剩余本金-剩余保证金）*预算区间末风险准备金计提比例-预算区间初（剩余本金-剩余保证金）*预算区间初风险准备金计提比例（风险准备金计提比例取【财务管理-拨备计提】中最新的计提比例）
        // 填报期间前的项目只有客户对应最近一次五级分类是“正常”的会随着剩余租赁年份进行计提比例的变化，否则延用最近一次拨备计提中的计提比例不变
        RiskFundHelper riskFundHelper = this.calculateRiskFund(endOfLastPeriodBalance, endOfThisPeriodBalance, contractBaseInfo, dataStartDate, dataEndDate, contractReceipt, assetClassifyIsNormal);
//        // 考核利润 = 毛利-拨备+项目利润调整项
//        // 此处是初始化，一定不存在项目利润调整项
//        BigDecimal profitBD = grassProfitBD.subtract(riskFundHelper.getRiskFundDiffBD());
//        // 扣费后利润
        Integer expenseRate = SpringUtil.getBean(BudgetParameterConfigService.class).getExpenseRatioByDeptId(contractBaseInfo.getBizDeptId());
//        BigDecimal profitWithoutExpenseBD = FinancialUtil.calculateProfitWithoutExpense(profitBD, expenseRate);
//        // 费用 = 考核利润 - 扣费后利润
//        BigDecimal expenseBD = profitBD.subtract(profitWithoutExpenseBD);
//        // 考核利润（原始值） = 毛利 - 拨备
//        BigDecimal profitOriginalBD = grassProfitBD.subtract(riskFundHelper.getRiskFundDiffBD());
//        // 扣费后利润 = 考核利润（原始值） - 费用
//        BigDecimal profitWithoutExpenseOriginalBD = profitOriginalBD.subtract(expenseBD);
        // 保存数据
        BudgetPlanProfitDetail toInsert = new BudgetPlanProfitDetail();
        BudgetPlanTypeEnum budgetPlanTypeEnum = BudgetPlanTypeEnum.findByName(budgetPlanProfit.getBudgetType());
        if (Objects.nonNull(budgetPlanTypeEnum) && (budgetPlanTypeEnum == BudgetPlanTypeEnum.MONTH || budgetPlanTypeEnum == BudgetPlanTypeEnum.MONTH_ADJUST)) {
            toInsert.setPeriodType(budgetPlanTypeEnum.getProfitPeriodType());
            toInsert.setPeriodValue(LocalDateTimeUtil.format(dataStartDate, DatePattern.NORM_MONTH_PATTERN));
        } else {
            // 默认年
            toInsert.setPeriodType(BudgetPlanTypeEnum.YEAR.getProfitPeriodType());
            toInsert.setPeriodValue(String.valueOf(dataStartDate.getYear()));
        }
        toInsert.setBudgetPlanId(budgetPlanProfit.getBudgetPlanId());
        toInsert.setBudgetPlanProfitId(budgetPlanProfit.getId());
        toInsert.setDataCategory(budgetPlanDataCategoryEnum.name());
        toInsert.setBelongDeptId(contractBaseInfo.getBizDeptId());
        toInsert.setSponsorUserId(contractBaseInfo.getProjSponsorUserId());
        toInsert.setClientId(contractBaseInfo.getClientId());
        toInsert.setClientName(id2NameService.clientId2NameSingle(contractBaseInfo.getClientId()));
        toInsert.setProjReviewId(contractBaseInfo.getProjReviewId());
        toInsert.setContractId(contractBaseInfo.getId());
        toInsert.setContractCode(contractBaseInfo.getContractCode());
        toInsert.setReceiptId(contractReceipt.getId());
        toInsert.setReceiptCode(contractReceipt.getReceiptCode());
        if (Objects.nonNull(projPricingBaseInfo)) {
            toInsert.setFtpIndustryCategory(projPricingBaseInfo.getFtpIndustryCategory());
        } else {
            toInsert.setFtpIndustryCategory(projReviewBaseInfo.getFtpIndustryCategory());
        }
        toInsert.setRiskControlIndustryClassify(projReviewBaseInfo.getRiskControlIndustryClassify());
        toInsert.setLeaseType(contractBaseInfo.getLeaseType());
        toInsert.setTermMonth(contractLeasePrice.getLeaseMonthCount());
        toInsert.setRepayFrequency(contractLeasePrice.getRepayRate());
        toInsert.setProjName(projReviewBaseInfo.getProjName());
        if (actualPay != 0) {
            toInsert.setDepositRate(Util.mithrasIntegerDecimalTwo(BigDecimal.valueOf(deposit).divide(BigDecimal.valueOf(actualPay), 20, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(1000000)).intValue()));
            toInsert.setConsultingFeeRate(Util.mithrasIntegerDecimalTwo(BigDecimal.valueOf(consultingFee).divide(BigDecimal.valueOf(actualPay), 20, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(1000000)).intValue()));
            toInsert.setConsultingFeeRateYear(Util.mithrasIntegerDecimalTwo(BigDecimal.valueOf(toInsert.getConsultingFeeRate()).divide(BigDecimal.valueOf(contractLeasePrice.getLeaseMonthCount()).divide(BigDecimal.valueOf(12), 20, RoundingMode.HALF_UP), 20, RoundingMode.HALF_UP).intValue()));
        }
        toInsert.setContractInterestRate(Optional.ofNullable(contractLeasePrice.getLprPercent()).orElse(0) + Optional.ofNullable(contractLeasePrice.getLprAddPercent()).orElse(0));
        toInsert.setIrr(contractReceipt.getActualIrr());
        toInsert.setXirr(Optional.ofNullable(contractReceipt.getXirr()).map(e -> e * 1000000).orElse(null));
        toInsert.setFtp(costCashFtp);
        toInsert.setActualPay(actualPay);
        toInsert.setPayDate(contractReceipt.getReceiptStartDate());
        if (Objects.nonNull(contractReceipt.getReceiptStartDate())) {
            toInsert.setPayDateYear(contractReceipt.getReceiptStartDate().getYear());
            toInsert.setPayDateMonth(contractReceipt.getReceiptStartDate().getMonthValue());
        }
        toInsert.setEndOfLastPeriodBalance(endOfLastPeriodBalance);
        toInsert.setEndOfThisPeriodBalance(endOfThisPeriodBalance);
        // 平均资金占用
        if (StrUtil.equalsAny(budgetPlanProfit.getBudgetType(), BudgetPlanTypeEnum.MONTH.name(), BudgetPlanTypeEnum.MONTH_ADJUST.name())) {
            toInsert.setFundOccupyAverage(totalCashOccupy);
        } else {
            BigDecimal b = BigDecimal.valueOf(totalCashOccupy).divide(BigDecimal.valueOf(12), 20, RoundingMode.HALF_UP);
            toInsert.setFundOccupyAverage(Util.mithrasLongDecimalTwo(b.longValue()));
        }
        toInsert.setInterestIncome(interestIncome);
        toInsert.setInterestIncomeWithoutTax(Util.mithrasLongDecimalTwo(interestIncomeWithoutTaxBD.longValue()));
        toInsert.setConsultingFeeIncome(consultingFeeIncome);
        toInsert.setConsultingFeeIncomeWithoutTax(Util.mithrasLongDecimalTwo(consultingFeeIncomeWithoutBD.longValue()));
        toInsert.setPenaltyInterestIncome(penaltyInterestIncome);
        toInsert.setPenaltyInterestIncomeWithoutTax(Util.mithrasLongDecimalTwo(penaltyInterestIncomeWithoutBD.longValue()));
        toInsert.setEarlyStopCompensationIncome(earlyStopCompensationIncome);
        toInsert.setEarlyStopCompensationIncomeWithoutTax(Util.mithrasLongDecimalTwo(earlyStopCompensationIncomeWithoutBD.longValue()));
        toInsert.setBeforeInterestIncome(beforeInterestIncome);
        toInsert.setBeforeInterestIncomeWithoutTax(Util.mithrasLongDecimalTwo(beforeInterestIncomeWithoutBD.longValue()));
        toInsert.setNominalPriceIncome(nominalPriceIncome);
        toInsert.setNominalPriceIncomeWithoutTax(Util.mithrasLongDecimalTwo(nominalPriceIncomeWithoutTaxBD.longValue()));
        toInsert.setIncome(Util.mithrasLongDecimalTwo(incomeBD.longValue()));
        toInsert.setIncomeWithoutTax(Util.mithrasLongDecimalTwo(incomeWithoutTaxBD.longValue()));
        toInsert.setCost(cost);
        toInsert.setCostWithoutTax(cost);
//        toInsert.setCostWithoutTax(Util.mithrasLongDecimalTwo(FinancialUtil.calculateAmountWithoutTax(cost, valueAddedTaxRate).longValue()));
        toInsert.setValueAddedTax(Util.mithrasLongDecimalTwo(valueAddedTaxBD.longValue()));
        toInsert.setStampTax(Util.mithrasLongDecimalTwo(stampTaxBD.longValue()));
        toInsert.setAdditionalTax(Util.mithrasLongDecimalTwo(additionalTaxBD.longValue()));
        toInsert.setEndOfLastPeriodRiskFund(Util.mithrasLongDecimalTwo(riskFundHelper.getRiskFundBalanceBeginBD().longValue()));
        toInsert.setEndOfThisPeriodRiskFund(Util.mithrasLongDecimalTwo(riskFundHelper.getRiskFundBalanceEndBD().longValue()));
//        toInsert.setRiskFundDiff(Util.mithrasLongDecimalTwo(riskFundHelper.getRiskFundDiffBD().longValue()));
        // 理想值初始化成一样的，后续Ecl模型结果可覆盖
        toInsert.setEndOfLastPeriodRiskFundIdeal(Util.mithrasLongDecimalTwo(riskFundHelper.getRiskFundBalanceBeginBD().longValue()));
        toInsert.setEndOfThisPeriodRiskFundIdeal(Util.mithrasLongDecimalTwo(riskFundHelper.getRiskFundBalanceEndBD().longValue()));
        toInsert.setGrossProfit(Util.mithrasLongDecimalTwo(grassProfitBD.longValue()));
//        toInsert.setExpense(Util.mithrasLongDecimalTwo(expenseBD.longValue()));
//        // 存量考核利润，如果为负数，需要置为0
//        long profit = profitBD.longValue();
//        long profitWithoutExpense = profitWithoutExpenseBD.longValue();
//        if (budgetPlanDataCategoryEnum == BudgetPlanDataCategoryEnum.HISTORY && profitOriginalBD.longValue() < 0) {
//            profit = 0L;
//        }
//        if (budgetPlanDataCategoryEnum == BudgetPlanDataCategoryEnum.HISTORY && profitWithoutExpenseOriginalBD.longValue() < 0) {
//            profitWithoutExpense = 0L;
//        }
//        toInsert.setAssessmentProfit(profit);
//        toInsert.setAssessmentProfitOriginal(Util.mithrasLongDecimalTwo(profitOriginalBD.longValue()));
//        toInsert.setAssessmentProfitWithoutExpense(profitWithoutExpense);
//        toInsert.setAssessmentProfitWithoutExpenseOriginal(Util.mithrasLongDecimalTwo(profitWithoutExpenseOriginalBD.longValue()));
        toInsert.setAssetClassifyResult(assetClassifyResult);
        toInsert.calculate(expenseRate);
        // 根据部门分润占比尝试进行拆分
        return this.trySplitByDept(toInsert);
    }

    private List<BudgetPlanProfitDetail> trySplitByDept(BudgetPlanProfitDetail origin) {
        KpiProjectDistribution kpiProjectDistribution = SpringUtil.getBean(KpiProjectDistributionService.class).getOneByContractId(origin.getContractId());
        if (Objects.isNull(kpiProjectDistribution)) {
            return Collections.singletonList(origin);
        }
        // 查询最新生效的部分分润占比
        CommonVersion commonVersion = SpringUtil.getBean(KpiProjectDistributionLibVersionService.class).findNewestVersion(kpiProjectDistribution.getMainId());
        if (Objects.isNull(commonVersion)) {
            return Collections.singletonList(origin);
        }
        // 查询部门分润占比
        List<KpiProjectDistributionDeptWeightLib> deptWeightLibList = SpringUtil.getBean(KpiProjectDistributionDeptWeightLibService.class).list(
                Wrappers.<KpiProjectDistributionDeptWeightLib>lambdaQuery()
                        .eq(KpiProjectDistributionDeptWeight::getProjectDistributionId, kpiProjectDistribution.getId())
                        .eq(KpiProjectDistributionDeptWeightLib::getVersion, commonVersion.getVersion())
        );
        if (CollectionUtil.isEmpty(deptWeightLibList)) {
            return Collections.singletonList(origin);
        }
        // 如果只有一条数据就更新一下部门id即可（分润比一定是100%，页面录入的时候保证了该逻辑）
        if (deptWeightLibList.size() == 1) {
            origin.setBelongDeptId(deptWeightLibList.get(0).getWeightTarget());
            return Collections.singletonList(origin);
        }
        // 按照部门分润比进行拆分
        List<BudgetPlanProfitDetail> result = new LinkedList<>();
        for (KpiProjectDistributionDeptWeightLib deptWeightLib : deptWeightLibList) {
            if (Objects.isNull(deptWeightLib.getWeightTarget())) {
                continue;
            }
            if (Objects.isNull(deptWeightLib.getWeightValue())) {
                continue;
            }
            BudgetPlanProfitDetail splitProfitDetail = BeanUtil.copyProperties(origin, BudgetPlanProfitDetail.class);
            splitProfitDetail.setBelongDeptId(deptWeightLib.getWeightTarget());
            splitProfitDetail.setActualPay(this.calculateSplitAmount(origin.getActualPay(), deptWeightLib.getWeightValue()));
            splitProfitDetail.setEndOfLastPeriodBalance(this.calculateSplitAmount(origin.getEndOfLastPeriodBalance(), deptWeightLib.getWeightValue()));
            splitProfitDetail.setEndOfThisPeriodBalance(this.calculateSplitAmount(origin.getEndOfThisPeriodBalance(), deptWeightLib.getWeightValue()));
            splitProfitDetail.setFundOccupyAverage(this.calculateSplitAmount(origin.getFundOccupyAverage(), deptWeightLib.getWeightValue()));
            splitProfitDetail.setInterestIncome(this.calculateSplitAmount(origin.getInterestIncome(), deptWeightLib.getWeightValue()));
            splitProfitDetail.setConsultingFeeIncome(this.calculateSplitAmount(origin.getConsultingFeeIncome(), deptWeightLib.getWeightValue()));
            splitProfitDetail.setIncome(this.calculateSplitAmount(origin.getIncome(), deptWeightLib.getWeightValue()));
            splitProfitDetail.setIncomeWithoutTax(this.calculateSplitAmount(origin.getIncomeWithoutTax(), deptWeightLib.getWeightValue()));
            splitProfitDetail.setCost(this.calculateSplitAmount(origin.getCost(), deptWeightLib.getWeightValue()));
            splitProfitDetail.setCostWithoutTax(this.calculateSplitAmount(origin.getCostWithoutTax(), deptWeightLib.getWeightValue()));
            splitProfitDetail.setValueAddedTax(this.calculateSplitAmount(origin.getValueAddedTax(), deptWeightLib.getWeightValue()));
            splitProfitDetail.setStampTax(this.calculateSplitAmount(origin.getStampTax(), deptWeightLib.getWeightValue()));
            splitProfitDetail.setAdditionalTax(this.calculateSplitAmount(origin.getAdditionalTax(), deptWeightLib.getWeightValue()));
            splitProfitDetail.setEndOfLastPeriodRiskFund(this.calculateSplitAmount(origin.getEndOfLastPeriodRiskFund(), deptWeightLib.getWeightValue()));
            splitProfitDetail.setEndOfThisPeriodRiskFund(this.calculateSplitAmount(origin.getEndOfThisPeriodRiskFund(), deptWeightLib.getWeightValue()));
            splitProfitDetail.setRiskFundDiff(this.calculateSplitAmount(origin.getRiskFundDiff(), deptWeightLib.getWeightValue()));
            splitProfitDetail.setGrossProfit(this.calculateSplitAmount(origin.getGrossProfit(), deptWeightLib.getWeightValue()));
            splitProfitDetail.setExpense(this.calculateSplitAmount(origin.getExpense(), deptWeightLib.getWeightValue()));
            splitProfitDetail.setAssessmentProfit(this.calculateSplitAmount(origin.getAssessmentProfit(), deptWeightLib.getWeightValue()));
            splitProfitDetail.setAssessmentProfitOriginal(this.calculateSplitAmount(origin.getAssessmentProfitOriginal(), deptWeightLib.getWeightValue()));
            splitProfitDetail.setAssessmentProfitWithoutExpense(this.calculateSplitAmount(origin.getAssessmentProfitWithoutExpense(), deptWeightLib.getWeightValue()));
            splitProfitDetail.setAssessmentProfitWithoutExpenseOriginal(this.calculateSplitAmount(origin.getAssessmentProfitWithoutExpenseOriginal(), deptWeightLib.getWeightValue()));
            splitProfitDetail.setEndOfLastPeriodRiskFundIdeal(this.calculateSplitAmount(origin.getEndOfLastPeriodRiskFundIdeal(), deptWeightLib.getWeightValue()));
            splitProfitDetail.setEndOfThisPeriodRiskFundIdeal(this.calculateSplitAmount(origin.getEndOfThisPeriodRiskFundIdeal(), deptWeightLib.getWeightValue()));
            splitProfitDetail.setRiskFundDiffIdeal(this.calculateSplitAmount(origin.getRiskFundDiffIdeal(), deptWeightLib.getWeightValue()));
            splitProfitDetail.setExpenseIdeal(this.calculateSplitAmount(origin.getExpenseIdeal(), deptWeightLib.getWeightValue()));
            splitProfitDetail.setAssessmentProfitIdeal(this.calculateSplitAmount(origin.getAssessmentProfitIdeal(), deptWeightLib.getWeightValue()));
            splitProfitDetail.setAssessmentProfitOriginalIdeal(this.calculateSplitAmount(origin.getAssessmentProfitOriginalIdeal(), deptWeightLib.getWeightValue()));
            splitProfitDetail.setAssessmentProfitWithoutExpenseIdeal(this.calculateSplitAmount(origin.getAssessmentProfitWithoutExpenseIdeal(), deptWeightLib.getWeightValue()));
            splitProfitDetail.setAssessmentProfitWithoutExpenseOriginalIdeal(this.calculateSplitAmount(origin.getAssessmentProfitWithoutExpenseOriginalIdeal(), deptWeightLib.getWeightValue()));
            result.add(splitProfitDetail);
        }
        return result;
    }

    private BigDecimal ensureDeptPercent(Long contractId, Long deptId) {
        KpiProjectDistribution kpiProjectDistribution = SpringUtil.getBean(KpiProjectDistributionService.class).getOneByContractId(contractId);
        if (Objects.isNull(kpiProjectDistribution)) {
            // 没有找到分配信息的默认给100%
            return BigDecimal.ONE;
        }
        // 查询最新生效的部分分润占比
        CommonVersion commonVersion = SpringUtil.getBean(KpiProjectDistributionLibVersionService.class).findNewestVersion(kpiProjectDistribution.getMainId());
        if (Objects.isNull(commonVersion)) {
            return BigDecimal.ONE;
        }
        // 查询部门分润占比
        List<KpiProjectDistributionDeptWeightLib> deptWeightLibList = SpringUtil.getBean(KpiProjectDistributionDeptWeightLibService.class).list(
                Wrappers.<KpiProjectDistributionDeptWeightLib>lambdaQuery()
                        .eq(KpiProjectDistributionDeptWeight::getProjectDistributionId, kpiProjectDistribution.getId())
                        .eq(KpiProjectDistributionDeptWeight::getWeightTarget, deptId)
                        .eq(KpiProjectDistributionDeptWeightLib::getVersion, commonVersion.getVersion())
        );
        if (CollectionUtil.isEmpty(deptWeightLibList)) {
            return BigDecimal.ONE;
        }
        int sum = deptWeightLibList.stream().filter(e -> Objects.nonNull(e.getWeightValue())).mapToInt(KpiProjectDistributionDeptWeight::getWeightValue).sum();
        return BigDecimal.valueOf(sum).divide(BigDecimal.valueOf(1000000), 20, RoundingMode.HALF_UP);
    }

    private Long calculateSplitAmount(Long amount, Integer percent) {
        if (Objects.isNull(amount)) {
            return null;
        }
        BigDecimal b = BigDecimal.valueOf(amount).multiply(BigDecimal.valueOf(percent).divide(BigDecimal.valueOf(1000000), 20, RoundingMode.HALF_UP));
        return Util.mithrasLongDecimalTwo(b.longValue());
    }

    private BudgetPlanProfitDetail calculateFromMonth(BudgetPlanProfit budgetPlanProfit, BudgetPlanPayDetail budgetPlanPayDetail) {
        BudgetPlanProfitDetail profitDetail = new BudgetPlanProfitDetail();
        profitDetail.setBudgetPlanPayDetailId(budgetPlanPayDetail.getId());
        profitDetail.setActualPay(budgetPlanPayDetail.getPlanPayAmount());
        profitDetail.setPayDate(budgetPlanPayDetail.getPlanPayDate());
        profitDetail.setPayDateYear(budgetPlanPayDetail.getPlanPayDate().getYear());
        // 上月末业务余额 = 0
        // 本月末业务余额 = 月平均资金占用额 = 拟投放额
        long endOfLastMonthBalance = 0L;
        long endOfThisMonthBalance = budgetPlanPayDetail.getPlanPayAmount();
        long cashOccupyAverage = budgetPlanPayDetail.getPlanPayAmount();
        // 营业收入 = 拟投放金额*（月末-投放日+1）/360*合同利率计算收入+当期咨询费
        long days = 0L;
        if (Objects.nonNull(budgetPlanPayDetail.getPlanPayDate())) {
            days = LocalDateTimeUtil.between(budgetPlanPayDetail.getPlanPayDate().atStartOfDay(), budgetPlanProfit.getBudgetDateTo().atStartOfDay(), ChronoUnit.DAYS) + 1;
        }
        BigDecimal interestIncomeBD = BigDecimal.valueOf(budgetPlanPayDetail.getPlanPayAmount())
                .multiply(BigDecimal.valueOf(days).divide(BigDecimal.valueOf(360), 20, RoundingMode.HALF_UP))
                .multiply(BigDecimal.valueOf(budgetPlanPayDetail.getContractInterestRate()).divide(BigDecimal.valueOf(1000000), 20,RoundingMode.HALF_UP));
        BigDecimal consultingFeeIncomeBD = BigDecimal.valueOf(Optional.ofNullable(budgetPlanPayDetail.getConsultingFee()).orElse(0L));
        BigDecimal incomeBD = interestIncomeBD.add(consultingFeeIncomeBD);
        // 不含税收入
        BigDecimal consultingFeeTaxRate = FinancialUtil.ensureConsultingTaxRate();
        BigDecimal valueAddedTaxRate = FinancialUtil.ensureValueAddedTaxRate(budgetPlanPayDetail.getLeaseType());
        BigDecimal consultingFeeIncomeWithoutTaxBD = FinancialUtil.calculateAmountWithoutTax(consultingFeeIncomeBD, consultingFeeTaxRate);
        BigDecimal interestIncomeWithoutTaxBD = FinancialUtil.calculateAmountWithoutTax(interestIncomeBD, valueAddedTaxRate);
//        BigDecimal incomeWithoutTaxBD = FinancialUtil.calculateAmountWithoutTax(incomeBD.longValue(), valueAddedTaxRate);
        BigDecimal incomeWithoutTaxBD = interestIncomeWithoutTaxBD.add(consultingFeeIncomeWithoutTaxBD);
        // 营业成本 = 拟投放金额*FTP价格*占用时间
        Integer ftp = budgetPlanPayDetail.getFtp();
        BigDecimal costBD;
        if (Objects.nonNull(ftp)) {
            costBD = BigDecimal.valueOf(budgetPlanPayDetail.getPlanPayAmount()).multiply(BigDecimal.valueOf(ftp).divide(BigDecimal.valueOf(1000000 * 360), 20, RoundingMode.HALF_UP)).multiply(BigDecimal.valueOf(days));
        } else {
            costBD = BigDecimal.ZERO;
        }
        // 增值税 = （不含税收入 - 营业成本） * 税率
        BigDecimal valueAddedTaxBD = incomeWithoutTaxBD.subtract(costBD).multiply(valueAddedTaxRate);
        if (valueAddedTaxBD.longValue() < 0) {
            valueAddedTaxBD = BigDecimal.ZERO;
        }
        // 附加税 = 增值税 * 12%
        BigDecimal additionalTaxBD = valueAddedTaxBD.multiply(BigDecimal.valueOf(0.12));
        // 印花税
        BigDecimal consultingFeeTaxRateBD = FinancialUtil.ensureConsultingTaxRate();
        BigDecimal stampTaxBD = BigDecimal.ZERO;
        long totalPrincipal = budgetPlanPayDetail.getPlanPayAmount();
        long totalInterest = BigDecimal.valueOf(totalPrincipal).multiply(BigDecimal.valueOf(budgetPlanPayDetail.getContractInterestRate())).divide(BigDecimal.valueOf(1000000), 20, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(budgetPlanPayDetail.getTermMonth()).divide(BigDecimal.valueOf(12), 20, RoundingMode.HALF_UP)).longValue();
        if (Objects.equals(budgetPlanPayDetail.getLeaseType(), LeaseType.hui_zu.name())) {
            // 回租 =（本金+含税利息/（1+税率)+含税咨询费/(1+税率)）*5/100000
            stampTaxBD = BigDecimal.valueOf(totalPrincipal)
                    .add(BigDecimal.valueOf(totalInterest).divide(BigDecimal.ONE.add(valueAddedTaxRate), 20, RoundingMode.HALF_UP))
                    .add(BigDecimal.valueOf(Optional.ofNullable(budgetPlanPayDetail.getConsultingFee()).orElse(0L)).divide(BigDecimal.ONE.add(consultingFeeTaxRateBD), 20, RoundingMode.HALF_UP))
                    .multiply(BigDecimal.valueOf(0.00005));
        }
        if (Objects.equals(budgetPlanPayDetail.getLeaseType(), LeaseType.zhi_zu.name())) {
            // 直租 =（含税租金总额/(1+税率)+含税咨询费/(1+税率)）*5/100000+投放金额*3/10000
            long totalRent = totalPrincipal + totalInterest;
            stampTaxBD = BigDecimal.valueOf(totalRent).divide(BigDecimal.ONE.add(valueAddedTaxRate), 20, RoundingMode.HALF_UP)
                    .add(BigDecimal.valueOf(Optional.ofNullable(budgetPlanPayDetail.getConsultingFee()).orElse(0L)).divide(BigDecimal.ONE.add(consultingFeeTaxRateBD), 20, RoundingMode.HALF_UP))
                    .multiply(BigDecimal.valueOf(0.00005))
                    .add(BigDecimal.valueOf(budgetPlanPayDetail.getPlanPayAmount()).multiply(BigDecimal.valueOf(0.0003)));
        }
        if (Objects.equals(budgetPlanPayDetail.getLeaseType(), LeaseType.jyx_zu.name())) {
            // 经营性租赁 =（含税租金总额/(1+税率)+含税咨询费/(1+税率)）*1/1000
            long totalRent = totalPrincipal + totalInterest;
            stampTaxBD = BigDecimal.valueOf(totalRent).divide(BigDecimal.ONE.add(valueAddedTaxRate), 20, RoundingMode.HALF_UP)
                    .add(BigDecimal.valueOf(Optional.ofNullable(budgetPlanPayDetail.getConsultingFee()).orElse(0L)).divide(BigDecimal.ONE.add(consultingFeeTaxRateBD), 20, RoundingMode.HALF_UP))
                    .multiply(BigDecimal.valueOf(0.0001));
        }
        // 毛利 = 营业收入（不含税） - 营业成本（含税） - 附加税 - 印花税
        BigDecimal grassProfitBD = incomeWithoutTaxBD.subtract(costBD).subtract(additionalTaxBD).subtract(stampTaxBD);
        // 拨备 = 预算区间末（剩余本金-剩余保证金）*预算区间末风险准备金计提比例-预算区间初（剩余本金-剩余保证金）*预算区间初风险准备金计提比例（风险准备金计提比例详见参数配置表）
        BigDecimal depositBalanceBegin = BigDecimal.ZERO;
        if (Objects.nonNull(budgetPlanPayDetail.getDeposit())) {
            depositBalanceBegin = BigDecimal.valueOf(budgetPlanPayDetail.getPlanPayAmount());
        }
        BigDecimal riskFundBalanceBegin;
        BigDecimal riskFundBalanceEnd;
        BigDecimal riskFundDiffBD;
        // 从参数配置中获取拨备计提比例
        FtpIndustryCategoryEnum ftpIndustryCategoryEnum = FtpIndustryCategoryEnum.getByName(budgetPlanPayDetail.getFtpIndustryCategory());
        RelatedTermRange relatedTermRange = RelatedTermRange.convertFromMonthCount(budgetPlanPayDetail.getTermMonth());
        Integer riskFundRate = SpringUtil.getBean(BudgetParameterConfigService.class).getRiskReserve(ftpIndustryCategoryEnum, relatedTermRange);
        riskFundBalanceBegin = BigDecimal.valueOf(budgetPlanPayDetail.getPlanPayAmount()).subtract(depositBalanceBegin).multiply(BigDecimal.valueOf(riskFundRate).divide(BigDecimal.valueOf(1000000), 20, RoundingMode.HALF_UP));
        if (riskFundBalanceBegin.longValue() < 0) {
            riskFundBalanceBegin = BigDecimal.ZERO;
        }
        riskFundBalanceEnd = riskFundBalanceBegin;
        riskFundDiffBD = riskFundBalanceEnd.subtract(riskFundBalanceBegin);
//        // 考核利润 = 毛利-拨备+项目利润调整项
//        // 重要！！！此处默认是初始化动作，一定不存在项目利润调整项，如果需要计算项目利润调整项，上层需自己处理
//        BigDecimal profitBD = grassProfitBD.subtract(riskFundDiffBD);
//        // 扣费后利润
        Integer expenseRate = SpringUtil.getBean(BudgetParameterConfigService.class).getExpenseRatioByDeptId(budgetPlanPayDetail.getBelongDeptId());
//        BigDecimal profitWithoutExpenseBD = FinancialUtil.calculateProfitWithoutExpense(profitBD, expenseRate);
//        // 费用 = 考核利润 - 扣费后利润
//        BigDecimal expenseBD = profitBD.subtract(profitWithoutExpenseBD);
//        // 考核利润（原始值） = 毛利 - 拨备
//        BigDecimal profitOriginalBD = grassProfitBD.subtract(riskFundDiffBD);
//        // 扣费后利润（原始值） = 考核利润（原始值） - 费用
//        BigDecimal profitWithoutExpenseOriginalBD = profitOriginalBD.subtract(expenseBD);
        // 保存数据
        profitDetail.setPeriodType(BudgetPlanTypeEnum.MONTH.getProfitPeriodType());
        profitDetail.setPeriodValue(LocalDateTimeUtil.format(budgetPlanProfit.getBudgetDateFrom(), DatePattern.NORM_MONTH_PATTERN));
        profitDetail.setBudgetPlanId(budgetPlanProfit.getBudgetPlanId());
        profitDetail.setBudgetPlanProfitId(budgetPlanProfit.getId());
        BudgetPlanDataCategoryEnum budgetPlanDataCategoryEnum = this.ensureDataCategory(budgetPlanPayDetail.getPlanPayDate(), budgetPlanProfit.getBudgetDateFrom(), budgetPlanProfit.getBudgetDateTo());
        profitDetail.setDataCategory(budgetPlanDataCategoryEnum.name());
        profitDetail.setBelongDeptId(budgetPlanPayDetail.getBelongDeptId());
        profitDetail.setSponsorUserId(budgetPlanPayDetail.getSponsorUserId());
        profitDetail.setClientId(budgetPlanPayDetail.getClientId());
        Client client = SpringUtil.getBean(ClientService.class).getById(budgetPlanPayDetail.getClientId());
        if (Objects.nonNull(client)) {
            profitDetail.setClientName(client.getClientName());
        }
        profitDetail.setFtpIndustryCategory(budgetPlanPayDetail.getFtpIndustryCategory());
        profitDetail.setRiskControlIndustryClassify(budgetPlanPayDetail.getRiskControlIndustryClassify());
        profitDetail.setProjReviewId(budgetPlanPayDetail.getProjReviewId());
        profitDetail.setLeaseType(budgetPlanPayDetail.getLeaseType());
        profitDetail.setTermMonth(budgetPlanPayDetail.getTermMonth());
        ProjReviewLeasePrice projReviewLeasePrice = SpringUtil.getBean(ProjReviewLeasePriceService.class).getByProjectId(budgetPlanPayDetail.getProjReviewId());
        if (Objects.nonNull(projReviewLeasePrice)) {
            profitDetail.setRepayFrequency(projReviewLeasePrice.getRepayRate());
        }
        BigDecimal depositRate = BigDecimal.valueOf(Optional.ofNullable(budgetPlanPayDetail.getDeposit()).orElse(0L)).divide(BigDecimal.valueOf(budgetPlanPayDetail.getPlanPayAmount()), 20, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(1000000));
        profitDetail.setDepositRate(Util.mithrasIntegerDecimalTwo(depositRate.intValue()));
        BigDecimal consultingFeeRate = BigDecimal.valueOf(Optional.ofNullable(budgetPlanPayDetail.getConsultingFee()).orElse(0L)).divide(BigDecimal.valueOf(budgetPlanPayDetail.getPlanPayAmount()), 20, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(1000000));
        profitDetail.setConsultingFeeRate(Util.mithrasIntegerDecimalTwo(consultingFeeRate.intValue()));
        profitDetail.setConsultingFeeRateYear(Util.mithrasIntegerDecimalTwo(BigDecimal.valueOf(profitDetail.getConsultingFeeRate()).divide(BigDecimal.valueOf(budgetPlanPayDetail.getTermMonth()).divide(BigDecimal.valueOf(12), 20, RoundingMode.HALF_UP), 20, RoundingMode.HALF_UP).intValue()));
        profitDetail.setContractInterestRate(budgetPlanPayDetail.getContractInterestRate());
        profitDetail.setIrr(budgetPlanPayDetail.getIrr());
        profitDetail.setXirr(budgetPlanPayDetail.getXirr());
        profitDetail.setFtp(budgetPlanPayDetail.getFtp());
        profitDetail.setActualPay(budgetPlanPayDetail.getPlanPayAmount());
        profitDetail.setPayDate(budgetPlanPayDetail.getPlanPayDate());
        if (Objects.nonNull(budgetPlanPayDetail.getPlanPayDate())) {
            profitDetail.setPayDateYear(budgetPlanPayDetail.getPlanPayDate().getYear());
            profitDetail.setPayDateMonth(budgetPlanPayDetail.getPlanPayDate().getMonthValue());
        }
        profitDetail.setEndOfLastPeriodBalance(endOfLastMonthBalance);
        profitDetail.setEndOfThisPeriodBalance(endOfThisMonthBalance);
        profitDetail.setFundOccupyAverage(Util.mithrasLongDecimalTwo(cashOccupyAverage));
        profitDetail.setInterestIncome(Util.mithrasLongDecimalTwo(interestIncomeBD.longValue()));
        profitDetail.setInterestIncomeWithoutTax(Util.mithrasLongDecimalTwo(interestIncomeWithoutTaxBD.longValue()));
        profitDetail.setConsultingFeeIncome(Util.mithrasLongDecimalTwo(consultingFeeIncomeBD.longValue()));
        profitDetail.setConsultingFeeIncomeWithoutTax(Util.mithrasLongDecimalTwo(consultingFeeIncomeWithoutTaxBD.longValue()));
        profitDetail.setIncome(Util.mithrasLongDecimalTwo(incomeBD.longValue()));
        profitDetail.setIncomeWithoutTax(Util.mithrasLongDecimalTwo(incomeWithoutTaxBD.longValue()));
        profitDetail.setCost(Util.mithrasLongDecimalTwo(costBD.longValue()));
        profitDetail.setCostWithoutTax(Util.mithrasLongDecimalTwo(costBD.longValue()));
//        profitDetail.setCostWithoutTax(Util.mithrasLongDecimalTwo(FinancialUtil.calculateAmountWithoutTax(profitDetail.getCost(), valueAddedTaxRate).longValue()));
        profitDetail.setValueAddedTax(Util.mithrasLongDecimalTwo(valueAddedTaxBD.longValue()));
        profitDetail.setStampTax(Util.mithrasLongDecimalTwo(stampTaxBD.longValue()));
        profitDetail.setAdditionalTax(Util.mithrasLongDecimalTwo(additionalTaxBD.longValue()));
        profitDetail.setEndOfLastPeriodRiskFund(Util.mithrasLongDecimalTwo(riskFundBalanceBegin.longValue()));
        profitDetail.setEndOfLastPeriodRiskFundIdeal(Util.mithrasLongDecimalTwo(riskFundBalanceBegin.longValue()));
        profitDetail.setEndOfThisPeriodRiskFund(Util.mithrasLongDecimalTwo(riskFundBalanceEnd.longValue()));
        profitDetail.setEndOfThisPeriodRiskFundIdeal(Util.mithrasLongDecimalTwo(riskFundBalanceEnd.longValue()));
        profitDetail.setRiskFundDiff(Util.mithrasLongDecimalTwo(riskFundDiffBD.longValue()));
        profitDetail.setRiskFundDiffIdeal(Util.mithrasLongDecimalTwo(riskFundDiffBD.longValue()));
        profitDetail.setGrossProfit(Util.mithrasLongDecimalTwo(grassProfitBD.longValue()));
//        profitDetail.setExpense(Util.mithrasLongDecimalTwo(expenseBD.longValue()));
//        // 存量考核利润，如果为负数，需要置为0
//        long profit = profitBD.longValue();
//        long profitWithoutExpense = profitWithoutExpenseBD.longValue();
//        if (budgetPlanDataCategoryEnum == BudgetPlanDataCategoryEnum.HISTORY && profitOriginalBD.longValue() < 0) {
//            profit = 0L;
//        }
//        if (budgetPlanDataCategoryEnum == BudgetPlanDataCategoryEnum.HISTORY && profitWithoutExpenseOriginalBD.longValue() < 0) {
//            profitWithoutExpense = 0L;
//        }
//        profitDetail.setAssessmentProfit(profit);
//        profitDetail.setAssessmentProfitOriginal(Util.mithrasLongDecimalTwo(profitOriginalBD.longValue()));
//        profitDetail.setAssessmentProfitWithoutExpense(profitWithoutExpense);
//        profitDetail.setAssessmentProfitWithoutExpenseOriginal(Util.mithrasLongDecimalTwo(profitWithoutExpenseOriginalBD.longValue()));
        profitDetail.calculate(expenseRate);
        return profitDetail;
    }

    private List<BudgetPlanProfitDetail> calculateFromOther(BudgetPlanProfit budgetPlanProfit, BudgetPlanPayDetail budgetPlanPayDetail) {
        List<BudgetPlanProfitDetail> result = new LinkedList<>();
        // 复用年度/半年度的逻辑，特殊之处在于需要根据年份进行拆解
        int year = budgetPlanProfit.getBudgetDateFrom().getYear();
        while (year <= budgetPlanProfit.getBudgetDateTo().getYear()) {
            LocalDate dataStartDate;
            LocalDate dataEndDate;
            if (year == budgetPlanProfit.getBudgetDateFrom().getYear()) {
                dataStartDate = budgetPlanProfit.getBudgetDateFrom();
            } else {
                dataStartDate = LocalDate.of(year, 1, 1);
            }
            if (year == budgetPlanProfit.getBudgetDateTo().getYear()) {
                dataEndDate = budgetPlanProfit.getBudgetDateTo();
            } else {
                dataEndDate = LocalDate.of(year, 12, 31);
            }
            result.add(this.calculateFromNotMonth(budgetPlanProfit, budgetPlanPayDetail, dataStartDate, dataEndDate));
            year++;
        }
        return result;
    }

    private BudgetPlanProfitDetail calculateFromNotMonth(BudgetPlanProfit budgetPlanProfit, BudgetPlanPayDetail budgetPlanPayDetail, LocalDate dataStartDate, LocalDate dataEndDate) {
        BudgetPlanProfitDetail profitDetail = new BudgetPlanProfitDetail();
        profitDetail.setBudgetPlanPayDetailId(budgetPlanPayDetail.getId());
        // 查询报价方案
        BudgetPlanPayDetailPrice budgetPlanPayDetailPrice = SpringUtil.getBean(BudgetPlanPayDetailPriceService.class).getOneByDetailId(budgetPlanPayDetail.getId());
        if (Objects.isNull(budgetPlanPayDetailPrice)) {
            log.error("没有找到投放计划对应的报价方案，计算利润失败[budgetPlanProfit:{}, budgetPlanPayDetail:{}]", JSONUtil.toJsonStr(budgetPlanProfit), JSONUtil.toJsonStr(budgetPlanPayDetail));
            throw new MithrasException("没有找到投放计划对应的报价方案");
        }
        profitDetail.setActualPay(budgetPlanPayDetailPrice.getProjectAmount());
        profitDetail.setPayDate(budgetPlanPayDetailPrice.getPayDate());
        profitDetail.setPayDateYear(budgetPlanPayDetailPrice.getPayDate().getYear());
        // 上年末业务余额
        long endOfLastYearBalance = SpringUtil.getBean(BudgetPlanPayDetailCashFlowMapper.class).calculatePrincipalBalance(Collections.singletonList(budgetPlanPayDetail.getId()), LocalDate.of(dataStartDate.getYear(), 1, 1), budgetPlanPayDetailPrice.getPayDate());
        // 本年末业务余额
        long endOfThisYearBalance = SpringUtil.getBean(BudgetPlanPayDetailCashFlowMapper.class).calculatePrincipalBalance(Collections.singletonList(budgetPlanPayDetail.getId()), LocalDate.of(dataStartDate.getYear(), 12, 31), budgetPlanPayDetailPrice.getPayDate());
        // 全年平均资金占用额
        long totalCashOccupy = SpringUtil.getBean(BudgetPlanPayDetailFtpInterestMapper.class).calculateCashOccupy(
                budgetPlanPayDetail.getId(),
                LocalDate.of(dataStartDate.getYear(), 1, 1),
                LocalDate.of(dataStartDate.getYear(), 12, 31)
        );
        BigDecimal costAverageBD = BigDecimal.valueOf(totalCashOccupy).divide(BigDecimal.valueOf(12), 20, RoundingMode.HALF_UP);
        // 营业收入
        long interestIncome = SpringUtil.getBean(BudgetPlanPayDetailIncomeSharingMapper.class).calculateIncomeByDetailId(budgetPlanPayDetail.getId(), dataStartDate, dataEndDate);
        long consultingFeeIncome = 0L;
        if (!budgetPlanPayDetailPrice.getPayDate().isBefore(dataStartDate) && !budgetPlanPayDetailPrice.getPayDate().isAfter(dataEndDate) && Objects.nonNull(budgetPlanPayDetailPrice.getConsultingFeeRate())) {
            // 需要计算咨询服务费
            BigDecimal b = BigDecimal.valueOf(budgetPlanPayDetailPrice.getProjectAmount()).multiply(BigDecimal.valueOf(budgetPlanPayDetailPrice.getConsultingFeeRate()).divide(BigDecimal.valueOf(1000000), 20, RoundingMode.HALF_UP));
            consultingFeeIncome = Util.mithrasLongDecimalTwo(b.longValue());
        }
        long nominalPriceIncome = 0L;
        if (Objects.nonNull(budgetPlanPayDetailPrice.getNominalPrice())) {
            // 判断预算区间内是否会结清，会的话需要计算名义价款
            List<BudgetPlanPayDetailCashFlow> cashFlowList = SpringUtil.getBean(BudgetPlanPayDetailCashFlowService.class).listByDetailId(budgetPlanPayDetail.getId());
            if (CollectionUtil.isNotEmpty(cashFlowList)) {
                cashFlowList.sort(Comparator.comparing(BudgetPlanPayDetailCashFlow::getCashFlowDate).reversed());
                BudgetPlanPayDetailCashFlow lastOne = cashFlowList.get(0);
                if (!lastOne.getCashFlowDate().isBefore(dataStartDate) && !lastOne.getCashFlowDate().isAfter(dataEndDate)) {
                    nominalPriceIncome = budgetPlanPayDetailPrice.getNominalPrice();
                }
            }
        }
        BigDecimal incomeBD = BigDecimal.valueOf(interestIncome + consultingFeeIncome + nominalPriceIncome);
        // 不含税营业收入
        BigDecimal consultingFeeTaxRate = FinancialUtil.ensureConsultingTaxRate();
        BigDecimal valueAddedTaxRate = FinancialUtil.ensureValueAddedTaxRate(budgetPlanPayDetail.getLeaseType());
        BigDecimal interestIncomeWithoutTaxBD = FinancialUtil.calculateAmountWithoutTax(interestIncome, valueAddedTaxRate);
        BigDecimal consultingFeeIncomeWithoutTaxBD = FinancialUtil.calculateAmountWithoutTax(consultingFeeIncome, consultingFeeTaxRate);
        BigDecimal nominalPriceIncomeWithoutTaxBD = FinancialUtil.calculateAmountWithoutTax(nominalPriceIncome, valueAddedTaxRate);
//        BigDecimal incomeWithoutTaxBD = FinancialUtil.calculateAmountWithoutTax(incomeBD.longValue(), valueAddedTaxRate);
        BigDecimal incomeWithoutTaxBD = interestIncomeWithoutTaxBD.add(consultingFeeIncomeWithoutTaxBD).add(nominalPriceIncomeWithoutTaxBD);
        // 营业成本
        long cost = SpringUtil.getBean(BudgetPlanPayDetailFtpInterestMapper.class).calculateCostByDetailId(budgetPlanPayDetail.getId(), dataStartDate, dataEndDate);
        BigDecimal costBD = BigDecimal.valueOf(cost);
        // 增值税 = (不含税收入 - 营业成本) * 税率
        BigDecimal valueAddedTaxBD = incomeWithoutTaxBD.subtract(costBD).multiply(valueAddedTaxRate);
        if (valueAddedTaxBD.longValue() < 0) {
            valueAddedTaxBD = BigDecimal.ZERO;
        }
        // 附加税 = 增值税 * 12%
        BigDecimal additionalTaxBD = valueAddedTaxBD.multiply(BigDecimal.valueOf(0.12));
        // 印花税 = 直接取算好的
        long stamp = SpringUtil.getBean(BudgetPlanPayDetailExpenseMapper.class).calculateStamp(budgetPlanPayDetail.getId());
        BigDecimal stampTaxBD = BigDecimal.valueOf(stamp);
        // 毛利 = 营业收入（不含税） - 营业成本（含税） - 附加税 - 印花税
        BigDecimal grassProfitBD = incomeWithoutTaxBD.subtract(costBD).subtract(additionalTaxBD).subtract(stampTaxBD);
        // 拨备 = 预算区间末（剩余本金-剩余保证金）*预算区间末风险准备金计提比例-预算区间初（剩余本金-剩余保证金）*预算区间初风险准备金计提比例（风险准备金计提比例详见参数配置表）
        BigDecimal depositBalanceBegin = BigDecimal.ZERO;
        if (Objects.nonNull(budgetPlanPayDetailPrice.getProjectAmount()) && Objects.nonNull(budgetPlanPayDetailPrice.getDepositRate()) && budgetPlanPayDetailPrice.getDepositRate() > 0 && !dataStartDate.isBefore(budgetPlanPayDetailPrice.getPayDate()) && !budgetPlanPayDetailPrice.getPayDate().isAfter(dataStartDate)) {
            depositBalanceBegin = BigDecimal.valueOf(budgetPlanPayDetailPrice.getProjectAmount()).multiply(BigDecimal.valueOf(budgetPlanPayDetailPrice.getDepositRate()).divide(BigDecimal.valueOf(1000000), 20, RoundingMode.HALF_UP));
        }
        BigDecimal depositBalanceEnd = depositBalanceBegin;
        if (depositBalanceEnd.longValue() > 0) {
            // 取现金流最后一期日期
            List<BudgetPlanPayDetailCashFlow> cashFlowList = SpringUtil.getBean(BudgetPlanPayDetailCashFlowService.class).listByDetailId(budgetPlanPayDetail.getId());
            if (CollectionUtil.isNotEmpty(cashFlowList)) {
                cashFlowList.sort(Comparator.comparing(BudgetPlanPayDetailCashFlow::getCashFlowDate).reversed());
                LocalDate lastCashFlowDate = cashFlowList.get(0).getCashFlowDate();
                if (dataEndDate.isAfter(lastCashFlowDate)) {
                    // 最后一期在预算区间结束日期之前的，说明在预算区间内会结清，期末的保证金余额为0
                    depositBalanceEnd = BigDecimal.ZERO;
                }
            }
        }
        BigDecimal riskFundBalanceBegin;
        BigDecimal riskFundBalanceEnd;
        BigDecimal riskFundDiffBD;
        // 从参数配置中获取拨备计提比例
        FtpIndustryCategoryEnum ftpIndustryCategoryEnum = FtpIndustryCategoryEnum.getByName(budgetPlanPayDetail.getFtpIndustryCategory());
        RelatedTermRange relatedTermRange = RelatedTermRange.convertFromMonthCount(budgetPlanPayDetailPrice.getTermMonth());
        Integer riskFundRate = SpringUtil.getBean(BudgetParameterConfigService.class).getRiskReserve(ftpIndustryCategoryEnum, relatedTermRange);
        riskFundBalanceBegin = BigDecimal.valueOf(endOfLastYearBalance).subtract(depositBalanceBegin).multiply(BigDecimal.valueOf(riskFundRate).divide(BigDecimal.valueOf(1000000), 20, RoundingMode.HALF_UP));
        if (riskFundBalanceBegin.longValue() < 0) {
            riskFundBalanceBegin = BigDecimal.ZERO;
        }
        riskFundBalanceEnd = BigDecimal.valueOf(endOfThisYearBalance).subtract(depositBalanceEnd).multiply(BigDecimal.valueOf(riskFundRate).divide(BigDecimal.valueOf(1000000), 20, RoundingMode.HALF_UP));
        if (riskFundBalanceEnd.longValue() < 0) {
            riskFundBalanceEnd = BigDecimal.ZERO;
        }
        riskFundDiffBD = riskFundBalanceEnd.subtract(riskFundBalanceBegin);
//        // 考核利润 = 毛利-拨备+项目利润调整项
//        // 重要！！！此处默认是初始化动作，一定不存在项目利润调整项，如果需要计算项目利润调整项，上层需自己处理
//        BigDecimal profitBD = grassProfitBD.subtract(riskFundDiffBD);
//        // 扣费后利润
        Integer expenseRate = SpringUtil.getBean(BudgetParameterConfigService.class).getExpenseRatioByDeptId(budgetPlanPayDetail.getBelongDeptId());
//        BigDecimal profitWithoutExpenseBD = FinancialUtil.calculateProfitWithoutExpense(profitBD, expenseRate);
//        // 费用 = 考核利润 - 扣费后利润
//        BigDecimal expenseBD = profitBD.subtract(profitWithoutExpenseBD);
//        // 考核利润（原始值） = 毛利 - 拨备
//        BigDecimal profitOriginalBD = grassProfitBD.subtract(riskFundDiffBD);
//        // 扣费后利润 = 考核利润（原始值） - 费用
//        BigDecimal profitWithoutExpenseOriginalBD = profitOriginalBD.subtract(expenseBD);
        // 保存数据
        profitDetail.setPeriodType(BudgetPlanTypeEnum.YEAR.getProfitPeriodType());
        profitDetail.setPeriodValue(String.valueOf(dataStartDate.getYear()));
        profitDetail.setBudgetPlanId(budgetPlanProfit.getBudgetPlanId());
        profitDetail.setBudgetPlanProfitId(budgetPlanProfit.getId());
        BudgetPlanDataCategoryEnum budgetPlanDataCategoryEnum = this.ensureDataCategory(budgetPlanPayDetailPrice.getPayDate(), budgetPlanProfit.getBudgetDateFrom(), budgetPlanProfit.getBudgetDateTo());
        profitDetail.setDataCategory(budgetPlanDataCategoryEnum.name());
        profitDetail.setBelongDeptId(budgetPlanPayDetail.getBelongDeptId());
        profitDetail.setSponsorUserId(budgetPlanPayDetail.getSponsorUserId());
        profitDetail.setClientName(budgetPlanPayDetail.getClientName());
        profitDetail.setFtpIndustryCategory(budgetPlanPayDetail.getFtpIndustryCategory());
        profitDetail.setRiskControlIndustryClassify(budgetPlanPayDetail.getRiskControlIndustryClassify());
        profitDetail.setLeaseType(budgetPlanPayDetail.getLeaseType());
        profitDetail.setTermMonth(budgetPlanPayDetailPrice.getTermMonth());
        profitDetail.setRepayFrequency(budgetPlanPayDetailPrice.getRepayFrequency());
        profitDetail.setDepositRate(budgetPlanPayDetailPrice.getDepositRate());
        profitDetail.setConsultingFeeRate(budgetPlanPayDetailPrice.getConsultingFeeRate());
        profitDetail.setConsultingFeeRateYear(Util.mithrasIntegerDecimalTwo(BigDecimal.valueOf(profitDetail.getConsultingFeeRate()).divide(BigDecimal.valueOf(budgetPlanPayDetailPrice.getTermMonth()).divide(BigDecimal.valueOf(12), 20, RoundingMode.HALF_UP), 20, RoundingMode.HALF_UP).intValue()));
        profitDetail.setContractInterestRate(budgetPlanPayDetailPrice.getContractInterestRate());
        profitDetail.setIrr(budgetPlanPayDetailPrice.getIrr());
        profitDetail.setXirr(budgetPlanPayDetailPrice.getXirr());
        profitDetail.setFtp(budgetPlanPayDetail.getFtp());
        profitDetail.setActualPay(budgetPlanPayDetailPrice.getProjectAmount());
        profitDetail.setPayDate(budgetPlanPayDetailPrice.getPayDate());
        if (Objects.nonNull(budgetPlanPayDetailPrice.getPayDate())) {
            profitDetail.setPayDateYear(budgetPlanPayDetailPrice.getPayDate().getYear());
            profitDetail.setPayDateMonth(budgetPlanPayDetailPrice.getPayDate().getMonthValue());
        }
        profitDetail.setEndOfLastPeriodBalance(endOfLastYearBalance);
        profitDetail.setEndOfThisPeriodBalance(endOfThisYearBalance);
        profitDetail.setFundOccupyAverage(Util.mithrasLongDecimalTwo(costAverageBD.longValue()));
        profitDetail.setInterestIncome(interestIncome);
        profitDetail.setInterestIncomeWithoutTax(Util.mithrasLongDecimalTwo(interestIncomeWithoutTaxBD.longValue()));
        profitDetail.setConsultingFeeIncome(consultingFeeIncome);
        profitDetail.setConsultingFeeIncomeWithoutTax(Util.mithrasLongDecimalTwo(consultingFeeIncomeWithoutTaxBD.longValue()));
        profitDetail.setNominalPriceIncome(nominalPriceIncome);
        profitDetail.setNominalPriceIncomeWithoutTax(Util.mithrasLongDecimalTwo(nominalPriceIncomeWithoutTaxBD.longValue()));
        profitDetail.setIncome(incomeBD.longValue());
        profitDetail.setIncomeWithoutTax(Util.mithrasLongDecimalTwo(incomeWithoutTaxBD.longValue()));
        profitDetail.setCost(cost);
        profitDetail.setCostWithoutTax(cost);
//        profitDetail.setCostWithoutTax(Util.mithrasLongDecimalTwo(FinancialUtil.calculateAmountWithoutTax(cost, valueAddedTaxRate).longValue()));
        profitDetail.setValueAddedTax(Util.mithrasLongDecimalTwo(valueAddedTaxBD.longValue()));
        profitDetail.setStampTax(Util.mithrasLongDecimalTwo(stampTaxBD.longValue()));
        profitDetail.setAdditionalTax(Util.mithrasLongDecimalTwo(additionalTaxBD.longValue()));
        profitDetail.setEndOfLastPeriodRiskFund(Util.mithrasLongDecimalTwo(riskFundBalanceBegin.longValue()));
        profitDetail.setEndOfThisPeriodRiskFund(Util.mithrasLongDecimalTwo(riskFundBalanceEnd.longValue()));
        profitDetail.setRiskFundDiff(Util.mithrasLongDecimalTwo(riskFundDiffBD.longValue()));
        profitDetail.setGrossProfit(Util.mithrasLongDecimalTwo(grassProfitBD.longValue()));
//        profitDetail.setExpense(Util.mithrasLongDecimalTwo(expenseBD.longValue()));
//        // 存量考核利润，如果为负数，需要置为0
//        long profit = profitBD.longValue();
//        long profitWithoutExpense = profitWithoutExpenseBD.longValue();
//        if (budgetPlanDataCategoryEnum == BudgetPlanDataCategoryEnum.HISTORY && profitOriginalBD.longValue() < 0) {
//            profit = 0L;
//        }
//        if (budgetPlanDataCategoryEnum == BudgetPlanDataCategoryEnum.HISTORY && profitWithoutExpenseOriginalBD.longValue() < 0) {
//            profitWithoutExpense = 0L;
//        }
//        profitDetail.setAssessmentProfit(profit);
//        profitDetail.setAssessmentProfitOriginal(Util.mithrasLongDecimalTwo(profitOriginalBD.longValue()));
//        profitDetail.setAssessmentProfitWithoutExpense(profitWithoutExpense);
//        profitDetail.setAssessmentProfitWithoutExpenseOriginal(Util.mithrasLongDecimalTwo(profitWithoutExpenseOriginalBD.longValue()));
        profitDetail.calculate(expenseRate);
        return profitDetail;
    }

    private BudgetPlanDataCategoryEnum ensureDataCategory(LocalDate targetDate, LocalDate budgetDateFrom, LocalDate budgetDateTo) {
        if (targetDate.isBefore(budgetDateFrom)) {
            return BudgetPlanDataCategoryEnum.HISTORY;
        } else {
            return BudgetPlanDataCategoryEnum.FUTURE;
        }
    }

    private RiskFundHelper calculateRiskFund(long endOfLastPeriodBalance, long endOfThisPeriodBalance, ContractBaseInfo contractBaseInfo, LocalDate dataStartDate, LocalDate dataEndDate, ContractReceipt contractReceipt, boolean assetClassifyIsNormal) {
        long depositBalanceBegin = SpringUtil.getBean(MarginRecordService.class).calculateBalanceBeforeTargetDate(contractBaseInfo.getId(), dataStartDate);
        long depositBalanceEnd = SpringUtil.getBean(MarginRecordService.class).calculateBalanceBeforeTargetDate(contractBaseInfo.getId(), dataEndDate);
        // 取期初的计提比例
        Integer riskFundRateBegin = SpringUtil.getBean(KpiProvisionDetailService.class).getTargetDateByReceiptId(contractReceipt.getId(), dataStartDate, true);
        if (Objects.isNull(riskFundRateBegin) && !assetClassifyIsNormal) {
            // 取最近一次的
            riskFundRateBegin = SpringUtil.getBean(KpiProvisionDetailService.class).getTargetDateByReceiptId(contractReceipt.getId(), dataStartDate, false);
        }
        if (Objects.isNull(riskFundRateBegin)) {
            FtpIndustryCategoryEnum ftpIndustryCategoryEnum = SpringUtil.getBean(ContractService.class).getFtpIndustryCategory(contractBaseInfo.getId());
            long monthCount = LocalDateTimeUtil.between(dataStartDate.atStartOfDay(), contractBaseInfo.getActualFinishDate().atStartOfDay(), ChronoUnit.MONTHS);
            RelatedTermRange relatedTermRange = RelatedTermRange.convertFromMonthCount((int) monthCount);
            riskFundRateBegin = SpringUtil.getBean(BudgetParameterConfigService.class).getRiskReserve(ftpIndustryCategoryEnum, relatedTermRange);
        }
        // 取期末的计提比例
        Integer riskFundRateEnd = SpringUtil.getBean(KpiProvisionDetailService.class).getTargetDateByReceiptId(contractReceipt.getId(), dataEndDate, true);
        if (Objects.isNull(riskFundRateEnd) && !assetClassifyIsNormal) {
            // 取最近一次的
            riskFundRateEnd = SpringUtil.getBean(KpiProvisionDetailService.class).getTargetDateByReceiptId(contractReceipt.getId(), dataEndDate, false);
        }
        if (Objects.isNull(riskFundRateEnd)) {
            FtpIndustryCategoryEnum ftpIndustryCategoryEnum = SpringUtil.getBean(ContractService.class).getFtpIndustryCategory(contractBaseInfo.getId());
            long monthCount = LocalDateTimeUtil.between(dataEndDate.atStartOfDay(), contractBaseInfo.getActualFinishDate().atStartOfDay(), ChronoUnit.MONTHS);
            RelatedTermRange relatedTermRange = RelatedTermRange.convertFromMonthCount((int) monthCount);
            riskFundRateEnd = SpringUtil.getBean(BudgetParameterConfigService.class).getRiskReserve(ftpIndustryCategoryEnum, relatedTermRange);
        }
        // 计算风险准备金相关金额
        BigDecimal riskFundBalanceBeginBD = BigDecimal.ZERO;
        BigDecimal riskFundBalanceEndBD = BigDecimal.ZERO;
        if (Objects.nonNull(riskFundRateBegin)) {
            long riskExposureBegin = endOfLastPeriodBalance - depositBalanceBegin;
            riskFundBalanceBeginBD = BigDecimal.valueOf(riskExposureBegin < 0 ? 0 : riskExposureBegin).multiply(BigDecimal.valueOf(riskFundRateBegin).divide(BigDecimal.valueOf(1000000), 20, RoundingMode.HALF_UP));
        }
        if (Objects.nonNull(riskFundRateEnd)) {
            long riskExposureEnd = endOfThisPeriodBalance - depositBalanceEnd;
            riskFundBalanceEndBD = BigDecimal.valueOf(riskExposureEnd < 0 ? 0 : riskExposureEnd).multiply(BigDecimal.valueOf(riskFundRateEnd).divide(BigDecimal.valueOf(1000000), 20, RoundingMode.HALF_UP));
        }
        BigDecimal riskFundDiffBD = riskFundBalanceEndBD.subtract(riskFundBalanceBeginBD);
        return new RiskFundHelper(riskFundBalanceBeginBD, riskFundBalanceEndBD, riskFundDiffBD);
    }

    private long calculatePrincipalBalance(ContractReceipt contractReceipt, LocalDate targetDate, boolean includeTargetDate, boolean overdue) {
        List<PaymentBaseInfo> paymentBaseInfoList = SpringUtil.getBean(PaymentBaseInfoService.class).listByReceiptId(contractReceipt.getId());
        if (CollectionUtil.isEmpty(paymentBaseInfoList)) {
            return 0L;
        }
        Set<Long> paymentIds = paymentBaseInfoList.stream().map(PaymentBaseInfo::getId).collect(Collectors.toSet());
        List<PaymentActualDetail> paymentActualDetailList = SpringUtil.getBean(PaymentActualDetailService.class).listByPaymentIds(paymentIds);
        List<CollectionBaseInfo> collectionBaseInfoList = SpringUtil.getBean(CollectionBaseInfoService.class).listByContractIds(Collections.singletonList(contractReceipt.getContractId()));
        // 只取当前借据或者付款的
        Iterator<CollectionBaseInfo> iterator = collectionBaseInfoList.iterator();
        while (iterator.hasNext()) {
            CollectionBaseInfo current = iterator.next();
            if (Objects.equals(current.getReceiptId(), contractReceipt.getId())) {
                continue;
            }
            if (Objects.nonNull(current.getPaymentId()) && paymentIds.contains(current.getPaymentId())) {
                continue;
            }
            iterator.remove();
        }
        // 投放款
        long actualPay = paymentActualDetailList.stream()
                .filter(e -> includeTargetDate ? !e.getPaidInDate().isAfter(targetDate) : e.getPaidInDate().isBefore(targetDate))
                .mapToLong(PaymentActualDetail::getPaidInAmount)
                .sum();
        // 首期租金
        long firstRent = collectionBaseInfoList.stream()
                .filter(e -> StrUtil.equals(e.getCashFlowItem(), CashFlowItemEnum.FIRST_RENT.name()))
                .filter(e -> includeTargetDate ? !e.getPlanCollectionDate().isAfter(targetDate) : e.getPlanCollectionDate().isBefore(targetDate))
                .mapToLong(e -> {
                    if (Objects.nonNull(e.getCollectionAmount()) && e.getCollectionAmount() > 0) {
                        return e.getCollectionAmount();
                    } else if (Objects.nonNull(e.getPlanCollectionAmount())) {
                        return e.getPlanCollectionAmount();
                    } else {
                        return 0L;
                    }
                }).sum();
        // 本金
        long rent = collectionBaseInfoList.stream()
                .filter(e -> StrUtil.equals(e.getCashFlowItem(), CashFlowItemEnum.RENT.name()))
                .filter(e -> includeTargetDate ? !e.getPlanCollectionDate().isAfter(targetDate) : e.getPlanCollectionDate().isBefore(targetDate))
                .mapToLong(e -> {
                    if (Objects.nonNull(e.getCollectionPrincipal()) && e.getCollectionPrincipal() > 0) {
                        return e.getCollectionPrincipal();
                    } else if (Objects.nonNull(e.getPrincipal())) {
                        if (overdue) {
                            return 0L;
                        } else {
                            return e.getPrincipal();
                        }
                    } else {
                        return 0L;
                    }
                }).sum();
        return actualPay - firstRent - rent;
    }

    private ContractRentActual findLastRent(ContractBaseInfo contractBaseInfo) {
        // 确定合同最后一期日期
        List<ContractRentActual> rentActualList = SpringUtil.getBean(ContractRentActualService.class).listByContract(contractBaseInfo.getId());
        if (CollectionUtil.isNotEmpty(rentActualList)) {
            // 容错处理，如果没有日期的，填充一个默认日期(历史已结清数据可能会有这种情况)
            rentActualList.forEach(e -> {
                if (Objects.isNull(e.getCashFlowDate())) {
                    e.setCashFlowDate(LocalDate.MIN);
                }
            });
            rentActualList.sort(Comparator.comparing(ContractRentActual::getCashFlowDate).reversed());
            return rentActualList.get(0);
        }
        return null;
    }

    private void suppleExtraProject(List<BudgetPlanPayDetail> detailList, BudgetPlan budgetPlan, BudgetPlanPay budgetPlanPay, Set<Long> ignoreProjReviewIds) {
        // 查询投放日期在填报区间内的
        LambdaQueryWrapper<PaymentActualDetail> query = Wrappers.lambdaQuery();
        query.ge(PaymentActualDetail::getPaidInDate, budgetPlan.getWriteDateFrom());
        query.le(PaymentActualDetail::getPaidInDate, budgetPlan.getWriteDateTo());
        query.orderByAsc(PaymentActualDetail::getPaidInDate);
        List<PaymentActualDetail> paymentActualDetailList = SpringUtil.getBean(PaymentActualDetailService.class).list(query);
        if (CollectionUtil.isEmpty(paymentActualDetailList)) {
            return;
        }
        Map<Long, List<PaymentActualDetail>> paymentId2ActualDetailMap = paymentActualDetailList.stream().collect(Collectors.groupingBy(PaymentActualDetail::getPaymentId));
        // 查询收款确认信息
        List<PaymentCollectionInfo> paymentCollectionInfoList = SpringUtil.getBean(PaymentCollectionInfoMapper.class).selectList(
                Wrappers.<PaymentCollectionInfo>lambdaQuery()
                        .in(PaymentCollectionInfo::getPaymentId, paymentId2ActualDetailMap.keySet())
        );
        Map<Long, PaymentCollectionInfo> paymentId2CollectionMap = paymentCollectionInfoList.stream().collect(Collectors.toMap(PaymentCollectionInfo::getPaymentId, e -> e, (a, b) -> b));
        // 查询FTP价格
        List<FtpAssessmentInfo> ftpAssessmentInfoList = SpringUtil.getBean(FtpAssessmentInfoService.class).list(
                Wrappers.<FtpAssessmentInfo>lambdaQuery().in(FtpAssessmentInfo::getPaymentId, paymentId2ActualDetailMap.keySet()).eq(FtpAssessmentInfo::getIsEffect, YesOrNoNumberEnum.YES.getCode())
        );
        Map<Long, List<FtpAssessmentInfo>> paymentId2FtpInfoMap = ftpAssessmentInfoList.stream().collect(Collectors.groupingBy(FtpAssessmentInfo::getPaymentId));
        // 查询付款申请
        List<PaymentBaseInfo> paymentBaseInfoList = SpringUtil.getBean(PaymentBaseInfoService.class).listByIds(paymentId2ActualDetailMap.keySet());
        Map<Long, List<PaymentBaseInfo>> contractId2PaymentMap = paymentBaseInfoList.stream().collect(Collectors.groupingBy(PaymentBaseInfo::getContractId));
        // 查询合同
        List<ContractBaseInfo> contractBaseInfoList = SpringUtil.getBean(ContractBaseInfoService.class).listByIds(paymentBaseInfoList.stream().map(PaymentBaseInfo::getContractId).collect(Collectors.toSet()));
        Map<Long, List<ContractBaseInfo>> projReviewId2ContractMap = contractBaseInfoList.stream().collect(Collectors.groupingBy(ContractBaseInfo::getProjReviewId));
        // 查询定价
        List<ProjPricingBaseInfo> projPricingBaseInfoList = SpringUtil.getBean(ProjPricingBaseInfoService.class).list(
                Wrappers.<ProjPricingBaseInfo>lambdaQuery()
                        .in(ProjPricingBaseInfo::getProjReviewId, projReviewId2ContractMap.keySet())
                        .eq(ProjPricingBaseInfo::getProjPricingStatus, RecordStatus.TAKE_EFFECT.name())
        );
        Map<Long, ProjPricingBaseInfo> projReviewId2PricingMap = projPricingBaseInfoList.stream().collect(Collectors.toMap(ProjPricingBaseInfo::getProjReviewId, e -> e, (a, b) -> b));
        // 遍历处理
        for (Long projReviewId : projReviewId2ContractMap.keySet()) {
            try {
                if (ignoreProjReviewIds.contains(projReviewId)) {
                    continue;
                }
                BudgetPlanPayDetail budgetPlanPayDetail = budgetPlanPayDetailService.copyFromProjReview(projReviewId);
                budgetPlanPayDetail.setBudgetPlanId(budgetPlanPay.getBudgetPlanId());
                budgetPlanPayDetail.setBudgetPlanPayId(budgetPlanPay.getId());
                // 投放日
                LocalDate payDate = this.ensurePayDate(projReviewId2ContractMap.get(projReviewId), contractId2PaymentMap, paymentId2ActualDetailMap);
                if (Objects.isNull(payDate)) {
                    log.error("月度调整计划添加计划外项目无法确定投放日期，跳过不处理[projReviewId:{}]", projReviewId);
                    continue;
                }
                // 业务类型（一个项目的业务类型在多个合同里应该是一致的，随便取一个）
                String leaseType = Optional.ofNullable(projReviewId2ContractMap.get(projReviewId)).map(e -> e.get(0).getLeaseType()).orElse(null);
                if (StrUtil.isBlank(leaseType)) {
                    log.error("月度调整计划添加计划外项目无法确定租赁类型，跳过不处理[projReviewId:{}]", projReviewId);
                    continue;
                }
                // 投放金额
                Long payAmount = this.ensurePayAmount(projReviewId2ContractMap.get(projReviewId), contractId2PaymentMap, paymentId2ActualDetailMap);
                // 保证金
                Long deposit = this.ensureDepositAmount(projReviewId2ContractMap.get(projReviewId), contractId2PaymentMap, paymentId2CollectionMap);
                // 咨询服务费
                Long consultingFee = this.ensureConsultingFeeAmount(projReviewId2ContractMap.get(projReviewId), contractId2PaymentMap, paymentId2CollectionMap);
                // FTP价格
                Integer ftp = this.ensureFtp(projReviewId2ContractMap.get(projReviewId), contractId2PaymentMap, paymentId2FtpInfoMap);
                // 期限
                Integer termMonth = this.ensureMonthCount(projReviewId);
                // 填充数据
                budgetPlanPayDetail.setPlanPayDate(payDate);
                budgetPlanPayDetail.setLeaseType(leaseType);
                budgetPlanPayDetail.setPlanPayAmount(payAmount);
                budgetPlanPayDetail.setDeposit(deposit);
                budgetPlanPayDetail.setConsultingFee(consultingFee);
                budgetPlanPayDetail.setFtp(ftp);
                budgetPlanPayDetail.setARate(Optional.ofNullable(projReviewId2PricingMap.get(projReviewId)).map(e -> Objects.isNull(e.getIsAAA()) ? null : e.getIsAAA().toString()).orElse(null));
                budgetPlanPayDetail.setManageLevel(Optional.ofNullable(projReviewId2PricingMap.get(projReviewId)).map(ProjPricingBaseInfo::getProjectManageLevel).orElse(null));
                // 计算价差
                if (Objects.nonNull(budgetPlanPayDetail.getIrr()) && Objects.nonNull(budgetPlanPayDetail.getFtp())) {
                    budgetPlanPayDetail.setIrrFtpDiff(budgetPlanPayDetail.getIrr() - budgetPlanPayDetail.getFtp());
                }
                budgetPlanPayDetail.setTermMonth(termMonth);
                detailList.add(budgetPlanPayDetail);
            } catch (Exception e) {
                log.error("月度调整预算增加计划外项目发生异常[projReviewId:{}]", projReviewId, e);
            }
        }
    }

    private LocalDate ensurePayDate(List<ContractBaseInfo> contractBaseInfoList, Map<Long, List<PaymentBaseInfo>> contractId2PaymentMap, Map<Long, List<PaymentActualDetail>> paymentId2ActualDetailMap) {
        LocalDate payDate = null;
        if (CollectionUtil.isEmpty(contractBaseInfoList)) {
            return payDate;
        }
        List<LocalDate> candidateList = new LinkedList<>();
        for (ContractBaseInfo contractBaseInfo : contractBaseInfoList) {
            List<PaymentBaseInfo> paymentBaseInfoList = contractId2PaymentMap.get(contractBaseInfo.getId());
            if (CollectionUtil.isEmpty(paymentBaseInfoList)) {
                continue;
            }
            for (PaymentBaseInfo paymentBaseInfo : paymentBaseInfoList) {
                List<PaymentActualDetail> paymentActualDetailList = paymentId2ActualDetailMap.get(paymentBaseInfo.getId());
                if (CollectionUtil.isEmpty(paymentActualDetailList)) {
                    continue;
                }
                for (PaymentActualDetail paymentActualDetail : paymentActualDetailList) {
                    candidateList.add(paymentActualDetail.getPaidInDate());
                }
            }
        }
        if (CollectionUtil.isEmpty(candidateList)) {
            return null;
        }
        candidateList.sort(Comparator.comparing(e -> e));
        return candidateList.get(0);
    }

    private Long ensurePayAmount(List<ContractBaseInfo> contractBaseInfoList, Map<Long, List<PaymentBaseInfo>> contractId2PaymentMap, Map<Long, List<PaymentActualDetail>> paymentId2ActualDetailMap) {
        long amount = 0L;
        if (CollectionUtil.isEmpty(contractBaseInfoList)) {
            return amount;
        }
        for (ContractBaseInfo contractBaseInfo : contractBaseInfoList) {
            List<PaymentBaseInfo> paymentBaseInfoList = contractId2PaymentMap.get(contractBaseInfo.getId());
            if (CollectionUtil.isEmpty(paymentBaseInfoList)) {
                continue;
            }
            for (PaymentBaseInfo paymentBaseInfo : paymentBaseInfoList) {
                List<PaymentActualDetail> paymentActualDetailList = paymentId2ActualDetailMap.get(paymentBaseInfo.getId());
                if (CollectionUtil.isEmpty(paymentActualDetailList)) {
                    continue;
                }
                for (PaymentActualDetail paymentActualDetail : paymentActualDetailList) {
                    amount += Optional.ofNullable(paymentActualDetail.getPaidInAmount()).orElse(0L);
                }
            }
        }
        return amount;
    }

    private Long ensureDepositAmount(List<ContractBaseInfo> contractBaseInfoList, Map<Long, List<PaymentBaseInfo>> contractId2PaymentMap, Map<Long, PaymentCollectionInfo> paymentId2CollectionMap) {
        long amount = 0L;
        if (CollectionUtil.isEmpty(contractBaseInfoList)) {
            return amount;
        }
        for (ContractBaseInfo contractBaseInfo : contractBaseInfoList) {
            List<PaymentBaseInfo> paymentBaseInfoList = contractId2PaymentMap.get(contractBaseInfo.getId());
            if (CollectionUtil.isEmpty(paymentBaseInfoList)) {
                continue;
            }
            for (PaymentBaseInfo paymentBaseInfo : paymentBaseInfoList) {
                PaymentCollectionInfo paymentCollectionInfo = paymentId2CollectionMap.get(paymentBaseInfo.getId());
                if (Objects.isNull(paymentCollectionInfo)) {
                    continue;
                }
                amount += paymentCollectionInfo.getEarnestMoney();
            }
        }
        return amount;
    }

    private Long ensureConsultingFeeAmount(List<ContractBaseInfo> contractBaseInfoList, Map<Long, List<PaymentBaseInfo>> contractId2PaymentMap, Map<Long, PaymentCollectionInfo> paymentId2CollectionMap) {
        long amount = 0L;
        if (CollectionUtil.isEmpty(contractBaseInfoList)) {
            return amount;
        }
        for (ContractBaseInfo contractBaseInfo : contractBaseInfoList) {
            List<PaymentBaseInfo> paymentBaseInfoList = contractId2PaymentMap.get(contractBaseInfo.getId());
            if (CollectionUtil.isEmpty(paymentBaseInfoList)) {
                continue;
            }
            for (PaymentBaseInfo paymentBaseInfo : paymentBaseInfoList) {
                PaymentCollectionInfo paymentCollectionInfo = paymentId2CollectionMap.get(paymentBaseInfo.getId());
                if (Objects.isNull(paymentCollectionInfo)) {
                    continue;
                }
                amount += paymentCollectionInfo.getEarnestMoney();
            }
        }
        return amount;
    }

    private Integer ensureFtp(List<ContractBaseInfo> contractBaseInfoList, Map<Long, List<PaymentBaseInfo>> contractId2PaymentMap, Map<Long, List<FtpAssessmentInfo>> paymentId2FtpInfoMap) {
        // 月度计划投放的FTP价格都应该一致，取一个即可
        if (CollectionUtil.isEmpty(contractBaseInfoList)) {
            return 0;
        }
        for (ContractBaseInfo contractBaseInfo : contractBaseInfoList) {
            List<PaymentBaseInfo> paymentBaseInfoList = contractId2PaymentMap.get(contractBaseInfo.getId());
            if (CollectionUtil.isEmpty(paymentBaseInfoList)) {
                continue;
            }
            for (PaymentBaseInfo paymentBaseInfo : paymentBaseInfoList) {
                List<FtpAssessmentInfo> ftpAssessmentInfoList = paymentId2FtpInfoMap.get(paymentBaseInfo.getId());
                if (CollectionUtil.isEmpty(ftpAssessmentInfoList)) {
                    continue;
                }
                ftpAssessmentInfoList.sort(Comparator.comparing(FtpAssessmentInfo::getEffectDate).reversed());
                return ftpAssessmentInfoList.get(0).getAssessmentPrice().intValue();
            }
        }
        return 0;
    }

    private Integer ensureMonthCount(Long projReviewId) {
        ProjReviewPriceDetailRSP rsp = SpringUtil.getBean(ProjReviewPriceService.class).detail(projReviewId);
        return Optional.ofNullable(rsp.getMonthCount()).orElse(0);
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    private static class RiskFundHelper {
        private BigDecimal riskFundBalanceBeginBD = BigDecimal.ZERO;
        private BigDecimal riskFundBalanceEndBD = BigDecimal.ZERO;
        private BigDecimal riskFundDiffBD = BigDecimal.ZERO;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class FtpInterestHelper {
        private String interestDate;
        private Long cashInterest;
        private Integer cashFtp;
        private Long cashOccupy;
        private Long billInterest;
        private Integer billFtp;
        private Long billOccupy;
    }
}
