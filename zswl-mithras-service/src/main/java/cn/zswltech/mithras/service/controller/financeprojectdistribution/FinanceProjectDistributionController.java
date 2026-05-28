package cn.zswltech.mithras.service.controller.financeprojectdistribution;

import cn.zswltech.mithras.api.common.R;
import cn.zswltech.mithras.api.financeprojectdistribution.FinanceProjectDistributionApi;
import cn.zswltech.mithras.dto.financeprojectdistribution.FinanceProjectDistributionSubmitREQ;
import cn.zswltech.mithras.service.auth.aop.DataAuthCheck;
import cn.zswltech.mithras.service.auth.checker.financeprojectdistribution.FinanceProjectDistributionModifyChecker;
import cn.zswltech.mithras.service.enums.BusinessModuleEnum;
import cn.zswltech.mithras.service.service.financeprofitdistribution.FinanceProjectDistributionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author lllin
 * @date2025/12/19
 * @description
 */
@Slf4j
@RestController
public class FinanceProjectDistributionController implements FinanceProjectDistributionApi {
    @Resource
    private FinanceProjectDistributionService financeProjectDistributionService;


    @DataAuthCheck(keyFieldName = "projectDistributionId", paramType = DataAuthCheck.ParamType.OBJECT, businessModule = BusinessModuleEnum.FINANCE_PROJECT_DISTRIBUTION, checkerClass = FinanceProjectDistributionModifyChecker.class)
    @Override
    public R<Void> submit(@Valid FinanceProjectDistributionSubmitREQ req) {
        financeProjectDistributionService.submit(req.getProjectDistributionId(),true);
        return R.ok();
    }

}
