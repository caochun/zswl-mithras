package cn.zswltech.mithras.service.controller.budget;
import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.api.budget.BudgetPlanPayWeeklyReportApi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.SinglePkREQ;
import cn.zswltech.mithras.dto.budget.weekly.*;
import cn.zswltech.mithras.budget.infrastructure.persistence.mapper.model.BudgetPlanPayWeeklyReport;
import cn.zswltech.mithras.service.service.budget.BudgetPlanPayWeeklyReportService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
* @description 预算管理-投放计划-项目周报
* @author vico
* @date 2025-04-11
*/
@RestController
public class BudgetPlanPayWeeklyReportController implements BudgetPlanPayWeeklyReportApi {

    @Resource
    private BudgetPlanPayWeeklyReportService budgetPlanPayWeeklyReportService;

    @Override
    public R<PageR<BudgetPlanPayWeeklyReportListRSP>> list(BudgetPlanPayWeeklyReportListREQ req){
        Page<BudgetPlanPayWeeklyReport> data = budgetPlanPayWeeklyReportService.list(req);
        List<BudgetPlanPayWeeklyReportListRSP> list = BeanUtil.copyToList(data.getRecords(), BudgetPlanPayWeeklyReportListRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

    @Override
    public R<BudgetPlanPayWeeklyReportListRSP> planInfo(@Valid SinglePkREQ req) {
        return R.ok(budgetPlanPayWeeklyReportService.planInfo(req.getId()));
    }

}