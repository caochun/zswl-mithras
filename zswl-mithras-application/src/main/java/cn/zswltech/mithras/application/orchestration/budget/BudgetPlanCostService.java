package cn.zswltech.mithras.application.orchestration.budget;

import cn.zswltech.mithras.budget.application.BudgetPlanCostDetailFundService;
import cn.zswltech.mithras.budget.application.BudgetPlanCostDetailProjectService;
import cn.zswltech.mithras.budget.application.BudgetPlanPayDetailExpenseService;
import cn.zswltech.mithras.budget.application.BudgetPlanPayDetailPriceService;
import cn.zswltech.mithras.budget.application.BudgetPlanPayProcessInfoService;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.budget.application.BudgetPlanCostApplicationService;
import cn.zswltech.mithras.budget.application.BudgetPlanPayDetailCashFlowService;
import cn.zswltech.mithras.budget.bo.BudgetEclRiskReserveBO;
import cn.zswltech.mithras.budget.bo.BudgetPlanStatisticsBO;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.dto.budget.BudgetPlanCostDetailListRSP;
import cn.zswltech.mithras.dto.budget.BudgetPlanCostListREQ;
import cn.zswltech.mithras.dto.budget.BudgetPlanCostListRSP;
import cn.zswltech.mithras.foundation.enums.CashFlowItemEnum;
import cn.zswltech.mithras.budget.enums.BudgetPlanCalculateStatusEnum;
import cn.zswltech.mithras.budget.enums.BudgetPlanDataCategoryEnum;
import cn.zswltech.mithras.budget.enums.BudgetPlanTypeEnum;
import cn.zswltech.mithras.budget.enums.BudgetStatusEnum;
import cn.zswltech.mithras.capital.enums.FinanceCashFlowItemEnum;
import cn.zswltech.mithras.contract.enums.contract.ContractStatus;
import cn.zswltech.mithras.fund.enums.financing.FundFinancingStatusEnum;
import cn.zswltech.mithras.fund.directfinancing.mapper.model.FundDirectFinancingBaseInfo;
import cn.zswltech.mithras.fund.directfinancing.mapper.model.FundDirectFinancingRepayActual;
import cn.zswltech.mithras.application.orchestration.fund.direct.service.FundDirectFinancingBaseInfoService;
import cn.zswltech.mithras.application.orchestration.fund.direct.service.FundDirectFinancingRepayActualService;
import cn.zswltech.mithras.budget.mapper.BudgetPlanCostMapper;
import cn.zswltech.mithras.budget.mapper.dto.BudgetPlanCostDetailFundGroupMonthDTO;
import cn.zswltech.mithras.budget.mapper.dto.BudgetPlanCostDetailProjectGroupMonthDTO;
import cn.zswltech.mithras.budget.mapper.model.*;
import cn.zswltech.mithras.collection.mapper.model.CollectionBaseInfo;
import cn.zswltech.mithras.contract.mapper.model.contract.ContractBaseInfo;
import cn.zswltech.mithras.fund.mapper.model.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.fund.mapper.model.financing.FundFinancingRepayActual;
import cn.zswltech.mithras.fund.mapper.model.receiptrepay.FundReceiptFlowDetail;
import cn.zswltech.mithras.fund.mapper.model.receiptrepay.FundReceiptRepayBaseInfo;
import cn.zswltech.mithras.payment.mapper.model.PaymentActualDetail;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.foundation.util.Util;
import cn.zswltech.mithras.fund.application.bo.FundCashFlowBO;
import cn.zswltech.mithras.collection.application.CollectionBaseInfoService;
import cn.zswltech.mithras.contract.core.ContractBaseInfoService;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingBaseInfoService;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingRepayActualService;
import cn.zswltech.mithras.application.orchestration.fund.receiptrepay.FundReceiptFlowDetailService;
import cn.zswltech.mithras.application.orchestration.fund.receiptrepay.FundReceiptRepayBaseInfoService;
import cn.zswltech.mithras.margin.service.MarginBaseInfoService;
import cn.zswltech.mithras.application.orchestration.payment.PaymentActualDetailService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
* @description 预算管理-预算计划-成本预算
* @author vico
* @date 2025-04-11
*/
@Slf4j
@Service
public class BudgetPlanCostService extends ServiceImpl<BudgetPlanCostMapper, BudgetPlanCost> implements BudgetPlanCostApplicationService {
    @Resource
    private BudgetPlanCostMapper budgetPlanCostMapper;
    @Resource
    private TransactionTemplate transactionTemplate;

