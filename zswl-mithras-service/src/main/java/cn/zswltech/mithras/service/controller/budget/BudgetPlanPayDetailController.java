package cn.zswltech.mithras.service.controller.budget;

import cn.zswltech.mithras.budget.application.BudgetPlanPayDetailExpenseService;
import cn.zswltech.mithras.budget.application.BudgetPlanPayDetailPriceService;
import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.MultiplePkREQ;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.budget.*;
import cn.zswltech.mithras.budget.infrastructure.persistence.mapper.model.BudgetPlanPayDetail;
import cn.zswltech.mithras.service.others.MithrasException;
import cn.zswltech.mithras.service.service.budget.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import cn.zswltech.mithras.api.budget.BudgetPlanPayDetailApi;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

/**
* @description 预算管理-预算计划-投放计划-明细
* @author vico
* @date 2025-04-11
*/
@Slf4j
@RestController
public class BudgetPlanPayDetailController implements BudgetPlanPayDetailApi {
    @Resource
    private BudgetPlanPayDetailService budgetPlanPayDetailService;
    @Resource
    private BudgetPlanPayDetailPriceService budgetPlanPayDetailPriceService;
    @Resource
    private BudgetPlanPayDetailCashFlowService budgetPlanPayDetailCashFlowService;
    @Resource
    private BudgetPlanPayDetailIncomeSharingService budgetPlanPayDetailIncomeSharingService;
    @Resource
    private BudgetPlanPayDetailFtpInterestService budgetPlanPayDetailFtpInterestService;
    @Resource
    private BudgetPlanPayDetailExpenseService budgetPlanPayDetailExpenseService;

    @Override
    public R<Void> batchDelete(MultiplePkREQ req) {
        budgetPlanPayDetailService.deleteByBudgetPlanPayDetailIds(req.getIds());
        return R.ok();
    }

    @Override
    public R<List<BudgetPlanPayDetailDeptConfirmInfoRSP>> listBizDeptConfirmInfo(BudgetPlanPayDetailDeptConfirmInfoREQ req) {
        return R.ok(budgetPlanPayDetailService.listBizDeptConfirmInfo(req));
    }

    @Override
    public R<Void> addDetailMonth(BudgetPlanPayDetailMonthAddREQ req) {
        budgetPlanPayDetailService.addDetailMonth(req);
        return R.ok();
    }

    @Override
    public R<Void> modifyDetailMonth(BudgetPlanPayDetailMonthModifyREQ req) {
        budgetPlanPayDetailService.modifyDetailMonth(req);
        return R.ok();
    }

    @Override
    public R<BudgetPlanPayDetailMonthStatisticsRSP> statisticsMonth(BudgetPlanPayDetailMonthListREQ req) {
        return R.ok(budgetPlanPayDetailService.statisticsMonth(req));
    }

    @Override
    public R<PageR<BudgetPlanPayDetailMonthListRSP>> pageListMonth(BudgetPlanPayDetailMonthListREQ req) {
        return R.ok(budgetPlanPayDetailService.pageListMonth(req));
    }

    @Override
    public R<List<Pair<String, Long>>> listCanChooseProject(BudgetChooseProjectREQ req) {
        return R.ok(budgetPlanPayDetailService.listMonthDetailProjReview(req));
    }

    @Override
    public R<List<BudgetPlanPayDetailMonthListRSP>> listMonthDetailContract(SinglePkREQ req) {
        return R.ok(budgetPlanPayDetailService.listMonthDetailContract(req.getId()));
    }

    @Override
    public R<Boolean> checkPlanPayAmount(BudgetPlanPayDetailMonthCheckREQ req) {
        BudgetPlanPayDetail byId = budgetPlanPayDetailService.getById(req.getId());
        return R.ok(budgetPlanPayDetailService.checkPlanPayAmount(byId.getProjReviewId(), req.getPlanPayAmount(),req.getId()));
    }

    @Override
    public R<BudgetPlanPayDetailNotMonthStatisticsRSP> statisticsNotMonth(BudgetPlanPayDetailNotMonthListREQ req) {
        return R.ok(budgetPlanPayDetailService.statisticsNotMonth(req));
    }

    @Override
    public R<PageR<BudgetPlanPayDetailNotMonthListRSP>> pageListNotMonth(BudgetPlanPayDetailNotMonthListREQ req) {
        return R.ok(budgetPlanPayDetailService.pageListNotMonth(req));
    }

    @Override
    public R<BudgetPlanPayDetailNotMonthBaseRSP> baseInfoNotMonth(SinglePkREQ req) {
        return R.ok(budgetPlanPayDetailService.getByDetailId(req.getId()));
    }

    @Override
    public R<BudgetPlanPayDetailNotMonthPriceRSP> priceNotMonth(SinglePkREQ req) {
        return R.ok(budgetPlanPayDetailPriceService.getByDetailId(req.getId()));
    }

    @Override
    public R<List<BudgetPlanPayDetailNotMonthCashFlowRSP>> cashFlowNotMonth(SinglePkREQ req) {
        return R.ok(budgetPlanPayDetailCashFlowService.listCashFlowByDetailId(req.getId()));
    }

    @Override
    public R<List<BudgetPlanPayDetailNotMonthCashFlowRSP>> importCashFlowNotMonth(MultipartFile file) {
        List<BudgetPlanPayDetailNotMonthCashFlowRSP> result;
        try {
            result = budgetPlanPayDetailCashFlowService.parseFromExcel(file.getInputStream());
        } catch (Exception e) {
            log.error("预算管理-现金流计划表-解析Excel异常", e);
            throw new MithrasException("导入失败");
        }
        return R.ok(result);
    }

    @Override
    public R<BudgetPlanPayDetailDynamicTableRSP> incomeSharingNotMonth(SinglePkREQ req) {
        return R.ok(budgetPlanPayDetailIncomeSharingService.getIncomeSharingDynamicTable(req.getId()));
    }

    @Override
    public R<BudgetPlanPayDetailDynamicTableRSP> ftpInterestNotMonth(SinglePkREQ req) {
        return R.ok(budgetPlanPayDetailFtpInterestService.getFtpInterestDynamicTable(req.getId()));
    }

    @Override
    public R<BudgetPlanPayDetailDynamicTableRSP> expenseNotMonth(SinglePkREQ req) {
        return R.ok(budgetPlanPayDetailExpenseService.getDynamicTable(req.getId()));
    }

    @Override
    public R<Long> calculateNotMonth(BudgetPlanPayDetailNotMonthCalculateREQ req) {
        return R.ok(budgetPlanPayDetailService.calculateNotMonth(req));
    }

    @Override
    public R<Long> copyNotMonth(SinglePkREQ req) {
        return R.ok(budgetPlanPayDetailService.copyNotMonth(req.getId()));
    }
}
