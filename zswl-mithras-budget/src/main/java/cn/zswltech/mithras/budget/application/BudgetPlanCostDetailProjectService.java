package cn.zswltech.mithras.budget.application;

import cn.zswltech.mithras.budget.infrastructure.persistence.mapper.BudgetPlanCostDetailProjectMapper;
import cn.zswltech.mithras.budget.infrastructure.persistence.mapper.model.BudgetPlanCostDetailProject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * @author dingqi
 * @date 2025/6/25
 * @description
 */
@Service
public class BudgetPlanCostDetailProjectService extends ServiceImpl<BudgetPlanCostDetailProjectMapper, BudgetPlanCostDetailProject> {
    @Transactional(rollbackFor = Throwable.class)
    public void deleteByBudgetPlanId(Long budgetPlanId) {
        LambdaQueryWrapper<BudgetPlanCostDetailProject> query = Wrappers.lambdaQuery();
        query.eq(BudgetPlanCostDetailProject::getBudgetPlanId, budgetPlanId);
        this.remove(query);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void deleteByBudgetPlanPayDetailIds(Collection<Long> budgetPlanPayDetailIds) {
        LambdaQueryWrapper<BudgetPlanCostDetailProject> query = Wrappers.lambdaQuery();
        query.in(BudgetPlanCostDetailProject::getBudgetPlanPayDetailId, budgetPlanPayDetailIds);
        this.remove(query);
    }
}
