package cn.zswltech.mithras.budget.application;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.NumberUtil;
import cn.zswltech.mithras.dto.budget.BudgetPlanPayDetailDynamicTableRSP;
import cn.zswltech.mithras.budget.mapper.dto.BudgetPlanPayDetailExpenseGroupDTO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import cn.zswltech.mithras.budget.mapper.BudgetPlanPayDetailExpenseMapper;
import cn.zswltech.mithras.budget.mapper.model.BudgetPlanPayDetailExpense;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
* @description 预算管理-预算计划-投放计划（非月度）-明细-期间费用
* @author vico
* @date 2025-04-11
*/
@Service
public class BudgetPlanPayDetailExpenseService extends ServiceImpl<BudgetPlanPayDetailExpenseMapper, BudgetPlanPayDetailExpense> {
    @Transactional(rollbackFor = Throwable.class)
    public void copyByDetailId(Long oldDetailId, Long newDetailId) {
        LambdaQueryWrapper<BudgetPlanPayDetailExpense> query = Wrappers.lambdaQuery();
        query.eq(BudgetPlanPayDetailExpense::getBudgetPlanPayDetailId, oldDetailId);
        List<BudgetPlanPayDetailExpense> list = this.list(query);
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        List<BudgetPlanPayDetailExpense> copy = BeanUtil.copyToList(list, BudgetPlanPayDetailExpense.class);
        copy.forEach(e -> {
            e.reset();
            e.setBudgetPlanPayDetailId(newDetailId);
        });
        this.saveBatch(copy);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void deleteByBudgetPlanId(Long budgetPlanId) {
        LambdaQueryWrapper<BudgetPlanPayDetailExpense> query = Wrappers.lambdaQuery();
        query.eq(BudgetPlanPayDetailExpense::getBudgetPlanId, budgetPlanId);
        this.remove(query);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void deleteByBudgetPlanPayDetailIds(Collection<Long> budgetPlanPayDetailIds) {
        LambdaQueryWrapper<BudgetPlanPayDetailExpense> query = Wrappers.lambdaQuery();
        query.in(BudgetPlanPayDetailExpense::getBudgetPlanPayDetailId, budgetPlanPayDetailIds);
        this.remove(query);
    }

    public Map<Long, BudgetPlanPayDetailExpenseGroupDTO> getExpenseMapByDetailIds(Collection<Long> detailIds, LocalDate startDate, LocalDate endDate) {
        if (CollectionUtil.isEmpty(detailIds)) {
            return Collections.emptyMap();
        }
        List<BudgetPlanPayDetailExpenseGroupDTO> list = this.getBaseMapper().listExpenseGroupByDetail(detailIds, startDate, endDate);
        return list.stream().collect(Collectors.toMap(BudgetPlanPayDetailExpenseGroupDTO::getBudgetPlanPayDetailId, e -> e));
    }

    public BudgetPlanPayDetailDynamicTableRSP getDynamicTable(Long budgetPlanPayDetailId) {
        List<BudgetPlanPayDetailExpenseMapper.IncomeGroupDTO> groupResult = this.getBaseMapper().listGroupResult(budgetPlanPayDetailId);
        BudgetPlanPayDetailDynamicTableRSP rsp = new BudgetPlanPayDetailDynamicTableRSP();
        if (CollectionUtil.isEmpty(groupResult)) {
            rsp.setHeaderList(Collections.emptyList());
            rsp.setDataList(Collections.emptyList());
        } else {
            Map<Integer, List<BudgetPlanPayDetailExpenseMapper.IncomeGroupDTO>> map = groupResult.stream().collect(Collectors.groupingBy(BudgetPlanPayDetailExpenseMapper.IncomeGroupDTO::getExpenseDateYear));
            // 表头
            List<String> headerList = new LinkedList<>();
            headerList.add("");
            List<Integer> allYears = new LinkedList<>(map.keySet());
            allYears.sort(Comparator.comparing(e -> e));
            allYears.forEach(e -> headerList.add(e + "年"));
            // 数据行
            List<Object> valueAddedTaxYearTotalRow = new LinkedList<>();
            valueAddedTaxYearTotalRow.add("税金-增值税");
            List<Object> additionalTaxYearTotalRow = new LinkedList<>();
            additionalTaxYearTotalRow.add("税金-附加税");
            List<Object> stampTaxYearTotalRow = new LinkedList<>();
            stampTaxYearTotalRow.add("税金-印花税");
            List<Object> grossProfitYearTotalRow = new LinkedList<>();
            grossProfitYearTotalRow.add("毛利");
            List<Object> riskFundYearTotalRow = new LinkedList<>();
            riskFundYearTotalRow.add("风险准备金");
            List<Object> profitYearTotalRow = new LinkedList<>();
            profitYearTotalRow.add("考核利润");
            List<Object> expenseYearTotalRow = new LinkedList<>();
            expenseYearTotalRow.add("费用");
            List<Object> profitWithoutExpenseYearTotalRow = new LinkedList<>();
            profitWithoutExpenseYearTotalRow.add("扣费后考核利润");
            for (Integer year : allYears) {
                List<BudgetPlanPayDetailExpenseMapper.IncomeGroupDTO> list = map.get(year);
                if (Objects.isNull(list)) {
                    list = Collections.emptyList();
                }
                long valueAddedTaxYearTotal = list.stream().filter(e -> Objects.nonNull(e.getValueAddedTaxYearTotal())).mapToLong(BudgetPlanPayDetailExpenseMapper.IncomeGroupDTO::getValueAddedTaxYearTotal).sum();
                valueAddedTaxYearTotalRow.add(NumberUtil.decimalFormat("#,##0.00", BigDecimal.valueOf(valueAddedTaxYearTotal).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP)));
                long additionalTaxYearTotal = list.stream().filter(e -> Objects.nonNull(e.getAdditionalTaxYearTotal())).mapToLong(BudgetPlanPayDetailExpenseMapper.IncomeGroupDTO::getAdditionalTaxYearTotal).sum();
                additionalTaxYearTotalRow.add(NumberUtil.decimalFormat("#,##0.00", BigDecimal.valueOf(additionalTaxYearTotal).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP)));
                long stampTaxYearTotal = list.stream().filter(e -> Objects.nonNull(e.getStampTaxYearTotal())).mapToLong(BudgetPlanPayDetailExpenseMapper.IncomeGroupDTO::getStampTaxYearTotal).sum();
                stampTaxYearTotalRow.add(NumberUtil.decimalFormat("#,##0.00", BigDecimal.valueOf(stampTaxYearTotal).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP)));
                long grossProfitYearTotal = list.stream().filter(e -> Objects.nonNull(e.getGrossProfitYearTotal())).mapToLong(BudgetPlanPayDetailExpenseMapper.IncomeGroupDTO::getGrossProfitYearTotal).sum();
                grossProfitYearTotalRow.add(NumberUtil.decimalFormat("#,##0.00", BigDecimal.valueOf(grossProfitYearTotal).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP)));
                long assessmentProfitYearTotal = list.stream().filter(e -> Objects.nonNull(e.getProfitYearTotal())).mapToLong(BudgetPlanPayDetailExpenseMapper.IncomeGroupDTO::getProfitYearTotal).sum();
                profitYearTotalRow.add(NumberUtil.decimalFormat("#,##0.00", BigDecimal.valueOf(assessmentProfitYearTotal).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP)));
                long expenseYearTotal = list.stream().filter(e -> Objects.nonNull(e.getExpenseYearTotal())).mapToLong(BudgetPlanPayDetailExpenseMapper.IncomeGroupDTO::getExpenseYearTotal).sum();
                expenseYearTotalRow.add(NumberUtil.decimalFormat("#,##0.00", BigDecimal.valueOf(expenseYearTotal).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP)));
                long riskFundDiffTotal = list.stream().filter(e -> Objects.nonNull(e.getRiskFundDiffYearTotal())).mapToLong(BudgetPlanPayDetailExpenseMapper.IncomeGroupDTO::getRiskFundDiffYearTotal).sum();
                riskFundYearTotalRow.add(NumberUtil.decimalFormat("#,##0.00", BigDecimal.valueOf(riskFundDiffTotal).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP)));
                profitWithoutExpenseYearTotalRow.add(NumberUtil.decimalFormat("#,##0.00", BigDecimal.valueOf(assessmentProfitYearTotal - expenseYearTotal).divide(BigDecimal.valueOf(10000), 2, RoundingMode.HALF_UP)));
            }
            rsp.setHeaderList(headerList);
            rsp.setDataList(ListUtil.of(valueAddedTaxYearTotalRow, additionalTaxYearTotalRow, stampTaxYearTotalRow, grossProfitYearTotalRow, riskFundYearTotalRow, profitYearTotalRow, expenseYearTotalRow, profitWithoutExpenseYearTotalRow));
        }
        return rsp;
    }
}