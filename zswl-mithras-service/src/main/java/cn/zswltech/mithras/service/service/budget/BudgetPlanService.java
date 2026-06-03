package cn.zswltech.mithras.service.service.budget;

import cn.zswltech.mithras.budget.application.BudgetPlanCostDetailFundService;
import cn.zswltech.mithras.budget.application.BudgetPlanCostDetailProjectService;
import cn.zswltech.mithras.budget.application.BudgetPlanPayDetailExpenseService;
import cn.zswltech.mithras.budget.application.BudgetPlanPayDetailPriceService;
import cn.zswltech.mithras.budget.application.BudgetPlanPayProcessInfoService;
import cn.zswltech.mithras.budget.domain.bo.BudgetEclRiskReserveBO;
import cn.zswltech.mithras.budget.domain.bo.BudgetPlanStatisticsBO;
import javax.annotation.Resource;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.flow.core.api.FlowTaskApiService;
import cn.zswltech.flow.core.domain.req.task.ProcessPageReq;
import cn.zswltech.flow.core.domain.resp.ProcessResp;
import cn.zswltech.flow.core.enums.ProcessBusinessStatusEnum;
import cn.zswltech.flow.core.util.Page;
import cn.zswltech.mithras.dto.flow.execution.ExecutionProcessBaseREQ;
import cn.zswltech.mithras.service.enums.ProcessModelTypeEnum;
import cn.zswltech.mithras.service.enums.YesOrNoNumberEnum;
import cn.zswltech.mithras.budget.domain.enums.BudgetStatusEnum;
import cn.zswltech.mithras.budget.infrastructure.persistence.mapper.model.BudgetPlanProfit;
import cn.zswltech.mithras.service.service.flow.ExecutionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.budget.infrastructure.persistence.mapper.BudgetPlanMapper;
import cn.zswltech.mithras.budget.infrastructure.persistence.mapper.model.BudgetPlan;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Objects;

/**
* @description 预算管理-预算计划
* @author vico
* @date 2025-04-11
*/
@Service
public class BudgetPlanService extends ServiceImpl<BudgetPlanMapper, BudgetPlan> {
    public BudgetPlan findByAdjustPlanId(Long planId) {
        LambdaQueryWrapper<BudgetPlan> query = Wrappers.lambdaQuery();
        query.eq(BudgetPlan::getAdjustPlanId, planId);
        return this.getOne(query);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void confirm(Long id) {
        BudgetPlan budgetPlan = this.getById(id);
        if (Objects.isNull(budgetPlan)) {
            throw new MithrasException("预算计划主数据不存在");
        }
        if (!Objects.equals(budgetPlan.getNeedCollect(), YesOrNoNumberEnum.NO.getCode())) {
            throw new MithrasException("仅<是否收集>为<否>时允许该操作");
        }
        // 主表确认
        BudgetPlan update = new BudgetPlan();
        update.setId(budgetPlan.getId());
        update.setBudgetStatus(BudgetStatusEnum.CONFIRM.name());
        this.updateById(update);
        // 利润预算确认
        SpringUtil.getBean(BudgetPlanProfitService.class).confirmByBudgetPlanId(budgetPlan.getId());
        // 成本预算确认
        SpringUtil.getBean(BudgetPlanCostService.class).confirmByBudgetPlanId(budgetPlan.getId());
        // 投放计划确认
        SpringUtil.getBean(BudgetPlanPayService.class).confirmByBudgetPlanId(budgetPlan.getId());
    }

    @Transactional(rollbackFor = Throwable.class)
    public void delete(Long id) {
        BudgetPlan budgetPlan = this.getById(id);
        if (Objects.isNull(budgetPlan)) {
            throw new MithrasException("预算计划主数据不存在");
        }
        if (Objects.equals(budgetPlan.getBudgetStatus(), BudgetStatusEnum.CONFIRM.name())) {
            throw new MithrasException("已确认的预算计划不允许删除");
        }
        // 删除主表数据
        this.removeById(id);
        // 删除利润预算数据
        SpringUtil.getBean(BudgetPlanProfitService.class).deleteByBudgetPlanId(id);
        // 删除成本预算数据
        SpringUtil.getBean(BudgetPlanCostService.class).deleteByBudgetPlanId(id);
        // 删除投放计划数据
        SpringUtil.getBean(BudgetPlanPayService.class).deleteByBudgetPlanId(id);
    }
}