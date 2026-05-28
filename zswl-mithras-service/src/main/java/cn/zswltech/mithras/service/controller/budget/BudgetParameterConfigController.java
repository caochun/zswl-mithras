package cn.zswltech.mithras.service.controller.budget;
import cn.zswltech.mithras.api.budget.BudgetParameterConfigApi;
import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.dto.budget.BudgetParameterConfigListREQ;
import cn.zswltech.mithras.dto.budget.BudgetParameterConfigListRSP;
import cn.zswltech.mithras.dto.budget.BudgetParameterConfigModifyREQ;
import cn.zswltech.mithras.service.service.budget.BudgetParameterConfigService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
* @description 预算管理-参数设置
* @author vico
* @date 2025-04-11
*/
@RestController
public class BudgetParameterConfigController implements BudgetParameterConfigApi {

    @Resource
    private BudgetParameterConfigService budgetParameterConfigService;

    @Override
    public R<Void> modify(BudgetParameterConfigModifyREQ req){
        budgetParameterConfigService.modify(req);
        return R.ok();
    }

    @Override
    public R<List<BudgetParameterConfigListRSP>> list(BudgetParameterConfigListREQ req){
        return R.ok(budgetParameterConfigService.list(req));
    }

}