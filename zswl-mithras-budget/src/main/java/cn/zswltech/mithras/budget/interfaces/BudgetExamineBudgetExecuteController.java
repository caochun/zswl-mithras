package cn.zswltech.mithras.budget.interfaces;
import cn.zswltech.mithras.api.budget.BudgetExamineBudgetExecuteApi;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.budget.*;
import cn.zswltech.mithras.budget.application.BudgetExamineBudgetExecuteApplicationService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
* @description 预算管理-预算考核-预算执行情况表
* @author vico
* @date 2025-04-11
*/
@RestController
public class BudgetExamineBudgetExecuteController implements BudgetExamineBudgetExecuteApi {

    @Resource
    private BudgetExamineBudgetExecuteApplicationService budgetExamineBudgetExecuteService;

    @Override
    public R<Void> modify(List<BudgetExamineBudgetExecuteModifyREQ> req){
        budgetExamineBudgetExecuteService.modify(req);
        return R.ok();
    }

    @Override
    public R<List<BudgetExamineBudgetExecuteListRSP>> list(BudgetExamineBudgetExecuteListREQ req){
        return R.ok(budgetExamineBudgetExecuteService.list(req));
    }


}