    public List<BudgetPlanCostDetailListRSP> detailList(Long budgetPlanCostId) {
        BudgetPlanCost budgetPlanCost = this.getById(budgetPlanCostId);
        if (Objects.isNull(budgetPlanCost)) {
            throw new MithrasException("成本预算主数据不存在");
        }
        if (!Objects.equals(budgetPlanCost.getCalculateStatus(), BudgetPlanCalculateStatusEnum.SUCCESS.name())) {
            return Collections.emptyList();
        }
        // 查询项目端存量数据
        List<BudgetPlanCostDetailProjectGroupMonthDTO> projectHistoryList = SpringUtil.getBean(BudgetPlanCostDetailProjectService.class).getBaseMapper().selectGroupByYearMonth(budgetPlanCostId, BudgetPlanDataCategoryEnum.HISTORY.name());
        Map<String, BudgetPlanCostDetailProjectGroupMonthDTO> projectHistoryMap = projectHistoryList.stream().collect(Collectors.toMap(e -> LocalDateTimeUtil.format(LocalDate.of(e.getYear(), e.getMonth(), 1), DatePattern.NORM_MONTH_PATTERN), e -> e));
        // 查询项目端新增数据
        List<BudgetPlanCostDetailProjectGroupMonthDTO> projectFutureList = SpringUtil.getBean(BudgetPlanCostDetailProjectService.class).getBaseMapper().selectGroupByYearMonth(budgetPlanCostId, BudgetPlanDataCategoryEnum.FUTURE.name());
        Map<String, BudgetPlanCostDetailProjectGroupMonthDTO> projectFutureMap = projectFutureList.stream().collect(Collectors.toMap(e -> LocalDateTimeUtil.format(LocalDate.of(e.getYear(), e.getMonth(), 1), DatePattern.NORM_MONTH_PATTERN), e -> e));
        // 查询资金端数据（目前都只有存量）
        List<BudgetPlanCostDetailFundGroupMonthDTO> fundList = SpringUtil.getBean(BudgetPlanCostDetailFundService.class).getBaseMapper().selectGroupByYearMonth(budgetPlanCostId);
        Map<String, BudgetPlanCostDetailFundGroupMonthDTO> fundMap = fundList.stream().collect(Collectors.toMap(e -> LocalDateTimeUtil.format(LocalDate.of(e.getYear(), e.getMonth(), 1), DatePattern.NORM_MONTH_PATTERN), e -> e));
        // 拼装返回参数
        List<BudgetPlanCostDetailListRSP> result = new LinkedList<>();
        LocalDate targetDate = budgetPlanCost.getBudgetDateFrom();
        while (!targetDate.isAfter(budgetPlanCost.getBudgetDateTo())) {
            String key = LocalDateTimeUtil.format(targetDate, DatePattern.NORM_MONTH_PATTERN);
            BudgetPlanCostDetailProjectGroupMonthDTO projectHistory = projectHistoryMap.get(key);
            BudgetPlanCostDetailProjectGroupMonthDTO projectFuture = projectFutureMap.get(key);
            BudgetPlanCostDetailFundGroupMonthDTO fund = fundMap.get(key);
            BudgetPlanCostDetailListRSP rsp = new BudgetPlanCostDetailListRSP();
            rsp.setBudgetPlanId(budgetPlanCost.getBudgetPlanId());
            rsp.setBudgetPlanCostId(budgetPlanCost.getId());
            rsp.setYear(targetDate.getYear());
            rsp.setMonth(targetDate.getMonthValue());
            if (Objects.nonNull(projectHistory)) {
                rsp.setProjectRentHistory(projectHistory.getRentSum());
                rsp.setProjectPrincipalHistory(projectHistory.getPrincipalSum());
                rsp.setProjectInterestHistory(projectHistory.getInterestSum());
                rsp.setProjectDepositHistory(projectHistory.getDepositSum());
            }
            if (Objects.nonNull(projectFuture)) {
                rsp.setProjectRentFeature(projectFuture.getRentSum());
                rsp.setProjectPrincipalFeature(projectFuture.getPrincipalSum());
                rsp.setProjectInterestFeature(projectFuture.getInterestSum());
                rsp.setProjectPayFeature(projectFuture.getPayAmountSum());
                rsp.setProjectDepositFeature(projectFuture.getDepositSum());
                rsp.setProjectConsultingFeeFeature(projectFuture.getConsultingFeeSum());
            }
            if (Objects.nonNull(fund)) {
                rsp.setFinanceRepayHistory(fund.getRepayAmountSum());
                rsp.setFinancePrincipalHistory(fund.getPrincipalSum());
                rsp.setFinanceInterestHistory(fund.getInterestSum());
            }
            // 资金缺口 = 存量租金回笼-还贷存量-退回保证金+新增保证金+新增咨询服务费-投放+新增租金回笼
            long amount = rsp.getProjectRentHistory()
                    - rsp.getFinanceRepayHistory()
                    - rsp.getProjectDepositHistory()
                    + rsp.getProjectDepositFeature()
                    + rsp.getProjectConsultingFeeFeature()
                    - rsp.getProjectPayFeature()
                    + rsp.getProjectRentFeature();
            rsp.setFundGap(amount);
            result.add(rsp);
            targetDate = targetDate.plusMonths(1);
        }
        return result;
    }

