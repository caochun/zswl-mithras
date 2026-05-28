package cn.zswltech.mithras.service.service.budget;

import cn.zswltech.mithras.service.mapper.budget.BudgetPlanPayProcessInfoMapper;
import cn.zswltech.mithras.service.mapper.model.budget.BudgetPlanPayProcessInfo;
import cn.zswltech.mithras.service.util.StringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author dingqi
 * @date 2025/6/13
 * @description
 */
@Service
public class BudgetPlanPayProcessInfoService extends ServiceImpl<BudgetPlanPayProcessInfoMapper, BudgetPlanPayProcessInfo> {
    public BudgetPlanPayProcessInfo findDeptLastOne(Long budgetPlanPayId, Long belongDeptId) {
        LambdaQueryWrapper<BudgetPlanPayProcessInfo> query = Wrappers.lambdaQuery();
        query.eq(BudgetPlanPayProcessInfo::getBudgetPlanPayId, budgetPlanPayId);
        query.eq(BudgetPlanPayProcessInfo::getBelongDeptId, belongDeptId);
        query.orderByDesc(BudgetPlanPayProcessInfo::getId);
        query.last(StringUtil.mysqlLimitOne());
        return this.getOne(query);
    }
}
