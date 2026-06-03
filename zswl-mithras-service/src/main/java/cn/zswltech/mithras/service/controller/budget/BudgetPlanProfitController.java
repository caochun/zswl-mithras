package cn.zswltech.mithras.service.controller.budget;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.budget.*;
import cn.zswltech.mithras.budget.domain.enums.BudgetPlanCalculateStatusEnum;
import cn.zswltech.mithras.budget.infrastructure.persistence.mapper.model.BudgetPlanCost;
import cn.zswltech.mithras.budget.infrastructure.persistence.mapper.model.BudgetPlanPay;
import cn.zswltech.mithras.budget.infrastructure.persistence.mapper.model.BudgetPlanProfit;
import cn.zswltech.mithras.service.service.budget.BudgetPlanCostService;
import cn.zswltech.mithras.service.service.budget.BudgetPlanPayService;
import cn.zswltech.mithras.service.service.budget.BudgetPlanProfitDetailService;
import cn.zswltech.mithras.service.util.ThreadPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.List;

import cn.zswltech.mithras.api.budget.BudgetPlanProfitApi;
import cn.zswltech.mithras.service.service.budget.BudgetPlanProfitService;

/**
* @description 预算管理-预算计划-利润预算
* @author vico
* @date 2025-04-11
*/
@Slf4j
@RestController
public class BudgetPlanProfitController implements BudgetPlanProfitApi {
    @Resource
    private BudgetPlanProfitService budgetPlanProfitService;
    @Resource
    private BudgetPlanProfitDetailService budgetPlanProfitDetailService;
    @Resource
    private BudgetPlanPayService budgetPlanPayService;
    @Resource
    private BudgetPlanCostService budgetPlanCostService;

    @Override
    public R<Long> create(BudgetPlanProfitCreateREQ req) {
        Long budgetPlanProfitId = budgetPlanProfitService.create(req);
        // 异步逻辑放在这里是为了等内层事务全部提交后执行，规避事务可见性问题。也可以使用事务管理器注册提交完成事件的回调方法来达到相同效果
        // 异步初始化利润预算明细
        ThreadPoolUtil.getCommonPool().execute(() -> {
            try {
                budgetPlanProfitService.initProfitDetail(budgetPlanProfitId);
            } catch (Exception e) {
                log.error("预算管理-异步计算利润明细发生异常[利润预算id: {}]", budgetPlanProfitId, e);
                // 更新利润预算计算状态
                SpringUtil.getBean(BudgetPlanProfitService.class).modifyCalculateStatus(budgetPlanProfitId, BudgetPlanCalculateStatusEnum.FAILURE);
            }
        });
        // 异步初始化成本预算明细
        ThreadPoolUtil.getCommonPool().execute(() -> {
            Long budgetPlanCostId = null;
            try {
                // 找一下预算成本数据
                BudgetPlanProfit budgetPlanProfit = budgetPlanProfitService.getById(budgetPlanProfitId);
                BudgetPlanCost budgetPlanCost = budgetPlanCostService.findByBudgetPlanId(budgetPlanProfit.getBudgetPlanId());
                budgetPlanCostId = budgetPlanCost.getId();
                SpringUtil.getBean(BudgetPlanCostService.class).initCostDetail(budgetPlanCost.getId());
                // 更新成本预算计算状态
                SpringUtil.getBean(BudgetPlanCostService.class).modifyCalculateStatus(budgetPlanCostId, BudgetPlanCalculateStatusEnum.SUCCESS);
            } catch (Exception e) {
                log.error("预算管理-异步计算成本预算明细发生异常[利润预算id: {}, 成本预算id: {}]", budgetPlanProfitId, budgetPlanCostId, e);
                // 更新成本预算计算状态
                SpringUtil.getBean(BudgetPlanCostService.class).modifyCalculateStatus(budgetPlanCostId, BudgetPlanCalculateStatusEnum.FAILURE);
            }
        });
        return R.ok(budgetPlanProfitId);
    }

    @Override
    public R<PageR<BudgetPlanProfitListRSP>> list(BudgetPlanProfitListREQ req){
        return R.ok(budgetPlanProfitService.pageList(req));
    }

    @Override
    public R<BudgetPlanProfitRSP> info(SinglePkREQ req) {
        BudgetPlanProfit budgetPlanProfit = budgetPlanProfitService.getById(req.getId());
        BudgetPlanProfitRSP rsp = BeanUtil.copyProperties(budgetPlanProfit, BudgetPlanProfitRSP.class);
        // 查一下对应的投放计划
        BudgetPlanPay budgetPlanPay = budgetPlanPayService.getByBudgetPlanId(budgetPlanProfit.getBudgetPlanId());
        rsp.setBudgetPlanPayId(budgetPlanPay.getId());
        return R.ok(rsp);
    }

    @Override
    public R<Void> remove(BudgetPlanProfitRemoveREQ req){
        budgetPlanProfitService.delete(req.getId());
        return R.ok();
    }

    @Override
    public R<BudgetPlanProfitSummaryRSP> summary(BudgetPlanProfitSummaryREQ req) {
        return R.ok(budgetPlanProfitService.summary(req));
    }

    @Override
    public R<List<BudgetPlanProfitSummaryOtherRSP>> summaryOther(BudgetPlanProfitSummaryOtherREQ req) {
        return R.ok(budgetPlanProfitService.summaryOther(req));
    }

    @Override
    public R<Void> confirm(SinglePkREQ req) {
        budgetPlanProfitService.confirm(req.getId());
        return R.ok();
    }

    @Override
    public R<List<BudgetPlanProfitProcessRSP>> process(BudgetPlanProfitProcessREQ req) {
        return R.ok(budgetPlanProfitService.process(req));
    }

    @Override
    public R<List<BudgetPlanProfitDetailHistoryRSP>> detailHistory(SinglePkREQ req) {
        return R.ok(budgetPlanProfitDetailService.listDetailHistoryRSP(req.getId()));
    }

    @Override
    public R<List<BudgetPlanProfitDetailFutureRSP>> detailFuture(SinglePkREQ req) {
        return R.ok(budgetPlanProfitDetailService.listDetailFutureRSP(req.getId()));
    }

    @Override
    public R<Void> notifyCreatePlanPay(SinglePkREQ req) {
        budgetPlanProfitService.notifyCreatePlanPay(req.getId());
        return R.ok();
    }

    @Override
    public R<List<BudgetPlanProfitDetailRSP>> deptDetail(BudgetPlanProfitDetailREQ req) {
        return R.ok(budgetPlanProfitDetailService.deptDetail(req));
    }

    @Override
    public R<Void> modifyProfitDetail(BudgetPlanProfitDetailModifyREQ req) {
        budgetPlanProfitDetailService.modifyDetail(req);
        return R.ok();
    }

}