    public BudgetPlanCost findByBudgetPlanId(Long budgetPlanId) {
        LambdaQueryWrapper<BudgetPlanCost> query = Wrappers.lambdaQuery();
        query.eq(BudgetPlanCost::getBudgetPlanId, budgetPlanId);
        return this.getOne(query);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void recalculateByBudgetPlanPayDetail(BudgetPlanPayDetail budgetPlanPayDetail) {
        BudgetPlanCost budgetPlanCost = this.findByBudgetPlanId(budgetPlanPayDetail.getBudgetPlanId());
        List<BudgetPlanCostDetailProject> detailProjectList = this.calculateBySinglePlanPayDetail(budgetPlanCost, budgetPlanPayDetail);
        if (CollectionUtil.isNotEmpty(detailProjectList)) {
            // 移除老数据
            SpringUtil.getBean(BudgetPlanCostDetailProjectService.class).remove(Wrappers.<BudgetPlanCostDetailProject>lambdaQuery().eq(BudgetPlanCostDetailProject::getBudgetPlanPayDetailId, budgetPlanPayDetail.getId()));
            SpringUtil.getBean(BudgetPlanCostDetailProjectService.class).saveBatch(detailProjectList);
        }
    }

    public void initCostDetail(Long budgetPlanCostId) {
        BudgetPlanCost budgetPlanCost = this.getById(budgetPlanCostId);
        if (Objects.isNull(budgetPlanCost)) {
            throw new MithrasException("成本预算主数据不存在");
        }
        // 项目端
        List<BudgetPlanCostDetailProject> projectByContractList = this.calculateByContract(budgetPlanCost);
        List<BudgetPlanCostDetailProject> projectByPlanDetailList = this.calculateByPlanPayDetail(budgetPlanCost);
        // 资金端
        List<BudgetPlanCostDetailFund> detailFundList = this.calculateByFundReceipt(budgetPlanCost);
        // 保存数据
        transactionTemplate.executeWithoutResult(transactionStatus -> {
            try {
                if (CollectionUtil.isNotEmpty(projectByContractList)) {
                    SpringUtil.getBean(BudgetPlanCostDetailProjectService.class).saveBatch(projectByContractList);
                }
                if (CollectionUtil.isNotEmpty(projectByPlanDetailList)) {
                    SpringUtil.getBean(BudgetPlanCostDetailProjectService.class).saveBatch(projectByPlanDetailList);
                }
                if (CollectionUtil.isNotEmpty(detailFundList)) {
                    SpringUtil.getBean(BudgetPlanCostDetailFundService.class).saveBatch(detailFundList);
                }
            } catch (Exception e) {
                transactionStatus.setRollbackOnly();
                log.error("初始化成本预算数据异常", e);
            }
        });
    }

    public void modifyCalculateStatus(Long budgetPlanCostId, BudgetPlanCalculateStatusEnum calculateStatus) {
        BudgetPlanCost update = new BudgetPlanCost();
        update.setId(budgetPlanCostId);
        update.setCalculateStatus(calculateStatus.name());
        this.updateById(update);
    }

    public Page<BudgetPlanCost> list(BudgetPlanCostListREQ req) {
        LambdaQueryWrapper<BudgetPlanCost> queryWrapper = Wrappers.<BudgetPlanCost>lambdaQuery()
                .eq(ObjectUtil.isNotEmpty(req.getBudgetStatus()), BudgetPlanCost::getBudgetStatus, req.getBudgetStatus())
                .orderByDesc(BudgetPlanCost::getId);
        return budgetPlanCostMapper.selectPage(new Page<>(req.getPage(), req.getPageSize()), queryWrapper);
    }

    public PageR<BudgetPlanCostListRSP> pageList(BudgetPlanCostListREQ req) {
        Page<BudgetPlanCost> data = this.list(req);
        List<BudgetPlanCostListRSP> list = BeanUtil.copyToList(data.getRecords(), BudgetPlanCostListRSP.class);
        return PageR.of(list, data.getTotal(), data.getPages(), data.getCurrent(), data.getSize());
    }

    @Transactional(rollbackFor = Throwable.class)
    public void deleteByBudgetPlanId(Long budgetPlanId) {
        LambdaQueryWrapper<BudgetPlanCost> query = Wrappers.lambdaQuery();
        query.eq(BudgetPlanCost::getBudgetPlanId, budgetPlanId);
        this.remove(query);
        // 删除明细
        SpringUtil.getBean(BudgetPlanCostDetailFundService.class).deleteByBudgetPlanId(budgetPlanId);
        SpringUtil.getBean(BudgetPlanCostDetailProjectService.class).deleteByBudgetPlanId(budgetPlanId);
    }

    public void confirmByBudgetPlanId(Long budgetPlanId) {
        LambdaUpdateWrapper<BudgetPlanCost> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.set(BudgetPlanCost::getBudgetStatus, BudgetStatusEnum.CONFIRM.name());
        updateWrapper.eq(BudgetPlanCost::getBudgetPlanId, budgetPlanId);
        this.update(updateWrapper);
    }

    private List<BudgetPlanCostDetailProject> calculateByContract(BudgetPlanCost budgetPlanCost) {
        // 找到所有起租合同
        List<ContractBaseInfo> contractBaseInfoList = SpringUtil.getBean(ContractBaseInfoService.class).list(
                Wrappers.<ContractBaseInfo>lambdaQuery()
                        .eq(ContractBaseInfo::getContractStatus, ContractStatus.START_RENT.name())
        );
        if (CollectionUtil.isEmpty(contractBaseInfoList)) {
            return Collections.emptyList();
        }
        List<BudgetPlanCostDetailProject> result = new LinkedList<>();
        // 遍历合同处理
        for (ContractBaseInfo contractBaseInfo : contractBaseInfoList) {
            result.addAll(this.calculateBySingleContract(budgetPlanCost, contractBaseInfo));
        }
        return result;
    }

    private List<BudgetPlanCostDetailProject> calculateBySingleContract(BudgetPlanCost budgetPlanCost, ContractBaseInfo contractBaseInfo) {
        // 找到付款
        List<PaymentActualDetail> paymentActualDetailList = SpringUtil.getBean(PaymentActualDetailService.class).listByContractIds(Collections.singletonList(contractBaseInfo.getId()));
        Map<String, List<PaymentActualDetail>> paymentActualDetailMap = paymentActualDetailList.stream().collect(Collectors.groupingBy(e -> LocalDateTimeUtil.format(e.getPaidInDate(), DatePattern.NORM_MONTH_PATTERN)));
        // 找到收款
        List<CollectionBaseInfo> collectionBaseInfoList = SpringUtil.getBean(CollectionBaseInfoService.class).list(
                Wrappers.<CollectionBaseInfo>lambdaQuery()
                        .eq(CollectionBaseInfo::getContractId, contractBaseInfo.getId())
        );
        // 分类
        List<CollectionBaseInfo> rentList = collectionBaseInfoList.stream().filter(e -> Objects.equals(e.getCashFlowItem(), CashFlowItemEnum.RENT.name())).collect(Collectors.toList());
        List<CollectionBaseInfo> consultingFeeList = collectionBaseInfoList.stream().filter(e -> Objects.equals(e.getCashFlowItem(), CashFlowItemEnum.OTHERAMOUNT.name())).collect(Collectors.toList());
        List<CollectionBaseInfo> depositList = collectionBaseInfoList.stream().filter(e -> Objects.equals(e.getCashFlowItem(), CashFlowItemEnum.EARNEST_MONEY.name())).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(rentList)) {
            return Collections.emptyList();
        }
        // 确定最后一期的年月
        rentList.sort(Comparator.comparing(CollectionBaseInfo::getPlanCollectionDate));
        CollectionBaseInfo lastRent = rentList.get(rentList.size() - 1);
        LocalDate lastRentDate = lastRent.getPlanCollectionDate();
        // 根据日期分组
        Map<String, List<CollectionBaseInfo>> rentMap = rentList.stream().collect(Collectors.groupingBy(e -> LocalDateTimeUtil.format(e.getPlanCollectionDate(), DatePattern.NORM_MONTH_PATTERN)));
        Map<String, List<CollectionBaseInfo>> consutinFeeMap = consultingFeeList.stream().collect(Collectors.groupingBy(e -> LocalDateTimeUtil.format(e.getPlanCollectionDate(), DatePattern.NORM_MONTH_PATTERN)));
        Map<String, List<CollectionBaseInfo>> depositMap = depositList.stream().collect(Collectors.groupingBy(e -> LocalDateTimeUtil.format(e.getPlanCollectionDate(), DatePattern.NORM_MONTH_PATTERN)));
        // 预算区间每个月份一条数据
        List<BudgetPlanCostDetailProject> result = new LinkedList<>();
        LocalDate targetDate = budgetPlanCost.getBudgetDateFrom();
        while (!targetDate.isAfter(budgetPlanCost.getBudgetDateTo())) {
            String key = LocalDateTimeUtil.format(targetDate, DatePattern.NORM_MONTH_PATTERN);
            List<CollectionBaseInfo> list = rentMap.get(key);
            if (CollectionUtil.isEmpty(list)) {
                targetDate = targetDate.plusMonths(1);
                continue;
            }
            BudgetPlanCostDetailProject budgetPlanCostDetailProject = new BudgetPlanCostDetailProject();
            budgetPlanCostDetailProject.setBudgetPlanId(budgetPlanCost.getBudgetPlanId());
            budgetPlanCostDetailProject.setBudgetPlanCostId(budgetPlanCost.getId());
            // 根据起租日是否在预算区间内决定数据分类是新增还是存量
            if (Objects.nonNull(contractBaseInfo.getActualLeaseDate())
                    && !contractBaseInfo.getActualLeaseDate().isBefore(budgetPlanCost.getBudgetDateFrom())
                    && !contractBaseInfo.getActualLeaseDate().isAfter(budgetPlanCost.getBudgetDateTo())) {
                budgetPlanCostDetailProject.setDataCategory(BudgetPlanDataCategoryEnum.FUTURE.name());
            } else {
                budgetPlanCostDetailProject.setDataCategory(BudgetPlanDataCategoryEnum.HISTORY.name());
            }
            budgetPlanCostDetailProject.setYear(targetDate.getYear());
            budgetPlanCostDetailProject.setMonth(targetDate.getMonthValue());
            budgetPlanCostDetailProject.setContractId(contractBaseInfo.getId());
            long principal = 0L;
            long interest = 0L;
            for (CollectionBaseInfo collectionBaseInfo : list) {
                // 有核销取核销，没核销取计划
                if (Objects.nonNull(collectionBaseInfo.getCollectionAmount()) && collectionBaseInfo.getCollectionAmount() != 0) {
                    principal += Optional.ofNullable(collectionBaseInfo.getCollectionPrincipal()).orElse(0L);
                    interest += Optional.ofNullable(collectionBaseInfo.getCollectionInterest()).orElse(0L);
                } else {
                    principal += Optional.ofNullable(collectionBaseInfo.getPrincipal()).orElse(0L);
                    interest += Optional.ofNullable(collectionBaseInfo.getInterest()).orElse(0L);
                }
            }
            budgetPlanCostDetailProject.setRent(principal + interest);
            budgetPlanCostDetailProject.setPrincipal(principal);
            budgetPlanCostDetailProject.setInterest(interest);
            if (Objects.equals(budgetPlanCostDetailProject.getDataCategory(), BudgetPlanDataCategoryEnum.HISTORY.name())) {
                if (targetDate.getYear() == lastRentDate.getYear() && targetDate.getMonthValue() == lastRentDate.getMonthValue()) {
                    // 填充退回保证金
                    long deposit = SpringUtil.getBean(MarginBaseInfoService.class).getMarginBalance(contractBaseInfo.getId());
                    budgetPlanCostDetailProject.setDeposit(deposit);
                }
            } else {
                // 填充投放、咨询服务费、保证金
                long payAmount = Optional.ofNullable(paymentActualDetailMap.get(key)).map(e -> e.stream().mapToLong(PaymentActualDetail::getPaidInAmount).sum()).orElse(0L);
                long consultingFee = 0L;
                if (CollectionUtil.isNotEmpty(consutinFeeMap.get(key))) {
                    for (CollectionBaseInfo collectionBaseInfo : consutinFeeMap.get(key)) {
                        if (Objects.nonNull(collectionBaseInfo.getCollectionAmount()) && collectionBaseInfo.getCollectionAmount() != 0) {
                            consultingFee += collectionBaseInfo.getCollectionAmount();
                        } else {
                            consultingFee += collectionBaseInfo.getPlanCollectionAmount();
                        }
                    }
                }
                long deposit = 0L;
                if (CollectionUtil.isNotEmpty(depositMap.get(key))) {
                    for (CollectionBaseInfo collectionBaseInfo : depositMap.get(key)) {
                        if (Objects.nonNull(collectionBaseInfo.getCollectionAmount()) && collectionBaseInfo.getCollectionAmount() != 0) {
                            deposit += collectionBaseInfo.getCollectionAmount();
                        } else {
                            deposit += collectionBaseInfo.getPlanCollectionAmount();
                        }
                    }
                }
                budgetPlanCostDetailProject.setPayAmount(payAmount);
                budgetPlanCostDetailProject.setConsultingFee(consultingFee);
                budgetPlanCostDetailProject.setDeposit(deposit);
            }
            result.add(budgetPlanCostDetailProject);
            targetDate = targetDate.plusMonths(1);
        }
        return result;
    }

