package cn.zswltech.mithras.application.orchestration.fund.receiptrepay;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.capital.enums.FinanceCashFlowItemEnum;
import cn.zswltech.mithras.fund.application.receiptrepay.FundReceiptRepayCashDepositService;
import cn.zswltech.mithras.fund.application.receiptrepay.FundReceiptRepayExpenseService;
import cn.zswltech.mithras.fund.directfinancing.mapper.model.FundDirectFinancingRepayActual;
import cn.zswltech.mithras.application.orchestration.fund.direct.service.FundDirectFinancingRepayActualService;
import cn.zswltech.mithras.fund.mapper.receiptrepay.FundReceiptFlowPlanMapper;
import cn.zswltech.mithras.fund.mapper.model.financing.FundFinancingBaseInfo;
import cn.zswltech.mithras.fund.mapper.model.receiptrepay.*;
import cn.zswltech.mithras.foundation.exception.MithrasException;
import cn.zswltech.mithras.application.orchestration.fund.financing.FundFinancingBaseInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author dingqi
 * @date 2024/8/19
 * @description
 */
@Slf4j
@Service
public class FundReceiptFlowPlanService extends ServiceImpl<FundReceiptFlowPlanMapper, FundReceiptFlowPlan> {
    @Resource
    private FundReceiptRepayBaseInfoService fundReceiptRepayBaseInfoService;
    @Resource
    private FundReceiptRepayBorrowingService fundReceiptRepayBorrowingService;
    @Resource
    private FundReceiptRepayCashFlowService fundReceiptRepayCashFlowService;
    @Resource
    private FundReceiptRepayCashDepositService fundReceiptRepayCashDepositService;
    @Resource
    private FundReceiptRepayExpenseService fundReceiptRepayExpenseService;

    public void removeByReceiptRepayId(Long receiptRepayId, Collection<String> cashFlowItems) {
        LambdaQueryWrapper<FundReceiptFlowPlan> query = Wrappers.lambdaQuery();
        query.eq(FundReceiptFlowPlan::getReceiptRepayId, receiptRepayId);
        if (CollectionUtil.isNotEmpty(cashFlowItems)) {
            query.in(FundReceiptFlowPlan::getCashFlowItem, cashFlowItems);
        }
        this.remove(query);
    }

    public void writeOff(String cashFlowCode, String writeOffState) {
        LambdaQueryWrapper<FundReceiptFlowPlan> query = Wrappers.lambdaQuery();
        query.eq(FundReceiptFlowPlan::getCashFlowCode, cashFlowCode);
        List<FundReceiptFlowPlan> todoList = this.list(query);
        if (CollectionUtil.isEmpty(todoList)) {
            log.error("没有在fund_receipt_flow_plan找到现金流编号为{}的现金流", cashFlowCode);
            throw new MithrasException("数据存在异常");
        }
        if (todoList.size() > 1) {
            log.error("在fund_receipt_flow_plan找到多条现金流编号为{}的现金流", cashFlowCode);
            throw new MithrasException("数据存在异常");
        }
        FundReceiptFlowPlan toUpdate = new FundReceiptFlowPlan();
        toUpdate.setId(todoList.get(0).getId());
        toUpdate.setWriteOffState(writeOffState);
        this.updateById(toUpdate);
    }

    @Transactional(rollbackFor = Exception.class)
    public void syncFromBorrowing(Long receiptRepayId) {
        // 移除老的
        this.removeByReceiptRepayId(receiptRepayId, FinanceCashFlowItemEnum.cashFlowItemBorrow());
        // 插入新的
        FundReceiptRepayBorrowing fundReceiptRepayBorrowing = fundReceiptRepayBorrowingService.getOneByReceiptRepayId(receiptRepayId);
        if (Objects.isNull(fundReceiptRepayBorrowing)) {
            return;
        }
        FundReceiptFlowPlan fundReceiptFlowPlan = new FundReceiptFlowPlan();
        fundReceiptFlowPlan.setReceiptRepayId(receiptRepayId);
        fundReceiptFlowPlan.setCashFlowCode(fundReceiptRepayBorrowing.getCashFlowCode());
        fundReceiptFlowPlan.setCashFlowDate(fundReceiptRepayBorrowing.getActualLoanDate());
        fundReceiptFlowPlan.setCashFlowItem(FinanceCashFlowItemEnum.FINANCE_FUND.name());
        fundReceiptFlowPlan.setCashFlowPhase(0);
        fundReceiptFlowPlan.setTotalAmount(fundReceiptRepayBorrowing.getPrincipal());
        fundReceiptFlowPlan.setWriteOffState(fundReceiptRepayBorrowing.getWriteOffState());
        this.save(fundReceiptFlowPlan);
    }

    public void syncFromRepay(Long receiptRepayId) {
        // 移除老的
        this.removeByReceiptRepayId(receiptRepayId, FinanceCashFlowItemEnum.cashFlowItemRepay());
        // 插入新的
        List<FundReceiptRepayCashFlow> cashFlowList = fundReceiptRepayCashFlowService.listByReceiptRepayId(receiptRepayId, null).orElse(Collections.emptyList());
        if (CollectionUtil.isEmpty(cashFlowList)) {
            return;
        }
        List<FundReceiptFlowPlan> toSaveList = cashFlowList.stream()
                .map(e -> {
                    FundReceiptFlowPlan fundReceiptFlowPlan = new FundReceiptFlowPlan();
                    fundReceiptFlowPlan.setReceiptRepayId(receiptRepayId);
                    fundReceiptFlowPlan.setCashFlowCode(e.getCashFlowCode());
                    fundReceiptFlowPlan.setCashFlowDate(e.getRepayDate());
                    fundReceiptFlowPlan.setCashFlowItem(FinanceCashFlowItemEnum.REPAY.name());
                    fundReceiptFlowPlan.setCashFlowPhase(e.getPhase());
                    fundReceiptFlowPlan.setTotalAmount(Optional.ofNullable(e.getRepayAmount()).orElse(0L));
                    fundReceiptFlowPlan.setPrincipalAmount(Optional.ofNullable(e.getPrincipleAmount()).orElse(0L));
                    fundReceiptFlowPlan.setInterestAmount(Optional.ofNullable(e.getInterestAmount()).orElse(0L));
                    fundReceiptFlowPlan.setWriteOffState(e.getWriteOffState());
                    return fundReceiptFlowPlan;
                })
                .sorted(Comparator.comparing(FundReceiptFlowPlan::getCashFlowPhase))
                .collect(Collectors.toList());
        this.saveBatch(toSaveList);
    }

