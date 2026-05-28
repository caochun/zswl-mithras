package cn.zswltech.mithras.service.controller.budget;
import cn.zswltech.mithras.api.budget.BudgetExamineBenefitApi;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.budget.BudgetExamineBenefitListREQ;
import cn.zswltech.mithras.dto.budget.BudgetExamineBenefitListRSP;
import cn.zswltech.mithras.dto.budget.BudgetExamineBenefitModifyREQ;
import cn.zswltech.mithras.service.service.budget.BudgetExamineBenefitService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
* @description 预算管理-预算考核-效益考核表
* @author vico
* @date 2025-04-11
*/
@RestController
public class BudgetExamineBenefitController implements BudgetExamineBenefitApi {

    @Resource
    private BudgetExamineBenefitService budgetExamineBenefitService;


    @Override
    public R<Void> modify(List<BudgetExamineBenefitModifyREQ> req){
        budgetExamineBenefitService.modify(req);
        return R.ok();
    }

    @Override
    public R<List<BudgetExamineBenefitListRSP>> list(BudgetExamineBenefitListREQ req){
        return R.ok(budgetExamineBenefitService.list(req));
    }


}