    private List<BudgetPlanCostDetailProject> calculateByPlanPayDetail(BudgetPlanCost budgetPlanCost) {
        List<BudgetPlanPayDetail> budgetPlanPayDetailList = SpringUtil.getBean(BudgetPlanPayDetailService.class).listByBudgetPlanId(budgetPlanCost.getBudgetPlanId());
        if (CollectionUtil.isEmpty(budgetPlanPayDetailList)) {
            return Collections.emptyList();
        }
        List<BudgetPlanCostDetailProject> result = new LinkedList<>();
        for (BudgetPlanPayDetail budgetPlanPayDetail : budgetPlanPayDetailList) {
            result.addAll(this.calculateBySinglePlanPayDetail(budgetPlanCost, budgetPlanPayDetail));
        }
        return result;
    }

    private List<BudgetPlanCostDetailProject> calculateBySinglePlanPayDetail(BudgetPlanCost budgetPlanCost, BudgetPlanPayDetail budgetPlanPayDetail) {
        List<BudgetPlanCostDetailProject> result = new LinkedList<>();
        if (StrUtil.equalsAny(budgetPlanCost.getBudgetType(), BudgetPlanTypeEnum.MONTH.name(), BudgetPlanTypeEnum.MONTH_ADJUST.name())) {
            if (!budgetPlanPayDetail.getPlanPayDate().isBefore(budgetPlanCost.getBudgetDateFrom()) && !budgetPlanPayDetail.getPlanPayDate().isAfter(budgetPlanCost.getBudgetDateTo())) {
                // 月度计划默认当月不会还租金，所以无需考虑租金回笼
                BudgetPlanCostDetailProject budgetPlanCostDetailProject = new BudgetPlanCostDetailProject();
                budgetPlanCostDetailProject.setBudgetPlanId(budgetPlanCost.getBudgetPlanId());
                budgetPlanCostDetailProject.setBudgetPlanCostId(budgetPlanCost.getId());
                budgetPlanCostDetailProject.setBudgetPlanPayDetailId(budgetPlanPayDetail.getId());
                budgetPlanCostDetailProject.setYear(budgetPlanPayDetail.getPlanPayDate().getYear());
                budgetPlanCostDetailProject.setMonth(budgetPlanPayDetail.getPlanPayDate().getMonthValue());
                budgetPlanCostDetailProject.setDataCategory(BudgetPlanDataCategoryEnum.FUTURE.name());
                budgetPlanCostDetailProject.setPayAmount(budgetPlanPayDetail.getPlanPayAmount());
                budgetPlanCostDetailProject.setConsultingFee(budgetPlanPayDetail.getConsultingFee());
                budgetPlanCostDetailProject.setDeposit(budgetPlanCostDetailProject.getDeposit());
                result.add(budgetPlanCostDetailProject);
            } else {
                // 按照逻辑，如果投放计划的产生的是存量数据，那所有数值都为0，没必要落库了？
            }
        } else {
            // 取数据
            BudgetPlanPayDetailPrice budgetPlanPayDetailPrice = SpringUtil.getBean(BudgetPlanPayDetailPriceService.class).getOneByDetailId(budgetPlanPayDetail.getId());
            List<BudgetPlanPayDetailCashFlow> budgetPlanPayDetailCashFlowList = SpringUtil.getBean(BudgetPlanPayDetailCashFlowService.class).listByDetailId(budgetPlanPayDetail.getId());
            budgetPlanPayDetailCashFlowList.sort(Comparator.comparing(BudgetPlanPayDetailCashFlow::getCashFlowDate));
            Map<String, List<BudgetPlanPayDetailCashFlow>> budgetPlanPayDetailCashFlowMap = budgetPlanPayDetailCashFlowList.stream().collect(Collectors.groupingBy(e -> LocalDateTimeUtil.format(e.getCashFlowDate(), DatePattern.NORM_MONTH_PATTERN)));
            LocalDate targetDate = budgetPlanCost.getBudgetDateFrom();
            while (!targetDate.isAfter(budgetPlanCost.getBudgetDateTo())) {
                BudgetPlanCostDetailProject budgetPlanCostDetailProject = new BudgetPlanCostDetailProject();
                budgetPlanCostDetailProject.setBudgetPlanId(budgetPlanCost.getBudgetPlanId());
                budgetPlanCostDetailProject.setBudgetPlanCostId(budgetPlanCost.getId());
                budgetPlanCostDetailProject.setBudgetPlanPayDetailId(budgetPlanPayDetail.getId());
                budgetPlanCostDetailProject.setYear(targetDate.getYear());
                budgetPlanCostDetailProject.setMonth(targetDate.getMonthValue());
                List<BudgetPlanPayDetailCashFlow> cashFlowList = budgetPlanPayDetailCashFlowMap.get(LocalDateTimeUtil.format(targetDate, DatePattern.NORM_MONTH_PATTERN));
                if (CollectionUtil.isNotEmpty(cashFlowList)) {
                    budgetPlanCostDetailProject.setPrincipal(cashFlowList.stream().mapToLong(BudgetPlanPayDetailCashFlow::getPrincipal).sum());
                    budgetPlanCostDetailProject.setInterest(cashFlowList.stream().mapToLong(BudgetPlanPayDetailCashFlow::getInterest).sum());
                    budgetPlanCostDetailProject.setRent(budgetPlanCostDetailProject.getPrincipal() + budgetPlanCostDetailProject.getInterest());
                }
                if (!budgetPlanPayDetail.getPlanPayDate().isBefore(budgetPlanCost.getBudgetDateFrom()) && !budgetPlanPayDetail.getPlanPayDate().isAfter(budgetPlanCost.getBudgetDateTo())) {
                    budgetPlanCostDetailProject.setDataCategory(BudgetPlanDataCategoryEnum.FUTURE.name());
                    if (targetDate.getYear() == budgetPlanPayDetailPrice.getPayDate().getYear() && targetDate.getMonthValue() == budgetPlanPayDetailPrice.getPayDate().getMonthValue()) {
                        // 如果和投放日一致则需要填充投放、新增保证金、新增咨询服务费
                        budgetPlanCostDetailProject.setPayAmount(budgetPlanPayDetailPrice.getProjectAmount());
                        BigDecimal depositBD = BigDecimal.valueOf(budgetPlanPayDetailPrice.getProjectAmount()).multiply(BigDecimal.valueOf(budgetPlanPayDetailPrice.getDepositRate()).divide(BigDecimal.valueOf(1000000), 20, RoundingMode.HALF_UP));
                        BigDecimal consultingFeeBD = BigDecimal.valueOf(budgetPlanPayDetailPrice.getProjectAmount()).multiply(BigDecimal.valueOf(budgetPlanPayDetailPrice.getConsultingFeeRate()).divide(BigDecimal.valueOf(1000000), 20, RoundingMode.HALF_UP));
                        budgetPlanCostDetailProject.setDeposit(Util.mithrasLongDecimalTwo(depositBD.longValue()));
                        budgetPlanCostDetailProject.setConsultingFee(Util.mithrasLongDecimalTwo(consultingFeeBD.longValue()));
                    }
                } else {
                    budgetPlanCostDetailProject.setDataCategory(BudgetPlanDataCategoryEnum.HISTORY.name());
                    // 找到最后一期租金日，填充退回保证金
                    if (CollectionUtil.isNotEmpty(budgetPlanPayDetailCashFlowList)) {
                        LocalDate lastRentDate = budgetPlanPayDetailCashFlowList.get(budgetPlanPayDetailCashFlowList.size() - 1).getCashFlowDate();
                        if (targetDate.getYear() == lastRentDate.getYear() && targetDate.getMonthValue() == lastRentDate.getMonthValue()) {
                            BigDecimal depositBD = BigDecimal.valueOf(budgetPlanPayDetailPrice.getProjectAmount()).multiply(BigDecimal.valueOf(budgetPlanPayDetailPrice.getDepositRate()).divide(BigDecimal.valueOf(1000000), 20, RoundingMode.HALF_UP));
                            budgetPlanCostDetailProject.setDeposit(Util.mithrasLongDecimalTwo(depositBD.longValue()));
                        }
                    }
                }
                result.add(budgetPlanCostDetailProject);
                targetDate = targetDate.plusMonths(1);
            }
        }
        return result;
    }

