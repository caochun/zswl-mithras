package cn.zswltech.mithras.budget.application;

import cn.zswltech.mithras.budget.infrastructure.persistence.mapper.BudgetPlanCostDetailFundMapper;
import cn.zswltech.mithras.budget.infrastructure.persistence.mapper.model.BudgetPlanCostDetailFund;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author dingqi
 * @date 2025/6/25
 * @description
 */
@Service
public class BudgetPlanCostDetailFundService extends ServiceImpl<BudgetPlanCostDetailFundMapper, BudgetPlanCostDetailFund> {
    @Transactional(rollbackFor = Throwable.class)
    public void deleteByBudgetPlanId(Long budgetPlanId) {
        LambdaQueryWrapper<BudgetPlanCostDetailFund> query = Wrappers.lambdaQuery();
        query.eq(BudgetPlanCostDetailFund::getBudgetPlanId, budgetPlanId);
        this.remove(query);
    }
}
