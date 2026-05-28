package cn.zswltech.mithras.service.service.budget;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.dto.budget.BudgetPlanPayDetailNotMonthCashFlowRSP;
import cn.zswltech.mithras.service.constant.GlobalConstants;
import cn.zswltech.mithras.service.excel.importer.CashFlowExcelImporter;
import cn.zswltech.mithras.service.excel.model.CashFlowExcelModel;
import cn.zswltech.mithras.service.others.MithrasException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import cn.zswltech.mithras.service.mapper.budget.BudgetPlanPayDetailCashFlowMapper;
import cn.zswltech.mithras.service.mapper.model.budget.BudgetPlanPayDetailCashFlow;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
* @description 预算管理-预算计划-投放计划（非月度）-明细-现金流计划
* @author vico
* @date 2025-04-11
*/
@Slf4j
@Service
public class BudgetPlanPayDetailCashFlowService extends ServiceImpl<BudgetPlanPayDetailCashFlowMapper, BudgetPlanPayDetailCashFlow> {
    @Transactional(rollbackFor = Throwable.class)
    public void copyByDetailId(Long oldDetailId, Long newDetailId) {
        LambdaQueryWrapper<BudgetPlanPayDetailCashFlow> query = Wrappers.lambdaQuery();
        query.eq(BudgetPlanPayDetailCashFlow::getBudgetPlanPayDetailId, oldDetailId);
        List<BudgetPlanPayDetailCashFlow> list = this.list(query);
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        List<BudgetPlanPayDetailCashFlow> copyList = BeanUtil.copyToList(list, BudgetPlanPayDetailCashFlow.class);
        copyList.forEach(e -> {
            e.reset();
            e.setBudgetPlanPayDetailId(newDetailId);
        });
        this.saveBatch(copyList);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void deleteByBudgetPlanId(Long budgetPlanId) {
        LambdaQueryWrapper<BudgetPlanPayDetailCashFlow> query = Wrappers.lambdaQuery();
        query.eq(BudgetPlanPayDetailCashFlow::getBudgetPlanId, budgetPlanId);
        this.remove(query);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void deleteByBudgetPlanPayDetailIds(Collection<Long> budgetPlanPayDetailIds) {
        LambdaQueryWrapper<BudgetPlanPayDetailCashFlow> query = Wrappers.lambdaQuery();
        query.in(BudgetPlanPayDetailCashFlow::getBudgetPlanPayDetailId, budgetPlanPayDetailIds);
        this.remove(query);
    }

    public List<BudgetPlanPayDetailNotMonthCashFlowRSP> listCashFlowByDetailId(Long budgetPlanPayDetailId) {
        List<BudgetPlanPayDetailCashFlow> dbList = this.listByDetailId(budgetPlanPayDetailId);
        if (CollectionUtil.isEmpty(dbList)) {
            return Collections.emptyList();
        }
        return BeanUtil.copyToList(dbList, BudgetPlanPayDetailNotMonthCashFlowRSP.class);
    }

    public List<BudgetPlanPayDetailCashFlow> listByDetailId(Long budgetPlanPayDetailId) {
        LambdaQueryWrapper<BudgetPlanPayDetailCashFlow> query = Wrappers.lambdaQuery();
        query.eq(BudgetPlanPayDetailCashFlow::getBudgetPlanPayDetailId, budgetPlanPayDetailId);
        query.orderByAsc(BudgetPlanPayDetailCashFlow::getCashFlowPhase);
        query.orderByAsc(BudgetPlanPayDetailCashFlow::getCashFlowDate);
        return this.list(query);
    }

    @Transactional(rollbackFor = Throwable.class)
    public List<BudgetPlanPayDetailNotMonthCashFlowRSP> parseFromExcel(InputStream inputStream) {
        List<CashFlowExcelModel> dataList = SpringUtil.getBean(CashFlowExcelImporter.class).parse(inputStream);
        if (CollectionUtil.isEmpty(dataList)) {
            throw new MithrasException("没有从Excel中解析出数据");
        }
        dataList.sort(Comparator.comparing(CashFlowExcelModel::getCashFlowPhase));
        return dataList.stream().map(excelModel -> {
            BudgetPlanPayDetailNotMonthCashFlowRSP cashFlow = new BudgetPlanPayDetailNotMonthCashFlowRSP();
            cashFlow.setCashFlowPhase(excelModel.getCashFlowPhase());
            cashFlow.setCashFlowDate(excelModel.getCashFlowDate());
            cashFlow.setCashFlowAmount(Optional.ofNullable(excelModel.getCashFlowAmount()).map(e -> e.multiply(new BigDecimal(GlobalConstants.MONEY_MULTIPLE)).longValue()).orElse(0L));
            cashFlow.setRent(Optional.ofNullable(excelModel.getRent()).map(e -> e.multiply(new BigDecimal(GlobalConstants.MONEY_MULTIPLE)).longValue()).orElse(0L));
            cashFlow.setPrincipal(Optional.ofNullable(excelModel.getPrincipal()).map(e -> e.multiply(new BigDecimal(GlobalConstants.MONEY_MULTIPLE)).longValue()).orElse(0L));
            cashFlow.setInterest(Optional.ofNullable(excelModel.getInterest()).map(e -> e.multiply(new BigDecimal(GlobalConstants.MONEY_MULTIPLE)).longValue()).orElse(0L));
            cashFlow.setRemainingPrincipal(Optional.ofNullable(excelModel.getRemainingPrincipal()).map(e -> e.multiply(new BigDecimal(GlobalConstants.MONEY_MULTIPLE)).longValue()).orElse(0L));
            return cashFlow;
        }).collect(Collectors.toList());
    }
}