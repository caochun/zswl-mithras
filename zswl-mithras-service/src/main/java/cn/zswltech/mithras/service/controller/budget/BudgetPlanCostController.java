package cn.zswltech.mithras.service.controller.budget;
import cn.hutool.core.bean.BeanUtil;
import cn.zswltech.mithras.api.budget.BudgetPlanCostApi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.budget.BudgetPlanCostListREQ;
import cn.zswltech.mithras.dto.budget.BudgetPlanCostListRSP;
import cn.zswltech.mithras.budget.infrastructure.persistence.mapper.model.BudgetPlanCost;
import cn.zswltech.mithras.service.service.budget.BudgetPlanCostService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
* @description 预算管理-预算计划-成本预算
* @author vico
* @date 2025-04-11
*/
@RestController
public class BudgetPlanCostController implements BudgetPlanCostApi {

    @Resource
    private BudgetPlanCostService budgetPlanCostService;

    @Override
    public R<PageR<BudgetPlanCostListRSP>> list(BudgetPlanCostListREQ req){
        Page<BudgetPlanCost> data = budgetPlanCostService.list(req);
        List<BudgetPlanCostListRSP> list = BeanUtil.copyToList(data.getRecords(), BudgetPlanCostListRSP.class);
        return R.ok(PageR.of(list, data.getTotal(),
                data.getPages(),
                data.getCurrent(),
                data.getSize()));
    }

}