    private List<BudgetPlanCostDetailFund> calculateByFundReceipt(BudgetPlanCost budgetPlanCost) {
        // 取借款日在预算区间开始日期之前的起息的融资
        List<FundReceiptRepayBaseInfo> targetList = new LinkedList<>();
        // 间融
        List<FundFinancingBaseInfo> fundFinancingBaseInfoList = SpringUtil.getBean(FundFinancingBaseInfoService.class).list(
                Wrappers.<FundFinancingBaseInfo>lambdaQuery()
                        .eq(FundFinancingBaseInfo::getFinancingStatus, FundFinancingStatusEnum.CARRY_INTEREST.name())
                        .lt(FundFinancingBaseInfo::getActualLoanDate, budgetPlanCost.getBudgetDateFrom())
        );
        Set<Long> indirectFinancingIds = fundFinancingBaseInfoList.stream().map(FundFinancingBaseInfo::getId).collect(Collectors.toSet());
        if (CollectionUtil.isNotEmpty(fundFinancingBaseInfoList)) {
            List<FundReceiptRepayBaseInfo> receiptRepayBaseInfoList = SpringUtil.getBean(FundReceiptRepayBaseInfoService.class).list(
                    Wrappers.<FundReceiptRepayBaseInfo>lambdaQuery()
                            .in(FundReceiptRepayBaseInfo::getFinancingId, indirectFinancingIds)
                            .isNull(FundReceiptRepayBaseInfo::getFinancingType)
            );
            if (CollectionUtil.isNotEmpty(receiptRepayBaseInfoList)) {
                targetList.addAll(receiptRepayBaseInfoList);
            }
        }
        // 直融
        List<FundDirectFinancingBaseInfo> fundDirectFinancingBaseInfoList = SpringUtil.getBean(FundDirectFinancingBaseInfoService.class).list(
                Wrappers.<FundDirectFinancingBaseInfo>lambdaQuery()
                        .eq(FundDirectFinancingBaseInfo::getFinancingStatus, FundFinancingStatusEnum.CARRY_INTEREST.name())
                        .lt(FundDirectFinancingBaseInfo::getCarryInterestTime, budgetPlanCost.getBudgetDateFrom())
        );
        Set<Long> directFinancingIds = fundDirectFinancingBaseInfoList.stream().map(FundDirectFinancingBaseInfo::getId).collect(Collectors.toSet());
        if (CollectionUtil.isNotEmpty(fundDirectFinancingBaseInfoList)) {
            List<FundReceiptRepayBaseInfo> receiptRepayBaseInfoList = SpringUtil.getBean(FundReceiptRepayBaseInfoService.class).list(
                    Wrappers.<FundReceiptRepayBaseInfo>lambdaQuery()
                            .in(FundReceiptRepayBaseInfo::getFinancingId, directFinancingIds)
                            .eq(FundReceiptRepayBaseInfo::getFinancingType, "DIRECT")
            );
            if (CollectionUtil.isNotEmpty(receiptRepayBaseInfoList)) {
                targetList.addAll(receiptRepayBaseInfoList);
            }
        }
        if (CollectionUtil.isEmpty(targetList)) {
            return Collections.emptyList();
        }
        Set<Long> receiptRepayIds = targetList.stream().map(FundReceiptRepayBaseInfo::getId).collect(Collectors.toSet());
        // 找还款计划
        List<FundFinancingRepayActual> planIndirectList = SpringUtil.getBean(FundFinancingRepayActualService.class).list(
                Wrappers.<FundFinancingRepayActual>lambdaQuery()
                        .in(FundFinancingRepayActual::getFinancingId, indirectFinancingIds)
                        .ge(FundFinancingRepayActual::getRepayDate, budgetPlanCost.getBudgetDateFrom())
                        .le(FundFinancingRepayActual::getRepayDate, budgetPlanCost.getBudgetDateTo())
                        .gt(FundFinancingRepayActual::getPhase, 0)
        );
        List<FundDirectFinancingRepayActual> planDirectList = SpringUtil.getBean(FundDirectFinancingRepayActualService.class).list(
                Wrappers.<FundDirectFinancingRepayActual>lambdaQuery()
                        .in(FundDirectFinancingRepayActual::getFinancingId, directFinancingIds)
                        .ge(FundDirectFinancingRepayActual::getRepayDate, budgetPlanCost.getBudgetDateFrom())
                        .le(FundDirectFinancingRepayActual::getRepayDate, budgetPlanCost.getBudgetDateTo())
                        .gt(FundDirectFinancingRepayActual::getPhase, 0)
        );
        // 找核销明细
        List<FundReceiptFlowDetail> actualList = SpringUtil.getBean(FundReceiptFlowDetailService.class).list(
                Wrappers.<FundReceiptFlowDetail>lambdaQuery()
                        .in(FundReceiptFlowDetail::getReceiptRepayId, receiptRepayIds)
                        .ge(FundReceiptFlowDetail::getCashFlowDate, budgetPlanCost.getBudgetDateFrom())
                        .le(FundReceiptFlowDetail::getCashFlowDate, budgetPlanCost.getBudgetDateTo())
                        .eq(FundReceiptFlowDetail::getCashFlowItem, FinanceCashFlowItemEnum.REPAY.name())
        );
        Map<String, List<FundReceiptFlowDetail>> actualMap = actualList.stream().collect(Collectors.groupingBy(FundReceiptFlowDetail::getCashFlowCode));
        // 合并现金流，有核销明细用核销明细，没有的用计划
        List<FundCashFlowBO> mergeList = new LinkedList<>();
        if (CollectionUtil.isNotEmpty(planIndirectList)) {
            for (FundFinancingRepayActual fundFinancingRepayActual : planIndirectList) {
                FundCashFlowBO fundCashFlowBO = FundCashFlowBO.copyFromFundFinancingRepayActual(fundFinancingRepayActual);
                List<FundReceiptFlowDetail> detailList = actualMap.get(fundFinancingRepayActual.getCashFlowCode());
                if (CollectionUtil.isNotEmpty(detailList)) {
                    fundCashFlowBO.setPrincipal(detailList.stream().filter(e -> Objects.nonNull(e.getPrincipalAmount())).mapToLong(FundReceiptFlowDetail::getPrincipalAmount).sum());
                    fundCashFlowBO.setInterest(detailList.stream().filter(e -> Objects.nonNull(e.getInterestAmount())).mapToLong(FundReceiptFlowDetail::getInterestAmount).sum());
                }
                mergeList.add(fundCashFlowBO);
            }
        }
        if (CollectionUtil.isNotEmpty(planDirectList)) {
            for (FundDirectFinancingRepayActual fundDirectFinancingRepayActual : planDirectList) {
                FundCashFlowBO fundCashFlowBO = FundCashFlowBO.copyFromFundDirectFinancingRepayActual(fundDirectFinancingRepayActual);
                List<FundReceiptFlowDetail> detailList = actualMap.get(fundDirectFinancingRepayActual.getCashFlowCode());
                if (CollectionUtil.isNotEmpty(detailList)) {
                    fundCashFlowBO.setPrincipal(detailList.stream().filter(e -> Objects.nonNull(e.getPrincipalAmount())).mapToLong(FundReceiptFlowDetail::getPrincipalAmount).sum());
                    fundCashFlowBO.setInterest(detailList.stream().filter(e -> Objects.nonNull(e.getInterestAmount())).mapToLong(FundReceiptFlowDetail::getInterestAmount).sum());
                }
                mergeList.add(fundCashFlowBO);
            }
        }
        // 按照月份分组
        Map<String, List<FundCashFlowBO>> mergeMap = mergeList.stream().collect(Collectors.groupingBy(e -> LocalDateTimeUtil.format(e.getCashFlowDate(), DatePattern.NORM_MONTH_PATTERN)));
        // 预算区间每个月份一条数据
        List<BudgetPlanCostDetailFund> result = new LinkedList<>();
        LocalDate targetDate = budgetPlanCost.getBudgetDateFrom();
        while (!targetDate.isAfter(budgetPlanCost.getBudgetDateTo())) {
            List<FundCashFlowBO> pList = mergeMap.get(LocalDateTimeUtil.format(targetDate, DatePattern.NORM_MONTH_PATTERN));
            if (CollectionUtil.isNotEmpty(pList)) {
                Map<String, List<FundCashFlowBO>> pMap = pList.stream().collect(Collectors.groupingBy(e -> e.getFinancingType() + "-" + e.getFinancingId()));
                for (Map.Entry<String, List<FundCashFlowBO>> entry : pMap.entrySet()) {
                    BudgetPlanCostDetailFund budgetPlanCostDetailFund = new BudgetPlanCostDetailFund();
                    budgetPlanCostDetailFund.setBudgetPlanId(budgetPlanCost.getBudgetPlanId());
                    budgetPlanCostDetailFund.setBudgetPlanCostId(budgetPlanCost.getId());
                    // 目前资金端只有存量数据
                    String[] array = entry.getKey().split("-");
                    budgetPlanCostDetailFund.setDataCategory(BudgetPlanDataCategoryEnum.HISTORY.name());
                    budgetPlanCostDetailFund.setYear(targetDate.getYear());
                    budgetPlanCostDetailFund.setMonth(targetDate.getMonthValue());
                    budgetPlanCostDetailFund.setFinancingType(array[0]);
                    budgetPlanCostDetailFund.setFinancingId(Long.parseLong(array[1]));
                    budgetPlanCostDetailFund.setPrincipal(entry.getValue().stream().filter(e -> Objects.nonNull(e.getPrincipal())).mapToLong(FundCashFlowBO::getPrincipal).sum());
                    budgetPlanCostDetailFund.setInterest(entry.getValue().stream().filter(e -> Objects.nonNull(e.getInterest())).mapToLong(FundCashFlowBO::getInterest).sum());
                    budgetPlanCostDetailFund.setRepayAmount(budgetPlanCostDetailFund.getPrincipal() + budgetPlanCostDetailFund.getInterest());
                    result.add(budgetPlanCostDetailFund);
                }
            }
            targetDate = targetDate.plusMonths(1);
        }
        return result;
    }

}
