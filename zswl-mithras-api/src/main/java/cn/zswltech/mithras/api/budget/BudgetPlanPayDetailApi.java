package cn.zswltech.mithras.api.budget;

import cn.hutool.core.lang.Pair;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.MultiplePkREQ;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.budget.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

/**
* @description 预算管理-预算计划-投放计划-明细
* @author vico
* @date 2025-04-11
*/
@Api(tags = "预算管理-预算计划-投放计划-明细-接口")
public interface BudgetPlanPayDetailApi {
    @ApiOperation("预算管理-预算计划-投放计划-明细-删除")
    @PostMapping(path = "/budget/plan/pay/detail/batchDelete")
    R<Void> batchDelete(@RequestBody @Valid MultiplePkREQ req);

    @ApiOperation("预算管理-预算计划-投放计划-明细-部门确认情况")
    @PostMapping(path = "/budget/plan/pay/detail/deptConfirm/list")
    R<List<BudgetPlanPayDetailDeptConfirmInfoRSP>> listBizDeptConfirmInfo(@RequestBody @Valid BudgetPlanPayDetailDeptConfirmInfoREQ req);

    @ApiOperation("预算管理-预算计划-投放计划-月度-明细-新增")
    @PostMapping(path = "/budget/plan/pay/month/detail/add")
    R<Void> addDetailMonth(@RequestBody @Valid BudgetPlanPayDetailMonthAddREQ req);

    @ApiOperation("预算管理-预算计划-投放计划-月度-明细-编辑")
    @PostMapping(path = "/budget/plan/pay/month/detail/modify")
    R<Void> modifyDetailMonth(@RequestBody @Valid BudgetPlanPayDetailMonthModifyREQ req);

    @ApiOperation("预算管理-预算计划-投放计划-月度-明细-统计")
    @PostMapping(path = "/budget/plan/pay/month/detail/statistics")
    R<BudgetPlanPayDetailMonthStatisticsRSP> statisticsMonth(@RequestBody @Valid BudgetPlanPayDetailMonthListREQ req);

    @ApiOperation("预算管理-预算计划-投放计划-月度-明细-列表")
    @PostMapping(path = "/budget/plan/pay/month/detail/pageList")
    R<PageR<BudgetPlanPayDetailMonthListRSP>> pageListMonth(@RequestBody @Valid BudgetPlanPayDetailMonthListREQ req);

    @ApiOperation("预算管理-预算计划-投放计划-月度-明细-选择项目评审")
    @PostMapping(path = "/budget/plan/pay/month/detail/projreview/list")
    R<List<Pair<String, Long>>> listCanChooseProject(@RequestBody @Valid BudgetChooseProjectREQ req);

    @ApiOperation("预算管理-预算计划-投放计划-月度-明细-列表-合同信息")
    @PostMapping(path = "/budget/plan/pay/month/detail/pageList/contract")
    R<List<BudgetPlanPayDetailMonthListRSP>> listMonthDetailContract(@RequestBody @Valid SinglePkREQ req);

    @ApiOperation("预算管理-预算计划-投放计划-月度-明细-编辑-校验拟投放金额")
    @PostMapping(path = "/budget/plan/pay/month/detail/checkPlanPayAmount")
    R<Boolean> checkPlanPayAmount(@RequestBody @Valid BudgetPlanPayDetailMonthCheckREQ req);

    @ApiOperation("预算管理-预算计划-投放计划-非月度-明细-统计")
    @PostMapping(path = "/budget/plan/pay/notmonth/detail/statistics")
    R<BudgetPlanPayDetailNotMonthStatisticsRSP> statisticsNotMonth(@RequestBody @Valid BudgetPlanPayDetailNotMonthListREQ req);

    @ApiOperation("预算管理-预算计划-投放计划-非月度-明细-列表")
    @PostMapping(path = "/budget/plan/pay/notmonth/detail/pageList")
    R<PageR<BudgetPlanPayDetailNotMonthListRSP>> pageListNotMonth(@RequestBody @Valid BudgetPlanPayDetailNotMonthListREQ req);

    @ApiOperation("预算管理-预算计划-投放计划-非月度-明细-基本信息")
    @PostMapping(path = "/budget/plan/pay/notmonth/detail/baseInfo")
    R<BudgetPlanPayDetailNotMonthBaseRSP> baseInfoNotMonth(@RequestBody @Valid SinglePkREQ req);

    @ApiOperation("预算管理-预算计划-投放计划-非月度-明细-报价方案")
    @PostMapping(path = "/budget/plan/pay/notmonth/detail/price")
    R<BudgetPlanPayDetailNotMonthPriceRSP> priceNotMonth(@RequestBody @Valid SinglePkREQ req);

    @ApiOperation("预算管理-预算计划-投放计划-非月度-明细-现金流计划")
    @PostMapping(path = "/budget/plan/pay/notmonth/detail/cashFlow")
    R<List<BudgetPlanPayDetailNotMonthCashFlowRSP>> cashFlowNotMonth(@RequestBody @Valid SinglePkREQ req);

    @ApiOperation("预算管理-预算计划-投放计划-非月度-明细-现金流计划-解析")
    @PostMapping(path = "/budget/plan/pay/notmonth/detail/cashFlow/parse")
    R<List<BudgetPlanPayDetailNotMonthCashFlowRSP>> importCashFlowNotMonth(@RequestParam("file") MultipartFile file);

    @ApiOperation("预算管理-预算计划-投放计划-非月度-明细-收入分摊情况")
    @PostMapping(path = "/budget/plan/pay/notmonth/detail/incomeSharing")
    R<BudgetPlanPayDetailDynamicTableRSP> incomeSharingNotMonth(@RequestBody @Valid SinglePkREQ req);

    @ApiOperation("预算管理-预算计划-投放计划-非月度-明细-资金成本")
    @PostMapping(path = "/budget/plan/pay/notmonth/detail/fundCost")
    R<BudgetPlanPayDetailDynamicTableRSP> ftpInterestNotMonth(@RequestBody @Valid SinglePkREQ req);

    @ApiOperation("预算管理-预算计划-投放计划-非月度-明细-期间费用")
    @PostMapping(path = "/budget/plan/pay/notmonth/detail/expense")
    R<BudgetPlanPayDetailDynamicTableRSP> expenseNotMonth(@RequestBody @Valid SinglePkREQ req);

    @ApiOperation("预算管理-预算计划-投放计划-非月度-明细-测算")
    @PostMapping(path = "/budget/plan/pay/notmonth/detail/calculate")
    R<Long> calculateNotMonth(@RequestBody @Valid BudgetPlanPayDetailNotMonthCalculateREQ req);

    @ApiOperation("预算管理-预算计划-投放计划-非月度-明细-复制")
    @PostMapping(path = "/budget/plan/pay/notmonth/detail/copy")
    R<Long> copyNotMonth(@RequestBody @Valid SinglePkREQ req);
}
