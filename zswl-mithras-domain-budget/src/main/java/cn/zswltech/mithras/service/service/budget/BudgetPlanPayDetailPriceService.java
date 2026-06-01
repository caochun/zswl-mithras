package cn.zswltech.mithras.service.service.budget;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.zswltech.mithras.dto.budget.BudgetPlanPayDetailNotMonthPriceRSP;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import cn.zswltech.mithras.service.mapper.budget.BudgetPlanPayDetailPriceMapper;
import cn.zswltech.mithras.service.mapper.model.budget.BudgetPlanPayDetailPrice;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
* @description 预算管理-预算计划-投放计划（非月度）-明细-报价方案
* @author vico
* @date 2025-04-11
*/
@Slf4j
@Service
public class BudgetPlanPayDetailPriceService extends ServiceImpl<BudgetPlanPayDetailPriceMapper, BudgetPlanPayDetailPrice> {
    public void copyByDetailId(Long oldDetailId, Long newDetailId) {
        LambdaQueryWrapper<BudgetPlanPayDetailPrice> query = Wrappers.lambdaQuery();
        query.eq(BudgetPlanPayDetailPrice::getBudgetPlanPayDetailId, oldDetailId);
        BudgetPlanPayDetailPrice budgetPlanPayDetailPrice = this.getOne(query);
        if (Objects.isNull(budgetPlanPayDetailPrice)) {
            return;
        }
        BudgetPlanPayDetailPrice copy = BeanUtil.copyProperties(budgetPlanPayDetailPrice, BudgetPlanPayDetailPrice.class);
        copy.reset();
        copy.setBudgetPlanPayDetailId(newDetailId);
        this.save(copy);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void deleteByBudgetPlanId(Long budgetPlanId) {
        LambdaQueryWrapper<BudgetPlanPayDetailPrice> query = Wrappers.lambdaQuery();
        query.eq(BudgetPlanPayDetailPrice::getBudgetPlanId, budgetPlanId);
        this.remove(query);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void deleteByBudgetPlanPayDetailIds(Collection<Long> budgetPlanPayDetailIds) {
        LambdaQueryWrapper<BudgetPlanPayDetailPrice> query = Wrappers.lambdaQuery();
        query.in(BudgetPlanPayDetailPrice::getBudgetPlanPayDetailId, budgetPlanPayDetailIds);
        this.remove(query);
    }

    public BudgetPlanPayDetailNotMonthPriceRSP getByDetailId(Long budgetPlanPayDetailId) {
        BudgetPlanPayDetailPrice budgetPlanPayDetailPrice = this.getOneByDetailId(budgetPlanPayDetailId);
        return BeanUtil.copyProperties(budgetPlanPayDetailPrice, BudgetPlanPayDetailNotMonthPriceRSP.class);
    }

    public BudgetPlanPayDetailPrice getOneByDetailId(Long budgetPlanPayDetailId) {
        LambdaQueryWrapper<BudgetPlanPayDetailPrice> query = Wrappers.lambdaQuery();
        query.eq(BudgetPlanPayDetailPrice::getBudgetPlanPayDetailId, budgetPlanPayDetailId);
        return this.getOne(query);
    }

    public Map<Long, BudgetPlanPayDetailPrice> getPriceMapByDetailIds(Collection<Long> budgetPlanPayDetailIds) {
        if (CollectionUtil.isEmpty(budgetPlanPayDetailIds)) {
            return Collections.emptyMap();
        }
        LambdaQueryWrapper<BudgetPlanPayDetailPrice> query = Wrappers.lambdaQuery();
        query.in(BudgetPlanPayDetailPrice::getBudgetPlanPayDetailId, budgetPlanPayDetailIds);
        return this.list(query).stream().collect(Collectors.toMap(BudgetPlanPayDetailPrice::getBudgetPlanPayDetailId, e -> e));
    }
}