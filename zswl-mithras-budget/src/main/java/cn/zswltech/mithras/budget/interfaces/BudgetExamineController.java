package cn.zswltech.mithras.budget.interfaces;

import cn.zswltech.mithras.api.budget.BudgetExamineApi;
import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.budget.application.BudgetExamineApplicationService;
import cn.zswltech.mithras.dto.IdREQ;
import cn.zswltech.mithras.dto.budget.*;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
* @description 预算管理-预算考核
* @author vico
* @date 2025-04-11
*/
@RestController
public class BudgetExamineController implements BudgetExamineApi {

    @Resource
    private BudgetExamineApplicationService budgetExamineService;

    @Override
    public R<Void> add(BudgetExamineAddREQ req) {
        budgetExamineService.add(req);
        return R.ok();
    }


    @Override
    public R<PageR<BudgetExamineListRSP>> list(BudgetExamineListREQ req){
        return R.ok(budgetExamineService.pageList(req));
    }

    @Override
    public R<Void> remove(BudgetExamineRemoveREQ req){
        budgetExamineService.remove(req);
        return R.ok();
    }

    @Override
    public R<Void> submit(@Valid IdREQ req) {
        budgetExamineService.submit(req);
        return R.ok();
    }

    @Override
    public R<BudgetExamineListRSP> detail(@Valid IdREQ req) {
        return R.ok(budgetExamineService.detail(req.getId()));
    }

}
