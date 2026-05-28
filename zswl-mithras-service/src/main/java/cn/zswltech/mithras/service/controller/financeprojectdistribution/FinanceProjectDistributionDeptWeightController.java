package cn.zswltech.mithras.service.controller.financeprojectdistribution;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.financeprojectdistribution.FinanceProjectDistributionDeptWeightApi;
import cn.zswltech.mithras.dto.financeprojectdistribution.*;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.financeprojectdistribution.FinanceProjectDistributionModifyChecker;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.service.financeprofitdistribution.FinanceProjectDistributionDeptLaunchWeightService;
import cn.zswltech.mithras.service.service.financeprofitdistribution.FinanceProjectDistributionDeptWeightService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @author lllin
 * @date2025/12/19
 * @description
 */
@Slf4j
@RestController
public class FinanceProjectDistributionDeptWeightController implements FinanceProjectDistributionDeptWeightApi {

    @Resource
    private FinanceProjectDistributionDeptWeightService financeProjectDistributionDeptWeightService;

    @Resource
    private FinanceProjectDistributionDeptLaunchWeightService financeProjectDistributionDeptLaunchWeightService;

    @Override
    @DataAuthCheck(keyFieldName = "projectDistributionId", paramType = DataAuthCheck.ParamType.OBJECT, businessModule = BusinessModuleEnum.FINANCE_PROJECT_DISTRIBUTION, checkerClass = FinanceProjectDistributionModifyChecker.class)
    public R<Void> saveDept(FinanceProjectDistributionDeptWeightSaveREQ req) {
        financeProjectDistributionDeptWeightService.saveDept(req);
        return R.ok();
    }

    @Override
    public R<FinanceProjectDistributionWeightRSP> detail(@Valid FinanceProjectDistributionWeightREQ req) {
        return R.ok(financeProjectDistributionDeptWeightService.detail(req));
    }


}
