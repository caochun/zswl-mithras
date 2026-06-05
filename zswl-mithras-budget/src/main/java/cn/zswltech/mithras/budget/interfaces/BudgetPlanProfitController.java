package cn.zswltech.mithras.budget.interfaces;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.budget.*;
import cn.zswltech.mithras.budget.application.BudgetPlanProfitDetailApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.List;

import cn.zswltech.mithras.api.budget.BudgetPlanProfitApi;
import cn.zswltech.mithras.budget.application.BudgetPlanProfitApplicationService;

/**
* @description 预算管理-预算计划-利润预算
* @author vico
* @date 2025-04-11
*/
@Slf4j
@RestController
public class BudgetPlanProfitController implements BudgetPlanProfitApi {
    @Resource
    private BudgetPlanProfitApplicationService budgetPlanProfitService;
    @Resource
    private BudgetPlanProfitDetailApplicationService budgetPlanProfitDetailService;

    @Override
    public R<Long> create(BudgetPlanProfitCreateREQ req) {
        return R.ok(budgetPlanProfitService.createAndInit(req));
    }

    @Override
    public R<PageR<BudgetPlanProfitListRSP>> list(BudgetPlanProfitListREQ req){
        return R.ok(budgetPlanProfitService.pageList(req));
    }

    @Override
    public R<BudgetPlanProfitRSP> info(SinglePkREQ req) {
        return R.ok(budgetPlanProfitService.info(req.getId()));
    }

    @Override
    public R<Void> remove(BudgetPlanProfitRemoveREQ req){
        budgetPlanProfitService.delete(req);
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