    public void syncFromDeposit(Long receiptRepayId) {
        // 移除老的
        this.removeByReceiptRepayId(receiptRepayId, FinanceCashFlowItemEnum.cashFlowItemDeposit());
        // 插入新的
        List<FundReceiptRepayCashDeposit> depositList = fundReceiptRepayCashDepositService.listByReceiptRepayIds(Collections.singletonList(receiptRepayId));
        if (CollectionUtil.isEmpty(depositList)) {
            return;
        }
        //这个去现金流日期 如何判断收支！！！
        LocalDate cashFlowDate = this.ensureLoanDate(receiptRepayId);
        List<FundReceiptFlowPlan> toSaveList = depositList.stream()
                .map(e -> {
                    FundReceiptFlowPlan fundReceiptFlowPlan = new FundReceiptFlowPlan();
                    fundReceiptFlowPlan.setReceiptRepayId(receiptRepayId);
                    fundReceiptFlowPlan.setCashFlowCode(e.getCashFlowCode());
                    fundReceiptFlowPlan.setCashFlowDate(cashFlowDate);
                    fundReceiptFlowPlan.setCashFlowItem(e.getDepositCashFlowType());
                    fundReceiptFlowPlan.setCashFlowPhase(0);
                    fundReceiptFlowPlan.setTotalAmount(Optional.ofNullable(e.getAmount()).orElse(0L));
                    fundReceiptFlowPlan.setWriteOffState(e.getWriteOffState());
                    return fundReceiptFlowPlan;
                })
                .sorted(Comparator.comparing(FundReceiptFlowPlan::getCashFlowPhase))
                .collect(Collectors.toList());
        this.saveBatch(toSaveList);
    }

    public void syncFromExpense(Long receiptRepayId) {
        // 移除老的
        this.removeByReceiptRepayId(receiptRepayId, FinanceCashFlowItemEnum.cashFlowItemExpense());
        // 插入新的
        List<FundReceiptRepayExpense> expenseList = fundReceiptRepayExpenseService.listByReceiptRepayIds(Collections.singletonList(receiptRepayId));
        if (CollectionUtil.isEmpty(expenseList)) {
            return;
        }
        LocalDate cashFlowDate = this.ensureLoanDate(receiptRepayId);
        List<FundReceiptFlowPlan> toSaveList = expenseList.stream()
                .map(e -> {
                    FundReceiptFlowPlan fundReceiptFlowPlan = new FundReceiptFlowPlan();
                    fundReceiptFlowPlan.setReceiptRepayId(receiptRepayId);
                    fundReceiptFlowPlan.setCashFlowCode(e.getCashFlowCode());
                    fundReceiptFlowPlan.setCashFlowDate(cashFlowDate);
                    fundReceiptFlowPlan.setCashFlowItem(e.getExpenseType());
                    fundReceiptFlowPlan.setCashFlowPhase(0);
                    fundReceiptFlowPlan.setTotalAmount(Optional.ofNullable(e.getTotalAmount()).orElse(0L));
                    fundReceiptFlowPlan.setWriteOffState(e.getWriteOffState());
                    return fundReceiptFlowPlan;
                })
                .sorted(Comparator.comparing(FundReceiptFlowPlan::getCashFlowPhase))
                .collect(Collectors.toList());
        this.saveBatch(toSaveList);
    }

    private LocalDate ensureLoanDate(Long receiptRepayId) {
        FundReceiptRepayBaseInfo fundReceiptRepayBaseInfo = fundReceiptRepayBaseInfoService.getById(receiptRepayId);
        if (Objects.isNull(fundReceiptRepayBaseInfo)) {
            return null;
        }
        if (Objects.isNull(fundReceiptRepayBaseInfo.getFinancingType())) {
            FundFinancingBaseInfo financingBaseInfo = SpringUtil.getBean(FundFinancingBaseInfoService.class).getById(fundReceiptRepayBaseInfo.getFinancingId());
            return Objects.nonNull(financingBaseInfo.getActualLoanDate()) ? financingBaseInfo.getActualLoanDate() : financingBaseInfo.getPlanLoanDate();
        } else {
            // TODO 直融现在没有起息日，等后续增加字段后从新增字段取，目前先保持老逻辑，取实际还款表的第一期
            List<FundDirectFinancingRepayActual> repayActualList = SpringUtil.getBean(FundDirectFinancingRepayActualService.class).listByFinancingId(fundReceiptRepayBaseInfo.getFinancingId());
            if (CollectionUtil.isEmpty(repayActualList)) {
                return null;
            }
            return repayActualList.stream().filter(e -> e.getPhase() > 0).min(Comparator.comparing(FundDirectFinancingRepayActual::getPhase)).map(FundDirectFinancingRepayActual::getRepayDate).orElse(null);
        }
    }
}
