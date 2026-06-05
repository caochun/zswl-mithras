package cn.zswltech.mithras.budget.interfaces;

import cn.zswltech.mithras.api.budget.BudgetPlanPayWeeklyReportDetailApi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.MultiplePkREQ;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.budget.BudgetPlanPayDetailMonthAddREQ;
import cn.zswltech.mithras.dto.budget.weekly.*;
import cn.zswltech.mithras.budget.application.BudgetPlanPayWeeklyReportDetailApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
* @description 预算管理-投放计划（月度）-项目周报-详情
* @author vico
* @date 2025-04-11
*/
@RestController
public class BudgetPlanPayWeeklyReportDetailController implements BudgetPlanPayWeeklyReportDetailApi {

    @Resource
    private BudgetPlanPayWeeklyReportDetailApplicationService budgetPlanPayWeeklyReportDetailService;

    /*@Override
    public R<Void> modify(BudgetPlanPayWeeklyReportDetailModifyREQ req){
        budgetPlanPayWeeklyReportDetailService.modify(req);
        return R.ok();
    }

    @Override
    public R<PageR<BudgetPlanPayWeeklyReportDetailListRSP>> list(BudgetPlanPayWeeklyReportDetailListREQ req){
        Page<BudgetPlanPayWeeklyReportDetail> data = budgetPlanPayWeeklyReportDetailService.list(req);
        List<BudgetPlanPayWeeklyReportDetailListRSP> list = BeanUtil.copyToList(data.getRecords(), BudgetPlanPayWeeklyReportDetailListRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }*/

    @Override
    public R<BudgetPlanPayWeeklyReportDetailStatisticsRSP> statisticsMonth(@Valid BudgetPlanPayWeeklyReportDetailListREQ req) {
        return R.ok(budgetPlanPayWeeklyReportDetailService.statisticsMonth(req));
    }

    @Override
    public R<PageR<BudgetPlanPayWeeklyReportDetailListRSP>> pageListMonth(@Valid BudgetPlanPayWeeklyReportDetailListREQ req) {
        return R.ok(budgetPlanPayWeeklyReportDetailService.pageListMonth(req));
    }

    @Override
    public R<Void> addDetailMonth(BudgetPlanPayWeeklyReportDetailAddREQ req) {
        budgetPlanPayWeeklyReportDetailService.addDetailMonth(req);
        return R.ok();
    }

    @Override
    public R<Void> modifyDetailMonth(BudgetPlanPayWeekReportDetailModifyREQ req) {
        budgetPlanPayWeeklyReportDetailService.modifyDetailMonth(req);
        return R.ok();
    }

    @Override
    public R<List<BudgetPlanPayWeeklyReportDetailListRSP>> listMonthDetailContract(@Valid SinglePkREQ req) {
        return R.ok(budgetPlanPayWeeklyReportDetailService.listMonthDetailContract(req.getId()));
    }

    @Override
    public R<List<BudgetPlanPayWeekReportDetailDeptConfirmInfoRSP>> listBizDeptConfirmInfo(@Valid SinglePkREQ req) {
        return R.ok(budgetPlanPayWeeklyReportDetailService.listBizDeptConfirmInfo(req.getId()));
    }

    @Override
    public R<Void> batchDelete(@Valid MultiplePkREQ req) {
        budgetPlanPayWeeklyReportDetailService.removeBatch(req);
        return R.ok();
    }

}